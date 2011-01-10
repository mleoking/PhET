// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.ThreePatchImageNode;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class PressureSensorNode extends SensorNode<Double> {

    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Property<Units.Unit> unitsProperty ) {
        this( transform, sensor, unitsProperty, null );
    }

    /**
     * @param transform
     * @param sensor
     * @param unitsProperty
     * @param pool          the area to constrain the node within or null if no constraints//TODO: redesign so this is not a problem
     */
    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Property<Units.Unit> unitsProperty, final Pool pool ) {
        super( transform, sensor, unitsProperty );

        final Property<String> text = new Property<String>( getText.apply() );
        addChild( new ThreePatchImageNode( text ) {{
            translate( 0, -getFullBounds().getHeight() / 2 );//make its hot spot be its opening which is on its center left
        }} );
        final SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                text.setValue( getText.apply() );
            }
        };
        textNode.setVisible( false );
        backgroundNode.setVisible( false );
        sensor.addValueObserver( updateText );
        unitsProperty.addObserver( updateText );
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
    }
}
