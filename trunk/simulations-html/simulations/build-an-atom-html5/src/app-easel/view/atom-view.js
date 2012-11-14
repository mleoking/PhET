// Copyright 2002-2012, University of Colorado
define( [
            'easel'
        ], function ( Easel ) {

    var AtomView = function ( label, color ) {
        this.initialize( label, color );
    };
    var p = AtomView.prototype = new Easel.Container(); // inherit from Container

    p.Container_initialize = p.initialize;
    p.initialize = function ( label, color ) {
        this.Container_initialize();
        console.log( "hello" );
        var x = new Easel.Shape();
        x.graphics.beginStroke( "orange" ).setStrokeStyle( 5 ).moveTo( -10, -10 ).lineTo( 10, 10 ).moveTo( -10, 10 ).lineTo( 10, -10 ).endStroke();
        this.addChild( x );
    };

    return AtomView;
} );
