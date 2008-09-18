package edu.colorado.phet.batteryvoltage.man.voltListeners;

import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.batteryvoltage.Battery;

public class CompositeVoltageListener implements VoltageListener, ChangeListener {
    JSlider source;
    Vector v = new Vector();
    Battery b;

    public CompositeVoltageListener( JSlider source, Battery b ) {
        this.b = b;
        this.source = source;
    }

    public void addVoltageListener( VoltageListener vx ) {
        this.v.add( vx );
    }

    public void voltageChanged( int value, Battery b ) {
        for ( int i = 0; i < v.size(); i++ ) {
            ( (VoltageListener) v.get( i ) ).voltageChanged( value, b );
        }
    }

    public void stateChanged( ChangeEvent ce ) {
        voltageChanged( source.getValue(), b );
    }
}
