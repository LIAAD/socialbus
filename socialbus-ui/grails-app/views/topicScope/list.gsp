<%@ page import="pt.up.fe.TopicScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap">
    <g:set var="entityName" value="${message(code: 'topicScope.label', default: 'TopicScope')}"/>
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

                <g:sortableColumn property="name" title="${message(code: 'topicScope.name.label', default: 'Name')}"/>

                <g:sortableColumn property="language"
                                  title="${message(code: 'topicScope.language.label', default: 'Language')}"/>

                <g:sortableColumn property="lastUpdated"
                                  title="${message(code: 'topicScope.lastUpdated.label', default: 'Last Updated')}"/>

                <g:sortableColumn property="topics"
                                  title="${message(code: 'topicScope.topics.label', default: 'Topics')}"/>

                <g:sortableColumn property="active"
                                  title="${message(code: 'topicScope.active.label', default: 'Active')}"/>

                <g:sortableColumn property="dateCreated"
                                  title="${message(code: 'topicScope.dateCreated.label', default: 'Date Created')}"/>

                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${topicScopeInstanceList}" var="topicScopeInstance">
                <tr>

                    <td>${fieldValue(bean: topicScopeInstance, field: "name")}</td>

                    <td>${fieldValue(bean: topicScopeInstance, field: "language")}</td>

                    <td><g:formatDate date="${topicScopeInstance.lastUpdated}"/></td>

                    <td>${fieldValue(bean: topicScopeInstance, field: "topics")}</td>

                    <td><g:formatBoolean boolean="${topicScopeInstance.active}"/></td>

                    <td><g:formatDate date="${topicScopeInstance.dateCreated}"/></td>

                    <td class="link">
                        <g:link action="show" id="${topicScopeInstance.id}" class="btn btn-small">Show &raquo;</g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <div class="pagination">
            <bootstrap:paginate total="${topicScopeInstanceTotal}"/>
        </div>
    </div>

</div>
</body>
</html>
