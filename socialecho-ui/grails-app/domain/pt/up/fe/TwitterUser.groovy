package pt.up.fe

class TwitterUser {

    /**
     * Twitter Username (notice that it could be modified by user, Twitter allows that)
     */
    String username

    /**
     * Twitter User Id
     */
    Long twitterId

    /**
     * Twitter API token
     */
    String token

    /**
     * Twitter API secret
     */
    String tokenSecret

    /**
     * Related to main App User
     */
    static belongsTo = [user: User]
	
	boolean available = true

    static constraints = {
        twitterId(unique: true, nullable: false)
        username(nullable: false, blank: false)
    }
	
    def beforeInsert() {
        log.info "creating TwitterUser with username ${username}"
		log.info "creating TwitterUser with token ${token}"
    }
}
