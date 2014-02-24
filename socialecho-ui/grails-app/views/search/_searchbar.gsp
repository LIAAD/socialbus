<div class="page-header" style="
margin-bottom: -155px;
padding-bottom: 0px;
margin: 20px 0 30px;
border-bottom: 0px solid #EEE;

">
        <g:if test="${!haveQuery}">
          <h2>What are you looking for?</h2>
        </g:if>
        <g:if test="${haveQuery}">
          <h2>Search results for tweets</h2>
        </g:if>  
      
        <g:form url='[controller:'tweetSearch',action: "index"]' id="searchableForm" 
          name="searchableForm" method="get" >

          <div class="input-append">
            <g:textField name="q" value="${params.q}" 
            class="span3" id="searchBox" type="text"/>
            <button class="btn" type="submit">Go!</button>
          </div>

        </g:form>
		
		<g:if test="${haveQuery}">

			<div class="result-stats" >
			About ${numFound} results (${queryTime} seconds)
			</div>

			<div style="
			position: relative;
			top: -96px;
			left: 380px;">
				<div id="trendline-tooltip" class="tip">.</div>
				<svg id="g-trendline" width="365" height="150"></svg>
			</div>
		</g:if>	
		
		<!-- <hr/> -->
</div>  