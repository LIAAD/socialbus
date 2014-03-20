var n_messages = 100;

var invalid_users = db["twitter_users"].find({
  "$or": [
    {"meta.bot": {"$exists": true},},
    {"statuses_count": {"$lt": n_messages}},
    ]
  },
  {"_id": 0, "id": 1,}
);

var users_to_avoid_list = invalid_users.map(
  function(element){return element.id;}
  );

map = function() {
  emit(this.user.id,{"count": 1,"latest_tweets":[this.id]})
};

reduce = function(key, emits) {
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
  return {"count": total, "latest_tweets": tweets};
};

var x = db.runCommand({
  "mapreduce": "twitter",
  "map": map,
  "reduce": reduce,
  "out": "temp",
  "query": {"user.id": {"$nin": users_to_avoid_list}},
  "sort": {"meta.created_at": -1},
  "scope": {"n_messages": n_messages},
});
