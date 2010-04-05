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
    private boolean showH2O;
    
    public MagnifyingGlass() {
        this( MGPConstants.MAGNIFYING_GLASS_DIAMETER_RANGE.getDefault(), MGPConstants.MAGNIFYING_GLASS_CENTER, MoleculeRepresentation.DOTS, false /* showH2O */ );
    }

    public MagnifyingGlass( int diameter, Point2D center, MoleculeRepresentation moleculeRepresentation, boolean showH2O ) {
        this.diameter = diameter;
        this.center = new Point2D.Double( center.getX(), center.getY() );
        this.moleculeRepresentation = moleculeRepresentation;
        this.showH2O = showH2O;
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
    
    public void setShowH2O( boolean showH2O ) {
        if ( showH2O != this.showH2O ) {
            this.showH2O = showH2O;
            fireStateChanged();
        }
    }
    
    public boolean getShowH2O() {
        return showH2O;
    }
}
