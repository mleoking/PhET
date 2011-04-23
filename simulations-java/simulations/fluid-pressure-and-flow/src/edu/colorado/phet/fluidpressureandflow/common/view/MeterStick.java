// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.common.model.Units;

/**
 * @author Sam Reid
 */
public class MeterStick extends FluidPressureAndFlowRuler {
    public MeterStick( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin, ResetModel resetModel ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( 5 ) ), new String[] { "0", "1", "2", "3", "4", "5" }, Units.METERS.getAbbreviation(), rulerModelOrigin, resetModel );
    }

    public MeterStick( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin, boolean flag, ResetModel resetModel ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( 30 ) ), new String[] { "0", "5", "10", "15", "20", "25", "30" }, Units.METERS.getAbbreviation(), rulerModelOrigin, resetModel );
    }
}
