// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureControlPanel;
import edu.colorado.phet.fluidpressureandflow.view.*;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas {
    private static final double modelHeight = 50;
    private static final double scale = STAGE_SIZE.getHeight() / modelHeight;
    private PNode waterDropLayer = new PNode();

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( module, ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.25, STAGE_SIZE.height * 0.75 ), scale ) );

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
            addChild( new PressureSensorNode( transform, pressureSensor, module.getFluidPressureAndFlowModel().pressureUnit ) );
        }

        addVelocitySensorNodes( module.getFluidPressureAndFlowModel() );

        //TODO: this is duplicated in FluidFlowCanvas
        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new WaterTowerControlPanel<WaterTowerModel>( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureControlPanel.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        addChild( new FluidDensityControl<WaterTowerModel>( module ) {{
            setOffset( 0, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, true, module.getFluidPressureAndFlowModel() );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, true, module.getFluidPressureAndFlowModel() );
        synchronizeRulerLocations( meterStick, englishRuler );

        addChild( meterStick );
        addChild( englishRuler );

        addChild( new FPAFMeasuringTape( transform, module.measuringTapeVisible, module.getFluidPressureAndFlowModel().distanceUnit ) );

        Property<Boolean> moduleActive = new Property<Boolean>( false ) {{
            module.addListener( new Module.Listener() {
                public void activated() {
                    setValue( true );
                }

                public void deactivated() {
                    setValue( false );
                }
            } );
        }};

        Property<Boolean> clockRunning = new Property<Boolean>( true );
        //wire up the clock to be running if the module is active and if the clock control button has been pressed
        new And( clockRunning, moduleActive ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    module.getFluidPressureAndFlowModel().getClock().setRunning( getValue() );
                }
            } );
        }};
        addChild( new FloatingClockControlNode( clockRunning, null, module.getClock(), FPAFStrings.RESET, new Property<Color>( Color.white ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
    }
}
