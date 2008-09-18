package edu.colorado.phet.batteryvoltage.common.electron.man.motions;

import java.util.Vector;

import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.Motion;

public class CompositeMotion implements Motion {
    Vector mx = new Vector();

    public boolean update( double dt, Man m ) {
        for ( int i = 0; i < mx.size(); i++ ) {
            ( (Motion) mx.get( i ) ).update( dt, m );
        }
        return true;
    }

    public void add( Motion m ) {
        mx.add( m );
    }
}
