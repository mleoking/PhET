package edu.colorado.phet.densityflex.components {

public class Observable {
    const listeners:Array = new Array();
    public function Observable() {
    }
    
    function addListener(listener:Function):void {
        listeners.push(listener);
    }

    function notifyObservers():void {
        for each (var listener:Function in listeners) {
            listener();
        }
    }
}
}