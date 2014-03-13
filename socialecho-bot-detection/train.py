from author import Author
import numpy

# Since the current version of scikit-learn is 0.10.0, it does not have DictVectorizer implemented.
# Thus, we have to do it ourselves.

def DictVectorizer(author):
  if isinstance(author, (list, tuple)):
    return numpy.array([ a.features().values() for a in author ])
  elif isinstance(author, Author):
    return numpy.array(list(author.features().values()))


def create_datasets(human, bot):
# I wish I could have the time to work with iterators. But we are only supporting lists or tuples at this moment.
  human_list = list(human)
  bot_list = list(bot)
  array = DictVectorizer(human_list + bot_list)
  values = numpy.array([0] * len(human_list) + [1] * len(bot_list)).transpose()
  return (array, values)


