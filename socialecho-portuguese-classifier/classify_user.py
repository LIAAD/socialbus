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
sample_size = 5  # Has no effect in the model data if we use premade model data.
model_file = "mongodata.pickle"  # The name of the file to store the model, pickled.
class_names = ("pt", "br")  # The names to return on classification.


# This is the premade collection of users.

pt = [   
2264444869,
53161052,
1011077418,
2153646048,
236138844,
1339859353,
212293323,
472169787,
1339859353,
1460491975,
212293323,
2190608648,
717874436,
549264462,
997860512,
1460491975
]

br = [
87843887,
443048958,
87843887,
87843887,
1069863936,
443048958,
443048958,
199901650,
393401881,
199901650
]

# db.twitter.find({_id:{$in:[436229899020689409,436249107183964161,436259186364915713,436276712255799296,436310382194741248,436581291228950528,436637473570312192,436828457671348224,436935368756432896,437000025143783424,437447659809300480,437615529382002688,437732762444132352,437735194985246721,437760998695796736,438004134244286464,438017630730194944,438017712129048576,438017876201844736,438028190616154112,438079892304183296,440590461724794880,440877790733557761,440903906454171648,440914662084067328,441520873422745600,441532032376930304,441544644821016576,441552085289811968,441555983538864129,441556333515796480,441571124175175680,441667247053238274,441684872110022656,441685606868221953,441686981459058688,441694961298591744,441705391073013760,441710362287931392,441719836008513536,43628406292876492,43628248059401830,43643930947092480,43647142375511244,436533966301298689,43661676535192371,43661903720510259,436623647328174081,436647586057818112,43665534046650368,43686432079924428,436897643110096896,43734180011876761,43757654689185792,43760635080239923,43760651855778611,437608828629032960,43800215712447692,43808724673102643,43809079219298304,438090946866331648,438091003149701122,438108751066071040,438119070412836864,438183703412355072,44000107475451084,440922749469335552,448546420216590337,44174745596555264,44121748764101017,44128025600014336,441288407747407872,442114639761928192,448496517641674752,442334679761698816,442675007383433217,442837696516747264,44336087002306150,443380147656617984,44341479194912358,436534495043268608,436534497224298498,43680106082993356,43732087378857164,43739483702612787,448540356049920000,44775845986673868,44753452293949030,44717209162091724,44756449629152870,44675756054966681,436906361868541952,44428402054570803]}}).count()

# db.twitter.find({_id:{$in:[436279144717230080,436591409936994304,436616567766671360,437244894697230336,437315719991001088,43769251987941785 ,437694655480987648,437697395007033344,43769741752245452 ,437698445277921280,43769942177900953 ,437710307490668544,437710611913654273,437728411868741632,437882465407885312,437979993000660992,437980084163837952,448574019680632832,438271917330792448,44857315812864000 ,44855345911814144 ,44855236922824704 ,44855124969300377 ,44855119099372339 ,44855105471697305 ,44854955110094848 ,44854861952084787 ,44854451310008729 ,44854370674515968 ,44854326877172940 ,44853918844794880 ,44853889084530278 ,44860332177583308 ,44861206420733952 ,44861208704950681 ,448533838353149954,44853104610430566 ,44863699223106355 ,448527385257058304,44852407221827993 ,448575035255816192,436227487811796992,436259855310262272,436459556148105216,436580772271882240,436590972928294913,436591779841081344,436591774392647681,436594262264664064,436595302272090113,436598691345203200,436599510287282176,436604066228371456,436606178060021760,436607059183038464,436610878960852992,436614308748075008,436621335108673536,436624252209758209,436636366651551744,436662520846512128,436864587854794752,436889789783347200,436947235759083520,436965276517924864,436985508749930497,437303413513408513,437339304315338752,437366411410022402,437352154798768128,437368320376209409,437374055025762304,437402882686877696,437402971421556737,437403036684935168,437541652341600256,437586175122219008,437654232364113920,437654857692889088]}},{"user.id":1,"_id":false})




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
  pt_samples = list( text_sample(u, sample_size) for u in pt )
  print pt_samples
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




def initialize_model():
    # Load the data from either a local file or get it from the DB if absent (and store the new model locally).
    try:
      model = load_model(model_file)
      return model
      
    except IOError:
      X, y = load_db_data(pt, br)
      model = create_model(X, y)
      save_model(model, model_file)
      
      return model  

# def classify(messages, model = model):
def classify(messages, model):
  "Classify language variant present in all these messages."
  norm_text = normalize.Normalizer.normalize(" ".join(messages))
  features = word.features_of_text(norm_text, len(messages))
  classification = model.predict(features)
  return class_names[int(classification[0])]

# print classify(["lindo!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! porto porto porto http://t.co/HZlswsZuf3"], model)
# print classify(["E VC AINDA ACREDITA QUE ESTE GOVFDRL PT E SOCIAL??  #VemPraRua #VemPraURNA  RT @JoaoBolsoni:  povo? Esse? http://t.co/KGPAI0Ao59"], model)


# if __name__ == "__main__":
#   import cgi
#   form = cgi.FieldStorage()
#   print "Content-Type: application/json"
#   print                               # blank line, end of headers
#   try:
#     messages = form.getlist('message')
#     if len(messages) == 0:
#       print json.dumps({"errno": -1, "error": "No message found. Use parameter \"message\" to supply messages."})
#     else:
#       variant = classify(messages, model)
#       print json.dumps({"errno": 0, "variant": variant})
#   except ValueError:
#     print json.dumps({"errno": -1, "error": "No message found. Use parameter \"message\" to supply messages."})
