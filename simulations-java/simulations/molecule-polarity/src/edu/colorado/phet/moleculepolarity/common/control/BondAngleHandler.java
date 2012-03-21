// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.event.InputEvent;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.moleculepolarity.MPSimSharing.Parameters;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.colorado.phet.moleculepolarity.common.view.AtomNode;
import edu.colorado.phet.moleculepolarity.common.view.BondAngleArrowsNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;

/**
 * Drag handler for manipulating a bond angle.
 * The atom being dragged is popped to the front.
 * A pair of arrows indicating the direction of drag are shown when the mouse enters the atom.
 * When the drag begins, these arrows are made invisible.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleHandler extends PDragSequenceEventHandler {

    private final Molecule2D molecule;
    private final Property<Double> bondAngle;
    private final AtomNode atomNode;
    private final BondAngleArrowsNode arrowsNode;
    private double previousAngle;

    /**
     * Constructor.
     *
     * @param molecule   angle is relative to this molecule's location, and we pause any animation of this molecule while dragging
     * @param bondAngle  property that this handler modifies
     * @param atomNode   node that is being dragged
     * @param arrowsNode arrows that indicate direction of dragging
     */
    public BondAngleHandler( Molecule2D molecule, Property<Double> bondAngle, AtomNode atomNode, BondAngleArrowsNode arrowsNode ) {
        this.molecule = molecule;
        this.bondAngle = bondAngle;
        this.atomNode = atomNode;
        this.arrowsNode = arrowsNode;
    }

    // make arrows visible only on mouse enter if the button1 isn't pressed
    @Override public void mouseEntered( PInputEvent event ) {
        super.mouseEntered( event );
        atomNode.moveToFront();
        arrowsNode.moveInBackOf( atomNode );
        if ( ( event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != InputEvent.BUTTON1_DOWN_MASK ) {
            arrowsNode.setVisible( true );
        }
    }

    // make arrows invisible on mouse exit
    @Override public void mouseExited( PInputEvent event ) {
        arrowsNode.setVisible( false );
    }

    @Override public void startDrag( PInputEvent event ) {
        super.startDrag( event );
        molecule.setDragging( true );
        previousAngle = getAngle( event ); //Store the original angle since rotations are computed as deltas between each event
        arrowsNode.setVisible( false );
        SimSharingManager.sendUserMessage( UserComponents.bondAngle, UserComponentTypes.sprite, UserActions.startDrag,
                                           parameterSet( Parameters.atom, atomNode.atom.getName() ).with( Parameters.angle, bondAngle.get() ) );
    }

    // Drag to rotate the molecule.
    @Override public void drag( PInputEvent event ) {
        super.drag( event );
        double angle = getAngle( event );
        bondAngle.set( bondAngle.get() + angle - previousAngle );
        previousAngle = angle;
    }

    @Override public void endDrag( PInputEvent event ) {
        super.endDrag( event );
        molecule.setDragging( false );
        SimSharingManager.sendUserMessage( UserComponents.bondAngle, UserComponentTypes.sprite, UserActions.endDrag,
                                           parameterSet( Parameters.atom, atomNode.atom.getName() ).with( Parameters.angle, bondAngle.get() ) );
    }

    // Find the angle about the molecule's location.
    private double getAngle( PInputEvent event ) {
        return new ImmutableVector2D( molecule.location.toPoint2D(), event.getPositionRelativeTo( atomNode.getParent() ) ).getAngle();
    }
}