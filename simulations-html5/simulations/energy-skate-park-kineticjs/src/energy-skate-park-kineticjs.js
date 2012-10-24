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
        var layer = new Kinetic.Layer();

        var skater = new Kinetic.Image( {
                                            x:140,
                                            y:100,
                                            image:images[0],
                                            width:106,
                                            height:118,
                                            draggable:true
                                        } );

        // add cursor styling
        skater.on( "mouseover", function () {
            document.body.style.cursor = "pointer";
        } );
        skater.on( "mouseout", function () {
            document.body.style.cursor = "default";
        } );
        // add the shape to the layer
        layer.add( skater );

        // add the layer to the stage
        stage.add( layer );


//        var imageObj = new Image();
//        imageObj.onload = function () {
//        };
//        imageObj.src = "resources/skater.png";


//        var fabricCanvas = new fabric.Canvas( 'display', {hoverCursor:'pointer', selection:false} );
//        console.log( 'canvas = ' + fabricCanvas );
//
//        //TODO: move this to another layer or maybe cache to improve performance
//        var grass = new fabric.Rect( { top:canvas.height / 2, left:canvas.width / 2, width:canvas.width, height:canvas.height / 2, selectable:false } );
//        grass.setFill( 'green' );
//        fabricCanvas.add( grass );
//
//        var sky = new fabric.Rect( { top:canvas.height / 4,
//                                       left:canvas.width / 2,
//                                       width:canvas.width,
//                                       height:canvas.height / 2,
//                                       selectable:false,
//                                       fill:'red'} );
//        sky.setGradientFill( fabricCanvas.getContext(), {
//            x1:0,
//            y1:0,
//            x2:0,
//            y2:sky.height,
//            colorStops:{
//                0:'#000',
//                1:'#fff'
//            }
//        } );
//        fabricCanvas.add( sky );
//
//        var circle = new fabric.Circle( {
//                                            left:100,
//                                            top:100,
//                                            radius:50,
//                                            fill:'blue'
//                                        } );
//        fabricCanvas.add( circle );
//
//        for ( var i = 0, len = 1; i < len; i++ ) {
//            fabric.Image.fromURL( 'resources/skater.png', function ( img ) {
//                img.set( {
//                             left:200,
//                             top:200
////                             left:fabric.util.getRandomInt( 0, 600 ),
////                             top:fabric.util.getRandomInt( 0, 500 ),
////                             angle:fabric.util.getRandomInt( 0, 90 )
//                         } );
//
//                img.perPixelTargetFind = true;
//                img.targetFindTolerance = 4;
//                img.hasControls = img.hasBorders = false;
//
////                img.scale( fabric.util.getRandomInt( 50, 100 ) / 100 );
//
//                fabricCanvas.add( img );
//            } );
////
//
//        }
//
//        fabricCanvas.renderAll();
//
//        console.log( 'canvas = ' + fabricCanvas );
//
//        //or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
//        //or here: http://jsfiddle.net/Y9uBv/5/
//        var requestAnimationFrame =
//                requestAnimationFrame ||
//                webkitRequestAnimationFrame ||
//                mozRequestAnimationFrame ||
//                msRequestAnimationFrame ||
//                oRequestAnimationFrame;
//
//        function loop() {
//            circle.setGradientFill( fabricCanvas.getContext(), {
//                x1:0,
//                y1:0,
//                x2:0,
//                y2:circle.height,
//                colorStops:{
//                    0:'#000',
//                    1:'#fff'
//                }
//            } );
//
//            sky.setGradientFill( fabricCanvas.getContext(), {
//                x1:0,
//                y1:0,
//                x2:0,
//                y2:sky.height,
//                colorStops:{
//                    0:'#000',
//                    1:'#fff'
//                }
//            } );
//
//            fabricCanvas.renderAll();
////            updatePhysics();
////            renderGraphics();
//            requestAnimationFrame( loop );
//        }
//
//        requestAnimationFrame( loop );
    }

    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadimages( "resources/skater.png" ).done( run )
    }
})();