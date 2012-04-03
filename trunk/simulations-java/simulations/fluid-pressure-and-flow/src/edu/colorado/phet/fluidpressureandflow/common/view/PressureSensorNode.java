// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
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
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.*;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.QUESTION_MARK;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.VALUE_WITH_UNITS_PATTERN;
import static java.text.MessageFormat.format;

/**
 * User-draggable node that displays the pressure at its location
 *
 * @author Sam Reid
 */
public class PressureSensorNode extends SensorNode {
    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Property<UnitSet> units,

                               //the area to constrain the node within or null if no constraints
                               final Property<IPool> pool,

                               final Function0<ImmutableRectangle2D> visibleModelRect ) {
        super( transform, sensor, getPressureUnit( units ) );

        addChild( new PNode() {{

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

            //make its hot spot be its opening which is on its center left
            translate( 0, -getFullBounds().getHeight() / 2 );
        }} );
        addInputEventListener( new RelativeDragHandler( this, transform, sensor.location, new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D point2D ) {
                Point2D pt = point2D;
                //not allowed to go to negative Potential Energy in the pool
                if ( pool != null ) {
                    pt = pool.get().clampSensorPosition( pt );
                }

                //Return the closest point within the visible model bounds, so it can't be dragged off-screen
                return visibleModelRect.apply().getClosestPoint( pt );
            }
        } ) );
    }

    //Use higher precision in the air since the pressure changes much more slowly there
    @Override public String getDisplayString( Unit unit, double v ) {
        String pattern = VALUE_WITH_UNITS_PATTERN;
        String value = QUESTION_MARK;
        if ( !Double.isNaN( v ) ) {
            final DecimalFormat format = sensor.location.get().getY() > 0 ? new DecimalFormat( "0.0000" ) : new DecimalFormat( "0.00" );
            value = format.format( unit.siToUnit( v ) );
        }
        return format( pattern, value, unit.getAbbreviation() );
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