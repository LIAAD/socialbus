from TwitterSearch import *
from datetime import date
import csv,os, sys, urllib,urllib2, datetime,requests,json,solr,time
from json import loads
from optparse import OptionParser
from pprint import pprint
import time

tokens = [        
# "1547973426-eDXEzMWXtKs30tTlV6SL6yn8sAD9dKUPelRABcV,2q3Jq7PndE0278b6YQKNIZ6zoutqa1aeVPaPAgWgow",
"1548022729-v22PpsKLvtutIyPDtDULHb6JhQzACoglxOWWf9b,iM488s3T49WP8YUX8RMl95Pa0zhA3CD2nRugRBdzFA",
"1548008234-6ArU8kJBJXavJTYbcEpdGWljdb1efsQ2j6S9xlw,nAVgB0cMDdgLasUM5JYYiMhA4NRQIfORXMOIhJHkr8",
"1548014438-FhRUMi6Ez7VO2ckZHXuT5koKSWk2RFZnpFy10rW,AWNAnltviLTLnEFLMzjJrRFMvfuYLTtYw2dFA6MUY",
"1550099874-Rf1g2cHEQrfHTc2rnsFyMhg2L7qoWFTHWCccRs6,krcTntjoQoarqs7Ic6tq0HEjBzazeEOlZo11Tdb8",
"1550103240-8Isj3RJBKqHRuuMzjKdUSjXWbaI1zoEoaQ1nwHZ,Mki8QfwGGXNtkvrApspatbdCRt76fiq7L4IMoAOshLQ",
"1550135072-v7F5B6VxsZYryGjlj5I8mrUq8q19hC9PTJSMPbD,UcBH1cnesN1K80uF3gCuGO6UzEXOaT41kwnoxWQA",
"1550156119-XS9ArZ23v3sQTLqY91FsDSezwFzAPvlJPlYfbyy,wLJ5Mx8qH1JS85fKKS7JeJjXP60iyLZCoIzSYDw8z2w",
"1550139206-hsmPmDrL8oywi819jUP0sRVIEBiBV5xyaOw1oN2,2DFNGbT7I2tdwoP4DDERVjKwwYOQL8UgHvLqKWglgRY",
"1550160037-wlJ28lSFuFDyvwqEaKgZDeS6WkbtvrSolzw5XRD,crO2O022LOC5cD28aNQ3y1uSx0HJYhsccExDJfH2I",
"1550118468-VMNhbWbh73wTWtZjnjhkMmE4WxHodPtNshBMWnf,anpuFLDCF5POxP29pKrWsmXrQfPOaOMAWA12I5Dsrk",
"1550120706-DRkgBoLpUSqshm0Q4Q5gCMGXHX6Ouzskiae2fbo,dTgpZqcAI5q1pWile2QhqWzsJYMgUXELiUaR9T0oq4",
"1550122158-qxJ0AtIE3iOHJiyh1mScvtcbjZaK1M5dAa9VYNf,y4SNw2e4YcBaMNDcIlpdhF1Ydp4p3V262VbZLCLLis",
"1551168680-sWavIep0uTLiguVT23nrfk7f1OXkvTV6586GI0U,lEbvMStJq2OOfMmJUyoPwIWkIxcjJjSbHSC9ovGJo",
"1551192505-sUrb0qVVQSINHXwkSIaM5hNPOcP9LmlrIFzoulp,pfx9xM9mgtT7Ltv3VYWgkItSPTBEP7spChy0ifTz1o",
"1551178082-necbsilYqT9GodXCkl1YQdTG3FFcYqZk33imuBV,OIAoG3bmAfTV3a5c2tBkSuanuuY7FKV34LMkDSQPY8",
"1551200473-vXMJdj5LFncOnNGCK59mZ16SQH0Lzrrv3gK7Chf,asb7n7XP0gdLnKML0jahoXPnz2Zr9jfrCVgKTV5RY",
"1551156120-75ioFzDLFKbEF6BV8PFMJM98ctUfBvc50W72ig3,7zuIhJUJYAPHG4alwsuPF04CEoJscztrSfbmjqYe0",
"1551187538-MlCa3mKhXQ06C6xfQzwXgvX60BeVlUUfC3RBdeN,5L5BxfBmxaWkNmFYtS6cyuD3LMHPS6i9s8DhQ40Y",
"1553636298-JJFvekT6xGf83GU66hSeRKX8eplMnjhawWcXOon,mnTjce2xHB2yVkoydoSC2u1FW8lOS5HfZqfX8EdZ7A",
"1507816758-GAaBK71z8mUCmsrhUMKCQQ9jovPgnHf53LFVm1F,vtGS7F0CjPiKMH3zOjr86D3fUMT2M6ce43U9r6YWtc",
"1508113506-8DIZ4o2RWkYxEjI0B09YwfLTyuCwQRiAtk1o99B,hL2KQEWrw9MXMoXJgQfpHv7dh46QTuTxXlLEwbB0uY",
"19595038-ktoF42PZrAEU1pFoHYKVktsgQuQ6kEWAQ77wTFQAw,pejWmJ57tmqKeDNgOzK8F5nR7UDKq0mFvFEKiaAywU",
# "1547962976-p95l3xWhaJ4GMKcp7YsqXnH2VcpThF1rkfUgM04,j5AE8KJSQJdbjR7stj2mIewxSAEwqXlzR5AVq6AgMM"
        ]

httpuser = "reaction_solr"
httppass = "r34ct10n@2013!@#$$#@!"

last_token_index = 0

def ask_for_twitter_token(token):
    try:
        ts = TwitterSearch(
        consumer_key = 'GJGALwtkuTDljV3eBDAYaQ',
        consumer_secret = 'lGENO2QQVasMzalcpp6A3fAciNO1u0wA2ZihMpibyY0',

        access_token = token.split(",")[0],
        access_token_secret = token.split(",")[1]
        )

        return ts
    except:
        time.sleep(30)
        ask_for_twitter_token(token)

    return None
    

def build_tokens(index):
    global last_token_index
    next_token = 0

    if not(last_token_index == (len(tokens) - 1)):
        next_token = last_token_index + 1
    else:
        next_token = 0
    
    token = tokens[next_token]

    print "last token id", last_token_index
    print "next token id", next_token
    print "tokens size", len(tokens)
    
    last_token_index = next_token

    ts = ask_for_twitter_token(token)
    
    print "last token", tokens[next_token - 1]
    print "next token", token

    return ts    

def read_queries(input_file):
    print "Reading queries"

    ins = open( input_file, "r" )
    queries = []
    for line in ins:
        # queries.append( line )
        if(cmd_options.search_by_users):
            queries.append( "from:" + line )
        else:
            queries.append( line )

    ins.close()
    return queries

def create_file_name(query):
    file_name = "tweets_"
    file_name = query.replace(" ","_")
    file_name = file_name.replace(":","_")
    file_name = file_name.lower()
    return file_name

def twitter_search(query,token):
    print "---------------------------------"
    print "Searching for tweets from ", query

    tweets_by_query = 0

    tso = TwitterSearchOrder() # create a TwitterSearchOrder object
    tso.setKeywords(query) # let's define all words we would like to have a look for

    # tso.setLanguage('pt') # we want to see German tweets only
    tso.setCount(100) # please dear Mr Twitter, only give us 7 results per page
    tso.setIncludeEntities(True) # and don't give us all those entity information

    output_file = create_file_name(query[0])
    output_file = cmd_options.output_dir + "/" + output_file

    print "Output file ", output_file

    f = open(output_file, 'w')

    try:
        for tweet in token.searchTweetsIterable(tso): # this is where the fun actually starts :)
            tweets_by_query = tweets_by_query + 1
            #print( '@%s tweeted: %s' % ( tweet['user']['screen_name'], tweet['text'] ) )
            print tweet['id'],tweet['created_at'],tweet['user']['screen_name']

            tweet_json = json.dumps(tweet)
            f.write(tweet_json)
            f.write("\n")

            # indexTweetIntoSolr(tweet)

    	# f.close()
    	print "---------------------------------"
    	print "Found ", str(tweets_by_query)," tweets for query ",query
    	print "---------------------------------"
    except:
        print "oops", "Too Many Requests"
        print "Rotating tokens"

        print "sleep a few seconds"
        time.sleep(10)

        token = build_tokens(last_token_index)
        twitter_search(query,token)

def indexTweetIntoSolr(json):
    user_data = json['user']

    #Sat Nov 10 01:59:54 +0000 2012
    userCreatedAt = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.strptime(json['user']['created_at'],'%a %b %d %H:%M:%S +0000 %Y'))
    statusCreatedAt = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.strptime(json['created_at'],'%a %b %d %H:%M:%S +0000 %Y'))

    print statusCreatedAt + " : " +  str(json['id']) + " : " +  json['text'].encode("utf-8");

    #_tokenized_text = tokenizer.tokenize(json["text"])

    _user_mentions = []
    _url_mentions = []
    _hashtag_mentions = []

    entities = json['entities']

    for user_mention in entities['user_mentions']:
        _user_mentions.append(user_mention["screen_name"])

    for url_mention in entities['urls']:
        _url_mentions.append(url_mention["url"])

    for hashtag_mention in entities['hashtags']:
        _hashtag_mentions.append(hashtag_mention["text"])

    try:
        s_tweets.add(
        #tokenized_text=_tokenized_text,
        hashtags=_hashtag_mentions,
        urls=_url_mentions,
        mentions=_user_mentions,
        user_id=json['user']['id'],
        name=json['user']['name'],
        screen_name=json['user']['screen_name'],
        location=json['user']['location'],
        description=json['user']['description'],
        user_created_at=userCreatedAt,
        lang=json['user']['lang'],
        time_zone=json['user']['time_zone'],
        followers_count=json['user']['followers_count'],
        favourites_count=json['user']['favourites_count'],
        statuses_count=json['user']['statuses_count'],
        friends_count=json['user']['friends_count'],
        listed_count=json['user']['listed_count'],
        profile_background_color=json['user']['profile_background_color'],
        profile_background_image_url=json['user']['profile_background_image_url'],
        profile_background_image_url_https=json['user']['profile_background_image_url_https'],
        profile_image_url_https=json['user']['profile_image_url_https'],
        profile_image_url=json['user']['profile_image_url'],
        profile_link_color=json['user']['profile_link_color'],
        profile_sidebar_border_color=json['user']['profile_sidebar_border_color'],
        profile_sidebar_fill_color=json['user']['profile_sidebar_fill_color'],
        profile_text_color=json['user']['profile_text_color'],
        id=json['id'],
        text=json['text'],
        created_at=statusCreatedAt,
        source=json['source'],
        status_in_reply_to_screen_name=json['in_reply_to_screen_name'],
        status_in_reply_to_user_id=json['in_reply_to_user_id'],
        status_in_reply_to_status_id=json['in_reply_to_status_id'],
        retweet_count=int(json['retweet_count']),
        version='12',
        datasource_s="search_api"
        )
    except solr.core.SolrException:
		print "Oops! Solr Request Error :" , sys.exc_info()[0]


if __name__ == "__main__":
    # Command Line Arguments Parser
    cmd_parser = OptionParser(version="%prog 0.1")
    cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="solr_address", help="Solr Address")
    cmd_parser.add_option("-O", "--outputdir", type="string", action="store", dest="output_dir", help="Output dir")
    cmd_parser.add_option("-F", "--input_queries", type="string", action="store", dest="input_queries", help="Input queries")
    cmd_parser.add_option("-U", action="store_true", dest="search_by_users", help="Search tweets by users")

    # cmd_parser.add_option("-T", "--token", type="string", action="store", dest="token", help="Token")
    # cmd_parser.add_option("-N", "--tokensecret", type="string", action="store", dest="token_secret", help="Token secret")


    (cmd_options, cmd_args) = cmd_parser.parse_args()

    # if not (cmd_options.input_queries or cmd_options.solr_address):
    if not (cmd_options.input_queries):
        cmd_parser.print_help()
        sys.exit(3)

    if not cmd_options.output_dir:
    	cmd_options.output_dir = "."

	# create a connection to a solr server
	# s_tweets = solr.SolrConnection(cmd_options.solr_address,http_user=httpuser,http_pass=httppass)

	print "##################################################################"
	print " [INFO] Twitter Search to Solr "
	print "------------------------------------------------------------------"

    queries = read_queries(cmd_options.input_queries)

    token = build_tokens(last_token_index)

    for query in queries:
        query = query.replace("\n","")
        print "sleep a few seconds"
        time.sleep(2)
        twitter_search([query],token)
    	
	print "------------------------------------------------------------------"
	print " [Done]"
	print "------------------------------------------------------------------"