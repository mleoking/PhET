/*  */
package edu.colorado.phet.forces1d.phetcommon.view.phetcomponents;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 22, 2005
 * Time: 10:48:14 AM
 */

public class PhetJComponentManager {
    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void phetJComponentCreated( PhetJComponent phetJComponent );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void phetJComponentCreated( PhetJComponent phetJComponent ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.phetJComponentCreated( phetJComponent );
        }
    }

}
