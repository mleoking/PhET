/**
 * Class: GlassPane
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 24, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Rectangle2D;

public class GlassPane extends PhotonFactory {
    private Rectangle2D.Double bounds;
    private double paneThickness = 0.6;

    public GlassPane( double x0, double width, double altitude ) {
        bounds = new Rectangle2D.Double( x0, altitude, width, paneThickness );
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public double getWidth() {
        return bounds.getWidth();
    }

    public double getHeight() {
        return bounds.getHeight();
    }
}
