<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <title>Define scope</title>
</head>

<body>
<div class="row-fluid">

    <div class="span9">

        <div class="page-header">
            <h1>Choose scope type</h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <h3>Define how do you want to track messages</h3>
        <ul class="thumbnails">
            <!--  controller="communityScope" action="create" params="[token: token.id]" -->
			    <!-- controller="topicScope" action="create" params="['token': token.id]" -->
			
            <li class="span3 tile tile-double tile-teal">
        
				<g:link controller="topicScope" action="create">
                    <h1 class="tile-text">by topics</h1>
                </g:link>
            </li>
			
			<li class="span3 tile tile-double tile-orange disabled">
				<g:link>
                    <h1 class="tile-text">by users</h1>
                </g:link>
            </li>
		    
            <!-- <li class="span3 tile tile-double tile-blue disabled">
                <g:link>
                    <h1 class="tile-text">by location</h1>
                </g:link>
            </li> -->
        </ul>

    </div>

</div>
</body>
</html>
