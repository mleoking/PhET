/** Sam Reid*/
package edu.colorado.phet.force1d_tag_chart.test;

import edu.colorado.phet.force1d_tag_chart.*;
import edu.colorado.phet.force1d_tag_chart.controllers.VerticalChartSlider2;
import edu.colorado.phet.common_force1d.model.clock.AbstractClock;
import edu.colorado.phet.common_force1d.model.clock.ClockTickEvent;
import edu.colorado.phet.common_force1d.model.clock.ClockTickListener;
import edu.colorado.phet.common_force1d.model.clock.SwingTimerClock;
import edu.colorado.phet.common_force1d.view.ApparatusPanel2;
import edu.colorado.phet.common_force1d.view.BasicGraphicsSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 18, 2004
 * Time: 10:42:48 PM
 * Copyright (c) Oct 18, 2004 by Sam Reid
 */
public class ControllerTest2 {
    private static double x = 0;

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                apparatusPanel.handleUserInput();
                apparatusPanel.paint();
            }
        } );
        apparatusPanel.setUseOffscreenBuffer( true );
        apparatusPanel.addGraphicsSetup( new BasicGraphicsSetup() );
        clock.start();
        final Chart ch = new Chart( apparatusPanel, new Range2D( -10, -10, 10, 10 ), new Rectangle( 50, 50, 400, 400 ) );
        x = ch.getRange().getMinX();
        apparatusPanel.addGraphic( ch );
        VerticalChartSlider2 verticalChartSlider = new VerticalChartSlider2( apparatusPanel, ch );
//        verticalChartSlider.getSlider().setBackground( );
        apparatusPanel.addGraphic( verticalChartSlider );
        ch.getVerticalTicks().setMajorOffset( -verticalChartSlider.getWidth() - 5, 0 );
        final DataSet ds = new DataSet();
        DataSetGraphic dsg = new LinePlot( ds, new BasicStroke( 3 ), Color.red );
        ch.addDataSetGraphic( dsg );
        verticalChartSlider.addListener( new VerticalChartSlider2.Listener() {
            public void valueChanged( double value ) {
                ds.addPoint( x, value );
                x += .07;
                if( x > ch.getRange().getMaxX() ) {
                    x = ch.getRange().getMinX();
                }
            }
        } );

        JFrame jFrame = new JFrame();
        jFrame.setContentPane( apparatusPanel );
        jFrame.setSize( 600, 600 );
        jFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jFrame.setVisible( true );
    }
}
