/** Sam Reid*/
package edu.colorado.phet.chart.test;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.chart.controllers.ChartCursor;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 18, 2004
 * Time: 10:42:48 PM
 * Copyright (c) Oct 18, 2004 by Sam Reid
 */
public class CursorRectangleTest {
    private static double x = 0;

    public static void main( String[] args ) {
        ApparatusPanel apparatusPanel = new ApparatusPanel() {
            public void repaint( long tm, int x, int y, int width, int height ) {
                super.repaint( tm, x, y, width, height );
            }

            public void repaint( Rectangle r ) {
                super.repaint( r );
            }

            public void repaint() {
                super.repaint();
            }

            public void repaint( int x, int y, int width, int height ) {
                super.repaint( x, y, width, height );
            }
        };
        AbstractClock clock = new SwingTimerClock( 1, 50, true );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( apparatusPanel, clock );
        repaintDebugGraphic.setTransparency( 200 );
        clock.start();
        apparatusPanel.addGraphic( repaintDebugGraphic, 5 );
        final Chart ch = new Chart( apparatusPanel, new Range2D( -10, -10, 10, 10 ), new Rectangle( 50, 50, 400, 400 ) );
        x = ch.getRange().getMinX();
        apparatusPanel.addGraphic( ch );
        VerticalChartSlider verticalChartSlider = new VerticalChartSlider( ch );

//        HorizontalCursor cursor = new HorizontalCursor( ch, new Color( 200, 200, 0, 150 ), new Color( 150, 150, 0, 50 ), 8 );
        ChartCursor cursor = new ChartCursor( apparatusPanel, ch, new Color( 200, 200, 255, 120 ), new Color( 150, 150, 255, 255 ), 8 );
        cursor.addListener( new ChartCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                System.out.println( "modelX = " + modelX );
            }
        } );
//        HorizontalCursor cursor = new HorizontalCursor( ch, new Color( 200, 200, 0, 200 ), new Color( 250, 250, 255, 200 ), 8 );
        apparatusPanel.addGraphic( cursor, 100 );

        ch.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
        final DataSet ds = new DataSet();
        DataSetGraphic dsg = new LinePlot( apparatusPanel, ch, ds, new BasicStroke( 3 ), Color.red );
        ch.addDataSetGraphic( dsg );
        verticalChartSlider.addListener( new VerticalChartSlider.Listener() {
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
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent cte ) {
                ds.addPoint( x, 10 * Math.sin( x / 3 ) );
                x += 0.07;
                if( x > ch.getRange().getMaxX() ) {
                    x = ch.getRange().getMinX();
                }
            }
        } );
    }
}
