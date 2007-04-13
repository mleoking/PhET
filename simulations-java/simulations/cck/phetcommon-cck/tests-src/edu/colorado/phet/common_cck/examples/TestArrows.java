///**
// * Class: TestArrows
// * Package: edu.colorado.phet.common.examples
// * Author: Another Guy
// * Date: May 10, 2004
// */
//package edu.colorado.phet.common_cck.examples;
//
//import edu.colorado.phet.common_cck.model.clock.AbstractClock;
//import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
//import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
//import edu.colorado.phet.common_cck.view.CompositeGraphic;
//import edu.colorado.phet.common_cck.view.CompositeInteractiveGraphicMouseDelegator;
//import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
//import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;
//import edu.colorado.phet.common_cck.view.graphics.TextGraphic;
//import edu.colorado.phet.common_cck.view.graphics.mousecontrols.Translatable;
//import edu.colorado.phet.common_cck.view.graphics.shapes.Arrow;
//import edu.colorado.phet.common_cck.view.util.GraphicsUtil;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.geom.Point2D;
//
//public class TestArrows {
//    static double theta = 0;
//
//    public static void main( String[] args ) {
//        JFrame frame = new JFrame( "Arrow test" );
//
//        final CompositeGraphic compositeGraphic = new CompositeGraphic();
//        final JPanel p = new JPanel() {
//            protected void paintComponent( Graphics g ) {
//                super.paintComponent( g );
//                Graphics2D g2 = (Graphics2D)g;
//                GraphicsUtil.setAntiAliasingOn( g2 );
//                compositeGraphic.paint( g2 );
//            }
//        };
//
//        final int x0 = 400;
//        final int y0 = 400;
//        final double r = 200;
//        final Arrow arrow = new Arrow( new Point( 200, 300 ), new Point( x0, y0 ), 100, 100, 35, .5, false );
//        final ShapeGraphic sg = new ShapeGraphic( arrow.getShape(), Color.blue );
//        SwingTimerClock clock = new SwingTimerClock( 1, 30, true );
//        clock.addClockTickListener( new ClockTickListener() {
//            public void clockTicked( AbstractClock c, double dt ) {
//                theta += Math.PI / 128;
//                double x = x0 + r * Math.cos( theta );
//                double y = y0 + r * Math.sin( theta );
//                Rectangle viewBounds = arrow.getShape().getBounds();
//                arrow.setTipLocation( new Point2D.Double( x, y ) );
//                Rectangle after = arrow.getShape().getBounds();
////                FastPaint.fastRepaint( p, viewBounds, after );
//                Rectangle union = viewBounds.union( after );
//                p.repaint( union.x, union.y, union.width, union.height );
//                //                p.repaint( );
//            }
//        } );
//        DefaultInteractiveGraphic dig = new DefaultInteractiveGraphic( sg );
//        dig.addCursorHandBehavior();
//        dig.addTranslationBehavior( new Translatable() {
//
//            public void translate( double dx, double dy ) {
//                arrow.translate( dx, dy );
//                //                AffineTransform at = AffineTransform.getTranslateInstance( dx, dy );
//                //                Shape trf = at.createTransformedShape( sg.getShape() );
//                //                sg.setShape( trf );
//                //                p.repaint();
//            }
//
//        } );
//
//        Font font = new Font( "Lucida Sans", Font.BOLD, 24 );
//        TextGraphic textGraphic = new TextGraphic( "Hello PhAT", font, 200, 100, Color.blue );
//        compositeGraphic.addGraphic( textGraphic, 10 );
//        compositeGraphic.addGraphic( dig );
//        CompositeInteractiveGraphicMouseDelegator delegator = new CompositeInteractiveGraphicMouseDelegator( compositeGraphic, this );
//        p.addMouseListener( delegator );
//        p.addMouseMotionListener( delegator );
//
//        frame.setContentPane( p );
//        frame.setSize( 600, 600 );
//        frame.setVisible( true );
//
//        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//
//        clock.start();
//    }
//}
