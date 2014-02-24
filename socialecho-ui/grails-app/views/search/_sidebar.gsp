<li ><legend>Date range</legend></li>

<li class="${params.date_range.equals('1HOUR') ? print ('active') : '' }">
	<g:link params="${params + [date_range:'1HOUR']}">
	Last 1 hour
	</g:link>
</li>			 			
			 
<li class="${params.date_range.equals('24HOUR') ? print ('active') : '' }">
	<g:link params="${params + [date_range:'24HOUR']}">
	Last 24 hours
	</g:link>
</li>
<li class="${params.date_range.equals('7DAY') ? print ('active') : '' }">
	<g:link params="${params + [date_range:'7DAY']}">
	Last 7 days
	</g:link>
</li>
<li class="${params.date_range.equals('30DAY') ? print ('active') : '' }">
	<g:link params="${params + [date_range:'30DAY']}">
	Last 30 days
	</g:link>
</li>
<li class="${params.date_range.equals('all_times') ? print ('active') : '' }">
	<g:link params="${params + [date_range:'all_times']}">
	All Time
	</g:link>
</li>
			  
<li ><legend>Languages</legend></li>
<li class="${params.lang.equals('all') ? print ('active') : '' }">
	<g:link params="${params + [lang:'all']}">
	All
	</g:link>
</li>
<li class="${params.lang.equals('en') ? print ('active') : '' }">
	<g:link params="${params + [lang:'en']}">
	English
	</g:link>
</li>
<li class="${params.lang.equals('pt') ? print ('active') : '' }">
	<g:link params="${params + [lang:'pt']}">
	Português
	</g:link>
</li>
<li class="${params.lang.equals('es') ? print ('active') : '' }">
	<g:link params="${params + [lang:'es']}">
	Español
	</g:link>
</li>
			  
<!-- <li ><legend>Most active users</legend></li>
<li class="${params.screen_name.equals('all') ? print ('active') : '' }">
	<g:link params="${params + [screen_name:'all']}">
	All
	</g:link>
</li>

<g:each var="facetUser" in="${facetUsers}">				  
	<li class="${params.screen_name.equals(facetUser.name) ? print ('active') : '' }">
		<g:link params="${params + [screen_name:facetUser.name]}">
		${facetUser.name} (${facetUser.count})
		</g:link>					  
	</li>				  
</g:each> -->