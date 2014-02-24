package pt.up.fe

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@TestFor(TopicScopeController)
@Mock(TopicScope)
class TopicScopeControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/topicScope/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.topicScopeInstanceList.size() == 0
        assert model.topicScopeInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.topicScopeInstance != null
    }

    void testSave() {
        controller.save()

        assert model.topicScopeInstance != null
        assert view == '/topicScope/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/topicScope/show/1'
        assert controller.flash.message != null
        assert TopicScope.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/topicScope/list'

        populateValidParams(params)
        def topicScope = new TopicScope(params)

        assert topicScope.save() != null

        params.id = topicScope.id

        def model = controller.show()

        assert model.topicScopeInstance == topicScope
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/topicScope/list'

        populateValidParams(params)
        def topicScope = new TopicScope(params)

        assert topicScope.save() != null

        params.id = topicScope.id

        def model = controller.edit()

        assert model.topicScopeInstance == topicScope
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/topicScope/list'

        response.reset()

        populateValidParams(params)
        def topicScope = new TopicScope(params)

        assert topicScope.save() != null

        // test invalid parameters in update
        params.id = topicScope.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/topicScope/edit"
        assert model.topicScopeInstance != null

        topicScope.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/topicScope/show/$topicScope.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        topicScope.clearErrors()

        populateValidParams(params)
        params.id = topicScope.id
        params.version = -1
        controller.update()

        assert view == "/topicScope/edit"
        assert model.topicScopeInstance != null
        assert model.topicScopeInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/topicScope/list'

        response.reset()

        populateValidParams(params)
        def topicScope = new TopicScope(params)

        assert topicScope.save() != null
        assert TopicScope.count() == 1

        params.id = topicScope.id

        controller.delete()

        assert TopicScope.count() == 0
        assert TopicScope.get(topicScope.id) == null
        assert response.redirectedUrl == '/topicScope/list'
    }
}
