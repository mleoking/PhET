package edu.colorado.phet.flexcommon.model {

public class Observable {
    private const listeners: Array = new Array();

    public function Observable() {
    }

    //TODO: should we have auto-callbacks?  This has worked very well in the Java side
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