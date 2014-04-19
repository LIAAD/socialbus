package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityUtils

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import grails.plugin.springsecurity.annotation.Secured;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.response.FacetField;

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.*

/*@Secured(['ROLE_ADMIN', 'ROLE_USER','ROLE_TWITTER'])*/
class MapsController {

    def static dbConnection

    def grailsApplication
	
	def searchService
	
    def index = {
		println "maps search controller"
		def query = params.q
        
        if (!params.q?.trim()) {
			//redirect(controller: "home", action: "index")
			return "retorna vazio"
           return [:]
/*          query =  "*:*"*/
        }
		
        try {
			def queryResp = searchService.mapsSearch(query, params)
			def queryTime = queryResp.getQTime()
			
			def docs = queryResp.getResults();
			def numFound = docs.getNumFound() 
			
			println "numFound : $numFound"
			
            return [results:docs,
					numFound:numFound,
					queryTime:queryTime]
					
        } catch (Exception ex) {
            return [parseException: true]
        }
	}
}
