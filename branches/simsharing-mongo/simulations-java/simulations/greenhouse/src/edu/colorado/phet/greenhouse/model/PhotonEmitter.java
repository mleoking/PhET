// Copyright 2002-2011, University of Colorado

/**
 * Class: PhotonEmitter
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;


public interface PhotonEmitter<T> {

    void addListener( Listener listener );

    void removeListener( Listener listener );

    double getProductionRate();

    void setProductionRate( double productionRate );

    T emitPhoton();

    //
    // Inner classes
    //
    public interface Listener<T> {
        void photonEmitted( T photon );
    }

}
