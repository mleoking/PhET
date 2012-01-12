/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.util
{
	
import flash.utils.Dictionary;
	
/**
 * A collection that contains no duplicate elements. More formally, 
 * sets contain no pair of elements e1 and e2 such that e1 === e2.
 * @author iiley
 */
public class HashSet
{
	
	private var container:Dictionary;
	private var length:int;
	
	public function HashSet(){
		container = new Dictionary();
		length = 0;
	}
	
	public function size():int{
		return length;
	}
	
	public function add(o:*):void{
		if(!contains(o)){
			length++;
		}
		container[o] = o;
	}
	
	public function contains(o:*):Boolean{
		return container[o] !== undefined;
	}
	
	public function isEmpty():Boolean{
		return length == 0;
	}
	
	public function remove(o:*):Boolean{
		if(contains(o)){
			delete container[o];
			length--;
			return true;
		}else{
			return false;
		}
	}
	
	public function clear():void{
		container = new Dictionary();
		length = 0;
	}
	
	public function addAll(arr:Array):void{
		for each(var i:* in arr){
			add(i);
		}
	}
	
	public function removeAll(arr:Array):void{
		for each(var i:* in arr){
			remove(i);
		}
	}
	
	public function containsAll(arr:Array):Boolean{
		for(var i:int=0; i<arr.length; i++){
			if(!contains(arr[i])){
				return false;
			}
		}
		return true;
	}
	
	public function each(func:Function):void{
		for each(var i:* in container){
			func(i);
		}
	}
	
	public function toArray():Array{
		var arr:Array = new Array(length);
		var index:int = 0;
		for each(var i:* in container){
			arr[index] = i;
			index ++;
		}
		return arr;
	}
}

}