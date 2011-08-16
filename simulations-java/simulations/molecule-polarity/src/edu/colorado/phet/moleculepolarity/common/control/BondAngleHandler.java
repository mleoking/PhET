// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculepolarity.common.model.IMolecule;
import edu.umd.cs.piccolo.PNode;
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
    private final PNode dragNode;
    double previousAngle;

    public BondAngleHandler( IMolecule molecule, Property<Double> bondAngle, PNode dragNode ) {
        this.molecule = molecule;
        this.bondAngle = bondAngle;
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
        return new ImmutableVector2D( molecule.getLocation().toPoint2D(), event.getPositionRelativeTo( dragNode.getParent() ) ).getAngle();
    }

    // Drag the molecule to rotate it.
    @Override public void mouseDragged( PInputEvent event ) {
        double angle = getAngle( event );
        bondAngle.set( bondAngle.get() + angle - previousAngle );
        previousAngle = angle;
    }
}
