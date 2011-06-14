/*
 CopyRight @ 2005 XLands.com INC. All rights reserved.
*/

import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.JLabel;
import org.aswing.overflow.JList;
import org.aswing.overflow.JListTree;
import org.aswing.ListCell;
import org.aswing.tree.list.ListTreeCellBorder;
import org.aswing.tree.TreePath;

/**
 * @author iiley
 */
class org.aswing.tree.list.AbstractListTreeCell extends JLabel implements ListCell {
	
	public static var INDENT_UNIT:Number = 8;
	
	private var tree:JListTree;
	private var path:TreePath;
	private var border:ListTreeCellBorder;
	private var indent:Number;
	
	public function AbstractListTreeCell(){
		super();
		setName("ListTreeCell");
		indent = 0;
		border = createListTreeCellBorder();
		setHorizontalAlignment(JLabel.LEFT);
		setOpaque(true);
		setFocusable(false);
		setBorder(border);
	}
	
	public function setListCellStatus(list : JList, isSelected : Boolean, index : Number) : Void {
		var com:Component = getCellComponent();
		if(isSelected){
			com.setBackground(list.getSelectionBackground());
			com.setForeground(list.getSelectionForeground());
		}else{
			com.setBackground(list.getBackground());
			com.setForeground(list.getForeground());
		}
		com.setFont(list.getFont());
	}

	public function setCellValue(value) : Void {
		path = TreePath(value);
		indent = getLevel(path) * INDENT_UNIT;
		border.setIndent(indent);
		border.setLeaf(isLeaf(path));
		border.setExpanded(isExpanded(path));
		setIcon(getCellIcon(path));
		setText(getCellText(path));
		repaint();
	}
	
	public function setTree(tree:JListTree):Void{
		this.tree = tree;
	}

	public function getCellValue() {
		return path;
	}

	public function getCellComponent() : Component {
		return this;
	}
	
	public function getIndent():Number{
		return indent;
	}
	
	public function getIndentAndArrowAmount():Number{
		return indent + border.getArrowSizeAmount();
	}
	
	private function isLeaf(path:TreePath):Boolean{
		return tree.getTreeModel().isLeaf(path.getLastPathComponent());
	}
	
	private function isExpanded(path:TreePath):Boolean{
		return tree.getListTreeModel().getExpandedState(path);
	}
	
	private function getLevel(path:TreePath):Number{
		var level:Number = path.getPathCount() - 1;
		if(!tree.getListTreeModel().isRootVisible()){
			level--;
		}
		return level;
	}
	
	//Override this
	private function getCellIcon(path:TreePath):Icon{
		trace("/e/Subclass must override this method");
		return null;
	}
	
	//Override this
	private function getCellText(path:TreePath):String{
		trace("/e/Subclass must override this method");
		return "";
	}
	
	//Override this if you want another extended ListTreeCellBorder
	private function createListTreeCellBorder():ListTreeCellBorder{
		return new ListTreeCellBorder(0);
	}
}