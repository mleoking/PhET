// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.GroundNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.common.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterDrop;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterTowerModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.BACKGROUND;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.FOREGROUND;

/**
 * Canvas for the water tower tab.
 *
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas<WaterTowerModel> {
    private static final double MODEL_HEIGHT = 50;
    private static final double SCALE = STAGE_SIZE.getHeight() / MODEL_HEIGHT;

    //Use different layers for the water tower drops vs faucet drops since water tower drops must go behind the ground, but faucet drops must go in front of the tower
    private final PNode waterTowerDropLayer = new PNode();
    private final PNode faucetDropLayer = new PNode();

    private final FPAFMeasuringTape measuringTape;
    final static Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    //Font size to use for "reset all" button and "fill" button
    public static final int FLOATING_BUTTON_FONT_SIZE = (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 );

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.225, STAGE_SIZE.height * 0.75 ), SCALE ), new ImmutableVector2D( 20, 20 ) );

        addChild( new SkyNode( transform, new Rectangle2D.Double( -1000, 0, 2000, 2000 ), 20 ) );

        //Show the water drops behind the ground so it looks like they are soaked up in the ground
        addChild( waterTowerDropLayer );

        //Show the ground before the hose will go in front
        addChild( new GroundNode( transform, new Rectangle2D.Double( -1000, -2000, 2000, 2000 ), 5 ) );

        //Show the optional hose that takes the water from the water tower
        HoseNode hoseNode = new HoseNode( transform, module.model.hose );
        addChild( hoseNode );

        //Show the water tower node, should be after the hose so that the red door will go in front of the hose
        addChild( new WaterTowerNode( transform, module.model.waterTower, module.model.liquidDensity ) );
        addChild( faucetDropLayer );

        //Add a button that will fill the tank
        addChild( new FillTankButton( module.model.waterTower.full, module.model.waterTower.fill, module.model.waterTower.panelOffset ) {{
            module.model.waterTower.tankBottomCenter.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D bottomCenter ) {

                    //Show the button to the left center of the tank, and move when the tank moves
                    final Point2D leftCenterOfWaterTower = transform.modelToView( module.model.waterTower.getTankShape().getX(),
                                                                                  bottomCenter.getY() + module.model.waterTower.getTankShape().getHeight() / 2 );
                    setOffset( leftCenterOfWaterTower.getX() - getFullBounds().getWidth() - INSET, leftCenterOfWaterTower.getY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );

        //Add the faucet
        addChild( new FPAFFaucetNode( UserComponents.waterTowerFaucet, module.model.faucetFlowRate, new Not( module.model.waterTower.full ) ) );

        module.model.addWaterTowerDropAddedListener( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop waterDrop ) {
                waterTowerDropLayer.addChild( new WaterDropNode( transform, waterDrop, module.model.liquidDensity ) {{
                    final WaterDropNode waterDropNode = this;
                    waterDrop.addRemovalListener( new SimpleObserver() {
                        public void update() {
                            waterTowerDropLayer.removeChild( waterDropNode );
                        }
                    } );
                }} );
            }
        } );

        module.model.addFaucetDropAddedListener( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop waterDrop ) {
                faucetDropLayer.addChild( new WaterDropNode( transform, waterDrop, module.model.liquidDensity ) {{
                    final WaterDropNode waterDropNode = this;
                    waterDrop.addRemovalListener( new SimpleObserver() {
                        public void update() {
                            faucetDropLayer.removeChild( waterDropNode );
                        }
                    } );
                }} );
            }
        } );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new WaterTowerControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, FLOATING_BUTTON_FONT_SIZE, FOREGROUND, BACKGROUND ) {{
            setConfirmationEnabled( false );
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + INSET * 2 );
        }} );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        measuringTape = new FPAFMeasuringTape( transform, module.measuringTapeVisible, module.model.units );
        addChild( measuringTape );

        //Add the floating clock controls and sim speed slider at the bottom of the screen
        addClockControls( module );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor pressureSensor : module.model.getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.model.units, null, visibleModelBounds ) );
        }

        //Add the sensor toolbox node, which also adds the velocity and pressure sensors
        //Doing this last ensures that the draggable sensors will appear in front of everything else
        addSensorToolboxNode( module.model, controlPanelNode, null );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, true, module.model, false );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, true, module.model, false );
        synchronizeRulerLocations( meterStick, englishRuler );

        addChild( meterStick );
        addChild( englishRuler );
    }

    //Additionally reset the measuring tape since not reset elsewhere
    public void reset() {
        measuringTape.reset();
    }
}