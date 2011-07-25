// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Visual representation of a molecule composed for 2 atoms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomMoleculeNode extends PhetPNode {

    private final TwoAtomMolecule molecule;

    public TwoAtomMoleculeNode( final TwoAtomMolecule molecule ) {

        this.molecule = molecule;

        addChild( new BondNode( molecule.atomA, molecule.atomB ) );
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );

        addInputEventListener( new CursorHandler() ); //TODO change cursor to indicate rotation
        addInputEventListener( new RotationHandler( this, molecule ) );
    }

    private static class RotationHandler extends PDragSequenceEventHandler {

        private final PNode dragNode;
        private final TwoAtomMolecule molecule;

        public RotationHandler( PNode dragNode, TwoAtomMolecule molecule ) {
            this.dragNode = dragNode;
            this.molecule = molecule;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            //TODO
        }

        @Override protected void drag( PInputEvent event ) {
            super.drag( event );
            molecule.angle.set( molecule.angle.get() + Math.toRadians( 1 ) ); // increment angle by 1 degree on any drag
            //TODO
        }
    }
}
