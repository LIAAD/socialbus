package pt.up.fe

import grails.converters.JSON
import org.springframework.context.ApplicationListener

class TopicScopeUpdateResponderService implements ApplicationListener<TopicScope.TopicScopeUpdateEvent> {

    StreamManagerService  streamManagerService

    void onApplicationEvent(TopicScope.TopicScopeUpdateEvent event) {
        println "Yarrr! The scope $event.source.name has received ${event.updateAction} action ! "

        println new JSON(event.source).toString()

        switch (event.updateAction){
            case "insert" :
                streamManagerService.startStream(event.source)
                break;

            case "delete" :
                streamManagerService.stopStream(event.source)
                break;

            case "update" :
                if(event.source.active){
                    streamManagerService.restartStream(event.source)
                }else{
                    streamManagerService.stopStream(event.source)
                }

                break;

        }
    }
}
