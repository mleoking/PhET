/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.plaf.basic.icon.SolidArrowIcon;
import org.aswing.plaf.UIResource;

/**
 * The default arrow icon for menu
 * @author iiley
 */
class org.aswing.plaf.basic.icon.MenuArrowIcon extends SolidArrowIcon implements UIResource {
		
	public function MenuArrowIcon(){
		super(0, 8, ASColor.BLACK);
	}
}