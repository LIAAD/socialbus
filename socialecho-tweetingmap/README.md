# Tweeting Map

Tweeting Map is an experimental near-realtime visualization of public geo-localized tweets. See it [in action](http://tweetingmap.tomatique.com/).

It is composed by two components: <code>tweetingmap\_server</code> and <code>tweetingmap\_client</code>.


## tweetingmap_server

This component establishes a persistent connection with the [Twitter Streaming API](https://dev.twitter.com/docs/streaming-api) to feed the client component with data from public geo-localized tweets in realtime.

It is built on the [Node.js](http://nodejs.org/) platform and depends on:

- [ntwitter](https://github.com/AvianFlu/ntwitter) — asynchronous connection to the Twitter Streaming API
- [Socket.IO](http://socket.io/) — realtime communication with the client component


## tweetingmap_client

This is a pure HTML5, CSS3 and JavaScript interface that depends on the following libraries:

- [Socket.IO](http://socket.io/)
- [jQuery](http://jquery.com/)
- [Bootstrap, from Twitter](http://twitter.github.com/bootstrap/)
- [Raphaël](http://raphaeljs.com/)
- [Add to home screen](http://cubiq.org/add-to-home-screen)
- [mercator-map](https://github.com/nunobaldaia/mercatormap)

