require([
  '../.',
  'caat'
], function( _, CAAT ){

  // create a director object
  var director = new CAAT.Director().initialize(
          1024,    // 100 pixels wide
          600,    // 100 pixels across
          document.getElementById('experiment-holder')
  );

  director.enableResizeEvents(CAAT.Director.prototype.RESIZE_PROPORTIONAL);

  // add a scene object to the director.
  var scene = director.createScene();

  var container = new CAAT.ActorContainer()
    .create()
    .setBounds(0, 0, director.canvas.width, director.canvas.height)
    .setFillStyle('#ccc');

  _.times(10, function(){
    // create a CAAT actor
    var circle = new CAAT.ShapeActor();

    circle
      .setLocation(Math.random() * 500, Math.random() * 500)
      .setSize(60,60)
      .setFillStyle('#ff0000')
      .setStrokeStyle('#000000');

    container.addChild(circle);

    circle.enableDrag();
  });

  // add it to the scene
  scene.addChild(container);

  // start the animation loop
  CAAT.loop(1);

});
