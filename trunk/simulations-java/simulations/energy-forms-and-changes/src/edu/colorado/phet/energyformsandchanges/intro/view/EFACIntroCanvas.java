// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Beaker;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.colorado.phet.energyformsandchanges.common.view.BeakerView;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerStandNode;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkNode;
import edu.colorado.phet.energyformsandchanges.energysystems.view.HeaterCoolerView;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.colorado.phet.energyformsandchanges.intro.model.ElementFollowingThermometer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Piccolo canvas for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EFACIntroCanvas extends PhetPCanvas implements Resettable {

    public static final Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;
    private static final double EDGE_INSET = 10;

    // Boolean property for showing/hiding developer control for dumping energy levels.
    public static final BooleanProperty showDumpEnergiesButton = new BooleanProperty( false );

    private final EFACIntroModel model;
    private final PNode thermometerToolBox;
    private final BooleanProperty normalSimSpeed = new BooleanProperty( true );

    /**
     * Constructor.
     *
     * @param model Model being portrayed on this canvas.
     */
    public EFACIntroCanvas( final EFACIntroModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.85 ) ),
                2200 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( EFACConstants.FIRST_TAB_BACKGROUND_COLOR );

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Create some PNodes that will act as layers in order to create the
        // needed Z-order behavior.
        final PNode backLayer = new PNode();
        rootNode.addChild( backLayer );
        final PNode beakerBackLayer = new PNode();
        rootNode.addChild( beakerBackLayer );
        final PNode beakerGrabLayer = new PNode();
        rootNode.addChild( beakerGrabLayer );
        final PNode blockLayer = new PNode();
        rootNode.addChild( blockLayer );
        PNode airLayer = new PNode();
        rootNode.addChild( airLayer );
        PNode heaterCoolerFrontLayer = new PNode();
        rootNode.addChild( heaterCoolerFrontLayer );
        final PNode thermometerLayer = new PNode();
        rootNode.addChild( thermometerLayer );
        final PNode beakerFrontLayer = new PNode();
        rootNode.addChild( beakerFrontLayer );

        // Add the lab bench surface.
        final PNode labBenchSurface = new PImage( EnergyFormsAndChangesResources.Images.SHELF_LONG );
        labBenchSurface.setOffset( mvt.modelToViewX( 0 ) - labBenchSurface.getFullBoundsReference().getWidth() / 2,
                                   mvt.modelToViewY( 0 ) - labBenchSurface.getFullBoundsReference().getHeight() / 2 + 10 ); // Slight tweak factor here due to nature of image.
        backLayer.addChild( labBenchSurface );

        // Add a node that will act as the background below the lab bench
        // surface, basically like the side of the bench.
        {
            double width = labBenchSurface.getFullBoundsReference().getWidth() * 0.95;
            double height = 1000; // Arbitrary large number, user should never see the bottom of this.
            Shape benchSupportShape = new Rectangle2D.Double( labBenchSurface.getFullBoundsReference().getCenterX() - width / 2,
                                                              labBenchSurface.getFullBoundsReference().getCenterY(),
                                                              width,
                                                              height );
            PhetPPath labBenchSide = new PhetPPath( benchSupportShape, new Color( 120, 120, 120 ) );
            backLayer.addChild( labBenchSide );
            labBenchSide.moveToBack(); // Must be behind bench top.
        }

        // Calculate the vertical center between the lower edge of the top of
        // the bench and the bottom of the canvas.  This is for layout.
        double centerYBelowSurface = ( STAGE_SIZE.getHeight() + labBenchSurface.getFullBoundsReference().getMaxY() ) / 2;

        // Add the clock controls.
        {
            PNode clockControl = new NormalAndFastForwardTimeControlPanel( normalSimSpeed, model.getClock() );
            clockControl.centerFullBoundsOnPoint( STAGE_SIZE.getWidth() / 2, centerYBelowSurface );
            normalSimSpeed.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean normalSimSpeed ) {
                    ConstantDtClock clock = model.getClock();
                    clock.setDt( normalSimSpeed ? EFACConstants.SIM_TIME_PER_TICK_NORMAL : EFACConstants.SIM_TIME_PER_TICK_FAST_FORWARD );
                }
            } );
            backLayer.addChild( clockControl );
        }

        // Add the reset button.
        {
            ResetAllButtonNode resetButton = new ResetAllButtonNode( this, this, 20, Color.black, new Color( 255, 153, 0 ) );
            resetButton.setConfirmationEnabled( false );
            resetButton.setOffset( STAGE_SIZE.getWidth() - resetButton.getFullBoundsReference().width - 20,
                                   centerYBelowSurface - resetButton.getFullBoundsReference().getHeight() / 2 );
            backLayer.addChild( resetButton );
        }

        // Add the control for showing/hiding energy chunks.
        {
            PNode energyChunkNode = EnergyChunkNode.createEnergyChunkNode( EnergyType.THERMAL );
            energyChunkNode.setScale( 1.1 );
            PropertyCheckBox showEnergyCheckBox = new PropertyCheckBox( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                        EnergyFormsAndChangesResources.Strings.ENERGY_SYMBOLS,
                                                                        model.energyChunksVisible );
            showEnergyCheckBox.setFont( new PhetFont( 20 ) );
            PNode hBox = new HBox( new PSwing( showEnergyCheckBox ) {{ setScale( 1.2 ); }}, energyChunkNode );
            backLayer.addChild( new ControlPanelNode( hBox, EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR ) {{
                setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - EDGE_INSET, EDGE_INSET );
            }} );
        }

        // Add developer control for printing out energy values.
        {
            final TextButtonNode dumpEnergiesButton = new TextButtonNode( "Dump Energies", new PhetFont( 14 ) );
            dumpEnergiesButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.dumpEnergies();
                }
            } );
            dumpEnergiesButton.setOffset( 20, centerYBelowSurface - dumpEnergiesButton.getFullBoundsReference().getHeight() / 2 );
            backLayer.addChild( dumpEnergiesButton );

            // Control the visibility of this button.
            showDumpEnergiesButton.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    dumpEnergiesButton.setVisible( visible );
                }
            } );
        }

        // Add the burners.
        double burnerProjectionAmount = mvt.modelToView( model.getLeftBurner().getOutlineRect() ).getBounds2D().getWidth() * 0.2; // Multiplier empirically determined for best look.
        double burnerWidth = mvt.modelToViewDeltaX( model.getLeftBurner().getOutlineRect().getWidth() ) * 0.7;
        double burnerHeight = burnerWidth * 0.8;
        double burnerOpeningHeight = burnerHeight * 0.2;
        double burnerYPosTweak = -10; // Empirically determined for best look.

        // Set up left heater-cooler node.
        HeaterCoolerView leftHeaterCooler = new HeaterCoolerView( model.getLeftBurner().heatCoolLevel, true, true,
                                                                  EnergyFormsAndChangesResources.Strings.HEAT,
                                                                  EnergyFormsAndChangesResources.Strings.COOL,
                                                                  burnerWidth, burnerHeight, burnerOpeningHeight, true,
                                                                  model.getLeftBurner().energyChunkList, mvt );
        leftHeaterCooler.setOffset( mvt.modelToViewX( model.getLeftBurner().getOutlineRect().getCenterX() ) - leftHeaterCooler.getHoleNode().getFullBounds().getWidth() / 2,
                                    mvt.modelToViewY( model.getLeftBurner().getOutlineRect().getMinY() ) - leftHeaterCooler.getFrontNode().getFullBounds().getHeight() - burnerYPosTweak );
        backLayer.addChild( leftHeaterCooler.getHoleNode() );
        backLayer.addChild( new BurnerStandNode( mvt.modelToView( model.getLeftBurner().getOutlineRect() ).getBounds2D(), burnerProjectionAmount ) );
        heaterCoolerFrontLayer.addChild( leftHeaterCooler.getFrontNode() );

        // Set up right heater-cooler node.
        HeaterCoolerView rightHeaterCooler = new HeaterCoolerView( model.getRightBurner().heatCoolLevel, true, true,
                                                                   EnergyFormsAndChangesResources.Strings.HEAT,
                                                                   EnergyFormsAndChangesResources.Strings.COOL,
                                                                   burnerWidth, burnerHeight, burnerOpeningHeight, true,
                                                                   model.getRightBurner().energyChunkList, mvt );
        rightHeaterCooler.setOffset( mvt.modelToViewX( model.getRightBurner().getOutlineRect().getCenterX() ) - rightHeaterCooler.getHoleNode().getFullBounds().getWidth() / 2,
                                     mvt.modelToViewY( model.getRightBurner().getOutlineRect().getMinY() ) - rightHeaterCooler.getFrontNode().getFullBounds().getHeight() - burnerYPosTweak );
        backLayer.addChild( rightHeaterCooler.getHoleNode() );
        backLayer.addChild( new BurnerStandNode( mvt.modelToView( model.getRightBurner().getOutlineRect() ).getBounds2D(), burnerProjectionAmount ) );
        heaterCoolerFrontLayer.addChild( rightHeaterCooler.getFrontNode() );

        // Add the air.
        airLayer.addChild( new AirNode( model.getAir(), mvt ) );

        // Add the movable objects.
        final BlockNode brickNode = new BlockNode( model, model.getBrick(), mvt );
        brickNode.setApproachingEnergyChunkParentNode( airLayer );
        blockLayer.addChild( brickNode );
        final BlockNode ironBlockNode = new BlockNode( model, model.getIronBlock(), mvt );
        ironBlockNode.setApproachingEnergyChunkParentNode( airLayer );
        blockLayer.addChild( ironBlockNode );
        BeakerView beakerView = new BeakerContainerView( model.getClock(), model, mvt );
        beakerFrontLayer.addChild( beakerView.getFrontNode() );
        beakerBackLayer.addChild( beakerView.getBackNode() );
        beakerGrabLayer.addChild( beakerView.getGrabNode() );

        // Add the thermometer nodes.
        ArrayList<MovableThermometerNode> movableThermometerNodes = new ArrayList();
        for ( final ElementFollowingThermometer thermometer : model.thermometers ) {
            final MovableThermometerNode thermometerNode = new MovableThermometerNode( thermometer, mvt );
            thermometerLayer.addChild( thermometerNode );
            thermometerNode.addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    if ( thermometerNode.getFullBoundsReference().intersects( thermometerToolBox.getFullBoundsReference() ) ) {
                        // Released over tool box, so deactivate.
                        thermometer.active.set( false );
                    }
                }
            } );
            movableThermometerNodes.add( thermometerNode );
        }

        // Add the tool box for the thermometers.
        HBox thermometerBox = new HBox();
        ArrayList<ThermometerToolBoxNode> thermometerToolBoxNodes = new ArrayList<ThermometerToolBoxNode>();
        for ( MovableThermometerNode movableThermometerNode : movableThermometerNodes ) {
            ThermometerToolBoxNode thermometerToolBoxNode = new ThermometerToolBoxNode( movableThermometerNode, mvt, this );
            thermometerBox.addChild( thermometerToolBoxNode );
            thermometerToolBoxNodes.add( thermometerToolBoxNode );
        }
        thermometerToolBox = new ControlPanelNode( thermometerBox, EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );
        thermometerToolBox.setOffset( EDGE_INSET, EDGE_INSET );
        backLayer.addChild( thermometerToolBox );
        for ( ThermometerToolBoxNode thermometerToolBoxNode : thermometerToolBoxNodes ) {
            thermometerToolBoxNode.setReturnRect( thermometerBox.getFullBoundsReference() );
        }

        // Add the control for setting the specific heat of the configurable block.
        // TODO: This was for heat capacity lab prototype, and has been removed
        // as of 8/6/2012.  Remove permanently in roughly a month if we are
        // reasonably sure that it is no longer needed.
//        PNode heatCapacityControlPanel;
//        {
//            HSliderNode heatCapacitySlider = new HSliderNode( EnergyFormsAndChangesSimSharing.UserComponents.heatCapacitySlider,
//                                                              ConfigurableHeatCapacityBlock.MIN_SPECIFIC_HEAT,
//                                                              ConfigurableHeatCapacityBlock.MAX_SPECIFIC_HEAT,
//                                                              model.getConfigurableBlock().specificHeat );
//            heatCapacitySlider.setTrackFillPaint( new GradientPaint( new Point2D.Double( 0, 0 ),
//                                                                     ConfigurableHeatCapacityBlock.HIGH_SPECIFIC_HEAT_COLOR,
//                                                                     new Point2D.Double( 0, VSliderNode.DEFAULT_TRACK_LENGTH ),
//                                                                     ConfigurableHeatCapacityBlock.LOW_SPECIFIC_HEAT_COLOR ) );
//            Font sliderLabelFont = new PhetFont( 16 );
//            heatCapacitySlider.addLabel( ConfigurableHeatCapacityBlock.MIN_SPECIFIC_HEAT, new PhetPText( "low", sliderLabelFont ) );
//            heatCapacitySlider.addLabel( ConfigurableHeatCapacityBlock.MAX_SPECIFIC_HEAT, new PhetPText( "high", sliderLabelFont ) );
//            heatCapacityControlPanel = new ControlPanelNode( new VBox( new HBox( new BlockIconNode( model.getConfigurableBlock().color ),
//                                                                                 new PhetPText( "Heat Capacity", new PhetFont( 18 ) ) ),
//                                                                       heatCapacitySlider ),
//                                                             EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );
//            heatCapacityControlPanel.setOffset( thermometerToolBox.getFullBoundsReference().getMaxX() + EDGE_INSET, EDGE_INSET );
//            backLayer.addChild( heatCapacityControlPanel );
//        }

        // Create an observer that updates the Z-order of the blocks when the
        // user controlled state changes.
        SimpleObserver blockChangeObserver = new SimpleObserver() {
            public void update() {
                if ( model.getIronBlock().isStackedUpon( model.getBrick() ) ) {
                    brickNode.moveToBack();
                }
                else if ( model.getBrick().isStackedUpon( model.getIronBlock() ) ) {
                    ironBlockNode.moveToBack();
                }
                else if ( model.getIronBlock().getRect().getMinX() >= model.getBrick().getRect().getMaxX() ||
                          model.getIronBlock().getRect().getMinY() >= model.getBrick().getRect().getMaxY() ) {
                    ironBlockNode.moveToFront();
                }
                else if ( model.getBrick().getRect().getMinX() >= model.getIronBlock().getRect().getMaxX() ||
                          model.getBrick().getRect().getMinY() >= model.getIronBlock().getRect().getMaxY() ) {
                    brickNode.moveToFront();
                }
            }
        };

        // Create an observer that moves the grab node of the beaker behind
        // the blocks when one or more blocks are in the beaker so that the
        // blocks can be extracted.
        model.getBeaker().fluidLevel.addObserver( new VoidFunction1<Double>() {
            public void apply( Double fluidLevel ) {
                if ( fluidLevel != Beaker.INITIAL_FLUID_LEVEL ) {
                    beakerGrabLayer.moveInBackOf( blockLayer );
                }
                else {
                    beakerGrabLayer.moveInFrontOf( blockLayer );
                }
            }
        } );

        // Update the Z-order of the blocks whenever the "userControlled" state
        // of either changes.
        model.getBrick().position.addObserver( blockChangeObserver );
        model.getIronBlock().position.addObserver( blockChangeObserver );
    }

    public void reset() {
        model.reset();
        normalSimSpeed.reset();
        model.getClock().setPaused( false );
    }
}
