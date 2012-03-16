/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.dnd.SourceData;

/**
 * The source data for draging list items.
 * @author iiley
 */
class org.aswing.dnd.ListSourceData extends SourceData {
	
	public static var DEFAULT_LIST_AUTO_DRAG_DATA_NAME:String = "DEFAULT_LIST_AUTO_DRAG_DATA_NAME";
	
	private var itemIndices:Array;
	
	public function ListSourceData(name : String, itemIndices:Array) {
		super(name, itemIndices);
		this.itemIndices = itemIndices;
	}
	
	public function getItemIndices():Array{
		return itemIndices;
	}
}