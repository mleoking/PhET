/** Sam Reid*/
package edu.colorado.phet.cck.common;


import edu.colorado.phet.common.math.ImmutableVector2D;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 10:13:15 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class AffineTransformUtil {

    public static AffineTransform getTransform( Rectangle2D src, Rectangle2D dst, double angle ) {
        AffineTransform at = new AffineTransform();
        at.concatenate( createTransform( src, dst ) );
        final Point2D.Double ctrDst = new Point2D.Double( src.getX() + src.getWidth() / 2, src.getY() + src.getHeight() / 2 );
        at.rotate( -angle, ctrDst.x, ctrDst.y );
        return at;
    }

    public static AffineTransform createTransform( Rectangle2D inputBounds, Rectangle2D outputBounds ) {
        double m00 = outputBounds.getWidth() / inputBounds.getWidth();
        double m01 = 0;
        double m02 = outputBounds.getX() - m00 * inputBounds.getX();
        double m10 = 0;
        double m11 = outputBounds.getHeight() / inputBounds.getHeight();
        double m12 = outputBounds.getY() - m11 * inputBounds.getY();
        return new AffineTransform( m00, m10, m01, m11, m02, m12 );
    }

    public static AffineTransform createTransform( int initWidth, int initHeight, Point2D srcpt, Point2D dstpt, double newHeight ) {
        double dist = srcpt.distance( dstpt );
        double newLength = dist;
        double angle = new ImmutableVector2D.Double( srcpt, dstpt ).getAngle();
        AffineTransform trf = new AffineTransform();
        trf.rotate( angle, srcpt.getX(), srcpt.getY() );
        trf.translate( 0, -newHeight / 2 );
        trf.translate( srcpt.getX(), srcpt.getY() );
        trf.scale( newLength / initWidth, newHeight / initHeight );
        return trf;
    }

    /**
     * Computes the offsets that must be applied to the buffered image's location so that it's head is
     * at the location of the photon
     *
     * @param theta
     */
    public Point2D computeOffsets( double theta, double baseImageWidth, double baseImageHeight ) {
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );

        double xOffset = 0;
        double yOffset = 0;
        double alpha = 0;
        double w = baseImageWidth;
        double h = baseImageHeight;
        if( theta >= 0 && theta <= Math.PI / 2 ) {
            xOffset = w * Math.cos( theta ) + ( h / 2 ) * Math.sin( theta );
            yOffset = w * Math.sin( theta ) + ( h / 2 ) * Math.cos( theta );
        }
        if( theta > Math.PI / 2 && theta <= Math.PI ) {
            alpha = theta - Math.PI / 2;
            xOffset = ( h / 2 ) * Math.cos( alpha );
            yOffset = w * Math.cos( alpha ) + ( h / 2 ) * Math.sin( alpha );
        }
        if( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            alpha = theta - Math.PI;
            xOffset = ( h / 2 ) * Math.sin( alpha );
            yOffset = ( h / 2 ) * Math.cos( alpha );
        }
        if( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            alpha = Math.PI * 2 - theta;
            xOffset = w * Math.cos( alpha ) + ( h / 2 ) * Math.sin( alpha );
            yOffset = ( h / 2 ) * Math.cos( alpha );
        }
        return new Point2D.Double( xOffset, yOffset );
    }

}
