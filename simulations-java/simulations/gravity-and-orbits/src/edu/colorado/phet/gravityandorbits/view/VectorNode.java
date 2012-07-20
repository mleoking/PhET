// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;

/**
 * Draws a vector for a Body, such as a force vector or velocity vector.
 *
 * @author Sam Reid
 */
public class VectorNode extends PNode {
    public static final double FORCE_SCALE = 76.0 / 5.179E15;
    private final Body body;
    private final Property<ModelViewTransform> modelViewTransform;
    private final double scale;
    private Property<Vector2D> vector;
    private ArrowNode arrowNode;

    public VectorNode( final Body body, final Property<ModelViewTransform> modelViewTransform, final BooleanProperty visible,
                       final Property<Vector2D> vector, final double scale, final Color fill, final Color outline ) {
        this.vector = vector;
        this.body = body;
        this.modelViewTransform = modelViewTransform;
        this.scale = scale;
        //Only show if the body hasn't collided
        visible.and( not( body.getCollidedProperty() ) ).addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        arrowNode = new ArrowNode( new Point2D.Double(), new Point2D.Double(), 15, 15, 5, 0.5, true ) {{
            setPaint( fill );
            setStrokePaint( outline );
        }};
        new RichSimpleObserver() {
            public void update() {
                final Point2D tail = getTail();
                arrowNode.setTipAndTailLocations( getTip( tail ), tail );
            }
        }.observe( vector, body.getPositionProperty(), modelViewTransform );
        addChild( arrowNode );
        arrowNode.setPickable( false );
        arrowNode.setChildrenPickable( false );
    }

    private Point2D getTail() {
        return modelViewTransform.get().modelToView( body.getPositionProperty().get().toPoint2D() );
    }

    protected Point2D getTip() {
        return getTip( getTail() );
    }

    private Point2D.Double getTip( Point2D tail ) {
        int minArrowLength = 10;
        Vector2D force = new Vector2D( modelViewTransform.get().modelToViewDelta( vector.get().getScaledInstance( scale ).toPoint2D() ) );
        if ( force.getMagnitude() < minArrowLength && force.getMagnitude() > 1E-12 ) {
            force = force.getInstanceOfMagnitude( minArrowLength );
        }
        return new Point2D.Double( force.getX() + tail.getX(), force.getY() + tail.getY() );
    }
}
