// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.controls;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:11:17 PM
 */

public class VectorViewModel {
    private boolean velocityVisible = true;
    private boolean accelerationVisible = true;
    private ArrayList listeners = new ArrayList();

    public VectorViewModel() {
        resetAll();
    }

    public boolean isVelocityVisible() {
        return velocityVisible;
    }

    public boolean isAccelerationVisible() {
        return accelerationVisible;
    }

    public void setVelocityVisible( boolean visible ) {
        if ( this.velocityVisible != visible ) {
            this.velocityVisible = visible;
            notifyListeners();
        }
    }

    public void setAccelerationVisible( boolean visible ) {
        if ( this.accelerationVisible != visible ) {
            this.accelerationVisible = visible;
            notifyListeners();
        }
    }

    public void resetAll() {
        setAccelerationVisible( true );
        setVelocityVisible( true );
    }

    public static interface Listener {
        void visibilityChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.visibilityChanged();
        }
    }
}
