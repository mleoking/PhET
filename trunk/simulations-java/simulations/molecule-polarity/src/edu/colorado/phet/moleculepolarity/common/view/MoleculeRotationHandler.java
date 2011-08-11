// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomsMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for rotating a molecule.
 * Copied from PrismNode in bending-light.
 * TODO: consider generalizing and migrating to piccolo-phet
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeRotationHandler extends PBasicInputEventHandler {

    private final TwoAtomsMolecule molecule;
    private final PNode dragNode;
    double previousAngle;

    public MoleculeRotationHandler( TwoAtomsMolecule molecule, PNode dragNode ) {
        this.molecule = molecule;
        this.dragNode = dragNode;
    }

    @Override public void mousePressed( PInputEvent event ) {
        molecule.setDragging( true );
        previousAngle = getAngle( event ); //Store the original angle since rotations are computed as deltas between each event
    }

    @Override public void mouseReleased( PInputEvent event ) {
        molecule.setDragging( false );
    }

    // Find the angle about the center of the bond.
    private double getAngle( PInputEvent event ) {
        return new ImmutableVector2D( molecule.bond.getCenter().toPoint2D(), event.getPositionRelativeTo( dragNode.getParent() ) ).getAngle();
    }

    // Drag the molecule to rotate it.
    @Override public void mouseDragged( PInputEvent event ) {
        double angle = getAngle( event );
        molecule.angle.set( molecule.angle.get() + angle - previousAngle );
        previousAngle = angle;
    }
}
