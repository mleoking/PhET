/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;


/**
 * OTClockControls is a custom clock control panel.
 * It has control buttons (Play, Pause, Step, Restart) and a time display.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTClockControlPanel extends JPanel implements ClockListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final DecimalFormat DEFAULT_TIME_FORMAT = new DecimalFormat( "0" );
    public static final int DEFAULT_TIME_DISPLAY_COLUMNS = 6;
    public static final Font TIME_DISPLAY_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    public static final Font TIME_UNITS_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    
    private JButton _restartButton;
    private JButton _playButton;
    private JButton _pauseButton;
    private JButton _stepButton;
    private JTextField _timeTextField;
    private JLabel _timeUnitsLabel;
    
    private NumberFormat _timeFormat;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public OTClockControlPanel( IClock clock ) {
        super();
        
        // Clock
        _clock = clock;
        _clock.addClockListener( this );
        
        // Labels
        String restartLabel = OTResources.getString( "button.restart" );
        String playLabel = OTResources.getCommonString( ClockControlPanel.PROPERTY_PLAY );
        String pauseLabel = OTResources.getCommonString( ClockControlPanel.PROPERTY_PAUSE );
        String stepLabel = OTResources.getCommonString( ClockControlPanel.PROPERTY_STEP );
        String timeUnitsLabel = OTResources.getString( "units.time" );

        // Icons
        Icon restartIcon = new ImageIcon( OTResources.getCommonImage( ClockControlPanel.IMAGE_REWIND ) );
        Icon playIcon = new ImageIcon( OTResources.getCommonImage( ClockControlPanel.IMAGE_PLAY ) );
        Icon pauseIcon = new ImageIcon( OTResources.getCommonImage( ClockControlPanel.IMAGE_PAUSE ) );
        Icon stepIcon = new ImageIcon( OTResources.getCommonImage( ClockControlPanel.IMAGE_STEP ) );
        Icon clockIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_CLOCK ) );

        // Time display
        JPanel timePanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        {
            JLabel clockLabel = new JLabel( clockIcon );
            _timeTextField = new JTextField();
            _timeTextField.setColumns( DEFAULT_TIME_DISPLAY_COLUMNS );
            _timeTextField.setFont( TIME_DISPLAY_FONT );
            _timeTextField.setPreferredSize( _timeTextField.getPreferredSize() );
            _timeTextField.setText( "0" );
            _timeTextField.setEditable( false );
            _timeTextField.setHorizontalAlignment( JTextField.RIGHT );
            
            _timeUnitsLabel = new JLabel( timeUnitsLabel );
            _timeUnitsLabel.setFont( TIME_UNITS_FONT );
            
            _timeFormat = DEFAULT_TIME_FORMAT;
            
            timePanel.add( clockLabel );
            timePanel.add( _timeTextField );
            timePanel.add( _timeUnitsLabel );
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
        
        // Layout
        setLayout( new FlowLayout( FlowLayout.CENTER ) );
        add( timePanel );
        add( Box.createHorizontalStrut( 30 ) ); // space between time display and controls
        add( controlsPanel );
        
        // Interactivity
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
        
        // Inital state
        updateTimeDisplay();
        updateButtonState();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _clock.removeClockListener( this );
        _clock = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the "Restart" component, used for attaching help items.
     * @return
     */
    public JComponent getRestartComponent() {
        return _restartButton;
    }
    
    /**
     * Gets the "Pause" component, used for attaching help items.
     * @return
     */
    public JComponent getPauseComponent() {
        return _pauseButton;
    }
    
    /**
     * Gets the clock associated with this control panel.
     * 
     * @return the clock
     */
    public IClock getClock() {
        return _clock;
    }
    
    /**
     * Sets the time units label.
     * 
     * @param label
     */
    public void setTimeUnitsLabel( String label ) {
        _timeUnitsLabel.setText( label );
    }
    
    /**
     * Sets the formatter used to format the time display.
     * 
     * @param enabled
     */
    public void setTimeDisplayPattern( String pattern ) {
        _timeFormat = new DecimalFormat( pattern );
        setTimeDisplayColumns( pattern.length() );
    }
    
    /**
     * Sets the number of columns in the time display.
     * 
     * @param columns
     */
    public void setTimeDisplayColumns( int columns ) {
        _timeTextField.setColumns( columns );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
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
