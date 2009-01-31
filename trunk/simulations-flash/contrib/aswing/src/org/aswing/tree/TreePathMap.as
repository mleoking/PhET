/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.tree.TreePath;
import org.aswing.util.HashMap;

/**
 * Only accept TreePath instance to be the key.
 * @author iiley
 */
class org.aswing.tree.TreePathMap extends HashMap {
	
	public function TreePathMap() {
		super();
	}
	
	private function isIdentifiableKey(key:TreePath):Boolean{
		return key.isIdentifiable();
	}
	
 	/**
  	 * Tests if the specified object is a key in this HashMap.
  	 * This operation is very fast if it is a string.
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
  	 */
 	public function containsKey(key:TreePath):Boolean{
 		if(isIdentifiableKey(key)){
	 		if(content[key.getIdCode()] != undefined){
	 			return true;
	 		}
 		}else{
	  		if(indexOfKey(key) >= 0){
	  			return true;
	  		}
 		}
  		return false;
 	}

 	/**
 	 * Returns the value to which the specified key is mapped in this HashMap.
 	 * Return null if the key is not mapped to any value in this HashMap.
  	 * This operation is very fast if the key is a string.
     * @param   key the key whose associated value is to be returned.
     * @return  the value to which this map maps the specified key, or
     *          <tt>null</tt> if the map contains no mapping for this key
     *           or it is null value originally.
 	 */
 	public function get(key:TreePath){
 		if(isIdentifiableKey(key)){
	 		var value = content[key.getIdCode()];
	 		if(value !== undefined){
	 			return value;
	 		}
 		}else{
	 		var index:Number = indexOfKey(key);
	 		if(index >= 0){
	 			return noneStringMapedValues[index];
	 		}
 		}
  		return null;
 	}

 	/**
 	 * Associates the specified value with the specified key in this map. 
 	 * If the map previously contained a mapping for this key, the old value is replaced. 
 	 * 
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the HashMap previously associated
     *	       <tt>null</tt> with the specified key.
  	 */
 	public function put(key:TreePath, value){
  		if(key == undefined){
   			trace("cannot put a value with undefined or null key!");
   			return undefined;
  		}else{
  			var exist:Boolean = containsKey(key);
 			if(!exist){
   				length++;
 			}
 			var oldValue = this.get(key);
 			if(isIdentifiableKey(key)){
	   			content[key.getIdCode()]=value;
	   			keyContent[key.getIdCode()]=key;
 			}else{
 				if(!exist){
					noneStringKeys.push(key);
					noneStringMapedValues.push(value);
 				}else{
 					var index:Number = indexOfKey(key);
 					noneStringKeys[index] = key;
 					noneStringMapedValues[index] = value;
 				}
 			}
   			return oldValue;
  		}
 	}

 	/**
     * Removes the mapping for this key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key.
  	 */
 	public function remove(key:TreePath){
 		var exist:Boolean = containsKey(key);
 		if(!exist){
 			return null;
 		}
  		var temp;
  		if(isIdentifiableKey(key)){
  			var id:String = key.getIdCode();
   			temp=content[id];
   			delete content[id];
   			delete keyContent[id];
  		}else{
 			var index:Number = indexOfKey(key);
 			temp = noneStringMapedValues[index];
 			noneStringKeys.splice(index, 1);
 			noneStringMapedValues.splice(index, 1);
  		}
   		length--;
  		return temp;
 	}	
	
 	private function indexOfKey(key:TreePath):Number{
 		for(var i:Number=0; i<noneStringKeys.length; i++){
 			var nsk = noneStringKeys[i];
 			if(key === nsk || key.equals(nsk)){
 				return i;
 			}
 		}
 		return -1;
 	}
}