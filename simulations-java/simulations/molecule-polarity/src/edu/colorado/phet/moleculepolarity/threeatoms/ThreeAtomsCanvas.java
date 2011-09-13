// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
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
        super();

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
        PNode controlPanelNode = new ThreeAtomsControlPanelNode( model, viewProperties, parentFrame );

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

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 100 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            enControlA.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, 50 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + 10, enControlA.getYOffset() );
            enControlC.setOffset( enControlB.getFullBounds().getMaxX() + 10, enControlB.getYOffset() );
            positivePlateNode.setOffset( enControlC.getFullBounds().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            controlPanelNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, positivePlateNode.getYOffset() );
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
