// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Belt;
import edu.umd.cs.piccolo.PNode;

/**
 * @author John Blanco
 */
public class BeltNode extends PNode {

    public BeltNode( Belt belt, final ModelViewTransform mvt ) {
        addChild( new PhetPPath( new Rectangle2D.Double( -5, -5, 10, 10 ), Color.PINK ) );

        // Update the overall offset based on the model position.
        belt.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }
}
