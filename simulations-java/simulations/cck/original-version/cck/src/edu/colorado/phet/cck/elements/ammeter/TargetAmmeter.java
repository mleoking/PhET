/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.ammeter;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:56:43 AM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class TargetAmmeter extends SimpleObservable {
    double x;
    double y;
    SimpleObservable aboutToTranslateListeners = new SimpleObservable();

    public TargetAmmeter( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public void addAboutToTranslateListener( SimpleObserver obs ) {
        aboutToTranslateListeners.addObserver( obs );
    }

    public Point2D.Double getLocation() {
        return new Point2D.Double( x, y );
    }

    public void translate( double dx, double dy ) {
        aboutToTranslateListeners.notifyObservers();
        x += dx;
        y += dy;
        notifyObservers();
    }

    public double getCurrent() {
        return 0;
    }
}
