<%@ page import="pt.up.fe.TopicScope; pt.up.fe.CommunityScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap"/>
    <title>SocialBus</title>
	
	<link REL=StyleSheet type="text/css" href="${resource(dir: 'css/twitter', file: 'card_embed.css')}" />
	<link REL=StyleSheet type="text/css" href="${resource(dir: 'css', file: 'trendlines.css')}" />
</head>

<body data-spy="scroll">
<div class="row-fluid">
	<g:set var="haveQuery" value="${params.q?.trim()}" />
    <g:set var="haveResults" value="${results}" />
   
   
    <div class="page-header">
        <h1>maps search
		</h1>	
    </div>

    <g:form style="0px 0px 0px 0px" url='[controller:'maps',action: "index"]' class="form-search" id="searchForm" name="searchForm" method="GET" >
        <input type="text" name="q" value="${params.q}" id="searchBox" class="input-xlarge search-query">
		</input>
		<button type="submit" class="btn btn-large ">Go!</button>
    </g:form>		

    <g:if test="${haveQuery && !haveResults}">
      <h3>Nothing matched your query - <strong>${params.q}</strong></h3>
    </g:if>
	<g:else>
		<h3  style="margin-top: 0px;" >${numFound} found results for <strong>${params.q}</strong></h3>
	</g:else>		

	<div class="row-fluid"> 
        <g:if test="${haveResults}">          
			
	  		<div class="span12"> 
				<div class="results">

	            <g:each var="status" in="${results}">
				${status.text}
				${status.geo_location}
				-----
	             <!-- <render template="status_card" model="['status':status]" /> -->
	            </g:each>

	          </div>

	          <!-- <div class="pagination">
	            <bootstrap:paginate total="${numFound?:0}" params="${params}"/>
	          </div> -->
			  
	  		<div >
	  			<div id="trendline-tooltip" class="tip">.</div>
	  			<svg id="g-trendline" width="365" height="150"></svg>
	  		</div>
		</div>	  
        </g:if> 
      </div>

</div>


 <script>
  $("#searchBox").focus();

  $(document).ready(function() {
  });

</script>
</body>
</html>
