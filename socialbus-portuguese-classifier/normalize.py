import re
import unicodedata

def to_char(s):
  return unichr(int(s,16))

def unaccent(character):
  new_character = ""
  category = unicodedata.category(character)
  if category.startswith("L"):  # If letter.
    decoded = unicodedata.decomposition(character)
    if decoded:  # If complex letter.
      for subchar in decoded.split(" "):
        try:
          printable = to_char(subchar)
        except ValueError:
          continue
        category = unicodedata.category(printable)
        if category.startswith("L"):  # If still letter.
          new_character += printable
        else:
          return new_character
      return new_character
    else:  # Not complex letter
      return character
  else:  # Not letter
    return character



def normalize_text(message,url,mention,spaces):
  no_url_text = url.sub("",message.replace(unichr(0)," ").lower())
  no_url_or_mention_text = mention.sub(r"\1",no_url_text)
  unaccented_text = "".join( unaccent(c) for c in no_url_or_mention_text)
  final_string = spaces.sub(" ",unaccented_text).strip()
  return final_string

class Normalizer:
  url = re.compile(r"\bhttp://[-=\w/.#?&\d]+|\bwww\.[-=\w\/.#?&\d]+")
  mention = re.compile(r"(^|\W)@\w+")
  spaces = re.compile(r"[\x00-\x19\s]+")

  @staticmethod
  def normalize(text):
    return normalize_text(text,Normalizer.url,Normalizer.mention,Normalizer.spaces)
