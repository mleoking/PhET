/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

import java.util.ArrayList;

/**
 * A TickListenter that passes events on to its children.
 */
public class CompositeTickListener implements TickListener {
    ArrayList list = new ArrayList();

    public void addTickListener( TickListener ticker ) {
        list.add( ticker );
    }

    public int numTickListeners() {
        return list.size();
    }

    public TickListener tickListenerAt( int i ) {
        return (TickListener)list.get( i );
    }

    public void removeTickListener( TickListener ticker ) {
        list.remove( ticker );
    }

    public void clockTicked( AbstractClock source ) {
        for( int i = 0; i < list.size(); i++ ) {
            TickListener tickListener = (TickListener)list.get( i );
            tickListener.clockTicked( source );
        }
    }
}
