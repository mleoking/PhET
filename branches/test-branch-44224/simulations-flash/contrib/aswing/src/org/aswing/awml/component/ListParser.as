/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ComponentParser;
import org.aswing.overflow.JList;
import org.aswing.VectorListModel;

/**
 * Parses {@link org.aswing.overflow.JList} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ListParser extends ComponentParser {
    
	private static var ATTR_HORIZONTAL_BLOCK_INCREMENT:String = "horizontal-block-increment";
	private static var ATTR_VERTICAL_BLOCK_INCREMENT:String = "vertical-block-increment";
	private static var ATTR_HORIZONTAL_UNIT_INCREMENT:String = "horizontal-unit-increment";
	private static var ATTR_VERTICAL_UNIT_INCREMENT:String = "vertical-unit-increment";
    private static var ATTR_SELECTION_MODE:String = "selection-mode";
    private static var ATTR_FIRST_VISIBLE_INDEX:String = "first-visible-index";
    private static var ATTR_LAST_VISIBLE_INDEX:String = "last-visible-index";
    private static var ATTR_SELECTED_INDEX:String = "selected-index";
    private static var ATTR_SELECTED_INDICES:String = "selected-indices";
    private static var ATTR_SELECTED_ITEM:String = "selected-item";
    private static var ATTR_PREFFERED_WIDTH_CELL_WIDTH_WHEN_NO_COUNT:String = "preffered-cell-width-when-no-count";
    private static var ATTR_VISIBLE_CELL_WIDTH:String = "visible-cell-width";
    private static var ATTR_VISIBLE_ROW_COUNT:String = "visible-row-count";
    
    private static var ATTR_ON_SELECTION_CHANGED:String = "on-selection-changed";
    private static var ATTR_ON_LIST_SCROLL:String = "on-list-scroll";
    private static var ATTR_ON_LIST_ITEM_PRESS:String = "on-list-item-press";
    private static var ATTR_ON_LIST_ITEM_RELEASE:String = "on-list-item-release";
    private static var ATTR_ON_LIST_ITEM_RELEASE_OUTSIDE:String = "on-list-item-release-outside";
    private static var ATTR_ON_LIST_ITEM_ROLL_OVER:String = "on-list-item-roll-over";
    private static var ATTR_ON_LIST_ITEM_ROLL_OUT:String = "on-list-item-roll-out";
    private static var ATTR_ON_LIST_ITEM_CLICKED:String = "on-list-item-clicked";
    
    private static var MODE_SINGLE:String = "single";
	private static var MODE_MULTIPLE:String = "multiple";
    
    
    /**
     * Constructor.
     */
    public function ListParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, list:JList, namespace:AwmlNamespace) {
    	
        list = super.parse(awml, list, namespace);
        
		// init block increments
		list.setHorizontalBlockIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_BLOCK_INCREMENT, list.getHorizontalBlockIncrement()));
		list.setVerticalBlockIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_BLOCK_INCREMENT, list.getVerticalBlockIncrement()));
		list.setHorizontalUnitIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_UNIT_INCREMENT, list.getHorizontalUnitIncrement()));
		list.setVerticalUnitIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_UNIT_INCREMENT, list.getVerticalUnitIncrement()));
        
        // init selection mode
        var mode:String = getAttributeAsString(awml, ATTR_SELECTION_MODE, null);
        switch (mode) {
        	case MODE_SINGLE:
        		list.setSelectionMode(JList.SINGLE_SELECTION);
        		break;	
        	case MODE_MULTIPLE:
        		list.setSelectionMode(JList.MULTIPLE_SELECTION);
        		break;	
        }
        
        //TODO selected items support 
        
        // init first and last visible indices
        list.setFirstVisibleIndex(getAttributeAsNumber(awml, ATTR_FIRST_VISIBLE_INDEX, list.getFirstVisibleIndex()));
        list.setLastVisibleIndex(getAttributeAsNumber(awml, ATTR_LAST_VISIBLE_INDEX, list.getLastVisibleIndex()));
        list.setSelectedValue(getAttributeAsString(awml, ATTR_SELECTED_ITEM, list.getSelectedValue()));
        
        // init cells pref. width
        list.setPreferredCellWidthWhenNoCount(getAttributeAsNumber(awml, ATTR_PREFFERED_WIDTH_CELL_WIDTH_WHEN_NO_COUNT, 
        		list.getPreferredCellWidthWhenNoCount()));
        
        list.setVisibleCellWidth(getAttributeAsNumber(awml, ATTR_VISIBLE_CELL_WIDTH, list.getVisibleCellWidth()));
        list.setVisibleRowCount(getAttributeAsNumber(awml, ATTR_VISIBLE_ROW_COUNT, list.getVisibleRowCount()));
        
        // init selected indices
        list.setSelectedIndex(getAttributeAsNumber(awml, ATTR_SELECTED_INDEX, list.getSelectedIndex()));
        list.setSelectedIndices(getAttributeAsArray(awml, ATTR_SELECTED_INDICES, list.getSelectedIndices()));
        
        // init events
        attachEventListeners(list, JList.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        attachEventListeners(list, JList.ON_SELECTION_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_SELECTION_CHANGED));
        attachEventListeners(list, JList.ON_LIST_SCROLL, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_SCROLL));
        attachEventListeners(list, JList.ON_ITEM_PRESS, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_ITEM_PRESS));
        attachEventListeners(list, JList.ON_ITEM_RELEASE, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_ITEM_RELEASE));
        attachEventListeners(list, JList.ON_ITEM_RELEASEOUTSIDE, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_ITEM_RELEASE_OUTSIDE));
        attachEventListeners(list, JList.ON_ITEM_ROLLOVER, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_ITEM_ROLL_OVER));
        attachEventListeners(list, JList.ON_ITEM_ROLLOUT, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_ITEM_ROLL_OUT));
        attachEventListeners(list, JList.ON_ITEM_CLICKED, getAttributeAsEventListenerInfos(awml, ATTR_ON_LIST_ITEM_CLICKED));
        
        return list;
	}
    
	private function parseChild(awml:XMLNode, nodeName:String, list:JList, namespace:AwmlNamespace):Void {

		super.parseChild(awml, nodeName, list, namespace);
		
		switch (nodeName) {
			case AwmlConstants.NODE_SELECTION_BACKGROUND:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) list.setSelectionBackground(color);
				break;
			case AwmlConstants.NODE_SELECTION_FOREGROUND:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) list.setSelectionForeground(color);
				break;
		}
		
		parseChildItem(awml, nodeName, list);
	}
    
    private function parseChildItem(awml:XMLNode, nodeName:String, list:JList):Void {
		switch (nodeName) {
			case AwmlConstants.NODE_LIST_ITEMS:
				var collection:Array = AwmlParser.parse(awml);
				if (collection != null) list.setModel(new VectorListModel(collection)); 
				break;
		}    		
    }
    
    private function getClass(Void):Function {
    	return JList;	
    }
    
}
