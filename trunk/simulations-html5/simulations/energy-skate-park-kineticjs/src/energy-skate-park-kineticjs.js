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

//        canvas.width = window.innerWidth;
//        canvas.height = window.innerHeight;

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        container.onselectstart = function () { return false; }; // ie
        container.onmousedown = function () { return false; }; // mozilla

        var stage = new Kinetic.Stage( {
                                           container:"container",
                                           width:800,
                                           height:600
                                       } );

        var backgroundLayer = new Kinetic.Layer();
        var sky = new Kinetic.Rect( {
                                        x:0,
                                        y:0,
                                        width:800,
                                        height:600,
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
                                           width:800 + 20,
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

        // add cursor styling
        skater.on( "mouseover", function () { document.body.style.cursor = "pointer";} );
        skater.on( "mouseout", function () { document.body.style.cursor = "default"; } );

        skaterLayer.add( skater );

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

        function loop() {
//            updatePhysics();
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