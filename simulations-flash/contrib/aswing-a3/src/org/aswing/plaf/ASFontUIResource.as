/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf
{
import org.aswing.ASFont;

/**
 * Font UI Resource.
 * @author iiley
 */
public class ASFontUIResource extends ASFont implements UIResource
{
	public function ASFontUIResource(name:String="Tahoma", size:Number=11, bold:Boolean=false, italic:Boolean=false, underline:Boolean=false, embedFontsOrAdvancedPros:*=null)
	{
		super(name, size, bold, italic, underline, embedFontsOrAdvancedPros);
	}
		
	/**
	 * Create a font ui resource with a font.
	 */
	public static function createResourceFont(font:ASFont):ASFontUIResource{
		return new ASFontUIResource(font.getName(), font.getSize(), font.isBold(), font.isItalic(), font.isUnderline(), font.getAdvancedProperties());
	}
}
}