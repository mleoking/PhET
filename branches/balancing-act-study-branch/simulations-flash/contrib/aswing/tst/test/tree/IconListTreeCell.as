/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Icon;
import org.aswing.tree.list.DefaultListTreeCell;
import org.aswing.tree.TreePath;

import test.tree.ListTreeBranchIcon;
import test.tree.ListTreeLeafIcon;

/**
 * @author iiley
 */
class test.tree.IconListTreeCell extends DefaultListTreeCell {
	
	private var leafIcon:Icon;
	private var branchIcon:Icon;
	
	public function IconListTreeCell() {
		super();
		leafIcon = new ListTreeLeafIcon();
		branchIcon = new ListTreeBranchIcon();
	}

	private function getCellIcon(path:TreePath):Icon{
		return isLeaf(path) ? leafIcon : branchIcon;
	}
}