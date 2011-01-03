// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
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
    private ArrayList<ImmutableVector2D> points = new ArrayList<ImmutableVector2D>();//points in view space

    public PathNode( final Body body, final Property<ModelViewTransform> transform, final Property<Boolean> visible, final Color color, final Property<Scale> scaleProperty ) {
        final int numFadePoints = 25;
        final BasicStroke stroke = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

        //The part that draws the path
        pathNode = new PNode() {
            @Override
            protected void paint( PPaintContext paintContext ) {

                //Prepare the graphics
                final Graphics2D g2 = paintContext.getGraphics();
                g2.setPaint( color );
                g2.setStroke( stroke );
                int numSolidPoints = Math.min( body.getMaxPathLength() - numFadePoints, points.size() );
                int numTransparentPoints = points.size() - numSolidPoints;

                //Create and render the solid part as a path.  New points are added at the tail of the list, so easiest to render backwards for fade-out.
                GeneralPath path = new GeneralPath();
                if ( points.size() > 0 ) {
                    path.moveTo( (float) points.get( points.size() - 1 ).getX(), (float) points.get( points.size() - 1 ).getY() );
                }
                for ( int i = points.size() - 2; i >= numTransparentPoints; i-- ) {
                    path.lineTo( (float) points.get( i ).getX(), (float) points.get( i ).getY() );
                }
                g2.draw( path );

                //Draw the faded out part
                g2.setStroke( new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ) );
                Color faded = color;
                for ( int i = numTransparentPoints - 1; i >= 0; i-- ) {
                    final int a = (int) ( faded.getAlpha() - 255.0 / numFadePoints );//fade out a little bit each segment
                    faded = new Color( faded.getRed(), faded.getGreen(), faded.getBlue(), Math.max( 0, a ) );
                    g2.setColor( faded );
                    g2.drawLine( (int) points.get( i + 1 ).getX(), (int) points.get( i + 1 ).getY(), (int) points.get( i ).getX(), (int) points.get( i ).getY() );
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
                points.add( pt );
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
