define([],function(){

  //-----------------------------------------------------------------------------
  // Reset button class
  //-----------------------------------------------------------------------------

  function ResetButton( initialLocation, color, reset ) {
      this.location = initialLocation;
      this.width = 90;
      this.height = 40;
      this.color = color;
      this.pressed = false;
      this.reset = reset;
  }

  ResetButton.prototype.draw = function ( context ) {
      var xPos = this.location.x;
      var yPos = this.location.y;
      var gradient = context.createLinearGradient( xPos, yPos, xPos, yPos + this.height );
      if ( !this.pressed ) {
          gradient.addColorStop( 0, "white" );
          gradient.addColorStop( 1, this.color );
      }
      else {
          gradient.addColorStop( 0, this.color );
      }
      // Draw box that defines button outline.
      context.strokeStyle = '#222'; // Gray
      context.lineWidth = 1;
      context.strokeRect( xPos, yPos, this.width, this.height );
      context.fillStyle = gradient;
      context.fillRect( xPos, yPos, this.width, this.height );
      // Put text on the box.
      context.fillStyle = '#000';
      context.font = '28px sans-serif';
      context.textBaseline = 'top';
      context.textAlign = 'left';
      context.fillText( 'Reset', xPos + 5, yPos + 5 );
  };

  ResetButton.prototype.press = function () {
      this.pressed = true;
      this.reset();
  };

  ResetButton.prototype.unPress = function ( context ) {
      this.pressed = false;
  };

  ResetButton.prototype.setLocationComponents = function ( x, y ) {
      this.location.x = x;
      this.location.y = y;
  };

  ResetButton.prototype.setLocation = function ( location ) {
      this.setLocationComponents( location.x, location.y );
  };

  ResetButton.prototype.containsPoint = function ( point ) {
      return point.x > this.location.x && point.x < this.location.x + this.width &&
             point.y > this.location.y && point.y < this.location.y + this.height;
  };

  return ResetButton;

});
