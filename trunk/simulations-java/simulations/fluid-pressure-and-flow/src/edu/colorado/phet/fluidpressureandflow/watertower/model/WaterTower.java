// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Model for the water tower and the water it contains.
 *
 * @author Sam Reid
 */
public class WaterTower {

    //Layout parameters for the water tower
    //Don't start the tank at the maximum height so the user can drag it up and down
    public static final int MAX_Y = 18;
    public static final int INITIAL_Y = 15;
    public static final double TANK_RADIUS = 5;
    public static final double PANEL_OFFSET = TANK_RADIUS + 0.25;
    public static final double TANK_HEIGHT = 10;
    private static final int LEG_EXTENSION = 3;

    //Assume the tank is a cylinder ond compute the max volume
    public static final double TANK_VOLUME = Math.PI * TANK_RADIUS * TANK_RADIUS * TANK_HEIGHT;

    //Location of the bottom center of the water tower
    public final Property<Vector2D> tankBottomCenter = new Property<Vector2D>( new Vector2D( 0, INITIAL_Y ) );

    //Start the tank partly full so that the "fill" button and faucet slider are initially enabled
    public final DoubleProperty fluidVolume = new DoubleProperty( TANK_VOLUME * 0.8 );//meters cubed

    //The movable panel that can cover the hole.
    public final Property<Vector2D> panelOffset = new Property<Vector2D>( new Vector2D( PANEL_OFFSET, 0 ) );

    //Flag indicating whether the tank is full, for purposes of disabling controls that can be used to fill the tank
    public final ObservableProperty<Boolean> full = fluidVolume.greaterThanOrEqualTo( TANK_VOLUME );

    //Size of the hole in meters
    public static final double HOLE_SIZE = 1;

    //Function to fill up the water tank
    public final VoidFunction0 fill = new VoidFunction0() {
        public void apply() {
            setFluidVolume( TANK_VOLUME );
        }
    };

    public Rectangle2D.Double getTankShape() {
        return new Rectangle2D.Double( tankBottomCenter.get().getX() - TANK_RADIUS, tankBottomCenter.get().getY(), TANK_RADIUS * 2, TANK_HEIGHT );
    }

    public Point2D getTankTopCenter() {
        return new Point2D.Double( tankBottomCenter.get().getX(), tankBottomCenter.get().getY() + TANK_HEIGHT );
    }

    //Get the shape of the legs
    public Shape getSupportShape() {
        final Point2D bottomCenter = tankBottomCenter.get().toPoint2D();
        final Point2D.Double leftLegTop = new Point2D.Double( bottomCenter.getX() - TANK_RADIUS / 2, bottomCenter.getY() );
        final Point2D.Double leftLegBottom = new Point2D.Double( bottomCenter.getX() - TANK_RADIUS / 2 - LEG_EXTENSION, 0 );
        final Point2D.Double rightLegTop = new Point2D.Double( bottomCenter.getX() + TANK_RADIUS / 2, bottomCenter.getY() );
        final Point2D.Double rightLegBottom = new Point2D.Double( bottomCenter.getX() + TANK_RADIUS / 2 + LEG_EXTENSION, 0 );

        //These functions allow us to map a fraction of the distance up one of the water tower legs to a point on the leg, for adding crossbeams
        final Function.LinearFunction yMap = new Function.LinearFunction( 0, 1, 0, bottomCenter.getY() );
        final Function.LinearFunction xMapLeft = new Function.LinearFunction( 0, 1, bottomCenter.getX() - TANK_RADIUS / 2 - LEG_EXTENSION, bottomCenter.getX() - TANK_RADIUS / 2 );
        final Function.LinearFunction xMapRight = new Function.LinearFunction( 0, 1, bottomCenter.getX() + TANK_RADIUS / 2 + LEG_EXTENSION, bottomCenter.getX() + TANK_RADIUS / 2 );

        DoubleGeneralPath path = new DoubleGeneralPath();
        addLine( path, leftLegTop, leftLegBottom );
        addLine( path, rightLegTop, rightLegBottom );
        addStruts( path, yMap, xMapLeft, xMapRight, 0.1, 0.4 );
        addStruts( path, yMap, xMapLeft, xMapRight, 0.5, 0.8 );
        return new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( path.getGeneralPath() );
    }

    //Add the struts to the water tower support path
    private void addStruts( DoubleGeneralPath path, Function.LinearFunction yMap, Function.LinearFunction xMapLeft, Function.LinearFunction xMapRight, double leftFraction, double rightFraction ) {
        addStrut( path, yMap, xMapLeft, xMapRight, leftFraction, rightFraction );
        addStrut( path, yMap, xMapLeft, xMapRight, rightFraction, leftFraction );
    }

    private void addStrut( DoubleGeneralPath path, Function.LinearFunction yMap, Function.LinearFunction xMapLeft, Function.LinearFunction xMapRight, double leftFraction, double rightFraction ) {
        addLine( path, new Point2D.Double( xMapLeft.evaluate( leftFraction ), yMap.evaluate( leftFraction ) ), new Point2D.Double( xMapRight.evaluate( rightFraction ), yMap.evaluate( rightFraction ) ) );
    }

    private void addLine( DoubleGeneralPath path, Point2D.Double a, Point2D.Double b ) {
        path.moveTo( a );
        path.lineTo( b );
    }

    //Gets the shape that the water should occupy
    public Shape getWaterShape() {
        return new Rectangle2D.Double( tankBottomCenter.get().getX() - TANK_RADIUS, tankBottomCenter.get().getY(), TANK_RADIUS * 2, getWaterLevel() );
    }

    //Get the height of the water
    public double getWaterLevel() {
        return fluidVolume.get() / Math.PI / TANK_RADIUS / TANK_RADIUS;
    }

    //Get the location where water can flow out of the hole in the water tower
    public Point2D getHoleLocation() {
        return new Point2D.Double( tankBottomCenter.get().getX() + TANK_RADIUS + 0.55 / 2, tankBottomCenter.get().getY() - 0.15 );
    }

    public boolean isHoleOpen() {
        return panelOffset.get().getY() > 0;
    }

    public void setFluidVolume( double v ) {
        fluidVolume.set( v );
    }

    public void reset() {
        tankBottomCenter.reset();
        fluidVolume.reset();
        panelOffset.reset();
    }
}