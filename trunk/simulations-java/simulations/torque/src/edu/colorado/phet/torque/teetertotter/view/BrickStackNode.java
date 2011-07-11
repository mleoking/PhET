// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.torque.teetertotter.model.weights.ShapeWeight;
import edu.umd.cs.piccolo.PNode;

/**
 * A node that represents a brick in the view.
 *
 * @author John Blanco
 */
public class BrickStackNode extends PNode {
    public BrickStackNode( final ModelViewTransform mvt, final ShapeWeight weight ) {
        addChild( new PhetPPath( new Color( 205, 38, 38 ), new BasicStroke( 1 ), Color.BLACK ) {{
            weight.shapeProperty.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WeightDragHandler( weight, this, mvt ) );
    }
}
