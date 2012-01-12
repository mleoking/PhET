/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
/**
 * Basic CheckBox implementation.
 * @author iiley
 * @private
 */		
public class BasicCheckBoxUI extends BasicRadioButtonUI{
	
	public function BasicCheckBoxUI(){
		super();
	}
	
    override protected function getPropertyPrefix():String {
        return "CheckBox.";
    }
}
}