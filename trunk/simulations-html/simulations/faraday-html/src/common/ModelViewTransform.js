// Copyright 2002-2012, University of Colorado

/**
 * Transform between model and view coordinate frames.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ],
        function ( Easel ) {

            /**
             * @class ModelViewTransform
             * @constructor
             * @param {Number} scale when going from model to view coordinates. 1 unit in the model is this many view units.
             * @param {Point} offset when going from model to view coordinates
             */
            function ModelViewTransform( scale, offset ) {

                /*
                 * Transformation a point from model to view coordinates.
                 * @param {Point} point
                 * @return {Point}
                 */
                this.modelToView = function ( point ) {
                    return new Easel.Point( ( point.x + offset.x ) * scale, ( point.y + offset.y ) * scale );
                };

                /*
                 * Transformation a point from view to model coordinates.
                 * @param {Point} point
                 * @return {Point}
                 */
                this.viewToModel = function ( point ) {
                    return new Easel.Point( ( point.x / scale ) - offset.x, ( point.y / scale ) - offset.y );
                };

                /**
                 * Transforms a scalar from model to view coordinates.
                 * @param {Number} scalar
                 * @return {Number}
                 */
                this.modelToViewScalar = function ( scalar ) {
                    return scalar * scale;
                };

                /**
                 * Transforms a scalar from view to model coordinates.
                 * @param {Number} scalar
                 * @return {Number}
                 */
                this.viewToModelScalar = function ( scalar ) {
                    return scalar / scale;
                };
            }

            return ModelViewTransform;
        } );
