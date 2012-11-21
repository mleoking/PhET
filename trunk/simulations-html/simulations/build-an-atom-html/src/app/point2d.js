define([], function(){

  //-----------------------------------------------------------------------------
  // Point2D class.
  //-----------------------------------------------------------------------------

  function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
  }

  Point2D.prototype.toString = function () {
    return this.x + ", " + this.y;
  };

  Point2D.prototype.setComponents = function ( x, y ) {
    this.x = x;
    this.y = y;
  };

  Point2D.prototype.set = function ( point2D ) {
    this.setComponents( point2D.x, point2D.y );
  };

  Point2D.prototype.equals = function ( point2D ) {
    return (point2D.x == this.x) && (point2D.y == this.y);
  };

  return Point2D;

});
