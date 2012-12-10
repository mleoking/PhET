// Copyright 2002-2012, University of Colorado

/**
 * A generalized drag handler for DisplayObjects.
 *
 * @author Sam Reid
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'phetcommon/math/Point2D'
        ],
        function ( Point2D ) {

            function DragHandler() {
            }

            /**
             * Registers a drag handler with the specified Easel display object.
             * @param {DisplayObject} displayObject
             * @param {Function} dragFunction function called while dragging, params: {Point2D}
             */
            DragHandler.register = function ( displayObject, dragFunction ) {

                // Drag cursor
                displayObject.onMouseOver = function () {
                    document.body.style.cursor = "pointer";
                };

                // Normal cursor
                displayObject.onMouseOut = function () {
                    document.body.style.cursor = "default";
                };

                // @param {MouseEvent} pressEvent
                displayObject.onPress = function ( pressEvent ) {

                    // Make dragging relative to touch Point2D.
                    var relativePressPoint = null;

                    // @param {MouseEvent} moveEvent
                    pressEvent.onMouseMove = function ( moveEvent ) {
                        var transformed = moveEvent.target.parent.globalToLocal( moveEvent.stageX, moveEvent.stageY );
                        if ( relativePressPoint === null ) {
                            relativePressPoint = new Point2D( pressEvent.target.x - transformed.x, pressEvent.target.y - transformed.y );
                        }
                        else {
                            dragFunction( new Point2D( transformed.x + relativePressPoint.x, transformed.y + relativePressPoint.y ) );
                        }
                    };
                };
            };

            //TODO implement unregister

            return DragHandler;
        } );
