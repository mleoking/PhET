/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.event.*;
import org.aswing.util.*;

/**
 * Abstract list model that provide the list model events base.
 * @author iiley
 */
public class AbstractListModel{
	
    private var listeners:Array;
    
    public function AbstractListModel(){
    	listeners = new Array();
    }

    public function addListDataListener(l:ListDataListener):void {
		listeners.push(l);
    }

    public function removeListDataListener(l:ListDataListener):void {
    	ArrayUtils.removeFromArray(listeners, l);
    }

    protected function fireContentsChanged(target:Object, index0:int, index1:int, removedItems:Array):void{
		var e:ListDataEvent = new ListDataEvent(target, index0, index1, removedItems);
	
		for (var i:int = listeners.length - 1; i >= 0; i --) {
			var lis:ListDataListener = ListDataListener(listeners[i]);
			lis.contentsChanged(e);
		}
    }

    protected function fireIntervalAdded(target:Object, index0:int, index1:int):void{
		var e:ListDataEvent = new ListDataEvent(target, index0, index1, []);
	
		for (var i:int = listeners.length - 1; i >= 0; i --) {
			var lis:ListDataListener = ListDataListener(listeners[i]);
			lis.intervalAdded(e);     
		}
    }

    protected function fireIntervalRemoved(target:Object, index0:int, index1:int, removedItems:Array):void{
		var e:ListDataEvent = new ListDataEvent(target, index0, index1, removedItems);
	
		for (var i:int = listeners.length - 1; i >= 0; i --) {
			var lis:ListDataListener = ListDataListener(listeners[i]);
			lis.intervalRemoved(e);
		}		
    }
}
}