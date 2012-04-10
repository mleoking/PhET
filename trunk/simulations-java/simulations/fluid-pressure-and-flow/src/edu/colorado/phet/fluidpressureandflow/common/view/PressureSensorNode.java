// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PointSensor;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerSensorNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.ComponentTypes;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.ParameterKeys.pressure;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static java.text.MessageFormat.format;

/**
 * User-draggable node that displays the pressure at its location
 *
 * @author Sam Reid
 */
public class PressureSensorNode extends SensorNode {
    private final Property<IPool> pool;

    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Property<UnitSet> units,

                               //the area to constrain the node within or null if no constraints
                               final Property<IPool> pool,

                               final Function0<ImmutableRectangle2D> visibleModelRect ) {
        super( transform, sensor, getPressureUnit( units ) );
        this.pool = pool;

        final PointSensor<Double> pointSensor = new PointSensor<Double>( 0, 0 );

        //Show the speedometer sensor and make top-center be 1.0 atm for the pressure sensor
        final ZeroOffsetNode speedometerNode = new ZeroOffsetNode( new SpeedometerSensorNode( transform, pointSensor, PRESSURE, Units.ATMOSPHERE.toSI( 1.0 ) * 2 ) ) {{

            //make the hot spot at the bottom center
            translate( -getFullWidth() / 2, -getFullBounds().getHeight() );
        }};
        addChild( speedometerNode );

        final PText textNode = new PText( text.get() ) {{
            setFont( new PhetFont( 16, true ) );
        }};

        final PhetPPath textOutline = new PhetPPath( Color.white, new BasicStroke( 1 ), Color.lightGray );
        addChild( textOutline );
        addChild( textNode );

        sensor.addValueObserver( new SimpleObserver() {
            @Override public void update() {
                final Double v = sensor.getValue();
                Option<Double> value = Double.isNaN( v ) ? new None<Double>() : new Some<Double>( v );
                pointSensor.value.set( value );
            }
        } );

        //Layout based on the size of the text string
        text.addObserver( new SimpleObserver() {
            public void update() {
                //Update the text itself.
                textNode.setText( text.get() );
                textNode.centerBoundsOnPoint( speedometerNode.getCenterX(), speedometerNode.getCenterY() + 30 );
                textOutline.setPathTo( RectangleUtils.expand( textNode.getFullBounds(), 2, 0 ) );
            }
        } );

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
        } ) {
            @Override protected void sendMessage( final Point2D modelPoint ) {
                super.sendMessage( modelPoint );
                SimSharingManager.sendUserMessage( sensor.userComponent, ComponentTypes.pressureSensor, UserActions.drag,
                                                   parameterSet( ParameterKeys.x, modelPoint.getX() ).
                                                           with( ParameterKeys.y, modelPoint.getY() ).
                                                           with( pressure, sensor.context.getPressure( modelPoint.getX(), modelPoint.getY() ) ) );
            }
        } );
    }

    //Use higher precision in the air since the pressure changes much more slowly there
    @Override public String getDisplayString( Unit unit, double v ) {
        String pattern = VALUE_WITH_UNITS_PATTERN;
        String value = QUESTION_MARK;
        if ( !Double.isNaN( v ) ) {

            //For pascals, don’t show any decimal places because it already has enough precision
            final DecimalFormat format = unit == Units.PASCAL ? new DecimalFormat( "0" ) :
                                         pool != null && pool.get().isAbbreviatedUnits( sensor.location.get(), v ) ? new DecimalFormat( "0.00" ) :
                                         new DecimalFormat( "0.0000" );
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