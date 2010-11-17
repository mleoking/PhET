package edu.colorado.phet.fluidpressureandflow.view;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.Pool;

/**
 * @author Sam Reid
 */
public class MeterStick extends FluidPressureAndFlowRuler {
    public MeterStick( ModelViewTransform2D transform, Pool pool, final Property<Boolean> visible ) {
        super( transform, pool, visible, Math.abs( transform.modelToViewDifferentialYDouble( 5 ) ), new String[] { "0", "1", "2", "3", "4", "5" }, "m" );
    }
}
