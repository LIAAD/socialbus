import pt.up.fe.Role
import pt.up.fe.User
import pt.up.fe.TwitterToken
import pt.up.fe.UserRole

class BootStrap {

    def init = { servletContext ->
		
		/*if(TwitterToken.count() == 0){
			new TwitterToken(token:"1547962976-p95l3xWhaJ4GMKcp7YsqXnH2VcpThF1rkfUgM04",tokenSecret:"j5AE8KJSQJdbjR7stj2mIewxSAEwqXlzR5AVq6AgMM").save(failOnError: true,flush:true)
			new TwitterToken(token:"1547973426-eDXEzMWXtKs30tTlV6SL6yn8sAD9dKUPelRABcV",tokenSecret:"2q3Jq7PndE0278b6YQKNIZ6zoutqa1aeVPaPAgWgow").save(failOnError: true,flush:true)
			new TwitterToken(token:"1548022729-v22PpsKLvtutIyPDtDULHb6JhQzACoglxOWWf9b",tokenSecret:"iM488s3T49WP8YUX8RMl95Pa0zhA3CD2nRugRBdzFA").save(failOnError: true,flush:true)
		}*/
				
        if (Role.count() == 0) {
            initSecurityData()
        }
    }

    def initSecurityData() {


        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = new Role(authority: 'ROLE_USER').save(failOnError: true, flush: true)
        def twitterRole = new Role(authority: 'ROLE_TWITTER').save(failOnError: true, flush: true)

        def adminUser = new User(username: 'reaction', enabled: true, password: 'reaction@123')
        adminUser.save(flush: true)
		
        def guestUser = new User(username: 'guest', enabled: true, password: 'guest@123')
        guestUser.save(flush: true)

        def marcelaUser = new User(username: 'marcela', enabled: true, password: 'marcela_pass')
        marcelaUser.save(flush: true)
		
        def jorgeUser = new User(username: 'jorge', enabled: true, password: 'jorge_pass')
        jorgeUser.save(flush: true)
		
        /*def arianUser = new User(username: 'arianpasquali', enabled: true, password: 'ariari')
        arianUser.save(flush: true)*/
		
		UserRole.create guestUser, userRole, true
        UserRole.create adminUser, adminRole, true
        UserRole.create marcelaUser, adminRole, true
		UserRole.create jorgeUser, adminRole, true
/*		UserRole.create arianUser, adminRole, true*/

/*        assert User.count() == 5*/
/*        assert Role.count() == 3*/
/*        assert UserRole.count() == 5*/
    }

    def destroy = {
    }
}
