/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.border.Border;
import org.aswing.border.LineBorder;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.border.ToolTipBorder extends LineBorder implements UIResource{
	
	private static var instance:Border;
	/**
	 * this make shared instance.
	 */	
	public static function createInstance():Border{
		if(instance == null){
			instance = new ToolTipBorder(UIManager.getColor("ToolTip.borderColor"));
		}
		return instance;
	}
	
	public function ToolTipBorder(color:ASColor) {
		super(null, color, 1);
	}

}
