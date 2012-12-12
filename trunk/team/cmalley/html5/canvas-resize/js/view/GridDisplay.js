/**
 * Displays a grid of red squares.
 * Assumes that the origin is in the center of the canvas.
 */
define( [ 'easel' ],
        function ( Easel ) {

            function GridDisplay( mvt, canvasWidth, canvasHeight ) {

                Easel.Container.call( this );

                var SIDE_LENGTH = 10;
                var X_SPACING = 3 * SIDE_LENGTH;
                var Y_SPACING = X_SPACING;
                var that = this;
                this.update = function ( canvasWidth, canvasHeight ) {

                    that.removeAllChildren();

                    // create a grid of red rectangles
                    var gridWidth = canvasWidth + X_SPACING;
                    var gridHeight = canvasHeight + Y_SPACING;
                    var y = -gridHeight/2;
                    while ( y <= gridHeight/2 ) {
                        var x = -gridWidth/2;
                        while ( x <= gridWidth/2 ) {
                            var needle = new Easel.Shape();
                            needle.graphics.beginFill( 'red' );
                            needle.graphics.rect( -SIDE_LENGTH/2, -SIDE_LENGTH/2, SIDE_LENGTH, SIDE_LENGTH );
                            needle.x = x;
                            needle.y = y;
                            that.addChild( needle );
                            x += X_SPACING;
                        }
                        y += Y_SPACING;
                    }
                }
                this.update( canvasWidth, canvasHeight );
            }

            GridDisplay.prototype = new Easel.Container();

            return GridDisplay;
        } );
