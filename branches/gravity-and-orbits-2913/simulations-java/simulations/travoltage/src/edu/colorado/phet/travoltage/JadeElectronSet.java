// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 10:59:17 PM
 */

public class JadeElectronSet {
    private ArrayList jadeElectrons = new ArrayList();

    public JadeElectronSet() {
    }

    public void addElectron( JadeElectron jadeElectron ) {
        jadeElectrons.add( jadeElectron );
        notifyListeners( jadeElectron );
    }

    public int getNumElectrons() {
        return jadeElectrons.size();
    }

    public JadeElectron getJadeElectron( int i ) {
        return (JadeElectron)jadeElectrons.get( i );
    }

    private ArrayList listeners = new ArrayList();

    public void removeElectron( int i ) {
        JadeElectron electron = (JadeElectron)jadeElectrons.remove( i );
        notifyElectronRemoved( electron );
    }

    private void notifyElectronRemoved( JadeElectron electron ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.electronRemoved( electron );
        }
    }

    public static interface Listener {
        void electronAdded( JadeElectron electron );

        void electronRemoved( JadeElectron electron );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners( JadeElectron jadeElectron ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.electronAdded( jadeElectron );
        }
    }

    public static class Adapter implements Listener {
        public void electronAdded( JadeElectron electron ) {
        }

        public void electronRemoved( JadeElectron electron ) {
        }
    }
}
