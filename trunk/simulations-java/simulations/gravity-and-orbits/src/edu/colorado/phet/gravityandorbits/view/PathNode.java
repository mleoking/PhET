package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PathNode extends PNode {
    private final PhetPPath phetPPath;
    private boolean hasPath = false;

    public PathNode( final Body body, final ModelViewTransform2D transform, final Property<Boolean> visible ) {
        phetPPath = new PhetPPath( new BasicStroke( 3 ), body.getColor() );
        addChild( phetPPath );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
                if ( visible.getValue() ) {
                    reset();
                }
            }
        } );
        body.addPathListener( new Body.PathListener() {
            public void pointAdded( Body.PathPoint point ) {
                if ( !visible.getValue() ) {
                    return;
                }
                Point2D viewPoint = transform.modelToViewDouble( point.point.toPoint2D() );
                if ( point.userControlled || !hasPath ) {
                    phetPPath.moveTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
                }
                else {
                    phetPPath.lineTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
                }
                hasPath = true;
            }

            public void cleared() {
                reset();
            }
        } );
    }

    public void reset() {
        phetPPath.reset();
        hasPath = false;
    }
}
