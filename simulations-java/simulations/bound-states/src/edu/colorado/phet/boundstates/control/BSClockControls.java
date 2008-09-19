/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.boundstates.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.model.BSClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;


/**
 * BSClockControls is a custom clock control panel.
 * It has control buttons (Restart, Play, Pause, Step) and a time display.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BSClockControls extends PiccoloClockControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSClock _clock;
    private JSlider _clockIndexSlider;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public BSClockControls( BSClock clock ) {
        super( clock );
        
        // Clock
        _clock = clock;
        _clock.setDt( BSConstants.DEFAULT_CLOCK_STEP );
        
        // Restart button
        setRestartButtonVisible( true  );

        // Time display
        setUnits( BSResources.getString( "units.time" ) );
        setTimeFormat( BSConstants.DEFAULT_CLOCK_FORMAT );
        setTimeColumns( 6 );
        setTimeFont( BSConstants.TIME_DISPLAY_FONT );
        setUnitsFont( BSConstants.TIME_UNITS_FONT );
        
        // Speed slider
        {
            _clockIndexSlider = new JSlider();
            _clockIndexSlider.setMinimum( 0 );
            _clockIndexSlider.setMaximum( BSConstants.CLOCK_STEPS.length - 1 );
            _clockIndexSlider.setMajorTickSpacing( 1 );
            _clockIndexSlider.setPaintTicks( true );
            _clockIndexSlider.setPaintLabels( true );
            _clockIndexSlider.setSnapToTicks( true );
            _clockIndexSlider.setValue( BSConstants.DEFAULT_CLOCK_INDEX );
            
            // Label the min "normal", the max "fast".
            String normalString = BSResources.getString( "label.clockSpeed.normal" );
            String fastString = BSResources.getString( "label.clockSpeed.fast" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Integer( _clockIndexSlider.getMinimum() ), new JLabel( normalString ) );
            labelTable.put( new Integer( _clockIndexSlider.getMaximum() ), new JLabel( fastString ) );
            _clockIndexSlider.setLabelTable( labelTable );
            
            // Set the slider's physical width
            Dimension preferredSize = _clockIndexSlider.getPreferredSize();
            Dimension size = new Dimension( 150, (int) preferredSize.getHeight() );
            _clockIndexSlider.setPreferredSize( size );
        }
        
        // Layout
        setLayout(  new FlowLayout( FlowLayout.CENTER ) );
        addBetweenTimeDisplayAndButtons( _clockIndexSlider );
        
        // Interactivity
        _clockIndexSlider.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleClockIndexChange();
            }
        } );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        super.cleanup();
        _clock = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the clock index (speed) component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getClockIndexComponent() {
        return _clockIndexSlider;
    }
    
    /**
     * Gets the clock index.
     * 
     * @return clock index
     */
    public int getClockIndex() {
        return _clockIndexSlider.getValue();
    }
    
    /**
     * Sets the clock index.
     * 
     * @param index
     */
    public void setClockIndex( int index ) {
        _clockIndexSlider.setValue( index );
        handleClockIndexChange();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleClockIndexChange() {
        int index = _clockIndexSlider.getValue();
        setTimeFormat( BSConstants.CLOCK_FORMATS[index] );
        _clock.setDt( BSConstants.CLOCK_STEPS[index] );
        _clock.resetSimulationTime(); // so the clock display doesn't overflow
    }
}
