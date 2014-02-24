package pt.up.fe

import org.springframework.context.ApplicationEvent

class TopicScope extends Scope {

    String topics

    static constraints = {
        topics(widget: 'textarea', blank: false)
    }

    def beforeInsert() {
        if (topics) {

            def topicsList = topics.split(",")

            log.debug "creating scope to monitor these keywords:"
            for (topic in topicsList) {
                log.debug topic
            }
        }
    }

    void afterUpdate() {
        def event = new TopicScopeUpdateEvent(this, "update")
        publishEvent event
    }

    void afterInsert() {
        def event = new TopicScopeUpdateEvent(this, "insert")
        publishEvent event
    }

    void afterDelete() {
        def event = new TopicScopeUpdateEvent(this, "delete")
        publishEvent event
    }

    class TopicScopeUpdateEvent extends ApplicationEvent {

        String updateAction

        TopicScopeUpdateEvent(TopicScope source, String action) {
            super(source)
            this.updateAction = action
        }
    }


}
