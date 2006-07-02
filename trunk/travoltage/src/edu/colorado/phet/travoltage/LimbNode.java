/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:37:46 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class LimbNode extends PNode {
    private Point pivot;
    private PImage imageNode;
    private double angle = 0;
    private ArrayList listeners = new ArrayList();

    public LimbNode( String imageLoc, Point pivot ) {
        imageNode = PImageFactory.create( imageLoc );
        addChild( imageNode );
        this.pivot = pivot;
        addInputEventListener( new RotationHandler( this ) );
        addInputEventListener( new CursorHandler() );
    }

    protected PImage getImageNode() {
        return imageNode;
    }

    public Point getPivot() {
        return new Point( pivot );
    }

    /**
     * Determines the angle of the input point to the pivot point in global coordinates.
     *
     * @param x
     * @param y
     */
    public double getAngleGlobal( double x, double y ) {
        Point2D temp = new Point2D.Double( pivot.x, pivot.y );
        localToGlobal( temp );
        Vector2D.Double vec = new Vector2D.Double( new Point2D.Double( x, y ), temp );
        return vec.getAngle();
    }

    public void rotateAboutPivot( double dTheta ) {
        this.angle += dTheta;
        rotateAboutPoint( dTheta, pivot );
        notifyListeners();
    }

    public void setAngle( double v ) {
        rotateAboutPivot( v - angle );
    }

    public Point2D getGlobalPivot() {
        return localToGlobal( new Point2D.Double( pivot.getX(), pivot.getY() ) );
    }

    public double getAngle() {
        return angle;
    }

    public static interface Listener {
        void limbRotated();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.limbRotated();
        }
    }
}
