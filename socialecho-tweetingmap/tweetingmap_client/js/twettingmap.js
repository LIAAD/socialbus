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

var SERVER_URL = "http://dry-island-6625.herokuapp.com/";

var MAP_LAT_TOP = 78.5;
var MAP_LAT_BOTTOM = -78.5;
var MAP_LON_LEFT = -180.0;
var MAP_LON_RIGHT = 180.0;
var MAP_WIDTH = 1024;
var MAP_HEIGHT = 748;

var paper;
var fixTweets = false;
var mercatorMap = new MercatorMap(MAP_WIDTH, MAP_HEIGHT, MAP_LAT_TOP, MAP_LAT_BOTTOM, MAP_LON_LEFT, MAP_LON_RIGHT);

console.log("connect to " + SERVER_URL);

var socket = io.connect(SERVER_URL);

socket.on('data', function (data) {
  var text = data['text'];
  var url = 'http://twitter.com/account/redirect_by_id?id=' + data['user_id'];
  var latitude = data['coordinates'][1];
  var longitude = data['coordinates'][0];
  var weight = weightForFollowers(data['followers_count']);
  var color = data['color'];
  
  if (latitude == 0 && longitude == 0) return;
  
  appendToConsole(data);
  drawTweet(text, url, latitude, longitude, weight, color);
});

function weightForFollowers(followers_count) {
  return Math.log(Math.sqrt(followers_count/20));
}

function cleanConsole(){
	$('#console').empty();
}

setInterval(cleanConsole,60000);

function appendToConsole(tweet){
    
	var _console = $('#console');
	
    var consoleText = tweet['user_name'] + " : " + tweet['text'];

    var spanText = $( "<span/>", {
        text: consoleText,
        class : "text"
    });

    spanText.innerHTML = consoleText;

    spanText.appendTo(_console);
    $("<br/>", {}).appendTo( _console );

    _console.scrollTop(
            _console[0].scrollHeight - _console.height()
    );
}

function drawTweet(text, url, lat, lon, weight, color) {
  var point = mercatorMap.getScreenLocation(lat, lon);
  var x = point[0];
  var y = point[1];
  
  var circle = paper.circle(x, y, 0);
  circle.attr("fill", "#"+color);
  circle.attr("stroke", "none");
  
  
  
  $(circle.node).attr("title", text).tooltip().bind('mouseenter', function() {
    circle.pause();
  }).bind('mouseleave', function() {
    circle.resume();
  });
  
  if (!window.navigator.standalone) {
    $(circle.node).attr("title", text).tooltip().bind('click', function() {
      window.open(url, '_blank');
      return false;
    });
  };
  
  if (fixTweets) {
    circle.attr("r", weight*5);
    circle.attr("fill-opacity", 0.4);
  } else {
    var anim = Raphael.animation({r: weight*14, opacity: 0.0}, weight*2000, "easeOut", function() {
      circle.remove();
    });
    circle.animate(anim);
  };
}

$(function(){
  paper = Raphael("map", MAP_WIDTH, MAP_HEIGHT);
  
  /*
    Settings panel
    */
  var settingsTimeout;
  
  $('#settings').bind('mouseenter', function() {
    clearTimeout(settingsTimeout);
    $("#settings-content").show();
  }).bind('mouseleave', function() {
    settingsTimeout = setTimeout(function() {
      $("#settings-content").hide();
    }, 1000);
  });
  
  $('#settings-button').bind('click', function() {
    clearTimeout(settingsTimeout);
    $("#settings-content").toggle();
    return false;
  });
    
  $('#mode-switch .btn').bind('click', function() {
    if ($(this).hasClass('fixed') && fixTweets || $(this).hasClass('volatile') && !fixTweets) return;
    fixTweets = !fixTweets;
    paper.forEach(function(el) { el.stop() }).clear();
    $('#mode-switch').children().removeClass('active');
    $(this).addClass('active');
  });
  
  $(document).bind('click', function() {
    clearTimeout(settingsTimeout);
    $("#settings-content").hide();
  });
  
  $("#settings-content").bind('click', function() {
    return false; // prevent the settings-content to hide on clicking over itself
  });
  
  /*
    Override the tooltip plugin to fix the position
    */
  var tooltipExtensions = {
    getPosition: function(inside) {
      var diameter = parseFloat(this.$element.attr('r')*2);
      return $.extend({}, (inside ? {top: 0, left: 0} : this.$element.offset()), {
        width: diameter
      , height: diameter
      })
    }
  };
  $.extend($.fn.tooltip.Constructor.prototype, tooltipExtensions);
  
});
