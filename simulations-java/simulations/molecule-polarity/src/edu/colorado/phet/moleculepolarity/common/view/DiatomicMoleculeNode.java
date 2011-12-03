// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.DiatomicMolecule;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode.OppositePartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;
import edu.umd.cs.piccolo.PNode;

/**
 * Visual representation of a diatomic molecule.
 * Children position themselves in world coordinates, so this node's offset should be (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiatomicMoleculeNode extends PhetPNode {

    private final PNode electrostaticPotentialNode, electronDensityNode;
    private final PNode partialChargeNodeA, partialChargeNodeB;
    private final PNode bondDipoleNode;

    public DiatomicMoleculeNode( DiatomicMolecule molecule ) {

        // nodes
        electrostaticPotentialNode = new DiatomicElectrostaticPotentialNode( molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPColors.RWB_GRADIENT );
        electronDensityNode = new DiatomicElectronDensityNode( molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPColors.BW_GRADIENT );
        final PNode bondNode = new BondNode( molecule.bond );
        final AtomNode atomANode = new AtomNode( molecule.atomA );
        final AtomNode atomBNode = new AtomNode( molecule.atomB );
        partialChargeNodeA = new OppositePartialChargeNode( molecule.atomA, molecule.bond );
        partialChargeNodeB = new OppositePartialChargeNode( molecule.atomB, molecule.bond );
        bondDipoleNode = new BondDipoleNode( molecule.bond );

        // rendering order:
        // surfaces
        addChild( new PNode() {{
            addChild( electrostaticPotentialNode );
            addChild( electronDensityNode );
        }} );
        // structure
        addChild( new PNode() {{
            addChild( bondNode ); // bond behind atoms
            addChild( atomANode );
            addChild( atomBNode );
        }} );
        // decorations
        addChild( new PNode() {{
            addChild( partialChargeNodeA );
            addChild( partialChargeNodeB );
            addChild( bondDipoleNode );
        }} );

        // rotate molecule by dragging anywhere
        addInputEventListener( new RotateCursorHandler() );
        addInputEventListener( new MoleculeAngleHandler( molecule, this ) );
    }

    public void setSurface( SurfaceType surfaceType ) {
        electrostaticPotentialNode.setVisible( surfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL );
        electronDensityNode.setVisible( surfaceType == SurfaceType.ELECTRON_DENSITY );
    }

    public void setPartialChargesVisible( boolean visible ) {
        partialChargeNodeA.setVisible( visible );
        partialChargeNodeB.setVisible( visible );
    }

    public void setBondDipoleVisible( boolean visible ) {
        bondDipoleNode.setVisible( visible );
    }
}