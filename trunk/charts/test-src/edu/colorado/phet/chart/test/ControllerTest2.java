/** Sam Reid*/
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.chart.controllers.ChartSlider;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.util.BasicGraphicsSetup;

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
        JFrame jFrame = new JFrame();
        PhetJComponent.init( jFrame );
        IClock clock = new SwingClock( 1, 30 );
        final ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
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
        ChartSlider chartSlider = new ChartSlider( apparatusPanel, ch, null, null );
//        chartSlider.getSlider().setBackground( );
        apparatusPanel.addGraphic( chartSlider );
        ch.getVerticalTicks().setMajorOffset( -chartSlider.getWidth() - 5, 0 );
        final DataSet ds = new DataSet();
        DataSetGraphic dsg = new LinePlot( apparatusPanel, ch, ds, new BasicStroke( 3 ), Color.red );
        ch.addDataSetGraphic( dsg );
        chartSlider.addListener( new ChartSlider.Listener() {
            public void valueChanged( double value ) {
                ds.addPoint( x, value );
                x += .07;
                if( x > ch.getRange().getMaxX() ) {
                    x = ch.getRange().getMinX();
                }
            }
        } );


        jFrame.setContentPane( apparatusPanel );
        jFrame.setSize( 600, 600 );
        jFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jFrame.setVisible( true );
    }
}
