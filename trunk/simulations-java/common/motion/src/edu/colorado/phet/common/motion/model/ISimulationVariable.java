package edu.colorado.phet.common.motion.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:15:36 AM
 */

public interface ISimulationVariable {

    public TimeData getData();

    public void setValue( double value );

    public static interface Listener {
        void valueChanged();
    }

    public void addListener( Listener listener );

}
