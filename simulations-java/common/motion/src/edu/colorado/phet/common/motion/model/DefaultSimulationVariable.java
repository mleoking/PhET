package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 26, 2007, 12:20:50 PM
 */
public class DefaultSimulationVariable implements ISimulationVariable {
    private double value;
    private double time;
    private ArrayList listeners = new ArrayList();

    public TimeData getData() {
        return new TimeData( value, time );
    }

    public void setValue( double value ) {
        if( this.value != value ) {
            this.value = value;
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener)listeners.get( i ) ).valueChanged();
            }
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public double getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }
}
