package edu.colorado.phet.signal;

import java.util.Vector;

public class SwitchCloser implements AngleListener {
    double thresholdAngle;
    Vector v = new Vector();

    public SwitchCloser( double thresholdAngle ) {
        this.thresholdAngle = thresholdAngle;
    }

    public void addSwitchListener( SwitchListener s ) {
        v.add( s );
    }

    public void angleChanged( double newAngle ) {
        boolean state = false;
        if( newAngle <= thresholdAngle ) {
            state = true;
        }
        for( int i = 0; i < v.size(); i++ ) {
            ( (SwitchListener)v.get( i ) ).setSwitchClosed( state );
        }
    }
}
