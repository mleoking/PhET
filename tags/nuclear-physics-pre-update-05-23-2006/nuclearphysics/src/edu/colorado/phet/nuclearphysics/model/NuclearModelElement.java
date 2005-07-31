/**
 * Class: NuclearModelElement
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Oct 6, 2004
 * Time: 7:25:34 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class NuclearModelElement extends Body {
    private ArrayList listeners = new ArrayList();

    public interface Listener {
        void leavingSystem( NuclearModelElement nme );
    }

    protected NuclearModelElement( Point2D location, Vector2D velocity,
                                   Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void leaveSystem() {
        // Do it this way, in case a listener calls removeListener( this ) in leavingSystem();
        while( listeners.size() > 0 ) {
            Listener listener = (Listener)listeners.get( 0 );
            listener.leavingSystem( this );
            this.removeListener( listener );
        }
    }
}
