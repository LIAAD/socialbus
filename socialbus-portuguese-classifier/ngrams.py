import collections
gram = 3

def count_ngrams(text):
  all_ngrams = collections.Counter()
  gram_count = 0

  if not text:
    return all_ngrams
  tokens = text.rstrip()
  if len(tokens) < gram:
    return all_ngrams
  for i in range(len(tokens) - gram + 1):
    all_ngrams[tokens[i:i+gram]] += 1
    gram_count += 1

  return all_ngrams
