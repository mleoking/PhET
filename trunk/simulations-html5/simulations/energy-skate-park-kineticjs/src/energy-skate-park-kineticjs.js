(function () {

    //http://www.javascriptkit.com/javatutors/preloadimagesplus.shtml
    function preloadimages( a ) {
        var newimages = [], loadedimages = 0;
        var postaction = function () {};
        var arr = (typeof a != "object") ? [a] : a;

        function imageloadpost() {
            loadedimages++;
            if ( loadedimages == arr.length ) {
                postaction( newimages ); //call postaction and pass in newimages array as parameter
            }
        }

        for ( var i = 0; i < arr.length; i++ ) {
            newimages[i] = new Image();
            newimages[i].src = arr[i];
            newimages[i].onload = function () {
                imageloadpost()
            };
            newimages[i].onerror = function () {
                imageloadpost()
            };
        }
        return { //return blank object with done() method
            done:function ( f ) {
                postaction = f || postaction; //remember user defined callback functions to be called when images load
            }
        }
    }

    function listenForRefresh() {
        if ( "WebSocket" in window ) {
            // Let us open a web socket
            var ws = new WebSocket( "ws://localhost:8887/echo" );
            ws.onmessage = function ( evt ) { document.location.reload( true ); };
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

        var sky = (navigator.userAgent.indexOf( "Firefox" ) == -1) ? new Kinetic.Rect( {
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
                                                                                                                   fill:'7cc7fe'
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


        var inited = false;
        var updateSplineTrack = function () {
            console.log( "drag" );

            //Have to do this lazily since the images load asynchronously
            if ( controlPoints.length == 3 && inited == false ) {
                inited = true;
                controlPoints[1].setX( 100 );
                controlPoints[1].setY( 200 );
                controlPoints[2].setX( 300 );
                controlPoints[2].setY( 0 );
            }

            var pointArray = [];
            for ( var i = 0; i < controlPoints.length; i++ ) {
                var circleElement = controlPoints[i];
                pointArray.push( circleElement.getX() + circle.getWidth() / 2, circleElement.getY() + circle.getHeight() / 2 );
            }
            track.setPoints( pointArray );

            function getX( point ) {return point.getX() + point.getWidth() / 2;}

            function getY( point ) {return point.getY() + point.getHeight() / 2;}

            var x = controlPoints.map( getX );
            var y = controlPoints.map( getY );
            var s = numeric.spline( x, y );

            //http://stackoverflow.com/questions/1669190/javascript-min-max-array-values
            var x0 = numeric.linspace( Math.min.apply( null, x ), Math.max.apply( null, x ), 1000 );
//            ctx.beginPath();
            var myArray = [];
            for ( var i = 0; i < x0.length; i++ ) {
                var a = x0[i];
                var b = s.at( x0[i] );
                myArray.push( a, b );
            }
            track.setPoints( myArray );

        };

        var controlPoints = [];
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

            image.on( "mouseover", pointerCursor );
            image.on( "mouseout", defaultCursor );
            skyLayer.add( image );
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

        skater.on( "dragstart", function () {
            skater.dragging = true;
            skater.velocityY = 0;
        } );
        skater.on( "dragend", function () { skater.dragging = false; } );

        // add cursor styling
        skater.on( "mouseover", pointerCursor );
        skater.on( "mouseout", defaultCursor );

        skaterLayer.add( skater );

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

            var w = 200 * scale;
//            top = top + 1;

            //            $( ".controlPanel" ).css( "right", 5*scale );
//            console.log( scale + ", top = " + top );
//            console.log( "w = " + w );
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


        skater.velocityY = 0;
        function updatePhysics() {
            var originalX = skater.getX();
            var originalY = skater.getY();

            var newY = skater.getY();
            if ( !skater.dragging ) {
                skater.velocityY = skater.velocityY + 0.5;
                newY = skater.getY() + skater.velocityY * 1;
            }
            skater.setY( newY );

//            console.log( "v = " + skater.velocityY );

            //Don't let the skater go below the ground.
            skater.setY( Math.min( 383, newY ) );

            //Only draw when necessary because otherwise performance is worse on ipad3
            if ( skater.getX() != originalX || skater.getY() != originalY ) {
                skaterLayer.draw();
            }

//            splineLayer.draw();
        }

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
//            renderGraphics();
            requestAnimationFrame( loop );
        }

        requestAnimationFrame( loop );
    }

    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadimages( "resources/skater.png" ).done( run )
    }
})();