// Copyright 2002-2012, University of Colorado
define([
], function( ){

    /**
     * @param x
     * @param y
     * @param color
     * @param radius
     * @param type
     * @constructor
     */
  function Particle( x, y, color, radius, type ){
      this.x = x;
      this.y = y;
      this.color = color;
      this.radius = radius;
      this.type = type;
  }

  return Particle;
});
