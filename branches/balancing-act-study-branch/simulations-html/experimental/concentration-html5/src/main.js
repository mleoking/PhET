// Copyright 2002-2011, University of Colorado
var canvas;
var context;

var globals = {
    MAX_FRICTION:1.5, friction:0
};

//Should be used with a fully loaded image.
function imageNode( _image, x, y, angle ) {
    var that = {image:_image};
    that.offset = new Point2D( x, y );
    that.angle = angle;
    that.draw = function ( ctx ) {
        context.translate( that.offset.x, that.offset.y );
        context.rotate( that.angle );
        context.drawImage( _image, 0, 0 );
    };
    that.contains = function ( point ) {
        point = point.plus( -that.offset.x, -that.offset.y );
        point = point.rotate( -that.angle );
        return point.x > 0 && point.x < that.image.width && point.y > 0 && point.y < that.image.height;
    };
    that.translate = function ( dx, dy ) {
        that.offset = new Point2D( that.offset.x + dx, that.offset.y + dy );
    };
    return that;
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

// Hook up the initialization function.
$( document ).ready( function () {
    init();
} );

// Hook up event handler for window resize.
$( window ).resize( resizer );


// Initialize the canvas, context,
function init() {

    var debuggingArea = $( '#debugging' );
    debuggingArea[0].onselectstart = function () {
        return false;
    };
    debuggingArea.html( "This is using jQuery to set it " );

    // Initialize references to the HTML5 canvas and its context.
    canvas = $( '#canvas' )[0];
    if ( canvas.getContext ) {
        context = canvas.getContext( '2d' );
    }

    // Set up event handlers.
    // TODO: Work with JO to "jquery-ize".
    document.onmousedown = onDocumentMouseDown;
    document.onmouseup = onDocumentMouseUp;
//    document.onmousemove = onDocumentMouseMove;

    var touchFromJQueryEvent = function ( evt ) {
        return evt.originalEvent.targetTouches[0];
    };

    $( canvas ).bind( "mousemove", function ( evt ) {
        evt.preventDefault();
        onTouchMove( {x:evt.pageX, y:evt.pageY} );
    } );
    $( canvas ).bind( "touchmove", function ( evt ) {
        evt.preventDefault();
        var touch = touchFromJQueryEvent( evt );
        onTouchMove( {x:touch.pageX, y:touch.pageY } );
    } );

    document.addEventListener( 'touchstart', onDocumentTouchStart, false );
//    document.addEventListener( 'touchmove', onDocumentTouchMove, false );
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

    var shakerImage = new Image();
    shakerImage.onload = function () {
        globals.shakerNode = imageNode( shakerImage, 400, 200, -Math.PI / 4 );
        console.log( "loaded image" );
    };
    shakerImage.src = "resources/shaker.png";

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
//    console.log( "shakernode = " + globals.shakerNode );
    if ( globals.shakerNode != null ) {
        globals.shakerNode.draw( context );
    }
    context.restore();

    context.save();
    context.fillStyle = '#000';
    context.font = '28px sans-serif';
    context.textBaseline = 'top';
    context.textAlign = 'left';
    if ( globals.shakerNode != null ) {
        context.fillText( "x = " + globals.shakerNode.offset.x + ", y = " + globals.shakerNode.offset.y, 100, 100 );
    }

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

//function onDocumentMouseMove( event ) {
//    onTouchMove( {x:event.clientX, y:event.clientY} );
//}

function onDocumentTouchStart( event ) {
    if ( event.touches.length == 1 ) {

        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
        event.preventDefault();
        onTouchStart( {x:event.touches[0].pageX, y:event.touches[0].pageY} );
    }
}

//function onDocumentTouchMove( event ) {
//    if ( event.touches.length == 1 ) {
//
//        //in the  the event handler to prevent the event from being propagated to the browser and causing unwanted scrolling events.
//        event.preventDefault();
//        onTouchMove( {x:event.touches[0].pageX, y:event.touches[0].pageY} );
//    }
//}

function onDocumentTouchEnd( event ) {
    onTouchEnd( {x:event.clientX, y:event.clientY} );
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
//    rootNode.onTouchStart( location );
    globals.pressed = true;
    if ( globals.shakerNode != null && globals.shakerNode.contains( new Point2D( location.x, location.y ) ) ) {
        globals.shakerNode.pressed = true;
        globals.shakerNode.relativePressPoint = new Point2D( globals.shakerNode.offset.x - location.x, globals.shakerNode.offset.y - location.y );
        globals.lastPoint = new Point2D( location.x, location.y );
    }
}

function onTouchMove( location ) {
    //see if the shaker image hits this point
    var point = new Point2D( location.x, location.y );

    //Testing to see if this spurious value can be avoided, problematic on ipad and android but not on desktop
//    if ( point.x === 0 && point.y === 0 ) {
//        return;
//    }

    if ( globals.shakerNode != null && globals.shakerNode.contains( point ) ) {
        canvas.style.cursor = "pointer";
    }
    else {
        canvas.style.cursor = "";
    }

    if ( globals.shakerNode != null && globals.shakerNode.pressed == true ) {
//        var delta = new Point2D( location.x - globals.lastPoint.x, location.y - globals.lastPoint.y );
//        console.log( "delta.x= " + delta + ", delta.y=" + delta.y );
//        globals.shakerNode.translate( delta.x, delta.y );
        globals.shakerNode.offset = new Point2D( location.x + globals.shakerNode.relativePressPoint.x, location.y + globals.shakerNode.relativePressPoint.y );
    }

    draw();
    globals.lastPoint = new Point2D( location.x, location.y );
}

function onTouchEnd( point ) {
    globals.pressed = false;
    if ( globals.shakerNode != null ) {
        globals.shakerNode.pressed = false;
    }
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