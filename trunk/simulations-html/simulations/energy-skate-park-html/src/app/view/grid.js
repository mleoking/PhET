define( ["view/skater", 'util/extend'], function ( Skater ) {

    return createjs.Container.extend( 'Grid', {
        initialize: function ( superInit, groundY ) {
            superInit();
            var shape = new createjs.Shape();
            shape.graphics.beginStroke( 'black' ).setStrokeStyle( 1 );
            var gridSpacing = 70;
            for ( var i = 0; i < 10; i++ ) {
                var y = groundY - gridSpacing * i;
                shape.graphics.moveTo( 0, y ).lineTo( 1024, y );
            }
            for ( i = 0; i < 15; i++ ) {
                var x = 0 + i * gridSpacing;
                shape.graphics.moveTo( x, 0 ).lineTo( x, groundY );
            }
            shape.graphics.endStroke();
            this.addChild( shape );

            //Cache the grid to get better performance, especially on iPad
            shape.cache( 0, 0, 1024, 768 );
        }
    } );

} );

