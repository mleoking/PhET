/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * ClockControlPanel
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class ClockControlPanel extends JPanel implements ClockStateListener {
    
    private AbstractClock clock;
    
    private JButton playPause;
    private JButton step;
    
    ImageIcon playIcon;
    ImageIcon pauseIcon;
    
    String playString;
    String pauseString;

    public ClockControlPanel( final AbstractClock runner ) throws IOException {
        this.clock = runner;
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        ImageLoader cil = new ImageLoader();

        // Button labels
        playString = SimStrings.get( "Common.ClockControlPanel.Play" );
        pauseString = SimStrings.get( "Common.ClockControlPanel.Pause" );
        String stepString = SimStrings.get( "Common.ClockControlPanel.Step" );
        
        // Button icons
        String root = "images/icons/java/media/";
        BufferedImage playU = cil.loadImage( root + "Play24.gif" );
        BufferedImage pauseU = cil.loadImage( root + "Pause24.gif" );
        BufferedImage stepU = cil.loadImage( root + "StepForward24.gif" );
        playIcon = new ImageIcon( playU );
        pauseIcon = new ImageIcon( pauseU );
        ImageIcon stepIcon = new ImageIcon( stepU );
        
        // Play/Pause button
        // Set this button to its maximum size so that the contents of
        // the control panel don't horizontally shift as the button state changes.
        {
            playPause = new JButton();
            
            // Get dimensions for "Play" state
            playPause.setText( playString );
            playPause.setIcon( playIcon );
            Dimension playSize = playPause.getPreferredSize();
            
            // Get dimensions for "Pause" state
            playPause.setText( pauseString );
            playPause.setIcon( pauseIcon );
            Dimension pauseSize = playPause.getPreferredSize();
            
            // Set max dimensions
            int maxWidth = (int) Math.max( playSize.getWidth(), pauseSize.getWidth() );
            int maxHeight = (int) Math.max( playSize.getHeight(), pauseSize.getHeight() );
            playPause.setPreferredSize( new Dimension( maxWidth, maxHeight ) );
        }
        
        // Step button
        step = new JButton( stepString, stepIcon );
        step.setEnabled( false );

        playPause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.setPaused( !clock.isPaused() );
                updateButtons();
            }
        } );

        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.tickOnce();
            }
        } );

        setLayout( new BorderLayout() );
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( playPause );
        buttonPanel.add( step );
        this.add( buttonPanel, BorderLayout.CENTER );

        updateButtons();
    }

    /*
     * Updates the state of the buttons to correspond to
     * the state of the clock and the control panel.
     */
    private void updateButtons() {
        boolean isPaused = clock.isPaused();
        if( isPaused ) {
            playPause.setText( playString );
            playPause.setIcon( playIcon );
        }
        else {
            playPause.setText( pauseString );
            playPause.setIcon( pauseIcon );
        }
        step.setEnabled( isPaused );
    }
    
    public void delayChanged( int waitTime ) {
    }

    public void dtChanged( double dt ) {
    }

    public void threadPriorityChanged( int priority ) {
    }

    public void pausedStateChanged( boolean b ) {
        updateButtons();
    }
}
