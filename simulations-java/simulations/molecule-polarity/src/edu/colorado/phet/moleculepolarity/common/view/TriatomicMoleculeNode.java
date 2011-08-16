// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.BondAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;
import edu.umd.cs.piccolo.PNode;

/**
 * Visual representation of a triatomic molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMoleculeNode extends PhetPNode {

    private final TriatomicMolecule molecule;

    public TriatomicMoleculeNode( final TriatomicMolecule molecule ) {

        this.molecule = molecule;

        // nodes
        PNode bondABNode = new BondNode( molecule.bondAB );
        BondNode bondBCNode = new BondNode( molecule.bondBC );
        AtomNode atomANode = new AtomNode( molecule.atomA );
        AtomNode atomBNode = new AtomNode( molecule.atomB );
        AtomNode atomCNode = new AtomNode( molecule.atomC );

        // rendering order, bonds behind atoms
        addChild( bondABNode );
        addChild( bondBCNode );
        addChild( atomANode );
        addChild( atomBNode );
        addChild( atomCNode );

        // rotate molecule by dragging bonds or atom B
        bondABNode.addInputEventListener( new RotateCursorHandler() );
        bondBCNode.addInputEventListener( new RotateCursorHandler() );
        atomBNode.addInputEventListener( new RotateCursorHandler() );
        bondABNode.addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
        bondBCNode.addInputEventListener( new MoleculeRotationHandler( molecule, this ) );
        atomBNode.addInputEventListener( new MoleculeRotationHandler( molecule, this ) );

        // change bond angles by dragging atom A or C
        //TODO change cursor to indicate bond angle manipulation
        atomANode.addInputEventListener( new CursorHandler() );
        atomCNode.addInputEventListener( new CursorHandler() );
        atomANode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleA, this ) );
        atomCNode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleC, this ) );
    }
}