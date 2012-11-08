// Copyright 2002-2012, University of Colorado
define( [
            'easel'
        ], function ( Easel ) {

    var BucketView = function ( x, y ) {
        this.initialize( x, y );
    };
    var p = BucketView.prototype = new Easel.Container(); // inherit from Container

    p.Container_initialize = p.initialize;
    p.initialize = function ( x, y ) {
        this.Container_initialize();

        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "gray" ).setStrokeStyle( 2 ).drawEllipse( 0, 0, 100, 30 ).endStroke().endFill();
        this.addChild( shape );

        this.x = x;
        this.y = y;
    };

    return BucketView;
} );
