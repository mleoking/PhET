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
     * @param {Point} offset
     * @param {Number} scale
     */
    function ModelViewTransform( offset, scale ) {

        /*
         * Transformation from model to view coordinate frame.
         * @param {Point} point
         * @return {Point}
         */
        this.modelToView = function( point ) {
            return new Easel.Point( point.x + offset.x, point.y + offset.y );
        };

        /*
         * Transformation from view to model coordinate frame.
         * @param {Point} point
         * @return {Point}
         */
        this.viewToModel = function( point ) {
            return new Easel.Point( point.x - offset.x, point.y - offset.y );
        };
    }

    return ModelViewTransform;
});
