package edu.colorado.phet.flexcommon.model {

public class Observable {
    private const listeners: Array = new Array();

    public function Observable() {
    }

    //TODO: should we have auto-callbacks?  This has worked very well in the Java side
    //REVIEW Yes, for consistency with PhET java sims and because it works well.  Would want a version of addListener
    // that makes notifications optional.
    public function addListener( listener: Function ): void {
        listeners.push( listener );
    }

    public function notifyObservers(): void {
        for each ( var listener: Function in listeners ) {
            listener();
        }
    }
}
}