/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{
	
/**
 * @author iiley
 */
public class Resizable2Imp1 implements Resizable2{
	
	private var r:Resizable3;
	private var flag:Boolean;
	
	public function Resizable2Imp1(r:Resizable3, flag:Boolean){
		this.r = r;
		this.flag = flag;
	}
	
    public function getElementCount():int      { 
    	return r.getElementCount(); 
    }
    
    public function getLowerBoundAt(i:int):int { 
    	if(flag){
    		return r.getLowerBoundAt(i);
    	}else{
    		return r.getMidPointAt(i);
    	}
    }
    
    public function getUpperBoundAt(i:int):int { 
    	if(flag){
    		return r.getMidPointAt(i);
    	}else{
    		return r.getUpperBoundAt(i);
    	} 
    }
    
    public function setSizeAt(newSize:int, i:int):void { 
    	r.setSizeAt(newSize, i); 
    }
}
}