# Classifies users as either bot or humans.
# Quick usage:
# classify_user([user_id1, user_id2, user_id3])

import sdb
import datasource
import train
import sklearn.svm
import sklearn.cross_validation as kfold
import sklearn.metrics
import datetime
import pickle
import sys
from json import loads
from optparse import OptionParser
from pprint import pprint

sample_size = 50  # Has no effect in the model data if we use premade model data.
model_name = "mongodata.pickle"  # The name of the file containing the model.
class_names = ("human", "bot")

# Training data:
human = [194725622, 131622649, 29067002, 374660865, 104945410, 54902532, 495415068, 161797925, 242009897, 298766124, 379293662, 38227767, 329910080, 360970056, 389490514, 30302036, 625811298, 531525476, 111621992, 31563628, 452106104, 232855418, 20717438, 19271557, 128219014, 283027339, 224661399, 200611740, 25272222, 51500961, 409847722, 119463851, 248162229, 229224374, 420304831, 68527435, 24936399, 285626320, 346283985, 25294814, 62052326, 75849713, 115734524, 23132159]
bot = [198561198, 52023334, 88229942, 22621759, 2561091, 15391813, 49951826, 21390437, 94081671, 33906365, 306922612, 37021303, 96159865, 19663485, 82696837, 19026567, 52053647, 73249951, 228018850, 140810916, 33041180, 57710023, 33467091, 82579152, 22905057, 57475305, 22924012, 19509552, 84918067, 447397227, 291072915, 21389998, 23585685, 22027165, 85087658, 395388847, 245154246, 28538823, 113227223, 15392221, 30328300, 29690355, 186127644, 37400272]


def create_model(X, y):
  classifier = sklearn.svm.SVC(kernel='poly', scale_C=True)
  classifier.fit(X, y)
  return classifier

def process():
    # Load or create a model.
    try:
      with open(model_name, "rb") as f:
        model = pickle.load(f)
    except IOError:
        # Read data and filter out problems with dates.
        usable_bot = datasource.filter_bad_data(datasource.gather_data(bot, sample_size))
        usable_human = datasource.filter_bad_data(datasource.gather_data(human, sample_size))
        X, y = train.create_datasets(usable_human, usable_bot)
        model = create_model(X, y)
        with open(model_name, "wb") as f:
          pickle.dump(model, f)

if __name__ == "__main__":
    # Command Line Arguments Parser
    # cmd_parser = OptionParser(version="%prog 0.1")
#     cmd_parser.add_option("-H", "--host", type="string", action="store", dest="mongo_host", help="Mongo host")
#     cmd_parser.add_option("-D", "--database", type="string", action="store", dest="mongo_database", help="Mongo database")
#     cmd_parser.add_option("-P", "--port", type="string", action="store", dest="mongo_port", help="Mongo port", default="27017")
#     cmd_parser.add_option("-U", "--users", type="string", action="store", dest="users_collection", help="users collection", default="twitter_users")
#     cmd_parser.add_option("-T", "--tweets", type="string", action="store", dest="tweets_collection", help="tweets collection", default="twitter")
#     
#     (cmd_options, cmd_args) = cmd_parser.parse_args()
# 
#     if not (cmd_options.mongo_host or cmd_options.mongo_database):
#         cmd_parser.print_help()
#         sys.exit(3)

    print "##################################################################"
    print " [INFO] Twitter users classifier "
    print "------------------------------------------------------------------"
    
    process()
    



def classify_user(user_list, limit = sample_size, model = model):
  "Classify the users on the list as either bot or human. If the user cannot be classified, it will be dropped from the output."
  author_data_list = datasource.gather_data(user_list, limit)
  usable_user_list = datasource.filter_bad_data(author_data_list)
  classification = [ (user.id(), model.predict(train.DictVectorizer(user))) for user in usable_user_list ]
  return [ (uid, class_names[int(uclass)]) for (uid, uclass) in classification ]


def k_fold_test():
  k = 10
  fold = kfold.StratifiedKFold(y, k)

  sum_pred = 0.0
  for train_index, test_index in fold:
    X_train, X_test = X[train_index], X[test_index]
    y_train, y_test = y[train_index], y[test_index]
    
    classifier = create_model(X_train, y_train)
    y_prediction = classifier.predict(X_test)
    sum_pred += sklearn.metrics.precision_score(y_test, y_prediction)

  print(sum_pred / k)
