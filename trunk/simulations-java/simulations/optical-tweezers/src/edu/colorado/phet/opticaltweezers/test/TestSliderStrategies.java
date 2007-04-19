/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import javax.swing.JSlider;


public class TestSliderStrategies {

    public TestSliderStrategies() {}
    
    //----------------------------------------------------------------------------
    // SliderStrategy
    //----------------------------------------------------------------------------
    
    public interface SliderStrategy {
        public double sliderToModel( int sliderValue );
        public int modelToSlider( double modelValue );
    }
    
    //----------------------------------------------------------------------------
    // AbstractStrategy
    //----------------------------------------------------------------------------
    
    public static abstract class AbstractStrategy implements SliderStrategy {
        
        private int _sliderMin, _sliderMax;
        private double _modelMin, _modelMax;
        
        public AbstractStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
            _sliderMin = sliderMin;
            _sliderMax = sliderMax;
            _modelMin = modelMin;
            _modelMax = modelMax;
        }
        
        protected int getSliderMin() {
            return _sliderMin;
        }
        
        protected int getSliderMax() {
            return _sliderMax;
        }

        protected double getModelMin() {
            return _modelMin;
        }
        
        protected double getModelMax() {
            return _modelMax;
        }
        
        public String toString() {
            return getClass().getName() + " slider=[" + _sliderMin + "," + _sliderMax + "] model=[" + _modelMin + "," + _modelMax + "]";
        }
    }
    
    //----------------------------------------------------------------------------
    // LinearStrategy
    //----------------------------------------------------------------------------
    
    public static class LinearStrategy extends AbstractStrategy {
        
        public LinearStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
            super( sliderMin, sliderMax, modelMin, modelMax );
        }

        public double sliderToModel( int sliderValue ) {
            double ratio = ( sliderValue - getSliderMin() ) / (double)( getSliderMax() - getSliderMin() );
            return getModelMin() + ( ratio * ( getModelMax() - getModelMin() ) );
        }

        public int modelToSlider( double modelValue ) {
            double ratio = ( modelValue - getModelMin() ) / ( getModelMax() - getModelMin() );
            return getSliderMin() + (int)( ratio * ( getSliderMax() - getSliderMin() ) );
        }
    }
    
    //----------------------------------------------------------------------------
    // LogarithmicStrategy
    //----------------------------------------------------------------------------
    
    public static class LogarithmicStrategy extends AbstractStrategy {
        
        private double _logMin, _logMax, _logRange;
        
        public LogarithmicStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
            super( sliderMin, sliderMax, modelMin, modelMax );
            if ( modelMin < 0 && modelMax > 0 || modelMin > 0 && modelMax < 0 ) {
                throw new IllegalArgumentException( "modelMin and modelMax must have the same sign" );
            }
            _logMin = adjustedLog10( modelMin );
            _logMax = adjustedLog10( modelMax );
            _logRange = _logMax - _logMin;
        }

        public double sliderToModel( int sliderValue ) {
            double modelValue = 0;
            int resolution = getSliderMax() - getSliderMin();
            double ratio = _logRange / (double)resolution;
            double pos = (double)( sliderValue - getSliderMin() ) * ratio;
            double adjustedPos = _logMin + pos;
            modelValue = Math.pow( 10.0, adjustedPos );
            if ( modelValue < getModelMin() ) {
                modelValue = getModelMin();
            }
            else if ( modelValue > getModelMax() ) {
                modelValue = getModelMax();
            }
            return modelValue;
        }

        public int modelToSlider( double modelValue ) {
            int sliderValue = 0;
            int resolution = getSliderMax() - getSliderMin();
            double logModelValue = adjustedLog10( modelValue );
            sliderValue = getSliderMin() + (int)( resolution * ( logModelValue - _logMin ) / _logRange );
            if ( sliderValue < getSliderMin() ) {
                sliderValue = getSliderMin();
            }
            else if ( sliderValue > getSliderMax() ) {
                sliderValue = getSliderMax();
            }
            return sliderValue;
        }
        
        /* Handles log base 10 of 0 and negative values. */
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
            return Math.log( d ) / Math.log(  10.0  );
        }
    }
    
    //----------------------------------------------------------------------------
    // Tests
    //----------------------------------------------------------------------------
    
    /*
     * Tests a strategy for a specified number of equally-spaced intervals.
     */
    private static void testStrategy( AbstractStrategy strategy, int numberOfTests ) {
        
        int sliderMin = strategy.getSliderMin();
        int sliderMax = strategy.getSliderMax();
        double modelMin = strategy.getModelMin();
        double modelMax = strategy.getModelMax();
        System.out.println( "test: " + strategy.toString() );
        
        int sliderStep = ( sliderMax - sliderMin ) / ( numberOfTests - 1 );
        double modelStep = ( modelMax - modelMin ) / ( numberOfTests - 1 );
        
        System.out.println( "LinearStrategy.sliderToModel tests..." );
        for ( int i = 0; i < numberOfTests; i++ ) {
            int sliderValue = sliderMin + ( i * sliderStep );
            double modelValue = modelMin + ( i * modelStep );
            testStrategy( strategy, sliderValue, modelValue );
        }
    }
    
    /*
     * Tests a strategy for specific slider and model value.
     */
    private static void testStrategy( SliderStrategy strategy, int sliderValue, double modelValue ) {
        System.out.println( sliderValue + " -> " + strategy.sliderToModel( sliderValue ) + " (" + modelValue + ")" );
        System.out.println( modelValue  + " -> " + strategy.modelToSlider( modelValue )  + " (" + sliderValue + ")" );
    }
    
    public static void runTests() {
        
        // Linear test
        {
            int numberOfTests = 10;
            System.out.println( "------------------------" );
            testStrategy( new LinearStrategy( -100, 100, -1.0, 1.0 ), numberOfTests );
            System.out.println( "------------------------" );
            testStrategy( new LinearStrategy( 0, 1000, 1000.0, 2000.0 ), numberOfTests );
            System.out.println( "------------------------" );
            testStrategy( new LinearStrategy( 0, 1000, -1000, 0 ), numberOfTests );
        }
        
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            SliderStrategy logarithmicStrategy = new LogarithmicStrategy( -100, 100, 20.0, 200000.0 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy, -100,    20.0 );
            testStrategy( logarithmicStrategy, -50,    200.0 );
            testStrategy( logarithmicStrategy,   0,   2000.0 );
            testStrategy( logarithmicStrategy,  50,  20000.0 );
            testStrategy( logarithmicStrategy, 100, 200000.0 );
        }
        
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            SliderStrategy logarithmicStrategy = new LogarithmicStrategy( 0, 1000, 20.0, 200000.0 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy,    0,     20.0 );
            testStrategy( logarithmicStrategy,  250,    200.0 );
            testStrategy( logarithmicStrategy,  500,   2000.0 );
            testStrategy( logarithmicStrategy,  750,  20000.0 );
            testStrategy( logarithmicStrategy, 1000, 200000.0 );
        }
        
        //XXX This case fails!
        // Logarithmic test
        {
            System.out.println( "------------------------" );
            SliderStrategy logarithmicStrategy = new LogarithmicStrategy( 0, 1000, -200000.0, -20.0 );
            System.out.println( "test: " + logarithmicStrategy.toString() );
            testStrategy( logarithmicStrategy,    0, -200000.0 );
            testStrategy( logarithmicStrategy,  250,  -20000.0 );
            testStrategy( logarithmicStrategy,  500,   -2000.0 );
            testStrategy( logarithmicStrategy,  750,    -200.0 );
            testStrategy( logarithmicStrategy, 1000,     -20.0 );
        }
    }
    
    public static void main( String[] args ) {
        TestSliderStrategies.runTests();
    }
}
