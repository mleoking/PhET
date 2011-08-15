// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

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
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.TriatomicMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.IsosurfaceType;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Three Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsCanvas extends MPCanvas {

    public ThreeAtomsCanvas( ThreeAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        TriatomicMoleculeNode moleculeNode = new TriatomicMoleculeNode( model.molecule );
        addChild( moleculeNode );

        final BondDipoleNode bondDipoleABNode = new BondDipoleNode( model.molecule.bondAB );
        addChild( bondDipoleABNode );

        final BondDipoleNode bondDipoleBCNode = new BondDipoleNode( model.molecule.bondBC );
        addChild( bondDipoleBCNode );

        final PartialChargeNode partialChargeNodeA = new PartialChargeNode( model.molecule.bondAB, model.molecule.atomA, false );
        addChild( partialChargeNodeA );

        //TODO what to do about partial charge for atom B?

        final PartialChargeNode partialChargeNodeC = new PartialChargeNode( model.molecule.bondBC, model.molecule.atomC, true );
        addChild( partialChargeNodeC );

        ElectronegativityControlNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        addChild( enControlA );

        ElectronegativityControlNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        addChild( enControlB );

        ElectronegativityControlNode enControlC = new ElectronegativityControlNode( model.molecule.atomC, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        addChild( enControlC );

        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties, true, false, false, MPStrings.BOND_DIPOLES ) );
        addChild( viewControlsNode );

        PNode isosurfaceControlsNode = new ControlPanelNode( new IsosurfaceControlPanel( viewProperties.isosurfaceType ) );
        addChild( isosurfaceControlsNode );

        PNode eFieldControlsNode = new ControlPanelNode( new EFieldControlPanel( model.eField.enabled ) );
        addChild( eFieldControlsNode );

        Resettable[] resettables = new Resettable[] { model, viewProperties };
        PNode resetAllButtonNode = new ResetAllButtonNode( resettables, parentFrame, 16, Color.BLACK, Color.YELLOW );
        addChild( resetAllButtonNode );

        PNode negativePlateNode = new NegativePlateNode( model.eField );
        addChild( negativePlateNode );

        PNode positivePlateNode = new PositivePlateNode( model.eField );
        addChild( positivePlateNode );

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 75 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            enControlA.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, 100 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + 10, enControlA.getYOffset() );
            enControlC.setOffset( enControlB.getFullBounds().getMaxX() + 10, enControlB.getYOffset() );
            positivePlateNode.setOffset( enControlC.getFullBounds().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, enControlA.getYOffset() );
            isosurfaceControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            eFieldControlsNode.setOffset( isosurfaceControlsNode.getXOffset(), isosurfaceControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( isosurfaceControlsNode.getXOffset(), eFieldControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // synchronize with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    bondDipoleABNode.setVisible( visible );
                    bondDipoleBCNode.setVisible( visible );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    partialChargeNodeA.setVisible( visible );
                    //TODO set visibility of partial changes for atom B
                    partialChargeNodeC.setVisible( visible );
                }
            } );

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<IsosurfaceType>() {
                public void apply( IsosurfaceType isosurfaceType ) {
                    //TODO change visibility of isosurface nodes
                }
            } );
        }
    }
}
