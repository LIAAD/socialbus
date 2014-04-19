package pt.up.fe

class TwitterToken {

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
//    static belongsTo = [scope: Scope]


	boolean available = true

    static constraints = {
        token(unique: true)
        twitterId(nullable: true)
        username(nullable: true)
    }
	
    def beforeInsert() {
        log.info "creating token ${token}"
    }
}
