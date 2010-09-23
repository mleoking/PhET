/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{

import org.aswing.*;
import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.basic.BasicLookAndFeel;
	
/**
 * This class keeps track of the current look and feel and its defaults.
 * 
 * <p>We manage three levels of defaults: user defaults, 
 * look and feel defaults, system defaults. A call to UIManager.get 
 * checks all three levels in order and returns the first non-null 
 * value for a key, if any. A call to UIManager.put just affects the 
 * user defaults. Note that a call to setLookAndFeel doesn't affect 
 * the user defaults, it just replaces the middle defaults "level". 
 * @author iiley
 */	
public class UIManager
{

	private static var lookAndFeelDefaults:UIDefaults;
	private static var lookAndFeel:LookAndFeel;
	
	/**
	 * @see org.aswing.ASWingUtils#updateAllComponentUI()
	 */
	public static function setLookAndFeel(laf:LookAndFeel):void{
		lookAndFeel = laf;
		setLookAndFeelDefaults(laf.getDefaults());
	}
	
	public static function getLookAndFeel():LookAndFeel{
		checkLookAndFeel();
		return lookAndFeel;
	}
	
	public static function getDefaults():UIDefaults{
		return getLookAndFeelDefaults();
	}
	
	public static function getLookAndFeelDefaults():UIDefaults{
		checkLookAndFeel();
		return lookAndFeelDefaults;
	}
	
	private static function setLookAndFeelDefaults(d:UIDefaults):void{
		lookAndFeelDefaults = d;
	}
	
	private static function checkLookAndFeel():void{
		if(lookAndFeel == null){
			setLookAndFeel(new BasicLookAndFeel());
		}
	}
	
	public static function containsKey(key:String):Boolean{
		return getDefaults().containsKey(key);
	}
	
	public static function get(key:String):*{
		return getDefaults().get(key);
	}
	
	public static function getUI(target:Component):ComponentUI{
		return getDefaults().getUI(target);
	}
	
	public static function getBoolean(key:String):Boolean{
		return getDefaults().getBoolean(key);
	}
	
	public static function getNumber(key:String):Number{
		return getDefaults().getNumber(key);
	}
	
	public static function getInt(key:String):int{
		return getDefaults().getInt(key);
	}
	
	public static function getUint(key:String):uint{
		return getDefaults().getUint(key);
	}
	
	public static function getString(key:String):String{
		return getDefaults().getString(key);
	}
	
	public static function getBorder(key:String):Border{
		return getDefaults().getBorder(key);
	}

	public static function getGroundDecorator(key:String):GroundDecorator{
		return getDefaults().getGroundDecorator(key);
	}	
	
	public static function getColor(key:String):ASColor{
		return getDefaults().getColor(key);
	}
	
	public static function getFont(key:String):ASFont{
		return getDefaults().getFont(key);
	}
	
	public static function getIcon(key:String):Icon{
		return getDefaults().getIcon(key);
	}
	
	public static function getInsets(key:String):Insets{
		return getDefaults().getInsets(key);
	}
	
	public static function getInstance(key:String):*{
		return getDefaults().getInstance(key);
	}
	
	public static function getClass(key:String):Class{
		return getDefaults().getConstructor(key);
	}
}

}