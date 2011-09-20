// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all 2D molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule2D {

    public final ImmutableVector2D location; // the point about which the molecule rotates
    public final Property<Double> angle; // angle of rotation of the entire molecule about the location, in radians
    public final Property<ImmutableVector2D> dipole; // the molecular dipole

    private boolean dragging; // true when the user is dragging the molecule

    protected Molecule2D( ImmutableVector2D location, double angle ) {
        this.location = location;
        this.angle = new Property<Double>( angle );
        this.dipole = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        this.dragging = false;
    }

    public void reset() {
        angle.reset();
        for ( Atom atom : getAtoms() ) {
            atom.reset();
        }
    }

    // tells the atom whether it's being dragged
    public boolean isDragging() {
        return dragging;
    }

    // allows clients to determine whether the molecule is currently being dragged
    public void setDragging( boolean dragging ) {
        this.dragging = dragging;
    }

    // gets an array of the molecule's atoms
    public abstract Atom[] getAtoms();

    // gets and array of the molecule's bonds
    protected abstract Bond[] getBonds();

    // implemented by subclasses, differs depending on the topology of the molecule
    protected abstract void updateAtomLocations();

    // implemented by subclasses, differs depending on the topology of the molecule
    protected abstract void updatePartialCharges();

    // molecular dipole is the vector sum of the bond dipoles
    protected void updateMolecularDipole() {
        ImmutableVector2D sum = new ImmutableVector2D();
        for ( Bond bond : getBonds() ) {
            sum = sum.plus( bond.dipole.get() );
        }
        dipole.set( sum );
    }

    // must be called by subclasses when fully constructed
    protected void initObservers() {

        // update atom locations when molecule is rotated
        this.angle.addObserver( new SimpleObserver() {
            public void update() {
                updateAtomLocations();
            }
        } );

        // update molecular dipole when bond dipoles change
        SimpleObserver dipoleUpdater = new SimpleObserver() {
            public void update() {
                updateMolecularDipole();
            }
        };
        for ( Bond bond : getBonds() ) {
            bond.dipole.addObserver( dipoleUpdater );
        }

        // update partial charges when atoms' EN changes
        SimpleObserver partialChargesUpdater = new SimpleObserver() {
            public void update() {
                updatePartialCharges();
            }
        };
        for ( Atom atom : getAtoms() ) {
            atom.electronegativity.addObserver( partialChargesUpdater );
        }
    }
}
