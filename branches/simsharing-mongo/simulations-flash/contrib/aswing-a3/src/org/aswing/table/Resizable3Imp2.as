/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{

/**
 * @author iiley
 */
public class Resizable3Imp2 implements Resizable3{
	
	private var cm:TableColumnModel;
	private var start:int;
	private var end:int;
		
	public function Resizable3Imp2(cm:TableColumnModel, start:int, end:int){
		this.cm = cm;
		this.start = start;
		this.end = end;
	}
	
    public function getElementCount():int{ 
    	return end-start;
    }
    
    public function getLowerBoundAt(i:int):int{ 
    	return cm.getColumn(i+start).getMinWidth(); 
    }
    
    public function getUpperBoundAt(i:int):int{ 
    	return cm.getColumn(i+start).getMaxWidth(); 
    }
    
    public function getMidPointAt(i:int):int{ 
    	return cm.getColumn(i+start).getWidth(); 
    }
    
    public function setSizeAt(s:int, i:int):void {
    	cm.getColumn(i+start).setWidth(s); 
    }
}
}