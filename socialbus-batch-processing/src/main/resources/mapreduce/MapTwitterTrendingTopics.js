function () {
    if("entities" in this){
        hashtags = this.entities.hashtags;

        for ( var i = 0 ; i < hashtags.length ; i++ ) {
          var hashtag = hashtags[i].text.toLowerCase();
          var topic = this.metadata.topic;

          emit({"topic":topic,"hashtag":hashtag},{count:1});
          // emit({"hashtag":hashtag},{count:1});
        }
    }
}