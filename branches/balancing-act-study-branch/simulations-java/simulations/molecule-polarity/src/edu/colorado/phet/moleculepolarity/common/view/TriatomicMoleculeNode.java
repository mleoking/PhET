// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.BondAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode.CompositePartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode.OppositePartialChargeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Visual representation of a triatomic molecule.
 * Children position themselves in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMoleculeNode extends PhetPNode {

    private final PNode partialChargeNodeA, partialChargeNodeB, partialChargeNodeC;
    private final PNode bondDipoleABNode, bondDipoleBCNode;
    private final PNode molecularDipoleNode;

    public TriatomicMoleculeNode( TriatomicMolecule molecule ) {

        // nodes
        final BondNode bondABNode = new BondNode( molecule.bondAB );
        final BondNode bondBCNode = new BondNode( molecule.bondBC );
        final AtomNode atomANode = new AtomNode( molecule.atomA );
        final AtomNode atomBNode = new AtomNode( molecule.atomB );
        final AtomNode atomCNode = new AtomNode( molecule.atomC );
        final BondAngleArrowsNode arrowsANode = new BondAngleArrowsNode( molecule, molecule.atomA );
        final BondAngleArrowsNode arrowsCNode = new BondAngleArrowsNode( molecule, molecule.atomC );
        partialChargeNodeA = new OppositePartialChargeNode( molecule.atomA, molecule.bondAB );
        partialChargeNodeB = new CompositePartialChargeNode( molecule.atomB, molecule );
        partialChargeNodeC = new OppositePartialChargeNode( molecule.atomC, molecule.bondBC );
        bondDipoleABNode = new BondDipoleNode( molecule.bondAB );
        bondDipoleBCNode = new BondDipoleNode( molecule.bondBC );
        molecularDipoleNode = new MolecularDipoleNode( molecule );

        // rendering order:
        // structure (ordering is modified during dragging)
        addChild( new PNode() {{
            addChild( bondABNode ); // bonds behind atoms
            addChild( bondBCNode );
            addChild( arrowsANode );
            addChild( arrowsCNode );
            addChild( atomANode );
            addChild( atomBNode );
            addChild( atomCNode );
        }} );
        // decorations
        addChild( new PNode() {{
            addChild( partialChargeNodeA );
            addChild( partialChargeNodeB );
            addChild( partialChargeNodeC );
            addChild( bondDipoleABNode );
            addChild( bondDipoleBCNode );
            addChild( molecularDipoleNode );
        }} );

        // rotate molecule by dragging bonds or atom B
        bondABNode.addInputEventListener( new RotateCursorHandler() );
        bondBCNode.addInputEventListener( new RotateCursorHandler() );
        atomBNode.addInputEventListener( new RotateCursorHandler() );
        bondABNode.addInputEventListener( new MoleculeAngleHandler( molecule, this ) );
        bondBCNode.addInputEventListener( new MoleculeAngleHandler( molecule, this ) );
        atomBNode.addInputEventListener( new MoleculeAngleHandler( molecule, this ) );

        // change bond angles by dragging atom A or C
        atomANode.addInputEventListener( new CursorHandler() );
        atomCNode.addInputEventListener( new CursorHandler() );
        atomANode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleA, atomANode, arrowsANode ) );
        atomCNode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleC, atomCNode, arrowsCNode ) );

        // default state
        arrowsANode.setVisible( false );
        arrowsCNode.setVisible( false );
    }

    public void setPartialChargesVisible( boolean visible ) {
        partialChargeNodeA.setVisible( visible );
        partialChargeNodeB.setVisible( visible );
        partialChargeNodeC.setVisible( visible );
    }

    public void setBondDipolesVisible( boolean visible ) {
        bondDipoleABNode.setVisible( visible );
        bondDipoleBCNode.setVisible( visible );
    }

    public void setMolecularDipoleVisible( boolean visible ) {
        molecularDipoleNode.setVisible( visible );
    }
}