package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;
import edu.colorado.phet.fluidpressureandflow.view.*;

/**
 * @author Sam Reid
 */
public class WaterTowerCanvas extends FluidPressureAndFlowCanvas {
    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 3.2;
    private static final double pipeCenterY = -2;
    private static final double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();

    public WaterTowerCanvas( final WaterTowerModule module ) {
        super( module, new ModelViewTransform2D( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2 + pipeCenterY + 0.75, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true ) );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );

        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, null, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) );
        }

        for ( VelocitySensor velocitySensor : module.getFluidPressureAndFlowModel().getVelocitySensors() ) {
            addChild( new VelocitySensorNode( transform, velocitySensor ) );
        }

    }
}
