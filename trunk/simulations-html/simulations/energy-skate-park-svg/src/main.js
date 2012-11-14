var x = 0;
var y = 0;
var touchX = 0;
var touchY = 0;
var width;
var height;
var rotation = 0;
var handlingGesture = false;

//http://techblog.floorplanner.com/post/20528546052/interactive-svg-on-the-ipad

var square = document.getElementById( "square" );
console.log( square );

square.ontouchstart = touchStart;
square.ontouchmove = touchMove;
square.ontouchend = touchEnd;
square.ongesturestart = gestureStart;
square.ongesturechange = gestureChange;
square.ongestureend = gestureEnd;

square.onmousedown = mouseDown;
square.onmousemove = mouseMove;
//square.onmouseup = mouseUp;

function mouseDown( event ) {
    event.preventDefault();

    // one finger touch => start drag
    var node = event.target;

    x = parseInt( node.getAttributeNS( null, 'x' ) );
    y = parseInt( node.getAttributeNS( null, 'y' ) );

    touchX = event.pageX;
    touchY = event.pageY;
    console.log( touchX );
}

function touchStart( event ) {
    event.preventDefault();

    if ( !handlingGesture ) {
        // one finger touch => start drag
        if ( event.touches.length == 1 ) {
            var touch = event.touches[0];
            var node = touch.target;

            x = parseInt( node.getAttributeNS( null, 'x' ) );
            y = parseInt( node.getAttributeNS( null, 'y' ) );

            touchX = touch.pageX;
            touchY = touch.pageY;
        }
    }
}

function mouseMove( event ) {
    event.preventDefault();

    // one finger touch => drag
    var touch = event;
    var node = touch.target;

    var dx = touch.pageX - touchX;
    var dy = touch.pageY - touchY;

    var newX = x + dx;
    var newY = y + dy;

    // set rotation to 0 before moving
//					setRotation(node, 0, newX+width/2, newY+height/2);

    node.setAttributeNS( null, 'x', touch.pageX - 50 );
    node.setAttributeNS( null, 'y', touch.pageY - 50 );

    // restore rotation
//					setRotation(node, rotation, newX+width/2, newY+height/2);
}

function touchMove( event ) {
    event.preventDefault();

    if ( !handlingGesture ) {
        // one finger touch => drag
        if ( event.touches.length == 1 ) {
            var touch = event.touches[0];
            var node = touch.target;

            var dx = touch.pageX - touchX;
            var dy = touch.pageY - touchY;

            var newX = x + dx;
            var newY = y + dy;

            // set rotation to 0 before moving
//					setRotation(node, 0, newX+width/2, newY+height/2);

            node.setAttributeNS( null, 'x', newX );
            node.setAttributeNS( null, 'y', newY );

            // restore rotation
//					setRotation(node, rotation, newX+width/2, newY+height/2);
        }
    }
}

function touchEnd( event ) {
    if ( event.touches.length == 0 ) {
        handlingGesture = false;
    }
}

function gestureStart( event ) {
    event.preventDefault();
    handlingGesture = true;
    var node = event.target;

    x = parseInt( node.getAttributeNS( null, 'x' ) );
    y = parseInt( node.getAttributeNS( null, 'y' ) );

    width = parseInt( node.getAttributeNS( null, 'width' ) );
    height = parseInt( node.getAttributeNS( null, 'height' ) );

    var transform = (node.getAttributeNS( null, 'transform' ));
    rotation = parseInt( transform.split( 'rotate(' )[1].split( ' ' )[0] ); // ouch

    if ( isNaN( rotation ) ) {
        rotation = 0;
    }
}

function gestureChange( event ) {
    event.preventDefault();
    var node = event.target;

    // scale
    var newWidth = width * event.scale;
    var newHeight = height * event.scale;

    var newX = x - (newWidth - width) / 2;
    var newY = y - (newHeight - height) / 2;

    node.setAttributeNS( null, 'width', newWidth );
    node.setAttributeNS( null, 'height', newHeight );
    node.setAttributeNS( null, 'x', newX );
    node.setAttributeNS( null, 'y', newY );

    // rotation
    var newRotation = rotation + event.rotation;
    var centerX = newX + newWidth / 2;
    var centerY = newY + newHeight / 2;
    setRotation( node, newRotation, centerX, centerY );
}

function gestureEnd( event ) {
    rotation = rotation + event.rotation;
}

function setRotation( node, rotation, x, y ) {
    var centerX = x + width / 2;
    var centerY = y + height / 2;

    node.setAttributeNS( null, 'transform', 'rotate(' + rotation + ' ' + x + ' ' + y + ')' );
}

//or another game loop here: http://www.playmycode.com/blog/2011/08/building-a-game-mainloop-in-javascript/
//or here: http://jsfiddle.net/Y9uBv/5/
//See http://stackoverflow.com/questions/5605588/how-to-use-requestanimationframe
var requestAnimationFrame = function () {
    return (
            window.requestAnimationFrame ||
            window.webkitRequestAnimationFrame ||
            window.mozRequestAnimationFrame ||
            window.oRequestAnimationFrame ||
            window.msRequestAnimationFrame ||
            function ( /* function */ callback ) {
                window.setTimeout( callback, 1000 / 60 );
            }
            );
}();


function updatePhysics() {
//    x = parseInt( square.getAttributeNS( null, 'x' ) );
//    y = parseInt( square.getAttributeNS( null, 'y' ) );
    y = y + 3;
//    square.setAttributeNS( null, 'x', x+2 );
    square.setAttributeNS( null, 'y', y );
}

function loop() {
    updatePhysics();
    requestAnimationFrame( loop );
}

requestAnimationFrame( loop );