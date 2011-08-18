// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.MPResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeControlNode;
import edu.colorado.phet.moleculepolarity.common.control.SurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.ElectronegativityTableNode;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;
import edu.colorado.phet.moleculepolarity.developer.JmolScriptNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesCanvas extends MPCanvas {

    private static final Dimension JMOL_VIEWER_SIZE = new Dimension( 400, 400 );

    public RealMoleculesCanvas( RealMoleculesModel model, final ViewProperties viewProperties, Frame parentFrame ) {
        super();

        // nodes
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        final JmolViewerNode viewerNode = new JmolViewerNode( model.currentMolecule, getBackground(), JMOL_VIEWER_SIZE );
        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties, true, false, true, MPStrings.BOND_DIPOLES ) );
        PNode isosurfaceControlsNode = new ControlPanelNode( new SurfaceControlPanel( viewProperties.isosurfaceType ) );
        PNode eFieldControlsNode = new ControlPanelNode( new EFieldControlPanel( model.eField.enabled ) );
        PNode resetAllButtonNode = new MPResetAllButtonNode( new Resettable[] { model, viewProperties }, parentFrame );
        JmolScriptNode scriptNode = new JmolScriptNode( viewerNode );
        final ElectronegativityTableNode electronegativityTableNode = new ElectronegativityTableNode( model.currentMolecule, viewerNode );
        MoleculeControlNode moleculeComboBox = new MoleculeControlNode( model.getMolecules(), model.currentMolecule );

        // rendering order
        {
            // plates
            addChild( negativePlateNode );
            addChild( positivePlateNode );

            // controls
            addChild( viewControlsNode );
            addChild( isosurfaceControlsNode );
            addChild( eFieldControlsNode );
            addChild( resetAllButtonNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( scriptNode );
            }
            addChild( electronegativityTableNode );

            // molecule
            addChild( viewerNode );

            // combo box on top because it has a popup
            addChild( moleculeComboBox );
        }

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 30 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            viewerNode.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            positivePlateNode.setOffset( viewerNode.getFullBoundsReference().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            electronegativityTableNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electronegativityTableNode.getFullBoundsReference().getWidth() / 2 ),
                                                  viewerNode.getFullBoundsReference().getMaxY() + 20 );
            moleculeComboBox.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( moleculeComboBox.getFullBoundsReference().getWidth() / 2 ),
                                        viewerNode.getFullBoundsReference().getMinY() - moleculeComboBox.getFullBoundsReference().getHeight() - 30 );
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, positivePlateNode.getYOffset() );
            isosurfaceControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            eFieldControlsNode.setOffset( isosurfaceControlsNode.getXOffset(), isosurfaceControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( isosurfaceControlsNode.getXOffset(), eFieldControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            scriptNode.setOffset( resetAllButtonNode.getXOffset(), resetAllButtonNode.getFullBoundsReference().getMaxY() + 50 );

        }

        // synchronize with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setBondDipolesVisible( visible );
                }
            } );

            viewProperties.molecularDipoleVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setMolecularDipoleVisible( visible );
                }
            } );

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType isosurfaceType ) {
                    viewerNode.setIsosurfaceType( isosurfaceType );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setPartialChargeVisible( visible );
                }
            } );

            viewProperties.atomLabelsVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    viewerNode.setAtomLabelsVisible( visible );
                }
            } );
        }
    }
}
