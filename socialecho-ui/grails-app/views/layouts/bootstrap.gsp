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

    <g:layoutHead/>
    <r:layoutResources/>
    %{--<r:require modules="twitterAuth"/>--}%
</head>

<body>

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a class="brand" href="#">socialecho</a>

            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active"><g:link controller="home">Home</g:link></li>
                    <li><g:link controller="scopeDashboard">Scopes</g:link></li>
                    <li><g:link controller="search">Search</g:link></li>
                </ul>
                %{--<form class="navbar-search pull-left" action="" novalidate="novalidate">--}%
                %{--<input type="text" class="search-query span2" placeholder="Search">--}%
                %{--</form>--}%
                <ul class="nav pull-right">

                    <sec:ifLoggedIn>

                        <li><g:link uri="#">hi <sec:loggedInUserInfo
                                field="username"/>!</g:link></li>

                        <!-- <sec:ifAnyGranted roles="ROLE_ADMIN">
                            <li><g:link controller="systemConfig">Config</g:link></li>
                        </sec:ifAnyGranted> -->
                        <li><g:link uri="/j_spring_security_logout">Logout</g:link></li>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                    %{--<twitterAuth:button />--}%
                        <li><g:link controller="login" action="auth">Login</g:link></li>
                    </sec:ifNotLoggedIn>

                </ul>
            </div><!-- /.nav-collapse -->
        </div>
    </div><!-- /navbar-inner -->
</div>




%{--<nav class="navbar navbar-fixed-top">--}%
%{--<div class="navbar-inner">--}%
%{--<div class="container-fluid">--}%
%{----}%
%{--<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">--}%
%{--<span class="icon-bar"></span>--}%
%{--<span class="icon-bar"></span>--}%
%{--<span class="icon-bar"></span>--}%
%{--</a>--}%
%{----}%
%{--<a class="brand" href="${createLink(uri: '/')}">TwitterEcho</a>--}%

%{--<div class="nav-collapse">--}%
%{--<ul class="nav">							--}%
%{--<li<%= request.forwardURI == "${createLink(uri: '/')}" ? ' class="active"' : '' %>><a href="${createLink(uri: '/')}">Home</a></li>--}%
%{--<g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">--}%
%{--<li<%= c.logicalPropertyName == controllerName ? ' class="active"' : '' %>><g:link controller="${c.logicalPropertyName}">${c.naturalName}</g:link></li>--}%
%{--</g:each>--}%
%{--</ul>--}%
%{--</div>--}%
%{--</div>--}%
%{--</div>--}%
%{--</nav>--}%

<div class="container-fluid">
    <g:layoutBody/>

    <hr>

</div>

<!-- Footer
    ================================================== -->
<g:render template="../templates/footer"/>

<r:layoutResources/>

</body>

%{--<g:javascript>--}%

%{--$("#tags").tagsInput({--}%
%{--'height':'100px',--}%
%{--'width':'300px',--}%
%{--'interactive':true,--}%
%{--'defaultText':'add a keyword',--}%
%{--'removeWithBackspace' : true,--}%
%{--'minChars' : 2,--}%
%{--'maxChars' : 0 //if not provided there is no limit,--}%
%{--'placeholderColor' : '#666666'--}%
%{--});--}%

%{--</g:javascript>--}%
</html>