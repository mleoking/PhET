/**
 * Class: Range2D
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 17, 2004
 */
package edu.colorado.phet.chart;

import java.awt.geom.Rectangle2D;

public class Range2D {
    double minX;
    double minY;
    double maxX;
    double maxY;

    public Range2D( double minX, double minY, double maxX, double maxY ) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX( double minX ) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY( double minY ) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX( double maxX ) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY( double maxY ) {
        this.maxY = maxY;
    }

    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double r = new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
        return r;
    }

}
