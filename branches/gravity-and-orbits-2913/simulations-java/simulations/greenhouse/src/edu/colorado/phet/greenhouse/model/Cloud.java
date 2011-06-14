// Copyright 2002-2011, University of Colorado

/**
 * Class: Cloud
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 10, 2003
 * Time: 11:36:26 PM
 */
package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


public class Cloud extends Body {

    private Ellipse2D.Double bounds;

    public Cloud( Ellipse2D.Double bounds ) {
        this.bounds = bounds;
        setLocation( getCM().getX(), getCM().getY() );
    }

    public Point2D.Double getCM() {
        return new Point2D.Double( bounds.getX() + bounds.getWidth() / 2,
                                   bounds.getY() + bounds.getHeight() / 2 );
    }

    public double getMomentOfInertia() {
        return Double.MAX_VALUE;
    }

    public Ellipse2D.Double getBounds() {
        return bounds;
    }

    public double getWidth() {
        return bounds.getWidth();
    }

    public double getHeight() {
        return bounds.getHeight();
    }
}
