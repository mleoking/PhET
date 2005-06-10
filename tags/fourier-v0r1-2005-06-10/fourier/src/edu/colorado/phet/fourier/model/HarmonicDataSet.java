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

import java.awt.geom.Point2D;
import java.lang.reflect.Array;

import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * HarmonicDataSet is the data set (used by the chart package) that
 * corresponds to a specific Harmonic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicDataSet extends DataSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Harmonic _harmonic;
    private int _waveType;
    private double _fundamentalCycle;;
    private int _numberOfPoints;
    private int _numberOfFundamentalCycles;
    private Point2D[] _points;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param harmonic                  the Harmonic that this data set represents
     * @param numberOfPoints            the number of data points used to approximate the harmonic
     * @param fundamentalCycle          the width of a one cycle of the fundamental harmonic
     * @param numberOfFundamentalCycles the number of fundamental cycles
     */
    public HarmonicDataSet( Harmonic harmonic, int numberOfPoints, double fundamentalCycle, int numberOfFundamentalCycles ) {

        assert ( harmonic != null );
        _harmonic = harmonic;
        _harmonic.addObserver( this );

        _numberOfPoints = numberOfPoints;
        _fundamentalCycle = fundamentalCycle;
        _numberOfFundamentalCycles = numberOfFundamentalCycles;

        _waveType = FourierConstants.WAVE_TYPE_SINE;

        _points = new Point2D[ numberOfPoints + 1 ];
        for ( int i = 0; i < _points.length; i++ ) {
            _points[ i ] = new Point2D.Double();
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
        assert ( waveType == FourierConstants.WAVE_TYPE_SINE || waveType == FourierConstants.WAVE_TYPE_COSINE );
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

    public Harmonic getHarmonic() {
        return _harmonic;
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

            for ( int i = 0; i < _points.length; i++ ) {
                double angle = startAngle + ( i * deltaAngle );
                double radians;
                if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                    radians = FourierUtils.sin( angle );
                }
                else { /* cosines */
                    radians = FourierUtils.cos( angle );
                }
                double x = startX + ( i * deltaX );
                double y = amplitude * radians;
                _points[ i ].setLocation( x, y );
            }
            
            addAllPoints( _points );
        }
    }
}
