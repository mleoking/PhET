// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.util.*;

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

    private HashMap ionMap = new HashMap();
    private HashMap freeIonCntMap = new HashMap();

    //----------------------------------------------------------------
    // SolubleSaltsModel.IonListener implementation
    //----------------------------------------------------------------

    public void ionAdded( Ion ion ) {
        List ionSet = (List) ionMap.get( ion.getClass() );
        if ( ionSet == null ) {
            ionSet = new ArrayList();
            ionMap.put( ion.getClass(), ionSet );
            freeIonCntMap.put( ion.getClass(), new Integer( 0 ) );
        }
        ionSet.add( ion );
        int cnt = ( (Integer) freeIonCntMap.get( ion.getClass() ) ).intValue();
        freeIonCntMap.put( ion.getClass(), new Integer( ++cnt ) );
        ionListenerProxy.ionAdded( new IonEvent( ion ) );
    }

    public void ionRemoved( Ion ion ) {
        int cnt = ( (Integer) freeIonCntMap.get( ion.getClass() ) ).intValue();
        freeIonCntMap.put( ion.getClass(), new Integer( --cnt ) );
        List ionSet = (List) ionMap.get( ion.getClass() );
        ionSet.remove( ion );

        ionListenerProxy.ionRemoved( new IonEvent( ion ) );
    }

    public int getNumIonsOfType( Class ionClass ) {
        int result = 0;
        List ionSet = (List) ionMap.get( ionClass );
        if ( ionSet != null ) {
            result = ionSet.size();
        }
        return result;
    }

    public int getNumFreeIonsOfType( Class ionClass ) {
        Collection ionSet = (Collection) ionMap.get( ionClass );
        int cnt = 0;
        if ( ionSet != null ) {
            for ( Iterator iterator = ionSet.iterator(); iterator.hasNext(); ) {
                Ion ion = (Ion) iterator.next();
                cnt += ion.isBound() ? 0 : 1;
            }
        }
        return cnt;
    }

    public List getIonsOfType( Class ionClass ) {
        return (List) ionMap.get( ionClass );
    }

    public List getIons() {
        List result = new ArrayList();
        Collection lists = ionMap.values();
        for ( Iterator it = lists.iterator(); it.hasNext(); ) {
            List l = (List) it.next();
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