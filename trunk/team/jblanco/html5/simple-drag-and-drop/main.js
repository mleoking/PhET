// Copyright 2002-2011, University of Colorado
var canvas;
var mouseX = 0;
var mouseY = 0;
var context;

init();

function init() {

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

    // Do the initial drawing, events will cause subsequent updates.
//    draw();
}

function draw() {
    clearBackground();
    drawParticle( mouseX, mouseY, 10, "red" )
}

var drawParticle = function ( xPos, yPos, radius, color ) {
    context.strokeStyle = '#000'; // black
    context.lineWidth = 4;

    var gradient1 = context.createRadialGradient( xPos, yPos, 0, xPos, yPos, radius );
    gradient1.addColorStop( 0, "white" );
    gradient1.addColorStop( 1, color );
    context.fillStyle = gradient1;

    // Draw some rectangles.
    context.beginPath();
    context.arc( xPos, yPos, radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

var clearBackground = function() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
};

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
    console.log( "onDocumentTouchStart" );
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