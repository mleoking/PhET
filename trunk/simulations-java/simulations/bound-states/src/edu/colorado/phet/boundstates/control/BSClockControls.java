/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.model.BSClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.ClockTimePanel;


/**
 * BSClockControls is a custom clock control panel.
 * It has control buttons (Restart, Play, Pause, Step) and a time display.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSClockControls extends ClockControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSClock _clock;
    
    private ClockTimePanel _timePanel;
    private JSlider _clockIndexSlider;
    private JButton _restartButton;

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
        _clock.setSimulationTimeChange( BSConstants.DEFAULT_CLOCK_STEP );
//        _clock.addClockListener( this );//this is handled automatically in superclass; it appears previous versions called addClockListener twice, once here and once in superclass
        
        // Restart button
        String restartLabel = BSResources.getString( "button.restart" );
        Icon restartIcon = new ImageIcon( BSResources.getCommonImage( PhetCommonResources.IMAGE_REWIND ) );
        _restartButton = new JButton( restartLabel, restartIcon );

        // Time display
        String units = BSResources.getString( "units.time" );
        DecimalFormat format = BSConstants.DEFAULT_CLOCK_FORMAT;
        int columns = 6;
        _timePanel = new ClockTimePanel( clock, units, format, columns );
        _timePanel.setTimeFont( BSConstants.TIME_DISPLAY_FONT );
        _timePanel.setUnitsFont( BSConstants.TIME_UNITS_FONT );
        
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
        addControlToLeft( _restartButton );
        addControlToLeft( _clockIndexSlider );
        addControlToLeft( _timePanel );
        
        // Interactivity
        _clockIndexSlider.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleClockIndexChange();
            }
        } );
        _restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRestart();
            }
        } );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        super.cleanup();//detachment from clock is handled in ClockControlPanel
        _clock = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the "Restart" component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getRestartComponent() {
        return _restartButton;
    }
    
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
        _timePanel.setTimeFormat( BSConstants.CLOCK_FORMATS[index] );
        _clock.setSimulationTimeChange( BSConstants.CLOCK_STEPS[index] );
        _clock.resetSimulationTime(); // so the clock display doesn't overflow
    }
    
    private void handleRestart() {
        _clock.resetSimulationTime();
    }
}
