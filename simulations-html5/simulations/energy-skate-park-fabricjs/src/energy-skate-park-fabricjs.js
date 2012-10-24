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
        }
        else {
            // The browser doesn't support WebSocket
            alert( "WebSocket NOT supported by your Browser!" );
        }
    }

    function run( images ) {

        listenForRefresh();

        $( "#myResetAllButton" ).click( function () { document.location.reload( true ); } );
        var canvas = document.getElementById( "display" );

        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        canvas.onselectstart = function () { return false; }; // ie
        canvas.onmousedown = function () { return false; }; // mozilla

        var fabricCanvas = new fabric.Canvas( 'display', {hoverCursor:'pointer', selection:false} );
        console.log( 'canvas = ' + fabricCanvas );

        for ( var i = 0, len = 1; i < len; i++ ) {
            fabric.Image.fromURL( 'resources/skater.png', function ( img ) {
                img.set( {
                             left:200,
                             top:200
//                             left:fabric.util.getRandomInt( 0, 600 ),
//                             top:fabric.util.getRandomInt( 0, 500 ),
//                             angle:fabric.util.getRandomInt( 0, 90 )
                         } );

                img.perPixelTargetFind = true;
                img.targetFindTolerance = 4;
                img.hasControls = img.hasBorders = false;

//                img.scale( fabric.util.getRandomInt( 50, 100 ) / 100 );

                fabricCanvas.add( img );
            } );
//
//            var rect = new fabric.Rect( { width:100, height:50 } );
//            rect.setFill( 'green' );
//            fabricCanvas.add( rect );
        }

//        fabricCanvas.renderAll();

        console.log( 'canvas = ' + fabricCanvas );
    }

    // Only executed our code once the DOM is ready.
    window.onload = function () {
        preloadimages( "resources/skater.png" ).done( run )
    }
})();