# WARNING: This code, while I like it very much, is no longer used, sinde the DB
# was changed. We no longer make non-trivial access to the database.
# It is silly to chace after a moving target.
# Thus, the currnet philosophy is: let the users handle the dababase affairs.
# You can finde the sdb.py file (simple/stupid database) that provides the minimum
# code to fetch data for our tests.
#
# The current code is meant to provide an idea of what was intended, and how
# performance can be increased through pre-computing values and using no-trivial
# queries.

# This was made to work only on pymongo 2.2.1, since it was the version installed
# on koi.
server = "192.168.102.195"
database = "socialecho_v05"

number_messages_to_eval = 100
max_users = 200
tmp_collection_name = "tmp_tweets_from_user_sample"

from pymongo import Connection
from bson.code import Code
from bson.son import SON
from author import Author
from random import shuffle

connection = Connection(server)
db = connection[database]


def get_list_of_user_id_not_classified(n_messages):
# TODO: consider other filtering criteria (e.g. date or version of classification).
  users = [ x["id"]
    for x in db["twitter_users"].find(
      {
        "meta.bot": {"$exists": False},
        "statuses_count": {"$gte": n_messages}
      },{"_id": 0, "id": 1}
    ).limit(max_users)
  ]
  shuffle(users)
  return(users[:max_users])


def get_list_of_messages_from_unclassified_users(n_messages):
  js_map = Code("""
    function() {
      emit(this.user.id,{
        "count": 1,
        "user": this.user,
        "latest_tweets": [this],
      })
    };
  """)

  js_reduce = Code("""
    function(key, emits) {
      total = 0;
      tweets = [];
      for (var i in emits) {
        total += emits[i].count;
        if (tweets.length + emits[i].latest_tweets.length <= n_messages) {
          tweets = tweets.concat(emits[i].latest_tweets);
        }
        else {
          for (var j = 0; total.length < n_messages; j++) {
            total.push(emit[i].latest_tweets[j]);
          }
        }
      }
      return {"count": total, "user": emits[0].user, "latest_tweets": tweets};
    };
  """)

# WARNING: This destroys data from the collection.
# Does not work well with incremental map-reduce, should it be implemented.
  js_finalize = Code("""
    function(key, values) {
      if (values["count"] < n_messages) {
        values["latest_tweets"] = [];
      }
      return values;
    }
  """)

# TODO: Consider using an incremental map-reduce.
# Also, the temporary collection is left on the DB to be overwriten at the next pass.
  return db["twitter"].map_reduce(
    map = js_map,
    reduce = js_reduce,
    query = {"user.id": {"$in": get_list_of_user_id_not_classified(n_messages)}},
    finalize = js_finalize,
    sort = SON([("meta.created_at", -1)]),
    scope = {"n_messages": n_messages},
    out = {"replace": tmp_collection_name},
    limit = 1E6
  )


def author_iterator(n_messages):
  return (
    Author(data["value"]["user"],data["value"]["latest_tweets"]) for data
    in get_list_of_messages_from_unclassified_users(n_messages).find(
      {"value.count": {"$gte": n_messages}},
      {"_id": 0, "value.count": 1, "value.latest_tweets": 1, "value.user": 1}
    )
  )

def debug_author(id = 79197076, limit=number_messages_to_eval):
  u = db["twitter_users"].find_one({"id": id})
  t = list(db["twitter"].find({"user.id": id}, order=[("user.meta.created_at",-1)], limit=limit))
  return Author(u,t)
