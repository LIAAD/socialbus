#!/usr/bin/env python
"""
twitterList.py

Created by Arian Pasquali on 2012-12-28.
e-mail: me (at) arianpasquali.com   twitter: arianpasquali
"""

'''Twitter feature extractor for statuses'''

__author__ = 'me@arianpasquali.com'
__version__ = '0.1'

import re
from time import *
from datetime import *
import simplejson as json

class TwitterUserFeatures(object):
    REGISTRATION_AGE = "REGISTRATION_AGE"
    STATUSES_COUNT = "STATUSES_COUNT"
    COUNT_FOLLOWERS = "COUNT_FOLLOWERS" 
    COUNT_FRIENDS = "COUNT_FRIENDS"
    IS_VERIFIED = "IS_VERIFIED"
    HAS_DESCRIPTION = "HAS_DESCRIPTION"
    HAS_URL = "HAS_URL"

class TwitterMessageFeatures(object):
    LENGTH_CHARACTERS  = "LENGTH_CHARACTERS"
    LENGTH_WORDS = "LENGTH_WORDS"
    CONTAINS_QUESTION_MARK = "CONTAINS_QUESTION_MARK"
    CONTAINS_EXCLAMATION_MARK = "CONTAINS_EXCLAMATION_MARK"
    CONTAINS_MULTI_QUEST_OR_EXCL = "CONTAINS_MULTI_QUEST_OR_EXCL"
    CONTAINS_EMOTICON_SMILE = "CONTAINS_EMOTICON_SMILE"
    CONTAINS_EMOTICON_FROWN = "CONTAINS_EMOTICON_FROWN"
    CONTAINS_PRONOUN = "CONTAINS_PRONOUN"
    COUNT_UPPERCASE_LETTERS = "COUNT_UPPERCASE_LETTERS"
    NUMBER_OF_URLS = "NUMBER_OF_URLS"
    CONTAINS_POPULAR_DOMAIN_TOP_100 = "CONTAINS_POPULAR_DOMAIN_TOP_100" 
    CONTAINS_POPULAR_DOMAIN_TOP_1000 = "CONTAINS_POPULAR_DOMAIN_TOP_1000" 
    CONTAINS_POPULAR_DOMAIN_TOP_10000 = "CONTAINS_POPULAR_DOMAIN_TOP_10000" 
    CONTAINS_USER_MENTION = "CONTAINS_USER_MENTION"
    CONTAINS_HASHTAG = "CONTAINS_HASHTAG"
    CONTAINS_STOCK_SYMBOL = "CONTAINS_STOCK_SYMBOL"
    IS_RETWEET = "IS_RETWEET"
    DAY_WEEKDAY = "DAY_WEEKDAY"
    SENTIMENT_POSITIVE_WORDS = "SENTIMENT_POSITIVE_WORDS"
    SENTIMENT_NEGATIVE_WORDS = "SENTIMENT_NEGATIVE_WORDS"
    SENTIMENT_SCORE ="SENTIMENT_SCORE"
    HASHTAGS ="HASHTAGS"
    URLS ="URLS"

## extract message features
class TwitterFeatureExtractor(object):
        
    def __init__(self):
        '''Instantiate a new TwitterFeatureExtractor object.
        
        Args:
        	status: the twitter status json object
        '''    
        
    def extractUserFeatures(self,_status):
        
        _user = _status["user"]
        
        _features = {}
        
        _features[TwitterUserFeatures.REGISTRATION_AGE] = self.getRegistrationAge(_user)
        _features[TwitterUserFeatures.HAS_DESCRIPTION] = self.hasDescription(_user)
        _features[TwitterUserFeatures.HAS_URL] = self.hasUrl(_user)
        _features[TwitterUserFeatures.STATUSES_COUNT] = self.countStatuses(_user)
        _features[TwitterUserFeatures.COUNT_FOLLOWERS] = self.countFollowers(_user)
        _features[TwitterUserFeatures.COUNT_FRIENDS] = self.countFriends(_user)
        _features[TwitterUserFeatures.IS_VERIFIED] = self.isVerified(_user)
        
        return _features
    
    def extractMessageFeatures(self,_status):
        
        _text = _status["text"]
        _features = {}
        
        _features[TwitterMessageFeatures.LENGTH_CHARACTERS] = self.getLengthCharacters(_text)
        _features[TwitterMessageFeatures.LENGTH_WORDS] = self.getLengthWords(_text)
        _features[TwitterMessageFeatures.CONTAINS_QUESTION_MARK] = self.containsQuestionMark(_text)
        _features[TwitterMessageFeatures.CONTAINS_EXCLAMATION_MARK] = self.containsExclamationMark(_text)
        _features[TwitterMessageFeatures.CONTAINS_MULTI_QUEST_OR_EXCL] = self.containsMultiQuestOrExcl(_text)
        _features[TwitterMessageFeatures.CONTAINS_EMOTICON_SMILE] = self.containsEmoticonSmile(_text)
        _features[TwitterMessageFeatures.CONTAINS_EMOTICON_FROWN] = self.containsEmoticonFrown(_text)
        _features[TwitterMessageFeatures.CONTAINS_PRONOUN] = self.containsPronoun(_text)
        _features[TwitterMessageFeatures.COUNT_UPPERCASE_LETTERS] = self.countUppercaseLetters(_text)
        _features[TwitterMessageFeatures.NUMBER_OF_URLS] = self.countUrls(_text)
        _features[TwitterMessageFeatures.CONTAINS_USER_MENTION] = self.containsUserMentions(_text)
        _features[TwitterMessageFeatures.CONTAINS_HASHTAG] = self.containsHashtags(_text)
        _features[TwitterMessageFeatures.HASHTAGS] = self.getHashtags(_text)
        _features[TwitterMessageFeatures.IS_RETWEET] = self.isRetweet(_text)
        _features[TwitterMessageFeatures.URLS] = self.getUrls(_text)
        # _features[TwitterMessageFeatures.DAY_WEEKDAY] = self.getWeekday(self._status["created_at"])      
        
        return _features    
            
    def getLengthCharacters(self,text):
        return len(text.replace(" ",""))
    
    def getLengthWords(self,text):
        text = re.sub(' +',' ',text)
    
        return len(text.split(" "))    

    def containsQuestionMark(self,text):
        return 1 if text.find("?") > -1 else -1
    
    def containsExclamationMark(self,text):
        return 1 if text.find("!") > -1 else -1
        
    def containsMultiQuestOrExcl(self,text):
        if not self.containsQuestionMark(text):
            return -1 
            
        if not self.containsExclamationMark(text):
            return -1 
        
        return 1 if (text.count("?") > 1 or text.count("!") > 1) else -1    
    
    def containsEmoticonSmile(self,text):
        smiles = ["=)","=-)","=D","=-D",":-)",";-)",":)",";)",":D",":-D",";-D",":P",";P"]
    
        for emoticon in smiles:
            if(text.find(emoticon) > -1):
                return 1
        
        return -1
    
    def containsEmoticonFrown(self,text):
        frowns = [":-(",";-(",":(","=(","=-("]
    
        for emoticon in frowns:
            if(text.find(emoticon) > -1):
                return 1
        
        return -1    
    
    def containsPronoun(self,text):
        pronouns = ["I","me","you","him","her"]
    
        for pronoun in pronouns:
            if(text.find(pronoun) > -1):
                return 1
        
        return -1
         
    def countUppercaseLetters(self,text):
        return len(re.findall("[A-Z]", text))
    
    def countUrls(self,text):
        return text.count("http")
    
    def containsUserMentions(self,text):
        res = self.getUserMentions(text)
        return 1 if len(res) > 0 else -1
        
    def getUserMentions(self,text):
        twitter_username_re = re.compile(r'@([A-Za-z0-9_]+)')
        return re.findall(twitter_username_re,text)
    
    def containsHashtags(self,text):
        return 1 if len(self.getHashtags(text)) > 0 else -1
    
    def getHashtags(self,text):
        hashtag_re = re.compile(r'#(\w+)')
        res = re.findall(hashtag_re,text)
        
        return res
        
        
    def getUrls(self,text):
        url_re = re.compile(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+')
        res = re.findall(url_re,text)
        
        return res    
        
    def isRetweet(self,text):
        return text.find("RT ")
    
    def getWeekday(self,_date):
        d = datetime.strptime(_date,'%a %b %d %H:%M:%S +0000 %Y')
        return d.date().weekday()
    
    ############################################    
    ## extract user features    

    def getRegistrationAge(self,twitter_user):
        created_at = datetime.strptime(twitter_user["created_at"],'%a %b %d %H:%M:%S +0000 %Y')
        #age in days
        age = int((date.today() - created_at.date()).days)
    
        return age

    def countStatuses(self,twitter_user):
        return twitter_user["statuses_count"]
    
    def countFollowers(self,twitter_user):
        return twitter_user["followers_count"]

    def countFriends(self,twitter_user):
        return twitter_user["friends_count"]    
    
    def isVerified(self,twitter_user):
        return twitter_user["verified"]        
    
    def hasDescription(self,twitter_user):
        return twitter_user["description"]!=""
    
    def hasUrl(self,twitter_user):
        return twitter_user["url"]!=""            