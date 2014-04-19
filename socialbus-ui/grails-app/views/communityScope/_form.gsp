<%@ page import="pt.up.fe.CommunityScope" %>



<div class="fieldcontain ${hasErrors(bean: communityScopeInstance, field: 'name', 'error')} required">
    <label for="name">
        <g:message code="communityScope.name.label" default="Name"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="name" required="" value="${communityScopeInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: communityScopeInstance, field: 'language', 'error')} ">
    <label for="language">
        <g:message code="communityScope.language.label" default="Language"/>

    </label>
    <g:select name="language" from="${communityScopeInstance.constraints.language.inList}"
              value="${communityScopeInstance?.language}" valueMessagePrefix="communityScope.language"
              noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: communityScopeInstance, field: 'users', 'error')} ">
    <label for="users">
        <g:message code="communityScope.users.label" default="Users"/>

    </label>
    <g:textArea name="users" cols="40" rows="5" value="${communityScopeInstance?.users}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: communityScopeInstance, field: 'active', 'error')} ">
    <label for="active">
        <g:message code="communityScope.active.label" default="Active"/>

    </label>
    <g:checkBox name="active" value="${communityScopeInstance?.active}"/>
</div>

