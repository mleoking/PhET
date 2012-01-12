/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractButton;
import org.aswing.border.Border;
import org.aswing.border.DecorateBorder;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.border.ToolBarButtonBorder extends DecorateBorder {
	
	private static var toolBarButtonBordeInstance:Border;
	
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Border{
		if(toolBarButtonBordeInstance == null){
			toolBarButtonBordeInstance = new ToolBarButtonBorder();
		}
		return toolBarButtonBordeInstance;
	}
	
	public function ToolBarButtonBorder(button:AbstractButton) {
		super(button.getBorder());
	}
	
	/**
	 * paint the ButtonBorder content.
	 */
    public function paintBorder(c:Component, g:Graphics, bounds:Rectangle):Void{
    	var isPressed:Boolean = false;
    	var button:AbstractButton = AbstractButton(c);
    	if(button != null){
			isPressed = button.getModel().isPressed() || button.getModel().isSelected();
			if((button.isFocusOwner() && FocusManager.getCurrentManager().isTraversing()) || button.getModel().isRollOver() || isPressed){
				super.paintBorder(c, g, bounds);
			}
    	}
    }

}
