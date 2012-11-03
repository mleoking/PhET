// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxWithIcon;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Thermometer;
import edu.colorado.phet.energyformsandchanges.common.view.BeakerView;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerNode;
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
    private final ThermometerToolBox thermometerToolBox;
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
        PNode blockLayer = new PNode();
        rootNode.addChild( blockLayer );
        PNode airLayer = new PNode();
        rootNode.addChild( airLayer );
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

        // Add the clock controls. TODO: i18n
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

        // Add the control for showing/hiding object energy. TODO: i18n
        {
            PropertyCheckBoxWithIcon showEnergyCheckBox = new PropertyCheckBoxWithIcon( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                                        "Energy Symbols",
                                                                                        new PhetFont( 20 ),
                                                                                        EnergyFormsAndChangesResources.Images.ENERGY_CHUNKS_WHITE_SEMIBOLD,
                                                                                        model.energyChunksVisible );
            backLayer.addChild( new ControlPanelNode( new PSwing( showEnergyCheckBox ), EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR ) {{
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
        backLayer.addChild( new BurnerNode( model.getLeftBurner(), mvt ) );
        backLayer.addChild( new BurnerNode( model.getRightBurner(), mvt ) );

        // Add the air.
        airLayer.addChild( new AirNode( model.getAir(), mvt ) );

        // Add the movable objects.
        final BlockNode brickNode = new BlockNode( model, model.getBrick(), mvt );
        brickNode.setApproachingEnergyChunkParentNode( airLayer );
        blockLayer.addChild( brickNode );
        final BlockNode ironBlockNode = new BlockNode( model, model.getIronBlock(), mvt );
        ironBlockNode.setApproachingEnergyChunkParentNode( airLayer );
        blockLayer.addChild( ironBlockNode );
        BeakerView beakerView = new BeakerContainerView( model, mvt );
        beakerFrontLayer.addChild( beakerView.getFrontNode() );
        backLayer.addChild( beakerView.getBackNode() );

        // Add the tool box for the thermometers.
        thermometerToolBox = new ThermometerToolBox( model, mvt, EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );
        thermometerToolBox.setOffset( EDGE_INSET, EDGE_INSET );
        backLayer.addChild( thermometerToolBox );

        // Add the thermometers.
        for ( ElementFollowingThermometer thermometer : model.thermometers ) {
            thermometerToolBox.putThermometerInOpenSpot( thermometer );
            // Add one thermometer node to the front layer and one to the back,
            // and control the visibility based on whether the thermometer is
            // in the tool box.
            final ThermometerNode frontThermometerNode = new ThermometerNode( thermometer, mvt );
            thermometerLayer.addChild( frontThermometerNode );
            final ThermometerNode backThermometerNode = new ThermometerNode( thermometer, mvt );
            backLayer.addChild( backThermometerNode );
            frontThermometerNode.addInputEventListener( new PBasicInputEventHandler() {

                @Override public void mouseReleased( PInputEvent event ) {
                    if ( frontThermometerNode.getFullBoundsReference().intersects( thermometerToolBox.getFullBoundsReference() ) ) {
                        // Released over tool box, so put the thermometer into it.
                        thermometerToolBox.putThermometerInOpenSpot( frontThermometerNode.getThermometer() );
                    }
                }
            } );

            // Monitor the thermometer's position and move it to the back of
            // the z-order when over the tool box.
            thermometer.position.addObserver( new VoidFunction1<Vector2D>() {
                public void apply( Vector2D position ) {
                    if ( mvt.viewToModel( thermometerToolBox.getFullBoundsReference() ).contains( position.toPoint2D() ) && frontThermometerNode.getTransparency() > 0 ) {
                        frontThermometerNode.setTransparency( 0 );
                    }
                    else if ( !mvt.viewToModel( thermometerToolBox.getFullBoundsReference() ).contains( position.toPoint2D() ) && frontThermometerNode.getTransparency() == 0 ) {
                        frontThermometerNode.setTransparency( 1 );
                    }
                }
            } );
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

        // Update the Z-order of the blocks whenever the "userControlled" state
        // of either changes.
        model.getBrick().position.addObserver( blockChangeObserver );
        model.getIronBlock().position.addObserver( blockChangeObserver );
    }

    public void reset() {
        model.reset();
        normalSimSpeed.reset();
        // Put the thermometers in the tool box.
        for ( ElementFollowingThermometer thermometer : model.thermometers ) {
            thermometerToolBox.putThermometerInOpenSpot( thermometer );
        }
    }

    // Class that defines the thermometer tool box.
    private static class ThermometerToolBox extends PNode {

        private static final int NUM_THERMOMETERS_SUPPORTED = EFACIntroModel.NUM_THERMOMETERS;

        private final EFACIntroModel model;
        private final ModelViewTransform mvt;
        private final double thermometerHeight;

        private ThermometerToolBox( EFACIntroModel model, ModelViewTransform mvt, Color backgroundColor ) {
            this.model = model;
            this.mvt = mvt;
            thermometerHeight = EnergyFormsAndChangesResources.Images.THERMOMETER_MEDIUM_BACK.getHeight( null );
            double thermometerWidth = EnergyFormsAndChangesResources.Images.THERMOMETER_MEDIUM_BACK.getWidth( null );
            PhetPPath thermometerRegion = new PhetPPath( new Rectangle2D.Double( 0, 0, thermometerWidth * ( NUM_THERMOMETERS_SUPPORTED + 2 ), thermometerHeight * 1.1 ), new Color( 0, 0, 0, 0 ) );
            addChild( new ControlPanelNode( new VBox( 0, thermometerRegion ), backgroundColor ) );
        }

        public void putThermometerInOpenSpot( Thermometer thermometer ) {
            // This is a little tweaky due to the relationship between the
            // thermometer in the model and the view representation.
            double xPos = 35;
            double yPos = getFullBoundsReference().getMaxY() - 40;
            boolean openLocationFound = false;
            for ( int i = 0; i < NUM_THERMOMETERS_SUPPORTED && !openLocationFound; i++ ) {
                xPos = getFullBoundsReference().width / NUM_THERMOMETERS_SUPPORTED * i + 15;
                openLocationFound = true;
                for ( ElementFollowingThermometer modelThermometer : model.thermometers ) {
                    if ( modelThermometer.position.get().distance( new Vector2D( mvt.viewToModel( xPos, yPos ) ) ) < 1E-3 ) {
                        openLocationFound = false;
                        break;
                    }
                }
            }
            thermometer.position.set( new Vector2D( mvt.viewToModel( xPos, yPos ) ) );
        }
    }
}
