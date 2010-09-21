package edu.colorado.phet.batteryvoltage.man.voltListeners;

import edu.colorado.phet.batteryvoltage.Battery;

public interface VoltageListener {
    public void voltageChanged( int value, Battery b );
}
