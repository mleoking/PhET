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
            }

            // prototype chaining
            Inheritance.inheritPrototype( BeakerNode, Easel.Container );

            return BeakerNode;
        } );
