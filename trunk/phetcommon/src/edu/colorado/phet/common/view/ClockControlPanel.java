/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.components.media.Resettable;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 10:24:21 PM
 * Copyright (runner) May 18, 2003 by Sam Reid
 */
public class ClockControlPanel extends JPanel {
    JButton play;
    JButton pause;
    JButton step;
    AbstractClock clock;
    private JButton logoButton;

    public ClockControlPanel( AbstractClock runner ) throws IOException {
        this( runner, null );
    }

    public ClockControlPanel( final AbstractClock runner, final Resettable rh ) throws IOException {
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
        play = new JButton( "Play", playIcon );
        pause = new JButton( "Pause", pauseIcon );
        step = new JButton( "Step", stepIcon );
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

        ImageIcon logo = new ImageIcon( new ImageLoader().loadImage( "images/Phet-logo-48x48.gif" ) );
        logoButton = new JButton( logo );
        logoButton.setToolTipText( "Full Frame" );
        logoButton.setPreferredSize( new Dimension( logo.getIconWidth() + 12, logo.getIconHeight() + 12 ) );
        this.add( logoButton, BorderLayout.EAST );
        //        logoButton.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                // Get the frame
        //                Component c = (Component)e.getSource();
        //                Component frame = SwingUtilities.getRoot( c );
        //                PhetFrame phetFrame = (PhetFrame)frame;
        //                ApplicationView view = phetFrame.getApp().getApplicationView();
        //                view.setFullScreen( true );
        //            }
        //        } );

        play.setEnabled( false );
        pause.setEnabled( true );
    }

    public JButton getLogoButton() {
        return logoButton;
    }
}
