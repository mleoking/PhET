/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.util.List;

/**
 * The mutable list model vector implementation.
 * @author iiley
 */
public class VectorListModel extends AbstractListModel implements MutableListModel, List{

	protected var _elements:Array;
	public static const CASEINSENSITIVE:int=1;
	public static const DESCENDING :int=2;
	public static const UNIQUESORT :int=4;
	public static const RETURNINDEXEDARRAY :int=8;	
	public static const NUMERIC  :int=16;	
	
	/**
	 * Create a VectorListModel instance.
	 * @param initalData (optional)the to be copid to the model.
	 */
	public function VectorListModel(initalData:Array=null){
		super();
		if(initalData != null){
			_elements = initalData.concat();
		}else{
			_elements = new Array();
		}
	}
	
	
	public function get(i:int):*{
		return _elements[i];
	}
	
	/**
	 * implemented ListMode
	 */
	public function getElementAt(i:int):*{
		return _elements[i];
	}
	
	public function append(obj:*, index:int=-1):void{
		if(index == -1){
			index = _elements.length;
			_elements.push(obj);
		}else{
			_elements.splice(index, 0, obj);
		}
		fireIntervalAdded(this, index, index);
	}
	
	public function replaceAt(index:int, obj:*):*{
		if(index<0 || index>= size()){
			return null;
		}
		var oldObj:* = _elements[index];
		_elements[index] = obj;
		fireContentsChanged(this, index, index, [oldObj]);
		return oldObj;
	}	
	
	/**
	 * Append all the elements of a array(arr) to the specified position of the 
	 * vector.
	 * @param arr the elements array
	 * @param index the position to be append, default is -1 means the end of the vector.
	 */
	public function appendAll(arr:Array, index:int=-1):void{
		if(arr == null || arr.length <= 0){
			return;
		}
		if(index == -1){
			index = _elements.length;
		}
		if(index == 0){
			_elements = arr.concat(_elements);
		}else if(index == _elements.length){
			_elements = _elements.concat(arr);
		}else{
			var right:Array = _elements.splice(index);
			_elements = _elements.concat(arr);
			_elements = _elements.concat(right);
		}
		fireIntervalAdded(this, index, index+arr.length-1);
	}
	
	/**
	 * Notice the listeners the specified obj's value changed.
	 */
	public function valueChanged(obj:*):void{
		valueChangedAt(indexOf(obj));
	}
	
	/**
	 * Notice the listeners the specified obj's value changed.
	 */
	public function valueChangedAt(index:int):void{
		if(index>=0 && index<_elements.length){
			fireContentsChanged(this, index, index, []);
		}
	}
	
	/**
	 * Notice the listeners the specified range values changed.
	 * [from, to](include "from" and "to").
	 */
	public function valueChangedRange(from:int, to:int):void{
		fireContentsChanged(this, from, to, []);
	}
	
	public function removeAt(index:int):*{
		if(index < 0 || index >= size()){
			return null;
		}
		var obj:* = _elements[index];
		_elements.splice(index, 1);
		fireIntervalRemoved(this, index, index, [obj]);
		return obj;
	}
	
	public function remove(obj:*):*{
		var i:int = indexOf(obj);
		if(i>=0){
			return removeAt(i);
		}else{
			return null;
		}
	}	
	
	/**
	 * Removes from this List all of the elements whose index is between fromIndex, 
	 * and toIndex(both inclusive). Shifts any succeeding elements to the left (reduces their index). 
	 * This call shortens the ArrayList by (toIndex - fromIndex) elements. (If toIndex==fromIndex, 
	 * this operation has no effect.) 
	 * @return the elements were removed from the vector
	 */
	public function removeRange(fromIndex:int, toIndex:int):Array{
		if(_elements.length > 0){
			fromIndex = Math.max(0, fromIndex);
			toIndex = Math.min(toIndex, _elements.length-1);
			if(fromIndex > toIndex){
				return [];
			}else{
				var removed:Array = _elements.splice(fromIndex, toIndex-fromIndex+1);
				fireIntervalRemoved(this, fromIndex, toIndex, removed);
				return removed;
			}
		}else{
			return [];
		}
	}
	
	/**
	 * @see #removeAt()
	 */
	public function removeElementAt(index:int):void{
		removeAt(index);
	}
		
	/**
	 * @see #append()
	 */
	public function insertElementAt(item:*, index:int):void{
		append(item, index);
	}
	
	public function indexOf(obj:*):int{
		for(var i:int = 0; i<_elements.length; i++){
			if(_elements[i] == obj){
				return i;
			}
		}
		return -1;
	}
	
	public function contains(obj:*):Boolean{
		return indexOf(obj) >=0;
	}
	
	public function appendList(list:List, index:int=-1) : void {
		appendAll(list.toArray(), index);
	}

	public function pop():* {
		if(size() > 0){
			return removeAt(size()-1);
		}else{
			return null;
		}
	}

	public function shift():* {
		if(size() > 0){
			return removeAt(0);
		}else{
			return null;
		}
	}
	
	
	public function first():*{
		return _elements[0];
	}
	
	public function last():*{
		return _elements[_elements.length - 1];
	}
	
	public function size():int{
		return _elements.length;
	}

	public function isEmpty():Boolean{
		return _elements.length <= 0;
	}	
	
	/**
	 * Implemented ListMode
	 */
	public function getSize():int{
		return size();
	}
	
	public function clear():void{
		var ei:int = size() - 1;
		if(ei >= 0){
			var temp:Array = toArray();
			_elements.splice(0);
			fireIntervalRemoved(this, 0, ei, temp);
		}
	}
	
	public function toArray():Array{
		return _elements.concat();;
	}
	
	/**
	 * Returns a array that contains elements start with startIndex and has length elements.
	 * @param startIndex the element started index(include)
	 * @param length length of the elements, if there is not enough elements left, return the elements ended to the end of the vector.
	 */
	public function subArray(startIndex:int, length:int):Array{
		if(size() == 0 || length <= 0){
			return new Array();
		}
		return _elements.slice(startIndex, Math.min(startIndex+length, size()));
	}	
		
	public function sort(compare:Object, options:int):Array{
		var returned:Array = _elements.sort(compare, options);
		fireContentsChanged(this, 0, _elements.length-1, []);
		return returned;
	}
	
	public function sortOn(key:Object, options:int):Array{
		var returned:Array = _elements.sortOn(key, options);
		fireContentsChanged(this, 0, _elements.length-1, []);
		return returned;
	}
	
	public function toString():String{
		return "VectorListModel : " + _elements.toString();
	}
}
}