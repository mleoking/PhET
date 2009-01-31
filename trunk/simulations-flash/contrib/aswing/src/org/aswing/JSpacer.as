/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.plaf.SpacerUI;
import org.aswing.UIManager;

/**
 * <code>JSpacer</code> provides basic functionality to create empty spaces between
 * other components.
 * 
 * @author iiley
 * @author Igor Sadovskiy
 */
class org.aswing.JSpacer extends Component {
	
	/**
	 * JSpacer(prefWidth:Number, prefHeight:Number)<br>
	 * JSpacer(prefSize:Dimension)<br>
	 * JSpacer()
	 */
	public function JSpacer(){
		super();
		setPreferredSize(arguments[0], arguments[1]);
		updateUI();
	}
	
	public function getUI():SpacerUI{
		return SpacerUI(ui);
	}
	
	public function setUI(ui:SpacerUI):Void{
		super.setUI(ui);
	}
	
	public function updateUI():Void{
		setUI(SpacerUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "SpacerUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicSpacerUI;
    }
	
	/**
	 * Creates a Spacer that displays its components from left to right.
	 * @param width (optional)the width of the spacer, default is 4
	 * @return the spacer
	 */
	public static function createHorizontalSpacer(width:Number):JSpacer{
		if(width == undefined) width = 4;
		var glue:JSpacer = new JSpacer();
		glue.setPreferredSize(width, 0);
		glue.setMaximumSize(width, Number.MAX_VALUE);
		return glue;
	}
	
	/**
	 * Creates a Spacer that displays its components from top to bottom.
	 * @param height (optional)the height of the spacer, default is 4
	 * @return the spacer
	 */
	public static function createVerticalSpacer(height:Number):JSpacer{
		if(height == undefined) height = 4;
		var glue:JSpacer = new JSpacer();
		glue.setPreferredSize(0, height);
		glue.setMaximumSize(Number.MAX_VALUE, height);
		return glue;
	}	
	
	/**
	 * Creates a solid Spacer with specified preffered width and height.
	 * @param width (optional)the width of the spacer, default is 4
	 * @param height (optional)the height of the spacer, default is 4
	 * @return the spacer
	 */
	public static function createSolidSpacer(width:Number, height:Number):JSpacer{
		if(width == undefined) width = 4;
		if(height == undefined) height = 4;
		var glue:JSpacer = new JSpacer();
		glue.setPreferredSize(width, height);
		return glue;
	}	
}
