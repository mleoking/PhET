// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.IsosurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.BondDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.BondTypeNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicIsosurfaceNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.IsosurfaceType;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsCanvas extends MPCanvas {

    public TwoAtomsCanvas( TwoAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        // nodes
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        DiatomicMoleculeNode moleculeNode = new DiatomicMoleculeNode( model.molecule );
        final PartialChargeNode partialChargeNodeA = new PartialChargeNode( model.molecule.atomA, model.molecule.bond );
        final PartialChargeNode partialChargeNodeB = new PartialChargeNode( model.molecule.atomB, model.molecule.bond );
        final BondDipoleNode bondDipoleNode = new BondDipoleNode( model.molecule.bond );
        final PNode electrostaticPotentialNode = new DiatomicIsosurfaceNode( model.molecule );
        final PNode electronDensityNode = new DiatomicIsosurfaceNode( model.molecule );
        ElectronegativityControlNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        ElectronegativityControlNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties, false, true, false, MPStrings.BOND_DIPOLE ) );
        PNode isosurfaceControlsNode = new ControlPanelNode( new IsosurfaceControlPanel( viewProperties.isosurfaceType ) );
        PNode eFieldControlsNode = new ControlPanelNode( new EFieldControlPanel( model.eField.enabled ) );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { model, viewProperties }, parentFrame, 16, Color.BLACK, Color.YELLOW );
        final PNode bondTypeNode = new BondTypeNode( model.molecule );

        // rendering order
        {
            // plates
            addChild( negativePlateNode );
            addChild( positivePlateNode );

            // controls
            addChild( enControlA );
            addChild( enControlB );
            addChild( viewControlsNode );
            addChild( isosurfaceControlsNode );
            addChild( eFieldControlsNode );
            addChild( resetAllButtonNode );
            addChild( bondTypeNode );

            // molecule
            addChild( moleculeNode );
            addChild( partialChargeNodeA );
            addChild( partialChargeNodeB );
            addChild( bondDipoleNode );
            addChild( electrostaticPotentialNode );
            addChild( electronDensityNode );
        }

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 75 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            enControlA.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, 100 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + xSpacing, enControlA.getYOffset() );
            positivePlateNode.setOffset( enControlB.getFullBounds().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            bondTypeNode.setOffset( 150, negativePlateNode.getFullBoundsReference().getMaxY() + 30 ); //TODO compute horizontal center
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, enControlA.getYOffset() );
            isosurfaceControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            eFieldControlsNode.setOffset( isosurfaceControlsNode.getXOffset(), isosurfaceControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( isosurfaceControlsNode.getXOffset(), eFieldControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // synchronize with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    bondDipoleNode.setVisible( visible );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    partialChargeNodeA.setVisible( visible );
                    partialChargeNodeB.setVisible( visible );
                }
            } );

            viewProperties.bondTypeVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    bondTypeNode.setVisible( visible );
                }
            } );

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<IsosurfaceType>() {
                public void apply( IsosurfaceType isosurfaceType ) {
                    electrostaticPotentialNode.setVisible( isosurfaceType == IsosurfaceType.ELECTROSTATIC_POTENTIAL );
                    electronDensityNode.setVisible( isosurfaceType == IsosurfaceType.ELECTRON_DENSITY );
                }
            } );
        }
    }
}
