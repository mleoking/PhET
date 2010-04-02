package edu.colorado.phet.acidbasesolutions.prototype;

/**
 * Model of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlass extends Changeable {
    
    private int diameter;
    
    public MagnifyingGlass() {
        this( ProtoConstants.MAGNIFYING_GLASS_DIAMETER_RANGE.getDefault() );
    }

    public MagnifyingGlass( int diameter ) {
        this.diameter = diameter;
    }
    
    public void setDiameter( int diameter ) {
        if ( diameter != this.diameter ) {
            this.diameter = diameter;
            fireStateChanged();
        }
    }
    
    public int getDiameter() {
        return diameter;
    }
}
