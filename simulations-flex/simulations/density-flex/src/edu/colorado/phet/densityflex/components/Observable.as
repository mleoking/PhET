package edu.colorado.phet.densityflex.components {

public class Observable {
    private const listeners:Array = new Array();
    public function Observable() {
    }
    
    public function addListener(listener:Function):void {
        listeners.push(listener);
    }

    public function notifyObservers():void {
        for each (var listener:Function in listeners) {
            listener();
        }
    }
}
}