<%@ page import="pt.up.fe.CommunityScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'communityScope.label', default: 'CommunityScope')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="row-fluid">

    <div class="span3">
        <div class="well">
            <ul class="nav nav-list">
                <li class="nav-header">${entityName}</li>
                <li>
                    <g:link class="list" action="list">
                        <i class="icon-list"></i>
                        <g:message code="default.list.label" args="[entityName]"/>
                    </g:link>
                </li>
                <li>
                    <g:link class="create" action="create">
                        <i class="icon-plus"></i>
                        <g:message code="default.create.label" args="[entityName]"/>
                    </g:link>
                </li>
            </ul>
        </div>
    </div>

    <div class="span9">

        <div class="page-header">
            <h1><g:message code="default.show.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <dl>

            <g:if test="${communityScopeInstance?.name}">
                <dt><g:message code="communityScope.name.label" default="Name"/></dt>

                <dd><g:fieldValue bean="${communityScopeInstance}" field="name"/></dd>

            </g:if>

            <g:if test="${communityScopeInstance?.language}">
                <dt><g:message code="communityScope.language.label" default="Language"/></dt>

                <dd><g:fieldValue bean="${communityScopeInstance}" field="language"/></dd>

            </g:if>

            <g:if test="${communityScopeInstance?.lastUpdated}">
                <dt><g:message code="communityScope.lastUpdated.label" default="Last Updated"/></dt>

                <dd><g:formatDate date="${communityScopeInstance?.lastUpdated}"/></dd>

            </g:if>

            <g:if test="${communityScopeInstance?.users}">
                <dt><g:message code="communityScope.users.label" default="Users"/></dt>

                <dd><g:fieldValue bean="${communityScopeInstance}" field="users"/></dd>

            </g:if>

            <g:if test="${communityScopeInstance?.active}">
                <dt><g:message code="communityScope.active.label" default="Active"/></dt>

                <dd><g:formatBoolean boolean="${communityScopeInstance?.active}"/></dd>

            </g:if>

            <g:if test="${communityScopeInstance?.dateCreated}">
                <dt><g:message code="communityScope.dateCreated.label" default="Date Created"/></dt>

                <dd><g:formatDate date="${communityScopeInstance?.dateCreated}"/></dd>

            </g:if>

        </dl>

        <g:form>
            <g:hiddenField name="id" value="${communityScopeInstance?.id}"/>
            <div class="form-actions">
                <g:link class="btn" action="edit" id="${communityScopeInstance?.id}">
                    <i class="icon-pencil"></i>
                    <g:message code="default.button.edit.label" default="Edit"/>
                </g:link>
                <button class="btn btn-danger" type="submit" name="_action_delete">
                    <i class="icon-trash icon-white"></i>
                    <g:message code="default.button.delete.label" default="Delete"/>
                </button>
            </div>
        </g:form>

    </div>

</div>
</body>
</html>
