// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Dimension;
import java.awt.Frame;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode;
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
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesCanvas extends MPCanvas {

    private static final Dimension JMOL_VIEWER_SIZE = new Dimension( 450, 450 );

    private final JmolViewerNode viewerNode;

    public RealMoleculesCanvas( RealMoleculesModel model, final ViewProperties viewProperties, Frame parentFrame ) {

        // nodes
        viewerNode = new JmolViewerNode( model.currentMolecule, getBackground(), JMOL_VIEWER_SIZE );
        final ElectronegativityTableNode electronegativityTableNode = new ElectronegativityTableNode( viewerNode );
        PNode moleculeComboBox = new MoleculeControlNode( model.getMolecules(), model.currentMolecule );
        final PNode electrostaticPotentialColorKeyNode = new ElectrostaticPotentialColorKeyNode();
        final PNode rainbowColorKeyNode = new RainbowElectrostaticPotentialColorKeyNode();
        final PNode electronDensityColorKeyNode = new ElectronDensityColorKeyNode();
        PNode controlPanelNode = new MPControlPanelNode( parentFrame, new Resettable[] { model, viewProperties },
                                                         new ViewControlPanel( viewProperties, true, false, true, true, MPStrings.BOND_DIPOLES ),
                                                         new SurfaceControlPanel( viewProperties.surfaceType ) );

        // rendering order
        {
            // controls
            addChild( controlPanelNode );

            // indicators
            addChild( electronegativityTableNode );
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
            final double xSpacing = 75;
            final double viewerX = ( getStageSize().getWidth() - viewerNode.getFullBoundsReference().getWidth() - controlPanelNode.getFullBoundsReference().getWidth() - xSpacing ) / 2;
            moleculeComboBox.setOffset( viewerX + ( viewerNode.getFullBoundsReference().getWidth() / 2 ) - ( moleculeComboBox.getFullBoundsReference().getWidth() / 2 ), 25 );
            viewerNode.setOffset( viewerX,
                                  moleculeComboBox.getFullBoundsReference().getMaxY() + 10 );
            electrostaticPotentialColorKeyNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          viewerNode.getFullBoundsReference().getMaxY() + 10 );
            rainbowColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            electronegativityTableNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electronegativityTableNode.getFullBoundsReference().getWidth() / 2 ),
                                                  electrostaticPotentialColorKeyNode.getFullBoundsReference().getMaxY() + 20 );
            controlPanelNode.setOffset( viewerNode.getFullBoundsReference().getMaxX() + xSpacing,
                                        viewerNode.getFullBoundsReference().getCenterY() - ( controlPanelNode.getFullBoundsReference().getHeight() / 2 ) );
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

            viewProperties.surfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType surfaceType ) {
                    viewerNode.setSurfaceType( surfaceType );
                }
            } );

            RichSimpleObserver colorKeyUpdater = new RichSimpleObserver() {
                public void update() {
                    electrostaticPotentialColorKeyNode.setVisible( viewProperties.surfaceType.get() == SurfaceType.ELECTROSTATIC_POTENTIAL && !JmolViewerNode.RAINBOW_MEP.get() );
                    rainbowColorKeyNode.setVisible( viewProperties.surfaceType.get() == SurfaceType.ELECTROSTATIC_POTENTIAL && JmolViewerNode.RAINBOW_MEP.get() );
                    electronDensityColorKeyNode.setVisible( viewProperties.surfaceType.get() == SurfaceType.ELECTRON_DENSITY );
                }
            };
            colorKeyUpdater.observe( viewProperties.surfaceType, JmolViewerNode.RAINBOW_MEP );
        }
    }

    public JmolViewer getJmolViewer() {
        return viewerNode.getViewer();
    }
}
