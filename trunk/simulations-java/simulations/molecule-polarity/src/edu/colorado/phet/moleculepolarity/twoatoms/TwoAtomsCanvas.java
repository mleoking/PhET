// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.MPResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.common.control.SurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.BondDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.BondTypeNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicElectronDensityNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicElectrostaticPotentialNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode.OppositePartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.ElectronDensityColorKeyNode;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.ElectrostaticPotentialColorKeyNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsCanvas extends MPCanvas {

    private static final double DIPOLE_SCALE = 1.0; // how much to scale the dipoles in the view

    public TwoAtomsCanvas( TwoAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        // nodes
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        PNode moleculeNode = new DiatomicMoleculeNode( model.molecule );
        final PNode partialChargeNodeA = new OppositePartialChargeNode( model.molecule.atomA, model.molecule.bond );
        final PNode partialChargeNodeB = new OppositePartialChargeNode( model.molecule.atomB, model.molecule.bond );
        final PNode bondDipoleNode = new BondDipoleNode( model.molecule.bond, DIPOLE_SCALE );
        final PNode electrostaticPotentialNode = new DiatomicElectrostaticPotentialNode( model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPColors.RWB_GRADIENT );
        final PNode electronDensityNode = new DiatomicElectronDensityNode( model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPColors.BW_GRADIENT );
        PNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        final PNode bondTypeNode = new BondTypeNode( model.molecule );
        final PNode electrostaticPotentialColorKeyNode = new ElectrostaticPotentialColorKeyNode();
        final PNode electronDensityColorKeyNode = new ElectronDensityColorKeyNode();

        // Floating control panels, with uniform width
        MPControlPanel viewControlPanel = new ViewControlPanel( viewProperties, false, true, false, false, MPStrings.BOND_DIPOLE );
        MPControlPanel surfaceControlPanel = new SurfaceControlPanel( viewProperties.isosurfaceType );
        MPControlPanel eFieldControlPanel = new EFieldControlPanel( model.eField.enabled );
        int minWidth = (int) Math.max( viewControlPanel.getPreferredSize().getWidth(), Math.max( surfaceControlPanel.getPreferredSize().getWidth(), eFieldControlPanel.getPreferredSize().getWidth() ) );
        viewControlPanel.setMinWidth( minWidth );
        surfaceControlPanel.setMinWidth( minWidth );
        eFieldControlPanel.setMinWidth( minWidth );
        PNode viewControlsNode = new ControlPanelNode( viewControlPanel );
        PNode isosurfaceControlsNode = new ControlPanelNode( surfaceControlPanel );
        PNode eFieldControlsNode = new ControlPanelNode( eFieldControlPanel );
        PNode resetAllButtonNode = new MPResetAllButtonNode( new Resettable[] { model, viewProperties }, parentFrame );

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

            // indicators
            addChild( bondTypeNode );
            addChild( electrostaticPotentialColorKeyNode );
            addChild( electronDensityColorKeyNode );

            // molecule
            addChild( electrostaticPotentialNode );
            addChild( electronDensityNode );
            addChild( moleculeNode );
            addChild( partialChargeNodeA );
            addChild( partialChargeNodeB );
            addChild( bondDipoleNode );
        }

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 100 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            enControlA.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, 50 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + xSpacing, enControlA.getYOffset() );
            positivePlateNode.setOffset( enControlB.getFullBounds().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            electrostaticPotentialColorKeyNode.setOffset( model.molecule.getLocation().getX() - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          negativePlateNode.getFullBoundsReference().getMaxY() );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            bondTypeNode.setOffset( model.molecule.getLocation().getX() - ( bondTypeNode.getFullBoundsReference().getWidth() / 2 ),
                                    enControlA.getFullBoundsReference().getMaxY() + ySpacing - PNodeLayoutUtils.getOriginYOffset( bondTypeNode ) );
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, positivePlateNode.getYOffset() );
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

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType isosurfaceType ) {
                    electrostaticPotentialNode.setVisible( isosurfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL );
                    electrostaticPotentialColorKeyNode.setVisible( electrostaticPotentialNode.getVisible() );
                    electronDensityNode.setVisible( isosurfaceType == SurfaceType.ELECTRON_DENSITY );
                    electronDensityColorKeyNode.setVisible( electronDensityNode.getVisible() );
                }
            } );
        }
    }
}
