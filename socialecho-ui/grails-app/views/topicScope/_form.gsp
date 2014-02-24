<%@ page import="pt.up.fe.TopicScope" %>



<div class="fieldcontain ${hasErrors(bean: topicScopeInstance, field: 'name', 'error')} required">
    <label for="name">
        <g:message code="topicScope.name.label" default="Name"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="name" required="" value="${topicScopeInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: topicScopeInstance, field: 'language', 'error')} ">
    <label for="language">
        <g:message code="topicScope.language.label" default="Language"/>

    </label>
    <g:select name="language" from="${topicScopeInstance.constraints.language.inList}"
              value="${topicScopeInstance?.language}" valueMessagePrefix="topicScope.language" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: topicScopeInstance, field: 'topics', 'error')} ">
    <label for="topics">
        <g:message code="topicScope.topics.label" default="Topics"/>

    </label>
    %{--<g:textArea name="topics" cols="40" rows="5" value="${topicScopeInstance?.topics}"/>--}%
    <input name="topics" id="tags" value="${topicScopeInstance?.topics}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: topicScopeInstance, field: 'active', 'error')} ">
    <label for="active">
        <g:message code="topicScope.active.label" default="Active"/>

    </label>
    <g:checkBox name="active" value="${topicScopeInstance?.active}"/>
</div>

