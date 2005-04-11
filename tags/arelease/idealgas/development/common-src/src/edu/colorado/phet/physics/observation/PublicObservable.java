package edu.colorado.phet.physics.observation;

import java.util.Observable;

/**
 * We have to make setChanged public, so adapters can call it without extending Observable.
 */
public class PublicObservable extends Observable{
    public void setChanged()
    {
        super.setChanged();
    }
}
