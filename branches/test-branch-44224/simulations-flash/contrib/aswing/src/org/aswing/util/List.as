/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * An ordered collection (also known as a sequence). 
 * The user of this interface has precise control over where in the list each element is inserted. 
 * The user can access elements by their integer index (position in the list), and search for elements in the list.
 * 
 * @author iiley
 */
interface org.aswing.util.List {
	
	/**
	 * Returns the element at the specified position in this list.
	 * @param index index of element to return.
	 * @return the element at the specified position in this list. Undefined will be 
	 * returned if the index is out of range (index < 0 || index >= size()).
	 */
	public function get(index:Number);
	
	/**
	 * Inserts the specified element at the specified position in this list. 
	 * Shifts the element currently at that position (if any) and any subsequent elements to the 
	 * right (adds one to their indices). 
	 * <p>
	 * If index is omited, the element will be append to the end of the list.
	 * @param element element to be inserted.
	 * @param index (optional)at which the specified element is to be inserted.
	 */
	public function append(element, index:Number):Void;
	
	/**
	 * Inserts all the elements in a array at the specified position in this list. 
	 * Shifts the element currently at that position (if any) and any subsequent elements to the 
	 * right (adds one to their indices). 
	 * <p>
	 * If index is omited, the elements will be append to the end of the list.
	 * @param arr arr of elements to be inserted.
	 * @param index (optional)at which the elements is to be inserted.
	 */
	public function appendAll(arr:Array, index:Number):Void;
	
	/**
	 * Inserts all the elements in a list at the specified position in this list. 
	 * Shifts the element currently at that position (if any) and any subsequent elements to the 
	 * right (adds one to their indices). 
	 * <p>
	 * If index is omited, the elements will be append to the end of the list.
	 * @param arr arr of elements to be inserted.
	 * @param index (optional)at which the elements is to be inserted.
	 */
	public function appendList(list:List, index:Number):Void;
	
	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 */
	public function replaceAt(index:Number, element);
	
	/**
	 * Removes the element at the specified position in this list. 
	 * Shifts any subsequent elements to the left (subtracts one from their indices). 
	 * Returns the element that was removed from the list.
	 * @param index the index of the element to removed.
	 * @return the element previously at the specified position. 
	 */
	public function removeAt(index:Number);
	
	/**
	 * Removes the first occurrence in this list of the specified element. 
	 * If this list does not contain the element, it is unchanged. 
	 * More formally, removes the element with the lowest index. 
	 * @param element element to be removed from this list, if present.
	 * @return the element removed or undefined there is not such element in the list.
	 */
	public function remove(element);
	
	/**
	 * Removes the elements at the specified index range.
	 * Shifts any subsequent elements to the left (subtracts one from their indices). 
	 * Returns the elements in a Array that were removed from the list.
	 * @param fromIndex the beginning index to be removed(include).
	 * @param fromIndex the ending index to be removed(include).
	 * @return the elements were removed.
	 */
	public function removeRange(fromIndex:Number, toIndex:Number):Array;
	
	/**
	 * Returns the index in this list of the first occurrence of the specified element, 
	 * or -1 if this list does not contain this element. 
	 * More formally, returns the lowest index.
	 * @param element element to search for.
	 * @return the index in this list of the first occurrence of the specified element, 
	 * or -1 if this list does not contain this element
	 */
	public function indexOf(element):Number;
	
	/**
	 * Returns true if this list contains the specified element.
	 * @param element element whose presence in this list is to be tested.
	 * @return true if this list contains the specified element.
	 */
	public function contains(element):Boolean;
	
	/**
	 * Returns the first element in the list. 
	 * Undefined will be return if there is not any elements in the list.
	 * @return the first element in the list. 
	 */
	public function first();
	
	/**
	 * Returns the last element in the list. 
	 * Undefined will be return if there is not any elements in the list.
	 * @return the last element in the list. 
	 */
	public function last();
	
	/**
	 * Removes and return the last element in the list. 
	 * Undefined will be return if there is not any elements in the list.
	 * @return the last element in the list. 
	 */
	public function pop();
	
	/**
	 * Removes and return the first element in the list. 
	 * Undefined will be return if there is not any elements in the list.
	 * @return the first element in the list. 
	 */
	public function shift();
	
	/**
	 * Returns the number of elements in this list.
	 * @return the number of elements in this list.
	 */
	public function size():Number;
	
	/**
	 * Removes all of the elements from this list. 
	 * This list will be empty after this call returns. 
	 */
	public function clear():Void;
	
	/**
	 * Returns true if this list contains no elements.
	 * @return true if this list contains no elements.
	 */
	public function isEmpty():Boolean;
	
	/**
	 * Returns an array containing all of the elements in this list in proper sequence.
	 * @return an array containing all of the elements in the list.
	 */
	public function toArray():Array;
}