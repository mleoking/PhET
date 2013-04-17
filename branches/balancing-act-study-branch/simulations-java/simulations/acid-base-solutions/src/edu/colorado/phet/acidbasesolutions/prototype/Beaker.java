// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;



/**
 * Model of a beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class Beaker extends Changeable {
    
    private int width, height;

    public Beaker() {
        this( MGPConstants.BEAKER_WIDTH_RANGE.getDefault(), MGPConstants.BEAKER_HEIGHT_RANGE.getDefault() );
    }
    public Beaker( int width, int height ) {
        this.width = width;
        this.height = height;
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
}
