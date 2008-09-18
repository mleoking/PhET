/*, 2003.*/
package edu.colorado.phet.common_1200.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_1200.model.clock.AbstractClock;
import edu.colorado.phet.common_1200.model.clock.ClockStateListener;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 10:24:21 PM
 */
public class ClockControlPanel extends JPanel implements ClockStateListener {
    private JButton play;
    private JButton pause;
    private JButton step;
    private AbstractClock clock;

    public ClockControlPanel( final AbstractClock runner ) throws IOException {
        this.clock = runner;
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        ImageLoader cil = new ImageLoader();

        String root = "images/icons/java/media/";
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
                clock.setPaused( false );
                play.setEnabled( false );
                pause.setEnabled( true );
                step.setEnabled( false );
            }
        } );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.setPaused( true );
                play.setEnabled( true );
                pause.setEnabled( false );
                step.setEnabled( true );
            }
        } );

        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.tickOnce();
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

    private void setPausedState( boolean state ) {
        clock.setPaused( state );
        play.setEnabled( state );
        pause.setEnabled( state );
        step.setEnabled( state );
    }

    public void delayChanged( int waitTime ) {
    }

    public void dtChanged( double dt ) {
    }

    public void threadPriorityChanged( int priority ) {
    }

    public void pausedStateChanged( boolean b ) {
        play.setEnabled( b );
        pause.setEnabled( !b );
        step.setEnabled( b );
    }
}
