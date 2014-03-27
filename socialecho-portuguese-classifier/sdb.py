from pymongo import Connection

server = "192.168.102.195"
database = "socialecho_v05"

connection = Connection(server)
db = connection[database]

def get_user_info(user_id):
  "Get all information about the user."
  return db["twitter"].find_one({"user.id": user_id})["user"]

def get_user_tweets(user_id, limit):
  "Get the latest [limit] messages of the user with twitter user id [user_id]."
  return list(db["twitter"].find({"user.id": user_id}, limit=limit))

def get_user_tweets_count(user_id):
  "Calculate how many messages of the user we have in the database."
  total = db["twitter"].find({"user.id": user_id}).count() 
  print total
  return total

# def get_users():
#   "Return an iterator that returns all users in the database."
#   return ( x['id'] for x in db.users.find({},{"id":True, "_id":False}) )

def get_text_sample(user_id, maximum):
  if get_user_tweets_count(user_id) >= maximum:
    print "return list() de tweets" ,user_id
    return [ x["text"] for x in db["twitter"].find({"user.id": user_id}, {"text": True, "_id": False}).limit(maximum) ]
  else:
    print "return list() vazio" ,user_id
    return list()
