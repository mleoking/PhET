// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class Zoomable extends SimpleObservable {
    private final double minZoomValue;
    private double zoomLevel;
    private final double maxZoomValue;

    protected Zoomable( double minZoomValue, double zoomLevel, double maxZoomValue ) {
        this.minZoomValue = minZoomValue;
        this.zoomLevel = zoomLevel;
        this.maxZoomValue = maxZoomValue;
    }

    public void zoomOut() {
        setZoomLevel( zoomLevel / 1.2 );
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel( double zoomLevel ) {
        this.zoomLevel = MathUtil.clamp( minZoomValue, zoomLevel, maxZoomValue );
        notifyObservers();
    }

    public void zoomIn() {
        setZoomLevel( zoomLevel * 1.2 );
    }

    @Override
    public void addObserver( SimpleObserver so ) {
        super.addObserver( so );
        so.update();
    }

    public boolean isZoomInEnabled() {
        return zoomLevel < maxZoomValue;
    }

    public boolean isZoomOutEnabled() {
        return zoomLevel > minZoomValue;
    }
}
