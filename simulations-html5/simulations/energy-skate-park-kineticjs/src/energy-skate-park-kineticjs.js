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

        $( "#myResetAllButton" ).click( function () { document.location.reload( true ); } );
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
        skater.on( "mouseover", function () { document.body.style.cursor = "pointer";} );
        skater.on( "mouseout", function () { document.body.style.cursor = "default"; } );

        skaterLayer.add( skater );

        //Scale up or down to fit the screen
        function updateStageSize() {
            var designWidth = 1024;
            var designHeight = 768;
            var stageWidth = stage.getWidth();
            var stageHeight = stage.getHeight();
            var sx = stageWidth / designWidth;
            var sy = stageHeight / designHeight;
            var min = Math.min( sx, sy );
            stage.setScale( min );
            stage.draw();
        }

        //TODO: Center on available bounds
//        var innerWidth = window.innerWidth;
//        var innerHeight = window.innerHeight;
//
//        //Center on available bounds
//        if ( sy == min ) {
//            rootNode.setPosition( windowWidth / 2 - designWidth * min / 2, 0 );
//        }
//        else {
//            rootNode.setPosition( 0, windowHeight / 2 - designHeight * min / 2 );
//        }
//
//
//        stage.setScale( 0.5 );

        //TODO: update scale when window size changes
        //See http://stackoverflow.com/questions/2854407/javascript-jquery-window-resize-how-to-fire-after-the-resize-is-completed
        var waitForFinalEvent = (function () {
            var timers = {};
            return function ( callback, ms, uniqueId ) {
                if ( !uniqueId ) {
                    uniqueId = "Don't call this twice without a uniqueId";
                }
                if ( timers[uniqueId] ) {
                    clearTimeout( timers[uniqueId] );
                }
                timers[uniqueId] = setTimeout( callback, ms );
            };
        })();

        $( window ).resize( function () {
            updateStageSize();
        } );

        $( window ).resize( function () {
            waitForFinalEvent( function () {
                updateStageSize();
//                alert( 'Resize...' );
                //...
            }, 100, "some unique string" );
        } );

        // add the skaterLayer to the stage
        stage.add( backgroundLayer );
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

            var newY = skater.getY();
            if ( !skater.dragging ) {
                skater.velocityY = skater.velocityY + 0.5;
                newY = skater.getY() + skater.velocityY * 1;
            }
            skater.setY( newY );

//            console.log( "v = " + skater.velocityY );

            //Don't let the skater go below the ground.
            skater.setY( Math.min( 383, newY ) );
            skaterLayer.draw();
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