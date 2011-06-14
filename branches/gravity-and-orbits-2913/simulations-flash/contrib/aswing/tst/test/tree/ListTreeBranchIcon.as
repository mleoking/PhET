/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;

/**
 * @author iiley
 */
class test.tree.ListTreeBranchIcon implements Icon {
	
	public function ListTreeBranchIcon(){
	}

	public function getIconWidth() : Number {
		return 10;
	}

	public function getIconHeight() : Number {
		return 10;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		g.fillRectangle(new SolidBrush(ASColor.RED), x, y, 10, 10);
	}

	public function uninstallIcon(com : Component) : Void {
	}
}