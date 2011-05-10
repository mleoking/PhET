// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.*;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.view.FluidPressureCanvas;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.view.FluidPressureControlPanel;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterDrop;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas {
    private static final double modelHeight = 50;
    private static final double scale = STAGE_SIZE.getHeight() / modelHeight;
    private PNode waterDropLayer = new PNode();
    private final FPAFMeasuringTape measuringTape;

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.25, STAGE_SIZE.height * 0.75 ), scale ), module.getFluidPressureAndFlowModel().visibleModelBounds );

        addChild( new SkyNode( transform ) );
        addChild( new WaterTowerNode( transform, module.getFluidPressureAndFlowModel().getWaterTower(), module.getFluidPressureAndFlowModel().liquidDensity ) );
        addChild( waterDropLayer );
        addChild( new GroundNode( transform ) );
        addChild( new FaucetNode( transform, module.getFluidPressureAndFlowModel().getFaucetFlowLevel() ) );

        module.getFluidPressureAndFlowModel().addDropAddedListener( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop waterDrop ) {
                waterDropLayer.addChild( new WaterDropNode( transform, waterDrop, module.getFluidPressureAndFlowModel().liquidDensity ) {{
                    final WaterDropNode waterDropNode = this;
                    waterDrop.addRemovalListener( new SimpleObserver() {
                        public void update() {
                            waterDropLayer.removeChild( waterDropNode );
                        }
                    } );
                }} );
            }
        } );

        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.getFluidPressureAndFlowModel().units ) );
        }

        addVelocitySensorNodes( module.getFluidPressureAndFlowModel() );

        //TODO: this is duplicated in FluidFlowCanvas
        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new WaterTowerControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, true, module.getFluidPressureAndFlowModel() );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, true, module.getFluidPressureAndFlowModel() );
        synchronizeRulerLocations( meterStick, englishRuler );

        addChild( meterStick );
        addChild( englishRuler );

        measuringTape = new FPAFMeasuringTape( transform, module.measuringTapeVisible, module.getFluidPressureAndFlowModel().units );
        addChild( measuringTape );

        Property<Boolean> moduleActive = new Property<Boolean>( false ) {{
            module.addListener( new Module.Listener() {
                public void activated() {
                    set( true );
                }

                public void deactivated() {
                    set( false );
                }
            } );
        }};

        Property<Boolean> clockRunning = new Property<Boolean>( true );
        //wire up the clock to be running if the module is active and if the clock control button has been pressed
        new And( clockRunning, moduleActive ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    module.getFluidPressureAndFlowModel().getClock().setRunning( get() );
                }
            } );
        }};

        //Add clock controls (play/pause), including a time speed slider (no time readout)
        addChild( createClockControls( module, new Property<Boolean>( true ) ) );
    }

    //Additionally reset the measuring tape since not reset elsewhere
    public void reset() {
        measuringTape.reset();
    }
}
