require( [
             'geometry',
             'vector2d',
             'websocket-refresh',
             'skater',
             'control-panel',
             'background',
             'spline',
             'physics',
             'image!resources/barChartIcon.png',
             'image!resources/gridIcon.png',
             'image!resources/house.png',
             'image!resources/mountains.png',
             'image!resources/pieChartIcon.png',
             'image!resources/skater.png',
             'image!resources/speedIcon.png'
         ], function ( Geometry, Vector2D, WebsocketRefresh, Skater, ControlPanel, Background, Spline, Physics, barChartIconImage, gridIconImage, houseImage, mountainsImage, pieChartIconImage, skaterImage, speedIconImage ) {

    WebsocketRefresh.listenForRefresh();

    var group = new createjs.Container();
    var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
    fpsText.x = 4;
    fpsText.y = 280;
    var groundHeight = 116;

    var skater = Skater.create( skaterImage, groundHeight );
    var controlPanel = ControlPanel.createControlPanel( barChartIconImage, pieChartIconImage, gridIconImage, speedIconImage );
    var background = Background.create( houseImage, mountainsImage, groundHeight );

    //Cache the background into a single image
//        background.cache( 0, 0, 1024, 768, 1 );

    var splineLayer = Spline.createSplineLayer( groundHeight );

    group.addChild( background );
    group.addChild( controlPanel );
    group.addChild( splineLayer );
    group.addChild( skater );
    group.addChild( fpsText );

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    var canvas = document.getElementById( "c" );
    canvas.onselectstart = function () { return false; }; // IE
    canvas.onmousedown = function () { return false; }; // Mozilla

    var stage = new createjs.Stage( canvas );
    stage.addChild( group );

    var frameCount = 0;

    var filterStrength = 20;
    var frameTime = 0, lastLoop = new Date, thisLoop;

    function updateFrameRate() {
        frameCount++;

        //Get frame rate but filter transients: http://stackoverflow.com/questions/4787431/check-fps-in-js
        var thisFrameTime = (thisLoop = new Date) - lastLoop;
        frameTime += (thisFrameTime - frameTime) / filterStrength;
        lastLoop = thisLoop;
        if ( frameCount > 30 ) {
            fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps";
        }
    }

    var onResize = function () {
        var winW = $( window ).width(),
                winH = $( window ).height(),
                scale = Math.min( winW / 1024, winH / 768 ),
                canvasW = scale * 1024,
                canvasH = scale * 768;
        var canvas = $( '#c' );
        canvas.attr( 'width', canvasW );
        canvas.attr( 'height', canvasH );
        canvas.offset( {left:(winW - canvasW) / 2, top:(winH - canvasH) / 2} );
        group.scaleX = group.scaleY = scale;
        stage.update();
    };
    $( window ).resize( onResize );
    onResize(); // initial position

    createjs.Ticker.setFPS( 60 );
    createjs.Ticker.addListener( updateFrameRate );
    createjs.Ticker.addListener( stage );
    createjs.Ticker.addListener( function () {Physics.updatePhysics( skater, groundHeight, splineLayer );} );

    //Enable touch and prevent default
    createjs.Touch.enable( stage, false, false );

    //Necessary to enable MouseOver events
    stage.enableMouseOver();

    //Paint once after initialization
    stage.update();
} );
