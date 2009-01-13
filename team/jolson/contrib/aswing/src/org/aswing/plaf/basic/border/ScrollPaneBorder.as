/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.border.Border;
import org.aswing.border.LineBorder;
import org.aswing.plaf.UIResource;
import org.aswing.UIDefaults;
import org.aswing.UIManager;
 
/**
 * @author iiley
 */
class org.aswing.plaf.basic.border.ScrollPaneBorder extends LineBorder implements UIResource{	
	
	private static var instance:Border;
	
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Border{
		if(instance == null){
			var table:UIDefaults = UIManager.getLookAndFeelDefaults();
			var color:ASColor = table.getColor("ScrollPane.darkShadow");
			instance = new ScrollPaneBorder(color);
		}
		return instance;
	}
	
	public function ScrollPaneBorder(color:ASColor){
		super(null, color, 1);
	}

}
