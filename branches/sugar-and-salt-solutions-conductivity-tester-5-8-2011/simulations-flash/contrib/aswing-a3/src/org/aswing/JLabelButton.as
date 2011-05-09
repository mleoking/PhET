/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.plaf.basic.BasicLabelButtonUI;

/**
 * A button that performances like a hyper link text.
 * @author iiley
 */
public class JLabelButton extends AbstractButton{
	
	protected var rolloverColor:ASColor;
	protected var pressedColor:ASColor;
	
    /**
     * Creates a label button.
     * @param text the text.
     * @param icon the icon.
     * @param horizontalAlignment the horizontal alignment, default is <code>CENTER</code>
     */	
	public function JLabelButton(text:String="", icon:Icon=null, horizontalAlignment:int=0){
		super(text, icon);
		setName("JLabelButton");
    	setModel(new DefaultButtonModel());
    	setHorizontalAlignment(horizontalAlignment);
	}
	
    override public function updateUI():void{
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicLabelButtonUI;
    }
    
	override public function getUIClassID():String{
		return "LabelButtonUI";
	}
	
	/**
	 * Sets the color for text rollover state.
	 * @param c the color.
	 */
	public function setRollOverColor(c:ASColor):void{
		if(c != rolloverColor){
			rolloverColor = c;
			repaint();
		}
	}
	
	/**
	 * Gets the color for text rollover state.
	 * @param c the color.
	 */	
	public function getRollOverColor():ASColor{
		return rolloverColor;
	}	
	
	/**
	 * Sets the color for text pressed/selected state.
	 * @param c the color.
	 */	
	public function setPressedColor(c:ASColor):void{
		if(c != pressedColor){
			pressedColor = c;
			repaint();
		}
	}
	
	/**
	 * Gets the color for text pressed/selected state.
	 * @param c the color.
	 */		
	public function getPressedColor():ASColor{
		return pressedColor;
	}	
}
}