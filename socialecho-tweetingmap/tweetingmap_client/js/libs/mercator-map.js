// Copyright (c) 2012 Nuno Baldaia
// Copyright (c) 2011 Till Nagel, tillnagel.com
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
// 
// 
// Mercator Map
// 
// This is an adaptation to JavaScript of the original Java utility class MercatorMap by Till Nagel.
// 
// Utility class to convert between geo-locations and Cartesian screen coordinates.
// Can be used with a bounding box defining the map section.
// 
// http://tillnagel.com/2011/06/tilemill-for-processing/
// http://tillnagel.com/wp-content/uploads/2011/06/MercatorMap.java
// http://wiki.openstreetmap.org/wiki/Mercator

/**
 * Creates a new MercatorMap with dimensions and bounding box to convert between geo-locations and screen coordinates.
 *
 * @param mapScreenWidth Horizontal dimension of this map, in pixels.
 * @param mapScreenHeight Vertical dimension of this map, in pixels.
 * @param topLatitude Northern border of this map, in degrees.
 * @param bottomLatitude Southern border of this map, in degrees.
 * @param leftLongitude Western border of this map, in degrees.
 * @param rightLongitude Eastern border of this map, in degrees.
 */
function MercatorMap(mapScreenWidth, mapScreenHeight, topLatitude, bottomLatitude, leftLongitude, rightLongitude) {
  this.mapScreenWidth = mapScreenWidth;
  this.mapScreenHeight = mapScreenHeight;
  this.topLatitudeRelative = this.getScreenYRelative(topLatitude);
  this.bottomLatitudeRelative = this.getScreenYRelative(bottomLatitude);
  this.leftLongitudeRadians = this.getRadians(leftLongitude);
  this.rightLongitudeRadians = this.getRadians(rightLongitude);
}

/**
 * Projects the geo location to Cartesian coordinates, using the Mercator projection.
 *
 * @param latitudeInDegrees: latitude in degrees.
 * @param longitudeInDegrees: longitude in degrees.
 * @returns The screen coordinates with [x, y].
 */
MercatorMap.prototype.getScreenLocation = function(latitudeInDegrees, longitudeInDegrees) {
  return [this.getScreenX(longitudeInDegrees), this.getScreenY(latitudeInDegrees)];
}

MercatorMap.prototype.getScreenY = function(latitudeInDegrees) {
  return this.mapScreenHeight*(this.getScreenYRelative(latitudeInDegrees) - this.topLatitudeRelative)/(this.bottomLatitudeRelative - this.topLatitudeRelative);
}

MercatorMap.prototype.getScreenX = function(longitudeInDegrees) {
  var longitudeInRadians = this.getRadians(longitudeInDegrees);
  return this.mapScreenWidth*(longitudeInRadians - this.leftLongitudeRadians)/(this.rightLongitudeRadians - this.leftLongitudeRadians);
}

MercatorMap.prototype.getScreenYRelative = function(latitudeInDegrees) {
  return Math.log(Math.tan(latitudeInDegrees/360.0*Math.PI + Math.PI/4));
}

MercatorMap.prototype.getRadians = function(deg) {
  return deg*Math.PI/180;
}
