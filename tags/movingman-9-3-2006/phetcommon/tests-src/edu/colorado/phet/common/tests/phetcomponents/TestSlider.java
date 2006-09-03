/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.phetcomponents;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetSlider;
import edu.colorado.phet.common.view.phetcomponents.TitleGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 1:19:40 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 *
 * @deprecated Use JSlider wrapped in a PhetJComponent.
 */

public class TestSlider {
    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final BaseModel model = new BaseModel();
        ApparatusPanel2 panel = new ApparatusPanel2( clock );
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        panel.addRepaintDebugGraphic( clock );
        final PhetSlider phetSlider = new PhetSlider( panel, 0, 100, 50, true );
        phetSlider.setBackgroundColor( Color.orange );
//        phetSlider.getBackgroundGraphic().setVisible( false );
        panel.addGraphic( phetSlider );
        JFrame jf = new JFrame( "Test Frame" );
        jf.setContentPane( panel );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setSize( 600, 600 );
        jf.show();
        phetSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "Changed,  value=" + phetSlider.getValue() );
            }
        } );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                model.stepInTime( event.getDt() );
            }
        } );
        clock.start();

        final TitleGraphic titleGraphic = new TitleGraphic( panel, "Velocity = 0 m/s", phetSlider );
        panel.addGraphic( titleGraphic );
        phetSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = phetSlider.getValue();
                titleGraphic.setTitle( "Velocity = " + value + " m/s" );
            }
        } );
        phetSlider.setLocation( 100, 100 );
        phetSlider.transform( AffineTransform.getRotateInstance( Math.PI / 12 ) );
    }
}
