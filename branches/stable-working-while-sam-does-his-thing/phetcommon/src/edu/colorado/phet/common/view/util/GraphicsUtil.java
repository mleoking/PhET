/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import java.awt.AlphaComposite;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 * GraphicsUtil
 *
 * @author Another Guy
 * @version $Revision$
 */
public class GraphicsUtil {

    /**
     * Sets the alpha for a Graphics2D
     *
     * @param g2
     * @param alpha
     */
    public static void setAlpha( Graphics2D g2, double alpha ) {
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float)alpha ) );
    }

    /**
     * Creates an AffineTransform that scales about a specified point
     *
     * @param scale
     * @param x
     * @param y
     * @return the scaled transform.
     * @deprecated
     */
    public static AffineTransform scaleInPlaceTx( double scale, double x, double y ) {
        AffineTransform atx = new AffineTransform();
        return scaleInPlaceTx( atx, scale, x, y );
    }

    /**
     * Sets up an AffineTransform to scale about a specified point
     *
     * @param atx
     * @param scale
     * @param x
     * @param y
     * @return the scaled transform.
     */
    public static AffineTransform scaleInPlaceTx( AffineTransform atx,
                                                  double scale, double x, double y ) {
        atx.setToIdentity();
        atx.translate( x, y );
        atx.scale( scale, scale );
        atx.translate( -x, -y );
        return atx;
    }

    // Sets anti-aliasing on for a specified Graphics2D
    public static Graphics2D setAntiAliasingOn( Graphics2D g2 ) {
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        return g2;
    }

    // This method maximizes a frame; the iconified bit is not affected.
    public static void maximizeFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Set the maximized bits
        state |= Frame.MAXIMIZED_BOTH;
        // Maximize the frame
        frame.setExtendedState( state );
    }

    // This method iconifies a frame; the maximized bits are not affected.
    public static void iconifyFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Set the iconified bit
        state |= Frame.ICONIFIED;
        // Iconify the frame
        frame.setExtendedState( state );
    }

    // This method deiconifies a frame; the maximized bits are not affected.
    public static void deiconifyFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Clear the iconified bit
        state &= ~Frame.ICONIFIED;
        // Deiconify the frame
        frame.setExtendedState( state );
    }

    // This method minimizes a frame; the iconified bit is not affected
    public static void minimizeFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Clear the maximized bits
        state &= ~Frame.MAXIMIZED_BOTH;
        // Maximize the frame
        frame.setExtendedState( state );
    }
}
