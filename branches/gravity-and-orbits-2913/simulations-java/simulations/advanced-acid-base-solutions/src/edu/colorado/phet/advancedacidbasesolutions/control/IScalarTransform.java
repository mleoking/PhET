
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.control;

import edu.colorado.phet.common.phetcommon.math.MathUtil;

/**
 * Strategies for transforming scalar values between model and view coordinates.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IScalarTransform {

    public double modelToView( double modelValue );

    public double viewToModel( double viewValue );

    /**
     * Transform between linear model and linear view values.
     */
    public static class LinearLinearTransform implements IScalarTransform {

        private final double modelMin, modelMax;
        private final double viewMin, viewMax;

        public LinearLinearTransform( double modelMin, double modelMax, double viewMin, double viewMax ) {
            this.modelMin = modelMin;
            this.modelMax = modelMax;
            this.viewMin = viewMin;
            this.viewMax = viewMax;
        }
        
        public double modelToView( final double modelValue ) {
            double ratio = ( modelValue - modelMin ) / ( modelMax - modelMin );
            return viewMin + (int) ( ratio * ( viewMax - viewMin ) );
        }

        public double viewToModel( final double viewValue ) {
            double ratio = ( viewValue - viewMin ) / ( viewMax - viewMin );
            return modelMin + ( ratio * ( modelMax - modelMin ) );
        }
    }

    /**
     * Transform between log model and linear view values.
     */
    public static class LogLinearTransform implements IScalarTransform {

        private final double viewMin, viewMax;
        private final double modelMin, modelMax;
        private final double scalingFactor;

        /**
         * Constructor.
         * @throws IllegalArgumentException if modelMin and modelMax have different signs
         */
        public LogLinearTransform( double modelMin, double modelMax, double viewMin, double viewMax ) {

            if ( modelMin < 0 && modelMax > 0 || modelMin > 0 && modelMax < 0 ) {
                throw new IllegalArgumentException( "modelMin and modelMax must have the same sign" );
            }
            this.viewMin = viewMin;
            this.viewMax = viewMax;

            /* 
            * This implementation is well-behaved for abs(modelMin) >= 1.
            * To support cases where abs(modelMin) < 1, we'll use a
            * scaling factor to adjust the model range and results.
            */
            scalingFactor = ( Math.abs( modelMin ) < 1 ) ? ( 1 / Math.abs( modelMin ) ) : 1;
            this.modelMin = adjustedLog10( modelMin * scalingFactor );
            this.modelMax = adjustedLog10( modelMax * scalingFactor );
        }

        public double modelToView( final double modelValue ) {
            final double adjustedModelValue = adjustedLog10( modelValue * scalingFactor );
            return viewMin + ( ( ( viewMax - viewMin)  * ( adjustedModelValue - modelMin ) / ( modelMax - modelMin ) ) );
        }

        public double viewToModel( final double viewValue ) {
            double modelValue = 0;
            final double logModelValue = modelMin + ( ( modelMax - modelMin ) * ( viewValue - viewMin ) / ( viewMax - viewMin ) );
            if ( logModelValue >= 0 ) {
                modelValue = Math.pow( 10.0, logModelValue ) / scalingFactor;
            }
            else {
                modelValue = -Math.pow( 10.0, -logModelValue ) / scalingFactor;
            }
            return modelValue;
        }

        /* Handles log10 of zero and negative values. */
        private static double adjustedLog10( double d ) {
            double value = 0;
            if ( d > 0 ) {
                value = MathUtil.log10( d );
            }
            else if ( d < 0 ) {
                value = -MathUtil.log10( -d );
            }
            return value;
        }
    }

}
