// Copyright 2002-2012, University of Colorado

/**
 * Transform between model and view coordinate frames.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ], function( Easel ) {

    /**
     * @class ModelViewTransform
     * @constructor
     * @param {Number} scale when going from model to view coordinates. 1 unit in the model is this many view units.
     * @param {Point} offset when going from model to view coordinates
     */
    function ModelViewTransform( scale, offset ) {

        /*
         * Transformation from model to view coordinate frame.
         * @param {Point} point
         * @return {Point}
         */
        this.modelToView = function( point ) {
            return new Easel.Point( ( point.x + offset.x ) * scale, ( point.y + offset.y ) * scale );
        };

        /*
         * Transformation from view to model coordinate frame.
         * @param {Point} point
         * @return {Point}
         */
        this.viewToModel = function( point ) {
            return new Easel.Point( ( point.x / scale ) - offset.x, ( point.y / scale ) - offset.y );
        };
    }

    return ModelViewTransform;
});
