/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.tests;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 28, 2005
 * Time: 11:13:04 AM
 * Copyright (c) Mar 28, 2005 by Sam Reid
 */

public class TestPlotDevice {
    public static void main( String[] args ) {
        PhetLookAndFeel.setLookAndFeel();
        JFrame frame = new JFrame();
        frame.setSize( 800, 800 );
        AbstractClock clock = new SwingTimerClock( 1, 30 );

        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                apparatusPanel.handleUserInput();
                apparatusPanel.paint();
            }
        } );
        apparatusPanel.addGraphicsSetup( new BasicGraphicsSetup() );
        frame.setContentPane( apparatusPanel );
        final PhetGraphic plotDevice = new PlotDevice( apparatusPanel, new Range2D( 0, 0, 10, 10 ), "test", null, null );
        plotDevice.setLocation( 100, 100 );
        apparatusPanel.addGraphic( plotDevice );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        plotDevice.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                plotDevice.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        } );
        clock.start();
    }
}
