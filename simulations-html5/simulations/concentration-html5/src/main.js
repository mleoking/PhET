// Copyright 2002-2011, University of Colorado
var canvas;
var context;

var globals = {
    MAX_FRICTION:1.5, friction:0
};

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
    };
    that.onTouchEnd = function ( point ) {
        that.selected = false;
    };
    that.onTouchMove = function ( point ) {
        if ( that.selected ) {
            that.x = point.x + that.objectTouchPoint.x - that.initTouchPoint.x;
            that.y = point.y + that.objectTouchPoint.y - that.initTouchPoint.y;
        }
    };
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

// Hook up the initialization function.
$( document ).ready( function () {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );


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

    globals.masses = [];

    function labeledCheckBox( label ) {
        return hbox00( checkbox( 0, 0 ), textNode( label ) );
    }

    var resetButton = new ResetButton( new Point2D( 740, 530 ), "orange" );

    var rootNodeComponents = new Array();
    for ( var i = 0; i < globals.masses.length; i++ ) {
        rootNodeComponents.push( globals.masses[i] );
    }
    rootNodeComponents.push( resetButton );

    shakerImage = loadImage( "resources/shaker.png" );
    shakerImage.onload = function () {
        draw();
    };

    // Do the initial drawing, events will cause subsequent updates.
    resizer();

    // Start the animation loop
    animate();
}

// Handler for window resize events.
function resizer() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight ? window.innerHeight : $( window ).height();
    console.log( "resize event, width = " + canvas.width + ", height = " + canvas.height );
    draw();
}

// Reset the sim to the initial state.
function reset() {
    for ( var i = 0; i < globals.masses.length; i++ ) {
        var mass = globals.masses[i];
        mass.spring = null;
        mass.x = mass.initX;
        mass.y = mass.initY;
        mass.velocity = 0;
    }
    for ( var i = 0; i < globals.springs.length; i++ ) {
        var spring = globals.springs[i];
        spring.mass = null;
    }
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
    updateCanvasSize();
    clearBackground();

    context.save();
    context.translate( 400, 200 );
    context.rotate( -Math.PI / 4 );
    context.drawImage( shakerImage, 0, 0 );
    context.restore();

//    if ( rootNode != null ) {
//        rootNode.draw( context );
//    }
}

var updateCanvasSize = function () {
    if ( window.innerWidth != canvas.width || window.innerHeight != canvas.height ) {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight ? window.innerHeight : $( window ).height(); // Workaround for iPad issues.
        window.scrollTo( 0, 0 );
    }
};

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
//    rootNode.onTouchStart( location );
}

function onDrag( location ) {
    //see if the shaker image hits this point
    var point = new Point2D( location.x, location.y );
    point = point.plus( -400, -200 );
    point = point.rotate( Math.PI / 4 );
    var inside = point.x > 0 && point.x < shakerImage.width && point.y > 0 && point.y < shakerImage.height;
    console.log( "inside = " + inside + ", transformed point = " + point.x + ", " + point.y );
    if ( inside ) {
        canvas.style.cursor = "pointer";
    }
    else {
        canvas.style.cursor = "";
    }

    draw();
}

function onTouchEnd( point ) {
//    rootNode.onTouchEnd( point );
    draw();
}

var count = 0;
var prevTime = new Date().getTime();

function animate() {

    var currentTime = new Date().getTime();
    var dt = ( currentTime - prevTime ) / 1000.0; // Delta time in seconds.
    prevTime = currentTime;

    //http://animaljoy.com/?p=254
    // insert your code to update your animation here

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
            else {
                // Mass is attached to spring, so update its velocity and position.
                var delta = mass.mass * 9.8 / mass.spring.k;
                //console.log("delta: "+delta);
                var equilibriumPoint = mass.spring.initialLength + (delta);
                var displacement = mass.y - equilibriumPoint;
                var springForce = -mass.spring.k * displacement;
//                console.log("spring force: "+springForce+" displacement = "+displacement)
                var gravityForce = 9.8 * 100;
                var frictionForce = -mass.velocity * globals.friction;
                var totalForce = springForce + gravityForce + frictionForce;
                mass.velocity = mass.velocity + totalForce * dt;
                mass.y = mass.y + mass.velocity * dt;
                mass.spring.attachmentPoint.y = mass.y;
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
    window.requestAnimationFrame = (function () {
        return window.webkitRequestAnimationFrame ||
               window.mozRequestAnimationFrame ||
               window.oRequestAnimationFrame ||
               window.msRequestAnimationFrame ||
               function ( /* function FrameRequestCallback */ callback, /* DOMElement Element */ element ) {
                   window.setTimeout( callback, 1000 / 60 );
               };
    })();
}

function Point2D( x, y ) {
    // Instance Fields or Data Members
    this.x = x;
    this.y = y;
}

Point2D.prototype.toString = function () {
    return this.x + ", " + this.y;
};

Point2D.prototype.setComponents = function ( x, y ) {
    this.x = x;
    this.y = y;
};

Point2D.prototype.minus = function ( pt ) {
    return new Point2D( this.x - pt.x, this.y - pt.y );
};

Point2D.prototype.plus = function ( dx, dy ) {
    return new Point2D( this.x + dx, this.y + dy );
};

Point2D.prototype.rotate = function ( angle ) {
    var currentAngle = Math.atan2( this.y, this.x );
    var newAngle = currentAngle + angle;
    var dist = this.distance( new Point2D( 0, 0 ) );
    return new Point2D( Math.cos( newAngle ) * dist, Math.sin( newAngle ) * dist );
};

Point2D.prototype.minus = function ( pt ) {
    return new Point2D( this.x + pt.x, this.y + pt.y );
};

Point2D.prototype.set = function ( point2D ) {
    this.setComponents( point2D.x, point2D.y );
};

Point2D.prototype.distance = function ( point2D ) {
    return ( Math.sqrt( Math.pow( this.x - point2D.x, 2 ) + Math.pow( this.y - point2D.y, 2 ) ) );
};

function sliderTrack() {
    var that = rectangularNode( 250, 5 );
    that.knobX = 0;
    that.image = new Image();
//    that.image.src = "resources/bonniemslider.png";

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
    };

    that.onTouchStart = function ( point ) {

        //todo factor out
        var contains = point.x >= that.x && point.x <= that.x + that.width && point.y >= that.y && point.y <= that.y + that.height;
        if ( contains ) {
            this.checkboxSelected = !this.checkboxSelected;
            draw();
        }
    };
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
};

ResetButton.prototype.onTouchStart = function ( pt ) {
    if ( this.containsPoint( pt ) ) {
        this.pressed = true;
        reset();
    }
};

ResetButton.prototype.onTouchEnd = function ( pt ) {
    this.pressed = false;
};
ResetButton.prototype.onTouchMove = function ( pt ) {
    this.pressed = false;
};

ResetButton.prototype.press = function () {
    this.pressed = true;
    reset();
};

ResetButton.prototype.unPress = function ( context ) {
    this.pressed = false;
};

ResetButton.prototype.setLocationComponents = function ( x, y ) {
    this.location.x = x;
    this.location.y = y;
};

ResetButton.prototype.setLocation = function ( location ) {
    this.setLocationComponents( location.x, location.y );
};

ResetButton.prototype.containsPoint = function ( point ) {
    return point.x > this.location.x && point.x < this.location.x + this.width &&
           point.y > this.location.y && point.y < this.location.y + this.height;
};
