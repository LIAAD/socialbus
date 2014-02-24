<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title><g:layoutTitle default="${meta(name: 'app.name')}"/></title>
    <meta name="description" content="">
    <meta name="author" content="">

    <meta name="viewport" content="initial-scale = 1.0">

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
			<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

    <r:require modules="scaffolding"/>

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
	<link rel="stylesheet" href="${resource(dir:'css',file:'twitter-auth.css')}" />

    <g:layoutHead/>
    <r:layoutResources/>
    
	
<!-- 	<style>
		.twitter-login .twitter-button {
			background: none !important;
			width: 151px;
			height: 45px;
			border: 0 none;
			display: inline-block;
			background-color: gray;
			color: white;
		}
	</style> -->
	
	
</head>

<body>

<div class="container-fluid">
    <g:layoutBody/>

    <hr>

</div>

<r:layoutResources/>

</body>
</html>