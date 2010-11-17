package edu.colorado.phet.fluidpressureandflow.view;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class EnglishRuler extends FluidPressureAndFlowRuler {
    public EnglishRuler( ModelViewTransform2D transform, Pool pool, final Property<Boolean> visible ) {
        super( transform, pool, visible, Math.abs( transform.modelToViewDifferentialYDouble( Units.FEET.toSI( 10 ) ) ), new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }, "ft" );
    }
}
