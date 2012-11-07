define([
  'underscore',
  'easel'
], function( _, Easel ){


  function ProtonView( proton, transform ){
    var shape = new Easel.Shape();
    _.extend( this, shape );

    this.proton = proton;

    this.setTransform(
      transform[0],
      transform[1],
      transform[2],
      transform[3], 0, 0, 0, 0 );

    this.render();
    this.draggable();
  }

  ProtonView.prototype.render = function(){
    var color  = this.proton.color;
    var xPos   = this.proton.position.x;
    var yPos   = this.proton.position.y;
    var radius = this.proton.radius;

    this.graphics
      .beginFill( color )
      .drawCircle( xPos, yPos, radius );
  };

  ProtonView.prototype.draggable = function(){
    var self = this;

    this.onPress = function ( evt ) {
      var offset = {
        x: self.x - evt.stageX,
        y: self.y - evt.stageY
      };

      evt.onMouseMove = function ( ev ) {
        self.x = ev.stageX + offset.x;
        self.y = ev.stageY + offset.y;

        self.proton.position.setComponents( self.x, self.y );
      };
    };

  };

  return ProtonView;


});
