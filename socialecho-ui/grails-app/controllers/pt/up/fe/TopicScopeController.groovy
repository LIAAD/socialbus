package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured
/*import org.scribe.model.Token*/
import org.springframework.dao.DataIntegrityViolationException

class TopicScopeController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
/*    def oauthService*/
	
	def springSecurityService
	def twitterAuthService

    @Secured(['ROLE_ADMIN', 'ROLE_USER','ROLE_TWITTER'])
    def index() {
		
        redirect action: 'list', params: params
    }

    //    TODO refatorar a forma como pega o token da sessao
    TwitterUser getTwitterUserToken() {
        println "getSessionToken"
		
		def user = springSecurityService.currentUser
		println user.getClass()
		println user.username
		
		TwitterUser twitterUser = TwitterUser.findByUsername(user.username)
		println twitterUser.token
		println twitterUser.tokenSecret
		
		return twitterUser;
		/*def principalUser = twitterAuthService.getAppUser(user)
		println principalUser*/
		
        /*Token tAT = (Token) session[oauthService.findSessionKeyForAccessToken('twitter')]

        if (tAT) {
            TwitterToken twitterToken = TwitterToken.findByToken(tAT.getToken())

            if (!twitterToken) {
                twitterToken = new TwitterToken();
                twitterToken.token = tAT.getToken()
                twitterToken.tokenSecret = tAT.getSecret()

                twitterToken.save()
            }

            print twitterToken

            return twitterToken

        } else {

            println "twitter session token not found"
        }*/
		    
		/*find available token	*/
		/*println "get available token"
		println "total of twitterTokens : ${TwitterToken.count()}" 
		
        TwitterToken twitterToken = TwitterToken.findByAvailable(true)
		println
        return twitterToken*/
    }


    @Secured(['ROLE_ADMIN', 'ROLE_USER','ROLE_TWITTER'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [topicScopeInstanceList: TopicScope.list(params), topicScopeInstanceTotal: TopicScope.count()]
    }

    @Secured(["ROLE_TWITTER"])
    def create() {
        println(params)

        switch (request.method) {

            case 'GET':
                def topicScopeInstance = new TopicScope(params)
                [topicScopeInstance: topicScopeInstance]

                break

            case 'POST':
                def topicScopeInstance = new TopicScope(params)

                TwitterUser twitterToken = getTwitterUserToken()
				
				if(twitterToken == null){
					flash.message = "Sorry, but you can't create another scope right now. There is a finite number of tokens and you have not a single available one right now. You can make one available by deactivating some Scope you no longer use. Or try to contact your sysadmin"
					redirect controller: 'scopeDashboard'
					break
				}
				
				topicScopeInstance.twitterToken = twitterToken

                if (!topicScopeInstance.save(flush: true)) {
					
                    [topicScopeInstance: topicScopeInstance]
                    render view: 'create', model: [topicScopeInstance: topicScopeInstance]
                    return
                }
				
				twitterToken.available = false
				twitterToken.save()

                flash.message = message(code: 'default.created.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), topicScopeInstance.name])
//	        redirect action: 'show', id: topicScopeInstance.id
                redirect controller: 'scopeDashboard'
                break
        }
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER',"ROLE_TWITTER"])
    def show() {
        def topicScopeInstance = TopicScope.get(params.id)
        if (!topicScopeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), params.id])
            redirect action: 'list'
            return
        }

        [topicScopeInstance: topicScopeInstance]
    }

    @Secured(['ROLE_ADMIN',"ROLE_TWITTER"])
    def edit() {
        switch (request.method) {
            case 'GET':
                def topicScopeInstance = TopicScope.get(params.id)
                if (!topicScopeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), params.id])
                    redirect action: 'list'
                    return
                }

                [topicScopeInstance: topicScopeInstance]
                break
            case 'POST':
                def topicScopeInstance = TopicScope.get(params.id)
                if (!topicScopeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (topicScopeInstance.version > version) {
                        topicScopeInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'topicScope.label', default: 'TopicScope')] as Object[],
                                "Another user has updated this TopicScope while you were editing")
                        render view: 'edit', model: [topicScopeInstance: topicScopeInstance]
                        return
                    }
                }

                topicScopeInstance.properties = params

                if (!topicScopeInstance.save(flush: true)) {
                    render view: 'edit', model: [topicScopeInstance: topicScopeInstance]
                    return
                }

				if(topicScopeInstance.active){
					topicScopeInstance.twitterToken.available = false
				}else{
					topicScopeInstance.twitterToken.available = true
				}
				
				topicScopeInstance.twitterToken.save()
			

                flash.message = message(code: 'default.updated.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), topicScopeInstance.id])
                redirect action: 'show', id: topicScopeInstance.id
                break
        }
    }

    @Secured(['ROLE_ADMIN',"ROLE_TWITTER"])
    def delete() {
        def topicScopeInstance = TopicScope.get(params.id)
		
		
		
        if (!topicScopeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), params.id])
            redirect action: 'list'
            return
        }

        try {
			
			TwitterUser twitterToken = topicScopeInstance.twitterToken
			topicScopeInstance.delete(flush: true)
			
			twitterToken.available = true
			twitterToken.save()
			
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'topicScope.label', default: 'TopicScope'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
