// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PointSensor;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerSensorNode;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;

/**
 * @author Sam Reid
 */
public class DensitySensorNode2D extends SpeedometerSensorNode {

    // TODO: change this to a 2D offset
    public final double horizontalSensorOffset;

    /**
     * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
     *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
     *                     model-view-transform size changes.
     */
    public DensitySensorNode2D( float kmToViewUnit ) {
        super( ModelViewTransform.createIdentity(), new PointSensor<Double>( 0, 0 ), "Density (kg/m^3)" );

        // scale it so that we achieve adherence to the model scale
        scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

        horizontalSensorOffset = getFullBounds().getWidth() / 2;

        // give it the "Hand" cursor
        addInputEventListener( new LWJGLCursorHandler() );
    }
}
