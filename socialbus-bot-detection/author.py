# Please note: I tried to keep the features ordered alphabetically.
import json
from collections import Counter,OrderedDict
import tweet
import codecs
import itertools
import operator
import math
import time


class Author:
  def __init__(self,json_user_data,tweets=None):
    self.user_data = json_user_data
    self.tweets = [ tweet.Tweet(t) for t in tweets ]


  def id(self):
    return self.user_data["id"]
    

  def date_features(self):
    "Find features related with the dates of publishing."

# Calculate the variation of the author's posting times.
    def std_dev_values(measures, time_list):
      "Calculates the standard deviation methods for some date values in the tweets."

      def std_dev(values):
        "Calculates std deviation for a series of values."
        avg = float(sum(values))/len(values)
        variance = sum(itertools.imap(lambda val: (avg-val)**2, values))/(len(values)-1)
        return math.sqrt(variance)

      return (
        ( name, std_dev(map(operation, time_list)) )
          for name, operation in measures
      )

    std_dev_measures = (
      ("day_of_week_std-dev", lambda x: x.weekday()),
      ("day_std-dev", operator.attrgetter("day")),
      ("hour_std-dev", operator.attrgetter("hour")),
      ("minutes_std-dev", operator.attrgetter("minute")),
      ("same_day_of_year_std-dev", lambda x: x.month*100 + x.day),
      ("same_hour-minute_std-dev", lambda x: x.hour*100 + x.minute),
      ("seconds_std-dev", operator.attrgetter("second")),
    )


# Calculate the author's time consistency.
# We are trying to reuse the std_dev above.
    def interval_values(time_data):

      def epoch_hours(datetime):  # We use hours to lower the scale.
        return time.mktime(datetime.timetuple()) / 3600 

      unix_time = itertools.imap(epoch_hours, time_data)
      before, after = itertools.tee(unix_time)
      after.next()
      return itertools.imap(None, after, before)

    interval_measures = (
      ("posting_interval_std-dev", lambda x: x[0]-x[1]),
    )


# Calculate the author's predisposition to post at certain days and time periods.
    def proportion_values(measures, time_list):
      "Calculates the proportion of some measures against the total number of tweets."
      total = float(len(time_list))
      return (
        ( name, len(filter(None,(itertools.imap(operator, time_list)))) / total )
          for name, operator in measures
      )

    weekend_values = {6, 7}
    proportion_measures = (
      ("weekend_posting_proportion", lambda x: x.weekday() in weekend_values),
      ("dawn_posting_proportion", lambda x: x.hour >= 1 and x.hour <= 8),
      ("evening_posting_proportion", lambda x: x.hour >= 20 or x.hour <= 1),
      ("lunchtime_posting_proportion", lambda x: x.hour >= 12 and x.hour <= 14),
    )


    time_data = map(operator.methodcaller("get_date"), self.tweets)

    return itertools.chain(
      std_dev_values(std_dev_measures, time_data),
      std_dev_values(interval_measures, interval_values(time_data)),
      proportion_values(proportion_measures, time_data)
    )
    

  def pattern_features(self):
    "Finds patterns across the text of several messages."
# We could do much more than what we are doing now with this information.
# Currently we just present the proportin of the most frequent pattern.
    total_tweets = float(len(self.tweets))
    def aggregate_pattern(pair):
      "Processes each pattern for each token lenght considered."
      name, patterns = pair
      return (
        (
          # Create a name from the pattern type and size,
          # and calculate the frequency of the most common pattern found.
          "{}-{:d}".format(name,pattern_size),
          pattern_count.most_common(1)[0][1]/total_tweets if pattern_count else 0.0
        )
        for pattern_size, pattern_count in patterns
      )
      
    pattern = [
      ('prefix', self.__collect_pattern("prefix")),
      ('prefix_excluding_rt', self.__collect_pattern("prefix_excluding_rt")),
      ('suffix', self.__collect_pattern("suffix")),
      ('suffix_without_url', self.__collect_pattern("suffix_excluding_url")),
    ]
    return ( itertools.chain.from_iterable( map(aggregate_pattern, pattern )) )


  def __collect_pattern(self, function, max_size = 5):
    return (
      (
        length,
        Counter( itertools.ifilter(None,
          itertools.imap(operator.methodcaller(function, length), self.tweets) )
        )
      )
      for length in range(1, max_size+1)
    )


  def tweets_features(self):
    "Processes the messages published by the author."

    def proportion(all_tweets_values):
      "Calculates the average of all features provided."
      #TODO: Maybe look at NumPy array?
      if not all_tweets_values:
        return []
      total = float(len(all_tweets_values))
      aggregator = OrderedDict(all_tweets_values[0])
      for tweet in all_tweets_values[1:]:
        for (n, v) in tweet:
          aggregator[n] += v
      return ((n, v/total) for n, v in aggregator.items())

    tweet_features = map(operator.methodcaller("features"), self.tweets)
    return proportion(tweet_features)


  def features(self):
    "Return all the features that we can extract."
    return OrderedDict( itertools.chain(
        self.tweets_features(),
        self.pattern_features(),
        self.date_features(),
    ) )


  def check_usable_dates(self):
    "There is a bug in the database, where some dates are unicode strings. Check for that."
    return not any( isinstance(x["created_at"], unicode) for x in self.tweets )
