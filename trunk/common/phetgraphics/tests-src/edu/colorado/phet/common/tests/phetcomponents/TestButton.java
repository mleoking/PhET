/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests.phetcomponents;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.view.phetcomponents.TitleGraphic;
import edu.colorado.phet.common.view.util.BasicGraphicsSetup;

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
        IClock clock = new SwingClock( 30, 1.0 );
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
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                model.update( event );
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