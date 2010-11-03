package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
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
public class TraceNode extends PNode {
    private final PhetPPath phetPPath;
    private Body body;

    public TraceNode( final Body body, final ModelViewTransform2D transform, final Property<Boolean> visible ) {
        this.body = body;
        phetPPath = new PhetPPath( new BasicStroke( 2 ), body.getColor() );
        addChild( phetPPath );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                final Body.TracePoint[] trace = body.getTrace();
                if ( trace.length == 0 ) {
                    clearPath();
                }
                else {
                    Body.TracePoint lastTrace = trace[trace.length - 1];
                    Point2D viewPoint = transform.modelToViewDouble( lastTrace.point.toPoint2D() );
                    if ( lastTrace.userControlled || trace.length == 1 ) {
                        phetPPath.moveTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
                    }
                    else {
                        phetPPath.lineTo( (float) viewPoint.getX(), (float) viewPoint.getY() );
                    }
                }
            }
        } );
    }

    private void clearPath() {
        phetPPath.reset();
    }
}
