function () {
    if("entities" in this){
        mentions = this.entities.user_mentions;

        for ( var i = 0 ; i < mentions.length ; i++ ) {
          var mention = mentions[i].screen_name.toLowerCase();
		  var topic = this.metadata.topic;
          emit({"topic":topic,"screen_name":mention},{count:1});
        }
    }
}