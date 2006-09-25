package edu.colorado.phet.cck.piccolo_cck;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 10:08:51 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class VoltmeterModel {
    private boolean visible = false;
    private Point2D.Double unit = new Point2D.Double();
    private ArrayList listeners = new ArrayList();

    public VoltmeterModel() {
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        System.out.println( "visible = " + visible );
        this.visible = visible;
        notifyListeners();
    }

    public Point2D getUnitOffset() {
        return new Point2D.Double( unit.x, unit.y );
    }

    public void translateBody( double dx, double dy ) {
        unit.x += dx;
        unit.y += dy;
        notifyListeners();
    }

    public static interface Listener {
        void voltmeterChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.voltmeterChanged();
        }
    }
}
