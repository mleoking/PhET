/**
 * Class: FieldVector
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Jul 29, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FieldVector {

    //
    // Static fields and methods
    //

    // Specs for the internal cache of arrows
    private final static int s_maxLengthCached = 50;
    private final static int s_numAnglesCached = 50;
    private static BufferedImage[][] arrowsCached = new BufferedImage[s_maxLengthCached + 1][s_numAnglesCached + 1];

    // arrow parameters
    private static BasicStroke arrowStroke = new BasicStroke( 2 );
    // arrowhead parameters
    private static int maxArrowHeadWidth = 10;
    private static int headWidthToLengthRatio = 3;
    //    private static Color arrowColor = Color.BLACK;
    private static Color arrowColor = new Color( 235, 235, 235 );
    private static BasicStroke arrowHeadStroke = new BasicStroke( 1 );
    private static Polygon arrowHead = new Polygon();


    private static int length = 15;

    public static void setLength( int length ) {
        FieldVector.length = length;
    }

    private static BufferedImage createBuffImg( double magnitude, double theta ) {
        BufferedImage buffImg = null;
        if ( length > 0 ) {

            int dx = Math.max( (int) ( length * Math.abs( Math.cos( theta ) ) ), 10 );
            int dy = Math.max( (int) ( length * Math.abs( Math.sin( theta ) ) ), 10 );
            buffImg = new BufferedImage( Math.max( dx, dy ) + 10,
                                         Math.max( dx, dy ) + 10,
                                         BufferedImage.TYPE_INT_ARGB );
            int xMid = buffImg.getWidth() / 2;
            int yMid = buffImg.getHeight() / 2;


            Graphics2D g2d = buffImg.createGraphics();
            g2d.setColor( arrowColor );
            float alpha = (float) magnitude / 100;
            g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );

            // draw the line
            g2d.setStroke( arrowStroke );
            g2d.drawLine( xMid - (int) ( length * Math.cos( theta ) / 2 ),
                          yMid - (int) ( length * Math.sin( theta ) / 2 ),
                          xMid + (int) ( length * Math.cos( theta ) / 2 ),
                          yMid + (int) ( length * Math.sin( theta ) / 2 ) );

            // draw the arrowhead
            double w = Math.min( (int) ( length / headWidthToLengthRatio ), maxArrowHeadWidth );
            arrowHead.reset();
            int xa = xMid + (int) ( ( ( length + w ) / headWidthToLengthRatio ) * Math.cos( theta ) );
            int ya = yMid + (int) ( ( ( length + w ) / headWidthToLengthRatio ) * Math.sin( theta ) );
            arrowHead.addPoint( xa, ya );

            double x3 = xa - w * Math.cos( theta );
            double y3 = ya - w * Math.sin( theta );
            double x4 = x3 + ( w / headWidthToLengthRatio ) * Math.sin( theta );
            double x5 = x3 - ( w / headWidthToLengthRatio ) * Math.sin( theta );
            double y4 = y3 - ( w / headWidthToLengthRatio ) * Math.cos( theta );
            double y5 = y3 + ( w / headWidthToLengthRatio ) * Math.cos( theta );
            arrowHead.addPoint( (int) x4, (int) y4 );
            arrowHead.addPoint( (int) x5, (int) y5 );

            g2d.setStroke( arrowHeadStroke );
            g2d.drawPolygon( arrowHead );
            g2d.fillPolygon( arrowHead );

            g2d.dispose();
        }
        return buffImg;
    }


    public static BufferedImage getGraphic( double length, double theta ) {

        BufferedImage arrow = null;

        // If the arrow is longer than the max length, just draw it
        if ( (int) length > s_maxLengthCached - 1 ) {
            arrow = createBuffImg( length, theta );
        }
        // If the arrow is within the range of lengths that we cache, then
        // try to find it in the cache.
        else {
            // Make sure theta is positive in the range 0 to 2pi
            theta %= Math.PI * 2;
            theta += Math.PI * 2;
            theta %= Math.PI * 2;
            int thetaIdx = (int) ( theta / ( Math.PI * 2 ) * s_numAnglesCached );
            int lengthIdx = Math.min( (int) length, s_maxLengthCached - 1 );
            arrow = arrowsCached[lengthIdx][thetaIdx];
            // If we didn't find the arrow in the cache, then create one and cache it
            if ( length > 0 && arrow == null ) {
                arrow = createBuffImg( length, theta );
                arrowsCached[lengthIdx][thetaIdx] = arrow;
            }
        }
        return arrow;
    }
}
