/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

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
        path.moveTo( (float)x, (float)y );
    }

    public void moveTo( double x, double y ) {
        path.moveTo( (float)x, (float)y );
    }

    public void moveTo( Point2D pt ) {
        moveTo( pt.getX(), pt.getY() );
    }

    public void moveTo( AbstractVector2D vec ) {
        moveTo( vec.getX(), vec.getY() );
    }

    public void lineTo( double x, double y ) {
        path.lineTo( (float)x, (float)y );
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
}
