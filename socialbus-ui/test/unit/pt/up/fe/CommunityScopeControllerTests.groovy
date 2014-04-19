package pt.up.fe

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@TestFor(CommunityScopeController)
@Mock(CommunityScope)
class CommunityScopeControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/communityScope/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.communityScopeInstanceList.size() == 0
        assert model.communityScopeInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.communityScopeInstance != null
    }

    void testSave() {
        controller.save()

        assert model.communityScopeInstance != null
        assert view == '/communityScope/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/communityScope/show/1'
        assert controller.flash.message != null
        assert CommunityScope.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/communityScope/list'

        populateValidParams(params)
        def communityScope = new CommunityScope(params)

        assert communityScope.save() != null

        params.id = communityScope.id

        def model = controller.show()

        assert model.communityScopeInstance == communityScope
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/communityScope/list'

        populateValidParams(params)
        def communityScope = new CommunityScope(params)

        assert communityScope.save() != null

        params.id = communityScope.id

        def model = controller.edit()

        assert model.communityScopeInstance == communityScope
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/communityScope/list'

        response.reset()

        populateValidParams(params)
        def communityScope = new CommunityScope(params)

        assert communityScope.save() != null

        // test invalid parameters in update
        params.id = communityScope.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/communityScope/edit"
        assert model.communityScopeInstance != null

        communityScope.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/communityScope/show/$communityScope.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        communityScope.clearErrors()

        populateValidParams(params)
        params.id = communityScope.id
        params.version = -1
        controller.update()

        assert view == "/communityScope/edit"
        assert model.communityScopeInstance != null
        assert model.communityScopeInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/communityScope/list'

        response.reset()

        populateValidParams(params)
        def communityScope = new CommunityScope(params)

        assert communityScope.save() != null
        assert CommunityScope.count() == 1

        params.id = communityScope.id

        controller.delete()

        assert CommunityScope.count() == 0
        assert CommunityScope.get(communityScope.id) == null
        assert response.redirectedUrl == '/communityScope/list'
    }
}
