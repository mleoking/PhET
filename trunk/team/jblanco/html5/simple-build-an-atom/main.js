// Copyright 2002-2011, University of Colorado

var canvas;
var mouseDown = false;
var context;
var particle;


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

// Initialize the canvas, context,
function init() {

    // Initialize references to the HTML5 canvas and its context.
    canvas = $( '#canvas' )[0];
    if ( canvas.getContext ) {
        context = canvas.getContext( '2d' );
    }

    // Set up event handlers.
    // TODO: Work with JO to "jquery-ize".
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

    // Create the particle.
    particle = new Particle( "red" );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();
}

function clearBackground() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}

function drawParticle( xPos, yPos, radius, color ) {
    var gradient1 = context.createRadialGradient( xPos - radius / 3, yPos - radius / 3, 0, xPos, yPos, radius );
    gradient1.addColorStop( 0, "white" );
    gradient1.addColorStop( 1, color );
    context.fillStyle = gradient1;
    context.beginPath();
    context.arc( xPos, yPos, radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

function Particle( color ) {
    this.xPos = 0;
    this.yPos = 0;
    this.radius = 20;
    this.color = color;
}

Particle.prototype.draw = function( context ) {
    var gradient = context.createRadialGradient( this.xPos - this.radius / 3, this.yPos - this.radius / 3, 0, this.xPos, this.yPos, this.radius );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    context.fillStyle = gradient;
    context.beginPath();
    context.arc( this.xPos, this.yPos, this.radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

// Main drawing function.
function draw() {
    clearBackground();
    particle.draw( context );
}

// Event handlers.

function onDocumentMouseDown() {
    console.log( "onDocumentMouseDown" );
    mouseDown = true;
    particle.xPos = event.clientX;
    particle.yPos = event.clientY;
    draw();
}

function onDocumentMouseUp() {
    console.log( "onDocumentMouseDown" );
    mouseDown = false;
    particle.xPos = event.clientX;
    particle.yPos = event.clientY;
    draw();
}

function onDocumentMouseMove( event ) {
    console.log( "onDocumentMouseMove" );
    if ( mouseDown ) {
        particle.xPos = event.clientX;
        particle.yPos = event.clientY;
        draw();
    }
}

function onDocumentTouchStart( event ) {
    console.log( "onDocumentTouchStart" );
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        particle.xPos = event.touches[ 0 ].pageX;
        particle.yPos = event.touches[ 0 ].pageY;
        draw();
    }
}

function onDocumentTouchMove( event ) {
    console.log( "onDocumentTouchMove" );
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        particle.xPos = event.touches[ 0 ].pageX;
        particle.yPos = event.touches[ 0 ].pageY;
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