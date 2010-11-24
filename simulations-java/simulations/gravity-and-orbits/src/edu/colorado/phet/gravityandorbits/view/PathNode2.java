package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Sam Reid
 */
public class PathNode2 extends PNode {
    private final PNode pathNode;
    private final Body body;
    private final ModelViewTransform2D transform;
    private final Property<Boolean> visible;
    private ArrayList<Point> points = new ArrayList<Point>();//points in view space
    private int MAX_TRACE_LENGTH = 2000;
    private int[] xPrimitive = new int[MAX_TRACE_LENGTH];
    private int[] yPrimitive = new int[MAX_TRACE_LENGTH];

    public PathNode2( final Body body, final ModelViewTransform2D transform, final Property<Boolean> visible, final Color color ) {
        this.body = body;
        this.transform = transform;
        this.visible = visible;
        final BasicStroke stroke = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        pathNode = new PNode() {
            @Override
            protected void paint( PPaintContext paintContext ) {
                final Graphics2D g2 = paintContext.getGraphics();

                g2.setPaint( color );
                g2.setStroke( stroke );
                for ( int i = 0; i < points.size(); i++ ) {
                    final Point point = points.get( i );
                    xPrimitive[i] = point.x;
                    yPrimitive[i] = point.y;
                }
                g2.drawPolyline( xPrimitive, yPrimitive, points.size() );
            }
        };
        //Paints the whole screen at every update, but we can skip bounds computations for this node
        pathNode.setBounds( new Rectangle2D.Double( -10000, -10000, 20000, 20000 ) );
        addChild( pathNode );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        body.addPathListener( new Body.PathListener() {
            public void pointAdded( Body.PathPoint point ) {
                Point2D pt = transform.modelToViewDouble( point.point.toPoint2D() );
                points.add( new Point( (int) pt.getX(), (int) pt.getY() ) );
                pathNode.repaint();
            }

            public void pointRemoved() {
                points.remove( 0 );
                pathNode.repaint();
            }

            public void cleared() {
                points.clear();
                pathNode.repaint();
            }
        } );
    }

    @Override
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
    }

}
