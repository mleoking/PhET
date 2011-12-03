// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.moleculepolarity.MPSimSharing;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for manipulating molecule angle.
 * Adapted from PrismNode in bending-light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeAngleHandler extends PDragSequenceEventHandler {

    private final Molecule2D molecule;
    private final PNode dragNode;
    private double previousAngle;

    public MoleculeAngleHandler( Molecule2D molecule, PNode dragNode ) {
        this.molecule = molecule;
        this.dragNode = dragNode;
    }

    @Override public void startDrag( PInputEvent event ) {
        super.startDrag( event );
        molecule.setDragging( true );
        previousAngle = getAngle( event ); //Store the original angle since rotations are computed as deltas between each event
        SimSharingEvents.sendEvent( MPSimSharing.OBJECT_MOLECULE_ANGLE, SimSharingActions.START_DRAG,
                                    Parameter.param( MPSimSharing.PARAM_ANGLE, molecule.angle.get() ) );
    }

    @Override public void endDrag( PInputEvent event ) {
        super.endDrag( event );
        molecule.setDragging( false );
        SimSharingEvents.sendEvent( MPSimSharing.OBJECT_MOLECULE_ANGLE, SimSharingActions.END_DRAG,
                                    Parameter.param( MPSimSharing.PARAM_ANGLE, molecule.angle.get() ) );
    }

    // Drag to rotate the molecule.
    @Override public void drag( PInputEvent event ) {
        super.drag( event );
        double angle = getAngle( event );
        molecule.angle.set( molecule.angle.get() + angle - previousAngle );
        previousAngle = angle;
    }

    // Find the angle about the molecule's location.
    private double getAngle( PInputEvent event ) {
        return new ImmutableVector2D( molecule.location.toPoint2D(), event.getPositionRelativeTo( dragNode.getParent() ) ).getAngle();
    }
}
