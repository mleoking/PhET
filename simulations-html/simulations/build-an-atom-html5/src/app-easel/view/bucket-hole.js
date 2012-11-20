// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'easel'
], function ( _, Easel ) {

    var BucketHole = function ( centerX, centerY, width ) {
        Easel.Container.prototype.initialize.call(this);
        this.initialize( centerX, centerY, width );
    };

    var p = BucketHole.prototype;

    _.extend(p, Easel.Container.prototype);

    p.initialize = function ( centerX, centerY, width ) {
        var bucketHoleHeight = width * 0.2;
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "black" ).setStrokeStyle( 2 ).drawEllipse( 0, 0, width, bucketHoleHeight ).endStroke().endFill();
        this.addChild( shape );

        this.x = centerX - width / 2;
        this.y = centerY - bucketHoleHeight / 2;
        this.centerX = centerX;
        this.centerY = centerY;
    };

    return BucketHole;
});
