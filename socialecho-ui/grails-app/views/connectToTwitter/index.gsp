<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <title>Connect to Twitter</title>
	<r:require modules="twitterAuth"/>
</head>

<body>
<div class="row-fluid">

    <div class="span9">

        <div class="page-header">
            <h1>connect to Twitter</h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <h3>To track messages you need to provide a Twitter account. Login at Twitter and authorize this application.</h3>
		
        <ul class="thumbnails">
            <li class="span3 tile tile-double tile-blue">

				<twitterAuth:button/>
            </li>
        </ul>

    </div>

</div>
</body>
</html>
