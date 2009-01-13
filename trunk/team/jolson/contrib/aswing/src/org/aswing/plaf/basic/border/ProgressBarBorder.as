/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.border.Border;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.Insets;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.border.ProgressBarBorder extends LineBorder implements UIResource{
	
	private static var instance:Border;
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Border{
		if(instance == null){
			instance = new ProgressBarBorder();
		}
		return instance;
	}
	
	public function ProgressBarBorder() {
		super(new EmptyBorder(null, new Insets(1,1,1,1)), UIManager.getColor("ProgressBar.foreground"));
	}

}
