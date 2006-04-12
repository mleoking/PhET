/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * QTClockControls is a custom clock control panel.
 * In addition to a couple of new buttons, it has a time display.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTClockControls extends JPanel implements ClockListener {
    
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
    public QTClockControls( IClock clock ) {
        super();
        
        // Clock
        _clock = clock;
        _clock.addClockListener( this );
        
        // Labels
        String restartLabel = SimStrings.get( "button.restart" );
        String playLabel = SimStrings.get( "button.play" );
        String pauseLabel = SimStrings.get( "button.pause" );
        String stepLabel = SimStrings.get( "button.step" );
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
            clockIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_CLOCK ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Time display
        JPanel timePanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        {
            JLabel clockLabel = new JLabel( clockIcon );
            _timeTextField = new JTextField( "0000000000" );
            _timeTextField.setFont( QTConstants.TIME_DISPLAY_FONT );
            _timeTextField.setPreferredSize( _timeTextField.getPreferredSize() );
            _timeTextField.setText( "0" );
            _timeTextField.setEditable( false );
            _timeTextField.setHorizontalAlignment( JTextField.RIGHT );
            
            _timeUnitsLabel = new JLabel( timeUnitsLabel );
            _timeUnitsLabel.setFont( QTConstants.TIME_UNITS_FONT );
            
            _timeFormat = new DecimalFormat( "0" );
            
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
        setLayout(  new FlowLayout( FlowLayout.CENTER ) );
        if ( QTConstants.TIME_DISPLAY_VISIBLE ) {
            add( timePanel );
            add( Box.createHorizontalStrut( 30 ) ); // space between time display and controls
        }
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
    public void setTimeFormat( NumberFormat format ) {
        _timeFormat = format;
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
        if ( QTConstants.TIME_DISPLAY_VISIBLE ) {
            double time = _clock.getSimulationTime();
            String sValue = _timeFormat.format( time );
            _timeTextField.setText( sValue );
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
