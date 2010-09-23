/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
import flash.filters.DropShadowFilter;

import org.aswing.*;
import org.aswing.plaf.*;
import org.aswing.plaf.basic.adjuster.PopupSliderThumbIcon;
import org.aswing.plaf.basic.background.*;
import org.aswing.plaf.basic.border.*;
import org.aswing.plaf.basic.cursor.*;
import org.aswing.plaf.basic.frame.*;
import org.aswing.plaf.basic.icon.*;
import org.aswing.plaf.basic.tree.BasicExpandControl;
import org.aswing.resizer.*;
import org.aswing.tree.*;

/**
 * Note: All empty object should be undefined or an UIResource instance.
 * Undefined/UIResource instance means not set, if it is null, means that user set it to be null, so LAF value will not be use. 
 * @author iiley
 */
public class BasicLookAndFeel extends LookAndFeel{
	
	protected var NULL_COLOR:ASColorUIResource = DefaultEmptyDecoraterResource.NULL_COLOR;
	protected var NULL_FONT:ASFontUIResource = DefaultEmptyDecoraterResource.NULL_FONT;
	
	/**
	 * Need to extends it to make a completed LAF and implements features.
	 */
	public function BasicLookAndFeel(){
	}
	
	override public function getDefaults():UIDefaults{
		var table:UIDefaults  = new UIDefaults();

		initClassDefaults(table);
		initSystemColorDefaults(table);
		initSystemFontDefaults(table);
		initCommonUtils(table);
		initComponentDefaults(table);
	
		return table;
	}
	
	protected function initClassDefaults(table:UIDefaults):void{
		var uiDefaults:Array = [
				// Basic ui is referenced in component class
				//if you created your ui, you must specified 
				//it in your LAF class like below commented.
			   /*"ButtonUI", org.aswing.plaf.basic.BasicButtonUI, 
			   "PanelUI", org.aswing.plaf.basic.BasicPanelUI, 
			   "ToggleButtonUI", org.aswing.plaf.basic.BasicToggleButtonUI,
			   "RadioButtonUI", org.aswing.plaf.basic.BasicRadioButtonUI,
			   "CheckBoxUI", org.aswing.plaf.basic.BasicCheckBoxUI, 
			   "ColorSwatchesUI", org.aswing.plaf.basic.BasicColorSwatchesUI,
			   "ColorMixerUI", org.aswing.plaf.basic.BasicColorMixerUI,
			   "ColorChooserUI", org.aswing.plaf.basic.BasicColorChooserUI,			   
			   "ScrollBarUI", org.aswing.plaf.basic.BasicScrollBarUI, 
			   "SeparatorUI", org.aswing.plaf.basic.BasicSeparatorUI,
			   "ViewportUI", org.aswing.plaf.basic.BasicViewportUI,
			   "ScrollPaneUI", org.aswing.plaf.basic.BasicScrollPaneUI, 
			   "LabelUI",org.aswing.plaf.basic.BasicLabelUI, 
			   "TextFieldUI",org.aswing.plaf.basic.BasicTextFieldUI, 
			   "TextAreaUI",org.aswing.plaf.basic.BasicTextAreaUI, 
			   "FrameUI",org.aswing.plaf.basic.BasicFrameUI, 
			   "ToolTipUI",org.aswing.plaf.basic.BasicToolTipUI, 
			   "ProgressBarUI", org.aswing.plaf.basic.BasicProgressBarUI,			   			   
			   "ListUI",org.aswing.plaf.basic.BasicListUI,		   			   
			   "ComboBoxUI",org.aswing.plaf.basic.BasicComboBoxUI,			   
			   "SliderUI",org.aswing.plaf.basic.BasicSliderUI,		   
			   "AdjusterUI",org.aswing.plaf.basic.BasicAdjusterUI,	   
			   "AccordionUI",org.aswing.plaf.basic.BasicAccordionUI,	   
			   "TabbedPaneUI",org.aswing.plaf.basic.BasicTabbedPaneUI,
			   "SplitPaneUI", org.aswing.plaf.basic.BasicSplitPaneUI,
			   "SpacerUI", org.aswing.plaf.basic.BasicSpacerUI,
			   "TableUI", org.aswing.plaf.basic.BasicTableUI, 
			   "TableHeaderUI", org.aswing.plaf.basic.BasicTableHeaderUI, 
			   "TreeUI", org.aswing.plaf.basic.BasicTreeUI, 
			   "ToolBarUI", org.aswing.plaf.basic.BasicToolBarUI*/
		   ];
		table.putDefaults(uiDefaults);
	}
	
	protected function initSystemColorDefaults(table:UIDefaults):void{
			var defaultSystemColors:Array = [
				"activeCaption", 0xF2F2F2, /* Color for captions (title bars) when they are active. */
				"activeCaptionText", 0x000000, /* Text color for text in captions (title bars). */
				"activeCaptionBorder", 0xC0C0C0, /* Border color for caption (title bar) window borders. */
				"inactiveCaption", 0xE7E7E7, /* Color for captions (title bars) when not active. */
				"inactiveCaptionText", 0x888888, /* Text color for text in inactive captions (title bars). */
				"inactiveCaptionBorder", 0x888888, /* Border color for inactive caption (title bar) window borders. */
				"window", 0xCCCCCC, /* Default color for the interior of windows */
				"windowBorder", 0x000000, /* ??? */
				"windowText", 0x000000, /* ??? */
				"menu", 0xEEEEEE, /* Background color for menus */
				"menuText", 0x000000, /* Text color for menus  */
				"text", 0xC0C0C0, /* Text background color */
				"textText", 0x000000, /* Text foreground color */
				"textHighlight", 0x000080, /* Text background color when selected */
				"textHighlightText", 0xFFFFFF, /* Text color when selected */
				"textInactiveText", 0x808080, /* Text color when disabled */
				"selectionBackground", 0x3C73CE, //0x316AC5, 
				"selectionForeground", 0xFFFFFF, 
				"control", 0xF4F4F4,//0xEFEFEF, /* Default color for controls (buttons, sliders, etc) */
				"controlText", 0x000000, /* Default color for text in controls */
				"controlHighlight", 0xEEEEEE, /* Specular highlight (opposite of the shadow) */
				"controlLtHighlight", 0x666666, /* Highlight color for controls */
				"controlShadow", 0xC7C7C5, /* Shadow color for controls */
				"controlDkShadow", 0x666666, /* Dark shadow color for controls */
				"scrollbar", 0xE0E0E0 /* Scrollbar background (usually the "track") */
			];
					
			for(var i:Number=0; i<defaultSystemColors.length; i+=2){
				table.put(defaultSystemColors[i], new ASColorUIResource(defaultSystemColors[i+1]));
			}
			table.put("focusInner", new ASColorUIResource(0x40FF40, 0.3));
			table.put("focusOutter", new ASColorUIResource(0x40FF40, 0.4));
					
	}
	
	protected function initSystemFontDefaults(table:UIDefaults):void{
		var defaultSystemFonts:Array = [
				"systemFont", new ASFontUIResource("Tahoma", 11), 
				"menuFont", new ASFontUIResource("Tahoma", 11), 
				"controlFont", new ASFontUIResource("Tahoma", 11), 
				"windowFont", new ASFontUIResource("Tahoma", 11)
		];
		table.putDefaults(defaultSystemFonts);
	}
	
	protected function initCommonUtils(table:UIDefaults):void{
		ResizerController.setDefaultResizerClass(DefaultResizer);
		var arrowColors:Array = [
			"resizeArrow", table.get("inactiveCaption"),
			"resizeArrowLight", table.get("window"),
			"resizeArrowDark", table.get("activeCaptionText"),
		];
		table.putDefaults(arrowColors);
		
		var cursors:Array = [
			"System.hResizeCursor", org.aswing.plaf.basic.cursor.H_ResizeCursor,
			"System.vResizeCursor", org.aswing.plaf.basic.cursor.V_ResizeCursor,
			"System.hvResizeCursor", org.aswing.plaf.basic.cursor.HV_ResizeCursor,
			"System.hMoveCursor", org.aswing.plaf.basic.cursor.H_ResizeCursor,
			"System.vMoveCursor", org.aswing.plaf.basic.cursor.V_ResizeCursor,
			"System.hvMoveCursor", org.aswing.plaf.basic.cursor.HV_ResizeCursor	
		];
		table.putDefaults(cursors);
		
		
	}
	
	protected function initComponentDefaults(table:UIDefaults):void{
		var buttonBG:ASColorUIResource = new ASColorUIResource(0xE7E7E5);
		// *** Button
		var comDefaults:Array = [
			"Button.background", buttonBG,
			"Button.foreground", table.get("controlText"),
			"Button.opaque", true,  
			"Button.focusable", true,  
			"Button.shadow", table.getColor("controlShadow"),		
			"Button.darkShadow", table.getColor("controlDkShadow"),		
			"Button.light", table.getColor("controlHighlight"),	   
	   		"Button.highlight", table.getColor("controlLtHighlight"),
			"Button.font", table.getFont("controlFont"),
			"Button.bg", org.aswing.plaf.basic.background.ButtonBackground,
			"Button.margin", new InsetsUIResource(2, 3, 3, 2), 
			"Button.textShiftOffset", 1
		];
		table.putDefaults(comDefaults);
		
		// *** LabelButton
		comDefaults = [
			"LabelButton.background", NULL_COLOR,
			"LabelButton.foreground", new ASColorUIResource(0x669900),
			"LabelButton.opaque", false,  
			"LabelButton.focusable", true,  
			"LabelButton.rollOver", new ASColorUIResource(0xFF9966),		
			"LabelButton.pressed", new ASColorUIResource(0x558000),		
			"LabelButton.font", table.getFont("controlFont"),
			"LabelButton.margin", new InsetsUIResource(0, 0, 0, 0), 
			"LabelButton.textShiftOffset", 0
		];
		table.putDefaults(comDefaults);
		
		// *** Panel
		comDefaults = [
			"Panel.background", table.get("window"),
			"Panel.foreground", table.get("windowText"),
			"Panel.opaque", false,  
			"Panel.focusable", false,
			"Panel.font", table.getFont("windowFont")
		];
		table.putDefaults(comDefaults);
		
		// *** ToggleButton
		comDefaults = [
			"ToggleButton.background", buttonBG,
			"ToggleButton.foreground", table.get("controlText"),
			"ToggleButton.opaque", true, 
			"ToggleButton.focusable", true, 
			"ToggleButton.shadow", table.getColor("controlShadow"),		
			"ToggleButton.darkShadow", table.getColor("controlDkShadow"),		
			"ToggleButton.light", table.getColor("controlHighlight"),	   
	   		"ToggleButton.highlight", table.getColor("controlLtHighlight"),
			"ToggleButton.font", table.getFont("controlFont"),
			"ToggleButton.bg", org.aswing.plaf.basic.background.ToggleButtonBackground,
			"ToggleButton.margin", new InsetsUIResource(2, 3, 3, 2), 
			"ToggleButton.textShiftOffset", 1
		];
		table.putDefaults(comDefaults);
		
		// *** RadioButton
		comDefaults = [
			"RadioButton.background", new ASColorUIResource(0xDCDBD8),
			"RadioButton.foreground", table.get("controlText"),
			"RadioButton.opaque", false, 
			"RadioButton.focusable", true, 
			"RadioButton.shadow", table.getColor("controlShadow"),		
			"RadioButton.darkShadow", table.getColor("controlDkShadow"),		
			"RadioButton.light", table.getColor("controlHighlight"),	   
	   		"RadioButton.highlight", table.getColor("controlLtHighlight"),
			"RadioButton.font", table.getFont("controlFont"),
			"RadioButton.icon", org.aswing.plaf.basic.icon.RadioButtonIcon,
			"RadioButton.margin", new InsetsUIResource(0, 0, 0, 0), 
			"RadioButton.textShiftOffset", 1
		];
		table.putDefaults(comDefaults);
		
		// *** CheckBox
		comDefaults = [
			"CheckBox.background", new ASColorUIResource(0xDCDBD8),
			"CheckBox.foreground", table.get("controlText"),
			"CheckBox.opaque", false, 
			"CheckBox.focusable", true, 
			"CheckBox.shadow", table.getColor("controlShadow"),		
			"CheckBox.darkShadow", table.getColor("controlDkShadow"),		
			"CheckBox.light", table.getColor("controlHighlight"),	   
	   		"CheckBox.highlight", table.getColor("controlLtHighlight"),
			"CheckBox.font", table.getFont("controlFont"),
			"CheckBox.icon", org.aswing.plaf.basic.icon.CheckBoxIcon,
			"CheckBox.margin", new InsetsUIResource(0, 0, 0, 0), 
			"CheckBox.textShiftOffset", 1
		];
		table.putDefaults(comDefaults);
		
	   // *** JTabbedPane
		comDefaults = [
			"TabbedPane.background", new ASColorUIResource(0xE7E7E5),
			"TabbedPane.foreground", table.get("controlText"),
			"TabbedPane.opaque", false,  
			"TabbedPane.focusable", true,  
			"TabbedPane.shadow", new ASColorUIResource(0x888888),		
			"TabbedPane.darkShadow", new ASColorUIResource(0x444444),		
			"TabbedPane.light", table.getColor("controlHighlight"),	   
	   		"TabbedPane.highlight", new ASColorUIResource(0xFFFFFF),
			"TabbedPane.arrowShadowColor", new ASColorUIResource(0x000000),
			"TabbedPane.arrowLightColor", new ASColorUIResource(0x444444),
			"TabbedPane.font", table.getFont("controlFont"),
			"TabbedPane.border", null,
			"TabbedPane.tabMargin", new InsetsUIResource(1, 1, 1, 1),
			"TabbedPane.baseLineThickness", 8,
			"TabbedPane.maxTabWidth", 1000,
			//"TabbedPane.itemDecorator", org.aswing.plaf.basic.background.TabbedPaneItem			
		];
		table.putDefaults(comDefaults);
		
		// *** Separator
		comDefaults = [
			"Separator.background", NULL_COLOR,
			"Separator.foreground", NULL_COLOR,
			"Separator.opaque", false, 
			"Separator.focusable", false 
		];
		table.putDefaults(comDefaults);		
		
		// *** ScrollBar
		comDefaults = [
			"ScrollBar.background", new ASColorUIResource(0xD0D0D0),
			"ScrollBar.foreground", table.get("controlText"),
			"ScrollBar.opaque", true,  
			"ScrollBar.focusable", true, 
			"ScrollBar.barWidth", 16, 
			"ScrollBar.minimumThumbLength", 9, 
			"ScrollBar.font", table.getFont("controlFont"),
			"ScrollBar.thumbBackground", table.get("control"),
			"ScrollBar.thumbShadow", table.get("controlShadow"),
			"ScrollBar.thumbDarkShadow", table.get("controlDkShadow"),
			"ScrollBar.thumbHighlight", table.get("controlHighlight"),
			"ScrollBar.thumbLightHighlight", table.get("controlLtHighlight"),
			"ScrollBar.thumbDecorator", org.aswing.plaf.basic.background.ScrollBarThumb,
			"ScrollBar.arrowShadowColor", new ASColorUIResource(0x000000),
			"ScrollBar.arrowLightColor", new ASColorUIResource(0x444444)
		];
		table.putDefaults(comDefaults);
		
		// *** ScrollPane
		comDefaults = [
			"ScrollPane.background", table.get("window"),
			"ScrollPane.foreground", table.get("windowText"),
			"ScrollPane.opaque", false,  
			"ScrollPane.focusable", false, 
			"ScrollPane.font", table.getFont("windowFont")
		];
		table.putDefaults(comDefaults);
		
		// *** ProgressBar
		comDefaults = [
			"ProgressBar.background", table.get("window"),
			"ProgressBar.foreground", table.get("windowText"),
			"ProgressBar.opaque", false,
			"ProgressBar.focusable", false,			
			"ProgressBar.font", new ASFontUIResource("Tahoma", 9),
			"ProgressBar.border", org.aswing.plaf.basic.border.ProgressBarBorder,
			"ProgressBar.fg", org.aswing.plaf.basic.background.ProgressBarIcon,
			"ProgressBar.progressColor", new ASColorUIResource(0x3366CC), 
			"ProgressBar.indeterminateDelay", 40
		];
		table.putDefaults(comDefaults);
		
		// *** Panel
		comDefaults = [
			"Viewport.background", table.get("window"),
			"Viewport.foreground", table.get("windowText"),
			"Viewport.opaque", false, 
			"Viewport.focusable", true, 
			"Viewport.font", table.getFont("windowFont")
		];
		table.putDefaults(comDefaults);
		
	   // *** Label
		comDefaults = [
			"Label.background", table.get("control"),
			"Label.foreground", table.get("controlText"),
			"Label.opaque", false, 
			"Label.focusable", false, 
			"Label.font", table.getFont("controlFont")
		];
		table.putDefaults(comDefaults);
		
	   // *** TextField
		comDefaults = [
			"TextField.background", new ASColorUIResource(0xF3F3F3),
			"TextField.foreground", new ASColorUIResource(0x000000),
			"TextField.opaque", true,  
			"TextField.focusable", true,
			"TextField.light", new ASColorUIResource(0xDCDEDD),
			"TextField.shadow", new ASColorUIResource(0x666666),
			"TextField.font", table.getFont("controlFont"),
			"TextField.bg", org.aswing.plaf.basic.background.TextComponentBackBround,
			"TextField.border", org.aswing.plaf.basic.border.TextFieldBorder
		];
		table.putDefaults(comDefaults);
		
	   // *** TextArea
		comDefaults = [
			"TextArea.background", new ASColorUIResource(0xF3F3F3),
			"TextArea.foreground", new ASColorUIResource(0x000000),
			"TextArea.opaque", true,  
			"TextArea.focusable", true,
			"TextArea.light", new ASColorUIResource(0xDCDEDD),
			"TextArea.shadow", new ASColorUIResource(0x666666),
			"TextArea.font", table.getFont("controlFont"),
			"TextArea.bg", org.aswing.plaf.basic.background.TextComponentBackBround,
			"TextArea.border", org.aswing.plaf.basic.border.TextAreaBorder
		];
		table.putDefaults(comDefaults);
		
		// *** Frame
		comDefaults = [
			"Frame.background", table.get("window"),
			"Frame.foreground", table.get("windowText"),
			"Frame.opaque", true,  
			"Frame.focusable", true,
			"Frame.dragDirectly", false, 
			"Frame.activeCaption", table.get("activeCaption"),
			"Frame.activeCaptionText", table.get("activeCaptionText"),
			"Frame.activeCaptionBorder", table.get("activeCaptionBorder"),
			"Frame.inactiveCaption", table.get("inactiveCaption"),
			"Frame.inactiveCaptionText", table.get("inactiveCaptionText"),
			"Frame.inactiveCaptionBorder", table.get("inactiveCaptionBorder"),
			"Frame.resizeArrow", table.get("inactiveCaption"),
			"Frame.resizeArrowLight", table.get("window"),
			"Frame.resizeArrowDark", table.get("activeCaptionText"),			
			"Frame.titleBarBG", org.aswing.plaf.basic.frame.BasicFrameTitleBarBG, 
			"Frame.titleBarHeight", 22, 
			"Frame.titleBarButtonGap", 2, 
			"Frame.resizer", org.aswing.resizer.DefaultResizer,
			"Frame.font", table.get("windowFont"),
			"Frame.border", org.aswing.plaf.basic.border.FrameBorder,
			"Frame.icon", org.aswing.plaf.basic.icon.TitleIcon,
			"Frame.iconifiedIcon", org.aswing.plaf.basic.icon.FrameIconifiedIcon,
			"Frame.normalIcon", org.aswing.plaf.basic.icon.FrameNormalIcon,
			"Frame.maximizeIcon", org.aswing.plaf.basic.icon.FrameMaximizeIcon,
			"Frame.closeIcon", org.aswing.plaf.basic.icon.FrameCloseIcon
		];
		table.putDefaults(comDefaults);		
		
		// *** ToolTip
		comDefaults = [
			"ToolTip.background", new ASColorUIResource(0xFFFFD5),
			"ToolTip.foreground", table.get("controlText"),
			"ToolTip.opaque", true, 
			"ToolTip.focusable", false, 
			"ToolTip.borderColor", table.get("controlText"),
			"ToolTip.font", table.getFont("controlFont"),
			"ToolTip.filters", [new DropShadowFilter(4.0, 45, 0, 1.0, 2.0, 2.0, 0.5)], 
			"ToolTip.border", org.aswing.plaf.basic.border.ToolTipBorder
		];
		table.putDefaults(comDefaults);
		
		// *** List
		comDefaults = [
			"List.font", table.getFont("controlFont"),
			"List.background", table.get("control"),
			"List.foreground", table.get("controlText"),
			"List.opaque", false, 
			"List.focusable", true, 
		    "List.selectionBackground", table.get("selectionBackground"),
		    "List.selectionForeground", table.get("selectionForeground")
		];
		table.putDefaults(comDefaults);
		
		// *** SplitPane
		comDefaults = [
			"SplitPane.background", table.get("window"), 
			"SplitPane.foreground", table.get("controlDkShadow"), 
			"SplitPane.opaque", false, 
			"SplitPane.focusable", true, 
			"SplitPane.defaultDividerSize", 10, 
			"SplitPane.font", table.getFont("windowFont"), 
			"SplitPane.border", undefined, 
			"SplitPane.presentDragColor", new ASColorUIResource(0x000000, 40)
		];
		table.putDefaults(comDefaults);	
		
		// *** Spacer
		comDefaults = [
			"Spacer.background", table.get("window"),
			"Spacer.foreground", table.get("window"),
			"Spacer.opaque", false,
			"Spacer.focusable", false
		];
		table.putDefaults(comDefaults);
		
		// *** ComboBox
		comDefaults = [
			"ComboBox.font", table.getFont("controlFont"),
			"ComboBox.background", table.get("control"),
			"ComboBox.foreground", table.get("controlText"),
			"ComboBox.opaque", true, 
			"ComboBox.focusable", true, 
			"ComboBox.popupBorder", org.aswing.plaf.basic.border.ComboBoxPopupBorder,	
			"ComboBox.shadow", table.getColor("controlShadow"),		
			"ComboBox.darkShadow", table.getColor("controlDkShadow"),		
			"ComboBox.light", table.getColor("controlHighlight"),	   
	   		"ComboBox.highlight", table.getColor("controlLtHighlight"),
			"ComboBox.border", org.aswing.plaf.basic.border.ComboBoxBorder,
			"ComboBox.arrowShadowColor", new ASColorUIResource(0x000000),
			"ComboBox.arrowLightColor", new ASColorUIResource(0x444444)
		];
		table.putDefaults(comDefaults);
		
		// *** Slider
		comDefaults = [
			"Slider.font", table.getFont("controlFont"),
			"Slider.background", table.get("control"),
			"Slider.foreground", table.get("controlText"),
			"Slider.opaque", false,  
			"Slider.focusable", true,  
			"Slider.shadow", table.getColor("controlShadow"),
			"Slider.darkShadow", table.getColor("controlDkShadow"),
			"Slider.light", table.getColor("controlHighlight"),
			"Slider.highlight", table.getColor("controlLtHighlight"),
			"Slider.font", table.getFont("controlFont"),
			"Slider.thumbIcon", org.aswing.plaf.basic.icon.SliderThumbIcon,
			"Slider.tickColor", table.get("controlDkShadow"),
			"Slider.progressColor", new ASColorUIResource(0xC4C4FE),
			"Slider.thumb", table.get("control"),
			"Slider.thumbShadow", table.get("controlShadow"),
			"Slider.thumbDarkShadow", table.get("controlDkShadow"),
			"Slider.thumbHighlight", table.get("controlHighlight"),
			"Slider.thumbLightHighlight", table.get("controlLtHighlight")
		];
		table.putDefaults(comDefaults);
		
		// *** Adjuster
		comDefaults = [
			"Adjuster.background", buttonBG,
			"Adjuster.foreground", table.get("controlText"),
			"Adjuster.opaque", false,  
			"Adjuster.focusable", true,  
			"Adjuster.shadow", table.getColor("controlShadow"),
			"Adjuster.darkShadow", table.getColor("controlDkShadow"),
			"Adjuster.light", table.getColor("controlHighlight"),
			"Adjuster.highlight", table.getColor("controlLtHighlight"),
			"Adjuster.font", table.getFont("controlFont"), 
			"Adjuster.thumbIcon", org.aswing.plaf.basic.adjuster.PopupSliderThumbIcon,
			"Adjuster.tickColor", table.get("controlDkShadow"),
			"Adjuster.progressColor", new ASColorUIResource(0xC4C4EE),
			"Adjuster.thumb", table.get("control"),
			"Adjuster.thumbShadow", table.get("controlShadow"),
			"Adjuster.thumbDarkShadow", table.get("controlDkShadow"),
			"Adjuster.thumbHighlight", table.get("controlHighlight"),
			"Adjuster.thumbLightHighlight", table.get("controlLtHighlight"),
			"Adjuster.arrowShadowColor", new ASColorUIResource(0x000000),
			"Adjuster.arrowLightColor", new ASColorUIResource(0x444444)
		];
		table.putDefaults(comDefaults);
		
		// *** ColorSwatches
		comDefaults = [
			"ColorSwatches.background", new ASColorUIResource(0xEEEEEE),
			"ColorSwatches.foreground", table.get("controlText"),
			"ColorSwatches.opaque", false,  
			"ColorSwatches.focusable", false,  
			"ColorSwatches.font", table.getFont("controlFont"),
			"ColorSwatches.border", undefined
		];
		table.putDefaults(comDefaults);
		
		// *** ColorMixer
		comDefaults = [
			"ColorMixer.background", new ASColorUIResource(0xEEEEEE),
			"ColorMixer.foreground", table.get("controlText"),
			"ColorMixer.opaque", false,  
			"ColorMixer.focusable", false,  
			"ColorMixer.font", table.getFont("controlFont"),
			"ColorMixer.border", undefined
		];
		table.putDefaults(comDefaults);
		
		// *** ColorChooser
		comDefaults = [
			"ColorChooser.background", table.get("window"),
			"ColorChooser.foreground", table.get("controlText"),
			"ColorChooser.opaque", false,  
			"ColorChooser.focusable", false,  
			"ColorChooser.font", table.getFont("controlFont"),
			"ColorChooser.border", org.aswing.plaf.basic.border.ColorChooserBorder
		];
		table.putDefaults(comDefaults);		
		
		// *** Accordion
		comDefaults = [
			"Accordion.font", table.getFont("controlFont"),
			"Accordion.background", table.get("window"),
			"Accordion.foreground", table.get("controlText"),
			"Accordion.opaque", false,  
			"Accordion.focusable", true,
			"Accordion.motionSpeed", 50, 
			"Accordion.tabMargin", new InsetsUIResource(2, 3, 3, 2)
		];
		table.putDefaults(comDefaults);
		
	   // *** JTabbedPane
		comDefaults = [
			"TabbedPane.background", new ASColorUIResource(0xE7E7E5),
			"TabbedPane.foreground", table.get("controlText"),
			"TabbedPane.opaque", false,  
			"TabbedPane.focusable", true,  
			"TabbedPane.shadow", new ASColorUIResource(0x888888),		
			"TabbedPane.darkShadow", new ASColorUIResource(0x444444),		
			"TabbedPane.light", table.getColor("controlHighlight"),	   
	   		"TabbedPane.highlight", new ASColorUIResource(0xFFFFFF),
			"TabbedPane.arrowShadowColor", new ASColorUIResource(0x000000),
			"TabbedPane.arrowLightColor", new ASColorUIResource(0x444444),
			"TabbedPane.font", table.getFont("controlFont"),
			"TabbedPane.tabMargin", new InsetsUIResource(2, 3, 1, 3),
			"TabbedPane.contentMargin", new InsetsUIResource(8, 2, 2, 2), 
			"TabbedPane.contentRoundLineThickness", 1, 
			"TabbedPane.topBlankSpace", 4, 
			"TabbedPane.maxTabWidth", 1000];
		table.putDefaults(comDefaults);
		
	   // *** JClosableTabbedPane
		comDefaults = [
			"ClosableTabbedPane.background", new ASColorUIResource(0xE7E7E5),
			"ClosableTabbedPane.foreground", table.get("controlText"),
			"ClosableTabbedPane.opaque", false,  
			"ClosableTabbedPane.focusable", true,  
			"ClosableTabbedPane.shadow", new ASColorUIResource(0x888888),		
			"ClosableTabbedPane.darkShadow", new ASColorUIResource(0x444444),		
			"ClosableTabbedPane.light", table.getColor("controlHighlight"),	   
	   		"ClosableTabbedPane.highlight", new ASColorUIResource(0xFFFFFF),
			"ClosableTabbedPane.arrowShadowColor", new ASColorUIResource(0x000000),
			"ClosableTabbedPane.arrowLightColor", new ASColorUIResource(0x444444),
			"ClosableTabbedPane.font", table.getFont("controlFont"),
			"ClosableTabbedPane.tabMargin", new InsetsUIResource(2, 3, 1, 3),
			"ClosableTabbedPane.contentMargin", new InsetsUIResource(8, 2, 2, 2), 
			"ClosableTabbedPane.contentRoundLineThickness", 1, 
			"ClosableTabbedPane.topBlankSpace", 4, 
			"ClosableTabbedPane.maxTabWidth", 1000];
		table.putDefaults(comDefaults);
		
		 // *** Table
		comDefaults = [
			"Table.background", table.get("control"),
			"Table.foreground", table.get("controlText"),
			"Table.opaque", true, 
			"Table.focusable", true, 
			"Table.font", table.getFont("controlFont"),
		    "Table.selectionBackground", table.get("selectionBackground"),
		    "Table.selectionForeground", table.get("selectionForeground"), 
			"Table.gridColor", new ASColorUIResource(0x444444),
			"Table.border", undefined
		];
		table.putDefaults(comDefaults);
		
		 // *** TableHeader
		comDefaults = [
			"TableHeader.background", buttonBG,
			"TableHeader.foreground", table.get("controlText"),
			"TableHeader.font", table.getFont("controlFont"),
			"TableHeader.opaque", true, 
			"TableHeader.focusable", true, 
			"TableHeader.gridColor", new ASColorUIResource(0x444444),
			"TableHeader.border", undefined, 
			"TableHeader.cellBorder", org.aswing.plaf.basic.border.TableHeaderCellBorder, 
			"TableHeader.sortableCellBorder", org.aswing.plaf.basic.border.TableHeaderCellBorder
		];
		table.putDefaults(comDefaults);
		
		 // *** Tree
		comDefaults = [
			"Tree.background", table.get("control"),
			"Tree.foreground", table.get("controlText"),
			"Tree.opaque", true,  
			"Tree.focusable", true,
			"Tree.font", table.getFont("controlFont"),
		    "Tree.selectionBackground", table.get("selectionBackground"),
		    "Tree.selectionForeground", table.get("selectionForeground"), 
			"Tree.leafIcon", org.aswing.tree.TreeLeafIcon, 
			"Tree.folderExpandedIcon", org.aswing.tree.TreeFolderIcon, 
			"Tree.folderCollapsedIcon", org.aswing.tree.TreeFolderIcon, 
			"Tree.leftChildIndent", 10, 
			"Tree.rightChildIndent", 0, 
			"Tree.rowHeight", 16, 
			"Tree.expandControl", org.aswing.plaf.basic.tree.BasicExpandControl, 
			"Tree.border", undefined
		];
		table.putDefaults(comDefaults);
		
		 // *** ToolBar
		comDefaults = [
			"ToolBar.background", table.get("window"),
			"ToolBar.foreground", table.get("windowText"),
			"ToolBar.opaque", true, 
			"ToolBar.focusable", false 
		];
		table.putDefaults(comDefaults);
	    
	     // *** MenuItem
	    comDefaults = [
		    "MenuItem.background", table.get("menu"),
		    "MenuItem.foreground", table.get("menuText"),
	    	"MenuItem.opaque", false,  
	    	"MenuItem.focusable", false,
	        "MenuItem.font", table.getFont("menuFont"),
		    "MenuItem.selectionBackground", table.get("selectionBackground"),
		    "MenuItem.selectionForeground", table.get("selectionForeground"),
		    "MenuItem.disabledForeground", new ASColorUIResource(0x888888),
		    "MenuItem.acceleratorFont", table.getFont("menuFont"),
		    "MenuItem.acceleratorForeground", table.get("menuText"),
		    "MenuItem.acceleratorSelectionForeground", table.get("menu"),
	    	"MenuItem.border", undefined,
	    	"MenuItem.arrowIcon", org.aswing.plaf.basic.icon.MenuItemArrowIcon,
	    	"MenuItem.checkIcon", org.aswing.plaf.basic.icon.MenuItemCheckIcon,
			"MenuItem.margin", new InsetsUIResource(0, 0, 0, 0)
	    ];
	    table.putDefaults(comDefaults);
	    
	     // *** CheckBoxMenuItem
	    comDefaults = [
		    "CheckBoxMenuItem.background", table.get("menu"),
		    "CheckBoxMenuItem.foreground", table.get("menuText"),
	    	"CheckBoxMenuItem.opaque", false,  
	    	"CheckBoxMenuItem.focusable", false,
	        "CheckBoxMenuItem.font", table.getFont("menuFont"),
		    "CheckBoxMenuItem.selectionBackground", table.get("selectionBackground"),
		    "CheckBoxMenuItem.selectionForeground", table.get("selectionForeground"),
		    "CheckBoxMenuItem.disabledForeground", new ASColorUIResource(0x888888),
		    "CheckBoxMenuItem.acceleratorFont", table.getFont("menuFont"),
		    "CheckBoxMenuItem.acceleratorForeground", table.get("menuText"),
		    "CheckBoxMenuItem.acceleratorSelectionForeground", table.get("menu"),
	    	"CheckBoxMenuItem.border", undefined,
	    	"CheckBoxMenuItem.arrowIcon", org.aswing.plaf.basic.icon.MenuItemArrowIcon,
	    	"CheckBoxMenuItem.checkIcon", org.aswing.plaf.basic.icon.CheckBoxMenuItemCheckIcon,
			"CheckBoxMenuItem.margin", new InsetsUIResource(0, 0, 0, 0)
	    ];
	    table.putDefaults(comDefaults);
	    
	     // *** RadioButtonMenuItem
	    comDefaults = [
		    "RadioButtonMenuItem.background", table.get("menu"),
		    "RadioButtonMenuItem.foreground", table.get("menuText"),
	    	"RadioButtonMenuItem.opaque", false,  
	    	"RadioButtonMenuItem.focusable", false,
	        "RadioButtonMenuItem.font", table.getFont("menuFont"),
		    "RadioButtonMenuItem.selectionBackground", table.get("selectionBackground"),
		    "RadioButtonMenuItem.selectionForeground", table.get("selectionForeground"),
		    "RadioButtonMenuItem.disabledForeground", new ASColorUIResource(0x888888),
		    "RadioButtonMenuItem.acceleratorFont", table.getFont("menuFont"),
		    "RadioButtonMenuItem.acceleratorForeground", table.get("menuText"),
		    "RadioButtonMenuItem.acceleratorSelectionForeground", table.get("menu"),
	    	"RadioButtonMenuItem.border", undefined,
	    	"RadioButtonMenuItem.arrowIcon", org.aswing.plaf.basic.icon.MenuItemArrowIcon,
	    	"RadioButtonMenuItem.checkIcon", org.aswing.plaf.basic.icon.RadioButtonMenuItemCheckIcon,
			"RadioButtonMenuItem.margin", new InsetsUIResource(0, 0, 0, 0)
	    ];
	    table.putDefaults(comDefaults);
	    
	     // *** Menu
	    comDefaults = [
		    "Menu.background", table.get("menu"),
		    "Menu.foreground", table.get("menuText"),
	    	"Menu.opaque", false,  
	    	"Menu.focusable", false,
	        "Menu.font", table.getFont("menuFont"),
		    "Menu.selectionBackground", table.get("selectionBackground"),
		    "Menu.selectionForeground", table.get("selectionForeground"),
		    "Menu.disabledForeground", new ASColorUIResource(0x888888),
		    "Menu.acceleratorFont", table.getFont("menuFont"),
		    "Menu.acceleratorForeground", table.get("menuText"),
		    "Menu.acceleratorSelectionForeground", table.get("menu"),
	    	"Menu.border", undefined,
	    	"Menu.arrowIcon", org.aswing.plaf.basic.icon.MenuArrowIcon,
	    	"Menu.checkIcon", org.aswing.plaf.basic.icon.MenuCheckIcon,
			"Menu.margin", new InsetsUIResource(0, 0, 0, 0),
			"Menu.useMenuBarBackgroundForTopLevel", true, 
			"Menu.menuPopupOffsetX", 0, 
			"Menu.menuPopupOffsetY", 0, 
			"Menu.submenuPopupOffsetX", -4, 
			"Menu.submenuPopupOffsetY", 0
	    ];
	    table.putDefaults(comDefaults);
	    
	     // *** PopupMenu
	    comDefaults = [
		    "PopupMenu.background", table.get("menu"),
		    "PopupMenu.foreground", table.get("menuText"),
	    	"PopupMenu.opaque", true,  
	    	"PopupMenu.focusable", false,
	        "PopupMenu.font", table.getFont("menuFont"),
	        "PopupMenu.borderColor", table.get("controlDkShadow"),
	    	"PopupMenu.border", org.aswing.plaf.basic.border.PopupMenuBorder
	    ];
	    table.putDefaults(comDefaults);
	    
	    // *** MenuBar
	    comDefaults = [
		    "MenuBar.background", table.get("menu"), 
		    "MenuBar.foreground", table.get("menuText"), 
	    	"MenuBar.opaque", false, 
	    	"MenuBar.focusable", true, 
	        "MenuBar.font", table.getFont("menuFont"), 
	    	"MenuBar.border", undefined
	    ];
	    table.putDefaults(comDefaults);
	}
	
}
}