#!/usr/bin/python2

# Identify the language variant being used by the author of a number of messages.
# Messages are provided as successive "message" fields, and accuracy improves with the number of messages provided.
# At the moment, it supports only the two main variants of Portuguese.

import numpy
import sdb
import sklearn.svm
import sklearn.cross_validation as kfold
import sklearn.metrics
import normalize
import word
import pickle
import json

premade_model = True  # Set to true if you want to load the model from the disk. False to read data from DB.
overwrite_model = False  # If set to true, overwrite the model on disk with the data read from the DB.
sample_size = 100  # Has no effect in the model data if we use premade model data.
model_file = "mongodata.pickle"  # The name of the file to store the model, pickled.
class_names = ("pt", "br")  # The names to return on classification.


# This is the premade collection of users.

pt = [45538821, 84824833, 283027339, 15024150, 7378332, 17084327, 298766124, 411675761, 485975606, 19487287, 276124858, 53888704, 393632069, 81061322, 442843956, 20716592, 152675021, 240426659, 32083145, 878590142, 403759239, 411717118, 188480767, 411439534, 151086155, 100746902, 75511224, 429929535, 270544939, 65019737, 5397, 5394, 71307689, 346018870, 284316677, 63155075, 100798695, 453113386, 285428843, 35865555, 440432132, 130290254, 248900725, 270899370, 15016050, 520423871, 278622214, 29554515, 751198758]
br = [14831625, 16558633, 104445507, 33906365, 27792582, 30953697, 64434051, 8813332, 175720726, 164003736, 115259320, 52165074, 71802362, 571203960, 327588114, 44051923, 290026351, 285626320, 311644724, 354499790, 316552146, 265526616, 131545942, 37747911, 36783462, 270671857, 69078944, 272190572, 208651752, 157785383, 71618262, 28653496, 78088646, 257523301, 280792387, 269518249, 124199219, 325741495, 354508410, 229699666, 117478116, 180972031, 29056831, 456591110, 28121085, 371997256, 280696857, 174244956]


def text_sample(user_id, sample_size = sample_size):
  "Read a sample of the messages of the user referred. You can specify the number of messages to get."
  return normalize.Normalizer.normalize(" ".join(sdb.get_text_sample(user_id, sample_size)))


def merge_matrices(pt, br):
  "Given the matrices for each language, join them together into X and y (classificatoin features and class)."
  X = numpy.vstack((pt, br))
  y = numpy.hstack((numpy.zeros(pt.shape[0]), numpy.ones(br.shape[0])))
  return (X, y)


def create_model(X, y):
  "Creates a trained model given the features matrix and their class."
  classifier = sklearn.svm.SVC(kernel='linear')
  classifier.fit(X, y)
  return classifier


def load_db_data(pt = pt, br = br):
  "Load the user data from the database."
  # First we get the text from the userlist above.
  # Then we extract the features, that are matrices.
  # Finally we join the matrices together to form the language features matrix.
  pt_samples = ( text_sample(u, sample_size) for u in pt )
  pt_features = ( word.features_of_text(sample, sample_size) for sample in pt_samples if sample )
  pt_matrix = numpy.vstack(tuple(pt_features))
  br_samples = ( text_sample(u, sample_size) for u in br )
  br_features = ( word.features_of_text(sample, sample_size) for sample in br_samples if sample )
  br_matrix = numpy.vstack(tuple(br_features))
  X, y = merge_matrices(pt_matrix, br_matrix)
  return X, y


def k_fold_test():
  k = 10 
  fold = kfold.StratifiedKFold(y, k)

  sum_pred = 0.0
  for train_index, test_index in fold:
    X_train, X_test = X[train_index], X[test_index]
    y_train, y_test = y[train_index], y[test_index]
    
    classifier = create_model(X_train, y_train)
    y_prediction = classifier.predict(X_test)
    print(y_prediction, y_test)
    r = sklearn.metrics.precision_score(y_test, y_prediction)
    print(r)
    sum_pred += r

  print(sum_pred / k)


def save_model(model, filename):
  with open(filename, "wb") as f:
    pickle.dump(model, f)


def load_model(filename):
  with open(filename, "rb") as f:
    return pickle.load(f)




# Load the data from either a local file or get it from the DB if absent (and store the new model locally).
try:
  model = load_model(model_file)
except IOError:
  X, y = load_db_data(pt, br)
  model = create_model(X, y)
  save_model(model, model_file)




def classify(messages, model = model):
  "Classify language variant present in all these messages."
  norm_text = normalize.Normalizer.normalize(" ".join(messages))
  features = word.features_of_text(norm_text, len(messages))
  classification = model.predict(features)
  return class_names[int(classification[0])]




if __name__ == "__main__":
  import cgi
  form = cgi.FieldStorage()
  print "Content-Type: application/json"
  print                               # blank line, end of headers
  try:
    messages = form.getlist('message')
    if len(messages) == 0:
      print json.dumps({"errno": -1, "error": "No message found. Use parameter \"message\" to supply messages."})
    else:
      variant = classify(messages, model)
      print json.dumps({"errno": 0, "variant": variant})
  except ValueError:
    print json.dumps({"errno": -1, "error": "No message found. Use parameter \"message\" to supply messages."})
