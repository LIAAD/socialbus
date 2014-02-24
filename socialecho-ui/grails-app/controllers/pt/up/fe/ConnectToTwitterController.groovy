package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class ConnectToTwitterController {

    def oauthService
    def grailsApplication
    def springSecurityService

    def index() {

    }


}