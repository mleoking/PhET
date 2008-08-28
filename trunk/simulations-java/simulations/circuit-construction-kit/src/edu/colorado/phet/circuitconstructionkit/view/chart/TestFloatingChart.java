/*  */
package edu.colorado.phet.circuitconstructionkit.view.chart;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 10:29:28 AM
 *
 */

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.view.chart.AbstractFloatingChart;
import edu.colorado.phet.circuitconstructionkit.view.chart.SingleTerminalFloatingChart;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

public class TestFloatingChart {
    private JFrame frame;
    private SwingClock clock;

    public TestFloatingChart() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas contentPane = new PhetPCanvas();
        clock = new SwingClock( 30, 1 );
        AbstractFloatingChart floatingChart = new SingleTerminalFloatingChart.Piccolo( contentPane, "floating chart test", new AbstractFloatingChart.ValueReader() {
            public double getValue( double x, double y ) {
                double v = y;
//                System.out.println( "v = " + v );
                return v;
            }
        }, clock, null );//todo can't test without CCKSImulationpanel now
        floatingChart.setOffset( 300, 200 );
        contentPane.getLayer().addChild( floatingChart );
        contentPane.setPanEventHandler( null );
        frame.setContentPane( contentPane );
    }

    public static void main( String[] args ) {
        new TestFloatingChart().start();
    }

    private void start() {
        clock.start();
        frame.setVisible( true );
    }
}

