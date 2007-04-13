package edu.colorado.phet.rotation.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:15:36 AM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class SimulationVariable {
    private double value;
    private ArrayList listeners = new ArrayList();

    public SimulationVariable() {
        this( 0.0 );
    }

    public SimulationVariable( double value ) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue( double value ) {
        if( this.value != value ) {
            this.value = value;
            notifyListeners();
        }
    }

    public static interface Listener {
        void valueChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.valueChanged();
        }
    }
}
