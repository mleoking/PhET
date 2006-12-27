package electron.man.motions;

import electron.man.Man;
import electron.man.Motion;

import java.util.Vector;

public class CompositeMotion implements Motion {
    Vector mx = new Vector();

    public boolean update( double dt, Man m ) {
        for( int i = 0; i < mx.size(); i++ ) {
            ( (Motion)mx.get( i ) ).update( dt, m );
        }
        return true;
    }

    public void add( Motion m ) {
        mx.add( m );
    }
}
