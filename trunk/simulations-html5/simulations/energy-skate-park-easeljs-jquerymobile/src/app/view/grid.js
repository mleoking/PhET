define( ["skater"], function ( Skater ) {
    var Grid = function ( groundY ) {
        this.initialize( groundY );
    };
    var p = Grid.prototype = new createjs.Container(); // inherit from Container

    p.Container_initialize = p.initialize;

    p.initialize = function ( groundY ) {
        this.Container_initialize();
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
        this.addChild( shape );
//        shape.cache( 0, 0, 1024, 768 );
    };

    return Grid;
} );