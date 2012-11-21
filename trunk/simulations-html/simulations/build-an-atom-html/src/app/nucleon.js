define([
  'point2d'
], function( Point2D ){

  var nucleonRadius = 20;

  //-----------------------------------------------------------------------------
  // Nucleon class.
  //-----------------------------------------------------------------------------

  function Nucleon( color ) {
    this.location = new Point2D( 0, 0 );
    this.radius = nucleonRadius;
    this.color = color;
  }

  Nucleon.prototype.draw = function( context ){
      var xPos = this.location.x;
      var yPos = this.location.y;
      var gradient = context.createRadialGradient( xPos - this.radius / 3, yPos - this.radius / 3, 0, xPos, yPos, this.radius );
      gradient.addColorStop( 0, "white" );
      gradient.addColorStop( 1, this.color );
      context.fillStyle = gradient;
      context.beginPath();
      context.arc( xPos, yPos, this.radius, 0, Math.PI * 2, true );
      context.closePath();
      context.fill();
  };

  Nucleon.prototype.setLocation = function ( location ) {
      this.setLocationComponents( location.x, location.y );
  };

  Nucleon.prototype.setLocationComponents = function ( x, y ) {
      this.location.x = x;
      this.location.y = y;
  };

  Nucleon.prototype.containsPoint = function ( point ) {
      return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
  };

  return Nucleon;

});
