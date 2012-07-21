// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.moleculepolarity.MPSimSharing.Parameters;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents.moleculeAngle;

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
        SimSharingManager.sendUserMessage( moleculeAngle, UserComponentTypes.sprite, UserActions.startDrag,
                                           ParameterSet.parameterSet( Parameters.angle, molecule.angle.get() ) );
    }

    @Override public void endDrag( PInputEvent event ) {
        super.endDrag( event );
        molecule.setDragging( false );
        SimSharingManager.sendUserMessage( moleculeAngle, UserComponentTypes.sprite, UserActions.endDrag,
                                           ParameterSet.parameterSet( Parameters.angle, molecule.angle.get() ) );
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
        return new Vector2D( molecule.location.toPoint2D(), event.getPositionRelativeTo( dragNode.getParent() ) ).getAngle();
    }
}
