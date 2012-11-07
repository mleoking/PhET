define([
  'underscore'
], function( _ ){

  function Particle(){
    this.initialize.apply(this, arguments);
  }

  Particle.prototype.initialize = function( color ){
    if( color === undefined ){
      throw new Error('You must supply a color property');
    }

    this.color = color;
  };

  return Particle;

});
