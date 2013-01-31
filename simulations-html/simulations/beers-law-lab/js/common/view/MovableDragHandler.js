// Copyright 2002-2013, University of Colorado

/**
 * A drag handler for something that is movable and constrained to some bounds.
 * All values herein are in the view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'phetcommon/math/Point2D'
        ],
        function ( Point2D ) {

            function MovableDragHandler() {
            }

            /**
             * Registers a drag handler with the specified Easel display object.
             * @param {DisplayObject} dragNode
             * @param {Rectangle} dragBounds
             * @param {Function} dragFunction
             */
            MovableDragHandler.register = function ( dragNode, dragBounds, dragFunction ) {

                // Drag cursor
                dragNode.onMouseOver = function () {
                    document.body.style.cursor = "pointer";
                };

                // Normal cursor
                dragNode.onMouseOut = function () {
                    document.body.style.cursor = "default";
                };

                // @param {MouseEvent} pressEvent
                dragNode.onPress = function ( pressEvent ) {

                    // Make dragging relative to touch Point2D.
                    var relativePressPoint = null;

                    // @param {MouseEvent} moveEvent
                    pressEvent.onMouseMove = function ( moveEvent ) {
                        var transformed = moveEvent.target.parent.globalToLocal( moveEvent.stageX, moveEvent.stageY );
                        if ( relativePressPoint === null ) {
                            relativePressPoint = new Point2D( pressEvent.target.x - transformed.x, pressEvent.target.y - transformed.y );
                        }
                        else {
                            var p = new Point2D( transformed.x + relativePressPoint.x, transformed.y + relativePressPoint.y );
                            dragFunction( MovableDragHandler.constrainBounds( p, dragBounds ) );
                        }
                    };
                };
            };

            /**
             * Constrains a point to some bounds.
             * @param {Point2D} point
             * @param {Rectangle} bounds
             */
            MovableDragHandler.constrainBounds = function ( point, bounds ) {
                if ( bounds === undefined || bounds.contains( point.x, point.y ) ) {
                    return point;
                }
                else {
                    var xConstrained = Math.max( Math.min( x, bounds.getMaxX() ), bounds.x );
                    var yConstrained = Math.max( Math.min( y, bounds.getMaxY() ), bounds.y );
                    return new Point2D( xConstrained, yConstrained );
                }
            };

            return MovableDragHandler;
        } );
