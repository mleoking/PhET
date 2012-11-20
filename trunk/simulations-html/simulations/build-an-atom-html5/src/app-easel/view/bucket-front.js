// Copyright 2002-2012, University of Colorado
define( [ 'underscore', 'easel' ], function ( _, Easel ) {
    /**
     * @class BucketFront
     * @param centerX Center of the bucket front.
     * @param topY Top of the bucket front.
     * @param width Width of the bucket front.
     * @param height Height of the bucket front.
     * @param labelText Text shown on front of bucket.
     * @constructor
     **/

    var BucketFront = function ( centerX, topY, width, labelText ) {
        Easel.Container.prototype.initialize.call( this );
        this.initialize( centerX, topY, width, labelText );
    };

    var p = BucketFront.prototype;

    _.extend( p, Easel.Container.prototype );

    p.initialize = function ( centerX, topY, width, labelText ) {
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
                .closePath();
        shape.graphics.endStroke().endFill();
        this.addChild( shape );

        // Create and add the label, centered on the front.
        var label = new Easel.Text( labelText, "bold 24px Helvetica", "white" );
        label.textBaseline = "middle";
        label.x = width / 2 - label.getMeasuredWidth() / 2;
        label.y = height / 2;
        this.addChild( label );

        // Set the position.
        this.x = centerX - width / 2;
        this.y = topY;
    };

    return BucketFront;
} );
