// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.SinePlot;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.event.HarmonicColorChangeEvent;
import edu.colorado.phet.fourier.event.HarmonicColorChangeListener;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * HarmonicPlot encapsulates the graphics and data set that allow a Chart
 * to draw a harmonic's waveform.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicPlot extends SinePlot implements SimpleObserver, HarmonicColorChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final WaveType DEFAULT_WAVE_TYPE = WaveType.SINES;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private WaveType _waveType;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param component
     * @param chart
     */
    public HarmonicPlot( Component component, Chart chart ) {
        super( component, chart );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _harmonic = null;
        _waveType = WaveType.COSINES; // force an update
        setWaveType( WaveType.SINES );
        setShowZeroAmplitudeEnabled( false );
        
        // Interested in changes to harmonic colors.
        HarmonicColors.getInstance().addHarmonicColorChangeListener( this );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        super.cleanup();
        if ( _harmonic != null ) {
            _harmonic.removeObserver( this );
            _harmonic = null;
        }
        HarmonicColors.getInstance().removeHarmonicColorChangeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the harmonic associated with this graphic.
     * 
     * @param harmonic
     */
    public void setHarmonic( Harmonic harmonic ) {
        assert ( harmonic != null );
        if ( _harmonic != null ) {
            _harmonic.removeObserver( this );
        }
        _harmonic = harmonic;
        _harmonic.addObserver( this );
        update();
    }
    
    /**
     * Gets the harmonic associated with this graphic.
     * 
     * @return Harmonic
     */
    public Harmonic getHarmonic() {
        return _harmonic;
    }
    
    /**
     * Sets the wave type;
     * 
     * @param waveType
     */
    public void setWaveType( WaveType waveType ) {
        if ( waveType != _waveType ) {
            _waveType = waveType;
            super.setCosineEnabled( _waveType == WaveType.COSINES );
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the harmonic model.
     */
    public void update() {
        if ( isVisible() && _harmonic != null ) {
            setAmplitude( _harmonic.getAmplitude() );
        }
    }
    
    //----------------------------------------------------------------------------
    // HarmonicColorChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Change the graphic's color when its associated harmonic color changes.
     */
    public void harmonicColorChanged( HarmonicColorChangeEvent e ) {
        if ( _harmonic != null && _harmonic.getOrder() == e.getOrder() ) {
            Color harmonicColor = HarmonicColors.getInstance().getColor( _harmonic );
            setBorderColor( harmonicColor );
        }
    }
}
