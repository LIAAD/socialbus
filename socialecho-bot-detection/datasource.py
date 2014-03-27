import sdb
import author
from itertools import ifilter
from operator import methodcaller
import datetime


def useful_authors():
  return filter_bad_data(gather_data(sdb.get_users()))


def gather_data(user_id_list, limit=5):
  "Return an Author object for the users in the provided list. Gather [limit] messages from each one."
  for user in user_id_list:
      
      
      
    if sdb.get_user_tweets_count(user) > limit:
      print user
      yield author.Author(sdb.get_user_info(user), sdb.get_user_tweets(user, limit))
    else:
      print "{} , {} , {}".format(user, "na", datetime.datetime.now())

# The database has some problems, where some records have "created_at" as a DateTime object,
# and others as a string (as Twitter sends it).
# This method filters out authors with tweet samples that exhibit this issue.
def filter_bad_data(user_list):
  return ifilter(methodcaller("check_usable_dates"), user_list)
