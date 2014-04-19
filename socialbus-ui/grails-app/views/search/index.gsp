<%@ page import="pt.up.fe.TopicScope; pt.up.fe.CommunityScope" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="bootstrap"/>
    <title>SocialBus</title>
	
	<link REL=StyleSheet type="text/css" href="${resource(dir: 'css/twitter', file: 'card_embed.css')}" />
	<link REL=StyleSheet type="text/css" href="${resource(dir: 'css', file: 'trendlines.css')}" />
</head>

<body>
<div class="row-fluid">
	<g:set var="haveQuery" value="${params.q?.trim()}" />
    <g:set var="haveResults" value="${results}" />
   
   
    <div class="page-header">
        <h1>search
		</h1>	
    </div>

    <g:form style="0px 0px 0px 0px" url='[controller:'search',action: "index"]' class="form-search" id="searchForm" name="searchForm" method="GET" >
        <input type="text" name="q" value="${params.q}" id="searchBox" class="input-xlarge search-query">
		</input>
		<button type="submit" class="btn btn-large ">Go!</button>
    </g:form>		

    <g:if test="${haveQuery && !haveResults}">
      <h3>Nothing matched your query - <strong>${params.q}</strong></h3>
    </g:if>
	<g:else>
		<h3  style="margin-top: 0px;" >${numFound} found results for <strong>${params.q}</strong></h3>
	</g:else>		

	<div class="row-fluid"> 
        <g:if test="${haveResults}">          
			
	  		<div class="span3 bs-docs-sidebar">
	  			<ul class="nav nav-list bs-docs-sidenav">

	  			<g:render template="sidebar"/>
	  			</ul>
	  		</div>

			
			<div class="span8"> 
				<div class="results">

	            <g:each var="status" in="${results}">
	             <g:render template="status_card" model="['status':status]" />
	            </g:each>

	          </div>

	          <div class="pagination">
	            <bootstrap:paginate total="${numFound?:0}" params="${params}"/>
	          </div>
			  
	  		<div >
	  			<div id="trendline-tooltip" class="tip">.</div>
	  			<svg id="g-trendline" width="365" height="150"></svg>
	  		</div>
		</div>	  
        </g:if> 
      </div>

</div>
 <script src="http://d3js.org/d3.v2.js"></script>
 <!-- <g:javascript src="trendline.js"/> -->

 <script>
  $("#searchBox").focus();

  $(document).ready(function() {
    configureTrendline();
  });

  var trends = trendJson = JSON.parse('${trendlineJson}');

  /**
   * Trendline visualization
   */
  function configureTrendline() {
	  
  var settings = {
    animatinoDuration: 1000,
    eventTextHeight: 0,
    eventIconSize: 0,
    eventMargin: 2,
    markerColor: "#444",
    fillColor: "#FECEA8",
    strokeColor: "#E5BA94",
    strokeWidth: 2
  };

  function drawTrendline(svg, width, height, granularity, trends, options) {
    trends.forEach(function(trend) {
      if (typeof(trend.timestamp) == "string") {
        trend.timestamp = new Date(trend.timestamp);
      }
    });

    var xMin = d3.first(trends).timestamp;
    var xMax = d3.last(trends).timestamp;
    var yMin = 0;
    var yMax = d3.max(trends, function(d) { return d.value; });
    var xScale = d3.time.scale().domain([xMin, xMax]).range([0, width]);
    var yScale = d3.scale.linear().domain([yMin, yMax]).range([0, height-settings.eventTextHeight-settings.eventIconSize-2*settings.eventMargin]);
    var innerSvg = svg.append("svg:g").attr("class", "trendline").attr("transform", "translate(0,"+height+")");


    // Events
    innerSvg.selectAll(".event").remove();

    var prevEventX = 0;
    var prevEventY = 0;

    trends.forEach(function(trend) {
      var x = xScale(trend.timestamp);
      var y;

      if (trend.event) {
        y = x-prevEventX < settings.eventIconSize ? prevEventY-settings.eventIconSize : height;

        var eventSvg = innerSvg.append("svg:g")
        .attr("class", "event up "+trend.event.type)
        .attr("data-timestamp", trend.timestamp);

        eventSvg.append("svg:line")
        .attr("x1", x)
        .attr("x2", x)
        .attr("y1", 0)
        .attr("y2", -(y-settings.eventTextHeight-settings.eventIconSize-2*settings.eventMargin));

        eventSvg.append("svg:image")
        .attr("x", x-settings.eventIconSize/2)
        .attr("y", -(y-settings.eventTextHeight-settings.eventMargin))
        .attr("width", settings.eventIconSize+"px")
        .attr("height", settings.eventIconSize+"px")
        .attr("xlink:href", "img/trendline-event-icons/"+trend.event.type+".png")
        .on("mouseover", function() {
          highlightEvent(d3.event.target.parentNode);
        })
        .on("mouseout", function() {
          highlightEvent();
        })
        .on("click", function() {
          if (trend.event.type === "Match") {
            switch (currentPage.type) {
              case "home":
              window.location.hash = "home&match="+trend.event.id;
              break;
              case "team":
              case "player":
              window.location.hash = currentPage.type+"="+currentPage.id+"&match="+trend.event.id;
              break;
            }
          }
        });

        eventSvg.append("svg:text")
        .attr("x", x)
        .attr("y", -height)
        .attr("text-anchor", "middle")
        .attr("alignment-baseline", "baseline")
        .text(trend.event.text);

        prevEventX = x;
        prevEventY = y;
      }

    });

    // Adjust text x position to be always visible
    innerSvg.selectAll("text")[0].forEach(function(text) {
      var bbox = text.getBBox();
      if (bbox.x < 0) {
        d3.select(text).attr("x", bbox.width/2);
      } else if (bbox.x + bbox.width > width) {
        d3.select(text).attr("x", width-bbox.width/2);
      }
    });

    //// Generators
    var areaGenerator = d3.svg.area()
    .x(function(d) { return xScale(d.timestamp); })
    .y1(function(d) { return -yScale(d.value); })
    .interpolate("linear");

    var lineGenerator = d3.svg.line()
    .x(function(d) { return xScale(d.timestamp); })
    .y(function(d) { return -yScale(d.value); })
    .interpolate("linear");

    var hitAreaGenerator = d3.svg.area()
    .x(function(d) { return xScale(d.timestamp); })
    .y1(function(d) { return -yScale(d.value)-20; })
    .interpolate("linear");

    //// Area/line
    var area = innerSvg.selectAll("path.area").data([trends]);
    updatePath(area, areaGenerator);

    var line = innerSvg.selectAll("path.line").data([trends]);
    updatePath(line, lineGenerator);

    var hitArea = innerSvg.selectAll("path.hit-area").data([trends]);
    updatePath(area, hitAreaGenerator);

    // Enter
    var newArea = area.enter()
    .append("svg:path")
    .attr("class", "area")
    .attr("fill", settings.fillColor);
    updatePath(newArea, areaGenerator);

    var newLine = line.enter()
    .append("svg:path")
    .attr("class", "line")
    .attr("fill", "transparent")
    .attr("stroke", settings.strokeColor)
    .attr("stroke-width", settings.strokeWidth);
    updatePath(newLine, lineGenerator);

    var marker = innerSvg.append("svg:circle")
    .attr("class", "marker")
    .attr("r", "4")
    .attr("fill", settings.markerColor);

    var newHitArea = hitArea.enter()
    .append("svg:path")
    .attr("class", "hit-area")
    .attr("fill", "transparent");
    updatePath(newHitArea, hitAreaGenerator);

    // Exit
    area.exit().remove(); // TODO check this
    line.exit().remove(); // TODO check this
    hitArea.exit().remove(); // TODO check this


    // Interactivity
    var trendsDict = {};
    trends.forEach(function(trend) {
      var timestamp = getDateRoundedToGranularity(trend.timestamp, granularity);
      trendsDict[timestamp] = trend;
    });

    var timeFormat = d3.time.format("%e %b %H:%M")
    var maxTrend = trends.reduce(function(prevTrend, currentTrend) {
      return !prevTrend || currentTrend.value > prevTrend.value ? currentTrend : prevTrend;
    });

    function setMarker(timestamp) {
      var value = trendsDict[timestamp].value,
          tooltipText = value+" comentários/hora, "+timeFormat(timestamp);
      marker.attr("cx", xScale(timestamp)).attr("cy", -yScale(value));
      d3.select("#trendline-tooltip").html(tooltipText);
      highlightEvent(".event[data-timestamp='"+timestamp+"']");
    }


    newHitArea
    .on("mouseout", function() {
      setMarker(maxTrend.timestamp);
    })
    .on("mousemove", function() {
      var timestamp = getDateRoundedToGranularity(xScale.invert(d3.mouse(d3.event.target)[0]), granularity);
      setMarker(timestamp);
    });

    setMarker(maxTrend.timestamp);
  }

  function updatePath(area, generator) {
    area.transition()
    .duration(settings.animatinoDuration)
    .attr("d", generator);
  }

  function highlightEvent(selector) {
    d3.selectAll(".event").classed("highlighted", false);
    if (selector) {
      d3.select(selector).classed("highlighted", true);
    }
  }

  function getDateRoundedToGranularity(date, granularity) {
    return new Date(Math.round(date.getTime()/granularity)*granularity);
  }

  drawTrendline(d3.select("#g-trendline"), 512, 80, 3600e3, trends);

  };

  // function configureTrendline() {
  // 		  
  // 		  
  // 		  
  // 		  
  // 		  
  //   var width = 450;
  //   var height = 180;
  //   var trendlineSvg = d3.select("#g-trendline").append("svg:svg").attr("width", width).attr("height", height);
  //   var svgPrimitives = new Trendlines();
  // 
  //   // $.getJSON("${resource(dir: 'js', file: 'trends.json')}", {}, function(json) {
  //     
  //     trendJson = JSON.parse('${trendlineJson}');
  //     svgPrimitives.drawTrendline(trendlineSvg, width, height, 60e3, trendJson);
  //   //});
  // }

</script>
</body>
</html>
