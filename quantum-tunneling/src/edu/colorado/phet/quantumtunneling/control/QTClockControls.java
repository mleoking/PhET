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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * QTClockControls
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTClockControls extends JPanel implements ClockStateListener, ClockTickListener {
    
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
    
    private double _timeValue;
    private double _timeScale;
    private boolean _loopEnabled;

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Controls
        _restartButton = new JButton( restartLabel, restartIcon );
        _playButton = new JButton( playLabel, playIcon );
        _pauseButton = new JButton( pauseLabel, pauseIcon );
        _stepButton = new JButton( stepLabel, stepIcon );
        _loopButton = new JToggleButton( loopLabel, _loopOffIcon );
        JLabel clockLabel = new JLabel( clockIcon );
        _timeTextField = new JTextField( "0000000000");
        _timeTextField.setPreferredSize( _timeTextField.getPreferredSize() );
        _timeTextField.setText( "0" );
        _timeTextField.setEditable( false );
        _timeTextField.setHorizontalAlignment( JTextField.RIGHT );
        _timeUnitsLabel = new JLabel( timeUnitsLabel );
        
        // Layout
        setLayout( new BorderLayout() );
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( clockLabel );
        buttonPanel.add( _timeTextField );
        buttonPanel.add( _timeUnitsLabel );
        buttonPanel.add( new JLabel( "        " ) ); // space
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
                handleLoop();
            }
        } );
        
        // Inital state
        _timeValue = 0;
        _timeScale = 1;
        updateTimeDisplay();
        updateButtonState();
    }
    
    public void setTimeScale( double timeScale ) {
        if ( timeScale <= 0 ) {
            throw new IllegalArgumentException( "timeScale must be > 0: " + timeScale );
        }
        _timeScale = timeScale;
    }
    
    public void setTimeUnits( String units ) {
        _timeUnitsLabel.setText( units );
    }
    
    private void handleRestart() {
        _timeValue = 0;
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
    
    private void handleLoop() {
        _loopEnabled = !_loopEnabled;
        if ( _loopEnabled ) {
            _loopButton.setIcon( _loopOnIcon );
        }
        else {
            _loopButton.setIcon( _loopOffIcon );
        } 
    }
   
    private void updateButtonState() {
        boolean isPaused = _clock.isPaused();
        _playButton.setEnabled( isPaused );
        _pauseButton.setEnabled( !isPaused );
        _stepButton.setEnabled( isPaused );
    }
    
    private void updateTimeDisplay() {
        int iValue = (int) ( _timeScale * _timeValue );
        String sValue = String.valueOf( iValue );
        _timeTextField.setText( sValue );
    }
    
    public void stateChanged( ClockStateEvent event ) {
        updateButtonState();
    }

    public void clockTicked( ClockTickEvent event ) {
        double dt = event.getDt();
        _timeValue += dt;
        updateTimeDisplay();
    }
}
