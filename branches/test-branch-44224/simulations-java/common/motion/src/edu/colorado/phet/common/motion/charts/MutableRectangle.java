package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class MutableRectangle extends SimpleObservable {
    private double x;
    private double y;
    private double width;
    private double height;
    private Rectangle2D.Double defaultValue;

    public MutableRectangle(Rectangle2D.Double bounds) {
        this(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    public MutableRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.defaultValue = new Rectangle2D.Double(x,y,width,height);
    }

    /**
     * Restore the values used to construct this MutableRectangle
     */
    public void reset(){
        this.x = defaultValue.getX();
        this.y = defaultValue.getY();
        this.width = defaultValue.getWidth();
        this.height = defaultValue.getHeight();
        notifyObservers();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        notifyObservers();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        notifyObservers();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        notifyObservers();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        notifyObservers();
    }

    public void setRect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        notifyObservers();
    }

    public double getMaxY() {
        return y + height;
    }

    public double getMinY() {
        return y;
    }

    public void setVerticalRange(double min, double max) {
        assert max > min;
        this.y = min;
        this.height = max - min;
        notifyObservers();
    }

    public Rectangle2D.Double toRectangle2D() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public double getMaxX() {
        return x + width;
    }

    public double getMinX() {
        return x;
    }

    public void setHorizontalRange(double minX, double maxX) {
        assert maxX > minX;
        if (this.x != minX || this.width != (maxX - minX)) {
            this.x = minX;
            this.width = maxX - minX;
            notifyObservers();
        }
    }
}
