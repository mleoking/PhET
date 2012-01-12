/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import flash.text.TextField;
import flash.text.TextFormat;

import org.aswing.geom.IntDimension;

/**
 * Font that specified the font name, size, style and whether or not embed.
 * @author iiley
 */
public class ASFont{
	
 	private var name:String;
 	private var size:uint;
 	private var bold:Boolean;
 	private var italic:Boolean;
 	private var underline:Boolean;
 	private var textFormat:TextFormat;
 	
 	private var advancedProperties:ASFontAdvProperties;
 	
 	/**
 	 * Create a font.
 	 * @param embedFontsOrAdvancedPros a boolean to indicate whether or not embedFonts or 
 	 * 			a <code>ASFontAdvProperties</code> instance.
 	 * @see org.aswing.ASFontAdvProperties
 	 */
	public function ASFont(name:String="Tahoma", size:Number=11, bold:Boolean=false, italic:Boolean=false, underline:Boolean=false, 
		embedFontsOrAdvancedPros:*=null){
		this.name = name;
		this.size = size;
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		if(embedFontsOrAdvancedPros is ASFontAdvProperties){
			advancedProperties = embedFontsOrAdvancedPros as ASFontAdvProperties;
		}else{
			advancedProperties = new ASFontAdvProperties(embedFontsOrAdvancedPros==true);
		}
		textFormat = getTextFormat();
	}
	
	public function getName():String{
		return name;
	}
	
	public function changeName(name:String):ASFont{
		return new ASFont(name, size, bold, italic, underline, advancedProperties);
	}
	
	public function getSize():uint{
		return size;
	}
	
	public function changeSize(size:int):ASFont{
		return new ASFont(name, size, bold, italic, underline, advancedProperties);
	}
	
	public function isBold():Boolean{
		return bold;
	}
	
	public function changeBold(bold:Boolean):ASFont{
		return new ASFont(name, size, bold, italic, underline, advancedProperties);
	}
	
	public function isItalic():Boolean{
		return italic;
	}
	
	public function changeItalic(italic:Boolean):ASFont{
		return new ASFont(name, size, bold, italic, underline, advancedProperties);
	}
	
	public function isUnderline():Boolean{
		return underline;
	}
	
	public function changeUnderline(underline:Boolean):ASFont{
		return new ASFont(name, size, bold, italic, underline, advancedProperties);
	}
	
	public function isEmbedFonts():Boolean{
		return advancedProperties.isEmbedFonts();
	}
	
	public function getAdvancedProperties():ASFontAdvProperties{
		return advancedProperties;
	}
	
	/**
	 * Applys the font to the specified text field.
	 * @param textField the text filed to be applied font.
	 * @param beginIndex The zero-based index position specifying the first character of the desired range of text. 
	 * @param endIndex The zero-based index position specifying the last character of the desired range of text. 
	 */
	public function apply(textField:TextField, beginIndex:int=-1, endIndex:int=-1):void{
		advancedProperties.apply(textField);
		textField.setTextFormat(textFormat, beginIndex, endIndex);
		textField.defaultTextFormat = textFormat;
	}
	
	/**
	 * Return a new text format that contains the font properties.
	 * @return a new text format.
	 */
	public function getTextFormat():TextFormat{
		return new TextFormat(name, size, null, bold, italic, underline);
	}
	
	/**
	 * Computes text size with this font.
	 * @param text the text to be compute
	 * @includeGutters whether or not include the 2-pixels gutters in the result
	 * @return the computed size of the text
	 * @see org.aswing.AsWingUtils#computeStringSizeWithFont
	 */
	public function computeTextSize(text:String, includeGutters:Boolean=true):IntDimension{
		return AsWingUtils.computeStringSizeWithFont(this, text, includeGutters);
	}
	
	public function toString():String{
		return "ASFont[" 
			+ "name : " + name 
			+ ", size : " + size 
			+ ", bold : " + bold 
			+ ", italic : " + italic 
			+ ", underline : " + underline 
			+ ", advanced : " + advancedProperties 
			+ "]";
	}
}

}