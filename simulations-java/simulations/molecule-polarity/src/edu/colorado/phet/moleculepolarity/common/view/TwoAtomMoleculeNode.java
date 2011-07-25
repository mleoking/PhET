// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TwoAtomMolecule;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;

/**
 * XXX
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomMoleculeNode extends PhetPNode {

    public TwoAtomMoleculeNode( final TwoAtomMolecule molecule ) {

        addChild( new BondNode( molecule.atomA, molecule.atomB ) );
        addChild( new AtomNode( molecule.atomA ) );
        addChild( new AtomNode( molecule.atomB ) );

        addInputEventListener( new CursorHandler() );  //TODO change cursor to indicate rotation
        addInputEventListener( new RotationHandler() );
    }

    private static class RotationHandler extends PDragSequenceEventHandler {

        public RotationHandler() {

        }
    }
}
