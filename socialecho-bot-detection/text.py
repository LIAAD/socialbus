# This should be refactored in the following way:
# features should be represented as a triple: (name, recognizer, finzlizer), where
# - name is the name of the feature
# - recognizer is a function that takes one argmuent (the text) and returns the relevant tokens (list or scalar)
# - finalizer is a function that takes two arguments: the name of the feature and the output of the finzlizer. Returns a list of tuples (name, scalar) that should be included into the final result.
#
# We could replace the finalizer to obtain different results. For example, the smiles finalizer could distinguish between left- and right-facing smiles, or horizontal and vertical smiles.
# We could create vocabulary recognizers to react on pre-defined words.
# Typical finalizers could be count (number of tokens recognized), proportion (count / total number of tokens in the text) and popularity (the proportion of the most popular token in relation to the second most popular one).

import re
from itertools import chain

complex_word_threshold = 0.5
complex_word_minimum_length = 6
interjection_minimum_length = 4
re_word = re.compile(r"^\w+$")
re_final_punct = re.compile(r"[.!?]+")
re_url = re.compile(r"(?i)http://[\w\d][-\w\d._/?=#%]{2,}[\w\d?#]$|www\.[-\w\d._/?=#%]+[\w\d?#]")
re_username = re.compile(r"@[\w\d_]+")
re_hashtag = re.compile(r"#[\w\d_]+")

def is_word(token):
  return bool(re_word.match(token))


def is_username(token):
  return bool(token.startswith("@")) and len(token) > 1


def is_url(token):
  return bool(re_url.match(token))


def is_final_punctuation(token):
  return bool(re_final_punct.match(token))


def complex_word(token_list):
  return [ token for token in token_list
    if len(token) > complex_word_minimum_length
      and is_word(token)
      and float(len(set(token))) / len(token) >= complex_word_threshold ]


def interjection(token_list):
  return [ token for token in token_list
    if len(token) > interjection_minimum_length
      and is_word(token)
      and float(len(set(token))) / len(token) < complex_word_threshold ]


def avg_word_length(token_list):
  word_list = filter_regexp(token_list, re_word)
  if word_list:
    return sum( len(word) for word in word_list ) / float(len(word_list))
  else:
    return 0


def filter_regexp(token_list, regexp):
  return [ token for token in token_list if regexp.match(token) ]



# We keep these in the global scope so they will be processed only once,
# when the module is imported.
feature_src = (
  ("avg_word_length", avg_word_length),
  ("complex_word", complex_word),
  ("date", r"\d{4}-\d{1,2}-\d{1,2}$|\d{4}/\d{1,2}/\d{1,2}$|\d{1,2}/\d{1,2}/\d{4}$|\d{1,2}-\d{1,2}-\d{4}"),
  ("full_length", lambda l: sum( len(t) for t in l ) + len(l) - 1),
  ("interjection", interjection),
  ("is_reply", lambda l: int(is_username(l[0]))),
  ("lol", r"(?i)l+(?:o+l+)+"),
  ("number", r"\d+(\.,\d+)*"),
  ("numberword", r"\w*(\d+\w+)+$|\d*(\w+\d+)+"),
  ("one_letter_words", lambda l: [ t for t in l if len(t) == 1 and re_word.match(t) ]),
  ("punctuation", r"\.+$|!+$|\?+$|!*(\?*!*)*$|,+"),
  ("smiley", r":-?\)$|:-?D$|[Xx]D$|<3$|&lt;3$|;\)$|:-?\($|:-?[pP]$|:-?\)$|\*-\*$|:-?[sS]$|=\)$|\^_\^"),
  ("time", r"(?i)\d{1,2}:\d{2}$|\d{1,2}h\d{2}m?"),
  ("token_count", lambda l: l),
  ("word_count", lambda l: [ t for t in l if is_word(t) ]),
)

feature_data = tuple(
  (
    name,
    re.compile(value+"$", re.UNICODE) if isinstance(value, basestring) else value
  )
  for name, value in feature_src
)


def identify_presence_features(token_list):
  "Identify features by looking at the tokens one by one."
  def to_int(result):
    if isinstance(result,list):
      return len(result)
    else:
      return result

  return (
    (
      name,
      len([ token for token in token_list if re.match(value,token) ])
          if type(value) is re._pattern_type
      else to_int(value(token_list))
    )
    for name, value
      in feature_data
  )


def style_features(token_list):
  "Find features that look at the text as a whole."
  return (
    ("ends_with_url", is_url(token_list[-1])),
    ("number_sentences", len(filter(is_final_punctuation, token_list)) - 1),
  )
  return 


def all_text_features(token_list):
  return chain(identify_presence_features(token_list), style_features(token_list))
