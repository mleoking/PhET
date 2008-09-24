/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor.phetcommon.model.clock.AbstractClock;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 10:24:21 PM
 */
public class ApplicationModelControlPanel extends JPanel {
    JButton play;
    JButton pause;
    JButton step;
    AbstractClock clock;
    private JButton logoButton;

    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public ApplicationModelControlPanel( AbstractClock runner ) throws IOException {
        this( runner, null );
    }

    public ApplicationModelControlPanel( final AbstractClock runner, final Object rh ) throws IOException {
        this.clock = runner;
        if ( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
//        ImageLoader cil = new ImageLoader();

        String root = "clock/";
        BufferedImage playU = PhetCommonResources.getInstance().getImage( root + "Play24.gif" );
        BufferedImage pauseU = PhetCommonResources.getInstance().getImage( root + "Pause24.gif" );
        BufferedImage stepU = PhetCommonResources.getInstance().getImage( root + "StepForward24.gif" );
        ImageIcon playIcon = new ImageIcon( playU );
        ImageIcon pauseIcon = new ImageIcon( pauseU );
        ImageIcon stepIcon = new ImageIcon( stepU );
//        this.rh = rh;
        play = new JButton( SimStrings.get( "ApplicationModelControlPanel.PlayButton" ), playIcon );
        pause = new JButton( SimStrings.get( "ApplicationModelControlPanel.PauseButton" ), pauseIcon );
        step = new JButton( SimStrings.get( "ApplicationModelControlPanel.StepButton" ), stepIcon );
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

        ImageIcon logo = new ImageIcon( PhetCommonResources.getInstance().getImage( "logos/phet-logo.jpg" ) );
        logoButton = new JButton( logo );
        logoButton.setToolTipText( SimStrings.get( "ApplicationModelControlPanel.LogoButtonToolTipText" ) );
        logoButton.setPreferredSize( new Dimension( logo.getIconWidth() + 12, logo.getIconHeight() + 12 ) );
//        this.add( logoButton, BorderLayout.EAST );
        logoButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // Get the frame
                Component c = (Component) e.getSource();
                Component frame = SwingUtilities.getRoot( c );
                PhetFrame phetFrame = (PhetFrame) frame;
                ApplicationView view = phetFrame.getApp().getApplicationView();
                view.setFullScreen( true );
            }
        } );

        play.setEnabled( false );
        pause.setEnabled( true );
    }

}
