/**
 * Class: TestCompositeGraphic
 * Package: edu.colorado.phet.common.tests.basicgraphics
 * Author: Another Guy
 * Date: Nov 12, 2004
 */
package edu.colorado.phet.common.tests.basicgraphics;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class TestCompositeGraphic {
    public static final void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel() {
            public void repaint( int x, int y, int width, int height ) {
                paintImmediately( x, y, width, height );
            }
        };
        final CompositePhetGraphic compositePhetGraphic = new CompositePhetGraphic( ap );
        final PhetShapeGraphic circleGraphic = new PhetShapeGraphic( ap, new Ellipse2D.Double( 50, 50, 75, 300 ), Color.blue );
        PhetShapeGraphic squareGraphic = new PhetShapeGraphic( ap, new Rectangle2D.Double( 400, 400, 50, 50 ), Color.red );
        compositePhetGraphic.addGraphic( circleGraphic );
        compositePhetGraphic.addGraphic( squareGraphic );

        ap.addGraphic( compositePhetGraphic );

        JFrame frame = new JFrame();
        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        frame.setVisible( true );

        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( ap, clock );
        repaintDebugGraphic.setActive( true );
        clock.start();

        clock.addClockTickListener( new ClockTickListener() {
            double startTime = System.currentTimeMillis();

            public void clockTicked( AbstractClock c, double dt ) {
                //                circleGraphic.translate( 1, 0 );
                compositePhetGraphic.setLocation( compositePhetGraphic.getLocation().x + 1, compositePhetGraphic.getLocation().y );

                if( System.currentTimeMillis() - startTime > 2000 ) {
                    compositePhetGraphic.setVisible( false );
                }
            }
        } );
    }
}
