/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.view.phetcomponents.PhetTextField;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.common.TitleGraphic;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 1:19:40 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 */

public class PlotDeviceTest {
    private static int clickCount = 0;

    public static void main( String[] args ) {
        ApparatusPanel panel = createPanel();


        JFrame jf = new JFrame( "Test Frame" );
        jf.setContentPane( panel );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setSize( 600, 600 );
        jf.show();


    }

    private static ApparatusPanel createPanel() {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final BaseModel model = new BaseModel();
        final ApparatusPanel2 panel = new ApparatusPanel2( clock );
//        ApparatusPanel panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        panel.addRepaintDebugGraphic( clock );
        final PhetButton button = new PhetButton( panel, "Test Button" );
        panel.addGraphic( button );


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
        ptf.transform( AffineTransform.getRotateInstance( Math.PI / 32 ) );

        final PlotDevice2 device = new PlotDevice2( panel, new Range2D( -10, -10, 10, 10 ) );
        device.setRegistrationPoint( RectangleUtils.getCenter( device.getBounds() ) );
//        panel.getGraphic().setRegistrationPoint( 300,300);
        panel.addGraphic( device );
        device.setLocation( 200, 200 );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                device.rotate( Math.PI/464);
                device.rotate( Math.PI / 464 );
//                panel.getGraphic().rotate( Math.PI/200);
            }
        } );
        device.rotate( Math.PI / 8 );
        timer.start();

        RepaintDebugGraphic.enable( panel, clock );
        return panel;
    }
}
