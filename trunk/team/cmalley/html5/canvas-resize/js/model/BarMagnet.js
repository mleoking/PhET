define( [
            'math/Point2D',
            'model/Property'
        ],
        function ( Point2D, Property ) {

            /**
             * @param {Point2D} location
             * @param {Dimension2D} size
             * @constructor
             */
            function BarMagnet( location, size ) {
                this.location = new Property( location );
                this.size = size;
            }

            return BarMagnet;
        } );

