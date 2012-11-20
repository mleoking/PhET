// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel'
        ], function ( _, Easel ) {

    /**
     * Sets the stroke style for the current subpath. Like all drawing methods, this can be chained, so you can define the stroke style and color in a single line of code like so:
     * myGraphics.setStrokeStyle(8,"round").beginStroke("#F00");
     * @param centerX Center of the bucket front.
     * @param topY Top of the bucket front.
     * @param width Width of the bucket front.
     * @param height Height of the bucket front.
     * @param labelText Text shown on front of bucket.
     * @return {BucketFront} The representation of the front of the bucket.
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
        shape.graphics.moveTo( 0, 0 );
        shape.graphics.lineTo( width * 0.1, height * 0.8 );
        shape.graphics.bezierCurveTo( width * 0.3, height, width * 0.7, height, width * 0.9, height * 0.8 );
        shape.graphics.lineTo( width, 0 );
        shape.graphics.bezierCurveTo( width * 0.8, height * 0.15, width * 0.2, height * 0.15, 0, 0 );
        shape.graphics.closePath();
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
