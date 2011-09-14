// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.BondDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.MolecularDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode.CompositePartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode.OppositePartialChargeNode;
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

    private static final double DIPOLE_SCALE = 1.0; // how much to scale the dipoles in the view

    public ThreeAtomsCanvas( ThreeAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {

        // nodes
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        PNode moleculeNode = new TriatomicMoleculeNode( model.molecule );
        final PNode partialChargeNodeA = new OppositePartialChargeNode( model.molecule.atomA, model.molecule.bondAB );
        final PNode partialChargeNodeB = new CompositePartialChargeNode( model.molecule.atomB, model.molecule );
        final PNode partialChargeNodeC = new OppositePartialChargeNode( model.molecule.atomC, model.molecule.bondBC );
        final PNode bondDipoleABNode = new BondDipoleNode( model.molecule.bondAB, DIPOLE_SCALE );
        final PNode bondDipoleBCNode = new BondDipoleNode( model.molecule.bondBC, DIPOLE_SCALE );
        final PNode molecularDipoleNode = new MolecularDipoleNode( model.molecule, DIPOLE_SCALE );
        PNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode enControlC = new ElectronegativityControlNode( model.molecule.atomC, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode controlPanelNode = new MPControlPanelNode( parentFrame, new Resettable[] { model, viewProperties },
                                                         new ViewControlPanel( viewProperties, true, false, false, false, MPStrings.BOND_DIPOLES ),
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
            addChild( partialChargeNodeA );
            addChild( partialChargeNodeB );
            addChild( partialChargeNodeC );
            addChild( bondDipoleABNode );
            addChild( bondDipoleBCNode );
            addChild( molecularDipoleNode );
        }

        // layout, relative to molecule location
        {
            final double moleculeX = model.molecule.getLocation().getX();
            final double plateXOffset = 300; // x offset from molecule
            negativePlateNode.setOffset( moleculeX - plateXOffset - negativePlateNode.getFullBoundsReference().getWidth(),
                                         100 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            positivePlateNode.setOffset( moleculeX + plateXOffset,
                                         negativePlateNode.getYOffset() );
            enControlB.setOffset( moleculeX - ( enControlB.getFullBoundsReference().getWidth() / 2 ), 50 );
            enControlA.setOffset( enControlB.getFullBounds().getMinX() - enControlA.getFullBoundsReference().getWidth() - 10,
                                  enControlB.getYOffset() );
            enControlC.setOffset( enControlB.getFullBounds().getMaxX() + 10,
                                  enControlB.getYOffset() );
            controlPanelNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + 25,
                                        positivePlateNode.getYOffset() );
        }

        // synchronize with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    bondDipoleABNode.setVisible( visible );
                    bondDipoleBCNode.setVisible( visible );
                }
            } );

            viewProperties.molecularDipoleVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    molecularDipoleNode.setVisible( visible );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    partialChargeNodeA.setVisible( visible );
                    partialChargeNodeB.setVisible( visible );
                    partialChargeNodeC.setVisible( visible );
                }
            } );
        }
    }
}
