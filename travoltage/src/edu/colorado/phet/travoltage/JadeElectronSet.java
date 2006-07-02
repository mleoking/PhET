/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 10:59:17 PM
 * Copyright (c) Jul 1, 2006 by Sam Reid
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

    public static interface Listener {
        void electronAdded( JadeElectron electron );
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
}
