<%@ page import="pt.up.fe.TopicScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'topicScope.label', default: 'TopicScope')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="row-fluid">

    <div class="span9">

        <div class="page-header">
            <h1><g:message code="default.show.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>

            <g:if test="${topicScopeInstance?.name}">
                <dt><g:message code="topicScope.name.label" default="Name"/></dt>

                <dd><g:fieldValue bean="${topicScopeInstance}" field="name"/></dd>

            </g:if>

            <g:if test="${topicScopeInstance?.language}">
                <dt><g:message code="topicScope.language.label" default="Language"/></dt>

                <dd><g:fieldValue bean="${topicScopeInstance}" field="language"/></dd>

            </g:if>

            <g:if test="${topicScopeInstance?.lastUpdated}">
                <dt><g:message code="topicScope.lastUpdated.label" default="Last Updated"/></dt>

                <dd><g:formatDate date="${topicScopeInstance?.lastUpdated}"/></dd>

            </g:if>

            <g:if test="${topicScopeInstance?.topics}">
                <dt><g:message code="topicScope.topics.label" default="Topics"/></dt>

                <dd><g:fieldValue bean="${topicScopeInstance}" field="topics"/></dd>

            </g:if>

            <g:if test="${topicScopeInstance?.active}">
                <dt><g:message code="topicScope.active.label" default="Active"/></dt>

                <dd><g:formatBoolean boolean="${topicScopeInstance?.active}"/></dd>

            </g:if>

            <g:if test="${topicScopeInstance?.dateCreated}">
                <dt><g:message code="topicScope.dateCreated.label" default="Date Created"/></dt>

                <dd><g:formatDate date="${topicScopeInstance?.dateCreated}"/></dd>

            </g:if>

        </dl>

        <g:form>
            <g:hiddenField name="id" value="${topicScopeInstance?.id}"/>
            <div class="form-actions">
                <g:link controller="search" class="btn btn-primary" params="[q:topicScopeInstance.topics]">
                    Show tweets
                </g:link>

                <span style="text-align: right">
                    <g:link class="btn" action="edit" id="${topicScopeInstance?.id}">
                        <i class="icon-pencil"></i>
                        <g:message code="default.button.edit.label" default="Edit"/>
                    </g:link>
                    <button class="btn btn-danger" type="submit" name="_action_delete">
                        <i class="icon-trash icon-white"></i>
                        <g:message code="default.button.delete.label" default="Delete"/>
                    </button>
                </span>

            </div>
        </g:form>

    </div>

</div>
</body>
</html>
