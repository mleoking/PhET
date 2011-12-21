// Copyright 2002-2011, University of Colorado

var canvas;
var touchInProgress = false;
var context;
var particles = new Array();
var neutronBucket;
var protonBucket;
var particleBeingDragged = null;
var resetButton;

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

    // Add the buckets where nucleons are created and returned.
    neutronBucket = new Bucket( new Point2D( 100, 300 ), "gray" );
    protonBucket = new Bucket( new Point2D( 400, 300 ), "red" );

    // Add the reset button.
    resetButton = new ResetButton( new Point2D( 600, 350 ), "orange" );

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

//-----------------------------------------------------------------------------
// Point2D class.
//-----------------------------------------------------------------------------

function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
}

Point2D.prototype.toString = function() {
    return this.x + ", " + this.y;
}

Point2D.prototype.setComponents = function( x, y ) {
    this.x = x;
    this.y = y;
}

Point2D.prototype.set = function( point2D ) {
    this.setComponents( point2D.x, point2D.y );
}

//-----------------------------------------------------------------------------
// Particle class.
//-----------------------------------------------------------------------------

function Particle( color ) {
    this.location = new Point2D( 0, 0 );
    this.radius = 20;
    this.color = color;
}

Particle.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createRadialGradient( xPos - this.radius / 3, yPos - this.radius / 3, 0, xPos, yPos, this.radius );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    context.fillStyle = gradient;
    context.beginPath();
    context.arc( xPos, yPos, this.radius, 0, Math.PI * 2, true );
    context.closePath();
    context.fill();
}

Particle.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

Particle.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

Particle.prototype.containsPoint = function( point ) {
    return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
}

//-----------------------------------------------------------------------------
// Bucket class.
//-----------------------------------------------------------------------------

function Bucket( initialLocation, color ) {
    this.location = initialLocation;
    this.width = 150;
    this.height = this.width * 0.5;
    this.color = color;
}

Bucket.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createLinearGradient( xPos, yPos, xPos + this.width, yPos );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    context.fillStyle = gradient;
    context.beginPath();
    context.moveTo( xPos, yPos );
    context.lineTo( xPos + this.width * 0.15, yPos + this.height );
    context.lineTo( xPos + this.width * 0.85, yPos + this.height );
    context.lineTo( xPos + this.width, yPos );
    context.lineTo( xPos, yPos );
    context.closePath();
    context.fill();
}

Bucket.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

Bucket.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

Bucket.prototype.containsPoint = function( point ) {
    // Treat the shape as rectangle, even though it may not exactly be one.
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
}

//-----------------------------------------------------------------------------
// Reset button class
//-----------------------------------------------------------------------------

function ResetButton( initialLocation, color ) {
    this.location = initialLocation;
    this.width = 70;
    this.height = 30;
    this.color = color;
}

ResetButton.prototype.draw = function( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createLinearGradient( xPos, yPos, xPos, yPos + this.height );
    gradient.addColorStop( 0, "white" );
    gradient.addColorStop( 1, this.color );
    // Draw box that defines button outline.
    context.fillStyle = gradient;
    context.fillRect( xPos, yPos, this.width, this.height );
    // Put text on the box.
    context.fillStyle = '#000';
    context.font = '20px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'Reset', xPos + 5, yPos + 5 );
}

ResetButton.prototype.setLocationComponents = function( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

ResetButton.prototype.setLocation = function( location ) {
    this.setLocationComponents( location.x, location.y );
}

ResetButton.prototype.containsPoint = function( point ) {
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
}

//-----------------------------------------------------------------------------


// Main drawing function.
function draw() {

    clearBackground();

    // Draw the text.
    drawTitle();
    drawPhetLogo();

    // Draw the reset button.
    resetButton.draw( context );

    // Draw the buckets.
    neutronBucket.draw( context );
    protonBucket.draw( context );

    // Draw the particles.
    for ( var i = 0; i < particles.length; i++ ) {
        particles[i].draw( context );
    }
}

function removeAllParticles() {
    particles.length = 0;
}

//-----------------------------------------------------------------------------
// Event handlers.
//-----------------------------------------------------------------------------

function onDocumentMouseDown( event ) {
    onTouchStart( new Point2D( event.clientX, event.clientY ) );
}

function onDocumentMouseUp( event ) {
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
    touchInProgress = true;
    particleBeingDragged = null;
    // See if this touch start is on any of the existing particles.
    for ( i = 0; i < particles.length; i++ ) {
        if ( particles[i].containsPoint( location ) ) {
            particleBeingDragged = particles[i];
            break;
        }
    }
    if ( particleBeingDragged == null ) {
        // See if the touch was on any of the buckets and, if so, create the
        // corresponding particle.
        if ( neutronBucket.containsPoint( location ) ) {
            particleBeingDragged = new Particle( "gray" );
            particles.push( particleBeingDragged );
        }
        else if ( protonBucket.containsPoint( location ) ) {
            particleBeingDragged = new Particle( "red" );
            particles.push( particleBeingDragged );
        }
        // Else see if the button is being touched.
        else if ( resetButton.containsPoint( location ) ) {
            removeAllParticles();
        }
    }

    // Position the particle (if there is one) at the location of this event.
    if ( particleBeingDragged != null ) {
        particleBeingDragged.setLocation( location );
    }

    draw();
}

function onDrag( location ) {
    if ( touchInProgress && particleBeingDragged != null ) {
        particleBeingDragged.setLocation( location );
        draw();
    }
}

function onTouchEnd( location ) {
    touchInProgress = false;
    particleBeingDragged = null;
}