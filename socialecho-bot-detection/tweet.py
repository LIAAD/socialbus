import text
import time
import codecs
import string
from itertools import chain, imap, repeat
import re

re_hostname = re.compile(ur"\b(?:https?://)?(?:[\w-]+\.)*([\w-]+\.[\w]+)")
re_long_url = re.compile(ur"\bhttps?://[-\d\w_./?&]+\.[\w]+|www\.[-\d\w_./?&]+")
re_short_url = re.compile(ur"\bhttp://[-\d\w_]{,10}\.[-\d\w_./?&]{,10}")
re_hashtag = re.compile(ur"(?:^|[^\d\w])#[\w\d]+")
re_user = re.compile(ur"(?:^|[^\w\d])@[\w\d]+")

class Tweet:
  def __init__(self,tweet_data):
    self.data = tweet_data
#TODO: do tokenization.
    self.tokenized_text = tweet_data['text'].split(" ")

  def get_date(self):
    return self.data['user_created_at']

#  Disabled due to changes in the database.
#  def get_local_date(self):
#    return self.data['meta']['created_at_local']


  def __getitem__(self,name):
    return self.data[name]


  def text_features(self):
    return text.all_text_features(self.tokenized_text)


  def entities_features(self):
    text = self.data['text']
    return (
      ('hashtag', len(re_hashtag.findall(text))),
      ('long_url', len(re_long_url.findall(text))),
      ('short_url', len(re_short_url.findall(text))),
      ('user', len(re_user.findall(text))),
    )


  def popular_urls_features(self):

    def hostname(url):
      hostname = re_hostname.match(url)
      if hostname.groups():
        return hostname.groups()[0].lower()
      else:
        return url.lower()


#    def different_urls(url_entity):
#      urls = set( 
#        filter(None, [
#          url_entity.get('url'),
#          url_entity.get('expanded_url'),
#          url_entity.get('display_url')
#          ]
#        )
#      )
#      return map(hostname, urls)


    def determine_class(url):
      return map(
        lambda u: popular_item_presence(
          u,
          popular_urls_tuple,
          popular_urls_set,
          other_class = u"other_host"),
          map(hostname, [url])
      )

    
    def add_features(seq_a, seq_b):
      return imap(lambda a, b: (a[0], a[1]+b[1]), seq_a, seq_b)

    return reduce(
      add_features,
      chain(
        *map(determine_class,
        re_long_url.findall(self.data["text"]))),
      imap(None, popular_urls_tuple, repeat(0))
    )


  def popular_sources_features(self):
    return popular_item_presence(
      self.data['source'],
      popular_clients_tuple,
      popular_clients_set,
      other_class = u"other_source",
    )

  def features(self):
    return chain(
      self.text_features(),
      self.entities_features(),
      self.popular_sources_features(),
      self.popular_urls_features(),
    )


  def prefix(self, size):
    return tuple(self.tokenized_text[:size])


  def prefix_excluding_rt(self, size):
    if self.tokenized_text and self.tokenized_text[0].lower() != "rt":
      return tuple(self.tokenized_text[:size])
    else:
      return tuple()


  def suffix(self, size):
    return tuple(self.tokenized_text[-1*size:])


  def suffix_excluding_url(self, size):
    provisory = self.suffix(size)
    for i in range(-1, -1*len(self.tokenized_text)+1, -1):
      if not text.is_url(provisory[-1]):
        return tuple(provisory)
    return tuple()



# Iterate over a series of items, and return a tuple saying if the item was found or not.
def popular_item_presence(one_item, iterator, fast_lookup, other_class=""):
  absent = one_item not in fast_lookup
  if absent:
    # We know that the item is not present. Say "no" to everything.
    response = imap(None, iterator, repeat(0))
  else:
    # The item was found. Check all of the itmes in the iterator, and say if it is the one.
    response = imap(lambda x: (x, int(x == one_item)), iterator)

  # Should we consider the case of "other", at the end of the iterator?
  # Or do we mind only of those provided by the iterator?
  if other_class:
    other = iter( ( (other_class, int(absent)), ) )
  else:
    other = iter([])
  return chain(response, other)



# Load data at import time.
with codecs.open("popular_url_hosts", "r", "utf-8") as in_file:
  popular_urls_tuple = tuple(imap(string.rstrip, in_file))
  popular_urls_set = set(popular_urls_tuple)


with codecs.open("popular_clients", "r", encoding="utf-8") as in_file:
  popular_clients_tuple = tuple(imap(string.rstrip, in_file))
  popular_clients_set = set(popular_clients_tuple)
