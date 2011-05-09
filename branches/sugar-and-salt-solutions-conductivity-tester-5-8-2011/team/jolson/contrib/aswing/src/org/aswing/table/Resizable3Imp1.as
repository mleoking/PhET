/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.table.Resizable3;
import org.aswing.table.TableColumnModel;

/**
 * @author iiley
 */
class org.aswing.table.Resizable3Imp1 implements Resizable3{
	
	private var cm:TableColumnModel;
	private var inverse:Boolean;
		
	public function Resizable3Imp1(cm:TableColumnModel, inverse:Boolean){
		this.cm = cm;
		this.inverse = inverse;
	}
	
    public function  getElementCount():Number{ 
    	return cm.getColumnCount(); 
    }
    
    public function  getLowerBoundAt(i:Number):Number { 
    	return cm.getColumn(i).getMinWidth(); 
    }
    
    public function  getUpperBoundAt(i:Number):Number { 
    	return cm.getColumn(i).getMaxWidth(); 
    }
    
    public function  getMidPointAt(i:Number):Number  {
        if (!inverse) {
	    	return cm.getColumn(i).getPreferredWidth();
        }else {
	    	return cm.getColumn(i).getWidth();
        }
    }
    
    public function setSizeAt(s:Number, i:Number):Void {
        if (!inverse) {
			cm.getColumn(i).setWidth(s);
        }else {
			cm.getColumn(i).setPreferredWidth(s);
        }
    }
}