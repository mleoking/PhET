// Copyright 2002-2011, University of Colorado

/**
 * Class: TestArrows
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 10, 2004
 */
package edu.colorado.phet.common.phetgraphics.test.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.BasicGraphicsSetup;

public class TestArrows {
    static double theta = 0;
    private static Arrow arrow;
    private static PhetShapeGraphic shapeGraphic;

    final static int x0 = 400;
    final static int y0 = 400;
    final static double r = 200;

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Arrow test" );

        final ApparatusPanel p = new ApparatusPanel();
        p.addGraphicsSetup( new BasicGraphicsSetup() );

        GraphicLayerSet compositeGraphic = new GraphicLayerSet( p );
        p.addGraphic( compositeGraphic );

        arrow = new Arrow( new Point( 200, 300 ), new Point( x0, y0 ), 100, 100, 35, .5, false );
        shapeGraphic = new PhetShapeGraphic( p, arrow.getShape(), Color.blue );
        SwingClock clock = new SwingClock( 30, 1.0 );
//        clock.addClockListener( new ClockAdapter() {
//            public void clockTicked( AbstractClock c, double dt ) {
//                theta += Math.PI / 128;
//                double x = x0 + r * Math.cos( theta );
//                double y = y0 + r * Math.sin( theta );
//                arrow.setTipLocation( new Point2D.Double( x, y ) );
//                shapeGraphic.setShape( new Area( arrow.getShape() ) );
//            }
//        } );
//        ClockTickListener tickListener = new ClockTickListener() {
//            public void clockTicked( ClockEvent event ) {
//                theta += Math.PI / 128;
//                double x = x0 + r * Math.cos( theta );
//                double y = y0 + r * Math.sin( theta );
//                arrow.setTipLocation( new Point2D.Double( x, y ) );
//                shapeGraphic.setShape( new Area( arrow.getShape() ) );
//            }
//        };
//        clock.addClockTickListener( tickListener );
        clock.addClockListener( new MyListener() );
        shapeGraphic.setCursorHand();
        shapeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                arrow.setTailLocation( translationEvent.getMouseEvent().getPoint() );
            }
        } );

        Font font = new PhetFont( Font.BOLD, 24 );
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

    public static class MyListener extends ClockAdapter {

        public void clockTicked( ClockEvent event ) {
            theta += Math.PI / 128;
//            System.out.println( "event = " + event );
            double x = x0 + r * Math.cos( theta );
            double y = y0 + r * Math.sin( theta );
            arrow.setTipLocation( new Point2D.Double( x, y ) );
            shapeGraphic.setShape( new Area( arrow.getShape() ) );
        }
    }
}
