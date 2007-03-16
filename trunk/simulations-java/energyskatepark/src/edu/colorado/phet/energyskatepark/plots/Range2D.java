package edu.colorado.phet.energyskatepark.plots;

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

    public Range2D( Range2D range ) {
        this( range.minX, range.minY, range.maxX, range.maxY );
    }

    public Range2D union( Range2D range ) {
        Range2D union = new Range2D( Math.min( minX, range.minX ),
                                     Math.min( minY, range.minY ),
                                     Math.max( maxX, range.maxX ),
                                     Math.max( maxY, range.maxY ) );
        return union;
    }

    public void setRange( double minX, double minY, double maxX, double maxY ) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setRange( Range2D range ) {
        this.minX = range.getMinX();
        this.minY = range.getMinY();
        this.maxX = range.getMaxX();
        this.maxY = range.getMaxY();
    }

    public void setMinX( double minX ) {
        this.minX = minX;
    }

    public double getMinX() {
        return minX;
    }

    public void setMaxX( double maxX ) {
        this.maxX = maxX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMinY( double minY ) {
        this.minY = minY;
    }

    public double getMinY() {
        return minY;
    }

    public void setMaxY( double maxY ) {
        this.maxY = maxY;
    }

    public double getMaxY() {
        return maxY;
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
