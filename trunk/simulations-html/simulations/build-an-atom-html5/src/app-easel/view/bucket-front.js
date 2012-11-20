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
        var height = width * 0.7; // Determined empirically for best look.
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "gray" ).setStrokeStyle( 2 ).drawRect( 0, 0, width, height ).endStroke().endFill();
        this.addChild( shape );

        this.x = centerX - width / 2;
        this.y = topY;
    };

    return BucketFront;
} );
