// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.And;
import edu.colorado.phet.common.phetcommon.model.Not;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;

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
    private final double cartoonScale;
    private Property<ImmutableVector2D> property;
    private ArrowNode arrowNode;

    public VectorNode( final Body body, final Property<ModelViewTransform> modelViewTransform, final Property<Boolean> visible,
                       final Property<ImmutableVector2D> property, final double scale, final double cartoonScale, final Color fill, final Color outline ) {
        this.property = property;
        this.body = body;
        this.modelViewTransform = modelViewTransform;
        this.scale = scale;
        this.cartoonScale = cartoonScale;
        new And( visible, new Not( body.getCollidedProperty() ) ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( getValue() );
                }
            } );
        }};
        arrowNode = new ArrowNode( new Point2D.Double(), new Point2D.Double(), 15, 15, 5, 2, true ) {{
            setPaint( fill );
            setStrokePaint( outline );
        }};
        final SimpleObserver updateArrow = new SimpleObserver() {
            public void update() {
                final Point2D tail = getTail();
                arrowNode.setTipAndTailLocations( getTip( tail ), tail );
            }
        };
        property.addObserver( updateArrow );
        body.getScaledPositionProperty().addObserver( updateArrow );
        modelViewTransform.addObserver( updateArrow );
        addChild( arrowNode );
        arrowNode.setPickable( false );
        arrowNode.setChildrenPickable( false );
        body.getScaleProperty().addObserver( updateArrow );
    }

    private Point2D getTail() {
        return modelViewTransform.getValue().modelToView( body.getScaledPositionProperty().getValue().toPoint2D() );
    }

    protected Point2D getTip() {
        return getTip( getTail() );
    }

    private Point2D.Double getTip( Point2D tail ) {
        final Point2D force = modelViewTransform.getValue().modelToViewDelta( property.getValue().getScaledInstance( scale * ( ( body.getScaleProperty().getValue() == Scale.CARTOON ) ? cartoonScale : 1.0 ) ).toPoint2D() );
        return new Point2D.Double( force.getX() + tail.getX(), force.getY() + tail.getY() );
    }
}
