// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.MPResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeControlNode;
import edu.colorado.phet.moleculepolarity.common.control.SurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.ElectronegativityTableNode;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.ElectronDensityColorKeyNode;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.ElectrostaticPotentialColorKeyNode;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.RainbowElectrostaticPotentialColorKeyNode;
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

    private static final Dimension JMOL_VIEWER_SIZE = new Dimension( 475, 475 );

    public RealMoleculesCanvas( RealMoleculesModel model, final ViewProperties viewProperties, Frame parentFrame ) {
        super();

        // nodes
        final JmolViewerNode viewerNode = new JmolViewerNode( model.currentMolecule, getBackground(), JMOL_VIEWER_SIZE );
        PNode scriptNode = new JmolScriptNode( viewerNode );
        final ElectronegativityTableNode electronegativityTableNode = new ElectronegativityTableNode( viewerNode );
        PNode moleculeComboBox = new MoleculeControlNode( model.getMolecules(), model.currentMolecule );
        final PNode electrostaticPotentialColorKeyNode = new ElectrostaticPotentialColorKeyNode();
        final PNode rainbowColorKeyNode = new RainbowElectrostaticPotentialColorKeyNode();
        final PNode electronDensityColorKeyNode = new ElectronDensityColorKeyNode();

        // Floating control panels, with uniform width
        MPControlPanel viewControlPanel = new ViewControlPanel( viewProperties, true, false, true, true, MPStrings.BOND_DIPOLES );
        MPControlPanel surfaceControlPanel = new SurfaceControlPanel( viewProperties.isosurfaceType );
        int minWidth = (int) Math.max( viewControlPanel.getPreferredSize().getWidth(), surfaceControlPanel.getPreferredSize().getWidth() );
        viewControlPanel.setMinWidth( minWidth );
        surfaceControlPanel.setMinWidth( minWidth );
        PNode viewControlsNode = new ControlPanelNode( viewControlPanel );
        PNode isosurfaceControlsNode = new ControlPanelNode( surfaceControlPanel );
        PNode resetAllButtonNode = new MPResetAllButtonNode( new Resettable[] { model, viewProperties }, parentFrame );

        // rendering order
        {
            // controls
            addChild( viewControlsNode );
            addChild( isosurfaceControlsNode );
            addChild( resetAllButtonNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( scriptNode );
            }
            addChild( electronegativityTableNode );

            // indicators
            addChild( electrostaticPotentialColorKeyNode );
            addChild( rainbowColorKeyNode );
            addChild( electronDensityColorKeyNode );

            // molecule
            addChild( viewerNode );

            // combo box on top because it has a popup
            addChild( moleculeComboBox );
        }

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            viewerNode.setOffset( 0, moleculeComboBox.getFullBoundsReference().getHeight() + 2 * ySpacing );
            electrostaticPotentialColorKeyNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          viewerNode.getFullBoundsReference().getMaxY() + ySpacing );
            rainbowColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            electronegativityTableNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electronegativityTableNode.getFullBoundsReference().getWidth() / 2 ),
                                                  electrostaticPotentialColorKeyNode.getFullBoundsReference().getMaxY() + 20 );
            moleculeComboBox.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( moleculeComboBox.getFullBoundsReference().getWidth() / 2 ),
                                        ySpacing );
            viewControlsNode.setOffset( viewerNode.getFullBoundsReference().getMaxX() + xSpacing, viewerNode.getYOffset() );
            isosurfaceControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( isosurfaceControlsNode.getXOffset(), isosurfaceControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            scriptNode.setOffset( resetAllButtonNode.getXOffset(), resetAllButtonNode.getFullBoundsReference().getMaxY() + ySpacing );

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

            viewProperties.electronegativityTableVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    electronegativityTableNode.setVisible( visible );
                }
            } );

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType isosurfaceType ) {
                    viewerNode.setIsosurfaceType( isosurfaceType );
                }
            } );

            RichSimpleObserver colorKeyUpdater = new RichSimpleObserver() {
                public void update() {
                    electrostaticPotentialColorKeyNode.setVisible( viewProperties.isosurfaceType.get() == SurfaceType.ELECTROSTATIC_POTENTIAL && !JmolViewerNode.RAINBOW_MEP.get() );
                    rainbowColorKeyNode.setVisible( viewProperties.isosurfaceType.get() == SurfaceType.ELECTROSTATIC_POTENTIAL && JmolViewerNode.RAINBOW_MEP.get() );
                    electronDensityColorKeyNode.setVisible( viewProperties.isosurfaceType.get() == SurfaceType.ELECTRON_DENSITY );
                }
            };
            colorKeyUpdater.observe( viewProperties.isosurfaceType, JmolViewerNode.RAINBOW_MEP );
        }
    }
}
