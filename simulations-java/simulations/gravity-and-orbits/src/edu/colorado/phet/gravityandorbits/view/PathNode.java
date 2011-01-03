// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Shows the "trail" left behind by a Body as it moves over time, which disappears after about 2 orbits.
 *
 * @author Sam Reid
 */
public class PathNode extends PNode {
    private final PNode pathNode;
    private ArrayList<Point> points = new ArrayList<Point>();//points in view space
    private int[] xPrimitive;
    private int[] yPrimitive;

    public PathNode( final Body body, final Property<ModelViewTransform> transform, final Property<Boolean> visible, final Color color, final Property<Scale> scaleProperty ) {
        xPrimitive = new int[body.getMaxPathLength()];
        yPrimitive = new int[body.getMaxPathLength()];

        final int numFadePoints = 25;
        final BasicStroke stroke = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

        //The part that draws the path
        pathNode = new PNode() {
            @Override
            protected void paint( PPaintContext paintContext ) {

                //Draw the solid part
                final Graphics2D g2 = paintContext.getGraphics();
                g2.setPaint( color );
                g2.setStroke( stroke );
                int numSolidPoints = Math.min( body.getMaxPathLength() - numFadePoints, points.size() );
                int numTransparentPoints = points.size() - numSolidPoints;
                for ( int i = 0; i < numSolidPoints; i++ ) {
                    final Point point = points.get( points.size() - 1 - i );
                    xPrimitive[i] = point.x;
                    yPrimitive[i] = point.y;
                }
                g2.drawPolyline( xPrimitive, yPrimitive, numSolidPoints );
                g2.setStroke( new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ) );

                //Draw the faded out part
                Color faded = color;
                for ( int i = 0; i < numTransparentPoints; i++ ) {
                    final int a = (int) ( faded.getAlpha() - 255.0 / numFadePoints );
                    faded = new Color( faded.getRed(), faded.getGreen(), faded.getBlue(), Math.max( 0, a ) );
                    g2.setColor( faded );
                    g2.drawLine( points.get( numTransparentPoints - i ).x, points.get( numTransparentPoints - i ).y, points.get( numTransparentPoints - i + 1 ).x, points.get( numTransparentPoints - i + 1 ).y );
                }
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
