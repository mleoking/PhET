// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;



/**
 * Model of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlass extends Changeable {
    
    public enum MoleculeRepresentation { DOTS, IMAGES };
    
    private int diameter;
    private MoleculeRepresentation moleculeRepresentation;
    
    public MagnifyingGlass() {
        this( MGPConstants.MAGNIFYING_GLASS_DIAMETER_RANGE.getDefault(), MoleculeRepresentation.IMAGES );
    }

    public MagnifyingGlass( int diameter, MoleculeRepresentation moleculeRepresentation ) {
        this.diameter = diameter;
        this.moleculeRepresentation = moleculeRepresentation;
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
    
    public void setMoleculeRepresentation( MoleculeRepresentation moleculeRepresentation ) {
        if ( moleculeRepresentation != this.moleculeRepresentation ) {
            this.moleculeRepresentation = moleculeRepresentation;
            fireStateChanged();
        }
    }
    
    public MoleculeRepresentation getMoleculeRepresentation() {
        return moleculeRepresentation;
    }
}
