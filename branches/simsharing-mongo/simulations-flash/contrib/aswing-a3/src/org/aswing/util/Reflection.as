/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.util{
	
import flash.utils.getQualifiedClassName;
import flash.system.ApplicationDomain;
import flash.display.DisplayObject;

public class Reflection{
	
	public static function createDisplayObjectInstance(fullClassName:String, applicationDomain:ApplicationDomain=null):DisplayObject{
		return createInstance(fullClassName, applicationDomain) as DisplayObject;
	}
	
	public static function createInstance(fullClassName:String, applicationDomain:ApplicationDomain=null):*{
		var assetClass:Class = getClass(fullClassName, applicationDomain);
		if(assetClass != null){
			return new assetClass();
		}
		return null;		
	}
	
	public static function getClass(fullClassName:String, applicationDomain:ApplicationDomain=null):Class{
		if(applicationDomain == null){
			applicationDomain = ApplicationDomain.currentDomain;
		}
		var assetClass:Class = applicationDomain.getDefinition(fullClassName) as Class;
		return assetClass;		
	}
	
	public static function getFullClassName(o:*):String{
		return getQualifiedClassName(o);
	}
	
	public static function getClassName(o:*):String{
		var name:String = getFullClassName(o);
		var lastI:int = name.lastIndexOf(".");
		if(lastI >= 0){
			name = name.substr(lastI+1);
		}
		return name;
	}
	
	public static function getPackageName(o:*):String{
		var name:String = getFullClassName(o);
		var lastI:int = name.lastIndexOf(".");
		if(lastI >= 0){
			return name.substring(0, lastI);
		}else{
			return "";
		}
	}
}

}