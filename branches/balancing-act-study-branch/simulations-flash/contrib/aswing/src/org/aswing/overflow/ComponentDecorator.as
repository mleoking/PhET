/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.ElementCreater;
import org.aswing.Icon;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;
import org.aswing.util.MCUtils;
 
/**
 * ComponentDecorator, manage a decorator for a component. Creats MCs TextFields for a component.
 * One component's can only manage one decorator MC or TextFiled in one Components by a ComponentDecorator.
 * @author iiley
 */
class org.aswing.overflow.ComponentDecorator {
	
	public static function get creater():ElementCreater{
		return ElementCreater.getInstance();
	}
	//////////////////////////////////////Utils For Decorators//////////////////////////////////////
	/**	 
	 * This method add a listener to the component, make to do icon uninstallation 
	 * at next component painting time, this avoid the icon flicker before paint.
	 * <p>
	 * This function usually be used when you changed a icon for a component and then 
	 * call <code>repaint</code> to redraw. But if you call <code>repaintImmediately</code> 
	 * to redraw the component, you should not call this method, just call <code>Icon.uninstallIcon</code> 
	 * generally.
	 * @see org.aswing.Icon#uninstallIcon()
	 */
	public static function removeIconWhenNextPaint(com:Component, icon:Icon):Void{
		var listener:Object = new Object();
		listener[Component.ON_PAINT] = Delegate.create(ComponentDecorator, _removeIcon, icon, listener);
		com.addEventListener(listener);
	}
	private static function _removeIcon(source:Component, icon:Icon, listener:Object):Void{
		var com:Component = source;
		icon.uninstallIcon(com);
		com.removeEventListener(listener);
	}
	
	/**	 
	 * This method add a listener to the component, make to do border uninstallation 
	 * at next component painting time, this avoid the border flicker before paint.
	 * <p>
	 * This function usually be used when you changed a border for a component and then 
	 * call <code>repaint</code> to redraw. But if you call <code>repaintImmediately</code> 
	 * to redraw the component, you should not call this method, just call <code>Border.uninstallBorder</code> 
	 * generally.
	 * @see org.aswing.border.Border#uninstallBorder()
	 */
	public static function removeBorderWhenNextPaint(com:Component, border:Border):Void{
		var listener:Object = new Object();
		listener[Component.ON_PAINT] = Delegate.create(ComponentDecorator, _removeBorder, border, listener);
		com.addEventListener(listener);
	}
	private static function _removeBorder(source:Component, border:Border, listener:Object):Void{
		var com:Component = source;
		border.uninstallBorder(com);
		com.removeEventListener(listener);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	private var mcMap:HashMap;
	private var tfMap:HashMap;
	
	public function ComponentDecorator(){
		mcMap = new HashMap();
		tfMap = new HashMap();
	}
	
	public function createDecorateMC(c:Component):MovieClip{
		var mc:MovieClip = c.createMovieClip("decorator");
		if(MCUtils.isMovieClipExist(mc)){
			mcMap.put(c.getID(), mc);
			return mc;
		}else{
			return null;
		}
	}
	
	public function createDecorateTextField(c:Component):TextField{
		var tf:TextField = c.createTextField("decor_txt");
		if(MCUtils.isTextFieldExist(tf)){
			tfMap.put(c.getID(), tf);
			return tf;
		}else{
			return null;
		}
	}
	
	public function attachDecrateMC(c:Component, linkage:String):MovieClip{
		var mc:MovieClip = c.attachMovieClip(linkage, "deco_att");
		if(MCUtils.isMovieClipExist(mc)){
			mcMap.put(c.getID(), mc);
			return mc;
		}else{
			return null;
		}
	}
	
	public function getCreateDecorateMC(c:Component):MovieClip{
		var mc:MovieClip = getDecorateMC(c);
		if(!MCUtils.isMovieClipExist(mc)){
			return createDecorateMC(c);
		}else{
			return mc;
		}
	}
	
	public function getCreateDecorateTextField(c:Component):TextField{
		var tf:TextField = getDecorateTextField(c);
		if(tf == null || tf.toString()==null){
			return createDecorateTextField(c);
		}else{
			return tf;
		}
	}
	
	public function getAttachDecorateMC(c:Component, linkage:String):MovieClip{
		var mc:MovieClip = getDecorateMC(c);
		if(!MCUtils.isMovieClipExist(mc)){
			return attachDecrateMC(c, linkage);
		}else{
			return mc;
		}
	}	
	
	public function getDecorateMC(c:Component):MovieClip{
		return MovieClip(mcMap.get(c.getID()));
	}
	
	public function getDecorateTextField(c:Component):TextField{
		return TextField(tfMap.get(c.getID()));
	}
	
	public function removeDecorateMC(c:Component):Void{
		var mc:MovieClip = MovieClip(mcMap.remove(c.getID()));
		mc.unloadMovie();
		mc.removeMovieClip();
	}
	
	public function removeDecorateTextField(c:Component):Void{
		var tf:TextField = TextField(tfMap.remove(c.getID()));
		tf.unloadMovie();
		tf.removeMovieClip();
	}
		
	/**
	 * This casue to call <code>uninstallDecorateExtra</code> and 
	 * remove decorate mcs created for the component.
	 */
	public function uninstallDecorate(com:Component):Void{
		uninstallDecorateExtra(com);
		removeDecorateMC(com);
		removeDecorateTextField(com);
	}
	
	/**
	 * Override this method to do your uninstall in sub-class
	 */
	private function uninstallDecorateExtra(com:Component):Void{
	}
}
