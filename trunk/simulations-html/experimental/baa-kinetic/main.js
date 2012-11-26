require([
  'kinetic'
],function( Kinetic ){


  var stage = new Kinetic.Stage({
    container: 'experiment-holder',
    width: 1024,
    height: 600
  });

  var layer = new Kinetic.Layer();

  var rect = new Kinetic.Rect({
    x: 239,
    y: 75,
    width: 100,
    height: 50,
    fill: 'green',
    stroke: '#000',
    strokeWidth: 4
  });

  // add the shape to the layer
  layer.add(rect);

  var circle = new Kinetic.Circle({
    x: stage.getWidth() / 2,
    y: stage.getHeight() / 2,
    radius: 70,
    fill: {
      start: {
        x: -50,
        y: -50
      },
      end: {
        x: 50,
        y: 50
      },
      colorStops: [0, 'red', 1, 'yellow']
    },
    stroke: 'black',
    strokeWidth: 4,
    draggable: true
  });

  circle.on('mouseover touchstart', function(e){
    this.setFill('red');
    layer.draw();
  });

  circle.on('mouseout touchend', function(e){
    this.setFill('black');
    layer.draw();
  });

  layer.add(circle);

  // add the layer to the stage
  stage.add(layer);


  $(function(){


    // $('#control-panel').css({
    //   width:$('#control-panel label').width()
    // });


    $(window).on('resize', function(){
      var win = $(window);

      $('canvas').css({
        width: win.width(),
        height: win.height()
      });

      stage.setWidth( win.width() );
      stage.setHeight( win.height() );

      layer.draw();
    });

  });



});
