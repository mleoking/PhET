// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.view;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;

/**
 * TopTickMarkRulerNode draws a ruler without showing the bottom tick marks,
 * because we wanted to put a label such as "meters" on the bottom instead.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class TopTickMarkRulerNode extends RulerNode {

    public TopTickMarkRulerNode( double distanceBetweenFirstAndLastTick, double height, String[] majorTickLabels, String units, int numMinorTicksBetweenMajors, int fontSize ) {
        super( distanceBetweenFirstAndLastTick, height, majorTickLabels, units, numMinorTicksBetweenMajors, fontSize );
    }

    //Does not draw tick marks on the bottom of the ruler
    @Override protected DoubleGeneralPath createTickMark( double xPosition, double rulerHeight, double tickHeight ) {
        DoubleGeneralPath tickPath = new DoubleGeneralPath( xPosition, 0 );
        tickPath.lineTo( xPosition, tickHeight );
        return tickPath;
    }
}