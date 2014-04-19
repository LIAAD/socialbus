# Words: defined by tokenization. Can be searched by set lookup. Faster to find.
#  Immune to errors derived by matching substrings. Ex: "the" matching in string "thesis".
# Expressions: defined only by string matching (regular expressions, actually).
#  Can handle strings containing multiple words (with spaces), and ngrams. Slower to process.


import collections
import operator
import functools
import re
import numpy

# Define the files to load.
pt_words = ("dict_pt.txt", "expressions_pt.txt")
pt_expressions = ("multiword_expressions_pt.txt", "entities_pt.txt", "ngrams_pt.txt")
br_words = ("dict_br.txt", "expressions_br.txt")
br_expressions = ("multiword_expressions_br.txt", "entities_br.txt", "ngrams_br.txt")



def load_file(filename):
  with open(filename, "rb") as f:
    # We use a space at the start of a line in the data files to mark a comment line.
    return { x.rstrip().decode("utf-8") for x in f if not x.startswith(" ") }



def load_all_words_separated():
  pt_words_set = functools.reduce(operator.or_, [ load_file(x) for x in pt_words ])
  br_words_set = functools.reduce(operator.or_, [ load_file(x) for x in br_words ])
  return (
      pt_words_set - br_words_set,
      br_words_set - pt_words_set,
  )


def load_all_words_mixed():
  pt, br = load_all_words_separated()
  return sorted(list(pt) + list(br))



def load_all_expressions_seperated():
  pt_expressions_set = functools.reduce(operator.or_, [ load_file(x) for x in pt_expressions ])
  br_expressions_set = functools.reduce(operator.or_, [ load_file(x) for x in br_expressions ])

  def to_regexp(expressions):
    return [ re.compile(x) for x in expressions ]

  return (
      to_regexp(pt_expressions_set - br_expressions_set),
      to_regexp(br_expressions_set - pt_expressions_set),
  )


def load_all_expressions_mixed():
  pt, br = load_all_expressions_seperated()
  return sorted(pt + br, key=operator.attrgetter("pattern"))



def count_words_present(bag_of_words, dictionary):
  return collections.Counter( word for word in bag_of_words if word in dictionary )


def count_expressions_present(text, re_dictionary):
  return functools.reduce(
      operator.add,
      (
          collections.Counter(regexp.findall(text)) for regexp in re_dictionary
      ),
      collections.Counter()
  )




def DictVectorizer(feature_list, occurences, message_count):
  return numpy.array([ occurences[feature] for feature in feature_list ]) / float(message_count)



__words = load_all_words_mixed()
__expressions = load_all_expressions_mixed()
__all_features = __words + list(map(operator.attrgetter("pattern"), __expressions))

def features_of_text(text, n_messages):
  def tokenize(text):
    return re.findall(ur"\b\w\b", text)

  return DictVectorizer(
      __all_features,
      count_words_present(tokenize(text), __words) + count_expressions_present(text, __expressions),
      n_messages
  ) 
