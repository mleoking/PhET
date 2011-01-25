// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Author: Sam Reid
 * Jul 11, 2007, 4:06:05 PM
 */
public class RotationRulerNode extends RulerNode {
    public RotationRulerNode( double distanceBetweenFirstAndLastTick, double height, String[] majorTickLabels, String units, int numMinorTicksBetweenMajors, int fontSize ) {
        super( distanceBetweenFirstAndLastTick, 14 * 3.0 / 200.0, height, majorTickLabels, new PhetFont( 14 ), units, new PhetFont( 14 ), numMinorTicksBetweenMajors, height * 0.4 / 2, height * 0.2 / 2 );
        setBackgroundPaint( new Color( 236, 225, 113, 150 ) );
        setBackgroundStroke( new BasicStroke( (float) RotationPlayAreaNode.SCALE ) );
        setTickStroke( new BasicStroke( (float) RotationPlayAreaNode.SCALE ) );
        setUnitsSpacing( 3 * RotationPlayAreaNode.SCALE );
        setFontScale( RotationPlayAreaNode.SCALE );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width,
                           -delta.height );//Y-axis is inverted
            }
        } );
    }
}
