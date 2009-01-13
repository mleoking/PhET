/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.event.ListDataEvent;
import org.aswing.event.ListDataListener;
import org.aswing.util.ArrayUtils;

/**
 * @author iiley
 */
class org.aswing.AbstractListModel{
    private var listeners:Array;
    
    public function AbstractListModel(){
    	listeners = new Array();
    }

    public function addListDataListener(l:ListDataListener):Void {
		listeners.push(l);
    }

    public function removeListDataListener(l:ListDataListener):Void {
    	ArrayUtils.removeFromArray(listeners, l);
    }

    private function fireContentsChanged(target:Object, index0:Number, index1:Number, removedItems:Array):Void
    {
		var e:ListDataEvent = new ListDataEvent(target, ListDataEvent.CONTENTS_CHANGED, index0, index1, removedItems);
	
		for (var i:Number = listeners.length - 1; i >= 0; i --) {
			var lis:ListDataListener = ListDataListener(listeners[i]);
			lis.contentsChanged(e);     
		}
    }



    private function fireIntervalAdded(target:Object, index0:Number, index1:Number):Void
    {
		var e:ListDataEvent = new ListDataEvent(target, ListDataEvent.INTERVAL_ADDED, index0, index1, []);
	
		for (var i:Number = listeners.length - 1; i >= 0; i --) {
			var lis:ListDataListener = ListDataListener(listeners[i]);
			lis.intervalAdded(e);     
		}
    }



    private function fireIntervalRemoved(target:Object, index0:Number, index1:Number, removedItems:Array):Void
    {
		var e:ListDataEvent = new ListDataEvent(target, ListDataEvent.INTERVAL_REMOVED, index0, index1, removedItems);
	
		for (var i:Number = listeners.length - 1; i >= 0; i --) {
			var lis:ListDataListener = ListDataListener(listeners[i]);
			lis.intervalRemoved(e);
		}		
    }
}
