// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'easel'
], function ( _, Easel ) {

    var BucketView = function ( x, y ) {
        Easel.Container.prototype.initialize.call(this);
        this.initialize( x, y );
    };

    var p = BucketView.prototype;

    _.extend(p, Easel.Container.prototype);

    p.initialize = function ( x, y ) {
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "gray" ).setStrokeStyle( 2 ).drawEllipse( 0, 0, 100, 30 ).endStroke().endFill();
        this.addChild( shape );

        this.x = x;
        this.y = y;
    };

    return BucketView;
});
