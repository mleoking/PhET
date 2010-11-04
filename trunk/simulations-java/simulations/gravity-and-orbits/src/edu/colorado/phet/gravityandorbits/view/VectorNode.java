package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class VectorNode extends PNode {
    public static final double FORCE_SCALE = 76.0 / 5.179E15;
    public static final double VELOCITY_SCALE = 200 / 4.466E-5;
    private final Body body;
    private final ModelViewTransform2D modelViewTransform2D;
    private final double scale;
    private Property<ImmutableVector2D> property;
    private ArrowNode arrowNode;

    public VectorNode( final Body body, final ModelViewTransform2D modelViewTransform2D, final Property<Boolean> visible,
                       final Property<ImmutableVector2D> property, final double scale, final Color fill, final Color outline ) {
        this.property = property;
        this.body = body;
        this.modelViewTransform2D = modelViewTransform2D;
        this.scale = scale;
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
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
        body.getPositionProperty().addObserver( updateArrow );
        addChild( arrowNode );
        arrowNode.setPickable( false );
        arrowNode.setChildrenPickable( false );
    }

    private Point2D getTail() {
        return modelViewTransform2D.modelToViewDouble( body.getPosition() );
    }

    protected Point2D getTip() {
        return getTip( getTail() );
    }

    private Point2D.Double getTip( Point2D tail ) {
        final Point2D force = modelViewTransform2D.modelToViewDifferentialDouble( property.getValue().getScaledInstance( scale ).toPoint2D() );
        return new Point2D.Double( force.getX() + tail.getX(), force.getY() + tail.getY() );
    }
}
