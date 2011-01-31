// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * @author Sam Reid
 */
public class MeterStick extends FluidPressureAndFlowRuler {
    public MeterStick( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( 5 ) ), new String[] { "0", "1", "2", "3", "4", "5" }, "m", rulerModelOrigin );
    }

    public MeterStick( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin, boolean flag ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( 30 ) ), new String[] { "0", "5", "10", "15", "20", "25", "30" }, "m", rulerModelOrigin );
    }
}
