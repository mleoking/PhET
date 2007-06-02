package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 2, 2007, 4:06:24 AM
 */
public class EnergySkateParkClock extends SwingClock {
    public EnergySkateParkClock( int delay, double dt ) {
        super( delay, dt );
    }

    public void setTimingStrategy( TimingStrategy timingStrategy ) {
        super.setTimingStrategy( timingStrategy );
        notifyTimingStrategyChanged();
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void changed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyTimingStrategyChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).changed();
        }
    }

}
