/**
 * Class: ParallaxReticle
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 8:17:53 AM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;

import java.awt.*;
import java.awt.geom.*;

public class ParallaxReticle implements Graphic {
//public class ParallaxReticle extends DefaultInteractiveGraphic /*implements Translatable*/ {

//    private Point2D.Double location = new Point2D.Double();
//    private AffineTransform atx = new AffineTransform();
//    private Reticle reticle;
    private Container container;
    private Rectangle2D.Double bounds;
    private double viewAngle;

    public ParallaxReticle( Container container, Rectangle2D.Double bounds, double viewAngle ) {
//        super( null, null );
        this.container = container;
        this.bounds = bounds;
        this.viewAngle = viewAngle;
//        reticle = new Reticle();
//        super.setGraphic( reticle );
//        super.setBoundary( reticle );
//        this.addCursorHandBehavior();
//        this.addTranslationBehavior( this );
    }

//    public void translate( double dx, double dy ) {
//        setLocation( location.getX() + dx, location.getY() + dy );
//    }
//
//    public void setLocation( Point2D.Double location ) {
//        setLocation( location.getX(), location.getY() );
//    }
//
//    public void setLocation( double x, double y ) {
//        this.location.setLocation( x, y );
//        container.repaint();
//    }


//    public void paint( Graphics2D g ) {
////        atx.setToIdentity();
////        atx.translate( location.getX(), location.getY() );
//        AffineTransform orgTx = g.getTransform();
////        g.transform( atx );
////        super.paint( g );
//        g.setTransform( orgTx );
//    }
//
//
//    //
//    // Inner classes
//    //
//    private class Reticle implements Graphic /*, Boundary*/ {
//
        private Color color = Color.yellow;
        private int length = 200;
        private int height = 400;
        private Stroke stroke = new BasicStroke( 1f );
        private Line2D.Double baseLine = new Line2D.Double( 0, 0, length, 0 );
        private int numMajorTicks = 3;
        private int numMinorTicks = 10;
        private int majorTickIncr = length / ( numMajorTicks - 1 );
        private int minorTickIncr = majorTickIncr / numMinorTicks;
        private Point2D.Double testPoint = new Point2D.Double();

        public Rectangle2D.Double getBounds() {
            return bounds;
        }

        /**
         * This method assumes that (0,0) is in the center of the view area
         * @param g
         */
        public void paint( Graphics2D g ) {

            g.setColor( color );
            g.setStroke( stroke );


//            Rectangle bounds = container.getBounds();
            // Draw the baseline
            baseLine.setLine( -bounds.getWidth() / 2, 0, bounds.getWidth() / 2, 0 );
            g.draw( baseLine );

            // Draw the tick marks
            height = (int)bounds.getHeight();

            int yMajor = height / 3;
            int yMinor = height / 4;

            double d = ( bounds.getWidth() / 2 ) / Math.tan( viewAngle / 2 );
            for( double phi = 0; phi <= viewAngle / 2; phi += 2 * Math.PI / 180 ) {
//                double b = d * phi;
                double b = d * Math.tan( phi );
                g.drawLine( (int)b, -yMajor, (int)b, yMajor );
                g.drawLine( (int)-b, -yMajor, (int)-b, yMajor );
            }

//            for( int i = 0; i < numMajorTicks - 1; i++ ) {
//                int xMajor = i * majorTickIncr;
//                g.drawLine( xMajor, -yMajor, xMajor, yMajor );
//
//                for( int j = 1; j < numMinorTicks; j++ ) {
//                    int xMinor = xMajor + j * minorTickIncr;
//                    g.drawLine( xMinor, -yMinor, xMinor, yMinor );
//                }
//            }
//            g.drawLine( length, -yMajor, length, yMajor );

        }

//        public boolean contains( int x, int y ) {
//            try {
//                atx.inverseTransform( new Point2D.Double( (double)x, (double)y ), testPoint );
//            }
//            catch( NoninvertibleTransformException e ) {
//                e.printStackTrace();
//            }
//            return bounds.contains( testPoint );
//        }
//    }
}
