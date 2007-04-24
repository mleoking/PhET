/*  */
package edu.colorado.phet.movingman.tests;

import edu.colorado.phet.chart_movingman.Range2D;
import edu.colorado.phet.common_movingman.model.clock.AbstractClock;
import edu.colorado.phet.common_movingman.model.clock.ClockTickEvent;
import edu.colorado.phet.common_movingman.model.clock.ClockTickListener;
import edu.colorado.phet.common_movingman.model.clock.SwingTimerClock;
import edu.colorado.phet.common_movingman.view.ApparatusPanel2;
import edu.colorado.phet.common_movingman.view.BasicGraphicsSetup;
import edu.colorado.phet.common_movingman.view.PhetLookAndFeel;
import edu.colorado.phet.common_movingman.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common_movingman.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common_movingman.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 28, 2005
 * Time: 11:13:04 AM
 *
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
