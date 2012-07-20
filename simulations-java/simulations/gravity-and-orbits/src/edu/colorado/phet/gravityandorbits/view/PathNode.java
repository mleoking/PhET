// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Shows the "trail" left behind by a Body as it moves over time, which disappears after about 2 orbits
 * The end of the tail is faded out, which is why we didn't simply use a PPath
 * This is named "Path" instead of "trail" since that is how it is supposed to appear to the students.
 *
 * @author Sam Reid
 */
public class PathNode extends PNode {
    private final PNode pathNode;
    private ArrayList<Vector2D> points = new ArrayList<Vector2D>();//points in view space
    public final int STROKE_WIDTH = 3;

    public PathNode( final Body body, final Property<ModelViewTransform> transform, final Property<Boolean> visible, final Color color ) {
        final int numFadePoints = 25;
        final BasicStroke stroke = new BasicStroke( STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

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

        addChild( pathNode );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.get() );
                body.clearPath();
                points.clear();
                pathNode.repaint();
            }
        } );

        //Update when the Body path changes
        final Body.PathListener listener = new Body.PathListener() {
            public void pointAdded( Vector2D point ) {
                Vector2D pt = transform.get().modelToView( point );
                points.add( pt );
                if ( getVisible() ) {
                    pathNode.setBounds( getBounds( points ) );
                    pathNode.repaint();
                }

            }

            public void pointRemoved() {
                if ( points.size() > 0 ) {
                    points.remove( 0 );
                }
                if ( getVisible() ) {
                    pathNode.setBounds( getBounds( points ) );
                    pathNode.repaint();
                }
            }

            public void cleared() {
                points.clear();
                pathNode.setBounds( getBounds( points ) );
                pathNode.repaint();
            }
        };
        body.addPathListener( listener );
        transform.addObserver( new SimpleObserver() {
            public void update() {
                body.clearPath();
            }
        } );
    }

    //Compute the bounds that contains the path, for repainting
    private Rectangle2D getBounds( ArrayList<Vector2D> points ) {
        if ( points.size() == 0 ) {
            return new Rectangle();
        }
        else {
//            long start = System.currentTimeMillis();
            Rectangle2D rect = new Rectangle2D.Double( points.get( 0 ).getX(), points.get( 0 ).getY(), 0, 0 );
            for ( Vector2D point : points ) {
                rect.add( point.getX(), point.getY() );
            }
//            long end = System.currentTimeMillis();
//            long elapsed = end - start;
//            System.out.println( "elapsed = " + elapsed );
            return RectangleUtils.expand( rect, STROKE_WIDTH, STROKE_WIDTH );
        }
    }
}
