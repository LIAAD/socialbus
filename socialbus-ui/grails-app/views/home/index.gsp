<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap"/>
    <title>Define scope</title>
</head>

<body>
<div class="row-fluid">
    %{--<section id="main" class="span9">--}%

    <div class="hero-unit">
        <h1>socialecho</h1>

        <p>
            Monitoring messages on social networks.
            SocialBus enables you to continuously collect data from particular user communities, topics or locations.
            Start now defining a new monitoring scope.
        </p>

        <p>
            <g:link class="btn btn-info btn-large" controller="scopeDashboard">
                show scopes
            </g:link>

            <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_TWITTER">
                or
                <!-- <g:link class="btn btn-primary btn-large" controller="connectToTwitter">
                    define a new one
                </g:link> -->
					
                <g:link class="btn btn-primary btn-large" controller="chooseScopeType">
                    define new scope
                </g:link>	
            </sec:ifAnyGranted>

        </p>
    </div>

    <div class="row-fluid">

        <div class="span6">
            <h3>popular users</h3>

            <p>Most mentioned users</p>

			<ul style="list-style-type: none;">
				<g:each in="${trendingUsers}" var="userInstance">
					<a href="http://twitter.com/${userInstance._id.screen_name}" target="_blank"><li>@${userInstance._id.screen_name}</li></a>
				</g:each>
			</ul>

            <!-- <p>
                <br/>
                <g:link controller="trends" action="users">View more »</g:link>
            </p> -->

        </div>

        <div class="span6">
            <h3>trending topics</h3>

            <p>Most mentioned hashtags</p>
			
			<ul style="list-style-type: none;">
				<g:each in="${trendingTopics}" var="topicInstance">
					<a href="https://twitter.com/search?q=${topicInstance._id.hashtag}" target="_blank"><li>${topicInstance._id.hashtag}</li></a>
				</g:each>
			</ul>
            <!-- <div id="topicsBox">
                loading...
            </div> -->


            <!-- <p>
                <br/>
                <g:link controller="trends" action="topics">View more »</g:link>
            </p> -->

        </div>

        <!-- <div class="span4">
            <h2>Popular websites</h2>

            <p>Most mentioned websites on twitter</p>

            <div id="websitesBox">
                loading...
            </div>


            <p>
                <br/>
                <g:link controller="trends" action="websites">View more »</g:link>
            </p>
        </div> -->

    </div>

    %{--</section>--}%
</div>

</body>
</html>
