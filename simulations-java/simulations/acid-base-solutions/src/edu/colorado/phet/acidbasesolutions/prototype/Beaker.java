/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.geom.Point2D;


/**
 * Model of a beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class Beaker extends Changeable {
    
    private int width, height;
    private final Point2D center;

    public Beaker() {
        this( ProtoConstants.BEAKER_WIDTH_RANGE.getDefault(), ProtoConstants.BEAKER_HEIGHT_RANGE.getDefault(), ProtoConstants.BEAKER_CENTER );
    }
    public Beaker( int width, int height, Point2D center ) {
        this.width = width;
        this.height = height;
        this.center = new Point2D.Double( center.getX(), center.getY() );
    }
    
    public void setWidth( int width ) {
        if ( width != this.width ) {
            this.width = width;
            fireStateChanged();
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setHeight( int height ) {
        if ( height != this.height ) {
            this.height = height;
            fireStateChanged();
        }
    }
    
    public int getHeight() {
        return height;
    }
    
    public Point2D getCenterReference() {
        return center;
    }
}
