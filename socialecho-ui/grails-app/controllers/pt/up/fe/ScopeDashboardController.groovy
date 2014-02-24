package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN', 'ROLE_USER'])
class ScopeDashboardController {
	
   
    def index() {}
}
