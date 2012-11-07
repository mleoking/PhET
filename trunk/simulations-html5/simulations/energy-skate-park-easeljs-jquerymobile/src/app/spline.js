define( [], function () {

    function showPointer( mouseEvent ) { document.body.style.cursor = "pointer"; }

    function showDefault( mouseEvent ) { document.body.style.cursor = "default"; }

    function setCursorHand( displayObject ) {
        displayObject.onMouseOver = showPointer;
        displayObject.onMouseOut = showDefault;
    }

    var that = {};

    that.createSplineLayer = function ( groundHeight ) {

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

        splineLayer.controlPoints = controlPoints;

        return splineLayer;
    };

    return that;
} );