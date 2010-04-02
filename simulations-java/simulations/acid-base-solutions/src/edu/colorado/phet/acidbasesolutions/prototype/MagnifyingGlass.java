/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;


/**
 * Model of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlass extends Changeable {
    
    public enum MoleculeRepresentation { DOTS, IMAGES };
    public enum H2ORepresentation { DOTS, IMAGES, SOLID_COLOR };
    
    private int diameter;
    private MoleculeRepresentation moleculeRepresentation;
    private H2ORepresentation h2oRepresentation;
    
    public MagnifyingGlass() {
        this( ProtoConstants.MAGNIFYING_GLASS_DIAMETER_RANGE.getDefault(), MoleculeRepresentation.DOTS, H2ORepresentation.SOLID_COLOR );
    }

    public MagnifyingGlass( int diameter,  MoleculeRepresentation moleculeRepresentation, H2ORepresentation h2oRepresentation ) {
        this.diameter = diameter;
        this.moleculeRepresentation = moleculeRepresentation;
        this.h2oRepresentation = h2oRepresentation;
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
    
    public void setH2ORepresentation( H2ORepresentation h2oRepresentation ) {
        if ( h2oRepresentation != this.h2oRepresentation ) {
            this.h2oRepresentation = h2oRepresentation;
            fireStateChanged();
        }
    }
    
    public H2ORepresentation getH2ORepresentation() {
        return h2oRepresentation;
    }
}
