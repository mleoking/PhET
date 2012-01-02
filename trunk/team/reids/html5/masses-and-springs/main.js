// Copyright 2002-2011, University of Colorado
var touchInProgress = false;
var canvas;
var context;
//var globals = new Array();
var dragTarget = null;

var rootNode = null;

function clearBackground( context ) {
    context.save();
    context.globalCompositeOperation = "source-over";
    context.fillStyle = "rgb(255, 255, 153)";
    context.fillRect( 0, 0, canvas.width, canvas.height );
    context.restore();
}

function node( params ) {
    return {draw:params.draw};
}

//Returns a function that gives the drawing function but translated
function translate( drawingFunction, x, y ) {
    return function ( context ) {
        context.save();
        context.translate( x, y );
        drawingFunction( context );
        context.restore();
    };
}

function compositeNode( children ) {
    return {
        draw:function ( context ) {
            for ( var i = 0; i < children.length; i++ ) {
                children[i].draw( context );
            }
        },
        onTouchStart:function ( point ) {

            for ( var i = 0; i < children.length; i++ ) {
                children[i].draw( context );
            }

            javascript: console.log( "onTouchStart: " + point.x + ", " + point.y );

            touchInProgress = true;
            var result = rootNode.findTarget( location );
            javascript: console.log( "result = " + result );
            if ( result != null && result.dragTarget != null ) {
                dragTarget = result.dragTarget;
                pickPath = globals.rootNode.getPickPath( dragTarget );
                relativeGrabPoint = result.relativeGrabPoint;
                dragTarget.onTouchStart();
                draw();
            }

        }};
}

//Functional way; a node has a draw, bounds and interaction functions.  Or is a composite of those methods.

//function Node( children ) {
//    this.draw = function ( context ) {
//
//    };
//}

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

//    globals.springs = new Array();
//    globals.springs.push( new Spring( "1", 200 ) );
//    globals.springs.push( new Spring( "2", 300 ) );
//    globals.springs.push( new Spring( "3", 400 ) );

//    var rootComponents = new Array(
//            new ImageSprite( "resources/red-mass.png", 114, 496 ),
//            new ImageSprite( "resources/green-mass.png", 210, 577 ),
//            new ImageSprite( "resources/gold-mass.png", 276, 541 ),
//            new ImageSprite( "resources/gram-50.png", 577, 590 ),
//            new ImageSprite( "resources/gram-100.png", 392, 562 ),
//            new ImageSprite( "resources/gram-250.png", 465, 513 ),
//            new ImageSprite( "resources/ruler.png", 12, 51 ),
//            new Node( { components:new Array( new Label( "Friction" ), new SliderKnob( 0, 0 ) ), x:700, y:80, layout:vertical} ),
//            new Node( { components:new Array( new Label( "Spring 3 Smoothness" ), new SliderKnob( 0, 0 ) ), x:700, y:150, layout:vertical} ),
//            new Node( { components:new Array( new CheckBox(), new Label( "Stopwatch" ) ), x:700, y:300, layout:horizontal} ),
//            new Node( { components:new Array( new CheckBox(), new Label( "Sound" ) ), x:700, y:350, layout:horizontal} )
//    );
//
//    //Add to the nodes for rendering
//    for ( var i = 0; i < globals.springs.length; i++ ) {
//        rootComponents.push( globals.springs[i] );
//    }
//    globals.rootNode = new Node( { components:rootComponents, x:0, y:0, layout:absolute } );

    //Init label components after canvas non-null
//    nodes.push( new BoxNode( { components:new Array( new Label( "Friction" ), new Label( "other label" ) ), x:700, y:200, layout:horizontal} ) );

    function loadImage( string ) {
        var image = new Image();
        image.src = string;
        //Repaint the screen when this image got loaded
        image.onload = function () {
            draw();
        }
        return image;
    }


    //Function that returns a function for rending from an image
    function drawImageFromString( string ) {
        var image = loadImage( string );
        return function drawImage( context ) {
            context.drawImage( image, 0, 0 );
        };
    }

    rootNode = compositeNode( new Array( node( {draw:clearBackground} ),
                                         node( {draw:drawImageFromString( "resources/red-mass.png" )} ),
                                         node( {draw:translate( drawImageFromString( "resources/green-mass.png" ), 100, 100 )} )
    ) );

    // Do the initial drawing, events will cause subsequent updates.
    resizer();

    // Start the game loop
    animate();
}

// Handler for window resize events.
function resizer() {
    console.log( "resize received" );
    canvas.width = $( window ).width();
    canvas.height = $( window ).height();
    draw();
}

// Main drawing function.
function draw() {
    rootNode.draw( context );
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
    onTouchEnd();
}

function onWindowDeviceOrientation( event ) {
    console.log( "onWindowDeviceOrientation" );
}

function onTouchStart( location ) {
    rootNode.onTouchStart( location );
}

function onDrag( location ) {
    if ( touchInProgress && dragTarget != null ) {
        var position = location.minus( relativeGrabPoint );

        //TODO: Convert global to local position
        for ( var i = 0; i < pickPath.length; i++ ) {
            position.x = position.x - pickPath[i].x;
            position.y = position.y - pickPath[i].y;
        }
        dragTarget.setPosition( position );
        javascript: console.log( "dragging to " + position );
        draw();
    }
}

function onTouchEnd() {
    touchInProgress = false;
    dragTarget = null;
    relativeGrabPoint = null;
    draw();
}

var count = 0;
function animate() {

    //http://animaljoy.com/?p=254
    // insert your code to update your animation here

//    for ( i = 0; i < globals.springs.length; i++ ) {
//        globals.springs[i].attachmentPoint.y = 300 + 100 * Math.sin( 6 * count / 100.0 );
//    }
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