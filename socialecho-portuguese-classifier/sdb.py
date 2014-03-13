from pymongo import Connection

server = "192.168.102.190"
database = "twitterecho"

connection = Connection(server)
db = connection[database]

def get_user_info(user_id):
  "Get all information about the user."
  return db.users.find_one({"id": user_id})

def get_user_tweets(user_id, limit):
  "Get the latest [limit] messages of the user with twitter user id [user_id]."
  return list(db.tweets.find({"user_id": user_id}, order=[("user.meta.created_at",-1)], limit=limit))

def get_user_tweets_count(user_id):
  "Calculate how many messages of the user we have in the database."
  return db.tweets.find({"user_id": user_id}).count()

def get_users():
  "Return an iterator that returns all users in the database."
  return ( x['id'] for x in db.users.find({},{"id":True, "_id":False}) )

def get_text_sample(user_id, maximum):
  if get_user_tweets_count(user_id) >= maximum:
    return [ x["text"] for x in db.tweets.find({"user_id": user_id}, {"text": True, "_id": False}).limit(maximum) ]
  else:
    return list()
