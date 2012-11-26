// Copyright 2002-2012, University of Colorado
define( [ 'underscore', 'easel' ], function ( _, Easel ) {
    /**
     * @class BucketFront
     * @param bucket The model of the bucket.
     * @constructor
     **/
    var BucketFront = function ( bucket ) {
        Easel.Container.prototype.initialize.call( this );
        this.bucket = bucket;
        this.initialize();
    };

    var p = BucketFront.prototype;

    _.extend( p, Easel.Container.prototype );

    p.initialize = function () {

        var width = this.bucket.width;
        var height = width * 0.5; // Determined empirically for best look.

        // Create the basic shape of the front of the bucket.
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "gray" ).setStrokeStyle( 2 );
        shape.graphics
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
        this.regY = 0;

        // Set the position.
        this.x = this.bucket.x;
        this.y = this.bucket.y;
    };

    return BucketFront;
} );
