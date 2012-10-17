define([], function(){

  function ElectronShell( initialLocation ) {
      this.location = initialLocation;
      this.radius = 120;
  }

  ElectronShell.prototype.draw = function ( context ) {
      var xPos = this.location.x;
      var yPos = this.location.y;
      var gradient = context.createRadialGradient( xPos, yPos, 0, xPos, yPos, this.radius );
      gradient.addColorStop( 0, "rgba( 0, 0, 200, 0.2)" );
      gradient.addColorStop( 1, "rgba( 0, 0, 200, 0.05)" );
      context.fillStyle = gradient;
      context.beginPath();
      context.arc( xPos, yPos, this.radius, 0, Math.PI * 2, true );
      context.closePath();
      context.fill();
  }

  ElectronShell.prototype.setLocationComponents = function ( x, y ) {
      this.location.x = x;
      this.location.y = y;
  }

  ElectronShell.prototype.setLocation = function ( location ) {
      this.setLocationComponents( location.x, location.y );
  }

  ElectronShell.prototype.containsPoint = function ( point ) {
      return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
  }

  return ElectronShell;
});
