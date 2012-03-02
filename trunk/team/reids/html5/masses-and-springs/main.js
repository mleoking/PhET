// Copyright 2002-2011, University of Colorado
var canvas;
var context;

var rootNode = null;
var globals = {};

function loadImage( string ) {
    var image = new Image();
    image.src = string;
    return image;
}

//Uses width and height for bounds checking
function rectangularNode( width, height ) {
    var that = {x:0, y:0, selected:false};
    that.width = width;
    that.height = height;

    that.onTouchStart = function ( point ) {
        that.selected = point.x >= that.x && point.x <= that.x + that.width && point.y >= that.y && point.y <= that.y + that.height;
        that.initTouchPoint = point;
        that.objectTouchPoint = {x:that.x, y:that.y};

        //flag for consumed
        return that.selected;
    }
    that.onTouchEnd = function ( point ) {
        that.selected = false;
    }
    that.onTouchMove = function ( point ) {
        if ( that.selected ) {
            that.x = point.x + that.objectTouchPoint.x - that.initTouchPoint.x;
            that.y = point.y + that.objectTouchPoint.y - that.initTouchPoint.y;
        }
    }
    return that;
}

function fillRectNode( width, height, style ) {
    var that = rectangularNode( width, height );
    that.draw = function ( context ) {
        context.save();
        context.globalCompositeOperation = "source-over";
        context.fillStyle = style;
        context.fillRect( that.x, that.y, width, height );
        context.restore();
    };
    return that;
}

function textNode( string ) {

    context.save();
    //Context must be initialized for us to determine the width, so only create labels during or after init
    context.fillStyle = '#00f';
    context.font = '30px sans-serif';
    var width = context.measureText( string ).width;
    var that = rectangularNode( width, 30 );

    context.restore();
    that.draw = function ( context ) {
        context.save();
        context.textBaseline = 'top';
        context.fillStyle = '#00f';
        context.font = '30px sans-serif';
        context.fillText( string, that.x, that.y );
        context.restore();
    };
    return that;
}

function imageNode( string, x, y ) {
    var image = loadImage( string );

    var that = rectangularNode( image.width, image.height );
    that.x = x;
    that.y = y;
    that.image = image;
    that.image.onload = function () {
        that.width = that.image.width;
        that.height = that.image.height;
        draw();
    };
    that.draw = function ( context ) {
        context.drawImage( image, that.x, that.y );

        //For debugging
//        if ( that.selected ) {
//            context.fillStyle = '#00f';
//            context.font = '30px sans-serif';
//            context.textBaseline = 'top';
//            context.fillText( 'dragging', that.x, that.y );
//        }
    };
    return that;
}

function massNode( string, _x, _y, _mass ) {
    var that = imageNode( string, _x, _y );
    that.initX = _x;
    that.initY = _y;
    that.mass = _mass;
    that.spring = null;
    that.velocity = 0;

    // Override onTouchStart.
    var superOnTouchStart = that.onTouchStart;
    that.onTouchStart = function ( point ) {
        superOnTouchStart.apply( that, new Array( point ) );
        if ( that.selected ) {
            if ( that.spring != null ) {
                // Detach from the spring.
                that.spring.mass = null;
                that.spring = null;
            }
        }
    }

    // Override onTouchStart.
    var superOnTouchEnd = that.onTouchEnd;
    that.onTouchEnd = function ( point ) {
        if ( that.selected ) {
            superOnTouchEnd.apply( that, new Array( point ) );
            var centerX = that.x + that.width / 2;
            for ( var i = 0; i < globals.springs.length; i++ ) {
                var spring = globals.springs[i];
                if ( new Point2D( centerX, that.y ).distance( spring.attachmentPoint ) < 50 ) {
                    // Attach to this spring.
                    if ( spring.mass == null ) {
                        that.attachToPoint( spring.attachmentPoint );
                        that.spring = spring;
                        spring.mass = that;
                        break;
                    }
                }
            }
        }
    }

    // Set the position so that the "hook" on the weight is at the given location.
    that.attachToPoint = function( point ) {
        that.x = point.x - that.width / 2;
        that.y = point.y - 5; // Tweak alert - this is about the width of the hook on each mass.
    }

    return that;
}

function vbox( args ) {
    var that = compositeNode( args.children );
    that.x = args.x;
    that.y = args.y;

    function vertical( components, spacing ) {
        components[0].x = 0;
        components[0].y = 0;
        for ( var i = 1; i < components.length; i++ ) {
            var prev = components[i - 1];
            var c = components[i];
            c.y = prev.y + prev.height + spacing;
            c.x = prev.x;

            if ( isNaN( c.y ) ) {
                console.log( "not a number" );
            }
        }
    }

    vertical( args.children, 10 );

    //compute width and height
    that.width = 0;
    that.height = args.children[args.children.length - 1].y + args.children[args.children.length - 1].height;
    for ( var i = 0; i < args.children.length; i++ ) {
        var obj = args.children[i];
        that.width = Math.max( obj.width, that.width );
    }

    return that;
}

//Varargs variant for creating vbox at 0,0
function vbox00() {
    var array = new Array();
    for ( var i = 0; i < arguments.length; i++ ) {
        array.push( arguments[i] );
    }
    return vbox( {children:array, x:0, y:0} );
}

//Varargs variant for creating hbox at 0,0
function hbox00() {
    var array = new Array();
    for ( var i = 0; i < arguments.length; i++ ) {
        array.push( arguments[i] );
    }
    return hbox( {children:array, x:0, y:0} );
}

function hbox( args ) {
    var that = compositeNode( args.children );
    that.x = args.x;
    that.y = args.y;

    function horizontal( components, spacing ) {
        components[0].x = 0;
        components[0].y = 0;
        for ( var i = 1; i < components.length; i++ ) {
            var prev = components[i - 1];
            var c = components[i];
            c.x = prev.x + prev.width + spacing;
            c.y = prev.y;
        }
    }

    horizontal( args.children, 10 );
    if ( isNaN( that.y ) ) {
        console.log( "not a number" );
    }

    //compute width and height
    that.height = 0;
    that.width = args.children[args.children.length - 1].x + args.children[args.children.length - 1].width;
    for ( var i = 0; i < args.children.length; i++ ) {
        var obj = args.children[i];
        that.height = Math.max( obj.height, that.height );
    }

    console.log( "hbox height = " + that.height );

    return that;
}

function compositeNode( children ) {
    var that = {
        x:0,
        y:0
    };

    that.onTouchEnd = function ( point ) {
        var relativePoint = {x:point.x - that.x, y:point.y - that.y};
        for ( var i = 0; i < children.length; i++ ) {
            children[i].onTouchEnd( relativePoint );
        }
    };
    that.onTouchMove = function ( point ) {
        var relativePoint = {x:point.x - that.x, y:point.y - that.y};
        for ( var i = 0; i < children.length; i++ ) {
            children[i].onTouchMove( relativePoint );
        }
    };

    //Define these methods outside of initial that declaration so we can refer to that. (Maybe a better way to do that)
    that.onTouchStart = function ( point ) {

        var relativePoint = {x:point.x - that.x, y:point.y - that.y};
        //Reverse order so things in front will consume the event
        for ( var i = children.length - 1; i >= 0; i-- ) {
            var consumed = children[i].onTouchStart( relativePoint );
            if ( consumed ) {

                var child = children[i];

                //Move to front (i.e. end of list) by default
                children.splice( i, 1 );
                children.push( child );

                break;
            }
        }
    }
    that.draw = function ( context ) {
        context.save();
        context.translate( that.x, that.y );
        for ( var i = 0; i < children.length; i++ ) {
            children[i].draw( context );
        }
        context.restore();
    };
    return that;
}

// Hook up the initialization function.
$( document ).ready( function () {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );


//Non-interactive spacer for layouts
function spacer() {
    var that = rectangularNode( 30, 30 );
    that.draw = function ( context ) {
    }
    that.onTouchStart = function ( point ) {
    }
    return that;
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

    // Disable elastic scrolling.  This is specific to iOS.
    document.addEventListener(
            'touchmove',
            function ( e ) {
                e.preventDefault();
            },
            false
    );

    globals.springs = new Array();
    var springSpacing = 180;
    var springOffset = 200;
    globals.springs.push( spring( "1", springOffset ) );
    globals.springs.push( spring( "2", springOffset + springSpacing * 1 ) );
    globals.springs.push( spring( "3", springOffset + springSpacing * 2 ) );

    globals.masses = new Array( massNode( "resources/red-mass.png", 114, 496, 3 ),
                                massNode( "resources/green-mass.png", 210, 577, 4 ),
                                massNode( "resources/gold-mass.png", 276, 541, 5 ),
                                massNode( "resources/gram-50.png", 577, 590, 6 ),
                                massNode( "resources/gram-100.png", 392, 562, 7 ),
                                massNode( "resources/gram-250.png", 465, 513, 8 ) )

    function labeledCheckBox( label ) {
        return hbox00( checkbox( 0, 0 ), textNode( label ) );
    }

//    var stopwatchCheckBox = labeledCheckBox( "Stopwatch" );
//    var soundCheckBox = labeledCheckBox( "Sound" );
    var frictionSlider = vbox00( textNode( "friction" ), slider() );
    var resetButton = new ResetButton( new Point2D( 650 + 180 - 25, 700 - 40 ), "orange" );

//    var oneTwoThree = hbox00( labeledCheckBox( "1" ), labeledCheckBox( "2" ), labeledCheckBox( "3" ) );
//    var showEnergyBox = vbox00( textNode( "Show Energy of" ), oneTwoThree, labeledCheckBox( "No show" ) );

    var rootNodeComponents = new Array();
    for ( var i = 0; i < globals.masses.length; i++ ) {
        rootNodeComponents.push( globals.masses[i] );
    }
    rootNodeComponents.push( vbox( {children:new Array( frictionSlider ), x:700, y:100} ) );
    rootNodeComponents.push( resetButton );
    rootNodeComponents.push( imageNode( "resources/ruler.png", 12, 51 ) );

    //Add to the nodes for rendering
    for ( var i = 0; i < globals.springs.length; i++ ) {
        rootNodeComponents.push( globals.springs[i] );
    }

    rootNode = compositeNode( rootNodeComponents );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();

    // Start the game loop
    animate();
}

// Handler for window resize events.
function resizer() {
    canvas.width = $( window ).width();
    canvas.height = $( window ).height();
    draw();
}

function clearBackground() {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}
// Main drawing function.
function draw() {
    clearBackground();
    if ( rootNode != null ) {
        rootNode.draw( context );
    }
}

function onDocumentMouseDown( event ) {
    onTouchStart( {x:event.clientX, y:event.clientY} );
}

function onDocumentMouseUp( event ) {
    onTouchEnd( {x:event.clientX, y:event.clientY} );
}

function onDocumentMouseMove( event ) {
    onDrag( {x:event.clientX, y:event.clientY} );
}

function onDocumentTouchStart( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onTouchStart( {x:event.touches[0].pageX, y:event.touches[0].pageY} );
    }
}

function onDocumentTouchMove( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onDrag( {x:event.touches[0].pageX, y:event.touches[0].pageY} );
    }
}

function onDocumentTouchEnd( event ) {
    onTouchEnd( {x:event.clientX, y:event.clientY} );
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    rootNode.onTouchStart( location );
}

function onDrag( location ) {
    rootNode.onTouchMove( location );
    draw();
}

function onTouchEnd( point ) {
    rootNode.onTouchEnd( point );
    draw();
}

var count = 0;
var prevTime = new Date().getTime();
;
function animate() {

    var currentTime = new Date().getTime();
    var dt = ( currentTime - prevTime ) / 1000.0; // Delta time in seconds.
    prevTime = currentTime;

    //http://animaljoy.com/?p=254
    // insert your code to update your animation here

//    for ( i = 0; i < globals.springs.length; i++ ) {
//        globals.springs[i].attachmentPoint.y = 300 + 100 * Math.sin( 6 * count / 100.0 );
//    }

    for ( var i = 0; i < globals.masses.length; i++ ) {
        var mass = globals.masses[i];
        if ( !mass.selected ) {
            if ( mass.spring == null ) {
                // If mass is above the ground, it should fall.
                if ( mass.y <= mass.initY ) {
                    mass.y += 10;
                }
                if ( mass.y > mass.initY ) {
                    mass.y = mass.initY;
                }
            }
            else{
                // Mass is attached to spring, so update its velocity and position.
                mass.velocity = mass.velocity + 9.8 * dt;
                mass.y = mass.y + mass.velocity * dt;
            }
        }
    }
    count++;
    requestAnimationFrame( animate );
    draw();
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

function spring( name, x ) {
    var that = rectangularNode( 0, 0 );
    that.name = name;
    that.anchor = new Point2D( x, 50 );
    that.attachmentPoint = new Point2D( x, 250 );
    that.color = '#f00';
    that.mass = null;
    that.draw = function ( context ) {
        context.beginPath();
        context.strokeStyle = that.color;
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
        context.fillText( this.name, this.anchor.x, this.anchor.y - 40, 1000 );
        context.textAlign = defaultTextAlign;
    }
    return that;
}

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

Point2D.prototype.minus = function ( pt ) {
    return new Point2D( this.x + pt.x, this.y + pt.y );
}

Point2D.prototype.set = function ( point2D ) {
    this.setComponents( point2D.x, point2D.y );
}

Point2D.prototype.distance = function ( point2D ) {
    return ( Math.sqrt( Math.pow( this.x - point2D.x, 2 ) + Math.pow( this.y - point2D.y, 2 ) ) );
}


function sliderTrack() {
    var that = rectangularNode( 250, 5 );
    that.knobX = 0;
    that.image = new Image();
    that.image.src = "resources/bonniemslider.png";

    that.draw = function ( context ) {
        //draw gray bar
        context.drawImage( that.image, 20, 24, 1, 9, that.x + 9, that.y + 8, that.width - 18, 9 );

        //draw right cap
        context.drawImage( that.image, 10, 24, 9, 9, that.x + that.width - 9, that.y + 8, 9, 9 );

        // draw left cap
        context.drawImage( that.image, 0, 24, 9, 9, that.x, that.y + 8, 9, 9 );

        // draw blue bar
        if ( that.knobX > 9 ) {
            context.drawImage( that.image, 22, 24, 1, 9, that.x + 9, that.y + 8, that.knobX, 9 );
        }
    };
    return that;
}

function checkbox( x, y ) {
    var that = rectangularNode( 30, 30 );
    that.x = x;
    that.y = y;
    that.checkboxSelected = true;
    that.draw = function ( context ) {
        context.fillStyle = '#fff';
        context.strokeStyle = '#88e';
        context.lineWidth = 2;
        roundRect( context, this.x, this.y, this.width, this.height, 7, true, true )

        context.strokeStyle = '#000';
        context.lineWidth = 4;
        if ( this.checkboxSelected ) {
            context.beginPath();
            context.beginPath();
            context.strokeStyle = '#222';
            context.moveTo( this.x + 5, this.y + 5 );
            context.lineTo( this.x + this.width - 5, this.y + this.width - 5 );
            context.moveTo( this.x + this.width - 5, this.y + 5 );
            context.lineTo( this.x + 5, this.y + this.height - 5 );
            context.stroke();
            context.closePath();
        }
    }

    that.onTouchStart = function ( point ) {

        //todo factor out
        var contains = point.x >= that.x && point.x <= that.x + that.width && point.y >= that.y && point.y <= that.y + that.height;
        if ( contains ) {
            this.checkboxSelected = !this.checkboxSelected;
            draw();
        }
    }
    return that;
}

/**
 * Draws a rounded rectangle using the current state of the canvas.
 * If you omit the last three params, it will draw a rectangle
 * outline with a 5 pixel border radius
 * @param {CanvasRenderingContext2D} ctx
 * @param {Number} x The top left x coordinate
 * @param {Number} y The top left y coordinate
 * @param {Number} width The width of the rectangle
 * @param {Number} height The height of the rectangle
 * @param {Number} radius The corner radius. Defaults to 5;
 * @param {Boolean} fill Whether to fill the rectangle. Defaults to false.
 * @param {Boolean} stroke Whether to stroke the rectangle. Defaults to true.
 *
 * @author http://js-bits.blogspot.com/2010/07/canvas-rounded-corner-rectangles.html
 */
function roundRect( ctx, x, y, width, height, radius, fill, stroke ) {
    if ( typeof stroke == "undefined" ) {
        stroke = true;
    }
    if ( typeof radius === "undefined" ) {
        radius = 5;
    }
    ctx.beginPath();
    ctx.moveTo( x + radius, y );
    ctx.lineTo( x + width - radius, y );
    ctx.quadraticCurveTo( x + width, y, x + width, y + radius );
    ctx.lineTo( x + width, y + height - radius );
    ctx.quadraticCurveTo( x + width, y + height, x + width - radius, y + height );
    ctx.lineTo( x + radius, y + height );
    ctx.quadraticCurveTo( x, y + height, x, y + height - radius );
    ctx.lineTo( x, y + radius );
    ctx.quadraticCurveTo( x, y, x + radius, y );
    ctx.closePath();
    if ( stroke ) {
        ctx.stroke();
    }
    if ( fill ) {
        ctx.fill();
    }
}

function clamp( min, value, max ) {
    var minClamp = Math.min( value, max );
    var maxClamp = Math.max( min, minClamp );
    return maxClamp;
}

function slider() {
    var track = sliderTrack();
    track.onTouchmove = function ( point ) {
    };
    var knob = imageNode( "resources/bonniemsliderthumb.png", 0, 0 );
    var that = compositeNode( new Array( track, knob ) );

    knob.onTouchMove = function ( point ) {
        if ( knob.selected ) {
            knob.x = clamp( 0, point.x + knob.objectTouchPoint.x - knob.initTouchPoint.x, track.width );
            track.knobX = knob.x;
        }
    };

    //compute width and height
    that.width = track.width;
    that.height = knob.height;
    return that;
}


//-----------------------------------------------------------------------------
// Reset button class
//-----------------------------------------------------------------------------

function ResetButton( initialLocation, color ) {
    this.location = initialLocation;
    this.width = 90;
    this.height = 40;
    this.color = color;
    this.pressed = false;
}

ResetButton.prototype.draw = function ( context ) {
    var xPos = this.location.x;
    var yPos = this.location.y;
    var gradient = context.createLinearGradient( xPos, yPos, xPos, yPos + this.height );
    if ( !this.pressed ) {
        gradient.addColorStop( 0, "white" );
        gradient.addColorStop( 1, this.color );
    }
    else {
        gradient.addColorStop( 0, this.color );
    }
    // Draw box that defines button outline.
    context.strokeStyle = '#222'; // Gray
    context.lineWidth = 1;
    context.strokeRect( xPos, yPos, this.width, this.height );
    context.fillStyle = gradient;
    context.fillRect( xPos, yPos, this.width, this.height );
    // Put text on the box.
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.textAlign = 'left';
    context.fillText( 'Reset', xPos + 5, yPos + 5 );
}

ResetButton.prototype.onTouchStart = function ( pt ) {
    if ( this.containsPoint( pt ) ) {
        this.pressed = true;
//        reset();
    }
}

ResetButton.prototype.onTouchEnd = function ( pt ) {
    this.pressed = false;
}
ResetButton.prototype.onTouchMove = function ( pt ) {
    this.pressed = false;
}

ResetButton.prototype.press = function () {
    this.pressed = true;
    reset();
}

ResetButton.prototype.unPress = function ( context ) {
    this.pressed = false;
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
