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

sample_size = 10  # Has no effect in the model data if we use premade model data.
model_name = "mongodata.pickle"  # The name of the file containing the model.
class_names = ("human", "bot")

# Training data:
# human = [194725622, 131622649, 29067002, 374660865, 104945410, 54902532, 495415068, 161797925, 242009897, 298766124, 379293662, 38227767, 329910080, 360970056, 389490514, 30302036, 625811298, 531525476, 111621992, 31563628, 452106104, 232855418, 20717438, 19271557, 128219014, 283027339, 224661399, 200611740, 25272222, 51500961, 409847722, 119463851, 248162229, 229224374, 420304831, 68527435, 24936399, 285626320, 346283985, 25294814, 62052326, 75849713, 115734524, 23132159]

human = [194725622, 131622649,104945410, 54902532, 161797925, 298766124, 379293662, 38227767, 360970056, 30302036, 20717438, 128219014, 224661399, 200611740, 119463851, 420304831, 285626320, 62052326, 75849713, 115734524, 23132159]
bot = [198561198, 52023334, 22621759, 2561091, 15391813, 21390437, 94081671, 19663485, 82696837, 73249951, 140810916, 82579152, 22905057, 19509552, 447397227, 21389998, 23585685, 22027165, 395388847, 28538823, 15392221, 30328300, 29690355, 186127644]

def create_model(X, y):
  # classifier = sklearn.svm.SVC(kernel='poly', scale_C=True)

  classifier = sklearn.svm.SVC(kernel='poly')
  classifier.fit(X, y)
  return classifier

def initialize_model():
    # Load or create a model.
    try:
      with open(model_name, "rb") as f:
        model = pickle.load(f)
    
        return model
    except IOError:
        # Read data and filter out problems with dates.
        usable_bot = datasource.filter_bad_data(datasource.gather_data(bot, sample_size))
        usable_human = datasource.filter_bad_data(datasource.gather_data(human, sample_size))
        X, y = train.create_datasets(usable_human, usable_bot)
        model = create_model(X, y)
        with open(model_name, "wb") as f:
          pickle.dump(model, f)
    
        return model  
      
def classify(user_list, model, limit = sample_size):
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
  
  
# print classify_user([129553279],5,model)
