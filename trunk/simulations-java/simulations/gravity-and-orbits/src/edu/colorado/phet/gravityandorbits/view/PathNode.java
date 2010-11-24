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
    private boolean started = false;
    private final Body body;
    private final ModelViewTransform2D transform;
    private final Property<Boolean> visible;

    public PathNode( final Body body, final ModelViewTransform2D transform, final Property<Boolean> visible ) {
        this.body = body;
        this.transform = transform;
        this.visible = visible;
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
                updatePath();
            }

            public void pointRemoved( Body.PathPoint point ) {
                updatePath();
            }

            public void cleared() {
                updatePath();
            }
        } );
    }

    private void updatePath() {
        reset();

        if ( !visible.getValue() ) {
            return;
        }
        for ( int i = 0; i < body.getPath().size(); i++ ) {
            Body.PathPoint point = body.getPath().get( i );
            Point2D viewPoint = transform.modelToViewDouble( point.point.toPoint2D() );
            if ( point.userControlled || !started ) {
                phetPPath.moveTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
            }
            else {
                phetPPath.lineTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
            }
            started = true;
        }
    }

    public void reset() {
        phetPPath.reset();
        started = false;
    }
}
