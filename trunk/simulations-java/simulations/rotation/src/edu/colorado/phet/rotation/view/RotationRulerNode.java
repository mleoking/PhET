package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.umd.cs.piccolo.event.PDragEventHandler;

import java.awt.*;

/**
 * Author: Sam Reid
 * Jul 11, 2007, 4:06:05 PM
 */
public class RotationRulerNode extends RulerNode {
    public RotationRulerNode( double distanceBetweenFirstAndLastTick, double height, String[] majorTickLabels, String units, int numMinorTicksBetweenMajors, int fontSize ) {
        super( distanceBetweenFirstAndLastTick, height, majorTickLabels, units, numMinorTicksBetweenMajors, fontSize );
        setBackgroundPaint( new Color( 236, 225, 113, 150 ) );

        addInputEventListener( new CursorHandler( OTConstants.UP_DOWN_CURSOR ) );
//        addInputEventListener( new BoundedDragHandler( this, null ) );
        addInputEventListener( new PDragEventHandler() );
    }
}
