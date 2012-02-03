// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPSimSharing;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBox;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBoxWithIcon;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPVerticalPanel;
import edu.colorado.phet.moleculepolarity.common.control.SurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.BondCharacterNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.DipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
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

    public TwoAtomsCanvas( TwoAtomsModel model, final ViewProperties viewProperties, Frame parentFrame ) {

        // nodes
        final DiatomicMoleculeNode moleculeNode = new DiatomicMoleculeNode( model.molecule );
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        PNode enControlA = new ElectronegativityControlNode( UserComponents.electronegativityControlA, model.molecule.atomA, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode enControlB = new ElectronegativityControlNode( UserComponents.electronegativityControlB, model.molecule.atomB, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        final PNode bondTypeNode = new BondCharacterNode( model.molecule );
        final PNode electrostaticPotentialColorKeyNode = new ElectrostaticPotentialColorKeyNode();
        final PNode electronDensityColorKeyNode = new ElectronDensityColorKeyNode();

        // floating control panel
        PNode controlPanelNode = new MPControlPanelNode( parentFrame,
                                                         new Resettable[] { model, viewProperties },
                                                         // View
                                                         new MPVerticalPanel( MPStrings.VIEW ) {{
                                                             add( new MPCheckBoxWithIcon( UserComponents.bondDipolesCheckBox, MPStrings.BOND_DIPOLE, DipoleNode.createIcon( MPColors.BOND_DIPOLE ), viewProperties.bondDipolesVisible ) );
                                                             add( new MPCheckBox( UserComponents.partialChargesCheckBox, MPStrings.PARTIAL_CHARGES, viewProperties.partialChargesVisible ) );
                                                             add( new MPCheckBox( UserComponents.bondCharacterCheckBox, MPStrings.BOND_CHARACTER, viewProperties.bondCharacterVisible ) );
                                                         }},
                                                         // Surface
                                                         new SurfaceControlPanel( viewProperties.surfaceType ),
                                                         // Electric Field
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
            addChild( moleculeNode );
        }

        // layout, based on molecule location
        {
            final double moleculeX = model.molecule.location.getX();
            final double moleculeY = model.molecule.location.getY();
            final double plateXOffset = 250; // x offset from molecule
            // to left of molecule, vertically centered
            negativePlateNode.setOffset( moleculeX - plateXOffset - negativePlateNode.getFullBoundsReference().getWidth(),
                                         moleculeY - ( MPConstants.PLATE_HEIGHT / 2 ) );
            // to right of molecule, vertically centered
            positivePlateNode.setOffset( moleculeX + plateXOffset,
                                         negativePlateNode.getYOffset() );
            // centered above molecule
            enControlA.setOffset( moleculeX - enControlA.getFullBoundsReference().getWidth() - 5, 50 );
            enControlB.setOffset( moleculeX + 5, enControlA.getYOffset() );
            // centered below molecule
            electrostaticPotentialColorKeyNode.setOffset( moleculeX - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          negativePlateNode.getFullBoundsReference().getMaxY() - 10 );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            // centered below EN controls
            bondTypeNode.setOffset( moleculeX - ( bondTypeNode.getFullBoundsReference().getWidth() / 2 ),
                                    enControlA.getFullBoundsReference().getMaxY() + 10 - PNodeLayoutUtils.getOriginYOffset( bondTypeNode ) );
            // to right of positive plate, top aligned
            controlPanelNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + 25, positivePlateNode.getYOffset() );
        }

        // synchronization with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    moleculeNode.setBondDipoleVisible( visible );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    moleculeNode.setPartialChargesVisible( visible );
                }
            } );

            viewProperties.bondCharacterVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    bondTypeNode.setVisible( visible );
                }
            } );

            viewProperties.surfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType surfaceType ) {
                    moleculeNode.setSurface( surfaceType );
                    electrostaticPotentialColorKeyNode.setVisible( surfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL );
                    electronDensityColorKeyNode.setVisible( surfaceType == SurfaceType.ELECTRON_DENSITY );
                }
            } );
        }
    }
}
