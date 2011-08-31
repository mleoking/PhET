// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.ThreeImageNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.*;

/**
 * User-draggable node that displays the pressure at its location
 *
 * @author Sam Reid
 */
public class PressureSensorNode extends SensorNode<Double> {
    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Property<UnitSet> units, Function0<ImmutableRectangle2D> visibleModelRect ) {
        this( transform, sensor, units, null, visibleModelRect );
    }

    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Property<UnitSet> units,

                               //the area to constrain the node within or null if no constraints
                               final Pool pool,

                               final Function0<ImmutableRectangle2D> visibleModelRect ) {
        super( transform, sensor, getPressureUnit( units ) );

        addChild( new PNode() {{

            //make its hot spot be its opening which is on its center left
            translate( 0, -getFullBounds().getHeight() / 2 );

            final ThreeImageNode imageNode = new ThreeImageNode( PRESSURE_METER_LEFT, PRESSURE_METER_CENTER, PRESSURE_METER_RIGHT );
            addChild( imageNode );

            final PText textNode = new PText( text.get() ) {{
                setFont( new PhetFont( 20, true ) );
            }};
            addChild( textNode );

            //Layout based on the size of the text string
            text.addObserver( new SimpleObserver() {
                public void update() {
                    //Update the text itself.
                    textNode.setText( text.get() );

                    imageNode.setCenterWidth( textNode.getFullBounds().getWidth() );

                    //Position the text node just to the right of the leftPatch and centered vertically.
                    textNode.setOffset( imageNode.leftPatch.getFullBounds().getMaxX(), imageNode.centerPatch.getFullBounds().getHeight() / 2 - textNode.getFullBounds().getHeight() / 2 );
                }
            } );
        }} );
        addInputEventListener( new RelativeDragHandler( this, transform, sensor.location, new Function1<Point2D, Point2D>() {
            //TODO: Factor pool to subclass or general constraint method
            public Point2D apply( Point2D point2D ) {

                Point2D pt = point2D;

                //not allowed to go to negative Potential Energy in the pool
                if ( pool != null ) {
                    pt = new Point2D.Double( point2D.getX(), Math.max( point2D.getY(), pool.getMinY() ) );
                    if ( pt.getY() < 0 ) {
                        pt.setLocation( MathUtil.clamp( pool.getMinX(), pt.getX(), pool.getMaxX() ), pt.getY() );
                    }
                }
                return visibleModelRect.apply().getClosestPoint( pt );
            }
        } ) );
    }

    //Gets a property corresponding to the Pressure unit in a UnitSet, consider creating a class UnitSetProperty and moving this method there
    private static Property<Unit> getPressureUnit( final Property<UnitSet> units ) {
        return new Property<Unit>( units.get().pressure ) {{
            units.addObserver( new VoidFunction1<UnitSet>() {
                public void apply( UnitSet unitSet ) {
                    set( unitSet.pressure );
                }
            } );
        }};
    }
}