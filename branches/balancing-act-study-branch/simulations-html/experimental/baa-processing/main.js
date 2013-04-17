require([
  'processing'
], function( Processing ){

  var canvas = $('#canvas');

  function demoCanvas( p ){
    var xPos = 0;

    p.size(1024,768);

    p.draw = function(){
      p.background(255,255,153);

      p.fill(255,0,0);
      p.ellipse(p.mouseX, p.mouseY,100,100);
    };

  }

  var processingInstance = new Processing(canvas[0], demoCanvas );

});
