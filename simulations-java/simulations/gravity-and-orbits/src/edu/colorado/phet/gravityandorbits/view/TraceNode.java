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
        this.body=body;
        phetPPath = new PhetPPath( new BasicStroke( 2 ), body.getColor() );
        addChild( phetPPath );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                body.clearTrace();
                clearPath();
                setVisible( visible.getValue() );
            }
        } );
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                final Point2D[] trace = body.getTrace();
                final Point2D[] txTrace = new Point2D[trace.length];
                for ( int i = 0; i < txTrace.length; i++ ) {
                    txTrace[i] = transform.modelToViewDouble( trace[i] );
                }
                if ( trace.length == 0 ) {
                    clearPath();
                }
                else {
                    phetPPath.setPathToPolyline( txTrace );
                }
            }
        } );
    }

    private void clearPath() {
        phetPPath.setPathTo( new Ellipse2D.Double( body.getPosition().getX(), body.getPosition().getY(), 0, 0 ) );
    }
}
