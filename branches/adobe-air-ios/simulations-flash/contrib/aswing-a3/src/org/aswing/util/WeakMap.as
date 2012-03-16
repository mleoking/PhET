package org.aswing.util{

import flash.utils.Dictionary;

/**
 * A map that both key and value are weaks.
 */
public class WeakMap{
	
	private var dic:Dictionary;
	
	public function WeakMap(){
		super();
		dic = new Dictionary(true);
	}
	
	public function put(key:*, value:*):void{
		var wd:Dictionary = new Dictionary(true);
		wd[value] = null;
		dic[key] = wd;
	}
	
	public function getValue(key:*):*{
		var wd:Dictionary = dic[key];
		if(wd){
			for(var v:* in wd){
				return v;
			}
		}
		return null;
	}
	
	public function remove(key:*):*{
		var value:* = getValue(key);
		delete dic[key];
		return value;
	}
}
}