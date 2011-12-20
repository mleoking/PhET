// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class ThermometerNode2D extends LiquidExpansionThermometerNode {

    /**
     * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
     *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
     *                     model-view-transform size changes.
     */
    public ThermometerNode2D( float kmToViewUnit ) {
        super( new PDimension( 50 * 0.8, 150 * 0.8 ) );

        // scale it so that we achieve adherence to the model scale
        scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

        // give it the "Hand" cursor
        addInputEventListener( new LWJGLCursorHandler() );
    }
}
