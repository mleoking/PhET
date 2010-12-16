package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.And;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;
import edu.colorado.phet.fluidpressureandflow.view.*;

/**
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas {
    private static final double modelHeight = 50;
    private static final double scale = STAGE_SIZE.getHeight() / modelHeight;

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( module, ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.25, STAGE_SIZE.height * 0.75 ), scale ) );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );

        addChild( new WaterTowerNode( transform, module.getFluidPressureAndFlowModel().getWaterTower() ) );

        module.getFluidPressureAndFlowModel().addDropAddedListener( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop waterDrop ) {
                addChild( new WaterDropNode( transform, waterDrop ) {{
                    final WaterDropNode waterDropNode = this;
                    waterDrop.addRemovalListener( new SimpleObserver() {
                        public void update() {
                            WaterTowerCanvas.this.removeChild( waterDropNode );
                        }
                    } );
                }} );
            }
        } );

        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) );
        }

        for ( VelocitySensor velocitySensor : module.getFluidPressureAndFlowModel().getVelocitySensors() ) {
            addChild( new VelocitySensorNode( transform, velocitySensor, module.getFluidPressureAndFlowModel().getVelocityUnitProperty() ) );
        }

        //TODO: this is duplicated in FluidFlowCanvas
        // Control Panel
        final ControlPanel controlPanelNode = new ControlPanel( new FluidFlowControlPanel<WaterTowerModel>( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetButton( module ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        addChild( new FluidDensityControl<WaterTowerModel>( module ) {{
            setOffset( 0, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        addChild( new MeterStick( transform, module.getMeterStickVisibleProperty(), module.getRulerVisibleProperty(), rulerModelOrigin ) );
        addChild( new EnglishRuler( transform, module.getYardStickVisibleProperty(), module.getRulerVisibleProperty(), rulerModelOrigin ) );

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
        addChild( new FloatingClockControlNode( clockRunning, new Function1<Double, String>() {
            public String apply( Double time ) {
//                return (int) ( time / 1.00 ) + " sec";
                return "";
            }
        }, module.getClock() ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
    }
}
