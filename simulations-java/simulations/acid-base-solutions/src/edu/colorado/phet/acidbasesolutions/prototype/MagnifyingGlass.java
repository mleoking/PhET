/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.geom.Point2D;


/**
 * Model of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlass extends Changeable {
    
    public enum MoleculeRepresentation { DOTS, IMAGES };
    
    private int diameter;
    private final Point2D center;
    private MoleculeRepresentation moleculeRepresentation;
    
    public MagnifyingGlass() {
        this( MGPConstants.MAGNIFYING_GLASS_DIAMETER_RANGE.getDefault(), MGPConstants.MAGNIFYING_GLASS_CENTER, MoleculeRepresentation.IMAGES );
    }

    public MagnifyingGlass( int diameter, Point2D center, MoleculeRepresentation moleculeRepresentation ) {
        this.diameter = diameter;
        this.center = new Point2D.Double( center.getX(), center.getY() );
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
    
    public Point2D getCenterReference() {
        return center;
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
