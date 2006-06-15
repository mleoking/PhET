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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;

/**
 * ClockControlPanel
 *
 * @author ?
 * @version $Revision$
 */
public class ClockControlPanel extends JPanel implements ClockListener {
    
    private static final String IMAGES_DIRECTORY = "images/icons/java/media/";
    
    // These image filenames may be used by sim-specific classes that have custom clock control panels!
    public static final String IMAGE_PAUSE = IMAGES_DIRECTORY + "Pause24.gif";
    public static final String IMAGE_PLAY = IMAGES_DIRECTORY + "Play24.gif";
    public static final String IMAGE_STEP = IMAGES_DIRECTORY + "StepForward24.gif";
    public static final String IMAGE_REWIND = IMAGES_DIRECTORY + "Rewind24.gif";
    public static final String IMAGE_FAST_FORWARD = IMAGES_DIRECTORY + "FastForward24.gif";
    public static final String IMAGE_STOP = IMAGES_DIRECTORY + "Stop24.gif";
    
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

        BufferedImage playU = loadImage( IMAGE_PLAY );
        BufferedImage pauseU = loadImage( IMAGE_PAUSE );
        BufferedImage stepU = loadImage( IMAGE_STEP );
        ImageIcon playIcon = new ImageIcon( playU );
        ImageIcon pauseIcon = new ImageIcon( pauseU );
        ImageIcon stepIcon = new ImageIcon( stepU );
        play = new JButton( SimStrings.get( "Common.ClockControlPanel.Play" ), playIcon );
        pause = new JButton( SimStrings.get( "Common.ClockControlPanel.Pause" ), pauseIcon );
        step = new JButton( SimStrings.get( "Common.ClockControlPanel.Step" ), stepIcon );
        step.setEnabled( false );

        SwingUtils.fixButtonOpacity( play );
        SwingUtils.fixButtonOpacity( pause );
        SwingUtils.fixButtonOpacity( step );

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

        stateChanged( clock.isPaused() );
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
    
    public void addToLeft( JComponent component ) {
        add( component, BorderLayout.WEST );
    }
    
    public void addToRight( JComponent component ) {
        add( component, BorderLayout.EAST );
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
