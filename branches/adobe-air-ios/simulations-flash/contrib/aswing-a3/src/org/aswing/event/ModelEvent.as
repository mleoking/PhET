/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event{

/**
 * The base class for model events.
 * @author iiley
 */
public class ModelEvent{
	
	private var source:Object;
	
	public function ModelEvent(source:Object){
		this.source = source;
	}
	
	public function getSource():Object{
		return source;
	}
}
}