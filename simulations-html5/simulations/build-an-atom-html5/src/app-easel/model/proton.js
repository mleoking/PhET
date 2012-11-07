define([
  'underscore',
  'model/particle',
  'model/point2D'
],function( _, Particle, Point2D ){

  function Proton(){
    this.initialize( "red" );

    this.radius = 50;

    this.position = new Point2D( 0, 0 );
  }

  _.extend( Proton.prototype, Particle.prototype );

  return Proton;

});
