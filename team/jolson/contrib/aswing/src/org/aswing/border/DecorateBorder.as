/*
 Copyright aswing.org, see the LICENCE.txt.
*/


import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Insets;

/**
 * DecorateBorder make your border can represented as many border arounded.
 * <p>
 * <b>Note:You should only need to override:</b>
 * <ul>
 * <li><code>paintBorderImp</code>
 * <li><code>uninstallBorderImp</code>
 * <li><code>getBorderInsetsImp</code>
 * </ul>
 * methods in sub-class generally.
 * 
 * @author iiley
 */
class org.aswing.border.DecorateBorder implements Border{

	private var interior:Border;
	
	public function DecorateBorder(interior:Border){
		this.interior = interior;
	}
			
	/**
	 * Sets new interior border.
	 * @param interior the new interior border
	 */
	public function setInterior(interior:Border):Void {
		this.interior = interior;	
	}

	/**
	 * Returns current interior border.
	 * @return current interior border
	 */
	public function getInterior(Void):Border {
		return interior;	
	}
			
	/**
	 * Override this method in sub-class to draw border on the specified mc.
	 * @param c the component for which this border is being painted 
	 * @param g the paint graphics
	 * @param bounds the bounds of border
	 */
	public function paintBorderImp(com:Component, g:Graphics, bounds:Rectangle):Void{
	}
	
	/**
	 * Override this method in sub-class to clear and remove the border things you 
	 * created for this component.
	 * @param com
	 * @see #uninstallBorder
	 */
	public function uninstallBorderImp(com:Component):Void{
	}	
    
    /**
     * You should override this method to count this border's insets.
     * @see #getBorderInsets
     */
    public function getBorderInsetsImp(c:Component, bounds:Rectangle):Insets{
    	return new Insets();
    }
	
	/**
	 * call <code>super.paintBorder</code> paint the border first and then 
	 * paint the interior border on the interior bounds.
	 * <br>
	 * Note:subclass should not override this method, should override paintBorderImp.
	 * @see #paintBorderImp
	 */
    public function paintBorder(c:Component, g:Graphics, bounds:Rectangle):Void{
    	paintBorderImp(c, g, bounds);
    	//then paint interior border
    	if(getInterior() != null){
    		var interiorBounds:Rectangle = getBorderInsetsImp(c, bounds).getInsideBounds(bounds);
    		getInterior().paintBorder(c, g, interiorBounds);
    	}
    }
    
   
	/**
	 * call <code>super.uninstallBorder</code> unpaint the border first and then 
	 * uninstall the interior border .
	 * <br>
	 * Note:subclass should not override this method, should override paintBorderImp.
	 * @see #uninstallBorderImp
	 */
	public function uninstallBorder(c:Component):Void{
    	uninstallBorderImp(c);
    	//then unistall interior border
    	if(getInterior() != null){
    		getInterior().uninstallBorder(c);
    	}
	}
    

    /**
     * Returns the insets of the border.<br>
     * Note:subclass should not override this method, should override getBorderInsetsImp.
     * @see #getBorderInsetsImp
     * @param c the component for which this border insets value applies
     * @param bounds the bounds of the border would paint in.
     */
    public function getBorderInsets(c:Component, bounds:Rectangle):Insets{
    	var insets:Insets = getBorderInsetsImp(c, bounds);
    	if(getInterior() != null){
    		var interiorBounds:Rectangle = insets.getInsideBounds(bounds);
    		insets.addInsets(getInterior().getBorderInsets(c, interiorBounds));
    	}
    	return insets;
    }
}
