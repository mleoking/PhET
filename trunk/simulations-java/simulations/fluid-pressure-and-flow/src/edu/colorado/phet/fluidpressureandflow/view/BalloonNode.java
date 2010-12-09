package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Balloon;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class BalloonNode extends PNode {

    /**
     * @param transform
     * @param sensor
     * @param unitsProperty
     */
    public BalloonNode( final ModelViewTransform transform, final Balloon sensor, final Property<Units.Unit> unitsProperty, final Pool pool ) {
        addInputEventListener( new CursorHandler() );
        addChild( new PhetPPath( Color.blue, new BasicStroke( 1 ), Color.lightGray ) {{
            final SimpleObserver update = new SimpleObserver() {
                public void update() {
                    double radius = transform.modelToViewDeltaX( sensor.getRadius() );
//                    System.out.println( "radius = " + radius );
                    setPathTo( new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 ) );
                }
            };
            sensor.addValueObserver( update );
            unitsProperty.addObserver( update );
        }} );
        sensor.addLocationObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
        addInputEventListener( new RelativeDragHandler( this, transform, sensor.getLocationProperty(), new Function1<Point2D, Point2D>() {
            //TODO: Factor pool to subclass or general constraint method
            public Point2D apply( Point2D point2D ) {
                if ( pool != null ) {
                    final Point2D.Double pt = new Point2D.Double( point2D.getX(), Math.max( point2D.getY(), pool.getMinY() ) );
                    if ( pt.getY() < 0 ) {
                        pt.setLocation( MathUtil.clamp( pool.getMinX(), pt.getX(), pool.getMaxX() ), pt.getY() );
                    }
                    return pt;//not allowed to go to negative Potential Energy
                }
                else { return point2D; }
            }
        } ) );
        sensor.addLocationObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
    }
}
