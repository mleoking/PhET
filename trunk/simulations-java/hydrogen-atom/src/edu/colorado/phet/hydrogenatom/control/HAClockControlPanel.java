/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HADefaults;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.model.HAClock;


/**
 * HAClockControlPanel is a custom clock control panel.
 * It has control buttons (Play, Pause, Step) and a time speed slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAClockControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_DT = false;
    
    private static final DecimalFormat DT_FORMATTER = new DecimalFormat( "0.#" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAClock _clock;
    
    private JButton _playButton;
    private JButton _pauseButton;
    private JButton _stepButton;
    private JSlider _clockIndexSlider;
    
    private JLabel _dtLabel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public HAClockControlPanel( HAClock clock ) {
        super();
        
        // Clock
        double dt = HAConstants.DEFAULT_CLOCK_STEP;
        _clock = clock;
        _clock.setDt( dt );
        
        // Labels (use localized strings from phetcommon)
        String playLabel = HAResources.getString( "Common.ClockControlPanel.Play" );
        String pauseLabel = HAResources.getString( "Common.ClockControlPanel.Pause" );
        String stepLabel = HAResources.getString( "Common.ClockControlPanel.Step" );
        
        // Icons
        Icon playIcon = null;
        Icon pauseIcon = null;
        Icon stepIcon = null;
        Icon clockIcon = null;
        try {
            playIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_PLAY ) );
            pauseIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_PAUSE ) );
            stepIcon = new ImageIcon( ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_STEP ) );
            clockIcon = new ImageIcon( HAResources.getImage( HAConstants.IMAGE_CLOCK ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Speed slider
        {
            _clockIndexSlider = new JSlider();
            _clockIndexSlider.setMinimum( 0 );
            _clockIndexSlider.setMaximum( HAConstants.CLOCK_STEPS.length - 1 );
            _clockIndexSlider.setMajorTickSpacing( 1 );
            _clockIndexSlider.setPaintTicks( true );
            _clockIndexSlider.setPaintLabels( true );
            _clockIndexSlider.setSnapToTicks( true );
            _clockIndexSlider.setValue( HADefaults.CLOCK_INDEX );
            
            // Label the min "normal", the max "fast".
            String normalString = HAResources.getString( "label.clockSpeed.slow" );
            String fastString = HAResources.getString( "label.clockSpeed.fast" );
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
            _playButton = new JButton( playLabel, playIcon );
            _pauseButton = new JButton( pauseLabel, pauseIcon );
            _stepButton = new JButton( stepLabel, stepIcon );
            
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
        add( new JLabel( clockIcon) );
        add( _clockIndexSlider );
        if ( _dtLabel != null ) {
            add( _dtLabel );
        }
        add( controlsPanel );
        
        // Interactivity
        _clockIndexSlider.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleClockIndexChange();
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
        updateButtonState();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the "Pause" component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getPauseComponent() {
        return _pauseButton;
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
        double dt = HAConstants.CLOCK_STEPS[index];
        _clock.setDt( dt );
        updateDt( dt );
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
    
    private void updateDt( double dt ) {
        if ( _dtLabel!= null ) {
            String s = DT_FORMATTER.format( dt );
            _dtLabel.setText( "dt=" + s );
        }
    }
}
