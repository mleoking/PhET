package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Sam Reid
 */
public class PathNode extends PNode {
    private final PNode pathNode;
    private ArrayList<Point> points = new ArrayList<Point>();//points in view space
    public static final int MAX_TRACE_LENGTH = (int) ( 365 / GravityAndOrbitsDefaults.NUMBER_DAYS_PER_TICK * 2 );//enough for 2 earth years
    private int[] xPrimitive = new int[MAX_TRACE_LENGTH];
    private int[] yPrimitive = new int[MAX_TRACE_LENGTH];

    public PathNode( final Body body, final Property<ModelViewTransform> transform, final Property<Boolean> visible, final Color color, final Property<Scale> scaleProperty ) {
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
                body.clearPath();
                points.clear();
                pathNode.repaint();
            }
        } );
        final Body.PathListener listener = new Body.PathListener() {
            public void pointAdded( Body.PathPoint point ) {
                ImmutableVector2D pt = transform.getValue().modelToView( scaleProperty.getValue() == Scale.CARTOON && point.cartoonPoint != null ? point.cartoonPoint : point.point );
                points.add( new Point( (int) pt.getX(), (int) pt.getY() ) );
                pathNode.repaint();
            }

            public void pointRemoved() {
                if ( points.size() > 0 ) {
                    points.remove( 0 );
                }
                pathNode.repaint();
            }

            public void cleared() {
                points.clear();
                pathNode.repaint();
            }
        };
        body.addPathListener( listener );
        transform.addObserver( new SimpleObserver() {
            public void update() {
                body.clearPath();
            }
        } );
        scaleProperty.addObserver( new SimpleObserver() {
            public void update() {
                //clear and add back all points in the right scale
                points.clear();
                for ( Body.PathPoint pathPoint : body.getPath() ) {
                    listener.pointAdded( pathPoint );
                }
            }
        } );
    }
}
