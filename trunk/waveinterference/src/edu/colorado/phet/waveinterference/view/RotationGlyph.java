/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

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
    private double boxHeight = 200;
    private MutableColor sideColor;
    private MutableColor surfaceColor = new MutableColor( Color.blue );
    private PPath crossSectionGraphic;
    private BasicStroke STROKE = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1 );
    private ArrayList listeners = new ArrayList();

    public RotationGlyph() {
        this( new MutableColor( Color.blue ) );
    }

    public RotationGlyph( MutableColor color ) {
        this.sideColor = color;
        surface = new PPath();
        this.crossSectionGraphic = new PPath();
        crossSectionGraphic.setPaint( Color.black );
        crossSectionGraphic.setStroke( CrossSectionGraphic.STROKE );
//        surface.setPaint( new Color( 128, 128, 255 ) );
        surface.setPaint( sideColor.getColor() );
        surface.setStroke( STROKE );

        depth = new PPath();
        depth.setPaint( Color.blue );

        depth.setStroke( STROKE );
//        depth.setStroke( CrossSectionGraphic.STROKE );
        addChild( depth );
        addChild( surface );
//        addChild( crossSectionGraphic );
        update();
        setRotation( 0.0 );
        sideColor.addListener( new MutableColor.Listener() {
            public void colorChanged() {
                updateColors();
            }
        } );
        setSideColor( sideColor.getColor() );
        updateColors();
    }

    private void updateColors() {
        surface.setPaint( surfaceColor.getColor() );
        depth.setPaint( sideColor.getColor() );
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


    public static interface Listener {
        void angleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyAngleChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.angleChanged();
        }
    }

    public void setAngle( double angle ) {
        this.angle = angle;
        assert angle >= 0 && angle <= Math.PI;
        update();
        notifyAngleChanged();
    }

    public void update() {
        Function.LinearFunction heightFunction = new Function.LinearFunction( 0, Math.PI / 2, 1, 0 );
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, 1 );
        Function.LinearFunction widthFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, expansionWidth );
        double depthExpansionWidth = 30;
        Function.LinearFunction depthWidthFunction = new Function.LinearFunction( 0, Math.PI / 2, depthExpansionWidth, 0 );
        double dx = widthFunction.evaluate( angle );//distance from normal edge to tilted edge
        double h = primaryHeight * heightFunction.evaluate( angle ) / 2;//distance from center to top
        //double boxHeight=this.boxHeight;//apparent box height independent of angle
        double boxHeight = this.boxHeight * linearFunction.evaluate( angle );

//        DoubleGeneralPath surfacePath = new DoubleGeneralPath(dx,h);//to have the full box
        DoubleGeneralPath surfacePath = new DoubleGeneralPath( dx, 0 );//half a box
        surfacePath.lineToRelative( primaryWidth - 2 * dx, 0 );
        surfacePath.lineToRelative( dx, h );
        Point2D pin1 = surfacePath.getCurrentPoint();
        surfacePath.lineToRelative( -primaryWidth, 0 );
        Point2D pin2 = surfacePath.getCurrentPoint();
        surfacePath.closePath();
        surface.setPathTo( surfacePath.getGeneralPath() );

        double depthDX = depthWidthFunction.evaluate( angle );
        DoubleGeneralPath depthPath = new DoubleGeneralPath( pin1 );
        depthPath.lineToRelative( -depthDX, boxHeight );
        depthPath.lineToRelative( pin2.getX() - pin1.getX() + 2 * depthDX, 0 );
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

    public void setColors( Color sideColor, Color topColor ) {
        this.sideColor.setColor( sideColor );
        this.surfaceColor.setColor( topColor );
        updateColors();
    }

    public void setSideColor( Color sideColor ) {
        this.sideColor.setColor( sideColor );
        this.surfaceColor.setColor( sideColor.darker() );
        updateColors();
    }

    public void setTopColor( Color topColor ) {
        this.sideColor.setColor( topColor.darker() );
        this.surfaceColor.setColor( topColor );
        updateColors();
    }

    public void synchronizeDepthSize( final WaveModelGraphic waveModelGraphic ) {
        waveModelGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateBoxHeight( waveModelGraphic );
            }
        } );
        updateBoxHeight( waveModelGraphic );
        update();
    }

    private void updateBoxHeight( WaveModelGraphic waveModelGraphic ) {
        boxHeight = waveModelGraphic.getFullBounds().getHeight() / 2;
    }
}
