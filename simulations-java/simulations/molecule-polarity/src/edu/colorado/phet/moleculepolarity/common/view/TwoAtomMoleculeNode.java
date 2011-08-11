// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomsMolecule;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Visual representation of a molecule composed for 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomMoleculeNode extends PhetPNode {

    private final TwoAtomsMolecule molecule;

    public TwoAtomMoleculeNode( final TwoAtomsMolecule molecule ) {

        this.molecule = molecule;

        addChild( new BondNode( molecule.bond ) );
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );

        addInputEventListener( new CursorHandler() ); //TODO change cursor to indicate rotation

        //Rotate the molecule about the center of the bond when dragged, copied from PrismNode in bending-light
        //TODO: consider generalizing
        addInputEventListener( new PBasicInputEventHandler() {
            double previousAngle;

            @Override public void mousePressed( PInputEvent event ) {
                molecule.setDragging( true );
                previousAngle = getAngle( event ); //Store the original angle since rotations are computed as deltas between each event
            }

            @Override public void mouseReleased( PInputEvent event ) {
                molecule.setDragging( false );
            }

            // Find the angle about the center of the bond.
            private double getAngle( PInputEvent event ) {
                return new ImmutableVector2D( molecule.bond.getCenter().toPoint2D(), event.getPositionRelativeTo( getParent() ) ).getAngle();
            }

            // Drag the molecule to rotate it.
            @Override public void mouseDragged( PInputEvent event ) {
                double angle = getAngle( event );
                molecule.angle.set( molecule.angle.get() + angle - previousAngle );
                previousAngle = angle;
            }
        } );
    }
}