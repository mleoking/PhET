// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;

/**
 * This class represents a ruler that sits on the bottom of the plank and
 * rotates as the plank rotates.
 *
 * @author John Blanco
 */
public class RotatingRulerNode extends RulerNode {

    public RotatingRulerNode( Plank plank, ModelViewTransform mvt ) {
        super( mvt.modelToViewDeltaX( Plank.LENGTH - 0.5 ),
               50,
               new String[] { "2", "1.75", "1.5", "1.25", "1", "0.75", "0.5", "0.25", "0", "0.25", "0.5", "0.75", "1", "1.25", "1.5", "1.75", "2" },
               "m",
               0,
               12 );
        centerFullBoundsOnPoint( mvt.modelToViewX( plank.getBalancePoint().getX() ),
                                 mvt.modelToViewY( plank.getBalancePoint().getY() ) + getFullBoundsReference().height / 2 );
    }
}
