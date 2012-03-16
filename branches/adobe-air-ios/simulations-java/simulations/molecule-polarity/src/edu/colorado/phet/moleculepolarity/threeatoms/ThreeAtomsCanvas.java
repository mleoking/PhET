// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBox;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBoxWithIcon;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPVerticalPanel;
import edu.colorado.phet.moleculepolarity.common.view.DipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.TriatomicMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Three Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsCanvas extends MPCanvas {

    public ThreeAtomsCanvas( ThreeAtomsModel model, final ViewProperties viewProperties, Frame parentFrame ) {

        // nodes
        final TriatomicMoleculeNode moleculeNode = new TriatomicMoleculeNode( model.molecule );
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        PNode enControlA = new ElectronegativityControlNode( UserComponents.electronegativityControlA, model.molecule.atomA, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode enControlB = new ElectronegativityControlNode( UserComponents.electronegativityControlB, model.molecule.atomB, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode enControlC = new ElectronegativityControlNode( UserComponents.electronegativityControlC, model.molecule.atomC, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );

        // floating control panel
        PNode controlPanelNode = new MPControlPanelNode( parentFrame,
                                                         new Resettable[] { model, viewProperties },
                                                         // View
                                                         new MPVerticalPanel( MPStrings.VIEW ) {{
                                                             add( new MPCheckBoxWithIcon( UserComponents.bondDipolesCheckBox, MPStrings.BOND_DIPOLES, DipoleNode.createIcon( MPColors.BOND_DIPOLE ), viewProperties.bondDipolesVisible ) );
                                                             add( new MPCheckBoxWithIcon( UserComponents.molecularDipoleCheckBox, MPStrings.MOLECULAR_DIPOLE, DipoleNode.createIcon( MPColors.MOLECULAR_DIPOLE ), viewProperties.molecularDipoleVisible ) );
                                                             add( new MPCheckBox( UserComponents.partialChargesCheckBox, MPStrings.PARTIAL_CHARGES, viewProperties.partialChargesVisible ) );
                                                         }},
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
            addChild( enControlC );
            addChild( controlPanelNode );

            // molecule
            addChild( moleculeNode );
        }

        // layout, relative to molecule location
        {
            final double moleculeX = model.molecule.location.getX();
            final double moleculeY = model.molecule.location.getY();
            final double plateXOffset = 300; // x offset from molecule
            // to left of molecule, vertically centered
            negativePlateNode.setOffset( moleculeX - plateXOffset - negativePlateNode.getFullBoundsReference().getWidth(),
                                         moleculeY - ( MPConstants.PLATE_HEIGHT / 2 ) );
            // to right of molecule, vertically centered
            positivePlateNode.setOffset( moleculeX + plateXOffset,
                                         negativePlateNode.getYOffset() );
            // centered above molecule
            enControlB.setOffset( moleculeX - ( enControlB.getFullBoundsReference().getWidth() / 2 ), 50 );
            enControlA.setOffset( enControlB.getFullBounds().getMinX() - enControlA.getFullBoundsReference().getWidth() - 10,
                                  enControlB.getYOffset() );
            enControlC.setOffset( enControlB.getFullBounds().getMaxX() + 10,
                                  enControlB.getYOffset() );
            // to right of positive plate, top aligned
            controlPanelNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + 25,
                                        positivePlateNode.getYOffset() );
        }

        // synchronization with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    moleculeNode.setBondDipolesVisible( visible );
                }
            } );

            viewProperties.molecularDipoleVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    moleculeNode.setMolecularDipoleVisible( visible );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    moleculeNode.setPartialChargesVisible( visible );
                }
            } );
        }
    }
}
