/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import javax.swing.JSlider;


public class TestSliderStrategies {

    public static void main( String[] args ) {
        TestSliderStrategies tester = new TestSliderStrategies();
        tester.test();
    }
    
    public TestSliderStrategies() {}
    
    public interface SliderStrategy {
        public double sliderToModel( int sliderValue );
        public int modelToSlider( double modelValue );
        public void setSliderRange( int min, int max );
        public void setModelRange( double min, double max );
    }
    
    public static abstract class AbstractStrategy implements SliderStrategy {
        
        private int _sliderMin, _sliderMax;
        private double _modelMin, _modelMax;
        
        public AbstractStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
            _sliderMin = sliderMin;
            _sliderMax = sliderMax;
            _modelMin = modelMin;
            _modelMax = modelMax;
        }
        
        public void setSliderRange( int min, int max ) {
            _sliderMin = min;
            _sliderMax = max;
        }
        
        protected int getSliderMin() {
            return _sliderMin;
        }
        
        protected int getSliderMax() {
            return _sliderMax;
        }

        public void setModelRange( double min, double max ) {
            _modelMin = min;
            _modelMax = max;
        }
        
        protected double getModelMin() {
            return _modelMin;
        }
        
        protected double getModelMax() {
            return _modelMax;
        }
    }
    
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
    
    public static class LogarithmicStrategy extends AbstractStrategy {
        
        public LogarithmicStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
            super( sliderMin, sliderMax, modelMin, modelMax );
        }

        public double sliderToModel( int sliderValue ) {
            double modelValue = 0;
            int resolution = getSliderMax() - getSliderMin();
            double logMin = log10( getModelMin() );
            double logMax = log10( getModelMax() );
            double logSpan = logMax - logMin;
            double ratio = logSpan / (double)resolution;
            double pos = (double)sliderValue * ratio;
            double adjustedPos = logMin + pos;
            modelValue = Math.pow( 10.0, adjustedPos );
            return modelValue;
        }

        public int modelToSlider( double modelValue ) {
            int sliderValue = 0;
            int resolution = getSliderMax() - getSliderMin();
            double logMin = log10( getModelMin() );
            double logMax = log10( getModelMax() );
            double logSpan = logMax - logMin;
            sliderValue = getSliderMin() + (int)( resolution * ( log10( modelValue ) - logMin ) / logSpan );
            return sliderValue;
        }
        
        private static double log10( double d ) {
            return Math.log( d ) / Math.log(  10.0  );
        }
    }
    
    //XXX
    private static double log10( double d ) {
        return Math.log( d ) / Math.log(  10.0  );
    }
    
    public void test() {
        
        double min = 20;
        double max = 200000;
        double resolution = 1000;
        
        double logMin = log10( min );
        double logMax = log10( max );
        double logSpan = logMax - logMin;
        
        // log: model -> slider
        {
            double logValue = 2000;
            int linearValue = (int)( resolution * ( log10( logValue ) - logMin ) / logSpan );
            System.out.println( "log to linear: " + logValue + " -> " + linearValue );
        }
//        
//        // log: slider -> model
//        {
//            double r = 1000;
//            double i = 500;
//            double logMin = log10( 20 );
//            double logMax = log10( 20000 );
//            double logSpan = logMax - logMin;
//            double u = Math.pow( 10, logMin ) + ( logSpan + i ) / r;
//            System.out.println( "u=" + u );
//        }
        
        // log: slider -> model
        {
            int linearValue = 750;
            double ratio = logSpan / (double)resolution;
            double pos = (double)linearValue * ratio;
            double adjustedPos = logMin + pos;
            double logValue = Math.pow( 10.0, adjustedPos );
            System.out.println( "linear to log: " + linearValue + " -> " + logValue );
        }
        
//        SliderStrategy linearStrategy = new TestSliderStrategies.LinearStrategy( -100, 100, -1.0, 1.0 );
//
//        System.out.println( linearStrategy.sliderToModel( -100 ) == -1.0 );
//        System.out.println( linearStrategy.modelToSlider( -1.0 ) == -100 );
//
//        System.out.println( linearStrategy.sliderToModel( -50 ) == -0.5 );
//        System.out.println( linearStrategy.modelToSlider( -0.5 ) == -50 );
//
//        System.out.println( linearStrategy.sliderToModel( -25 ) == -0.25 );
//        System.out.println( linearStrategy.modelToSlider( -0.25 ) == -25 );
//
//        System.out.println( linearStrategy.sliderToModel( 0 ) == 0.0 );
//        System.out.println( linearStrategy.modelToSlider( 0.0 ) == 0 );
//
//        System.out.println( linearStrategy.sliderToModel( 25 ) == 0.25 );
//        System.out.println( linearStrategy.modelToSlider( 0.25 ) == 25 );
//
//        System.out.println( linearStrategy.sliderToModel( 50 ) == 0.5 );
//        System.out.println( linearStrategy.modelToSlider( 0.5 ) == 50 );
//
//        System.out.println( linearStrategy.sliderToModel( 100 ) == 1.0 );
//        System.out.println( linearStrategy.modelToSlider( 1.0 ) == 100 );
        
        SliderStrategy logarithmicStrategy = new TestSliderStrategies.LogarithmicStrategy( 0, 1000, 20.0, 200000.0 );
        
        System.out.println( logarithmicStrategy.sliderToModel( 0 ) );
        System.out.println( logarithmicStrategy.modelToSlider( 20 ) );
        
        System.out.println( logarithmicStrategy.sliderToModel( 250 ) );
        System.out.println( logarithmicStrategy.modelToSlider( 200.0 ) );
        
        System.out.println( logarithmicStrategy.sliderToModel( 500 ) );
        System.out.println( logarithmicStrategy.modelToSlider( 2000.0 ) );
        
        System.out.println( logarithmicStrategy.sliderToModel( 750 ) );
        System.out.println( logarithmicStrategy.modelToSlider( 20000.0 ) );
        
        System.out.println( logarithmicStrategy.sliderToModel( 1000 ) );
        System.out.println( logarithmicStrategy.modelToSlider( 200000.0 ) );
    }
}
