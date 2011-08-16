// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Interface implemented by all molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IMolecule {

    public ImmutableVector2D getMolecularDipole();

    public void addMolecularDipoleObserver( SimpleObserver observer );

    // gets an array of the molecule's atoms
    public Atom[] getAtoms();

    // gets the point about which the molecule rotates
    public ImmutableVector2D getLocation();

    // sets the rotation angle, in radians
    public void setAngle( double angle );

    // gets the rotation angle, in radians
    public double getAngle();

    // tells the atom whether it's being dragged
    public void setDragging( boolean dragging );

    // allows clients to determine whether the molecule is currently being dragged
    public boolean isDragging();
}
