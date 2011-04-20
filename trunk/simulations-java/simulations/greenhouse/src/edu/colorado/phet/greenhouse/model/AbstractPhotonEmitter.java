// Copyright 2002-2011, University of Colorado

/**
 * Class: AbstractPhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

public abstract class AbstractPhotonEmitter extends Observable implements ModelElement, PhotonEmitter<Photon> {

    private double productionRate;
    private double timeSincePhotonsProduced;
    private HashSet listeners = new HashSet();

    public void addListener( PhotonEmitter.Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( PhotonEmitter.Listener listener ) {
        listeners.remove( listener );
    }

    public double getProductionRate() {
        return productionRate;
    }

    public void setProductionRate( double productionRate ) {
        this.timeSincePhotonsProduced = 0;
        this.productionRate = productionRate;
    }

    public void stepInTime( double dt ) {
        timeSincePhotonsProduced += dt;
        int numPhotons = (int) ( productionRate * timeSincePhotonsProduced );
        for ( int i = 0; i < numPhotons; i++ ) {
            Photon photon = emitPhoton();
            notifyListeners( photon );
            timeSincePhotonsProduced = 0;
        }
    }

    // Notify all listeners
    protected void notifyListeners( Photon photon ) {
        for ( Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
            Listener listener = (Listener) iterator.next();
            listener.photonEmitted( photon );
        }
    }
}
