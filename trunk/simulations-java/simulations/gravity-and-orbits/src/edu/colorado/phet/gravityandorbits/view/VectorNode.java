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
    public static final double FORCE_SCALE = 100.0 / 5.179E13;
    public static final double VELOCITY_SCALE = 100/4.466E-5;

    public VectorNode( final Body body, final ModelViewTransform2D modelViewTransform2D, final Property<Boolean> visible, final Property<ImmutableVector2D> property, final double scale ) {
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        property.addObserver( new SimpleObserver() {
            public void update() {
                removeAllChildren();
                final Point2D tail = modelViewTransform2D.modelToViewDouble( body.getPosition() );
                final Point2D force = modelViewTransform2D.modelToViewDifferentialDouble( property.getValue().getScaledInstance( scale ).toPoint2D() );
                final ArrowNode child = new ArrowNode( tail, new Point2D.Double( force.getX() + tail.getX(), force.getY() + tail.getY() ), 10, 10, 5, 2, true ) {{
                    setPaint( Color.pink );
                    setStrokePaint( Color.red );
                }};
                addChild( child );
            }
        } );
    }
}
