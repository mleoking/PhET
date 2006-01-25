/*Copyrig/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
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
 * @author ?
 * @version $Revision$
 */
public class ClockControlPanel extends JPanel implements ClockListener {
    private JButton play;
    private JButton pause;
    private JButton step;
    private IClock clock;

    public ClockControlPanel( final IClock clock ) {
        this.clock = clock;
        clock.addClockListener( this );
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }

        String root = "images/icons/java/media/";
        BufferedImage playU = loadImage( root + "Play24.gif" );
        BufferedImage pauseU = loadImage( root + "Pause24.gif" );
        BufferedImage stepU = loadImage( root + "StepForward24.gif" );
        ImageIcon playIcon = new ImageIcon( playU );
        ImageIcon pauseIcon = new ImageIcon( pauseU );
        ImageIcon stepIcon = new ImageIcon( stepU );
        play = new JButton( SimStrings.get( "Common.ClockControlPanel.Play" ), playIcon );
        pause = new JButton( SimStrings.get( "Common.ClockControlPanel.Pause" ), pauseIcon );
        step = new JButton( SimStrings.get( "Common.ClockControlPanel.Step" ), stepIcon );
        step.setEnabled( false );

        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.start();
                play.setEnabled( false );
                pause.setEnabled( true );
                step.setEnabled( false );
            }
        } );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.pause();
                play.setEnabled( true );
                pause.setEnabled( false );
                step.setEnabled( true );
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
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( play );
        buttonPanel.add( pause );
        buttonPanel.add( step );
        this.add( buttonPanel, BorderLayout.CENTER );

        play.setEnabled( false );
        pause.setEnabled( true );
    }

    private BufferedImage loadImage( String s ) {
        try {
            return ImageLoader.loadBufferedImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
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
        stateChanged( clock.isPaused() );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Event handlers
    //

    /*
    * Updates the state of the buttons to correspond to
    * the state of the clock and the control panel.
    */
    private void stateChanged( boolean isPaused ) {
        boolean enabled = isEnabled();
        play.setEnabled( enabled && isPaused );
        pause.setEnabled( enabled && !isPaused );
        step.setEnabled( enabled && isPaused );
    }

    public void clockTicked( ClockEvent clockEvent ) {
    }

    public void clockStarted( ClockEvent clockEvent ) {
        stateChanged( clockEvent.getClock().isPaused() );
    }

    public void clockPaused( ClockEvent clockEvent ) {
        stateChanged( clockEvent.getClock().isPaused() );
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}
