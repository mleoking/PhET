// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.ModelControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.TestControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.DipoleNode.BondDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.EFieldPlateNode.NegativeEFieldPlateNode;
import edu.colorado.phet.moleculepolarity.common.view.EFieldPlateNode.PositiveEFieldPlateNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.TwoAtomMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsCanvas extends MPCanvas {

    public TwoAtomsCanvas( TwoAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        TwoAtomMoleculeNode moleculeNode = new TwoAtomMoleculeNode( model.molecule );
        addChild( moleculeNode );

        BondDipoleNode bondDipoleNode = new BondDipoleNode( model.molecule.bond );
        addChild( bondDipoleNode );

        ElectronegativityControlNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        ElectronegativityControlNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        addChild( enControlA );
        addChild( enControlB );

        PNode modelControlsNode = new ControlPanelNode( new ModelControlPanel( viewProperties.modelRepresentation ) );
        addChild( modelControlsNode );

        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties ) );
        addChild( viewControlsNode );

        PNode testControlsNode = new ControlPanelNode( new TestControlPanel( model.eField.enabled ) );
        addChild( testControlsNode );

        Resettable[] resettables = new Resettable[] { model, viewProperties };
        PNode resetAllButtonNode = new ResetAllButtonNode( resettables, parentFrame, 16, Color.BLACK, Color.YELLOW );
        addChild( resetAllButtonNode );

        NegativeEFieldPlateNode negativeEFieldPlateNode = new NegativeEFieldPlateNode( model.eField );
        addChild( negativeEFieldPlateNode );

        PositiveEFieldPlateNode positiveEFieldPlateNode = new PositiveEFieldPlateNode( model.eField );
        addChild( positiveEFieldPlateNode );

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativeEFieldPlateNode.setOffset( 30, 125 - PNodeLayoutUtils.getOriginYOffset( negativeEFieldPlateNode ) );
            enControlA.setOffset( negativeEFieldPlateNode.getFullBoundsReference().getMaxX() + xSpacing, 100 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + xSpacing, enControlA.getYOffset() );
            positiveEFieldPlateNode.setOffset( enControlB.getFullBounds().getMaxX() + xSpacing, negativeEFieldPlateNode.getYOffset() );
            modelControlsNode.setOffset( positiveEFieldPlateNode.getFullBoundsReference().getMaxX() + xSpacing, enControlA.getYOffset() );
            viewControlsNode.setOffset( modelControlsNode.getXOffset(), modelControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            testControlsNode.setOffset( modelControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( modelControlsNode.getXOffset(), testControlsNode.getFullBoundsReference().getMaxY() + ySpacing );


        }
    }
}
