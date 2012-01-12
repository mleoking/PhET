/*
 CopyRight @ 2005 XLands.com INC. All rights reserved.
*/

import org.aswing.Icon;
import org.aswing.tree.list.AbstractListTreeCell;
import org.aswing.tree.TreePath;

/**
 * @author iiley
 */
class org.aswing.tree.list.DefaultListTreeCell extends AbstractListTreeCell {
	
	public function DefaultListTreeCell() {
		super();
	}
	
	//Override this
	private function getCellIcon(path:TreePath):Icon{
		return null;
	}
	
	//Override this
	private function getCellText(path:TreePath):String{
		return path.getLastPathComponent().toString();
	}
}