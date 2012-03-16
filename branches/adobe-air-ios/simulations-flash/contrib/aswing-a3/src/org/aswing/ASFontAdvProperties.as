/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.text.AntiAliasType;
import flash.text.GridFitType;
import flash.text.TextField;
	

/**
 * The advanced properties for font.
 * @see flash.text.TextField
 * @see flash.text.AntiAliasType
 * @see flash.text.GridFitType
 * @author iiley
 */
public class ASFontAdvProperties{
	
	
 	private var antiAliasType:String;
 	private var gridFitType:String;
 	private var sharpness:Number;
 	private var thickness:Number;
 	private var embedFonts:Boolean;
 	
	public function ASFontAdvProperties(
		embedFonts:Boolean=false, antiAliasType:String="normal", 
		gridFitType:String="pixel", sharpness:Number=0, thickness:Number=0){
		this.embedFonts = embedFonts;
		this.antiAliasType = antiAliasType;
		this.gridFitType = gridFitType;
		this.sharpness = sharpness;
		this.thickness = thickness;
	}
	
	public function getAntiAliasType():String{
		return antiAliasType;
	}
	
	public function changeAntiAliasType(newType:String):ASFontAdvProperties{
		return new ASFontAdvProperties(embedFonts, newType, gridFitType, sharpness, thickness);
	}
	
	public function getGridFitType():String{
		return gridFitType;
	}
	
	public function changeGridFitType(newType:String):ASFontAdvProperties{
		return new ASFontAdvProperties(embedFonts, antiAliasType, newType, sharpness, thickness);
	}
	
	public function getSharpness():Number{
		return sharpness;
	}
	
	public function changeSharpness(newSharpness:Number):ASFontAdvProperties{
		return new ASFontAdvProperties(embedFonts, antiAliasType, gridFitType, newSharpness, thickness);
	}
	
	public function getThickness():Number{
		return thickness;
	}
	
	public function changeThickness(newThickness:Number):ASFontAdvProperties{
		return new ASFontAdvProperties(embedFonts, antiAliasType, gridFitType, sharpness, newThickness);
	}
	
	public function isEmbedFonts():Boolean{
		return embedFonts;
	}
	
	public function changeEmbedFonts(ef:Boolean):ASFontAdvProperties{
		return new ASFontAdvProperties(ef, antiAliasType, gridFitType, sharpness, thickness);
	}	
	
	/**
	 * Applys the properties to the specified text field.
	 * @param textField the text filed to be applied font.
	 */
	public function apply(textField:TextField):void{
		textField.embedFonts = isEmbedFonts();
		textField.antiAliasType = getAntiAliasType();
		textField.gridFitType = getGridFitType();
		textField.sharpness = getSharpness();
		textField.thickness = getThickness();
	}
	
	public function toString():String{
		return "ASFontAdvProperties[" 
			+ "embedFonts : " + embedFonts 
			+ ", antiAliasType : " + antiAliasType 
			+ ", gridFitType : " + gridFitType 
			+ ", sharpness : " + sharpness 
			+ ", thickness : " + thickness 
			+ "]";
	}
}
}