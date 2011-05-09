/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{
	
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;
import org.aswing.util.HashMap;

/**
 * A table of defaults for AsWing components.  Applications can set/get
 * default values via the <code>UIManager</code>.
 * 
 * @see UIManager
 * @author iiley
 */
public class UIDefaults extends HashMap
{
	public function UIDefaults()
	{
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
 	override public function put(key:*, value:*):*{
 		var oldValue:* = (value == null) ? super.remove(key) : super.put(key, value);
 		return oldValue;
 	}
 	
	/**
     * Puts all of the key/value pairs in the database.
     * @param keyValueList  an array of key/value pairs
     * @see #put()
     * @see org.aswing.utils.Hashtable#put()
     */
	public function putDefaults(keyValueList:Array):void{
		for(var i:Number = 0; i < keyValueList.length; i += 2) {
            var value:* = keyValueList[i + 1];
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
		var ui:ComponentUI = getInstance(target.getUIClassID()) as ComponentUI;
		if(ui == null){
			ui = getCreateInstance(target.getDefaultBasicUIClass()) as ComponentUI;
		}
		return ui;
	}
	
	public function getBoolean(key:String):Boolean{
		return (this.get(key) == true);
	}
	
	public function getNumber(key:String):Number{
		return this.get(key) as Number;
	}
	
	public function getInt(key:String):int{
		return this.get(key) as int;
	}
	
	public function getUint(key:String):uint{
		return this.get(key) as uint;
	}
	
	public function getString(key:String):String{
		return this.get(key) as String;
	}
	
	public function getBorder(key:String):Border{
		var border:Border = getInstance(key) as Border;
		if(border == null){
			border = EmptyUIResources.BORDER; //make it to be an ui resource then can override by next LAF
		}
		return border;
	}
	
	public function getIcon(key:String):Icon{
		var icon:Icon = getInstance(key) as Icon;
		if(icon == null){
			icon = EmptyUIResources.ICON; //make it to be ui resource property then can override by next LAF
		}
		return icon;
	}
	
	public function getGroundDecorator(key:String):GroundDecorator{
		var dec:GroundDecorator = getInstance(key) as GroundDecorator;
		if(dec == null){
			dec = EmptyUIResources.DECORATOR; //make it to be ui resource property then can override by next LAF
		}
		return dec;
	}
	
	public function getColor(key:String):ASColor{
		var color:ASColor = getInstance(key) as ASColor;
		if(color == null){
			color = EmptyUIResources.COLOR; //make it to be an ui resource then can override by next LAF
		}
		return color;
	}
	
	public function getFont(key:String):ASFont{
		var font:ASFont = getInstance(key) as ASFont;
		if(font == null){
			font = EmptyUIResources.FONT; //make it to be an ui resource then can override by next LAF
		}
		return font;
	}
	
	public function getInsets(key:String):Insets{
		var i:Insets = getInstance(key) as Insets;
		if(i == null){
			i = EmptyUIResources.INSETS; //make it to be an ui resource then can override by next LAF
		}
		return i;
	}	
	
	//-------------------------------------------------------------
	public function getConstructor(key:String):Class{
		return this.get(key) as Class;
	}
	
	public function getInstance(key:String):*{
		var value:* = this.get(key);
		if(value is Class){
			return getCreateInstance(value as Class);
		}else{
			return value;
		}
	}
	
	private function getCreateInstance(constructor:Class):Object{
		return new constructor();
	}
}

}