<%@ page import="pt.up.fe.CommunityScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'communityScope.label', default: 'CommunityScope')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="row-fluid">

    <div class="span3">
        <div class="well">
            <ul class="nav nav-list">
                <li class="nav-header">${entityName}</li>
                <li class="active">
                    <g:link class="list" action="list">
                        <i class="icon-list icon-white"></i>
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
            <h1><g:message code="default.list.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <table class="table table-striped">
            <thead>
            <tr>

                <g:sortableColumn property="name"
                                  title="${message(code: 'communityScope.name.label', default: 'Name')}"/>

                <g:sortableColumn property="language"
                                  title="${message(code: 'communityScope.language.label', default: 'Language')}"/>

                <g:sortableColumn property="lastUpdated"
                                  title="${message(code: 'communityScope.lastUpdated.label', default: 'Last Updated')}"/>

                <g:sortableColumn property="users"
                                  title="${message(code: 'communityScope.users.label', default: 'Users')}"/>

                <g:sortableColumn property="active"
                                  title="${message(code: 'communityScope.active.label', default: 'Active')}"/>

                <g:sortableColumn property="dateCreated"
                                  title="${message(code: 'communityScope.dateCreated.label', default: 'Date Created')}"/>

                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${communityScopeInstanceList}" var="communityScopeInstance">
                <tr>

                    <td>${fieldValue(bean: communityScopeInstance, field: "name")}</td>

                    <td>${fieldValue(bean: communityScopeInstance, field: "language")}</td>

                    <td><g:formatDate date="${communityScopeInstance.lastUpdated}"/></td>

                    <td>${fieldValue(bean: communityScopeInstance, field: "users")}</td>

                    <td><g:formatBoolean boolean="${communityScopeInstance.active}"/></td>

                    <td><g:formatDate date="${communityScopeInstance.dateCreated}"/></td>

                    <td class="link">
                        <g:link action="show" id="${communityScopeInstance.id}"
                                class="btn btn-small">Show &raquo;</g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <div class="pagination">
            <bootstrap:paginate total="${communityScopeInstanceTotal}"/>
        </div>
    </div>

</div>
</body>
</html>
