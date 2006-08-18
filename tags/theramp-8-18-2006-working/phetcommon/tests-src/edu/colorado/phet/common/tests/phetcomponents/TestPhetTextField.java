/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.phetcomponents;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.view.phetcomponents.PhetTextField;
import edu.colorado.phet.common.view.phetcomponents.TitleGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 1:19:40 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 *
 * @deprecated Use JTextField wrapped in a PhetJComponent.
 */

public class TestPhetTextField {
    private static int clickCount = 0;

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final BaseModel model = new BaseModel();
//        ApparatusPanel2 panel = new ApparatusPanel2( model, clock );
        ApparatusPanel panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        panel.addRepaintDebugGraphic( clock );
        final PhetButton button = new PhetButton( panel, "Test Button" );
        panel.addGraphic( button );

        JFrame jf = new JFrame( "Test Frame" );
        jf.setContentPane( panel );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setSize( 600, 600 );
        jf.show();
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                model.stepInTime( event.getDt() );
            }
        } );
        clock.start();

        final TitleGraphic titleGraphic = new TitleGraphic( panel, "Velocity = 0 m/s", button );
        panel.addGraphic( titleGraphic );

        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Button pressed!" );
                titleGraphic.setTitle( "Click Count=" + clickCount++ );
//                button.setBackgroundColor( Color.green);
            }
        } );
        button.setLocation( 100, 100 );
        button.setBackgroundColor( Color.blue );
//        button.setFont( new Font( "Lucida Sans",Font.PLAIN, 12) );
        button.setFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
        button.setBorderStroke( new BasicStroke( 2 ) );

        button.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                System.out.println( "e = " + e );
            }
        } );

        PhetButton button3 = new PhetButton( panel, "button3" );
        panel.addGraphic( button3 );
        button3.setLocation( 400, 100 );

        PhetTextField ptf = new PhetTextField( panel, new Font( "Lucida Sans", 0, 28 ), "Hello", Color.blue, 0, 0 );
        panel.addGraphic( ptf );
        ptf.setLocation( 100, 400 );
//        ptf.transform( AffineTransform.getRotateInstance( Math.PI / 32 ) );
    }
}
