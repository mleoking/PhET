/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{

import flash.display.SimpleButton;

import org.aswing.plaf.basic.BasicButtonUI;

/**
 * An implementation of a "push" button.
 * @author iiley
 */
public class JButton extends AbstractButton
{
	public function JButton(text:String="", icon:Icon=null){
		super(text, icon);
		setName("JButton");
    	setModel(new DefaultButtonModel());
	}
	
	/**
	 * Returns whether this button is the default button of its root pane or not.
	 * @return true if this button is the default button of its root pane, false otherwise.
	 */
	public function isDefaultButton():Boolean{
		var rootPane:JRootPane = getRootPaneAncestor();
		if(rootPane != null){
			return rootPane.getDefaultButton() == this;
		}
		return false;
	}
	
	/**
	 * Wrap a SimpleButton to be this button's representation.
	 * @param btn the SimpleButton to be wrap.
	 * @return the button self
	 */
	override public function wrapSimpleButton(btn:SimpleButton):AbstractButton{
		mouseChildren = true;
		drawTransparentTrigger = false;
		setShiftOffset(0);
		setIcon(new SimpleButtonIcon(btn));
		setBorder(null);
		setMargin(new Insets());
		setBackgroundDecorator(null);
		setOpaque(false);
		setHorizontalTextPosition(AsWingConstants.CENTER);
		setVerticalTextPosition(AsWingConstants.CENTER);
		return this;
	}
	
    override public function updateUI():void{
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicButtonUI;
    }
    
	override public function getUIClassID():String{
		return "ButtonUI";
	}
	
}

}