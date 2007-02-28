package edu.colorado.phet.physics.observation;

/**
 * This AbstractObservable definition leaves addition of Observers to subclasses to
 * promote static typing.
 */
public interface AbstractObservable {
    public void notifyObservers();
    public void setChanged();
}
