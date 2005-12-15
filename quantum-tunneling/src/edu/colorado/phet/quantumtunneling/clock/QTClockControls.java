/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.clock;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.common.model.clock.*;
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
public class QTClockControls extends JPanel implements ClockStateListener, ClockTickListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractClock _clock;
    
    private JButton _restartButton;
    private JButton _playButton;
    private JButton _pauseButton;
    private JButton _stepButton;
    private JToggleButton _loopButton;
    private JTextField _timeTextField;
    private JLabel _timeUnitsLabel;
    
    private Icon _loopOnIcon;
    private Icon _loopOffIcon;
    
    private double _timeScale;
    private boolean _loopEnabled;
    private NumberFormat _timeFormat;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public QTClockControls( AbstractClock clock ) {
        super();
        
        // Clock
        _clock = clock;
        _clock.addClockStateListener( this );
        _clock.addClockTickListener( this );
        
        // Labels
        String restartLabel = SimStrings.get( "button.restart" );
        String playLabel = SimStrings.get( "button.play" );
        String pauseLabel = SimStrings.get( "button.pause" );
        String stepLabel = SimStrings.get( "button.step" );
        String loopLabel = SimStrings.get( "button.loop" );
        String timeUnitsLabel = SimStrings.get( "units.time" );
        
        // Icons
        Icon restartIcon = null;
        Icon playIcon = null;
        Icon pauseIcon = null;
        Icon stepIcon = null;
        _loopOnIcon = null;
        _loopOffIcon = null;
        Icon clockIcon = null;
        try {
            restartIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_RESTART ) );
            playIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_PLAY ) );
            pauseIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_PAUSE ) );
            stepIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_STEP ) );
            _loopOnIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_LOOP_ON ) );
            _loopOffIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_LOOP_OFF ) );
            clockIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_CLOCK ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Time display
        JLabel clockLabel = new JLabel( clockIcon );
        _timeTextField = new JTextField( "0000000000");
        _timeTextField.setPreferredSize( _timeTextField.getPreferredSize() );
        _timeTextField.setText( "0" );
        _timeTextField.setEditable( false );
        _timeTextField.setHorizontalAlignment( JTextField.RIGHT );
        _timeUnitsLabel = new JLabel( timeUnitsLabel );
        _timeFormat = new DecimalFormat( "0" );
        
        // Clock control buttons
        _restartButton = new JButton( restartLabel, restartIcon );
        _playButton = new JButton( playLabel, playIcon );
        _pauseButton = new JButton( pauseLabel, pauseIcon );
        _stepButton = new JButton( stepLabel, stepIcon );
        _loopButton = new JToggleButton( loopLabel, _loopOffIcon );
        
        // Layout
        setLayout( new BorderLayout() );
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( clockLabel );
        buttonPanel.add( _timeTextField );
        buttonPanel.add( _timeUnitsLabel );
        buttonPanel.add( new JLabel( "            " ) ); // space between time display and controls
        buttonPanel.add( _restartButton );
        buttonPanel.add( _playButton );
        buttonPanel.add( _pauseButton );
        buttonPanel.add( _stepButton );
        buttonPanel.add( _loopButton );

        this.add( buttonPanel, BorderLayout.CENTER );
        
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
        _loopButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleLoopToggled();
            }
        } );
        
        // Inital state
        _timeScale = 1;
        updateTimeDisplay();
        updateButtonState();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _clock.removeClockStateListener( this );
        _clock.removeClockTickListener( this );
        _clock = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the clock associated with this control panel.
     * 
     * @return the clock
     */
    public AbstractClock getClock() {
        return _clock;
    }
    
    /**
     * Sets the scaling factor used to display time.
     * The time display normally displays clock ticks.
     * 
     * @param timeScale
     */
    public void setTimeScale( double timeScale ) {
        if ( timeScale <= 0 ) {
            throw new IllegalArgumentException( "timeScale must be > 0: " + timeScale );
        }
        _timeScale = timeScale;
    }
    
    /**
     * Gets the time scale.
     * 
     * @return
     */
    public double getTimeScale() {
        return _timeScale;
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
    
    /**
     * Turns looping on and off.
     * 
     * @param true or false
     */
    public void setLoopEnabled( boolean enabled ) {
        _loopEnabled = !enabled;
        handleLoopToggled();
    }
    
    /**
     * Is looping enabled?
     *
     * @return true or false
     */
    public boolean isLoopEnabled() {
        return _loopEnabled;
    }
    
    /**
     * Shows or hides the loop button.
     * 
     * @param true or false
     */
    public void setLoopVisible( boolean visible ) {
        _loopButton.setVisible( false );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleRestart() {
        _clock.resetRunningTime();
        updateTimeDisplay();
    }
    
    private void handlePlay() {
        _clock.setPaused( false );
    }
    
    private void handlePause() {
        _clock.setPaused( true );
    }
    
    private void handleStep() {
        _clock.tickOnce();
    }
    
    private void handleLoopToggled() {
        _loopEnabled = !_loopEnabled;
        if ( _loopEnabled ) {
            _loopButton.setIcon( _loopOnIcon );
        }
        else {
            _loopButton.setIcon( _loopOffIcon );
        } 
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
        double scaledTime = _timeScale * _clock.getRunningTime();
        String sValue = _timeFormat.format( scaledTime );
        _timeTextField.setText( sValue );
    }
    
    //----------------------------------------------------------------------------
    // ClockStateListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the state of the buttons whenever the clock state changes.
     * 
     * @param event
     */
    public void stateChanged( ClockStateEvent event ) {
        updateButtonState();
    }

    //----------------------------------------------------------------------------
    // ClockTickListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the time display whenever the clock ticks.
     * 
     * @param event
     */
    public void clockTicked( ClockTickEvent event ) {
        updateTimeDisplay();
    }
}
