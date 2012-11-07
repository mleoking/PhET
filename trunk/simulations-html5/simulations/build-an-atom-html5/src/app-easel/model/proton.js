define([
  'underscore',
  'model/particle',
  'model/point2D'
],function( _, Particle, Point2D ){

  function Proton( x, y ){
    this.initialize( "red" );

    this.radius = 5;

    var xPos = x || 0;
    var yPos = y || 0;

    this.position = new Point2D( xPos, yPos );
  }

  _.extend( Proton.prototype, Particle.prototype );

  return Proton;

});
