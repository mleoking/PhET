// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Stroke;

import edu.colorado.phet.balanceandtorque.teetertotter.model.PositionedVector;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that takes depicts a vector that has an origin as well as x and y
 * components, and that monitors the vector and updates the representation
 * when changes occur.
 *
 * @author John Blanco
 */
public class PositionedVectorNode extends PNode {
    private static final double SCALE_FACTOR = 0.002; // Arbitrary scaling factor to make vectors a reasonable size.
    private static final Stroke ARROW_STROKE = new BasicStroke( 2 );

    public PositionedVectorNode( final Property<PositionedVector> positionedVectorProperty, BooleanProperty visibilityProperty, final ModelViewTransform mvt ) {
        // Create the vector node and add it as a child.
        final Vector2DNode vectorNode = new Vector2DNode( 0, 0, 1, SCALE_FACTOR ) {{
            setHeadSize( 15, 10 ); // Head size is arbitrary based on what looked good.
        }};
        addChild( vectorNode );
        // Listen to the vector and update the node when changes occur.
        positionedVectorProperty.addObserver( new VoidFunction1<PositionedVector>() {
            public void apply( PositionedVector positionedVector ) {
                vectorNode.setOffset( mvt.modelToView( positionedVectorProperty.get().origin.toPoint2D() ) );
                vectorNode.setVector( new Vector2D( mvt.modelToView( positionedVectorProperty.get().vector ) ) );
            }
        } );
        // Set up visibility control.
        visibilityProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

    }
}
