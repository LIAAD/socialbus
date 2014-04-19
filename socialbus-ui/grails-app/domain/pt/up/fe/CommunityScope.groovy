package pt.up.fe

import org.springframework.context.ApplicationEvent

class CommunityScope extends Scope {

    String users

    static constraints = {
        users(widget: 'textarea', blank: false)
    }
	
    static mapping = {
          users type: "text"
      }

    void afterUpdate() {
        def event = new CommunityScopeUpdateEvent(this, "update")
        publishEvent event
    }

    void afterInsert() {
        def event = new CommunityScopeUpdateEvent(this, "insert")
        publishEvent event
    }

    void afterDelete() {
        def event = new CommunityScopeUpdateEvent(this, "delete")
        publishEvent event
    }

    class CommunityScopeUpdateEvent extends ApplicationEvent {

        String updateAction

        CommunityScopeUpdateEvent(CommunityScope source, String action) {
            super(source)
            this.updateAction = action
        }
    }
}
