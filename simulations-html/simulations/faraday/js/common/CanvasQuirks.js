// Copyright 2002-2012, University of Colorado

/**
 * Encapsulates "quirks detection" for HTML Canvas.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [],
        function () {

            function CanvasQuirks() {
            }

            /**
             * Get rid of text cursor by disabling text selection.
             * See http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
             * @param {Canvas} canvas
             */
            CanvasQuirks.fixTextCursor = function ( canvas ) {
                canvas.onselectstart = function () {
                    return false;
                }; // IE
                canvas.onmousedown = function () {
                    return false;
                }; // Mozilla
            }

            return CanvasQuirks;
        } );
