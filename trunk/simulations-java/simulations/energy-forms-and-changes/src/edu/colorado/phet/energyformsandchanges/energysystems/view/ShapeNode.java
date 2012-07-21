// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.energysystems.model.ShapeModelElement;
import edu.umd.cs.piccolo.PNode;

/**
 * TODO: Temp for prototyping, should be gone eventually.
 *
 * @author John Blanco
 */
public class ShapeNode extends PNode {
    public ShapeNode( ShapeModelElement shapeModelElement, final ModelViewTransform mvt ) {
        Shape transformedShape = mvt.modelToView( shapeModelElement.getShape() );
        AffineTransform offsettingTransform = AffineTransform.getTranslateInstance( -mvt.modelToViewX( shapeModelElement.getShape().getBounds2D().getCenterX() ),
                                                                                    -mvt.modelToViewY( shapeModelElement.getShape().getBounds2D().getCenterY() ) );
        Shape untranslatedShape = offsettingTransform.createTransformedShape( transformedShape );
        addChild( new PhetPPath( untranslatedShape, shapeModelElement.getColor(), new BasicStroke( 2 ), Color.BLACK ) );

        shapeModelElement.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D newPosition ) {
                setOffset( mvt.modelToView( newPosition ).toPoint2D() );
            }
        } );
    }
}
