/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{

/**
 * @author iiley
 */
public class Resizable3Imp1 implements Resizable3{
	
	private var cm:TableColumnModel;
	private var inverse:Boolean;
		
	public function Resizable3Imp1(cm:TableColumnModel, inverse:Boolean){
		this.cm = cm;
		this.inverse = inverse;
	}
	
    public function getElementCount():int{ 
    	return cm.getColumnCount(); 
    }
    
    public function getLowerBoundAt(i:int):int{ 
    	return cm.getColumn(i).getMinWidth(); 
    }
    
    public function getUpperBoundAt(i:int):int{ 
    	return cm.getColumn(i).getMaxWidth(); 
    }
    
    public function getMidPointAt(i:int):int{
        if (!inverse) {
	    	return cm.getColumn(i).getPreferredWidth();
        }else {
	    	return cm.getColumn(i).getWidth();
        }
    }
    
    public function setSizeAt(s:int, i:int):void{
        if (!inverse) {
			cm.getColumn(i).setWidth(s);
        }else {
			cm.getColumn(i).setPreferredWidth(s);
        }
    }
}
}