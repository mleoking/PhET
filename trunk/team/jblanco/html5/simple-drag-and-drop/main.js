// Copyright 2002-2011, University of Colorado

var canvas;
var mouseX = 0;
var mouseY = 0;
var mouseDown = false;
var context;


//Hook up the initialization function.
$( document ).ready( function() {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );

// Handler for window resize events.
function resizer() {
    canvas.width = $( window ).width();
    canvas.height = $( window ).height();
    draw();
}
;

// Initialize the canvas, context,
function init() {
    function Node() {
        this.x = 5;
        this.y = 10;

        this.funcName = function() {
            this.y = 20;
        }
    }

    var testNode = new Node();
    console.log( testNode.y );
    testNode.funcName();
    console.log( testNode.y );

    canvas = $( '#canvas' )[0];

    if ( canvas.getContext ) {
        context = canvas.getContext( '2d' );
    }

    document.onmousedown = onDocumentMouseDown;
    document.onmouseup = onDocumentMouseUp;
    document.onmousemove = onDocumentMouseMove;

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
    resizer();
}

function drawParticle( xPos, yPos, radius, color ) {
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

function clearBackground() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}
;

function drawCircle( xPos, yPos, radius, color ) {
    context.strokeStyle = '#000'; // black
    context.lineWidth = 4;
    context.fillStyle = color;
    context.beginPath();
    context.arc( xPos, yPos, radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

// Main drawing function.
function draw() {
    clearBackground();
    drawCircle( mouseX, mouseY, 20, "red" );
}

// Event handlers.

function onDocumentMouseDown() {
    console.log( "onDocumentMouseDown" );
    mouseDown = true;
    mouseX = event.clientX;
    mouseY = event.clientY;
    draw();
}

function onDocumentMouseUp() {
    console.log( "onDocumentMouseDown" );
    mouseDown = false;
    mouseX = event.clientX;
    mouseY = event.clientY;
    draw();
}

function onDocumentMouseMove( event ) {
    console.log( "onDocumentMouseMove" );
    if ( mouseDown ) {
        mouseX = event.clientX;
        mouseY = event.clientY;
        draw();
    }
}

function onDocumentTouchStart( event ) {
    console.log( "onDocumentTouchStart" );
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        mouseX = event.touches[ 0 ].pageX;
        mouseY = event.touches[ 0 ].pageY;
        draw();
    }
}

function onDocumentTouchMove( event ) {
    console.log( "onDocumentTouchMove" );
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        mouseX = event.touches[ 0 ].pageX;
        mouseY = event.touches[ 0 ].pageY;
        draw();
    }
}

function onDocumentTouchEnd( event ) {
    console.log( "onDocumentTouchEnd" );
    mouseDown = false;
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}