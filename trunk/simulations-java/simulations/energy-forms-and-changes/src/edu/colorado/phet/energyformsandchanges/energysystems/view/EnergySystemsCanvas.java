// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemsModel;
import edu.colorado.phet.energyformsandchanges.energysystems.model.ShapeModelElement;
import edu.colorado.phet.energyformsandchanges.intro.view.NormalAndFastForwardTimeControlPanel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Piccolo canvas for the "Energy Systems" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EnergySystemsCanvas extends PhetPCanvas {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;
    private static final double CONTROL_INSET = 10;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final BooleanProperty normalSimSpeed = new BooleanProperty( true );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergySystemsCanvas( final EnergySystemsModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform. IMPORTANT NOTES: The multiplier
        // factors for the 2nd point can be adjusted to shift the center right
        // or left, and the scale factor can be adjusted to zoom in or out
        // (smaller numbers zoom out, larger ones zoom in).
        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( DEFAULT_STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( DEFAULT_STAGE_SIZE.getHeight() * 0.5 ) ),
                2200 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( new Color( 245, 246, 247 ) );

        //------- Node Creation ----------------------------------------------

        // Create the clock controls. TODO: i18n
        PNode clockControl = new NormalAndFastForwardTimeControlPanel( normalSimSpeed, model.getClock() );
        {
            normalSimSpeed.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean normalSimSpeed ) {
                    ConstantDtClock clock = (ConstantDtClock) model.getClock();
                    clock.setDt( normalSimSpeed ? EFACConstants.SIM_TIME_PER_TICK_NORMAL : EFACConstants.SIM_TIME_PER_TICK_FAST_FORWARD );
                }
            } );
        }

        // Create the back drop for the clock controls.
        PNode clockControlBackground = new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth() * 2, clockControl.getFullBoundsReference().getHeight() ),
                                                      Color.LIGHT_GRAY,
                                                      new BasicStroke( 1 ),
                                                      Color.BLACK );

        // Create the control for showing/hiding energy chunks. TODO: i18n
        PropertyCheckBox showEnergyCheckBox = new PropertyCheckBox( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                    "Energy Symbols",
                                                                    model.energyChunksVisible ) {{
            setFont( new PhetFont( 20 ) );
        }};
        ControlPanelNode showEnergyControlPanel = new ControlPanelNode( showEnergyCheckBox, EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );

        // Create the legend for energy chunks.
        final PNode energyChunkLegend = new EnergyChunkLegend();

        // Control the visibility of energy chunks and the energy chunk legend.
        model.energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyChunksVisible ) {
                energyChunkLegend.setVisible( energyChunksVisible );
            }
        } );

        // Create the carousel control nodes.
        PNode energySourcesCarouselController = new CarouselControllerNode( model.energySourcesCarousel );
        PNode energyConvertersCarouselController = new CarouselControllerNode( model.energyConvertersCarousel );
        PNode energyUsersCarouselController = new CarouselControllerNode( model.energyUsersCarousel );

        // TODO: Temp.
        addWorldChild( new FaucetNode( EnergyFormsAndChangesSimSharing.UserComponents.faucet,
                                       new Property<Double>( 0.0 ),
                                       new BooleanProperty( true ),
                                       200,
                                       true ) {{ setOffset( 130, 20 );}} );

        // Add the nodes that exist in the carousels.
        List<ShapeNode> shapeNodes = new ArrayList<ShapeNode>();
        for ( ShapeModelElement shapeModelElement : model.shapeModelElementList ) {
            shapeNodes.add( new ShapeNode( shapeModelElement, mvt ) );
        }


        //------- Node Layering -----------------------------------------------

        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        for ( PNode shapeNode : shapeNodes ) {
            rootNode.addChild( shapeNode );
        }
        rootNode.addChild( clockControlBackground );
        rootNode.addChild( clockControl );
        rootNode.addChild( showEnergyControlPanel );
        rootNode.addChild( energyChunkLegend );
        rootNode.addChild( energySourcesCarouselController );
        rootNode.addChild( energyConvertersCarouselController );
        rootNode.addChild( energyUsersCarouselController );

        //------- Node Layout -------------------------------------------------

        // Clock controls
        clockControlBackground.setOffset( STAGE_SIZE.getWidth() / 2 - clockControlBackground.getFullBoundsReference().getWidth() / 2,
                                          STAGE_SIZE.getHeight() - clockControlBackground.getFullBoundsReference().getHeight() );
        clockControl.setOffset( STAGE_SIZE.getWidth() / 2 - clockControl.getFullBoundsReference().getWidth() / 2,
                                STAGE_SIZE.getHeight() - clockControl.getFullBoundsReference().height );

        // Energy chunk control.
        {
            double energyControlsCenterX = STAGE_SIZE.getWidth() - Math.max( showEnergyControlPanel.getFullWidth() / 2, energyChunkLegend.getFullBoundsReference().getWidth() / 2 ) - CONTROL_INSET;
            showEnergyControlPanel.setOffset( energyControlsCenterX - showEnergyControlPanel.getFullBoundsReference().getWidth() / 2, CONTROL_INSET );
            energyChunkLegend.setOffset( energyControlsCenterX - energyChunkLegend.getFullBoundsReference().getWidth() / 2,
                                         showEnergyControlPanel.getFullBoundsReference().getMaxY() + CONTROL_INSET );
        }

        // Carousel control.  TODO: Will ultimately be linked to horizontal carousel position.
        double carouselControllersYPos = clockControlBackground.getFullBoundsReference().getMinY() - energyConvertersCarouselController.getFullBoundsReference().getHeight() - CONTROL_INSET;
        energySourcesCarouselController.setOffset( 100, carouselControllersYPos );
        energyConvertersCarouselController.setOffset( 350, carouselControllersYPos );
        energyUsersCarouselController.setOffset( 650, carouselControllersYPos );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------
}
