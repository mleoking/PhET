/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASFont;
import org.aswing.plaf.UIResource;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.ASFontUIResource extends ASFont implements UIResource{
	
	public function ASFontUIResource(_name:String , _size:Number , _bold:Boolean , 
		_italic:Boolean , _underline:Boolean, _embedFonts:Boolean){
		super(_name, _size, _bold, _italic , _underline, _embedFonts);
	}
		
	public static function createResourceFont(font:ASFont):ASFontUIResource{
		return new ASFontUIResource(font.getName(), font.getSize(), font.getBold(), font.getItalic(), font.getUnderline(), font.getEmbedFonts());
	}
	
}
