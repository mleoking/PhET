// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.common.control.BondAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeRotationHandler;
import edu.colorado.phet.moleculepolarity.common.control.MouseOverVisibilityHandler;
import edu.colorado.phet.moleculepolarity.common.control.MoveToFrontHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;
import edu.umd.cs.piccolo.PNode;

/**
 * Visual representation of a triatomic molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TriatomicMoleculeNode extends PhetPNode {

    public TriatomicMoleculeNode( TriatomicMolecule molecule ) {

        // nodes
        PNode bondABNode = new BondNode( molecule.bondAB );
        BondNode bondBCNode = new BondNode( molecule.bondBC );
        AtomNode atomANode = new AtomNode( molecule.atomA );
        AtomNode atomBNode = new AtomNode( molecule.atomB );
        AtomNode atomCNode = new AtomNode( molecule.atomC );
        final BondAngleDragIndicatorNode indicatorANode = new BondAngleDragIndicatorNode( molecule.atomA ) {{
            setVisible( false );
        }};
        final BondAngleDragIndicatorNode indicatorCNode = new BondAngleDragIndicatorNode( molecule.atomC ) {{
            setVisible( false );
        }};

        // rendering order, bonds behind atoms
        addChild( bondABNode );
        addChild( bondBCNode );
        addChild( indicatorANode );
        addChild( indicatorCNode );
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
        atomANode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleA, atomANode ) );
        atomCNode.addInputEventListener( new BondAngleHandler( molecule, molecule.bondAngleC, atomCNode ) );
        // move the atom being dragged to the front
        atomANode.addInputEventListener( new MoveToFrontHandler( atomANode ) );
        atomCNode.addInputEventListener( new MoveToFrontHandler( atomCNode ) );

        // make bond angle indicators visible only on mouseOver
        atomANode.addInputEventListener( new MouseOverVisibilityHandler( indicatorANode ) );
        atomCNode.addInputEventListener( new MouseOverVisibilityHandler( indicatorCNode ) );
    }
}