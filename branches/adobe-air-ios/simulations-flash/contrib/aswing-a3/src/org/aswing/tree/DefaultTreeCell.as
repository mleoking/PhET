/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree { 

import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.JLabel;
import org.aswing.JTree;
import org.aswing.geom.IntRectangle;

/**
 * The default cell for tree.
 * @author iiley
 */
public class DefaultTreeCell extends JLabel implements TreeCell {
	
	protected var expanded_folder_icon:Icon;
	protected var collapsed_folder_icon:Icon;
	protected var leaf_icon:Icon;
	
	protected var value:*;
	
	public function DefaultTreeCell(){
		super();
		setHorizontalAlignment(LEFT);
		setOpaque(true);
	}
	
	/**
	 * Simpler this method to speed up performance
	 */
	override public function setComBounds(b:IntRectangle):void{
		if(!b.equals(bounds)){
			bounds.setRect(b);
			locate();
			valid = false;
		}
	}
	
	/**
	 * Simpler this method to speed up performance
	 */
	override public function invalidate():void {
		clearPreferSizeCaches();
		valid = false;
	}
	
	/**
	 * Simpler this method to speed up performance
	 */
	override public function revalidate():void {
		valid = false;
	}
	
	/**
	 * do nothing, because paintImmediately will be called in by Tree
	 * @see #paintImmediately()
	 */
	override public function repaint():void{
		//do nothing, because paintImmediately will be called in by Tree
	}
	
	public function getExpandedFolderIcon():Icon{
		return expanded_folder_icon;
	}
	public function getCollapsedFolderIcon():Icon{
		return collapsed_folder_icon;
	}
	public function getLeafIcon():Icon{
		return leaf_icon;
	}
	
	protected function createExpandedFolderIcon(tree:JTree):Icon{
		return tree.getUI().getIcon("Tree.folderExpandedIcon");
	}
	protected function createCollapsedFolderIcon(tree:JTree):Icon{
		return tree.getUI().getIcon("Tree.folderCollapsedIcon");;
	}
	protected function createLeafIcon(tree:JTree):Icon{
		return tree.getUI().getIcon("Tree.leafIcon");;
	}
	
	//**********************************************************
	//				  Implementing TableCell
	//**********************************************************
	public function setCellValue(value:*) : void {
		readyToPaint = true;
		this.value = value;
		setText(value + "");
	}
	
	public function getCellValue():*{
		return value;
	}
	
	public function setTreeCellStatus(tree : JTree, selected : Boolean, expanded : Boolean, leaf : Boolean, row : int) : void {
		if(expanded_folder_icon == null){
			expanded_folder_icon = createExpandedFolderIcon(tree);
			//make it can get image from tree ui properties
			getUI().putDefault("Tree.folderExpandedImage", tree.getUI().getDefault("Tree.folderExpandedImage"));
		}
		if(collapsed_folder_icon == null){
			collapsed_folder_icon = createCollapsedFolderIcon(tree);
			//make it can get image from tree ui properties
			getUI().putDefault("Tree.folderCollapsedImage", tree.getUI().getDefault("Tree.folderCollapsedImage"));
		}
		if(leaf_icon == null){
			leaf_icon = createLeafIcon(tree);
			//make it can get image from tree ui properties
			getUI().putDefault("Tree.leafImage", tree.getUI().getDefault("Tree.leafImage"));
		}
		
		if(selected){
			setBackground(tree.getSelectionBackground());
			setForeground(tree.getSelectionForeground());
		}else{
			setBackground(tree.getBackground());
			setForeground(tree.getForeground());
		}
		setFont(tree.getFont());
		if(leaf){
			setIcon(getLeafIcon());
		}else if(expanded){
			setIcon(getExpandedFolderIcon());
		}else{
			setIcon(getCollapsedFolderIcon());
		}
	}
	
	public function getCellComponent() : Component {
		return this;
	}
	
	override public function toString():String{
		return "TreeCell[label:" + super.toString() + "]\n";
	}
}
}