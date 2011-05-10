// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author Sam Reid
 */
public class WaterTower {
    public static final int MAX_Y = 18;
    public static double TANK_RADIUS = 5;
    public static double PANEL_OFFSET = TANK_RADIUS + 0.25;
    public static double TANK_HEIGHT = 10;
    private static final int LEG_EXTENSION = 3;
    public static double tankVolume = Math.PI * TANK_RADIUS * TANK_RADIUS * TANK_HEIGHT;
    public final Property<ImmutableVector2D> tankBottomCenter = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, MAX_Y ) );
    public final Property<Double> fluidVolume = new Property<Double>( tankVolume );//meters cubed
    public final Property<ImmutableVector2D> panelOffset = new Property<ImmutableVector2D>( new ImmutableVector2D( PANEL_OFFSET, 0 ) );//The movable panel that can cover the hole.

    public Rectangle2D.Double getTankShape() {
        return new Rectangle2D.Double( tankBottomCenter.get().getX() - TANK_RADIUS, tankBottomCenter.get().getY(), TANK_RADIUS * 2, TANK_HEIGHT );
    }

    public Point2D getTankTopCenter() {
        return new Point2D.Double( tankBottomCenter.get().getX(), tankBottomCenter.get().getY() + TANK_HEIGHT );
    }

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

    public Shape getWaterShape() {
        return new Rectangle2D.Double( tankBottomCenter.get().getX() - TANK_RADIUS, tankBottomCenter.get().getY(), TANK_RADIUS * 2, getWaterLevel() );
    }

    public double getWaterLevel() {
        return fluidVolume.get() / Math.PI / TANK_RADIUS / TANK_RADIUS;
    }

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

    public boolean isFull() {
        return fluidVolume.get() >= tankVolume;
    }
}
