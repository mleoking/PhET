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
    private boolean dragging; // true when the user is dragging the molecule

    protected Molecule2D( ImmutableVector2D location, double angle ) {
        this.location = location;
        this.angle = new Property<Double>( angle );
        this.dragging = false;
    }

    public void reset() {
        angle.reset();
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

    // gets the molecular dipole
    public abstract ImmutableVector2D getDipole();

    // adds an observer to the molecular dipole
    public abstract void addDipoleObserver( SimpleObserver observer );
}
