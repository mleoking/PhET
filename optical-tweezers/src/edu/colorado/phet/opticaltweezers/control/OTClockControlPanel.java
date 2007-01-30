/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTDefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;


/**
 * HAClockControls is a custom clock control panel.
 * It has control buttons (Play, Pause, Step) and a time speed slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OTClockControlPanel extends JPanel implements ClockListener {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_DT = false;
    
    private static final DecimalFormat DT_FORMATTER = new DecimalFormat( "0.#" );
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TIME_DISPLAY_COLUMNS = 6;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private OTClock _clock;
    
    private JButton _restartButton;
    private JButton _playButton;
    private JButton _pauseButton;
    private JButton _stepButton;
    private JSlider _clockIndexSlider;
    
    private JTextField _timeTextField;
    private JLabel _timeUnitsLabel;
    
    private NumberFormat _timeFormat;
    
    private JLabel _dtLabel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public OTClockControlPanel( OTClock clock ) {
        super();
        
        // Clock
        double dt = OTConstants.DEFAULT_CLOCK_STEP;
        _clock = clock;
        _clock.setSimulationTimeChange( dt );
        _clock.addClockListener( this );
        
        // Labels (use localized strings from phetcommon)
        String restartLabel = SimStrings.get( "button.restart" );
        String playLabel = SimStrings.get( "Common.ClockControlPanel.Play" );
        String pauseLabel = SimStrings.get( "Common.ClockControlPanel.Pause" );
        String stepLabel = SimStrings.get( "Common.ClockControlPanel.Step" );
        String timeUnitsLabel = SimStrings.get( "units.time" );
        
        // Icons
        Icon restartIcon = null;
        Icon playIcon = null;
        Icon pauseIcon = null;
        Icon stepIcon = null;
        Icon clockIcon = null;
        try {
            restartIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_REWIND ) );
            playIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_PLAY ) );
            pauseIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_PAUSE ) );
            stepIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_STEP ) );
            clockIcon = new ImageIcon( ImageLoader.loadBufferedImage( OTConstants.IMAGE_CLOCK ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Time display
        JPanel timePanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        {
            JLabel clockLabel = new JLabel( clockIcon );
            _timeTextField = new JTextField();
            _timeTextField.setColumns( TIME_DISPLAY_COLUMNS );
            _timeTextField.setFont( OTConstants.TIME_DISPLAY_FONT );
            _timeTextField.setEditable( false );
            _timeTextField.setHorizontalAlignment( JTextField.RIGHT );
            
            _timeUnitsLabel = new JLabel( timeUnitsLabel );
            _timeUnitsLabel.setFont( OTConstants.TIME_UNITS_FONT );
            
            _timeFormat = new DecimalFormat( "0" );
            
            timePanel.add( clockLabel );
            timePanel.add( _timeTextField );
            timePanel.add( _timeUnitsLabel );
        }
        
        // Speed slider
        {
            _clockIndexSlider = new JSlider();
            _clockIndexSlider.setMinimum( 0 );
            _clockIndexSlider.setMaximum( OTConstants.CLOCK_STEPS.length - 1 );
            _clockIndexSlider.setMajorTickSpacing( 1 );
            _clockIndexSlider.setPaintTicks( true );
            _clockIndexSlider.setPaintLabels( true );
            _clockIndexSlider.setSnapToTicks( true );
            _clockIndexSlider.setValue( OTDefaults.CLOCK_INDEX );
            
            // Label the min "normal", the max "fast".
            String normalString = SimStrings.get( "label.clockSpeed.slow" );
            String fastString = SimStrings.get( "label.clockSpeed.fast" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Integer( _clockIndexSlider.getMinimum() ), new JLabel( normalString ) );
            labelTable.put( new Integer( _clockIndexSlider.getMaximum() ), new JLabel( fastString ) );
            _clockIndexSlider.setLabelTable( labelTable );
            
            // Set the slider's physical width
            Dimension preferredSize = _clockIndexSlider.getPreferredSize();
            Dimension size = new Dimension( 150, (int) preferredSize.getHeight() );
            _clockIndexSlider.setPreferredSize( size );
        }
        
        // Clock control buttons
        JPanel controlsPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        {
            _restartButton = new JButton( restartLabel, restartIcon );
            _playButton = new JButton( playLabel, playIcon );
            _pauseButton = new JButton( pauseLabel, pauseIcon );
            _stepButton = new JButton( stepLabel, stepIcon );
            
            controlsPanel.add( _restartButton );
            controlsPanel.add( _playButton );
            controlsPanel.add( _pauseButton );
            controlsPanel.add( _stepButton );
        }
        
        // dt value
        if ( DEBUG_SHOW_DT ) {           
            _dtLabel = new JLabel();
            updateDt( dt );
        }
        
        // Layout
        setLayout(  new FlowLayout( FlowLayout.CENTER ) );
        add( timePanel );
        add( Box.createHorizontalStrut( 10 ) );
        add( _clockIndexSlider );
        if ( _dtLabel != null ) {
            add( _dtLabel );
        }
        add( Box.createHorizontalStrut( 10 ) );
        add( controlsPanel );
        
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
        _playButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePlay();
            }
        } );
        _pauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePause();
            }
        } );
        _stepButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleStep();
            }
        } );
        
        // Listen for clock state changes
        _clock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updateButtonState();
            }
            public void clockPaused( ClockEvent clockEvent ) {
                updateButtonState();
            }
        });
        
        // Inital state
        updateTimeDisplay();
        updateButtonState();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the clock associated with this control panel.
     * 
     * @return the clock
     */
    public IClock getClock() {
        return _clock;
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
        double dt = OTConstants.CLOCK_STEPS[index];
        _clock.setSimulationTimeChange( dt );
        updateDt( dt );
    }
    
    private void handleRestart() {
        _clock.resetSimulationTime();
        updateTimeDisplay();
    }
    
    private void handlePlay() {
        _clock.start();
    }
    
    private void handlePause() {
        _clock.pause();
    }
    
    private void handleStep() {
        _clock.stepClockWhilePaused();
    }
   
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the state of the buttons to match the state of the clock.
     */
    private void updateButtonState() {
        boolean isPaused = _clock.isPaused();
        _playButton.setEnabled( isPaused );
        _pauseButton.setEnabled( !isPaused );
        _stepButton.setEnabled( isPaused );
    }
    
    /*
     * Updates the time display.
     */
    private void updateTimeDisplay() {
        double time = _clock.getSimulationTime();
        String sValue = _timeFormat.format( time );
        _timeTextField.setText( sValue );
    }
    
    /*
     * Updates the dt display.
     */
    private void updateDt( double dt ) {
        if ( _dtLabel!= null ) {
            String s = DT_FORMATTER.format( dt );
            _dtLabel.setText( "dt=" + s );
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    public void clockTicked( ClockEvent clockEvent ) {
        updateTimeDisplay();
    }

    public void clockStarted( ClockEvent clockEvent ) {
        updateButtonState();
    }

    public void clockPaused( ClockEvent clockEvent ) {
        updateButtonState();
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        updateTimeDisplay();
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
        updateTimeDisplay();
    }
}
