/**
 * Class: TestArrows
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 10, 2004
 */
package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class TestArrows {
    static double theta = 0;


    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Arrow test" );

        final ApparatusPanel p = new ApparatusPanel();
        p.addGraphicsSetup( new BasicGraphicsSetup() );

        GraphicLayerSet compositeGraphic = new GraphicLayerSet( p );
        p.addGraphic( compositeGraphic );

        final int x0 = 400;
        final int y0 = 400;
        final double r = 200;
        final Arrow arrow = new Arrow( new Point( 200, 300 ), new Point( x0, y0 ), 100, 100, 35, .5, false );
        final PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( p, arrow.getShape(), Color.blue );
        SwingTimerClock clock = new SwingTimerClock( 1, 30, true );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                theta += Math.PI / 128;
                double x = x0 + r * Math.cos( theta );
                double y = y0 + r * Math.sin( theta );
                arrow.setTipLocation( new Point2D.Double( x, y ) );
                shapeGraphic.setShape( new Area( arrow.getShape() ) );
            }
        } );
        shapeGraphic.setCursorHand();
        shapeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                arrow.setTailLocation( translationEvent.getMouseEvent().getPoint() );
            }
        } );

        Font font = new Font( "Lucida Sans", Font.BOLD, 24 );
        PhetTextGraphic textGraphic = new PhetTextGraphic( p, font, "Hello PhET", Color.blue, 200, 100 );
        compositeGraphic.addGraphic( textGraphic, 10 );
        compositeGraphic.addGraphic( shapeGraphic, 30 );
        frame.setContentPane( p );
        frame.setSize( 600, 600 );
        frame.setVisible( true );
        compositeGraphic.addGraphic( new RepaintDebugGraphic( p, clock ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        clock.start();
    }

}
