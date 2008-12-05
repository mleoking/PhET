package edu.colorado.phet.rotation.model;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 10:45:52 AM
 */
public class AngleUnitModel {
    private boolean radians;
    private ArrayList listeners = new ArrayList();

    public AngleUnitModel( boolean radians ) {
        this.radians = radians;
    }

    public boolean isRadians() {
        return radians;
    }

    public void setRadians( boolean radians ) {
        if ( this.radians != radians ) {
            this.radians = radians;
            notifyValueChanged();
        }
    }

    public static interface Listener {
        void changed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyValueChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).changed();
        }
    }

    public String toString() {
        return super.toString() + " radians=" + radians;
    }
}
