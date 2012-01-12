/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree{

import org.aswing.util.HashMap;
	
/**
 * A hash map that accept TreePath key.
 * @author iiley
 */	
public class TreePathMap{
	
	private var map:HashMap;
	private var keyMap:HashMap;
	
	public function TreePathMap(){
		map = new HashMap();
		keyMap = new HashMap();
	}
	
 	public function size():int{
  		return map.size();
 	}
 	
 	public function isEmpty():Boolean{
  		return map.isEmpty();
 	}

 	public function keys():Array{
  		return keyMap.values();
 	}
 	
 	/**
  	 * Returns an Array of the values in this HashMap.
  	 */
 	public function values():Array{
  		return map.values();
 	}
 	
 	public function containsValue(value:*):Boolean{
 		return map.containsValue(value);
 	}

 	public function containsKey(key:TreePath):Boolean{
 		return map.containsKey(key.getLastPathComponent());
 	}

 	public function get(key:TreePath):*{
  		return map.getValue(key.getLastPathComponent());
 	}
 	
 	public function getValue(key:TreePath):*{
  		return map.getValue(key.getLastPathComponent());
 	}

 	public function put(key:TreePath, value:*):*{
 		keyMap.put(key.getLastPathComponent(), key);
  		return map.put(key.getLastPathComponent(), value);
 	}

 	public function remove(key:TreePath):*{
 		keyMap.remove(key.getLastPathComponent());
 		return map.remove(key.getLastPathComponent());
 	}

 	public function clear():void{
 		keyMap.clear();
  		map.clear();
 	}

 	/**
 	 * Return a same copy of HashMap object
 	 */
 	public function clone():TreePathMap{
  		var temp:TreePathMap = new TreePathMap();
  		temp.map = map.clone();
  		temp.keyMap = keyMap.clone();
  		return temp;
 	}

 	public function toString():String{
  		return map.toString();
 	}
}
}