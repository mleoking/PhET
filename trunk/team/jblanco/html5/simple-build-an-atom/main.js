// Copyright 2002-2011, University of Colorado

var canvas;
var touchInProgress = false;
var context;
var particles = new Array();
var addProtonNext = true;

// Hook up the initialization function.
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

function drawTitle() {
    context.fillStyle = '#00f';
    context.font = '30px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'Build an Atom', 10, 10 );
}

function drawPhetLogo() {
    context.fillStyle = '#f80';
    context.font = 'italic 20px sans-serif';
    context.textBaseline = 'top';
//    context.fillText( 'PhET', canvas.width - 70, canvas.height - 40 );
    context.fillText( 'PhET', canvas.width - 70, canvas.height / 2 );
}

function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
}

Point2D.prototype.toString = function() {
    return this.x + ", " + this.y;
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
    drawTitle();
    drawPhetLogo();
    for ( var i = 0; i < particles.length; i++ ) {
        particles[i].draw( context );
    }
}

// Event handlers.

function onDocumentMouseDown() {
    onTouchStart( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentMouseUp() {
    onTouchEnd( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentMouseMove( event ) {
    onDrag( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentTouchStart( event ) {
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        onTouchStart( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchMove( event ) {
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        onDrag( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchEnd( event ) {
    if ( event.touches.length == 1 ) {
        event.preventDefault();
        onTouchEnd( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    console.log( "onTouchStart called, location = " + location.toString() );
    touchInProgress = true;
    var color = 'red';
    if ( !addProtonNext ) {
        color = 'gray'
    }
    addProtonNext = !addProtonNext;
    particles.push( new Particle( color ) );
    particles[particles.length - 1].xPos = location.x;
    particles[particles.length - 1].yPos = location.y;
    draw();
}

function onDrag( location ) {
    if ( touchInProgress ) {
        particles[particles.length - 1].xPos = location.x;
        particles[particles.length - 1].yPos = location.y;
        draw();
    }
}

function onTouchEnd( location ) {
    touchInProgress = false;
}