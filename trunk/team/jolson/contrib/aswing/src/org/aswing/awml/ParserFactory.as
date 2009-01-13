/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.border.BevelBorderParser;
import org.aswing.awml.border.EmptyBorderParser;
import org.aswing.awml.border.LineBorderParser;
import org.aswing.awml.border.NoBorderParser;
import org.aswing.awml.border.SideLineBorderParser;
import org.aswing.awml.border.SimpleTitledBorderParser;
import org.aswing.awml.border.TitledBorderParser;
import org.aswing.awml.common.BoundsParser;
import org.aswing.awml.common.ColorParser;
import org.aswing.awml.common.FontParser;
import org.aswing.awml.common.InsetsParser;
import org.aswing.awml.common.TextFormatParser;
import org.aswing.awml.component.AccordionParser;
import org.aswing.awml.component.AdjusterParser;
import org.aswing.awml.component.AttachPaneParser;
import org.aswing.awml.component.BoxParser;
import org.aswing.awml.component.ButtonParser;
import org.aswing.awml.component.CheckBoxMenuItemParser;
import org.aswing.awml.component.CheckBoxParser;
import org.aswing.awml.component.ColorChooserParser;
import org.aswing.awml.component.ColorMixerParser;
import org.aswing.awml.component.ColorSwatchesParser;
import org.aswing.awml.component.ComboBoxParser;
import org.aswing.awml.component.FrameParser;
import org.aswing.awml.component.LabelParser;
import org.aswing.awml.component.list.ListItemParser;
import org.aswing.awml.component.list.ListItemsParser;
import org.aswing.awml.component.ListParser;
import org.aswing.awml.component.listtree.ListTreeItemParser;
import org.aswing.awml.component.listtree.ListTreeItemsParser;
import org.aswing.awml.component.ListTreeParser;
import org.aswing.awml.component.LoadPaneParser;
import org.aswing.awml.component.MCPanelParser;
import org.aswing.awml.component.MenuBarParser;
import org.aswing.awml.component.MenuItemParser;
import org.aswing.awml.component.MenuParser;
import org.aswing.awml.component.PanelParser;
import org.aswing.awml.component.PopupMenuParser;
import org.aswing.awml.component.PopupParser;
import org.aswing.awml.component.ProgressBarParser;
import org.aswing.awml.component.RadioButtonMenuItemParser;
import org.aswing.awml.component.RadioButtonParser;
import org.aswing.awml.component.ScrollBarParser;
import org.aswing.awml.component.ScrollPaneParser;
import org.aswing.awml.component.SeparatorParser;
import org.aswing.awml.component.SliderParser;
import org.aswing.awml.component.SoftBoxParser;
import org.aswing.awml.component.SpacerParser;
import org.aswing.awml.component.split.SplitComponentParser;
import org.aswing.awml.component.SplitPaneParser;
import org.aswing.awml.component.tab.TabParser;
import org.aswing.awml.component.TabbedPaneParser;
import org.aswing.awml.component.table.TableCellParser;
import org.aswing.awml.component.table.TableColumnParser;
import org.aswing.awml.component.table.TableColumnsParser;
import org.aswing.awml.component.table.TableRowParser;
import org.aswing.awml.component.table.TableRowsParser;
import org.aswing.awml.component.TableParser;
import org.aswing.awml.component.text.TextCSSParser;
import org.aswing.awml.component.text.TextParser;
import org.aswing.awml.component.TextAreaParser;
import org.aswing.awml.component.TextFieldParser;
import org.aswing.awml.component.ToggleButtonParser;
import org.aswing.awml.component.ToolBarParser;
import org.aswing.awml.component.tree.TreeNodeParser;
import org.aswing.awml.component.tree.TreeRootParser;
import org.aswing.awml.component.TreeParser;
import org.aswing.awml.component.ViewportParser;
import org.aswing.awml.component.WindowParser;
import org.aswing.awml.icon.AttachIconParser;
import org.aswing.awml.icon.IconWrapperParser;
import org.aswing.awml.icon.LoadIconParser;
import org.aswing.awml.icon.OffsetIconParser;
import org.aswing.awml.layout.AlignLayoutParser;
import org.aswing.awml.layout.BorderLayoutParser;
import org.aswing.awml.layout.BoxLayoutParser;
import org.aswing.awml.layout.CenterLayoutParser;
import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.awml.layout.FlowLayoutParser;
import org.aswing.awml.layout.GridLayoutParser;
import org.aswing.awml.layout.SoftBoxLayoutParser;
import org.aswing.awml.reflection.ArgumentsParser;
import org.aswing.awml.reflection.ArrayParser;
import org.aswing.awml.reflection.ComponentParser;
import org.aswing.awml.reflection.EventParser;
import org.aswing.awml.reflection.FormParser;
import org.aswing.awml.reflection.InstanceParser;
import org.aswing.awml.reflection.MethodParser;
import org.aswing.awml.reflection.PropertyParser;
import org.aswing.awml.reflection.ValueParser;
import org.aswing.util.HashMap;

/**
 * Configures dependencies between AWML elements, objects and parsers. Provides 
 * routines to obtain required parsers. 
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.ParserFactory {
    
    /** Defines component parser type. */
    public static var COMPONENT:Number = AwmlConstants.TYPE_COMPONENT;

    /** Defines component wrapper parser type. */
    public static var COMPONENT_WRAPPER:Number = AwmlConstants.TYPE_COMPONENT_WRAPPER;
    
    /** Defines border parser type. */
    public static var BORDER:Number = AwmlConstants.TYPE_BORDER;
    
    /** Defines layout parser type. */
    public static var LAYOUT:Number = AwmlConstants.TYPE_LAYOUT;

    /** Defines menu item parser type. */
    public static var MENU_ITEM:Number = AwmlConstants.TYPE_MENU_ITEM;
    
    /** Defines icon parser type. */
    public static var ICON:Number = AwmlConstants.TYPE_ICON;
    
    /** Defines icon wrapper parser type. */
    public static var ICON_WRAPPER:Number = AwmlConstants.TYPE_ICON_WRAPPER;

    /** Defines reflection property value parser type. */
    public static var PROPERTY:Number = AwmlConstants.TYPE_PROPERTY;
    
    /** Defines reflection property value parser type. */
    public static var ARGUMENT:Number = AwmlConstants.TYPE_ARGUMENT;


    /** Singleton instance. */
    private static var instance:ParserFactory;
       
        
    /**
     * Creates singleton instance.
     * 
     * @return singleton instance.
     */
    private static function getInstance(Void):ParserFactory { 
        if (instance == null) {
            instance = new ParserFactory(); 
        }
        return instance;    
    }
    
    /**
     * Associates parser class with AWML node name.
     * 
     * @param parserName the AWML node name.
     * @param parserClass the parser class responsible for AWML node parsing. 
     * @param parserType the numeric representation of the parser's type. 
     * Parser's type can be multiple. Multiple types assembles from the
     * individual types using bitwise OR operator (<code>|</code>).
     * @see #COMPONENT 
     * @see #LAYOUT
     * @see #BORDER
     * @see #ICON
     * @see #ICON_WRAPPER
     */
    public static function put(parserName:String, parserClass:Function, parserType:Number):Void {
        if (parserClass == null || parserName == null || parserName == "") return;
        getInstance().addParser(parserName, parserClass, parserType);
    }

    /**
     * Gets parser instance by AWML node name.
     * 
     * @param parserName the AWML node name.
     * @return parser instance.
     */
    public static function get(parserName:String) {
        return getInstance().getParser(parserName);
    }
    
    /**
     * Checks if parser has specified type.
     * @param parserName the name of the parser
     * @param parserType the type of the parser to check 
     *  
     * @see #COMPONENT 
     * @see #LAYOUT
     * @see #BORDER
     * @see #ICON
     * @see #ICON_WRAPPER
     */
    public static function isTypeOf(parserName:String, parserType:Number):Boolean {
    	return Boolean(getInstance().parserTypes.get(parserName) & parserType);
    }  		


    /** Holds parser classes associated with parser names. */
    private var parserClasses:HashMap;
    
    /** Holds parser types associated with parser names. */
    private var parserTypes:HashMap;

    /** Holds parser instances associated with parser classes. */
    private var parserInstances:HashMap;


    /**
     * Private Constructor.
     */
    private function ParserFactory(Void) {
        parserClasses = new HashMap();
        parserTypes = new HashMap();
        parserInstances = new HashMap();
        
        // init parsers 
        addParser(AwmlConstants.NODE_FRAME, FrameParser, COMPONENT);
        addParser(AwmlConstants.NODE_WINDOW, WindowParser, COMPONENT);
        addParser(AwmlConstants.NODE_POPUP, PopupParser, COMPONENT);
        addParser(AwmlConstants.NODE_MC_PANEL, MCPanelParser, COMPONENT);
        addParser(AwmlConstants.NODE_POPUP_MENU, PopupMenuParser, COMPONENT);
        
        addParser(AwmlConstants.NODE_TEXT_AREA, TextAreaParser, COMPONENT);
        addParser(AwmlConstants.NODE_TEXT_FIELD, TextFieldParser, COMPONENT);
        addParser(AwmlConstants.NODE_SEPARATOR, SeparatorParser, COMPONENT | MENU_ITEM);
        addParser(AwmlConstants.NODE_PROGRESS_BAR, ProgressBarParser, COMPONENT);
        addParser(AwmlConstants.NODE_LABEL, LabelParser, COMPONENT);
        addParser(AwmlConstants.NODE_BUTTON, ButtonParser, COMPONENT);
        addParser(AwmlConstants.NODE_TOGGLE_BUTTON, ToggleButtonParser, COMPONENT);
        addParser(AwmlConstants.NODE_CHECK_BOX, CheckBoxParser, COMPONENT);
        addParser(AwmlConstants.NODE_RADIO_BUTTON, RadioButtonParser, COMPONENT);
        addParser(AwmlConstants.NODE_SCROLL_BAR, ScrollBarParser, COMPONENT);
        addParser(AwmlConstants.NODE_SLIDER, SliderParser, COMPONENT);
        addParser(AwmlConstants.NODE_LIST, ListParser, COMPONENT);
        addParser(AwmlConstants.NODE_COMBO_BOX, ComboBoxParser, COMPONENT);
        addParser(AwmlConstants.NODE_VIEW_PORT, ViewportParser, COMPONENT);
        addParser(AwmlConstants.NODE_SCROLL_PANE, ScrollPaneParser, COMPONENT);
        addParser(AwmlConstants.NODE_TABLE, TableParser, COMPONENT);
        addParser(AwmlConstants.NODE_TREE, TreeParser, COMPONENT);
        addParser(AwmlConstants.NODE_LIST_TREE, ListTreeParser, COMPONENT);
        addParser(AwmlConstants.NODE_ADJUSTER, AdjusterParser, COMPONENT);
        addParser(AwmlConstants.NODE_SPACER, SpacerParser, COMPONENT);
        addParser(AwmlConstants.NODE_SPLIT_PANE, SplitPaneParser, COMPONENT);
        addParser(AwmlConstants.NODE_MENU_BAR, MenuBarParser, COMPONENT);
        
        addParser(AwmlConstants.NODE_PANEL, PanelParser, COMPONENT);
        addParser(AwmlConstants.NODE_BOX, BoxParser, COMPONENT);
        addParser(AwmlConstants.NODE_SOFT_BOX, SoftBoxParser, COMPONENT);
        addParser(AwmlConstants.NODE_TOOL_BAR, ToolBarParser, COMPONENT);
        addParser(AwmlConstants.NODE_ATTACH_PANE, AttachPaneParser, COMPONENT);
        addParser(AwmlConstants.NODE_LOAD_PANE, LoadPaneParser, COMPONENT);
        
        addParser(AwmlConstants.NODE_ACCORDION, AccordionParser, COMPONENT);
        addParser(AwmlConstants.NODE_TABBED_PANE, TabbedPaneParser, COMPONENT);
        
        addParser(AwmlConstants.NODE_COLOR_SWATCHES, ColorSwatchesParser, COMPONENT);
        addParser(AwmlConstants.NODE_COLOR_MIXER, ColorMixerParser, COMPONENT);
        addParser(AwmlConstants.NODE_COLOR_CHOOSER, ColorChooserParser, COMPONENT);
        
        addParser(AwmlConstants.NODE_MENU, MenuParser, MENU_ITEM);
        addParser(AwmlConstants.NODE_MENU_ITEM, MenuItemParser, MENU_ITEM);
        addParser(AwmlConstants.NODE_CHECK_BOX_MENU_ITEM, CheckBoxMenuItemParser, MENU_ITEM);
        addParser(AwmlConstants.NODE_RADIO_BUTTON_MENU_ITEM, RadioButtonMenuItemParser, MENU_ITEM);
        
        addParser(AwmlConstants.NODE_EMPTY_LAYOUT, EmptyLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_CENTER_LAYOUT, CenterLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_ALIGN_LAYOUT, AlignLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_FLOW_LAYOUT, FlowLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_BOX_LAYOUT, BoxLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_SOFT_BOX_LAYOUT, SoftBoxLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_GRID_LAYOUT, GridLayoutParser, LAYOUT);
        addParser(AwmlConstants.NODE_BORDER_LAYOUT, BorderLayoutParser, LAYOUT);
        
        addParser(AwmlConstants.NODE_BEVEL_BORDER, BevelBorderParser, BORDER);
        addParser(AwmlConstants.NODE_EMPTY_BORDER, EmptyBorderParser, BORDER);
        addParser(AwmlConstants.NODE_LINE_BORDER, LineBorderParser, BORDER);
        addParser(AwmlConstants.NODE_SIDE_LINE_BORDER, SideLineBorderParser, BORDER);
        addParser(AwmlConstants.NODE_TITLED_BORDER, TitledBorderParser, BORDER);
        addParser(AwmlConstants.NODE_SIMPLE_TITLED_BORDER, SimpleTitledBorderParser, BORDER);
        addParser(AwmlConstants.NODE_NO_BORDER, NoBorderParser, BORDER);
        
        addParser(AwmlConstants.NODE_MARGINS, InsetsParser);
        addParser(AwmlConstants.NODE_INSETS, InsetsParser);
        addParser(AwmlConstants.NODE_MAXIMIZED_BOUNDS, BoundsParser);
        addParser(AwmlConstants.NODE_CLIP_BOUNDS, BoundsParser);
        addParser(AwmlConstants.NODE_FONT, FontParser);
        addParser(AwmlConstants.NODE_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_BACKGROUND, ColorParser);
        addParser(AwmlConstants.NODE_FOREGROUND, ColorParser);
        addParser(AwmlConstants.NODE_HIGHLIGHT_INNER_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_HIGHLIGHT_OUTER_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_SHADOW_INNER_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_SHADOW_OUTER_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_LINE_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_LINE_LIGHT_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_TEXT_FORMAT, TextFormatParser);
        addParser(AwmlConstants.NODE_SELECTION_BACKGROUND, ColorParser);
        addParser(AwmlConstants.NODE_SELECTION_FOREGROUND, ColorParser);
        addParser(AwmlConstants.NODE_GRID_COLOR, ColorParser);
        addParser(AwmlConstants.NODE_SELECTED_COLOR, ColorParser);
        
        addParser(AwmlConstants.NODE_LIST_ITEMS, ListItemsParser);
        addParser(AwmlConstants.NODE_LIST_ITEM, ListItemParser);

        addParser(AwmlConstants.NODE_LIST_TREE_ITEMS, ListTreeItemsParser);
        addParser(AwmlConstants.NODE_LIST_TREE_ITEM, ListTreeItemParser);

        addParser(AwmlConstants.NODE_TREE_ROOT, TreeRootParser);
        addParser(AwmlConstants.NODE_TREE_NODE, TreeNodeParser);

        addParser(AwmlConstants.NODE_TABLE_COLUMNS, TableColumnsParser);
        addParser(AwmlConstants.NODE_TABLE_COLUMN, TableColumnParser);
        addParser(AwmlConstants.NODE_TABLE_ROWS, TableRowsParser);
        addParser(AwmlConstants.NODE_TABLE_ROW, TableRowParser);
        addParser(AwmlConstants.NODE_TABLE_CELL, TableCellParser);
        
        addParser(AwmlConstants.NODE_TAB, TabParser);
        
        addParser(AwmlConstants.NODE_TOP_COMPONENT, SplitComponentParser, COMPONENT_WRAPPER);
        addParser(AwmlConstants.NODE_BOTTOM_COMPONENT, SplitComponentParser, COMPONENT_WRAPPER);
        addParser(AwmlConstants.NODE_LEFT_COMPONENT, SplitComponentParser, COMPONENT_WRAPPER);
        addParser(AwmlConstants.NODE_RIGHT_COMPONENT, SplitComponentParser, COMPONENT_WRAPPER);
        
        addParser(AwmlConstants.NODE_TEXT, TextParser);
        addParser(AwmlConstants.NODE_TEXT_CSS, TextCSSParser);
        
        addParser(AwmlConstants.NODE_ATTACH_ICON, AttachIconParser, ICON);
        addParser(AwmlConstants.NODE_LOAD_ICON, LoadIconParser, ICON);
        addParser(AwmlConstants.NODE_OFFSET_ICON, OffsetIconParser, ICON);
        
        addParser(AwmlConstants.NODE_ICON, IconWrapperParser, ICON_WRAPPER);
        addParser(AwmlConstants.NODE_SELECTED_ICON, IconWrapperParser, ICON_WRAPPER);
        addParser(AwmlConstants.NODE_PRESSED_ICON, IconWrapperParser, ICON_WRAPPER);
        addParser(AwmlConstants.NODE_DISABLED_ICON, IconWrapperParser, ICON_WRAPPER);
        addParser(AwmlConstants.NODE_DISABLED_SELECTED_ICON, IconWrapperParser, ICON_WRAPPER);
        addParser(AwmlConstants.NODE_ROLL_OVER_ICON, IconWrapperParser, ICON_WRAPPER);
        addParser(AwmlConstants.NODE_ROLL_OVER_SELECTED_ICON, IconWrapperParser, ICON_WRAPPER);

		// for JScrollPane        
        addParser(AwmlConstants.NODE_SCROLL_VIEW_PORT, ViewportParser);
        
        addParser(AwmlConstants.NODE_FORM, FormParser, COMPONENT);
        addParser(AwmlConstants.NODE_COMPONENT, ComponentParser, COMPONENT | PROPERTY | ARGUMENT);
        addParser(AwmlConstants.NODE_PROPERTY, PropertyParser);
        addParser(AwmlConstants.NODE_METHOD, MethodParser);
        addParser(AwmlConstants.NODE_EVENT, EventParser);
        addParser(AwmlConstants.NODE_CONSTRUCTOR, ArgumentsParser);
        addParser(AwmlConstants.NODE_ARGUMENTS, ArgumentsParser);
        addParser(AwmlConstants.NODE_VALUE, ValueParser, PROPERTY | ARGUMENT);
        addParser(AwmlConstants.NODE_INSTANCE, InstanceParser, PROPERTY | ARGUMENT);
        addParser(AwmlConstants.NODE_ARRAY, ArrayParser, PROPERTY | ARGUMENT);
    }

    private function addParser(parserName:String, parserClass:Function, parserType:Number):Void {
        parserClasses.put(parserName, parserClass);
        parserTypes.put(parserName, parserType);
    }

    private function getParser(parserName:String) {
    	var parserClass:Function = parserClasses.get(parserName); 
        if (parserClass == null) return null;
        
        var parserInstance = parserInstances.get(parserClass);
        if (parserInstance == null) {
            parserInstance = new parserClass();
            parserInstances.put(parserClass, parserInstance);
        }   
        
        return parserInstance;
    }

}
