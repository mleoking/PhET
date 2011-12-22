// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class ThermometerNode2D extends LiquidExpansionThermometerNode {

    public final double sensorVerticalOffset;

    /**
     * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
     *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
     *                     model-view-transform size changes.
     */
    public ThermometerNode2D( float kmToViewUnit ) {
        super( new PDimension( 50 * 0.8, 150 * 0.8 ) );

        // add in an arrow to show where we are sensing the temperature
        final double sensorHeight = getFullBounds().getHeight() - getBulbDiameter() / 2;
        addChild( new PhetPPath( new DoubleGeneralPath( -8, sensorHeight ) {{
            lineToRelative( 5, 5 );
            lineToRelative( 0, -10 );
            lineToRelative( -5, 5 );
        }}.getGeneralPath(), Color.RED, new BasicStroke( 1 ), Color.BLACK ) );

        // scale it so that we achieve adherence to the model scale
        scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

        sensorVerticalOffset = getBulbDiameter() / 2 * getScale();

        // give it the "Hand" cursor
        addInputEventListener( new LWJGLCursorHandler() );
    }
}
