/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockListener;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * GlaciersClockControlPanel is a custom clock control panel.
 * It has a time display, frame rate control, and transport control buttons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClockControlPanel extends ClockControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final ConstantDtClockListener _clockListener;
    private final LinearValueControl _frameRateControl;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param clock
     */
    public GlaciersClockControlPanel( GlaciersClock clock ) {
        super( clock );
        
        // Clock
        _clock = clock;
        _clockListener = new ConstantDtClockAdapter() {
            public void dtChanged( ConstantDtClockEvent event ) {
                // clock frame rate changed, update the speed slider
                _frameRateControl.setValue( _clock.getFrameRate() );
            }
        };
        _clock.addConstantDtClockListener( _clockListener );
        
        // common clock controls
        setRestartButtonVisible( false );
        setTimeDisplayVisible( true );
        setUnits( GlaciersStrings.UNITS_TIME );
        setTimeFormat( GlaciersConstants.CLOCK_DISPLAY_FORMAT );
        setTimeColumns( GlaciersConstants.CLOCK_DISPLAY_COLUMNS );

        
        // Frame Rate control
        {
            double min = GlaciersConstants.CLOCK_FRAME_RATE_RANGE.getMin();
            double max = GlaciersConstants.CLOCK_FRAME_RATE_RANGE.getMax();
            String label = "";
            String textFieldPattern = "";
            String units = "";
            _frameRateControl = new LinearValueControl( min, max, label, textFieldPattern, units, new SliderOnlyLayoutStrategy() );
            _frameRateControl.setValue( _clock.getFrameRate() );
            _frameRateControl.setMinorTicksVisible( false );
            
            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( GlaciersStrings.SLIDER_CLOCK_SLOW ) );
            labelTable.put( new Double( max ), new JLabel( GlaciersStrings.SLIDER_CLOCK_FAST ) );
            _frameRateControl.setTickLabels( labelTable );
            
            // Change font on tick labels
            Dictionary d = _frameRateControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setFont( new PhetFont( 10 ) );
            }
            
            // Slider width
            _frameRateControl.setSliderWidth( 125 );
        }
        addBetweenTimeDisplayAndButtons( _frameRateControl );
        
        // Interactivity
        _frameRateControl.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                _clock.setFrameRate( (int)_frameRateControl.getValue() );
            }
        } );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        cleanup();
        _clock.removeConstantDtClockListener( _clockListener );
    }
}
