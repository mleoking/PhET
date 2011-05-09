/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.dnd{

/**
 * The drag source data.
 * @author iiley
 */
public class SourceData{
	
	private var name:String;
	private var data:*;
	
	public function SourceData(name:String, data:*){
		this.name = name;
		this.data = data;
	}
	
	public function getName():String{
		return name;
	}
	
	public function getData():*{
		return data;
	}	
}
}