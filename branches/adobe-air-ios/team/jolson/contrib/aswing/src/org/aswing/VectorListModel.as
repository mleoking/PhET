/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractListModel;
import org.aswing.MutableListModel;
import org.aswing.util.List;

/**
 * The mutable list model vector implementation.
 * @author iiley
 */
class org.aswing.VectorListModel extends AbstractListModel implements MutableListModel, List{
	
	private var array:Array;
	
	/**
	 * VectorListMode(array:Array) the array to be the data in vector<br>
	 * VectorListMode() create a new array to be the data in vector<br>
	 */
	public function VectorListModel(array:Array){
		if(array == undefined){
			this.array = new Array();
		}else{
			this.array = array;
		}
	}
	
	public function get(i:Number){
		return array[i];
	}
	
	/**
	 * implemented ListMode
	 */
	public function getElementAt(i:Number):Object{
		return get(i);
	}
	
	public function append(obj, index:Number):Void{
		if(index == undefined){
			index = array.length;
			array.push(obj);
		}else{
			array.splice(index, 0, obj);
		}
		fireIntervalAdded(this, index, index);
	}
	
	public function replaceAt(index:Number, element){
		if(index<0 || index>= size()){
			return null;
		}
		var oldObj:Object = array[index];
		array[index] = element;
		fireContentsChanged(this, index, index, [oldObj]);
		return oldObj;
	}	
	
	public function appendAll(arr:Array, index:Number):Void{
		if(arr == undefined || arr.length <= 0){
			return;
		}
		if(index == undefined){
			index = array.length;
		}
		if(index == 0){
			array = arr.concat(array);
		}else if(index == array.length){
			array = array.concat(arr);
		}else{
			var right:Array = array.splice(index);
			array = array.concat(arr);
			array = array.concat(right);
		}
		fireIntervalAdded(this, index, index+arr.length-1);
	}
	
	/**
	 * notice the listeners the specified obj's value changed.
	 */
	public function valueChanged(obj:Object):Void{
		valueChangedAt(indexOf(obj));
	}
	
	/**
	 * notice the listeners the specified obj's value changed.
	 */
	public function valueChangedAt(index:Number):Void{
		if(index>=0 && index<array.length){
			fireContentsChanged(this, index, index, []);
		}
	}
	
	/**
	 * notice the listeners the specified range values changed.
	 * [from, to](include "from" and "to").
	 */
	public function valueChangedRange(from:Number, to:Number):Void{
		fireContentsChanged(this, from, to, []);
	}
	
	public function removeAt(index:Number){
		if(index < 0 || index >= size()){
			return null;
		}
		var obj:Object = array[index];
		array.splice(index, 1);
		fireIntervalRemoved(this, index, index, [obj]);
		return obj;
	}
	
	public function remove(obj){
		var i:Number = indexOf(obj);
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
	public function removeRange(fromIndex:Number, toIndex:Number):Array{
		if(array.length > 0){
			fromIndex = Math.max(0, fromIndex);
			toIndex = Math.min(toIndex, array.length-1);
			if(fromIndex > toIndex){
				return [];
			}else{
				var removed:Array = array.splice(fromIndex, toIndex-fromIndex+1);
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
	public function removeElementAt(index:Number):Void{
		removeAt(index);
	}
		
	/**
	 * @see #append()
	 */
	public function insertElementAt(item:Object, index:Number):Void{
		append(item, index);
	}
	
	public function indexOf(obj):Number{
		for(var i:Number = 0; i<array.length; i++){
			if(array[i] == obj){
				return i;
			}
		}
		return -1;
	}
	
	public function contains(obj):Boolean{
		return indexOf(obj) >=0;
	}
	
	public function appendList(list : List, index : Number) : Void {
		appendAll(list.toArray(), index);
	}

	public function pop() {
		if(size() > 0){
			return removeAt(size()-1);
		}else{
			return undefined;
		}
	}

	public function shift() {
		if(size() > 0){
			return removeAt(0);
		}else{
			return undefined;
		}
	}
	
	
	public function first(){
		return array[0];
	}
	
	public function last(){
		return array[array.length - 1];
	}
	
	public function size():Number{
		return array.length;
	}
	

	public function isEmpty():Boolean{
		return array.length <= 0;
	}	
	
	/**
	 * Implemented ListMode
	 */
	public function getSize():Number{
		return size();
	}
	
	public function clear():Void{
		var ei:Number = size() - 1;
		if(ei >= 0){
			var temp:Array = toArray();
			array.splice(0);
			fireIntervalRemoved(this, 0, ei, temp);
		}
	}
	
	public function toArray():Array{
		return array.concat();;
	}
	
	/**
	 * Returns a array that contains elements start with startIndex and has length elements.
	 * @param startIndex the element started index(include)
	 * @param length length of the elements, if there is not enough elements left, return the elements ended to the end of the vector.
	 */
	public function subArray(startIndex:Number, length:Number):Array{
		if(size() == 0 || length <= 0){
			return new Array();
		}
		return array.slice(startIndex, Math.min(startIndex+length, size()));
	}	
		
	public function sort(compare:Object, options:Number):Array{
		var returned:Array = array.sort(compare, options);
		fireContentsChanged(this, 0, array.length-1, []);
		return returned;
	}
	
	public function sortOn(key:Object, options:Number):Array{
		var returned:Array = array.sortOn(key, options);
		fireContentsChanged(this, 0, array.length-1, []);
		return returned;
	}
	
	public function toString():String{
		return "VectorListModel : " + array.toString();
	}
}
