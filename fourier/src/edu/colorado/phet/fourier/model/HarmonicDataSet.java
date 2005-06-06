/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.model;

import java.util.Hashtable;

import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * HarmonicDataSet is the data set (used by the chart package) that 
 * corresponds to a specific Harmonic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicDataSet extends DataSet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int WAVE_TYPE_SINE = 0;
    public static final int WAVE_TYPE_COSINE = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private int _waveType;
    private double[] _sineValues;
    private double _fundamentalCycle;;
    private int _numberOfPoints;
    private int _numberOfFundamentalCycles;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param harmonic the Harmonic that this data set represents
     * @param numberOfPoints the number of data points used to approximate the harmonic
     * @param fundamentalCycle the width of a one cycle of the fundamental harmonic
     * @param numberOfFundamentalCycles the number of fundamental cycles 
     */
    public HarmonicDataSet( Harmonic harmonic, int numberOfPoints,
            double fundamentalCycle, int numberOfFundamentalCycles ) {
        
        assert( harmonic != null );
        _harmonic = harmonic;
        _harmonic.addObserver( this );
        
        _numberOfPoints = numberOfPoints; //XXX 2000
        _fundamentalCycle = fundamentalCycle; //XXX 1.0
        _numberOfFundamentalCycles = numberOfFundamentalCycles; //XXX 4
        
        _waveType = WAVE_TYPE_SINE;

        _sineValues = new double[360];
        for ( int i = 0; i < 360; i++ ) {
            _sineValues[i] = Math.sin( Math.toRadians( i ) );
        }

        update();
    }
    
    public void finalize() {
        _harmonic.removeObserver( this );
        _harmonic = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wave type (sine or cosine).
     * 
     * @param waveType WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public void setWaveType( int waveType ) {
        assert( waveType == WAVE_TYPE_SINE || waveType == WAVE_TYPE_COSINE );
        if ( waveType != _waveType ) {
            _waveType = waveType;
            update();
        }
    }
    
    /**
     * Gets the wave type.
     * 
     * @return WAVE_TYPE_SINES or WAVE_TYPE_COSINE
     */
    public int getWaveType() {
        return _waveType;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
//        System.out.println( "HarmonicDataSet.update " + _harmonic.getOrder() );//XXX
//        addPoint( 0, 0 ); //XXX
//        addPoint( 0.5, ( 4 / Math.PI ) / ( _harmonic.getOrder() + 1 ) );//XXX
        
        clear();

        double amplitude = _harmonic.getAmplitude();
        
        if ( amplitude != 0 ) {
            
            int numberOfCycles = _numberOfFundamentalCycles * ( _harmonic.getOrder() + 1 );
            double pointsPerCycle = _numberOfPoints / (double) numberOfCycles;
            double deltaX = ( _numberOfFundamentalCycles * _fundamentalCycle ) / _numberOfPoints;
            double deltaAngle = ( 2.0 * Math.PI ) / pointsPerCycle;
            
            double startX = -2 * _fundamentalCycle;
            double startAngle = 0.0;
            for ( int i = 0; i <= _numberOfPoints; i++ ) {
                double angle = startAngle + ( i * deltaAngle );
                int degrees = (int)( Math.toDegrees( angle ) % 360 );
//                double radians = ( _waveType == WAVE_TYPE_SINE ) ? Math.sin( angle ) : Math.cos( angle );
                double radians = _sineValues[ degrees ];
                double x = startX + ( i * deltaX );
                double y = amplitude * radians;
                addPoint( x, y );
            }
        }
    }
}
