/**
 * Class: FieldVector
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Jul 29, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.coreadditions.LRUCache;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A graphic for vectors
 */
public class FieldVector {

    //
    // Static fields and methods
    //

    // arrow parameters
    private static BasicStroke arrowStroke = new BasicStroke( 2 );
    // arrowhead parameters
    private static int maxArrowHeadWidth = 10;
    private static BasicStroke arrowHeadStroke = new BasicStroke( 1 );
    private static Polygon arrowHead = new Polygon();

    private final static int s_cacheSize = 200;
    private final static int s_maxCachedArrowLength = 200;
    private static LRUCache cache = new LRUCache( s_cacheSize );
    private static int miss;
    private static int hit;
    private static VectorSpec spec = new VectorSpec( 0, 0, null );

    public static BufferedImage getGraphic( double length, double theta, Color color ) {

        BufferedImage arrow = null;

        spec.setLength( length );
        spec.setTheta( theta );
        spec.setColor( color );
        if( length > 0 ) {
            arrow = (BufferedImage)cache.get( spec );
            if( arrow == null ) {

//                System.out.println( "miss: " + ++miss );

                // Create a new arrow and put it in the cache
                // Make sure theta is positive in the range 0 to 2pi
                theta %= Math.PI * 2;
                theta += Math.PI * 2;
                theta %= Math.PI * 2;
                arrow = createBuffImg( length, theta, color );
                VectorSpec newSpec = new VectorSpec( spec );
                // Don't cache really long arrows
                if( length < s_maxCachedArrowLength ) {
                    cache.put( newSpec, arrow );
                }
            }
            else {
//                System.out.println( "hit: " + ++hit );
            }
        }

        // Test of rotating arrows using transform
//        if( arrow != null ) {
//            AffineTransform tx = AffineTransform.getRotateInstance( theta, arrow.getWidth() / 2, arrow.getHeight() / 2 );
//            AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
//            arrow = op.filter( arrow, null );
//        }
        return arrow;
    }

    private static BufferedImage createBuffImg( double l, double theta, Color color ) {
        BufferedImage buffImg = null;
        if( l > 0 ) {

            int dx = Math.max( (int)( l * Math.abs( Math.cos( theta ) ) ), 10 );
            int dy = Math.max( (int)( l * Math.abs( Math.sin( theta ) ) ), 10 );
            buffImg = new BufferedImage( Math.max( dx, dy ) + 10,
                                         Math.max( dx, dy ) + 10,
                                         BufferedImage.TYPE_INT_ARGB );
            int xMid = buffImg.getWidth() / 2;
            int yMid = buffImg.getHeight() / 2;

            Graphics2D g2d = buffImg.createGraphics();
            g2d.setColor( color );
            float alpha = 0.5f;
            g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );

            // draw the line
            g2d.setStroke( arrowStroke );
            g2d.drawLine( xMid - (int)( l * Math.cos( theta ) / 2 ),
                          yMid - (int)( l * Math.sin( theta ) / 2 ),
                          xMid + (int)( l * Math.cos( theta ) / 2 ),
                          yMid + (int)( l * Math.sin( theta ) / 2 ) );

            // draw the arrowhead
            double w = Math.min( (int)( l / 2 ), maxArrowHeadWidth );
            arrowHead.reset();
            int xa = xMid + (int)( ( ( l + w ) / 2 ) * Math.cos( theta ) );
            int ya = yMid + (int)( ( ( l + w ) / 2 ) * Math.sin( theta ) );
            arrowHead.addPoint( xa, ya );

            double x3 = xa - w * Math.cos( theta );
            double y3 = ya - w * Math.sin( theta );
            double x4 = x3 + ( w / 2 ) * Math.sin( theta );
            double x5 = x3 - ( w / 2 ) * Math.sin( theta );
            double y4 = y3 - ( w / 2 ) * Math.cos( theta );
            double y5 = y3 + ( w / 2 ) * Math.cos( theta );
            arrowHead.addPoint( (int)x4, (int)y4 );
            arrowHead.addPoint( (int)x5, (int)y5 );

            g2d.setStroke( arrowHeadStroke );
            g2d.drawPolygon( arrowHead );
            g2d.fillPolygon( arrowHead );

            g2d.dispose();
        }
        return buffImg;
    }


    //
    // Inner classes
    //

    public static double thetaGranularity = 0.2;
    public static double lengthGranularity = 5;

    private static class VectorSpec {
        double length;
        double theta;
        Color color;

        public VectorSpec( double length, double theta, Color color ) {
            this.length = length;
            this.theta = theta;
            this.color = color;
        }

        public VectorSpec( VectorSpec that ) {
            this.length = that.length;
            this.theta = that.theta;
            this.color = that.color;
        }

        public int hashCode() {
            int hc = (int)( theta / thetaGranularity );
            return hc;
        }

        public boolean equals( Object obj ) {
            if( obj instanceof VectorSpec ) {
                VectorSpec that = (VectorSpec)obj;
                return Math.abs( this.length - that.length ) <= lengthGranularity &&
                        Math.abs( this.theta - that.theta ) <= thetaGranularity &&
                        this.color == that.color;
            }
            else {
                return false;
            }
        }

        public void setLength( double length ) {
            this.length = length;
        }

        public void setTheta( double theta ) {
            this.theta = theta;
        }

        public void setColor( Color color ) {
            this.color = color;
        }
    }
}
