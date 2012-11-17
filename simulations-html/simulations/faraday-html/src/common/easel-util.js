// Copyright 2002-2012, University of Colorado
//TODO This is a subset of SR's file from ESP, eventually move to easel-phet.

define( [], function () {

    var theRelativeDragHandler = function ( e ) {

        //Make dragging relative to touch point
        var relativePressPoint = null;

        e.onMouseMove = function ( event ) {
            var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
            if ( relativePressPoint === null ) {
                relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
            }
            else {
                e.target.x = transformed.x + relativePressPoint.x;
                e.target.y = transformed.y + relativePressPoint.y;
            }
        };

        e.onMouseUp = function ( event ) {
        };
    };

    return {
        relativeDragHandler:theRelativeDragHandler,

        //Makes an object draggable, and uses the cursor hand
        makeDraggable:function ( displayObject ) {

            displayObject.onMouseOver = function () {
                document.body.style.cursor = "pointer";
            };

            displayObject.onMouseOut = function () {
                document.body.style.cursor = "default";
            };

            displayObject.onPress = theRelativeDragHandler;
        }
    };
} );