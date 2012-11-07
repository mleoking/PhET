define([
  'underscore',
  'easel'
], function( _, Easel ){


  function ProtonView( proton ){
    this.proton = proton;
    this.render();
    this.draggable();
  }

  ProtonView.prototype = new Easel.Shape();

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

      console.log( "Pressed!!!" );

      evt.onMouseMove = function ( ev ) {
        self.x = ev.stageX + offset.x;
        self.y = ev.stageY + offset.y;

        self.proton.position.setComponents( self.x, self.y );
        console.log( "Dragged!!!", self.proton.position.toString() );
      };
    };

  };

  return ProtonView;


});
