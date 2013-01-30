// Copyright 2013, University of Colorado

define( [
            'easel',
            'common/model/Inheritance',
            'common/util/StringUtils',
            'i18n!../../../nls/beers-law-lab-strings'
        ],
        function ( Easel, Inheritance, StringUtils, Strings ) {

            /**
             * Constructor
             * @param {Beaker} beaker
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function BeakerNode( beaker, mvt ) {

                Easel.Container.call( this ); // constructor stealing

                // constants
                var MAX_VOLUME = 1;
                var RIM_OFFSET = 20;
                var MINOR_TICK_SPACING = 0.1; // L
                var MINOR_TICKS_PER_MAJOR_TICK = 5;
                var MAJOR_TICK_LENGTH = 30;
                var MINOR_TICK_LENGTH = 15;
                var TICK_LABEL_X_SPACING = 8;
                var MAJOR_TICK_LABELS = new Array( "\u00bd", "1" ); // 1/2, 1

                // outline of the beaker, starting from upper left
                var width = mvt.modelToView( beaker.size.width );
                var height = mvt.modelToView( beaker.size.height );
                var outlineNode = new Easel.Shape();
                outlineNode.graphics
                        .setStrokeStyle( 6, 'round', 'round' )
                        .beginStroke( 'black' )
                        .moveTo( -(width / 2 ) - RIM_OFFSET, -height - RIM_OFFSET )
                        .lineTo( -(width / 2), -height )
                        .lineTo( -(width / 2), 0 )
                        .lineTo( width / 2, 0 )
                        .lineTo( width / 2, -height )
                        .lineTo( (width / 2) + RIM_OFFSET, -height - RIM_OFFSET );
                this.addChild( outlineNode );

                // horizontal tick marks, left edge, from bottom up
                var ticksParent = new Easel.Container();
                this.addChild( ticksParent );
                var numberOfTicks = Math.round( MAX_VOLUME / MINOR_TICK_SPACING );
                var deltaY = height / numberOfTicks;
                for ( var i = 1; i <= numberOfTicks; i++ ) {

                    // tick
                    var isMajorTick = ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 );
                    var y = -( i * deltaY );
                    var leftX = -width / 2;
                    var rightX = leftX + ( isMajorTick ? MAJOR_TICK_LENGTH : MINOR_TICK_LENGTH );
                    var tickNode = new Easel.Shape();
                    tickNode.graphics
                            .setStrokeStyle( 2, 'butt', 'bevel' )
                            .beginStroke( 'black' )
                            .moveTo( leftX, y )
                            .lineTo( rightX, y );
                    ticksParent.addChild( tickNode );

                    if ( isMajorTick ) {
                        // major tick label
                        var labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                        if ( labelIndex < MAJOR_TICK_LABELS.length ) {
                            var label = StringUtils.messageFormat( Strings.pattern_0value_1units, [MAJOR_TICK_LABELS[labelIndex], Strings.units_liters] );
                            var textNode = new Easel.Text( label, "24px Arial", 'black' );
                            ticksParent.addChild( textNode );
                            textNode.x = rightX + TICK_LABEL_X_SPACING;
                            textNode.y = y - 10; //TODO replace 10 with 0.5*textNode.height
                        }
                    }
                }

                var location = mvt.modelToView( beaker.location );
                this.x = location.x;
                this.y = location.y;
            }

            // prototype chaining
            Inheritance.inheritPrototype( BeakerNode, Easel.Container );

            return BeakerNode;
        } );
