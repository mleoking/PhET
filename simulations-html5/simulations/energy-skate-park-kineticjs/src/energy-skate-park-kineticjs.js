(function () {

    //http://www.javascriptkit.com/javatutors/preloadimagesplus.shtml
    function preloadImages( a ) {
        var newImages = [], loadedImages = 0;
        var postAction = function () {};
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

    function listenForRefresh() {
        if ( "WebSocket" in window ) {
            // Let us open a web socket
            var ws = new WebSocket( "ws://localhost:8887/echo" );
            ws.onmessage = function ( event ) { document.location.reload( true ); };
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

        $( "#myResetAllButton" ).click( function () { window.location.reload(); } );
        var container = document.getElementById( "container" );

        container.width = window.innerWidth;
        container.height = window.innerHeight;

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        container.onselectstart = function () { return false; }; // ie
        container.onmousedown = function () { return false; }; // mozilla

        var stage = new Kinetic.Stage( {
                                           container:"container",
                                           width:container.width,
                                           height:container.height
                                       } );

        var groundLayer = new Kinetic.Layer();
        var skyLayer = new Kinetic.Layer();

        //Work around addColorStop error on Firefox and IE
        var sky = !($.browser.mozilla || $.browser.msie) ? new Kinetic.Rect( {
                                                                                 x:0,
                                                                                 y:0,
                                                                                 width:1024,
                                                                                 height:768,
                                                                                 fill:{
                                                                                     start:{ x:0, y:0 },
                                                                                     end:{ x:0, y:600 },
                                                                                     colorStops:[0, '7cc7fe', 1, '#eef7fe']
                                                                                 }
                                                                             } ) : new Kinetic.Rect( {
                                                                                                         x:0,
                                                                                                         y:0,
                                                                                                         width:1024,
                                                                                                         height:768,
                                                                                                         fill:'#7cc7fe'
                                                                                                     } );

        var ground = new Kinetic.Rect( {
                                           x:-10,
                                           y:500,
                                           width:1024 + 20,
                                           height:600,
                                           fill:'#64aa64',
                                           stroke:'#008200',
                                           strokeWidth:2
                                       } );
        groundLayer.add( ground );

        var skaterLayer = new Kinetic.Layer();
        var splineLayer = new Kinetic.Layer();

        var controlPoints = [];

        function getX( point ) {return point.getX() + point.getWidth() / 2;}

        function getY( point ) {return point.getY() + point.getHeight() / 2;}

        var track = new Kinetic.Line( {
                                          points:[73, 70, 340, 23, 450, 60, 500, 20],
                                          stroke:"gray",
                                          strokeWidth:15,
                                          lineCap:"cap",
                                          lineJoin:"cap"
                                      } );
        splineLayer.add( track );

        var pointerCursor = function () { document.body.style.cursor = "pointer";};
        var defaultCursor = function () { document.body.style.cursor = "default"; };


        var initialized = false;
        var updateSplineTrack = function () {
            console.log( "drag" );

            //Have to do this lazily since the images load asynchronously
            if ( controlPoints.length == 3 && initialized == false ) {
                initialized = true;
                controlPoints[1].setX( 100 );
                controlPoints[1].setY( 400 );

                controlPoints[2].setX( 300 );
                controlPoints[2].setY( 200 );
            }

            //use the same algorithm as in trunk\simulations-java\common\spline\src\edu\colorado\phet\common\spline\CubicSpline2D.java
            var pointArray = [];
            for ( var i = 0; i < controlPoints.length; i++ ) {
                var circleElement = controlPoints[i];
                pointArray.push( circleElement.getX() + circle.getWidth() / 2, circleElement.getY() + circle.getHeight() / 2 );
            }
            track.setPoints( pointArray );

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
            track.setPoints( myArray );
        };

        var getDistance = function ( a, b ) {
            var deltaX = a.x - b.x;
            var deltaY = a.y - b.y;
            return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
        };

        function updatePhysics() {
            var originalX = skater.getX();
            var originalY = skater.getY();
            if ( skater.attached ) {

                skater.attachmentPoint = skater.attachmentPoint + 0.007;

                //TODO: could avoid recomputing the splines in this step if they haven't changed.
                var s = numeric.linspace( 0, 1, controlPoints.length );
                var splineX = numeric.spline( s, controlPoints.map( getX ) );
                var splineY = numeric.spline( s, controlPoints.map( getY ) );
                skater.setX( splineX.at( skater.attachmentPoint ) - skater.getWidth() / 2 );
                skater.setY( splineY.at( skater.attachmentPoint ) - skater.getHeight() );

                if ( skater.attachmentPoint > 1.0 || skater.attachmentPoint < 0 ) {
                    skater.attached = false;
                }
                skater.velocity = vector2d( skater.getX() - originalX, skater.getY() - originalY );
            }
            else {

                var newY = skater.getY();
                var newX = skater.getX();
                if ( !skater.dragging ) {
                    skater.velocity = skater.velocity.plus( 0, 0.5 );
                    newY = skater.getY() + skater.velocity.times( 1 ).y;
                    newX = skater.getX() + skater.velocity.times( 1 ).x;
                }
                skater.setX( newX );
                skater.setY( newY );

                //Don't let the skater go below the ground.
                var newSkaterY = Math.min( 383, newY );
                skater.setY( newSkaterY );
                if ( newSkaterY == 383 ) {
                    skater.velocity = zero();
                }

                //don't let the skater cross the spline
                if ( controlPoints.length > 2 ) {
                    var s = numeric.linspace( 0, 1, controlPoints.length );
                    var delta = 1E-6;

                    function getSides( xvalue, yvalue ) {
                        var splineX = numeric.spline( s, controlPoints.map( getX ).map( function ( x ) {return x - xvalue - skater.getWidth() / 2} ) );
                        var splineY = numeric.spline( s, controlPoints.map( getY ).map( function ( y ) {return y - yvalue - skater.getHeight()} ) );

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
                    var newSides = getSides( skater.getX(), skater.getY() );

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
            if ( skater.getX() != originalX || skater.getY() != originalY ) {

                skaterDebugShape.setX( skater.getX() + skater.getWidth() / 2 );
                skaterDebugShape.setY( skater.getY() + skater.getHeight() );
                skaterLayer.draw();
            }
        }

        for ( var index = 0; index < 3; index++ ) {
            var circle = new Kinetic.Circle( {
                                                 x:21,
                                                 y:21,
                                                 radius:20,
                                                 fill:'blue',
                                                 stroke:'red',
                                                 strokeWidth:2,
                                                 draggable:true,
                                                 opacity:0.5
                                             } );

            // convert shape into an image object
            circle.toImage( {
                                // define the size of the new image object
                                width:44,
                                height:44,
                                callback:function ( img ) {
                                    // cache the image as a Kinetic.Image shape
                                    var image = new Kinetic.Image( {
                                                                       image:img,
                                                                       draggable:true
                                                                   } );

                                    controlPoints.push( image );
                                    image.on( "mouseover", pointerCursor );
                                    image.on( "mouseout", defaultCursor );
                                    image.on( "dragmove", updateSplineTrack );
                                    console.log( "callback" );
                                    splineLayer.add( image );
                                    updateSplineTrack();
                                    splineLayer.draw();
                                }
                            } );
        }

        sky.toImage( {width:1024, height:768, callback:function ( img ) {
            // cache the image as a Kinetic.Image shape
            var image = new Kinetic.Image( {
                                               image:img
                                           } );

            skyLayer.add( image );
            skyLayer.add( new Kinetic.Image( {image:images[1], x:800, y:430} ) );
            skyLayer.add( new Kinetic.Image( {image:images[2], x:0, y:425, scale:0.4} ) );
            skyLayer.draw();
        }} );
        var skater = new Kinetic.Image( {
                                            x:140,
                                            y:100,
                                            image:images[0],
                                            width:106,
                                            height:118,

                                            draggable:true
                                        } );
        skater.attached = false;

        skater.on( "dragstart", function () {
            skater.dragging = true;
            skater.velocity = zero();
            skater.attached = false;
        } );
        skater.on( "dragend", function () { skater.dragging = false; } );

        // add cursor styling
        skater.on( "mouseover", pointerCursor );
        skater.on( "mouseout", defaultCursor );

        skaterLayer.add( skater );

        var skaterDebugShape = new Kinetic.Circle( {
                                                       x:0,
                                                       y:0,
                                                       radius:3,
                                                       fill:'red'
                                                   } );
        skaterLayer.add( skaterDebugShape );

        var top = 1;
        //Scale up or down to fit the screen
        function updateStageSize() {
            var designWidth = 1024;
            var designHeight = 768;
            stage.setWidth( window.innerWidth );
            stage.setHeight( window.innerHeight );
            var stageWidth = stage.getWidth();
            var stageHeight = stage.getHeight();
            var sx = stageWidth / designWidth;
            var sy = stageHeight / designHeight;
            var scale = Math.min( sx, sy );
            stage.setScale( scale );
            stage.draw();

            var top = 0;
            var right = 0;

            //Center on available bounds
            if ( sy == scale ) {
                stage.setPosition( window.innerWidth / 2 - designWidth * scale / 2, 0 );

                top = 0;
                right = (window.innerWidth / 2 - designWidth * scale / 2) * scale;
            }
            else {
                stage.setPosition( 0, window.innerHeight / 2 - designHeight * scale / 2 );
                top = (window.innerHeight / 2 - designHeight * scale / 2) * scale;
                right = 0;
            }

            $( ".controlPanel" ).
//                    css( "-webkit-transform", "scale(" + scale + "," + scale + ")" ).
                    css( "top", top ).css( "right", right );
//                    css( "width", w );//.css( "height", 300 * scale );

        }

        updateStageSize();

        $( window ).resize( updateStageSize );

        var causeRepaintsOn = $( "h1, h2, h3, p, .buttonText" );

        $( window ).resize( function () {
            causeRepaintsOn.css( "z-index", 1 );
        } );

        stage.add( skyLayer );
        // add the skaterLayer to the stage
        stage.add( groundLayer );
        stage.add( splineLayer );
        stage.add( skaterLayer );

        //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
        //or here: http://jsfiddle.net/Y9uBv/5/
        //See http://stackoverflow.com/questions/5605588/how-to-use-requestanimationframe
        var requestAnimationFrame = function () {
            return (
                    window.requestAnimationFrame ||
                    window.webkitRequestAnimationFrame ||
                    window.mozRequestAnimationFrame ||
                    window.oRequestAnimationFrame ||
                    window.msRequestAnimationFrame ||
                    function ( /* function */ callback ) {
                        window.setTimeout( callback, 1000 / 60 );
                    }
                    );
        }();


        skater.velocity = zero();
        console.log( skater.velocity );

        //Add Internationalization by replacing strings with those loaded from .properties files.
        //Note this will not work with file:// syntax on chrome

        // This will initialize the plugin
        // and show two dialog boxes: one with the text "Olá World"
        // and other with the text "Good morning John!"

        //http://stackoverflow.com/questions/901115/how-can-i-get-query-string-values/901144#901144
        function getParameterByName( name ) {
            name = name.replace( /[\[]/, "\\\[" ).replace( /[\]]/, "\\\]" );
            var regexS = "[\\?&]" + name + "=([^&#]*)";
            var regex = new RegExp( regexS );
            var results = regex.exec( window.location.search );
            if ( results == null ) {
                return "";
            }
            else {
                return decodeURIComponent( results[1].replace( /\+/g, " " ) );
            }
        }

        var language = getParameterByName( "language" );

        if ( language == "" ) {
            language = "en";
        }

        console.log( language );
        jQuery.i18n.properties( {
                                    name:'energy-skate-park-strings',
                                    path:'localization/',
                                    mode:'map',
                                    language:language,
                                    callback:function () {
                                        // We specified mode: 'both' so translated values will be
                                        // available as JS vars/functions and as a map

                                        $( "#skaterMassLabel" ).html( $.i18n.prop( 'skater.mass' ) );
                                        $( "#barGraphLabel" ).html( $.i18n.prop( 'plots.bar-graph' ) );
                                        $( "#pieChartLabel" ).html( $.i18n.prop( 'pieChart' ) );
                                        $( "#gridLabel" ).html( $.i18n.prop( 'controls.show-grid' ) );
                                        $( "#speedLabel" ).html( $.i18n.prop( 'properties.speed' ) );
                                        $( "#returnSkaterButton" ).html( $.i18n.prop( 'controls.reset-character' ) );
//                                        $( "#resetAllButton" ).html( $.i18n.prop( 'controls.reset-character' ) );
                                    }
                                } );

        function loop() {

            updatePhysics();
            requestAnimationFrame( loop );
        }

        requestAnimationFrame( loop );
    }

    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadImages( ["resources/skater.png", "resources/house.png", "resources/mountains.png"] ).done( run )
    }
})();