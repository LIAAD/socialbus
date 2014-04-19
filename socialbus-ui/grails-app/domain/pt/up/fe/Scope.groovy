package pt.up.fe

class Scope {

    String name
    String language = "All"

    Date dateCreated
    Date lastUpdated

    TwitterUser twitterToken

    boolean active = true

    static constraints = {
        name(blank: false)
        language(inList: ["All", "Portuguese", "English", "Spanish", "German", "French", "Italian"])
		active(validator:{ val, obj ->
				println "custom validator for active $val, ${obj.name}, ${obj.twitterToken.username} "
				println "custom validator for active $val, ${obj.name}, ${obj.twitterToken.username}"
				
				if(val == true){
					def count = TwitterUser.findAllByAvailableAndUsername(true,obj.twitterToken.username).size()
					println count
				
					if (count == 0) {
						return ['no_token_available']
					}
				}								
        })
        twitterToken(nullable: false
			/*, validator: { val, obj ->
				println "custom validator for $val, $obj"
				def count = TwitterUser.findAllByAvailableAndUsername(true,val.username).size()
				println count
				
				if (count > 1) {
					return ['Sorry, but a user can have only one scope enable at a time.']
				}
				
				return true				
        }*/
		)
        lastUpdated(nullable: true)
    }

    def beforeInsert() {
        log.info "creating scope ${name}"
    }

//    def afterInsert(){
//        log.info "start scope manager service"
//
//        //scopeManagerService.start(this)
//    }
}
