/**
 * Class: TestCompositeGraphic
 * Package: edu.colorado.phet.common.tests.basicgraphics
 * Author: Another Guy
 * Date: Nov 12, 2004
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class TestGraphicLayerSet {
    public static final void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel() {
            public void repaint( int x, int y, int width, int height ) {
                paintImmediately( x, y, width, height );
            }
        };
//        final GraphicLayerSet compositeGraphic = new GraphicLayerSet( ap );
        final CompositePhetGraphic compositeGraphic = new CompositePhetGraphic( ap );
        final PhetShapeGraphic circleGraphic = new PhetShapeGraphic( ap, new Ellipse2D.Double( 50, 50, 75, 300 ), Color.blue );
        PhetShapeGraphic squareGraphic = new PhetShapeGraphic( ap, new Rectangle2D.Double( 400, 400, 50, 50 ), Color.red );
        compositeGraphic.addGraphic( circleGraphic );
        compositeGraphic.addGraphic( squareGraphic );

        ap.addGraphic( compositeGraphic );

        JFrame frame = new JFrame();
        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        frame.setVisible( true );

        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( ap, clock );
        repaintDebugGraphic.setActive( true );
        clock.start();

//        clock.addClockTickListener( new ClockTickListener() {
//            double startTime = System.currentTimeMillis();
//
//            public void clockTicked( AbstractClock c, double dt ) {
//                //                circleGraphic.translate( 1, 0 );
//                compositeGraphic.setLocation( compositeGraphic.getLocation().x + 1, compositeGraphic.getLocation().y );
//
//                if( System.currentTimeMillis() - startTime > 2000 ) {
//                    compositeGraphic.setVisible( false );
//                }
//            }
//        } );
        compositeGraphic.setCursorHand();
        compositeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                compositeGraphic.setLocation( compositeGraphic.getLocation().x + translationEvent.getDx(), compositeGraphic.getLocation().y + translationEvent.getDy() );
            }
        } );
    }
}
