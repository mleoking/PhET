/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic
{
import org.aswing.*;
import org.aswing.plaf.BaseComponentUI;
import org.aswing.geom.IntDimension;

/**
 * @private
 */
public class BasicSpacerUI extends BaseComponentUI
{
	public function BasicSpacerUI(){
		super();
	}
	
	protected function getPropertyPrefix():String {
		return "Spacer.";
	}
	
	override public function installUI(c:Component):void{
		installDefaults(JSpacer(c));
	}
	
	override public function uninstallUI(c:Component):void{
		uninstallDefaults(JSpacer(c));
	}
	
	protected function installDefaults(s:JSpacer):void{
		var pp:String = getPropertyPrefix();
		LookAndFeel.installColors(s, pp);
		LookAndFeel.installBasicProperties(s, pp);
		LookAndFeel.installBorderAndBFDecorators(s, pp);
	}
	
	protected function uninstallDefaults(s:JSpacer):void{
		LookAndFeel.uninstallBorderAndBFDecorators(s);
	}
	
	override public function getPreferredSize(c:Component):IntDimension{
		return c.getInsets().getOutsideSize(new IntDimension(0, 0));
	}
	
	override public function getMaximumSize(c:Component):IntDimension
	{
		return IntDimension.createBigDimension();
	}
	/**
	 * Returns null
	 */	
	override public function getMinimumSize(c:Component):IntDimension
	{
		return new IntDimension(0, 0);
	}
}
}