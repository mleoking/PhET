// Copyright 2013, University of Colorado

define( [
            'easel',
            'common/model/Inheritance'
        ],
        function ( Easel, Inheritance ) {

            /**
             * Constructor
             * @param {Beaker} beaker
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function BeakerNode( beaker, mvt ) {

                Easel.Container.call( this ); // constructor stealing

                // constants
                var RIM_OFFSET = 20;
                var MINOR_TICK_SPACING = 0.1; // L
                var MINOR_TICKS_PER_MAJOR_TICK = 5;
                var MAJOR_TICK_LENGTH = 30;
                var MINOR_TICK_LENGTH = 15;
                var TICK_LABEL_X_SPACING = 8;

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

                // tick marks
                var ticksParent = new Easel.Container();
                this.addChild( ticksParent );
                var numberOfTicks = Math.round( 1 / MINOR_TICK_SPACING );

//                final int numberOfTicks = (int) Math.round( MAX_VOLUME / MINOR_TICK_SPACING );
//                        final double leftX = -getOriginXOffset(); // don't use bounds or position will be off because of stroke width
//                        final double rightX = getOriginXOffset();
//                        final double bottomY = beaker.size.getHeight() - getOriginYOffset(); // don't use bounds or position will be off because of stroke width
//                        double deltaY = beaker.size.getHeight() / numberOfTicks;
//                        for ( int i = 1; i <= numberOfTicks; i++ ) {
//                            final double y = bottomY - ( i * deltaY );
//                            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
//                                // major tick
//                                double x1 = ( ticksLocation == TicksLocation.LEFT ) ? leftX : rightX - MAJOR_TICK_LENGTH;
//                                double x2 = ( ticksLocation == TicksLocation.LEFT ) ? leftX + MAJOR_TICK_LENGTH : rightX;
//                                Shape tickPath = new Line2D.Double( x1, y, x2, y );
//                                PPath tickNode = new PPath( tickPath );
//                                tickNode.setStroke( MAJOR_TICK_STROKE );
//                                tickNode.setStrokePaint( TICK_COLOR );
//                                ticksNode.addChild( tickNode );
//
//                                // major tick label
//                                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
//                                if ( labelIndex < MAJOR_TICK_LABELS.length && MAJOR_TICK_LABELS[labelIndex] != null ) {
//                                    String label = MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, MAJOR_TICK_LABELS[labelIndex], Strings.UNITS_LITERS );
//                                    PText textNode = new PText( label );
//                                    textNode.setFont( TICK_LABEL_FONT );
//                                    textNode.setTextPaint( TICK_COLOR );
//                                    ticksNode.addChild( textNode );
//                                    double xOffset = ( ticksLocation == TicksLocation.LEFT ) ?
//                                                     ( tickNode.getFullBounds().getMaxX() + TICK_LABEL_X_SPACING ) :
//                                                     ( tickNode.getFullBounds().getMinX() - textNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING );
//                                    double yOffset = tickNode.getFullBounds().getMinY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
//                                    textNode.setOffset( xOffset, yOffset );
//                                }
//                            }
//                            else {
//                                // minor tick
//                                double x1 = ( ticksLocation == TicksLocation.LEFT ) ? leftX : rightX - MINOR_TICK_LENGTH;
//                                double x2 = ( ticksLocation == TicksLocation.LEFT ) ? leftX + MINOR_TICK_LENGTH : rightX;
//                                Shape tickPath = new Line2D.Double( x1, y, x2, y );
//                                PPath tickNode = new PPath( tickPath );
//                                tickNode.setStroke( MINOR_TICK_STROKE );
//                                tickNode.setStrokePaint( TICK_COLOR );
//                                ticksNode.addChild( tickNode );
//                            }
            }

            // prototype chaining
            Inheritance.inheritPrototype( BeakerNode, Easel.Container );

            return BeakerNode;
        } );
