/*Copyrig/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * ClockControlPanel
 *
 * @author ?
 * @version $Revision$
 */
public class ClockControlPanel extends JPanel implements ClockListener {
    
    // Image resource names
    public static final String IMAGE_PAUSE = PhetCommonResources.IMAGE_PAUSE;
    public static final String IMAGE_PLAY = PhetCommonResources.IMAGE_PLAY;
    public static final String IMAGE_STEP = PhetCommonResources.IMAGE_STEP_FORWARD;
    public static final String IMAGE_REWIND = PhetCommonResources.IMAGE_REWIND;
    public static final String IMAGE_FAST_FORWARD = PhetCommonResources.IMAGE_FAST_FORWARD;
    public static final String IMAGE_STOP = PhetCommonResources.IMAGE_STOP;
    
    public static final String PROPERTY_PLAY = "Common.ClockControlPanel.Play";
    public static final String PROPERTY_PAUSE = "Common.ClockControlPanel.Pause";
    public static final String PROPERTY_STEP = "Common.ClockControlPanel.Step";
    
    private JButton playPause;
    private JButton step;
    private IClock clock;
    private JPanel buttonPanel;
    
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    
    private String playString;
    private String pauseString;

    public ClockControlPanel( final IClock clock ) {
        
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        this.clock = clock;
        clock.addClockListener( this );

        // Button labels
        playString = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_PLAY );
        pauseString = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_PAUSE );
        String stepString = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_STEP );
        
        // Button icons
        BufferedImage playImage = PhetCommonResources.getInstance().getImage( IMAGE_PLAY );
        BufferedImage pauseImage = PhetCommonResources.getInstance().getImage( IMAGE_PAUSE );
        BufferedImage stepImage = PhetCommonResources.getInstance().getImage( IMAGE_STEP );
        playIcon = new ImageIcon( playImage );
        pauseIcon = new ImageIcon( pauseImage );
        ImageIcon stepIcon = new ImageIcon( stepImage );

        playPause = new JButton();
        updatePlayPauseButtonDimension();

        // Step button
        step = new JButton( stepString, stepIcon );

        SwingUtils.fixButtonOpacity( playPause );
        SwingUtils.fixButtonOpacity( step );

        playPause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( clock.isPaused() ) {
                    clock.start();
                }
                else {
                    clock.pause();
                }
                updateButtons();
            }
        } );

        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( clock.isPaused() ) {
                    clock.stepClockWhilePaused();
                }
            }
        } );

        setLayout( new BorderLayout() );
        buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( playPause );
        buttonPanel.add( step );
        this.add( buttonPanel, BorderLayout.CENTER );

        updateButtons();
    }

    /**
     * Adds a listener to the play/pause button.
     * @param actionListener the listener
     */
    protected void addPlayPauseActionListener( ActionListener actionListener ) {
        playPause.addActionListener( actionListener );
    }

    /**
     * Sets the text of the Play mode on the Play/Pause button. 
     * @param playString the new text for the Play mode
     */
    protected void setPlayString(String playString){
        this.playString=playString;
        updatePlayPauseButtonDimension();
        updateButtons();
    }

    /**
     * Play/Pause button
     * Set this button to its maximum size so that the contents of
     * the control panel don't horizontally shift as the button state changes.
     */
    private void updatePlayPauseButtonDimension() {
        // Get dimensions for "Play" state
        playPause.setText( playString );
        playPause.setIcon( playIcon );
        Dimension playSize = playPause.getUI().getPreferredSize( playPause );

        // Get dimensions for "Pause" state
        playPause.setText( pauseString );
        playPause.setIcon( pauseIcon );
        Dimension pauseSize = playPause.getUI().getPreferredSize( playPause );

        // Set max dimensions
        int maxWidth = (int)Math.max( playSize.getWidth(), pauseSize.getWidth() );
        int maxHeight = (int)Math.max( playSize.getHeight(), pauseSize.getHeight() );
        playPause.setPreferredSize( new Dimension( maxWidth, maxHeight ) );
    }

    /**
     * Gets the clock that's being controlled.
     * 
     * @return IClock
     */
    protected IClock getClock() {
        return clock;
    }
    
    /**
     * Adds a component to the sub-panel which contains the main content for this control panel.
     * @param control
     */
    public void addControl(JComponent control){
        buttonPanel.add(control);
    }
    
    /**
     * Adds a component to the left of the sub-panel which contains the main content for this control panel.
     * @param control
     */
    public void addControlToLeft(JComponent control){
        buttonPanel.add(control,0);
    }

    /**
     * Enables or disables the clock control panel.
     * When disabled, all buttons (play/pause/step) are also disabled.
     * When enabled, the buttons are enabled to correspond to the clock state.
     *
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateButtons();
    }
    
    public void addToLeft( JComponent component ) {
        add( component, BorderLayout.WEST );
    }
    
    public void addToRight( JComponent component ) {
        add( component, BorderLayout.EAST );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Updaters
    //
    
    /*
     * Updates the state of the buttons to correspond to
     * the state of the clock and the control panel.
     */
    private void updateButtons() {
        boolean enabled = isEnabled();
        boolean isPaused = clock.isPaused();
        if( isPaused ) {
            playPause.setText( playString );
            playPause.setIcon( playIcon );
        }
        else {
            playPause.setText( pauseString );
            playPause.setIcon( pauseIcon );
        }
        playPause.setEnabled( enabled );
        step.setEnabled( enabled && isPaused );
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Event handlers
    //

    public void clockTicked( ClockEvent clockEvent ) {
    }

    public void clockStarted( ClockEvent clockEvent ) {
        updateButtons();
    }

    public void clockPaused( ClockEvent clockEvent ) {
        updateButtons();
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}
