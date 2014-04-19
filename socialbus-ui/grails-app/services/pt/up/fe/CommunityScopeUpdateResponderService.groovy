package pt.up.fe

import org.springframework.context.ApplicationListener

class CommunityScopeUpdateResponderService implements ApplicationListener<TopicScope.TopicScopeUpdateEvent> {

    void onApplicationEvent(TopicScope.TopicScopeUpdateEvent event) {
        println "Yarrr! The scope $event.source.name has received ${event.updateAction} action ! "
    }
}
