/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.phetcomponents;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.view.phetcomponents.TitleGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 1:19:40 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 */

public class TestButton {
    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final BaseModel model = new BaseModel();
        ApparatusPanel2 panel = new ApparatusPanel2( clock );
//        ApparatusPanel panel = new ApparatusPanel( );
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
                titleGraphic.setTitle( "Velocity Changed." );
//                button.setBackgroundColor( Color.green);
            }
        } );
        button.setLocation( 100, 100 );
        button.setBackgroundColor( Color.blue );
//        button.setFont( new Font( "Lucida Sans",Font.PLAIN, 12) );
        button.setFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
        button.setBorderStroke( new BasicStroke( 2 ) );
    }
}
