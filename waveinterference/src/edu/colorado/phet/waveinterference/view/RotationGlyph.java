/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
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
    private int boxHeight = 200;
    private MutableColor color;
    private PPath crossSectionGraphic;

    public RotationGlyph( MutableColor color ) {
        this.color = color;
        surface = new PPath();
        this.crossSectionGraphic = new PPath();
        crossSectionGraphic.setPaint( Color.black );
        crossSectionGraphic.setStroke( CrossSectionGraphic.STROKE );
//        surface.setPaint( new Color( 128, 128, 255 ) );
        surface.setPaint( color.getColor() );
        surface.setStroke( new BasicStroke( 3 ) );

        depth = new PPath();
        depth.setPaint( Color.blue );
        depth.setStroke( new BasicStroke( 4 ) );
        addChild( depth );
        addChild( surface );
        addChild( crossSectionGraphic );
        update();
        setRotation( 0.0 );
        color.addListener( new MutableColor.Listener() {
            public void colorChanged() {
                updateColors();
            }

        } );
        updateColors();
    }

    public RotationGlyph() {
        this( new MutableColor( Color.blue ) );
    }

    private void updateColors() {
        surface.setPaint( color.getColor().darker() );
        depth.setPaint( color.getColor() );
    }

    public void setDepthVisible( boolean visible ) {
        depth.setVisible( visible );
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

//    public void update() {
//        Function.LinearFunction heightFunction = new Function.LinearFunction( 0, Math.PI / 2, 1, 0 );
//        Function.LinearFunction widthFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, expansionWidth );
//        double dx = widthFunction.evaluate( angle );//distance from normal edge to tilted edge
//        double h = primaryHeight * heightFunction.evaluate( angle ) / 2;//distance from center to top
//
//        DoubleGeneralPath surfacePath = new DoubleGeneralPath();
//        surfacePath.moveTo( dx, h );
//        surfacePath.lineTo( primaryWidth - dx, h );
//        surfacePath.lineTo( primaryWidth+dx, h*2 );
//        Point2D pin1 = surfacePath.getCurrentPoint();
//        surfacePath.lineTo( -dx, h*2 );
//        Point2D pin2 = surfacePath.getCurrentPoint();
//        surfacePath.lineTo( dx, h );
//        surfacePath.closePath();
//        surface.setPathTo( surfacePath.getGeneralPath() );
//
//        DoubleGeneralPath depthPath = new DoubleGeneralPath( pin1 );
//        depthPath.lineToRelative( 0, boxHeight );
//        depthPath.lineToRelative( pin2.getX() - pin1.getX(), 0 );
//        depthPath.lineTo( pin2 );
//        depthPath.closePath();
//        depth.setPathTo( depthPath.getGeneralPath() );
//    }

    public void update() {
        Function.LinearFunction heightFunction = new Function.LinearFunction( 0, Math.PI / 2, 1, 0 );
        Function.LinearFunction widthFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, expansionWidth );
        double dx = widthFunction.evaluate( angle );//distance from normal edge to tilted edge
        double h = primaryHeight * heightFunction.evaluate( angle ) / 2;//distance from center to top

        DoubleGeneralPath surfacePath = new DoubleGeneralPath();
        surfacePath.moveTo( dx, h );
        surfacePath.lineToRelative( primaryWidth - 2 * dx, 0 );
        surfacePath.lineToRelative( 2 * dx, h * 2 );
        Point2D pin1 = surfacePath.getCurrentPoint();
        surfacePath.lineToRelative( -primaryWidth - 2 * dx, 0 );
        Point2D pin2 = surfacePath.getCurrentPoint();
        surfacePath.lineTo( dx, h );
        surfacePath.closePath();
        surface.setPathTo( surfacePath.getGeneralPath() );

        DoubleGeneralPath depthPath = new DoubleGeneralPath( pin1 );
        depthPath.lineToRelative( 0, boxHeight );
        depthPath.lineToRelative( pin2.getX() - pin1.getX(), 0 );
        depthPath.lineTo( pin2 );
        depthPath.closePath();
        depth.setPathTo( depthPath.getGeneralPath() );
        crossSectionGraphic.setPathTo( new Line2D.Double( 0, 2 * h, primaryWidth, 2 * h ) );
    }

    public double getSurfaceHeight() {
        return surface.getFullBounds().getHeight();
    }

    public PPath getSurface() {
        return surface;
    }

    public double getAngle() {
        return angle;
    }

    public double getExpansionWidth() {
        return expansionWidth;
    }

    public PPath getDepth() {
        return depth;
    }

    public void setColor( Color color ) {
        surface.setPaint( color );
    }
}
