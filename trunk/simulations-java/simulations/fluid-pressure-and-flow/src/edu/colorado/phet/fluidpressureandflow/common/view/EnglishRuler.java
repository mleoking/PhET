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
public class EnglishRuler extends FluidPressureAndFlowRuler {
    public EnglishRuler( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin, ResetModel resetModel ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( Units.FEET.toSI( 10 ) ) ), new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }, Units.FEET.getAbbreviation(), rulerModelOrigin, resetModel );
    }

    public EnglishRuler( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin, boolean flag, ResetModel resetModel ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( Units.FEET.toSI( 100 ) ) ), new String[] { "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" }, Units.FEET.getAbbreviation(), rulerModelOrigin, resetModel );
    }
}
