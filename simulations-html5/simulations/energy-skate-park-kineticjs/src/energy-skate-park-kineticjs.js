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
            alert( "WebSocket NOT supported by your Browser!" );
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

        var backgroundLayer = new Kinetic.Layer();
        var sky = new Kinetic.Rect( {
                                        x:0,
                                        y:0,
                                        width:1024,
                                        height:768,
                                        fill:{
                                            start:{ x:0, y:0 },
                                            end:{ x:0, y:600 },
                                            colorStops:[0, '7cc7fe', 1, '#eef7fe']
                                        }
                                    } );
        backgroundLayer.add( sky );

        var ground = new Kinetic.Rect( {
                                           x:-10,
                                           y:500,
                                           width:1024 + 20,
                                           height:600,
                                           fill:'#64aa64',
                                           stroke:'#008200',
                                           strokeWidth:2
                                       } );
        backgroundLayer.add( ground );

        var skaterLayer = new Kinetic.Layer();

        var splineLayer = new Kinetic.Layer();
        var circle = new Kinetic.Circle( {
                                             x:50,
                                             y:50,
                                             radius:50,
                                             fill:'red',
                                             stroke:'black',
                                             strokeWidth:4,
                                             draggable:true
                                         } );

        var pointerCursor = function () { document.body.style.cursor = "pointer";};
        var defaultCursor = function () { document.body.style.cursor = "default"; };

        // convert shape into an image object
        circle.toImage( {
                            // define the size of the new image object
                            width:100,
                            height:100,
                            callback:function ( img ) {
                                // cache the image as a Kinetic.Image shape
                                var image = new Kinetic.Image( {
                                                                   image:img,
                                                                   draggable:true
                                                               } );

                                image.on( "mouseover", pointerCursor );
                                image.on( "mouseout", defaultCursor );
                                console.log( "callback" );
                                splineLayer.add( image );
                                splineLayer.draw();
                            }
                        } );

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
                    css( "-webkit-transform", "scale(" + scale + "," + scale + ")" ).
                    css( "top", top ).css( "right", right );
//                    css( "width", w );//.css( "height", 300 * scale );

        }

        updateStageSize();

        $( window ).resize( updateStageSize );

        var causeRepaintsOn = $( "h1, h2, h3, p, .buttonText" );

        $( window ).resize( function () {
            causeRepaintsOn.css( "z-index", 1 );
        } );

        // add the skaterLayer to the stage
        stage.add( backgroundLayer );
        stage.add( splineLayer );
        stage.add( skaterLayer );

        //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
        //or here: http://jsfiddle.net/Y9uBv/5/
        var requestAnimationFrame =
                requestAnimationFrame ||
                webkitRequestAnimationFrame ||
                mozRequestAnimationFrame ||
                msRequestAnimationFrame ||
                oRequestAnimationFrame;


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

            if ( skater.getX() != originalX || skater.getY() != originalY ) {
                skaterLayer.draw();
            }

//            splineLayer.draw();
        }

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