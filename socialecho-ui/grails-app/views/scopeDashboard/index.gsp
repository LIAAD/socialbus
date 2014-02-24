<%@ page import="pt.up.fe.TopicScope; pt.up.fe.CommunityScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap"/>
    <title>SocialEcho</title>
</head>

<body>
<div class="row-fluid">
    %{--<section id="main" class="span9">--}%

    <div class="page-header">
        <h1>scopes</h1>
    </div>

    <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>

    <p>
        <!-- <g:link class="btn btn btn-large" controller="connectToTwitter">
            Define new scope
        </g:link> -->
		
        <g:link class="btn btn btn-large" controller="chooseScopeType">
            Define new scope
        </g:link>
    </p>

    <g:if test="${pt.up.fe.Scope.findAllByActive(true).size() > 0}">

        <h3>Active scopes</h3>

        <ul class="thumbnails">
            <g:each in="${pt.up.fe.Scope.findAllByActive(true)}" var="scopeInstance">

                <%
                    def scope_type = scopeInstance.getClass()
                    def tile_style = "tile-teal"
                    def detailLink = ""

                    if (scope_type == CommunityScope.class) {
                        tile_style = "tile-orange"
                        detailLink = createLink(controller: "communityScope", action: "show", id: scopeInstance.id)
                    } else if (scope_type == TopicScope.class) {
                        tile_style = "tile-teal"
                        detailLink = createLink(controller: "topicScope", action: "show", id: scopeInstance.id)
                    } else {
                        tile_style = "tile-blue"
                        detailLink = createLink(controller: "communityScope", action: "show", id: scopeInstance.id)
                    }

                %>

                <li class="span3 tile tile-double ${tile_style}">
                    <g:link url="${detailLink}">
                        <h2 class="tile-text">${fieldValue(bean: scopeInstance, field: "name")}</h2>

                    </g:link>

                </li>

            </g:each>

        </ul>

        <hr/>
    </g:if>

    <g:if test="${pt.up.fe.Scope.findAllByActive(false).size() > 0}">

        <h3>Inactive scopes</h3>

        <ul class="thumbnails">
            <g:each in="${pt.up.fe.Scope.findAllByActive(false)}" var="scopeInstance">
                <%
                    def scope_type = scopeInstance.getClass()
                    def tile_style = "tile-teal"
                    def detailLink = ""

                    if (scope_type == CommunityScope.class) {
                        detailLink = createLink(controller: "communityScope", action: "show", id: scopeInstance.id)
                    } else if (scope_type == TopicScope.class) {
                        detailLink = createLink(controller: "topicScope", action: "show", id: scopeInstance.id)
                    } else {
                        detailLink = createLink(controller: "communityScope", action: "show", id: scopeInstance.id)
                    }

                %>

                <li class="span3 tile tile-double">
                    <g:link url="${detailLink}">
                        <h2 class="tile-text">${fieldValue(bean: scopeInstance, field: "name")}</h2>
                    </g:link>
                </li>

            </g:each>

        </ul>
    </g:if>


%{--</section>--}%
</div>

<hr/>

<div class="">

</div>

</body>
</html>
