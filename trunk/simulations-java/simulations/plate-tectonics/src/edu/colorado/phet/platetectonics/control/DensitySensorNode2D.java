// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PointSensor;
import edu.colorado.phet.common.piccolophet.nodes.SensorNode;
import edu.colorado.phet.jmephet.JMECursorHandler;

/**
 * @author Sam Reid
 */
public class DensitySensorNode2D extends SensorNode<Double> {

    /**
     * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
     *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
     *                     model-view-transform size changes.
     */
    public DensitySensorNode2D( float kmToViewUnit ) {
        super( ModelViewTransform.createIdentity(), new PointSensor<Double>( 0, 0 ), new Property<Function1<Double, String>>( new Function1<Double, String>() {
            public String apply( Double aDouble ) {
                return new DecimalFormat( "0.00" ).format( aDouble );
            }
        } ), "Density" );

        // scale it so that we achieve adherence to the model scale
        scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

        // give it the "Hand" cursor
        addInputEventListener( new JMECursorHandler() );
    }
}
