/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.border.Border;
import org.aswing.plaf.basic.border.TextBorder;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.border.TextFieldBorder extends TextBorder {
		
	private static var instance:Border;
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Border{
		if(instance == null){
			instance = new TextFieldBorder(
							UIManager.getColor("TextField.shadow"),
							UIManager.getColor("TextField.darkShadow"),
							UIManager.getColor("TextField.light"),
							UIManager.getColor("TextField.highlight")		
			);
		}
		return instance;
	}
	
	public function TextFieldBorder(shadow : ASColor, darkShadow : ASColor, light : ASColor, highlight : ASColor) {
		super(shadow, darkShadow, light, highlight);
	}

}
