// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculepolarity.common.model.IMolecule;
import edu.colorado.phet.moleculepolarity.common.view.AtomNode;
import edu.colorado.phet.moleculepolarity.common.view.BondAngleDragIndicatorNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for manipulating a bond angle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BondAngleHandler extends PBasicInputEventHandler {

    private final IMolecule molecule;
    private final Property<Double> bondAngle;
    private final AtomNode atomNode;
    private final BondAngleDragIndicatorNode indicatorNode;
    double previousAngle;

    /**
     * Constructor.
     *
     * @param molecule  angle is relative to this molecule's location, and we pause any animation of this molecule while dragging
     * @param bondAngle property that this handler modifies
     * @param atomNode  node that is being dragged
     */
    public BondAngleHandler( IMolecule molecule, Property<Double> bondAngle, AtomNode atomNode, BondAngleDragIndicatorNode indicatorNode ) {
        this.molecule = molecule;
        this.bondAngle = bondAngle;
        this.atomNode = atomNode;
        this.indicatorNode = indicatorNode;
    }

    @Override public void mousePressed( PInputEvent event ) {
        molecule.setDragging( true );
        previousAngle = getAngle( event ); //Store the original angle since rotations are computed as deltas between each event
        atomNode.moveToFront();
        indicatorNode.moveInBackOf( atomNode );
    }

    @Override public void mouseReleased( PInputEvent event ) {
        molecule.setDragging( false );
    }

    // Drag to rotate the molecule.
    @Override public void mouseDragged( PInputEvent event ) {
        double angle = getAngle( event );
        bondAngle.set( bondAngle.get() + angle - previousAngle );
        previousAngle = angle;
    }

    // Find the angle about the molecule's location.
    private double getAngle( PInputEvent event ) {
        return new ImmutableVector2D( molecule.getLocation().toPoint2D(), event.getPositionRelativeTo( atomNode.getParent() ) ).getAngle();
    }
}
