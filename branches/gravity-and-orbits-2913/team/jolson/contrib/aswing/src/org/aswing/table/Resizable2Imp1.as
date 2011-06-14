/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.table.Resizable2;
import org.aswing.table.Resizable3;
/**
 * @author iiley
 */
class org.aswing.table.Resizable2Imp1 implements Resizable2{
	
	private var r:Resizable3;
	private var flag:Boolean;
	
	public function Resizable2Imp1(r:Resizable3, flag:Boolean){
		this.r = r;
		this.flag = flag;
	}
	
    public function getElementCount():Number      { 
    	return r.getElementCount(); 
    }
    public function getLowerBoundAt(i:Number):Number { 
    	if(flag){
    		return r.getLowerBoundAt(i);
    	}else{
    		return r.getMidPointAt(i);
    	}
    }
    public function getUpperBoundAt(i:Number):Number { 
    	if(flag){
    		return r.getMidPointAt(i);
    	}else{
    		return r.getUpperBoundAt(i);
    	} 
    }
    public function setSizeAt(newSize:Number, i:Number):Void { 
    	r.setSizeAt(newSize, i); 
    }
}