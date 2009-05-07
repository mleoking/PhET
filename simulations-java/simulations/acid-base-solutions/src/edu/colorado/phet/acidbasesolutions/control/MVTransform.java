
package edu.colorado.phet.acidbasesolutions.control;


public interface MVTransform {

    public double modelToView( double modelValue );

    public double viewToModel( double viewValue );

    /**
     * Linear transform between model and view values.
     */
    public static class LinearTransform implements MVTransform {

        private final double modelMin, modelMax;
        private final double viewMin, viewMax;

        public LinearTransform( double modelMin, double modelMax, double viewMin, double viewMax ) {
            this.modelMin = modelMin;
            this.modelMax = modelMax;
            this.viewMin = viewMin;
            this.viewMax = viewMax;
        }
        
        public double modelToView( final double modelValue ) {
            double viewValue = 0;
            // Handle min and max specially to avoid precision errors
            if ( modelValue == modelMin ) {
                viewValue = viewMin;
            }
            else if ( modelValue == modelMax ) {
                viewValue = viewMax;
            }
            else {
                double ratio = ( modelValue - modelMin ) / ( modelMax - modelMin );
                viewValue = viewMin + (int) ( ratio * ( viewMax - viewMin ) );
            }
            return viewValue;
        }

        public double viewToModel( final double viewValue ) {
            double modelValue = 0;
            // Handle min and max specially to avoid precision errors
            if ( viewValue == viewMin ) {
                modelValue = modelMin;
            }
            else if ( viewValue == viewMax ) {
                modelValue = modelMax;
            }
            else {
                double ratio = ( viewValue - viewMin ) / ( viewMax - viewMin );
                modelValue = modelMin + ( ratio * ( modelMax - modelMin ) );
            }
            return modelValue;
        }
    }

    /**
     * Log transform between model and view values.
     */
    public static class LogTransform implements MVTransform {

        private final double modelMin, modelMax;
        private final double viewMin, viewMax;

        private final double _logMin, _logMax, _logRange;
        private final double _scalingFactor;

        /**
         * Constructor.
         * @throws IllegalArgumentException if modelMin and modelMax have different signs
         */
        public LogTransform( double modelMin, double modelMax, double viewMin, double viewMax ) {

            if ( modelMin < 0 && modelMax > 0 || modelMin > 0 && modelMax < 0 ) {
                throw new IllegalArgumentException( "modelMin and modelMax must have the same sign" );
            }
            this.modelMin = modelMin;
            this.modelMax = modelMax;
            this.viewMin = viewMin;
            this.viewMax = viewMax;

            /* 
            * This implementation is well-behaved for abs(modelMin) >= 1.
            * To support cases where abs(modelMin) < 1, we'll use a
            * scaling factor to adjust the model range and results.
            */
            _scalingFactor = ( Math.abs( modelMin ) < 1 ) ? ( 1 / Math.abs( modelMin ) ) : 1;

            _logMin = adjustedLog10( modelMin * _scalingFactor );
            _logMax = adjustedLog10( modelMax * _scalingFactor );
            _logRange = _logMax - _logMin;
        }

        public double modelToView( final double modelValue ) {
            double viewValue = 0;
            // Handle min and max specially to avoid precision errors
            if ( modelValue == modelMin ) {
                viewValue = viewMin;
            }
            else if ( modelValue == modelMax ) {
                viewValue = viewMax;
            }
            else {
                double resolution = viewMax - viewMin;
                double logModelValue = adjustedLog10( modelValue * _scalingFactor );
                viewValue = viewMin + ( ( resolution * ( logModelValue - _logMin ) / _logRange ) );
            }
            return viewValue;
        }

        public double viewToModel( final double viewValue ) {
            double modelValue = 0;
            // Handle min and max specially to avoid precision errors
            if ( viewValue == viewMin ) {
                modelValue = modelMin;
            }
            else if ( viewValue == viewMax ) {
                modelValue = modelMax;
            }
            else {
                double resolution = viewMax - viewMin;
                double ratio = _logRange / resolution;
                double pos = ( viewValue - viewMin ) * ratio;
                double adjustedPos = _logMin + pos;
                if ( adjustedPos >= 0 ) {
                    modelValue = Math.pow( 10.0, adjustedPos ) / _scalingFactor;
                }
                else {
                    modelValue = -Math.pow( 10.0, -adjustedPos ) / _scalingFactor;
                }
            }
            return modelValue;
        }

        /* Handles log10 of zero and negative values. */
        private static double adjustedLog10( double d ) {
            double value = 0;
            if ( d > 0 ) {
                value = log10( d );
            }
            else if ( d < 0 ) {
                value = -log10( -d );
            }
            return value;
        }

        /* Log base 10 */
        private static double log10( double d ) {
            return Math.log( d ) / Math.log( 10.0 );
        }
    }

}
