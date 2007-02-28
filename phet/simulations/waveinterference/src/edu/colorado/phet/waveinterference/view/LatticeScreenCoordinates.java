/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:46:29 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public abstract class LatticeScreenCoordinates {
    private ArrayList listeners = new ArrayList();

    public abstract Point2D toScreenCoordinates( int i, int j );

    public abstract Point toLatticeCoordinates( double x, double y );

    protected abstract Dimension getGridSize();

    public Rectangle2D getScreenRect() {
        Dimension dim = getGridSize();
        return toScreenRect( new Rectangle( 0, 0, dim.width, dim.height ) );
    }

    public Rectangle2D toScreenRect( Rectangle rectangle ) {
        Point2D min = toScreenCoordinates( rectangle.x, rectangle.y );
        Point2D max = toScreenCoordinates( rectangle.x + rectangle.width, rectangle.y + rectangle.height );
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal( min, max );
        return rect;
    }

    public void notifyMappingChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.mappingChanged();
        }
    }

    public double getCellWidth() {
//        return toLatticeCoordinates( 1,0).getX()-toLatticeCoordinates( 0,0).getX();
        return toScreenCoordinates( 1, 0 ).getX() - toScreenCoordinates( 0, 0 ).getX();
    }

    public double toLatticeCoordinatesDifferentialX( double dx ) {
        return toLatticeCoordinates( dx, 0 ).getX() - toLatticeCoordinates( 0, 0 ).getX();
    }

    public double toLatticeCoordinatesDifferentialY( double dy ) {
        return toLatticeCoordinates( 0, dy ).getY() - toLatticeCoordinates( 0, 0 ).getY();
    }

    public static interface Listener {
        void mappingChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
