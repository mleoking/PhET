// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonEvent;
import edu.colorado.phet.solublesalts.model.ion.IonListener;

/**
 * IonTracker
 * <p/>
 * An agent that tracks the addition and removal of ions from the model
 *
 * @author Ron LeMaster
 */
public class IonTracker {

    private HashMap<Class, List<Ion>> ionMap = new HashMap<Class, List<Ion>>();

    //----------------------------------------------------------------
    // SolubleSaltsModel.IonListener implementation
    //----------------------------------------------------------------

    public void ionAdded( Ion ion ) {
        List<Ion> ionList = ionMap.get( ion.getClass() );
        if ( ionList == null ) {
            ionList = new ArrayList<Ion>();
            ionMap.put( ion.getClass(), ionList );
        }
        ionList.add( ion );
        ionListenerProxy.ionAdded( new IonEvent( ion ) );
    }

    public void ionRemoved( Ion ion ) {
        ionMap.get( ion.getClass() ).remove( ion );

        ionListenerProxy.ionRemoved( new IonEvent( ion ) );
    }

    public int getNumIonsOfType( Class ionClass ) {
        int result = 0;
        List<Ion> ionSet = ionMap.get( ionClass );
        if ( ionSet != null ) {
            result = ionSet.size();
        }
        return result;
    }

    public int getNumFreeIonsOfType( Class ionClass ) {
        Collection<Ion> ionSet = ionMap.get( ionClass );
        int cnt = 0;
        if ( ionSet != null ) {
            for ( Ion ion : ionSet ) {
                cnt += ion.isBound() ? 0 : 1;
            }
        }
        return cnt;
    }

    public List<Ion> getIonsOfType( Class ionClass ) {
        if ( ionMap.containsKey( ionClass ) ) {
            return ionMap.get( ionClass );
        }
        else { return new ArrayList<Ion>(); }
    }

    public List<Ion> getIons() {
        List<Ion> result = new ArrayList<Ion>();
        Collection<List<Ion>> lists = ionMap.values();
        for ( List<Ion> l : lists ) {
            result.addAll( l );
        }
        return result;
    }

    //----------------------------------------------------------------
    // Events and listeners for Ions
    //----------------------------------------------------------------

    private EventChannel ionEventChannel = new EventChannel( IonListener.class );
    private IonListener ionListenerProxy = (IonListener) ionEventChannel.getListenerProxy();

    public void addIonListener( IonListener listener ) {
        ionEventChannel.addListener( listener );
    }

    public void removeIonListener( IonListener listener ) {
        ionEventChannel.removeListener( listener );
    }
}