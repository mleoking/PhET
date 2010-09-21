/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.plaf.ComponentUI;
import org.aswing.util.HashMap;
import org.aswing.util.Reflection;

/**
 * A table of defaults for Swing components.  Applications can set/get
 * default values via the <code>UIManager</code>.
 * 
 * @see UIManager
 * @author iiley
 */
class org.aswing.UIDefaults extends HashMap{
	
	private static var CREATE_METHOD_NAME:String = "createInstance";
	
	
	public function UIDefaults() {
		super();
	}
	
    /**
     * Sets the value of <code>key</code> to <code>value</code>.
     * If value is <code>null</code>, the key is removed from the table.
     *
     * @param key    the unique <code>Object</code> who's value will be used
     *          to retrieve the data value associated with it
     * @param value  the new <code>Object</code> to store as data under
     *		that key
     * @return the previous <code>Object</code> value, or <code>null</code>
     * @see #putDefaults()
     * @see org.aswing.utils.HashMap#put()
     */	
 	public function put(key:String, value:Object){
 		var oldValue = (value == null) ? super.remove(key) : super.put(key, value);
 		return oldValue;
 	}
 	
	/**
     * Puts all of the key/value pairs in the database.
     * @param keyValueList  an array of key/value pairs
     * @see put
     * @see org.aswing.utils.Hashtable#put
     */
	public function putDefaults(keyValueList:Array):Void{
		for(var i:Number = 0; i < keyValueList.length; i += 2) {
            var value = keyValueList[i + 1];
            if (value == null) {
                super.remove(keyValueList[i]);
            }else {
                super.put(keyValueList[i], value);
            }
        }
	}
	
	/**
	 * Returns the component LookAndFeel specified UI object
	 * @return target's UI object, or null if there is not his UI object
	 */
	public function getUI(target:Component):ComponentUI{
		var ui:ComponentUI = ComponentUI(getInstance(target.getUIClassID()));
		if(ui == null){
			ui = ComponentUI(getCreateInstance(target.getDefaultBasicUIClass()));
		}
		return ui;
	}
	
	public function getBoolean(key:String):Boolean{
		return (get(key) == true);
	}
	
	public function getNumber(key:String):Number{
		return get(key);
	}
	
	public function getBorder(key:String):Border{
		var border:Border = Border(getInstance(key));
		if(border == null){
			border = undefined; //make it to be an undefined property then can override by next LAF
		}
		return border;
	}
	
	public function getIcon(key:String):Icon{
		var icon:Icon = Icon(getInstance(key));
		if(icon == null){
			icon = undefined; //make it to be an undefined property then can override by next LAF
		}
		return icon;
	}
	
	public function getColor(key:String):ASColor{
		var color:ASColor = ASColor(getInstance(key));
		if(color == null){
			color = undefined; //make it to be an undefined property then can override by next LAF
		}
		return color;
	}
	
	public function getFont(key:String):ASFont{
		var font:ASFont = ASFont(getInstance(key));
		if(font == null){
			font = undefined; //make it to be an undefined property then can override by next LAF
		}
		return font;
	}
	
	public function getInsets(key:String):Insets{
		var i:Insets = Insets(getInstance(key));
		if(i == null){
			i = undefined; //make it to be an undefined property then can override by next LAF
		}
		return i;
	}	
	
	//-------------------------------------------------------------
	public function getConstructor(key:String):Function{
		return Function(this.get(key));
	}
	
	public function getInstance(key:String, args:Array):Object{
		var value = this.get(key);
		if(value instanceof Function){
			return getCreateInstance(Function(value), args);
		}else{
			return value;
		}
	}
	
	private function getCreateInstance(constructor:Function, args:Array):Object{
		var instance:Object = constructor[CREATE_METHOD_NAME].apply(null, args);
		if(instance == null){
			instance = new constructor();
		}
		return instance;
	}	
}
