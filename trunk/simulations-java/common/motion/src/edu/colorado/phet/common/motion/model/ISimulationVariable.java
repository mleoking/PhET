package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:15:36 AM
 */

public interface ISimulationVariable {

    TimeData getData();

    void setValue( double value );

    double getValue();

    public static interface Listener {
        void valueChanged();
    }

    public void addListener( Listener listener );

    public void removeListener(Listener listener);

}
