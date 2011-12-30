// Copyright 2002-2011, University of Colorado

var canvas;
var touchInProgress = false;
var context;
var particlesInNucleus = new Array();

var springs = new Array();

var nodes = new Array();

nodes.push( new ImageSprite( "resources/red-mass.png", 114, 496 ) );
nodes.push( new ImageSprite( "resources/green-mass.png", 210, 577 ) );
nodes.push( new ImageSprite( "resources/gold-mass.png", 276, 541 ) );
nodes.push( new ImageSprite( "resources/gram-50.png", 577, 590 ) );
nodes.push( new ImageSprite( "resources/gram-100.png", 392, 562 ) );
nodes.push( new ImageSprite( "resources/gram-250.png", 465, 513 ) );

nodes.push( new ImageSprite( "resources/ruler.png", 12, 51 ) );

nodes.push( new Slider( 700, 100 ) );
nodes.push( new Slider( 700, 150 ) );

//Performance consideration: 10 springs of 20 line segments each causes problems.
//for ( var i = 0; i < 10; i++ ) {
//    springs.push( new Spring( 50 + i * 50 ) );
//}

springs.push( new Spring( "1", 200 ) );
springs.push( new Spring( "2", 300 ) );
springs.push( new Spring( "3", 400 ) );

var dragTarget = null;
var relativeGrabPoint = null;
var resetButton;

// Hook up the initialization function.
$( document ).ready( function () {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );

// Handler for window resize events.
function resizer() {
    console.log( "resize received" );
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

    // Add the reset button.
    resetButton = new ResetButton( new Point2D( 900, 618 ), "orange" );

    // Commenting out, since iPad seems to send these continuously.
//	window.addEventListener( 'deviceorientation', onWindowDeviceOrientation, false );

    // Disable elastic scrolling.  This is specific to iOS.
    document.addEventListener(
            'touchmove',
            function ( e ) {
                e.preventDefault();
            },
            false
    );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();

    // Start the game loop
    animate();
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
    context.fillText( 'Masses and Springs', 700, 10 );
}

function drawPhetLogo() {
    context.fillStyle = '#f80';
    context.font = 'italic 20px sans-serif';
    context.textBaseline = 'top';
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

Point2D.prototype.toString = function () {
    return this.x + ", " + this.y;
}

Point2D.prototype.setComponents = function ( x, y ) {
    this.x = x;
    this.y = y;
}

Point2D.prototype.minus = function ( pt ) {
    return new Point2D( this.x - pt.x, this.y - pt.y );
}

Point2D.prototype.set = function ( point2D ) {
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

function Spring( name, x ) {
    this.name = name;
    this.anchor = new Point2D( x, 50 );
    this.attachmentPoint = new Point2D( x, 250 );
}

Spring.prototype.draw = function ( context ) {
    context.beginPath();
    context.fillStyle = '#00f';
    context.strokeStyle = '#f00';
    context.lineWidth = 4;
    context.beginPath();
    context.moveTo( this.anchor.x, this.anchor.y );
    const numZigs = 10;
    const zigHeight = -(this.attachmentPoint.y - this.anchor.y) / numZigs / 2;
//    javascript: console.log( "zig Height = " + zigHeight );

    var pt = new Point2D( this.anchor.x, this.anchor.y );
    for ( var i = 0; i < numZigs; i++ ) {
        var pt2 = new Point2D( pt.x + 10, pt.y - zigHeight );
        var pt3 = new Point2D( pt2.x - 10, pt2.y - zigHeight );
        context.lineTo( pt2.x, pt2.y );
        context.lineTo( pt3.x, pt3.y );
        pt = pt3;
    }
    context.stroke();
    context.closePath();

    const textHeight = 32;
    context.font = textHeight + "px sans-serif";
    const defaultTextAlign = context.textAlign;
    context.textAlign = "center";
    context.fillText( this.name, this.anchor.x, this.anchor.y - 32, 1000 );
    context.textAlign = defaultTextAlign;
}

function ImageSprite( im, x, y ) {
    this.image = new Image();
    this.image.src = im;
    this.position = new Point2D( x, y );

    //Repaint the screen when this image got loaded
    this.image.onload = function () {
        draw();
    }
}

ImageSprite.prototype.draw = function ( context ) {
    context.drawImage( this.image, this.position.x, this.position.y );
}

ImageSprite.prototype.containsPoint = function ( point ) {
    javascript: console.log( "point = " + point.x + ", " + point.y + ", location = " + this.position.x + ", " + this.position.y + ", width = " + this.image.width + ", height = " + this.image.height );
    return point.x >= this.position.x && point.y >= this.position.y && point.x <= this.position.x + this.image.width && point.y <= this.position.y + this.image.height;
}

ImageSprite.prototype.getReferencePoint = function () {
    return this.position;
}

ImageSprite.prototype.setPosition = function ( point ) {
    this.position = new Point2D( point.x, point.y );
//    javascript: console.log( "translated image to " + this.position.x + ", " + this.position.y );
}

Particle.prototype.draw = function ( context ) {
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

Particle.prototype.setLocation = function ( location ) {
    this.setLocationComponents( location.x, location.y );
}

Particle.prototype.setLocationComponents = function ( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

Particle.prototype.containsPoint = function ( point ) {
    return Math.sqrt( Math.pow( point.x - this.location.x, 2 ) + Math.pow( point.y - this.location.y, 2 ) ) < this.radius;
}

//-----------------------------------------------------------------------------
// Reset button class
//-----------------------------------------------------------------------------

function ResetButton( initialLocation, color ) {
    this.location = initialLocation;
    this.width = 90;
    this.height = 40;
    this.color = color;
}

ResetButton.prototype.draw = function ( context ) {
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
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.fillText( 'Reset', xPos + 5, yPos + 5 );
}

ResetButton.prototype.setLocationComponents = function ( x, y ) {
    this.location.x = x;
    this.location.y = y;
}

ResetButton.prototype.setLocation = function ( location ) {
    this.setLocationComponents( location.x, location.y );
}

ResetButton.prototype.containsPoint = function ( point ) {
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
}

/**
 * Provides requestAnimationFrame in a cross browser way.
 * https://gist.github.com/838785
 * @author paulirish / http://paulirish.com/
 */

if ( !window.requestAnimationFrame ) {
    window.requestAnimationFrame = ( function () {
        return window.webkitRequestAnimationFrame ||
               window.mozRequestAnimationFrame ||
               window.oRequestAnimationFrame ||
               window.msRequestAnimationFrame ||
               function ( /* function FrameRequestCallback */ callback, /* DOMElement Element */ element ) {
                   window.setTimeout( callback, 1000 / 60 );
               };
    } )();
}

// Main drawing function.
function draw() {

    clearBackground();

    // Draw the text.
    drawTitle();
    drawPhetLogo();

    // Draw the reset button.
    resetButton.draw( context );

    // Draw the particles that are in the nucleus.
    for ( var i = 0; i < particlesInNucleus.length; i++ ) {
        particlesInNucleus[i].draw( context );
    }

    //Draw the masses

    for ( i = 0; i < springs.length; i++ ) {
        springs[i].draw( context );
    }

    for ( i = 0; i < nodes.length; i++ ) {
        nodes[i].draw( context );
    }

    // Draw particle that is being dragged if there is one so it will be on top
    if ( dragTarget != null ) {
        dragTarget.draw( context );
    }
}

var count = 0;
function animate() {

    //http://animaljoy.com/?p=254
    // insert your code to update your animation here

    for ( i = 0; i < springs.length; i++ ) {
        springs[i].attachmentPoint.y = 300 + 100 * Math.sin( 6 * count / 100.0 );
    }
    count++;
    requestAnimationFrame( animate );
    draw();
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

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onTouchStart( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchMove( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onDrag( new Point2D( event.touches[ 0 ].pageX, event.touches[ 0 ].pageY ) );
    }
}

function onDocumentTouchEnd( event ) {
    onTouchEnd();
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    touchInProgress = true;
    dragTarget = null;

    //See which sprite wants to handle the touch
    for ( var i = 0; i < nodes.length; i++ ) {
        var containsPoint = nodes[i].containsPoint( location );
//        javascript: console.log( "checking mass contains: " + containsPoint );
        if ( containsPoint ) {
            dragTarget = nodes[i];
            var referencePoint = dragTarget.getReferencePoint();
            relativeGrabPoint = new Point2D( location.x - referencePoint.x, location.y - referencePoint.y );
            break;
        }
    }

    draw();
}

function onDrag( location ) {
    if ( touchInProgress && dragTarget != null ) {
        dragTarget.setPosition( location.minus( relativeGrabPoint ) );
        draw();
    }
}

function onTouchEnd() {
    touchInProgress = false;
    draw();
}

function Slider( x, y ) {
    this.image = new Image();
    this.image.src = "resources/bonniemslider.png";
    this.knobX = 0;
    this.width = 250;
    this.x = x;
    this.y = y;
}

Slider.prototype.setPosition = function ( pt ) {
    var x = Math.max( 0, pt.x );
    var x2 = Math.min( x, this.width );
    this.knobX = x2;
}

Slider.prototype.draw = function ( context ) {
    //draw gray bar
    context.drawImage( this.image, 20, 24, 1, 9, this.x + 9, this.y + 8, this.width - 18, 9 );

    //draw right cap
    context.drawImage( this.image, 10, 24, 9, 9, this.x + this.width - 9, this.y + 8, 9, 9 );

    // draw left cap
    context.drawImage( this.image, 0, 24, 9, 9, this.x, this.y + 8, 9, 9 );

    // draw blue bar
    if ( this.knobX > 9 ) {
        context.drawImage( this.image, 22, 24, 1, 9, this.x + 9, this.y + 8, this.knobX, 9 );
    }
    // draw control button
    context.drawImage( this.image, 0, 0, 22, 22, this.x + this.knobX - 22 / 2, this.y + 1, 22, 22 );
}

Slider.prototype.containsPoint = function ( location ) {
    return location.x > this.x + this.knobX - 22 &&
           location.x < this.x + this.knobX + 22 / 2 &&
           location.y > this.y &&
           location.y < this.y + 22
}

Slider.prototype.getReferencePoint = function () {
    return new Point2D( this.knobX, this.y );
}