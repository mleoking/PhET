/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * Contains constants used by AWML parser.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AwmlConstants {

	public static var NODE_ASWING:String = "aswing";
	
	public static var NODE_FRAME:String = "frame";
	public static var NODE_WINDOW:String = "window";
	public static var NODE_POPUP:String = "popup";
	public static var NODE_MC_PANEL:String = "mc-panel";
	public static var NODE_POPUP_MENU:String = "popup-menu";
	
	public static var NODE_TEXT_FIELD:String = "text-field";
	public static var NODE_TEXT_AREA:String = "text-area";
	public static var NODE_SEPARATOR:String = "separator";
	public static var NODE_PROGRESS_BAR:String = "progress-bar";
	public static var NODE_LABEL:String = "label";
	public static var NODE_BUTTON:String = "button";
	public static var NODE_TOGGLE_BUTTON:String = "toggle-button";
	public static var NODE_CHECK_BOX:String = "check-box";
	public static var NODE_RADIO_BUTTON:String = "radio-button";
	public static var NODE_SCROLL_BAR:String = "scroll-bar";
	public static var NODE_SLIDER:String = "slider";
	public static var NODE_LIST:String = "list";
	public static var NODE_COMBO_BOX:String = "combo-box";
	public static var NODE_LOAD_PANE:String = "load-pane";
	public static var NODE_ATTACH_PANE:String = "attach-pane";
	public static var NODE_TABLE:String = "table";
	public static var NODE_ADJUSTER:String = "adjuster";
	public static var NODE_SPACER:String = "spacer";
	public static var NODE_TREE:String = "tree";
	public static var NODE_LIST_TREE:String = "list-tree";
	public static var NODE_SPLIT_PANE:String = "split-pane";
	public static var NODE_MENU_BAR:String = "menu-bar";
	
	public static var NODE_COLOR_SWATCHES:String = "color-swatches";
	public static var NODE_COLOR_MIXER:String = "color-mixer";
	public static var NODE_COLOR_CHOOSER:String = "color-chooser";
	
	public static var NODE_ACCORDION:String = "accordion";
	public static var NODE_TABBED_PANE:String = "tabbed-pane";
	
	public static var NODE_PANEL:String = "panel";
	public static var NODE_BOX:String = "box";
	public static var NODE_SOFT_BOX:String = "soft-box";
	public static var NODE_TOOL_BAR:String = "tool-bar";
	public static var NODE_SCROLL_PANE:String = "scroll-pane";
	public static var NODE_VIEW_PORT:String = "view-port";

	public static var NODE_MENU:String = "menu";
	public static var NODE_MENU_ITEM:String = "menu-item";
	public static var NODE_CHECK_BOX_MENU_ITEM:String = "check-box-menu-item";
	public static var NODE_RADIO_BUTTON_MENU_ITEM:String = "radio-button-menu-item";

	public static var NODE_EMPTY_LAYOUT:String = "empty-layout";
	public static var NODE_CENTER_LAYOUT:String = "center-layout";
	public static var NODE_ALIGN_LAYOUT:String = "align-layout";
	public static var NODE_FLOW_LAYOUT:String = "flow-layout";
	public static var NODE_BOX_LAYOUT:String = "box-layout";
	public static var NODE_SOFT_BOX_LAYOUT:String = "soft-box-layout";
	public static var NODE_GRID_LAYOUT:String = "grid-layout";
	public static var NODE_BORDER_LAYOUT:String = "border-layout";
	
	public static var NODE_BEVEL_BORDER:String = "bevel-border";
	public static var NODE_EMPTY_BORDER:String = "empty-border";
	public static var NODE_LINE_BORDER:String = "line-border";
	public static var NODE_SIDE_LINE_BORDER:String = "side-line-border";
	public static var NODE_TITLED_BORDER:String = "titled-border";
	public static var NODE_SIMPLE_TITLED_BORDER:String = "simple-titled-border";
	public static var NODE_NO_BORDER:String = "no-border";
	
	public static var NODE_SCROLL_VIEW_PORT:String = "scroll-view-port";
	
	public static var NODE_MARGINS:String = "margins";
	public static var NODE_INSETS:String = "insets";
	public static var NODE_MAXIMIZED_BOUNDS:String = "maximized-bounds";
	public static var NODE_CLIP_BOUNDS:String = "clip-bounds";
	public static var NODE_FONT:String = "font";
	public static var NODE_COLOR:String = "color";
	public static var NODE_SELECTED_COLOR:String = "selected-color";
	public static var NODE_BACKGROUND:String = "background";
	public static var NODE_FOREGROUND:String = "foreground";
	public static var NODE_HIGHLIGHT_OUTER_COLOR:String = "highlight-outer-color";
	public static var NODE_HIGHLIGHT_INNER_COLOR:String = "highlight-inner-color";
	public static var NODE_SHADOW_OUTER_COLOR:String = "shadow-outer-color";
	public static var NODE_SHADOW_INNER_COLOR:String = "shadow-inner-color";
	public static var NODE_LINE_COLOR:String = "line-color";
	public static var NODE_LINE_LIGHT_COLOR:String = "line-light-color";
	public static var NODE_TEXT_FORMAT:String = "text-format";
	
    public static var NODE_SELECTION_FOREGROUND:String = "selection-foreground";
    public static var NODE_SELECTION_BACKGROUND:String = "selection-background";
    public static var NODE_GRID_COLOR:String = "grid-color"; 

	public static var NODE_LIST_ITEMS:String = "list-items";
	public static var NODE_LIST_ITEM:String = "list-item";

	public static var NODE_LIST_TREE_ITEMS:String = "list-tree-items";
	public static var NODE_LIST_TREE_ITEM:String = "list-tree-item";

	public static var NODE_TREE_ROOT:String = "tree-root";
	public static var NODE_TREE_NODE:String = "tree-node";

	public static var NODE_TABLE_COLUMNS:String = "table-columns";
	public static var NODE_TABLE_COLUMN:String = "table-column";
	public static var NODE_TABLE_ROWS:String = "table-rows";
	public static var NODE_TABLE_ROW:String = "table-row";
	public static var NODE_TABLE_CELL:String = "table-cell";
	
	public static var NODE_TAB:String = "tab";
	
	public static var NODE_TOP_COMPONENT:String = "top-component";
	public static var NODE_BOTTOM_COMPONENT:String = "bottom-component";
	public static var NODE_LEFT_COMPONENT:String = "left-component";
	public static var NODE_RIGHT_COMPONENT:String = "right-component";
	
	public static var NODE_TEXT:String = "text";
	public static var NODE_TEXT_CSS:String = "text-css";

	public static var NODE_ATTACH_ICON:String = "attach-icon";
	public static var NODE_LOAD_ICON:String = "load-icon";
	public static var NODE_OFFSET_ICON:String = "offset-icon";
	
	public static var NODE_ICON:String = "icon";
	public static var NODE_PRESSED_ICON:String = "pressed-icon";
	public static var NODE_DISABLED_ICON:String = "disabled-icon";
	public static var NODE_ROLL_OVER_ICON:String = "roll-over-icon";
	public static var NODE_SELECTED_ICON:String = "selected-icon";
	public static var NODE_DISABLED_SELECTED_ICON:String = "disabled-selected-icon";
	public static var NODE_ROLL_OVER_SELECTED_ICON:String = "roll-over-selected-icon";

	public static var NODE_FORM:String = "form";
	public static var NODE_COMPONENT:String = "component";
	public static var NODE_PROPERTY:String = "property";
	public static var NODE_METHOD:String = "method";
	public static var NODE_EVENT:String = "event";
	public static var NODE_CONSTRUCTOR:String = "constructor";
	public static var NODE_ARGUMENTS:String = "arguments";
	public static var NODE_VALUE:String = "value";
	public static var NODE_INSTANCE:String = "instance";
	public static var NODE_ARRAY:String = "array";

    public static var TYPE_COMPONENT:Number = 1;
    public static var TYPE_COMPONENT_WRAPPER:Number = 2;
    public static var TYPE_BORDER:Number = 4;
    public static var TYPE_LAYOUT:Number = 8;
    public static var TYPE_MENU_ITEM:Number = 16;
    public static var TYPE_ICON:Number = 32;
    public static var TYPE_ICON_WRAPPER:Number = 64;
    public static var TYPE_PROPERTY:Number = 128;
    public static var TYPE_ARGUMENT:Number = 256;
    


	/**
	 * Private constructor.
	 */
	private function AwmlConstants(Void) {
		//
	}	
	
}
