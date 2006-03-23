/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 11:11:38 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class RotationGlyph extends PNode {
    private PPath surface;
    private double angle;
    private double primaryHeight = 100;
    private double primaryWidth = 100;
    private double expansionWidth = 50;
    private PPath depth;

    public RotationGlyph() {
        surface = new PPath();
        surface.setPaint( new Color( 128, 128, 255 ) );
        surface.setStroke( new BasicStroke( 3 ) );

        depth = new PPath();
        depth.setPaint( Color.blue );
        depth.setStroke( new BasicStroke( 4 ) );
        addChild( depth );
        addChild( surface );

        update();
        setRotation( 0.0 );
    }

    public double getPrimaryHeight() {
        return primaryHeight;
    }

    public void setPrimaryHeight( double primaryHeight ) {
        this.primaryHeight = primaryHeight;
        update();
    }

    public double getPrimaryWidth() {
        return primaryWidth;
    }

    public void setPrimaryWidth( double primaryWidth ) {
        this.primaryWidth = primaryWidth;
        update();
    }

    public void setAngle( double angle ) {
        this.angle = angle;
        assert angle >= 0 && angle <= Math.PI;
        update();
    }

    public void updateORIG() {
        Function.LinearFunction heightFunction = new Function.LinearFunction( 0, Math.PI / 2, 1, 0 );
        Function.LinearFunction widthFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, expansionWidth );
        double dx = widthFunction.evaluate( angle );//distance from normal edge to tilted edge
        double h = primaryHeight * heightFunction.evaluate( angle ) / 2;//distance from center to top

        DoubleGeneralPath generalPath = new DoubleGeneralPath();
        generalPath.moveTo( dx, h );
        generalPath.lineToRelative( primaryWidth - dx, 0 );
        generalPath.lineToRelative( 2 * dx, h * 2 );
        Point2D pin1 = generalPath.getCurrentPoint();
        generalPath.lineToRelative( -primaryWidth - 4 * dx, 0 );
        Point2D pin2 = generalPath.getCurrentPoint();
        generalPath.lineTo( dx, h );
        generalPath.closePath();
        surface.setPathTo( generalPath.getGeneralPath() );

        DoubleGeneralPath depthPath = new DoubleGeneralPath( pin1 );
        depthPath.lineToRelative( 0, 200 );
        depthPath.lineToRelative( pin2.getX() - pin1.getX(), 0 );
        depthPath.lineTo( pin2 );
        depthPath.closePath();
        depth.setPathTo( depthPath.getGeneralPath() );
    }

    public void update() {
        Function.LinearFunction heightFunction = new Function.LinearFunction( 0, Math.PI / 2, 1, 0 );
        Function.LinearFunction widthFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, expansionWidth );
        double dx = widthFunction.evaluate( angle );//distance from normal edge to tilted edge
        double h = primaryHeight * heightFunction.evaluate( angle ) / 2;//distance from center to top

        DoubleGeneralPath generalPath = new DoubleGeneralPath();
        generalPath.moveTo( dx, h );
        generalPath.lineToRelative( primaryWidth - dx, 0 );
        generalPath.lineToRelative( 2 * dx, h * 2 );
        Point2D pin1 = generalPath.getCurrentPoint();
        generalPath.lineToRelative( -primaryWidth - 4 * dx, 0 );
        Point2D pin2 = generalPath.getCurrentPoint();
        generalPath.lineTo( dx, h );
        generalPath.closePath();
        surface.setPathTo( generalPath.getGeneralPath() );

        DoubleGeneralPath depthPath = new DoubleGeneralPath( pin1 );
        depthPath.lineToRelative( 0, 200 );
        depthPath.lineToRelative( pin2.getX() - pin1.getX(), 0 );
        depthPath.lineTo( pin2 );
        depthPath.closePath();
        depth.setPathTo( depthPath.getGeneralPath() );
    }

    public double getSurfaceHeight() {
        return surface.getFullBounds().getHeight();
    }
}
