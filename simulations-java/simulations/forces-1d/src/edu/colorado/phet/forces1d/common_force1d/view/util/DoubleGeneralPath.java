/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.util;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.forces1d.common_force1d.math.AbstractVector2D;

/**
 * This adapter class for GeneralPath allows provides an interface in double coordinates.
 *
 * @author ?
 * @version $Revision$
 */
public class DoubleGeneralPath {
    GeneralPath path;

    public DoubleGeneralPath() {
        this.path = new GeneralPath();
    }

    public DoubleGeneralPath( Shape shape ) {
        path = new GeneralPath( shape );
    }

    public DoubleGeneralPath( AbstractVector2D pt ) {
        this( pt.getX(), pt.getY() );
    }

    public DoubleGeneralPath( Point2D pt ) {
        this( pt.getX(), pt.getY() );
    }

    public DoubleGeneralPath( double x, double y ) {
        path = new GeneralPath();
        path.moveTo( (float) x, (float) y );
    }

    public void moveTo( double x, double y ) {
        path.moveTo( (float) x, (float) y );
    }

    public void moveTo( Point2D pt ) {
        moveTo( pt.getX(), pt.getY() );
    }

    public void moveTo( AbstractVector2D vec ) {
        moveTo( vec.getX(), vec.getY() );
    }

    public void quadTo( double x1, double y1, double x2, double y2 ) {
        path.quadTo( (float) x1, (float) y1, (float) x2, (float) y2 );
    }

    public void curveTo( double x1, double y1, double x2, double y2, double x3, double y3 ) {
        path.curveTo( (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3 );
    }

    public void lineTo( double x, double y ) {
        path.lineTo( (float) x, (float) y );
    }

    public void lineTo( Point2D pt ) {
        lineTo( pt.getX(), pt.getY() );
    }

    public void lineToRelative( double dx, double dy ) {
        Point2D cur = path.getCurrentPoint();
        lineTo( cur.getX() + dx, cur.getY() + dy );
    }

    public Point2D getCurrentPoint() {
        return path.getCurrentPoint();
    }

    public void lineToRelative( AbstractVector2D vec ) {
        Point2D cur = path.getCurrentPoint();
        lineTo( cur.getX() + vec.getX(), cur.getY() + vec.getY() );
    }

    public GeneralPath getGeneralPath() {
        return path;
    }

    public void lineTo( AbstractVector2D loc ) {
        lineTo( loc.getX(), loc.getY() );
    }

    public void reset() {
        path.reset();
    }

    public void closePath() {
        path.closePath();
    }
}
