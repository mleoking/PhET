package edu.colorado.phet.rotation.view;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform {
    private ArrayList listeners = new ArrayList();
    private double angle;

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle( double angle ) {
        if( this.angle != angle ) {
            this.angle = angle;
            notifyListeners();
        }
    }

    public static interface Listener {
        void angleChanged();
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.angleChanged();
        }
    }
}
