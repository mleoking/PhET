$( function () {

    //Listen for file changes for auto-refresh
    function listenForRefresh() {
        if ( "WebSocket" in window ) {
            // Let us open a web socket
            var ws = new WebSocket( "ws://localhost:8887/echo" );
            ws.onmessage = function () { document.location.reload( true ); };
            ws.onclose = function () { };
            console.log( "opened websocket" );
        }
        else {
            // The browser doesn't support WebSocket
            //            alert( "WebSocket NOT supported by your Browser!" );
            console.log( "WebSocket NOT supported by your Browser!" );
        }
    }

    function run( images ) {

        listenForRefresh();
        // Widgets

        var Slider = function ( opts ) {
            this.initialize( opts );
        };
        Slider.prototype = $.extend( new createjs.Container(), {
            initialize:function ( opts ) {
                opts = opts || {};
                this.min = opts.min;
                this.max = opts.max;
                this.size = opts.size;
                this.thumbSize = this.size.y / 2;
                this.trackWidth = this.size.x - this.size.y;
                this.onChange = opts.onChange || function () {};
                this.x = opts.pos.x;
                this.y = opts.pos.y;

                this.track = new createjs.Shape();
                this.track.graphics
                        .setStrokeStyle( this.size.y / 4, 1 /*round*/ )
                        .beginStroke( createjs.Graphics.getHSL( 192, 10, 80 ) )
                        .moveTo( this.thumbSize, 0 )
                        .lineTo( this.thumbSize + this.trackWidth, 0 );
                this.addChild( this.track );

                this.thumb = new createjs.Shape();
                this.thumb.graphics
                        .setStrokeStyle( this.size.y / 12 )
                        .beginStroke( createjs.Graphics.getHSL( 18, 50, 30 ) )
                        .beginFill( createjs.Graphics.getHSL( 18, 30, 90 ) )
                        .drawCircle( 0, 0, this.thumbSize );
                this.addChild( this.thumb );

                this.thumb.onPress = this.drag.bind( this );
                this.thumb.onMouseDrag = this.drag.bind( this );

                if ( opts.value ) {
                    this.setValue( opts.value )
                }
            },

            getValue:function () {
                return this._value
            },

            setValue:function ( value ) {
                if ( this.max && !(value && value < this.max) )  // This phrasing deals with NaN
                {
                    value = this.max;
                }
                if ( this.min && !(value && value > this.min) )  // max === null / undefined / NaN defaults to min
                {
                    value = this.min;
                }
                this._value = value;

                this.thumb.x = this.thumbSize + this.trackWidth * (value - this.min) / (this.max - this.min);

                this.onChange( value )
            },

            drag:function ( event ) {
                event.onMouseMove = this.drag.bind( this );
                var dragAtX = this.globalToLocal( event.stageX, event.stageY ).x;
                if ( event.type === 'onPress' ) {
                    this.dragOffset = this.thumb.x - this.thumbSize - dragAtX;
                }
                else {
                    this.setValue( (dragAtX + this.dragOffset) / this.trackWidth * (this.max - this.min) + this.min )
                }
            }
        } );

        var group = new createjs.Container();
        var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
        fpsText.x = 4;
        fpsText.y = 280;
        group.addChild( fpsText );
        var skater = new createjs.Bitmap( images[0] );

        //put registration point at bottom center of the skater
        skater.regX = images[0].width / 2;
        skater.regY = images[0].height;
        skater.x = 100;
        skater.y = 20;
        skater.velocity = vector2d( 0, 0 );
        var scaleFactor = 0.65;
        skater.scaleX = scaleFactor;
        skater.scaleY = scaleFactor;

        function pressHandler( e ) {

            //Make dragging relative to touch point
            var relativePressPoint = null;
            e.onMouseMove = function ( event ) {
                var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
                if ( relativePressPoint === null ) {
                    relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
                }
                else {
                    e.target.x = transformed.x + relativePressPoint.x;

                    //don't let the skater go below ground
                    e.target.y = Math.min( transformed.y + relativePressPoint.y, 768 - groundHeight );
//                    console.log( e.target.y );
                }
                skater.dragging = true;
            };
            e.onMouseUp = function ( event ) {
                skater.dragging = false;
                skater.velocity = vector2d( 0, 0 );
            };
        }

        skater.onPress = pressHandler;

        var groundGraphics = new createjs.Graphics();
        groundGraphics.beginFill( "#64aa64" );
        var groundHeight = 200;
        groundGraphics.rect( 0, 768 - groundHeight, 1024, groundHeight );
        var ground = new createjs.Shape( groundGraphics );

        var skyGraphics = new createjs.Graphics();
        skyGraphics.beginFill( "#7cc7fe" );
        skyGraphics.rect( 0, 0, 1024, 768 - groundHeight );
        var sky = new createjs.Shape( skyGraphics );

        //Use the allTexts to measure row width for layout
        function checkBoxRow( allTexts, index, image ) {
            var row = new createjs.Container();

            var checkBox = new createjs.Shape( new createjs.Graphics().beginStroke( createjs.Graphics.fromRG ).drawRoundRect( 0, 0, 30, 30, 5 ).endStroke() );

            var text2 = new createjs.Text( allTexts[index], '20px "Arial",Tahoma' );
            text2.y = 3;
            text2.x = 30 + 5;
            row.height = 40;//make all rows same height

            row.width = text2.getMeasuredWidth();
            row.addChild( checkBox );
            row.addChild( text2 );
            var bitmap = new createjs.Bitmap( image );
            bitmap.x = 180 - image.width;
            row.addChild( bitmap );
            return row;
        }

        function verticalLayoutPanel() {
            var container = new createjs.Container();
            var offsetY = 8;
            var insetX = 8;
            container.addChild( new createjs.Shape( new createjs.Graphics().beginFill( "#c8f0c8" ).drawRoundRect( 0, 0, 200, 195, 10 ).endFill().beginStroke( "black" ).drawRoundRect( 0, 0, 200, 195, 10 ).endStroke() ) );
            container.addLayoutItem = function ( child ) {
                child.x = insetX;
                child.y = offsetY;
                container.addChild( child );
                offsetY += child.height;
            };
            container.y = 8;
            container.x = 1024 - 200 - 8;
            return container;
        }

        function createControlPanel() {
            var controlPanel = verticalLayoutPanel();
            var texts = ["Bar Graph", "Pie Chart", "Grid", "Speed"];
            controlPanel.addLayoutItem( checkBoxRow( texts, 0, images[3] ) );
            controlPanel.addLayoutItem( checkBoxRow( texts, 1, images[4] ) );
            controlPanel.addLayoutItem( checkBoxRow( texts, 2, images[5] ) );
            controlPanel.addLayoutItem( checkBoxRow( texts, 3, images[6] ) );
            var container = new createjs.Container();
            var text = new createjs.Text( "Skater Mass", '24px "Arial",Tahoma' );
            text.x = 25;
            container.addChild( text );
            controlPanel.addLayoutItem( container );
            return controlPanel;
        }

        var controlPanel = createControlPanel();

        var background = new createjs.Container();
        background.addChild( sky );
        background.addChild( ground );
        var houseImage = images[1];
        var house = new createjs.Bitmap( houseImage );
        house.y = 768 - groundHeight - houseImage.height;
        house.x = 800;
        var mountainImage = images[2];
        var mountain = new createjs.Bitmap( mountainImage );
        var mountainScale = 0.6;
        mountain.x = 0;
        mountain.y = 768 - groundHeight - mountainImage.height * mountainScale;
        mountain.scaleX = mountainScale;
        mountain.scaleY = mountainScale;
        background.addChild( mountain );
        background.addChild( house );

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
            //add my own fields for layout
            controlPoint.x = x;
            controlPoint.y = y;
            return controlPoint;
        }

        var a = createControlPoint( 100, 200 );
        var b = createControlPoint( 200, 300 );
        var c = createControlPoint( 300, 250 );

        var controlPoints = [a, b, c];

//        var updateSplineTrack = function () {
//            console.log( "drag" );
//
//            //use the same algorithm as in trunk\simulations-java\common\spline\src\edu\colorado\phet\common\spline\CubicSpline2D.java
//
//            track.setPoints( myArray );
//        };

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
            return line;
        }

        line = createLine();
        splineLayer.addChild( line );


        splineLayer.addChild( a );
        splineLayer.addChild( b );
        splineLayer.addChild( c );

        group.addChild( background );
        group.addChild( controlPanel );
        group.addChild( splineLayer );
        group.addChild( skater );

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        var canvas = document.getElementById( "c" );
        canvas.onselectstart = function () { return false; }; // IE
        canvas.onmousedown = function () { return false; }; // Mozilla

        var stage = new createjs.Stage( canvas );
        stage.addChild( group );
        stage.update();

        // Physics

        var Vector = {
            add:function ( v, w ) {
                return { x:v.x + w.x,
                    y:v.y + w.y }
            },
            sub:function ( v, w ) {
                return { x:v.x - w.x,
                    y:v.y - w.y }
            },
            times:function ( v, s ) {
                return { x:v.x * s,
                    y:v.y * s }
            },
            dot:function ( v, w ) {
                return v.x * w.x + v.y * w.y
            },
            length:function ( v ) {
                return Math.sqrt( Vector.dot( v, v ) )
            },
            normalize:function ( v ) {
                var length = Vector.length( v );
                return { x:v.x / length,
                    y:v.y / length }
            }
        };

        // UI

        var frameCount = 0,
                lastFrameRateUpdate = null;

        function displayFrameRate() {
            frameCount++;
            if ( frameCount > 30 ) {
                var now = new Date().getTime();

                var rate = frameCount * 1000 / (now - lastFrameRateUpdate);
                fpsText.text = rate.toFixed( 1 ) + " fps";

                frameCount = 0;
                lastFrameRateUpdate = now
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
            stage.update();
        };
        $( window ).resize( onResize );
        onResize(); // initial position

        stage.update();


        function updatePhysics() {
            var originalX = skater.x;
            var originalY = skater.y;
            if ( skater.attached ) {

                skater.attachmentPoint = skater.attachmentPoint + 0.007;

                //TODO: could avoid recomputing the splines in this step if they haven't changed.
                var s = numeric.linspace( 0, 1, controlPoints.length );
                var splineX = numeric.spline( s, controlPoints.map( getX ) );
                var splineY = numeric.spline( s, controlPoints.map( getY ) );
                skater.x = splineX.at( skater.attachmentPoint );
                skater.y = splineY.at( skater.attachmentPoint );

                if ( skater.attachmentPoint > 1.0 || skater.attachmentPoint < 0 ) {
                    skater.attached = false;
                }
                skater.velocity = vector2d( skater.x - originalX, skater.y - originalY );
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
                    skater.velocity = zero();
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
                            var side = linePointPosition2DVector( pre, post, {x:0, y:0} );
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

                            if ( distance < 1E-4 && getSign( originalSide.side ) != getSign( newSide.side ) ) {
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
    }

    //http://www.javascriptkit.com/javatutors/preloadimagesplus.shtml
    function preloadImages( a ) {
        var newImages = [], loadedImages = 0;
        var postAction = function ( event ) {};
        var arr = (typeof a != "object") ? [a] : a;

        function imageLoadPost() {
            loadedImages++;
            if ( loadedImages == arr.length ) {
                postAction( newImages ); //call postAction and pass in newImages array as parameter
            }
        }

        for ( var i = 0; i < arr.length; i++ ) {
            newImages[i] = new Image();
            newImages[i].src = arr[i];
            newImages[i].onload = function () {imageLoadPost()};
            newImages[i].onerror = function () {imageLoadPost()};
        }
        return { //return blank object with done() method
            done:function ( f ) {
                postAction = f || postAction; //remember user defined callback functions to be called when images load
            }
        }
    }

    //TODO: Switch to http://www.createjs.com/#!/PreloadJS
    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadImages( ["resources/skater.png", "resources/house.png", "resources/mountains.png", "resources/barChartIcon.png", "resources/pieChartIcon.png", "resources/gridIcon.png", "resources/speedIcon.png"] ).done( run )
    }
} );
