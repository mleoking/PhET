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

import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConstants;


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
    
    private static double[] _sineValues;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private int _waveType;
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
        
        _numberOfPoints = numberOfPoints;
        _fundamentalCycle = fundamentalCycle;
        _numberOfFundamentalCycles = numberOfFundamentalCycles;
        
        _waveType = FourierConstants.WAVE_TYPE_SINE;

        // Cache of sine values.
        if ( _sineValues == null ) {
            _sineValues = new double[360];
            for ( int i = 0; i < 360; i++ ) {
                _sineValues[i] = Math.sin( Math.toRadians( i ) );
            }
        }

        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
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
        assert( waveType == FourierConstants.WAVE_TYPE_SINE || waveType == FourierConstants.WAVE_TYPE_COSINE );
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
    
    /**
     * Updates this data set to match its associated Harmonic model.
     */
    public void update() {
        
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
                double radians;
                if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                    radians = _sineValues[ degrees ];
                }
                else { /* cosines */
                    radians = 1 - _sineValues[ degrees ];
                }
                double x = startX + ( i * deltaX );
                double y = amplitude * radians;
                addPoint( x, y );
            }
        }
    }
}
