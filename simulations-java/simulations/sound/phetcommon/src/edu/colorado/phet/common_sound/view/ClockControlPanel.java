/*Copyrig/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_sound.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * ClockControlPanel
 *
 * @author ?
 * @version $Revision$
 */
public class ClockControlPanel extends JPanel {
    private JButton play;
    private JButton pause;
    private JButton step;
    private IClock clock;

    public ClockControlPanel( final IClock clock ) throws IOException {
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                updateButtons( clockEvent.getClock().isPaused() );
            }
            public void clockPaused( ClockEvent clockEvent ) {
                updateButtons( clockEvent.getClock().isPaused() );
            }
        });
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        ImageLoader cil = new ImageLoader();

        String root = "sound/images/icons/java/media/";
        BufferedImage playU = cil.loadImage( root + "Play24.gif" );
        BufferedImage pauseU = cil.loadImage( root + "Pause24.gif" );
        BufferedImage stepU = cil.loadImage( root + "StepForward24.gif" );
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
            }
        } );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.pause();
            }
        } );

        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.stepClockWhilePaused();
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

    /**
     * Enables or disables the clock control panel.
     * When disabled, all buttons (play/pause/step) are also disabled.
     * When enabled, the buttons are enabled to correspond to the clock state.
     *
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        boolean isPaused = clock.isPaused();
        play.setEnabled( enabled && isPaused );
        pause.setEnabled( enabled && !isPaused );
        step.setEnabled( enabled && isPaused );
    }


    private void updateButtons( boolean isPaused ) {
        play.setEnabled( isPaused );
        pause.setEnabled( !isPaused );
        step.setEnabled( isPaused );
    }
}
