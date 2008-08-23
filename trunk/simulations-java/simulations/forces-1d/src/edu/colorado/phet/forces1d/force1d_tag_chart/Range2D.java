/**
 * Class: Range2D
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 17, 2004
 */
package edu.colorado.phet.forces1d.force1d_tag_chart;

import java.awt.geom.Rectangle2D;

public class Range2D {
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    public Range2D( double minX, double minY, double maxX, double maxY ) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public Range2D( Rectangle2D rectangle ) {
        this( rectangle.getX(), rectangle.getY(), rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight() );
    }

    public double getMinX() {
        return minX;
    }

    public Range2D union( Range2D range ) {
        Range2D union = new Range2D( Math.min( minX, range.minX ),
                                     Math.min( minY, range.minY ),
                                     Math.max( maxX, range.maxX ),
                                     Math.max( maxY, range.maxY ) );
        return union;
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

    public Range2D getScaledRange( double fractionX, double fractionY ) {
        Rectangle2D.Double rect = getBounds();
        double centerX = rect.getX() + rect.getWidth() / 2;
        double centerY = rect.getY() + rect.getHeight() / 2;

        double width = rect.getWidth() * fractionX;
        double height = rect.getHeight() * fractionY;

        double x = centerX - width / 2;
        double y = centerY - height / 2;
        Rectangle2D.Double newRect = new Rectangle2D.Double( x, y, width, height );
        return new Range2D( newRect );
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public String toString() {
        return "x=[" + minX + ", " + maxX + "], y=[" + minY + ", " + maxY + "]";
    }

    public boolean containsX( double x ) {
        return x >= minX && x <= maxX;
    }

    public boolean containsY( double y ) {
        return y >= minY && y <= maxY;
    }
}
