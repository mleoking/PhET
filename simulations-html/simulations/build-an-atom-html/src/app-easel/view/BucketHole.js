// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'easel'
], function ( _, Easel ) {

    var BucketHole = function ( bucket ) {
        Easel.Container.prototype.initialize.call(this);
        this.bucket = bucket;
        this.initialize();
    };

    var p = BucketHole.prototype;
    _.extend(p, Easel.Container.prototype);

    p.initialize = function () {
        var width = this.bucket.width;
        var height = width * 0.2;
        var centerX = this.bucket.x;
        var centerY = this.bucket.y;
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "black" ).setStrokeStyle( 2 ).drawEllipse( 0, 0, width, height ).endStroke().endFill();
        this.addChild( shape );

        this.x = centerX - width / 2;
        this.y = centerY - height / 2;
    };

    return BucketHole;
});
