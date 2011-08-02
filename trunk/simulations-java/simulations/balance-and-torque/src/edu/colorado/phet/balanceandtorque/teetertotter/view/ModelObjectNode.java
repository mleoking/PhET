// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;

import edu.colorado.phet.balanceandtorque.teetertotter.model.ShapeModelElement;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for displaying and interacting with model objects.  Uses the
 * shape of the object, which will change as the object moves.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class ModelObjectNode extends PNode {

    private PhetPPath modelShapeNode;

    /**
     * Constructor.
     *
     * @param mvt
     * @param modelObject
     * @param paint
     */
    public ModelObjectNode( final ModelViewTransform mvt, final ShapeModelElement modelObject, Paint paint ) {
        modelShapeNode = new PhetPPath( paint, new BasicStroke( 1, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_SQUARE ), Color.BLACK ) {{
            modelObject.getShapeProperty().addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }};
        addChild( modelShapeNode );
    }

    /**
     * Change the initial paint value.  Useful when rotating model objects
     * that were created with a gradient paint.
     *
     * @param paint
     */
    public void setPaint( Paint paint ) {
        modelShapeNode.setPaint( paint );
    }
}
