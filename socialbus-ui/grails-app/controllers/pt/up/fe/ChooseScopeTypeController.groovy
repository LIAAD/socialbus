package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured
/*import org.scribe.model.Token*/

@Secured(['ROLE_TWITTER'])
class ChooseScopeTypeController {

    def oauthService

    def index() {

       /* Token tAT = (Token) session[oauthService.findSessionKeyForAccessToken('twitter')]

        if (tAT) {
            def resourceURL = "https://api.twitter.com/1.1/account/settings.json"


            TwitterToken twitterToken = TwitterToken.findByToken(tAT.getToken())

            if (twitterToken) {
                Scope scope = Scope.findByTwitterToken(twitterToken)

                if (scope) {
                    flash.message = "This token is already been used by scope '${scope.name}'. Please provide a new one."


                    redirect controller: "connectToTwitter"
                    return
                }

            } else {
                twitterToken = new TwitterToken();
                twitterToken.token = tAT.getToken()
                twitterToken.tokenSecret = tAT.getSecret()

                twitterToken.save()
            }

            print twitterToken

            return ["token": twitterToken]

        }*/
	   
	   /*TODO criar um pool de tokens */
	   
	   /*return ["token": twitterToken]	   */
	   
/*	   return []*/
    }
}
