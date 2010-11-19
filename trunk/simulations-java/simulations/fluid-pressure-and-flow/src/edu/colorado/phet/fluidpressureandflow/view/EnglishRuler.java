package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class EnglishRuler extends FluidPressureAndFlowRuler {
    public EnglishRuler( ModelViewTransform transform, final Property<Boolean> visible, Point2D.Double rulerModelOrigin ) {
        super( transform, visible, Math.abs( transform.modelToViewDeltaY( Units.FEET.toSI( 10 ) ) ), new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }, "ft", rulerModelOrigin );
    }
}
