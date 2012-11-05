require( [
             'geometry',
             'vector2d',
             'websocket-refresh',
             'skater',
             'control-panel',
             'background',
             'image!resources/barChartIcon.png',
             'image!resources/gridIcon.png',
             'image!resources/house.png',
             'image!resources/mountains.png',
             'image!resources/pieChartIcon.png',
             'image!resources/skater.png',
             'image!resources/speedIcon.png'
         ], function ( Geometry, Vector2D, WebsocketRefresh, Skater, ControlPanel, Background, barChartIconImage, gridIconImage, houseImage, mountainsImage, pieChartIconImage, skaterImage, speedIconImage ) {

    WebsocketRefresh.listenForRefresh();

    function showPointer( mouseEvent ) { document.body.style.cursor = "pointer"; }

    function showDefault( mouseEvent ) { document.body.style.cursor = "default"; }

    function setCursorHand( displayObject ) {
        displayObject.onMouseOver = showPointer;
        displayObject.onMouseOut = showDefault;
    }

    var group = new createjs.Container();
    var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
    fpsText.x = 4;
    fpsText.y = 280;
    var groundHeight = 116;

    var skater = Skater.create( skaterImage, groundHeight );
    setCursorHand( skater );
    var controlPanel = ControlPanel.createControlPanel( barChartIconImage, pieChartIconImage, gridIconImage, speedIconImage );
    var background = Background.create( houseImage, mountainsImage, groundHeight );

    var splineLayer = new createjs.Container();
    var line = null;

    function controlPointPressHandler( e ) {

        //Make dragging relative to touch point
        var relativePressPoint = null;
        e.onMouseMove = function ( event ) {
            var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
            if ( relativePressPoint === null ) {
                relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
            }
            else {
                e.target.x = transformed.x + relativePressPoint.x;
                e.target.y = Math.min( transformed.y + relativePressPoint.y, 768 - groundHeight );

                //add my own fields for layout
                e.target.centerX = e.target.x;
                e.target.centerY = e.target.y;
                line.drawBetweenControlPoints();
            }
        }
    }

    function createControlPoint( x, y ) {
        var circleGraphics = new createjs.Graphics();
        circleGraphics.beginFill( createjs.Graphics.getRGB( 0, 0, 255 ) );
        circleGraphics.drawCircle( 0, 0, 20 );
        var controlPoint = new createjs.Shape( circleGraphics );
        controlPoint.onPress = controlPointPressHandler;
        setCursorHand( controlPoint );
        //add my own fields for layout
        controlPoint.x = x;
        controlPoint.y = y;
        return controlPoint;
    }

    var a = createControlPoint( 100, 200 );
    var b = createControlPoint( 200, 300 );
    var c = createControlPoint( 300, 250 );

    var controlPoints = [a, b, c];

    function getX( point ) {return point.x;}

    function getY( point ) {return point.y;}

    function createLine() {
        var graphics = new createjs.Graphics();
        var line = new createjs.Shape( graphics );
        line.drawBetweenControlPoints = function () {
            graphics.clear();
            graphics.beginStroke( "#000000" ).setStrokeStyle( 20 );

            var pointArray = [];
            for ( var i = 0; i < controlPoints.length; i++ ) {
                var circleElement = controlPoints[i];
                pointArray.push( circleElement.x, circleElement.y );
            }

            var x = controlPoints.map( getX );
            var y = controlPoints.map( getY );
            var s = numeric.linspace( 0, 1, controlPoints.length );
            var splineX = numeric.spline( s, x );
            var splineY = numeric.spline( s, y );

            //Use 75 interpolating points because it is smooth enough even for a very large track (experimented with 3 control points only, with more control points may need more samples)
            var sAll = numeric.linspace( 0, 1, 75 );

            //http://stackoverflow.com/questions/1669190/javascript-min-max-array-values
            var myArray = [];
            for ( var i = 0; i < sAll.length; i++ ) {
                var b = splineX.at( sAll[i] );
                var a = splineY.at( sAll[i] );
                myArray.push( {x:b, y:a} );
            }

            for ( var i = 0; i < myArray.length; i++ ) {
                var controlPoint = myArray[i];
                if ( i == 0 ) {
                    graphics.moveTo( controlPoint.x, controlPoint.y );
                }
                else {
                    graphics.lineTo( controlPoint.x, controlPoint.y );
                }
            }
        };
        line.drawBetweenControlPoints();
        setCursorHand( line );

        //Allow dragging the entire line
        function pressHandler( e ) {
            line.dragging = true;
            //Make dragging relative to touch point
            var previousPoint = null;
            e.onMouseMove = function ( event ) {
                var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
                if ( previousPoint === null ) {
                    previousPoint = {x:transformed.x, y:transformed.y};
                }
                else {
                    var newPoint = {x:transformed.x, y:transformed.y};
                    var deltaX = newPoint.x - previousPoint.x;
                    var deltaY = newPoint.y - previousPoint.y;

                    a.x = a.x + deltaX;
                    a.y = a.y + deltaY;

                    b.x = b.x + deltaX;
                    b.y = b.y + deltaY;

                    c.x = c.x + deltaX;
                    c.y = c.y + deltaY;

                    line.drawBetweenControlPoints();
                    previousPoint = newPoint;
                }
            };
        }

        line.onPress = pressHandler;


        return line;
    }

    line = createLine();
    splineLayer.addChild( line );

    splineLayer.addChild( a );
    splineLayer.addChild( b );
    splineLayer.addChild( c );

    //Cache the background into a single image
//        background.cache( 0, 0, 1024, 768, 1 );

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

    //Necessary to enable mouseover events
    stage.enableMouseOver();

    stage.addChild( group );
    stage.update();

    // UI

    var frameCount = 0;
    var lastFrameRateUpdate = null;

    var filterStrength = 20;
    var frameTime = 0, lastLoop = new Date, thisLoop;

    function displayFrameRate() {
        frameCount++;

        //Get frame rate but filter transients: http://stackoverflow.com/questions/4787431/check-fps-in-js
        var thisFrameTime = (thisLoop = new Date) - lastLoop;
        frameTime += (thisFrameTime - frameTime) / filterStrength;
        lastLoop = thisLoop;
        if ( frameCount > 30 ) {
            fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps";
        }
    }


    // Event handling

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
//            controlPanel.cache(0,0,1024,768,scale*2);
        stage.update();
    };
    $( window ).resize( onResize );
    onResize(); // initial position

    stage.update();

    function updatePhysics() {
        var originalX = skater.x;
        var originalY = skater.y;
        var originalEnergy = 0.5 * skater.mass * skater.velocity.magnitude() * skater.velocity.magnitude() + skater.mass * 9.8 * (768 - groundHeight - skater.y);
//            console.log( originalEnergy );
        if ( skater.attached ) {

            var speed = skater.velocity.magnitude();
            skater.attachmentPoint = skater.attachmentPoint + speed / 1.8 * 0.003;

            //Find a point on the spline that conserves energy and is near the original point and in the right direction.


            //Could avoid recomputing the splines in this step if they haven't changed.  But it doesn't show up as high in the profiler.
            var s = numeric.linspace( 0, 1, controlPoints.length );
            var splineX = numeric.spline( s, controlPoints.map( getX ) );
            var splineY = numeric.spline( s, controlPoints.map( getY ) );
            skater.x = splineX.at( skater.attachmentPoint );
            skater.y = splineY.at( skater.attachmentPoint );

            if ( skater.attachmentPoint > 1.0 || skater.attachmentPoint < 0 ) {
                skater.attached = false;
                skater.velocity = new Vector2D( skater.x - originalX, skater.y - originalY );
            }

        }
        else {

            var newY = skater.y;
            var newX = skater.x;
            if ( !skater.dragging ) {
                skater.velocity = skater.velocity.plus( 0, 0.5 );
                newY = skater.y + skater.velocity.times( 1 ).y;
                newX = skater.x + skater.velocity.times( 1 ).x;
            }
            skater.x = newX;
            skater.y = newY;

            //Don't let the skater go below the ground.
            var maxY = 768 - groundHeight;
            var newSkaterY = Math.min( maxY, newY );
            skater.y = newSkaterY;
            if ( newSkaterY == maxY ) {
                skater.velocity = new Vector2D();
            }

            //don't let the skater cross the spline
            if ( controlPoints.length > 2 ) {
                var s = numeric.linspace( 0, 1, controlPoints.length );
                var delta = 1E-6;

                function getSides( xvalue, yvalue ) {
                    var splineX = numeric.spline( s, controlPoints.map( getX ).map( function ( x ) {return x - xvalue } ) );
                    var splineY = numeric.spline( s, controlPoints.map( getY ).map( function ( y ) {return y - yvalue } ) );

                    var xRoots = splineX.roots();
                    var sides = [];
                    for ( var i = 0; i < xRoots.length; i++ ) {
                        var xRoot = xRoots[i];
                        var pre = {x:splineX.at( xRoot - delta ), y:splineY.at( xRoot - delta )};
                        var post = {x:splineX.at( xRoot + delta ), y:splineY.at( xRoot + delta )};
                        var side = Geometry.linePointPosition2DVector( pre, post, {x:0, y:0} );
                        sides.push( {xRoot:xRoot, side:side} );
                    }
                    return sides;
                }

                var originalSides = getSides( originalX, originalY );
                var newSides = getSides( skater.x, skater.y );

                for ( var i = 0; i < originalSides.length; i++ ) {
                    var originalSide = originalSides[i];
                    for ( var j = 0; j < newSides.length; j++ ) {
                        var newSide = newSides[j];

                        var distance = Math.abs( newSide.xRoot - originalSide.xRoot );

                        if ( distance < 1E-4 && Geometry.getSign( originalSide.side ) != Geometry.getSign( newSide.side ) ) {
                            console.log( "crossed over" );
                            skater.attached = true;
                            skater.attachmentPoint = newSide.xRoot;
                        }
                    }
                }
            }
        }

        //Only draw when necessary because otherwise performance is worse on ipad3
        if ( skater.x != originalX || skater.y != originalY ) {

//                skaterDebugShape.setX( skater.getX() + skater.getWidth() / 2 );
//                skaterDebugShape.setY( skater.getY() + skater.getHeight() );
//                skaterLayer.draw();
        }
    }


    createjs.Ticker.setFPS( 60 );
    createjs.Ticker.addListener( displayFrameRate );
    createjs.Ticker.addListener( stage );
    createjs.Ticker.addListener( updatePhysics );

    //Enable touch and prevent default
    createjs.Touch.enable( stage, false, false );

} );
