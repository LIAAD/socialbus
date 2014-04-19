<%@ page import="pt.up.fe.TopicScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'topicScope.label', default: 'TopicScope')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<div class="row-fluid">

    <div class="span9">

        <div class="page-header">
            <h1><g:message code="default.create.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:hasErrors bean="${topicScopeInstance}">
            <bootstrap:alert class="alert-error">

                <ul>
                    <g:eachError bean="${topicScopeInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </bootstrap:alert>
        </g:hasErrors>

        <fieldset>
            <g:form class="form-horizontal" action="create">
                <fieldset>
                    <f:all bean="topicScopeInstance" except="twitterToken,active"/>
					<div class="control-group ">
					    <div class="controls">
					        <small>* topics must be comma separated</small>
					    </div>
					</div>
					
					
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="icon-ok icon-white"></i>
                            <g:message code="default.button.create.label" default="Create"/>
                        </button>

                        <g:link controller="scopeDashboard" class="btn ">
                            <g:message code="default.button.cancel.label" default="Cancel"/>
                        </g:link>

                    </div>
                </fieldset>
            </g:form>
        </fieldset>

    </div>

</div>
</body>
</html>
