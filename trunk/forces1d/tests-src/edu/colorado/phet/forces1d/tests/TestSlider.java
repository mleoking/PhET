/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.forces1d.common.SliderPhetGraphic;
import edu.colorado.phet.forces1d.common.TitleGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 1:19:40 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 */

public class TestSlider {
    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final BaseModel model = new BaseModel();
        ApparatusPanel2 panel = new ApparatusPanel2( model, clock );
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        panel.addRepaintDebugGraphic( clock );
        final SliderPhetGraphic sliderPhetGraphic = new SliderPhetGraphic( panel, 0, 100, 50 );
        panel.addGraphic( sliderPhetGraphic );
        JFrame jf = new JFrame( "Test Frame" );
        jf.setContentPane( panel );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setSize( 600, 600 );
        jf.show();
        sliderPhetGraphic.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "Changed,  value=" + sliderPhetGraphic.getValue() );
            }
        } );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                model.stepInTime( event.getDt() );
            }
        } );
        clock.start();

        final TitleGraphic titleGraphic = new TitleGraphic( panel, "Velocity = 0 m/s", sliderPhetGraphic );
        panel.addGraphic( titleGraphic );
        sliderPhetGraphic.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = sliderPhetGraphic.getValue();
                titleGraphic.setTitle( "Velocity = " + value + " m/s" );
            }
        } );
        sliderPhetGraphic.setLocation( 100, 100 );
    }
}
