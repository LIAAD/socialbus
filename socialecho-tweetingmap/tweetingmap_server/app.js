// Copyright (c) 2012 Nuno Baldaia
// 
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

// Socket.IO
var port = parseInt(process.env.PORT || 3000);

var io = require('socket.io').listen(port, function() {
  console.log("Listening on " + port);
});

var amqp = require('amqp');


console.log("Starting ... AMQP ");
var conn = amqp.createConnection({
	host: 'localhost'
	, port: 5672
	, login: 'guest'
	, password: 'guest'
	, connectionTimeout: 0
 },{defaultExchangeName: "socialecho-1"});

conn.addListener('error', function (e) {
	console.log("error");
    throw e;
});
 
conn.addListener('close', function (e) {
  console.log('connection closed.');
});
 
 
conn.addListener('ready', function () {
  console.log("connected to " + conn.serverProperties.product);
});
 

// Force long polling and prevent the use of WebSockets for Heroku.
// http://devcenter.heroku.com/articles/using-socket-io-with-node-js-on-heroku
// io.configure(function () { 
//   io.set("transports", ["xhr-polling"]); 
//   io.set("polling duration", 10); 
// });


// Stream Twitter to clients with Socket.IO
var twitter = require('ntwitter');
var twit = new twitter({
  consumer_key: 'nzlQLLYqzeBUFT9wP49jqE0z4',
  consumer_secret: 'Ox9hLXIDSSYxJdlGy9Sjl1rstQh2hlb2lwdrVSLntomqpPfBiF',
  access_token_key: '2432370937-VFoo8HzLSdtMHUCoukww156f9oPeLGexkLvP1pE',
  access_token_secret: 'oP2KXNn99IP7xwZZ4vbxWW9wjC5T8bnCgVFhTFbW95PTz'
});

twit.stream('statuses/filter', {'locations':'-180,-90,180,90'}, function(stream) {
  stream.on('data', function (data) {
    // console.log(data);
    if (data['coordinates'] != null && data['coordinates']['type'] == 'Point' && data['coordinates']['coordinates'] != [0, 0]) {
      var custom_data = {
        'text': data['text'],
        'id': data['id_str'],
        'user_id': data['user']['id_str'],
        'coordinates': data['coordinates']['coordinates'],
        'color': data['user']['profile_background_color'],
        'followers_count': data['user']['followers_count']
      }
	  // console.log(custom_data["text"]);
      io.sockets.emit('data', custom_data);
    };
  });
});
