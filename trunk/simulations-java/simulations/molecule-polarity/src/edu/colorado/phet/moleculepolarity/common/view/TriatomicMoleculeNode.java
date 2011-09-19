// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.BondAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;

/**
 * Visual representation of a triatomic molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMoleculeNode extends PhetPNode {

    public TriatomicMoleculeNode( TriatomicMolecule molecule ) {

        // nodes
        BondNode bondABNode = new BondNode( molecule.bondAB );
        BondNode bondBCNode = new BondNode( molecule.bondBC );
        AtomNode atomANode = new AtomNode( molecule.atomA );
        AtomNode atomBNode = new AtomNode( molecule.atomB );
        AtomNode atomCNode = new AtomNode( molecule.atomC );
        final BondAngleArrowsNode arrowsANode = new BondAngleArrowsNode( molecule, molecule.atomA );
        final BondAngleArrowsNode arrowsCNode = new BondAngleArrowsNode( molecule, molecule.atomC );

        // rendering order, bonds behind atoms
        addChild( bondABNode );
        addChild( bondBCNode );
        addChild( arrowsANode );
        addChild( arrowsCNode );
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
        atomANode.addInputEventListener( new CursorHandler() );
        atomCNode.addInputEventListener( new CursorHandler() );
        atomANode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleA, atomANode, arrowsANode ) );
        atomCNode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleC, atomCNode, arrowsCNode ) );

        // default state
        arrowsANode.setVisible( false );
        arrowsCNode.setVisible( false );
    }
}