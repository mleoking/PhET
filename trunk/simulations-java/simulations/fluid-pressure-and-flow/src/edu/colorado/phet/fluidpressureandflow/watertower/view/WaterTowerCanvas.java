// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.GroundNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.common.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterDrop;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterTowerModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;

/**
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas<WaterTowerModel> {
    private static final double modelHeight = 50;
    private static final double scale = STAGE_SIZE.getHeight() / modelHeight;
    private final PNode waterDropLayer = new PNode();
    private final FPAFMeasuringTape measuringTape;
    final static Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.25, STAGE_SIZE.height * 0.75 ), scale ), module.model.visibleModelBounds );

        addChild( new SkyNode( transform, new Rectangle2D.Double( -1000, 0, 2000, 2000 ), 20 ) );

        addChild( new WaterTowerNode( transform, module.model.getWaterTower(), module.model.liquidDensity ) );
        addChild( waterDropLayer );

        //Show the ground in front of the water tower so we don't have to clip the legs of the water tower--this will make them look flat
        addChild( new GroundNode( transform, new Rectangle2D.Double( -1000, -2000, 2000, 2000 ), 5 ) );

        //Show the optional hose that takes the water from the water tower
        HoseNode hoseNode = new HoseNode( transform, module.model.hose );
        addChild( hoseNode );

        //Add the faucet
        addChild( new FPAFFaucetNode( module.model.getFaucetFlowRate() ) );

        module.model.addDropAddedListener( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop waterDrop ) {
                waterDropLayer.addChild( new WaterDropNode( transform, waterDrop, module.model.liquidDensity ) {{
                    final WaterDropNode waterDropNode = this;
                    waterDrop.addRemovalListener( new SimpleObserver() {
                        public void update() {
                            waterDropLayer.removeChild( waterDropNode );
                        }
                    } );
                }} );
            }
        } );

        //TODO: this is duplicated in FluidFlowCanvas
        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new WaterTowerControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, true, module.model );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, true, module.model );
        synchronizeRulerLocations( meterStick, englishRuler );

        addChild( meterStick );
        addChild( englishRuler );

        measuringTape = new FPAFMeasuringTape( transform, module.measuringTapeVisible, module.model.units );
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
                    module.model.getClock().setRunning( get() );
                }
            } );
        }};

        //Add clock controls (play/pause), including a time speed slider (no time readout)
        addChild( createClockControls( module, new Property<Boolean>( true ) ) );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor pressureSensor : module.model.getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.model.units ) );
        }

        addVelocitySensorNodes( module.model );
    }

    //Additionally reset the measuring tape since not reset elsewhere
    public void reset() {
        measuringTape.reset();
    }
}