// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Piccolo canvas for the "Energy Systems" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EnergySystemsCanvas extends PhetPCanvas implements Resettable {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;
    private static final double CONTROL_INSET = 10;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final BooleanProperty energyChunkVizCheckBoxProperty = new BooleanProperty( false );
    private final EnergySystemsModel model;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergySystemsCanvas( final EnergySystemsModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform. IMPORTANT NOTES: The multiplier
        // factors for the 2nd point can be adjusted to shift the center right
        // or left, and the scale factor can be adjusted to zoom in or out
        // (smaller numbers zoom out, larger ones zoom in).
        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( DEFAULT_STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( DEFAULT_STAGE_SIZE.getHeight() * 0.475 ) ),
                EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( EFACConstants.SECOND_TAB_BACKGROUND_COLOR );

        //------- Node Creation ----------------------------------------------

        PNode clockControl = new PSwing( new PiccoloClockControlPanel( model.getClock() ) {{
            Color transparent = new Color( 0, 0, 0, 0 );
            setBackground( transparent );
            getButtonCanvas().setBackground( transparent );
            getBackgroundNode().setVisible( false );
        }} );

        // Create the back drop for the clock controls.
        PNode clockControlBackground = new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth() * 2, clockControl.getFullBoundsReference().getHeight() * 20 ), // Tall enough so that users are unlikely to see bottom.
                                                      Color.LIGHT_GRAY,
                                                      new BasicStroke( 1 ),
                                                      Color.BLACK );

        // Create the control for showing/hiding energy chunks.
        PropertyCheckBox showEnergyCheckBox = new PropertyCheckBox( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                    EnergyFormsAndChangesResources.Strings.ENERGY_SYMBOLS,
                                                                    energyChunkVizCheckBoxProperty ) {{
            setFont( new PhetFont( 20 ) );
        }};
        ControlPanelNode showEnergyControlPanel = new ControlPanelNode( showEnergyCheckBox, EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );

        // Hook up the check box.  This is done through a local property so
        // that energy chunk pre-loading can be done prior to making ECs visible.
        energyChunkVizCheckBoxProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyVisible ) {
                if ( energyVisible ) {
                    model.preLoadEnergyChunks();
                }
                model.energyChunksVisible.set( energyVisible );
            }
        } );

        // Create the legend for energy chunks.
        final PNode energyChunkLegend = new EnergyChunkLegend();

        // Control the visibility of energy chunks and the energy chunk legend.
        model.energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyChunksVisible ) {
                energyChunkLegend.setVisible( energyChunksVisible );
            }
        } );

        // Create the carousel control nodes.
        PNode energySourcesCarouselController = new EnergySystemElementSelector( model.energySourcesCarousel );
        PNode energyConvertersCarouselController = new EnergySystemElementSelector( model.energyConvertersCarousel );
        PNode energyUsersCarouselController = new EnergySystemElementSelector( model.energyUsersCarousel );

        // Create the various energy system elements.
        PNode faucetAndWaterNode = new FaucetAndWaterNode( model.faucet, mvt );
        PNode sunNode = new SunNode( model.sun, model.energyChunksVisible, mvt );
        PNode teaPotNode = new TeaPotNode( model.teaPot, model.getClock(), model.energyChunksVisible, mvt );
        PNode bikerNode = new BikerNode( model.biker, mvt );
        PNode electricalGeneratorNode = new ElectricalGeneratorNode( model.waterPoweredGenerator, mvt );
        PNode incandescentLightBulbNode = new IncandescentLightBulbNode( model.incandescentLightBulb, model.energyChunksVisible, mvt );
        PNode fluorescentLightBulbNode = new FluorescentLightBulbNode( model.fluorescentLightBulb, model.energyChunksVisible, mvt );
        PNode solarPanelNode = new SolarPanelNode( model.solarPanel, mvt );
        PNode beakerHeaterNode = new BeakerHeaterNode( model.getClock(), model.beakerHeater, model.energyChunksVisible, mvt );
        PNode beltNode = new BeltNode( model.belt, mvt );

        // Create the reset button.
        ResetAllButtonNode resetButton = new ResetAllButtonNode( new Resettable[]{this, model}, this, 20, Color.black, new Color( 255, 153, 0 ) );
        resetButton.setConfirmationEnabled( false );

        //------- Node Layering -----------------------------------------------

        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // The order is important due to some of the visual interactions
        // between the energy system elements.
        rootNode.addChild( faucetAndWaterNode );
        rootNode.addChild( solarPanelNode );
        rootNode.addChild( beakerHeaterNode );
        rootNode.addChild( sunNode );
        rootNode.addChild( incandescentLightBulbNode );
        rootNode.addChild( fluorescentLightBulbNode );
        rootNode.addChild( electricalGeneratorNode );
        rootNode.addChild( beltNode );
        rootNode.addChild( bikerNode );
        rootNode.addChild( teaPotNode );

        rootNode.addChild( clockControlBackground );
        rootNode.addChild( clockControl );
        rootNode.addChild( showEnergyControlPanel );
        rootNode.addChild( energyChunkLegend );
        rootNode.addChild( resetButton );
        rootNode.addChild( energySourcesCarouselController );
        rootNode.addChild( energyConvertersCarouselController );
        rootNode.addChild( energyUsersCarouselController );

        //------- Node Layout -------------------------------------------------

        // Clock controls
        clockControlBackground.setOffset( STAGE_SIZE.getWidth() / 2 - clockControlBackground.getFullBoundsReference().getWidth() / 2,
                                          STAGE_SIZE.getHeight() - clockControl.getFullBoundsReference().getHeight() );
        clockControl.setOffset( STAGE_SIZE.getWidth() / 2 - clockControl.getFullBoundsReference().getWidth() / 2,
                                STAGE_SIZE.getHeight() - clockControl.getFullBoundsReference().height );

        // Energy chunk control.
        {
            double energyControlsCenterX = STAGE_SIZE.getWidth() - Math.max( showEnergyControlPanel.getFullWidth() / 2, energyChunkLegend.getFullBoundsReference().getWidth() / 2 ) - CONTROL_INSET;
            showEnergyControlPanel.setOffset( energyControlsCenterX - showEnergyControlPanel.getFullBoundsReference().getWidth() / 2, CONTROL_INSET );
            energyChunkLegend.setOffset( STAGE_SIZE.getWidth() - energyChunkLegend.getFullBoundsReference().getWidth() - CONTROL_INSET,
                                         showEnergyControlPanel.getFullBoundsReference().getMaxY() + CONTROL_INSET );
        }

        // Carousel control.
        double carouselControllersCenterYPos = clockControlBackground.getFullBoundsReference().getMinY() - energyConvertersCarouselController.getFullBoundsReference().getHeight() / 2 - 5;
        energySourcesCarouselController.centerFullBoundsOnPoint( mvt.modelToViewX( model.energySourcesCarousel.getSelectedElementPosition().getX() ),
                                                                 carouselControllersCenterYPos );
        energyConvertersCarouselController.centerFullBoundsOnPoint( mvt.modelToViewX( model.energyConvertersCarousel.getSelectedElementPosition().getX() ),
                                                                    carouselControllersCenterYPos );
        energyUsersCarouselController.centerFullBoundsOnPoint( mvt.modelToViewX( model.energyUsersCarousel.getSelectedElementPosition().getX() ),
                                                               carouselControllersCenterYPos );

        // Reset button.
        resetButton.setOffset( STAGE_SIZE.getWidth() - resetButton.getFullBounds().getWidth() - CONTROL_INSET,
                               clockControlBackground.getFullBounds().getMinY() - resetButton.getFullBounds().getWidth() - CONTROL_INSET );
        resetButton.centerFullBoundsOnPoint( ( energyUsersCarouselController.getFullBoundsReference().getMaxX() + STAGE_SIZE.getWidth() ) / 2,
                                             energyUsersCarouselController.getFullBoundsReference().getCenterY() );
    }

    public void reset() {
        energyChunkVizCheckBoxProperty.reset();
        model.getClock().setPaused( false );
    }
}
