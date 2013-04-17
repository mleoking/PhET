/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.table.Resizable3;
import org.aswing.table.TableColumnModel;

/**
 * @author iiley
 */
class org.aswing.table.Resizable3Imp2 implements Resizable3{
	
	private var cm:TableColumnModel;
	private var start:Number;
	private var end:Number;
		
	public function Resizable3Imp2(cm:TableColumnModel, start:Number, end:Number){
		this.cm = cm;
		this.start = start;
		this.end = end;
	}
	
    public function getElementCount():Number{ 
    	return end-start; 
    }
    public function getLowerBoundAt(i:Number):Number  { 
    	return cm.getColumn(i+start).getMinWidth(); 
    }
    public function getUpperBoundAt(i:Number):Number  { 
    	return cm.getColumn(i+start).getMaxWidth(); 
    }
    public function getMidPointAt(i:Number):Number    { 
    	return cm.getColumn(i+start).getWidth(); 
    }
    public function setSizeAt(s:Number, i:Number):Void {
    	cm.getColumn(i+start).setWidth(s); 
    }
}