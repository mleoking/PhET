/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.basic.BasicAccordionUI;

/**
 * Accordion Container.
 * @author iiley
 */
public class JAccordion extends AbstractTabbedPane{
	
    /**
     * Create an accordion.
     */
	public function JAccordion() {
		super();
		setName("JAccordion");
		
		updateUI();
	}
	
	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicAccordionUI;
    }
	
	override public function getUIClassID():String{
		return "AccordionUI";
	}
	
	/**
	 * Generally you should not set layout to JAccordion.
	 * @param layout layoutManager for JAccordion
	 * @throws ArgumentError when you set a non-AccordionUI layout to JAccordion.
	 */
	override public function setLayout(layout:LayoutManager):void{
		if(layout is ComponentUI){
			super.setLayout(layout);
		}else{
			throw ArgumentError("Cannot set non-AccordionUI layout to JAccordion!");
		}
	}
}
}