// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Dimension;
import java.awt.Frame;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBox;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBoxWithIcon;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPVerticalPanel;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeControlNode;
import edu.colorado.phet.moleculepolarity.common.control.SurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.DipoleNode;
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

        // floating control panel
        PNode controlPanelNode = new MPControlPanelNode( parentFrame,
                                                         new Resettable[] { model, viewProperties },
                                                         // View
                                                         new MPVerticalPanel( MPStrings.VIEW ) {{
                                                             add( new MPCheckBoxWithIcon( UserComponents.bondDipolesCheckBox, MPStrings.BOND_DIPOLES, DipoleNode.createIcon( MPColors.BOND_DIPOLE ), viewProperties.bondDipolesVisible ) );
                                                             add( new MPCheckBoxWithIcon( UserComponents.molecularDipoleCheckBox, MPStrings.MOLECULAR_DIPOLE, DipoleNode.createIcon( MPColors.MOLECULAR_DIPOLE ), viewProperties.molecularDipoleVisible ) );
                                                             add( new MPCheckBox( UserComponents.partialChargesCheckBox, MPStrings.PARTIAL_CHARGES, viewProperties.partialChargesVisible ) );
                                                             add( new MPCheckBox( UserComponents.atomElectronegativitiesCheckBox, MPStrings.ATOM_ELECTRONEGATIVITIES, viewProperties.electronegativityTableVisible ) );
                                                             add( new MPCheckBox( UserComponents.atomLabelsCheckBox, MPStrings.ATOM_LABELS, viewProperties.atomLabelsVisible ) );
                                                         }},
                                                         // Surface
                                                         new SurfaceControlPanel( viewProperties.surfaceType ) );

        /*
         * WORKAROUND:
         * Above, we set the background color of the Jmol viewer to be the same as the canvas.
         * But Jmol lightens the background color slightly, enough so that you can see the boundaries
         * of the Jmol viewer. This workaround sets the canvas background to the actual RGB values
         * rendered by the Jmol viewer. This canvas will be slightly lighter than canvases that
         * don't use Jmol, but the user is unlikely to notice when switching between modules.
         */
        setBackground( BufferedImageUtils.getPixelColor( BufferedImageUtils.toBufferedImage( viewerNode.toImage() ), 0, 0 ) );

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
            // centered above viewer
            moleculeComboBox.setOffset( viewerX + ( viewerNode.getFullBoundsReference().getWidth() / 2 ) - ( moleculeComboBox.getFullBoundsReference().getWidth() / 2 ), 25 );
            // centered in the space to the left of the control panel
            viewerNode.setOffset( viewerX, moleculeComboBox.getFullBoundsReference().getMaxY() + 10 );
            // centered below the viewer
            electrostaticPotentialColorKeyNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          viewerNode.getFullBoundsReference().getMaxY() + 10 );
            rainbowColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            // centered below the color keys
            electronegativityTableNode.setOffset( viewerNode.getFullBoundsReference().getCenterX() - ( electronegativityTableNode.getFullBoundsReference().getWidth() / 2 ),
                                                  electrostaticPotentialColorKeyNode.getFullBoundsReference().getMaxY() + 20 );
            // to the right of the viewer, vertically centered
            controlPanelNode.setOffset( viewerNode.getFullBoundsReference().getMaxX() + xSpacing,
                                        viewerNode.getFullBoundsReference().getCenterY() - ( controlPanelNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // synchronization with view properties
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

    // Used by developer-only code to open a Jmol Console
    public JmolViewer getJmolViewer() {
        return viewerNode.getViewer();
    }
}
