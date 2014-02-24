package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured
/*import org.scribe.model.Token*/
import org.springframework.dao.DataIntegrityViolationException

class CommunityScopeController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
    def oauthService

    //    TODO refatorar a forma como pega o token da sessao
    TwitterToken getSessionToken() {
        println "getSessionToken"
        /*Token tAT = (Token) session[oauthService.findSessionKeyForAccessToken('twitter')]

        if (tAT) {
            TwitterToken twitterToken = TwitterToken.findByAvailable(tAT.getToken())

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
		print "get available token"
		print "total of twitterTokens : ${TwitterToken.count()}" 
		
        TwitterToken twitterToken = TwitterToken.findByAvailable(true)
		
        return twitterToken
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def index() {
        redirect action: 'list', params: params
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [communityScopeInstanceList: CommunityScope.list(params), communityScopeInstanceTotal: CommunityScope.count()]
    }

    @Secured(['ROLE_ADMIN'])
    def create() {
        switch (request.method) {
            case 'GET':
                def communityScopeInstance = new CommunityScope(params)

/*                TwitterToken twitterToken = TwitterToken.get(Integer.parseInt(params.token))*/
/*                communityScopeInstance.twitterToken = twitterToken*/
                [communityScopeInstance: communityScopeInstance]

                break
            case 'POST':
                def communityScopeInstance = new CommunityScope(params)

                TwitterToken twitterToken = getSessionToken()
				
				if(twitterToken == null){
					flash.message = "Sorry, but you can't create another scope right now. There is a finite number of tokens and you have not a single available one right now. You can make one available by deactivating some Scope you no longer use. Or try to contact your sysadmin"
					redirect controller: 'scopeDashboard'
					break
				}
				
                communityScopeInstance.twitterToken = twitterToken


                if (!communityScopeInstance.save(flush: true)) {
                    render view: 'create', model: [communityScopeInstance: communityScopeInstance]
                    return
                }

				twitterToken.available = false
				twitterToken.save()
				
                flash.message = message(code: 'default.created.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), communityScopeInstance.id])
//	        redirect action: 'show', id: communityScopeInstance.id


                redirect controller: 'scopeDashboard'
                break
        }
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def show() {
        def communityScopeInstance = CommunityScope.get(params.id)
        if (!communityScopeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), params.id])
            redirect action: 'list'
            return
        }

        [communityScopeInstance: communityScopeInstance]
    }

    @Secured(['ROLE_ADMIN'])
    def edit() {
        switch (request.method) {
            case 'GET':
                def communityScopeInstance = CommunityScope.get(params.id)
                if (!communityScopeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), params.id])
                    redirect action: 'list'
                    return
                }

                [communityScopeInstance: communityScopeInstance]
                break
            case 'POST':
                def communityScopeInstance = CommunityScope.get(params.id)
                if (!communityScopeInstance) {
                    flash.message = message(code: 'default.not.found.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (communityScopeInstance.version > version) {
                        communityScopeInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
                                [message(code: 'communityScope.label', default: 'CommunityScope')] as Object[],
                                "Another user has updated this CommunityScope while you were editing")
                        render view: 'edit', model: [communityScopeInstance: communityScopeInstance]
                        return
                    }
                }

                communityScopeInstance.properties = params

                if (!communityScopeInstance.save(flush: true)) {
                    render view: 'edit', model: [communityScopeInstance: communityScopeInstance]
                    return
                }
				
				if(communityScopeInstance.active){
					communityScopeInstance.twitterToken.available = false
				}else{
					communityScopeInstance.twitterToken.available = true
				}
				
				communityScopeInstance.twitterToken.save()

                flash.message = message(code: 'default.updated.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), communityScopeInstance.name])
                redirect action: 'show', id: communityScopeInstance.id
                break
        }
    }

    @Secured(['ROLE_ADMIN'])
    def delete() {
        def communityScopeInstance = CommunityScope.get(params.id)
        if (!communityScopeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), params.id])
            redirect action: 'list'
            return
        }

        try {

			TwitterToken twitterToken = communityScopeInstance.twitterToken
			
            communityScopeInstance.delete(flush: true)
			
			twitterToken.available = true
			twitterToken.save()
			
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'communityScope.label', default: 'CommunityScope'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
