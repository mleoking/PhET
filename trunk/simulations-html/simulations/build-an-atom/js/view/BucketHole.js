// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'easel',
  'common/Point2D'
], function ( _, Easel, Point2D ) {

    var BucketHole = function ( bucket, mvt ) {
        Easel.Container.prototype.initialize.call(this);
        this.bucket = bucket;
        this.initialize( mvt );
    };

    var p = BucketHole.prototype;
    _.extend(p, Easel.Container.prototype);

    p.initialize = function ( mvt ) {
        var width = mvt.modelToView( this.bucket.width );
        var height = mvt.modelToView( width * 0.2 );
        var center = mvt.modelToView( new Point2D( this.bucket.x, this.bucket.y ) );

        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "black" ).setStrokeStyle( 2 ).drawEllipse( 0, 0, width, height ).endStroke().endFill();
        this.addChild( shape );

        this.x = center.x - width / 2;
        this.y = center.y;
    };

    return BucketHole;
});
