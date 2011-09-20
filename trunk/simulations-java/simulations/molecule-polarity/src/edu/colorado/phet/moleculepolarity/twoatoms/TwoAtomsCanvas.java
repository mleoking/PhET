// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode;
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
        PNode controlPanelNode = new MPControlPanelNode( parentFrame, new Resettable[] { model, viewProperties },
                                                         new ViewControlPanel( viewProperties, false, true, false, false, MPStrings.BOND_DIPOLE ),
                                                         new SurfaceControlPanel( viewProperties.surfaceType ),
                                                         new EFieldControlPanel( model.eField.enabled ) );

        // rendering order
        {
            // plates
            addChild( negativePlateNode );
            addChild( positivePlateNode );

            // controls
            addChild( enControlA );
            addChild( enControlB );
            addChild( controlPanelNode );

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

        // layout, based on molecule location
        {
            final double moleculeX = model.molecule.location.getX();
            final double plateXOffset = 250; // x offset from molecule
            negativePlateNode.setOffset( moleculeX - plateXOffset - negativePlateNode.getFullBoundsReference().getWidth(),
                                         100 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            positivePlateNode.setOffset( moleculeX + plateXOffset,
                                         negativePlateNode.getYOffset() );
            enControlA.setOffset( moleculeX - enControlA.getFullBoundsReference().getWidth() - 5, 50 );
            enControlB.setOffset( moleculeX + 5, enControlA.getYOffset() );
            electrostaticPotentialColorKeyNode.setOffset( moleculeX - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          negativePlateNode.getFullBoundsReference().getMaxY() + 5 );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            bondTypeNode.setOffset( moleculeX - ( bondTypeNode.getFullBoundsReference().getWidth() / 2 ),
                                    enControlA.getFullBoundsReference().getMaxY() + 10 - PNodeLayoutUtils.getOriginYOffset( bondTypeNode ) );
            controlPanelNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + 25, positivePlateNode.getYOffset() );
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

            viewProperties.surfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType surfaceType ) {
                    electrostaticPotentialNode.setVisible( surfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL );
                    electrostaticPotentialColorKeyNode.setVisible( electrostaticPotentialNode.getVisible() );
                    electronDensityNode.setVisible( surfaceType == SurfaceType.ELECTRON_DENSITY );
                    electronDensityColorKeyNode.setVisible( electronDensityNode.getVisible() );
                }
            } );
        }
    }
}
