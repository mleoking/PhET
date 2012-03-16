var canvas;

init();

function init() {

    canvas = document.getElementById( 'canvas' );

    document.onmousedown = onDocumentMouseDown;
    document.onmouseup = onDocumentMouseUp;
    document.onmousemove = onDocumentMouseMove;
    document.ondblclick = onDocumentDoubleClick;

    document.addEventListener( 'touchstart', onDocumentTouchStart, false );
    document.addEventListener( 'touchmove', onDocumentTouchMove, false );
    document.addEventListener( 'touchend', onDocumentTouchEnd, false );

    // Commenting out, since iPad seems to send these continuously.
//	window.addEventListener( 'deviceorientation', onWindowDeviceOrientation, false );

    // Disable elastic scrolling.  This is specific to iOS.
    document.addEventListener(
            'touchmove',
            function( e ) {
                e.preventDefault();
            },
            false
    );
}

// Event handlers.

function onDocumentMouseDown() {
    console.log( "onDocumentMouseDown" );
}

function onDocumentMouseUp() {
    console.log( "onDocumentMouseDown" );
}

function onDocumentMouseMove( event ) {
    console.log( "onDocumentMouseMove" );
}

function onDocumentDoubleClick() {
    console.log( "onDocumentDoubleClick" );
}

function onDocumentTouchStart( event ) {
    console.log( "onDocumentTouchStart" + event.type );
}

function onDocumentTouchMove( event ) {
    console.log( "onDocumentTouchMove" );
}

function onDocumentTouchEnd( event ) {
    console.log( "onDocumentTouchEnd" );
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}