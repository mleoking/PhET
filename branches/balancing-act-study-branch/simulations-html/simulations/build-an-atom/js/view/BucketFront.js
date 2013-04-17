// Copyright 2002-2012, University of Colorado
define( [ 'underscore', 'easel', 'common/Point2D' ], function ( _, Easel, Point2D ) {

    /**
     * @class BucketFront
     * @param bucket The model of the bucket.
     * @constructor
     **/
    var BucketFront = function ( bucket, mvt ) {
        Easel.Container.prototype.initialize.call( this );
        this.initialize( bucket, mvt );
    };

    var p = BucketFront.prototype;

    _.extend( p, Easel.Container.prototype );

    p.initialize = function ( bucket, mvt ) {

        this.bucket = bucket;
        var width = mvt.modelToView( this.bucket.width );
        var height = width * 0.5; // Determined empirically for best look.

        // Create the basic shape of the front of the bucket.
        var shape = new Easel.Shape();
        shape.graphics
                .beginStroke( "black" )
                .beginLinearGradientFill( ["white", bucket.color, "gray" ], [.05,.9,1], 0, 0, width, 0 )
                .setStrokeStyle( 2 )
                .moveTo( 0, 0 )
                .lineTo( width * 0.1, height * 0.8 )
                .bezierCurveTo( width * 0.3, height, width * 0.7, height, width * 0.9, height * 0.8 )
                .lineTo( width, 0 )
                .bezierCurveTo( width * 0.8, height * 0.15, width * 0.2, height * 0.15, 0, 0 )
                .closePath()
                .endStroke()
                .endFill();
        this.addChild( shape );

        // Create and add the label, centered on the front.
        var label = new Easel.Text( this.bucket.labelText, "bold 24px Helvetica", "white" );
        label.textBaseline = "middle";
        label.x = width / 2 - label.getMeasuredWidth() / 2;
        label.y = height / 2;
        this.addChild( label );

        // Set the registration point.
        this.regX = width / 2;
        this.regY = -width * 0.1; // TODO: This was manually coordinated with BucketHole, should be made automatic.

        // Set the position.
        var topCenter = mvt.modelToView( new Point2D( this.bucket.x, this.bucket.y ) );
        this.x = topCenter.x;
        this.y = topCenter.y;
    };

    return BucketFront;
} );
