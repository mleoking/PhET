/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
import flash.display.InteractiveObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;

import org.aswing.*;
import org.aswing.border.BevelBorder;
import org.aswing.event.AWEvent;
import org.aswing.event.FocusKeyEvent;
import org.aswing.event.ReleaseEvent;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;
import org.aswing.plaf.basic.adjuster.PopupSliderUI;
import org.aswing.plaf.basic.icon.ArrowIcon;

/**
 * Basic adjust ui imp.
 * @author iiley
 * @private
 */
public class BasicAdjusterUI extends BaseComponentUI implements AdjusterUI{
	
	protected var adjuster:JAdjuster;
	protected var arrowButton:Component;
	protected var popup:JPopup;
	protected var inputText:JTextField;
	protected var popupSlider:JSlider;
	protected var popupSliderUI:SliderUI;
	protected var startMousePoint:IntPoint;
	protected var startValue:Number;
	
	protected var thumbLightHighlightColor:ASColor;
    protected var thumbHighlightColor:ASColor;
    protected var thumbLightShadowColor:ASColor;
    protected var thumbDarkShadowColor:ASColor;
    protected var thumbColor:ASColor;
    protected var arrowShadowColor:ASColor;
    protected var arrowLightColor:ASColor;
    
    protected var highlightColor:ASColor;
    protected var shadowColor:ASColor;
    protected var darkShadowColor:ASColor;
    protected var lightColor:ASColor;	
	
	public function BasicAdjusterUI(){
		super();
		inputText   = new JTextField("", 3);
		inputText.setFocusable(false);
		popupSlider = new JSlider();
		popupSlider.setFocusable(false);
	}
	
	public function getPopupSlider():JSlider{
		return popupSlider;
	}
	
	public function getInputText():JTextField{
		return inputText;
	}
	
    override public function installUI(c:Component):void{
    	adjuster = JAdjuster(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
    	adjuster = JAdjuster(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
    
	protected function getPropertyPrefix():String {
		return "Adjuster.";
	}
	
	protected function installDefaults():void{
		var pp:String = getPropertyPrefix();
        LookAndFeel.installBorderAndBFDecorators(adjuster, pp);
        LookAndFeel.installColorsAndFont(adjuster, pp);
        LookAndFeel.installBasicProperties(adjuster, pp);
		arrowShadowColor = getColor(pp+"arrowShadowColor");
		arrowLightColor = getColor(pp+"arrowLightColor");
	}
    
    protected function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(adjuster);
    }
    
	protected function installComponents():void{
		initInputText();
		initPopupSlider();
		arrowButton = createArrowButton();
		arrowButton.setUIElement(true);
		popupSlider.setUIElement(true);
		popupSliderUI = createPopupSliderUI();
		popupSlider.setUI(popupSliderUI);
		popupSlider.setModel(adjuster.getModel());
		adjuster.addChild(inputText);
		adjuster.addChild(arrowButton);
		
		inputText.addEventListener(MouseEvent.MOUSE_WHEEL, __onInputTextMouseWheel);
		arrowButton.addEventListener(MouseEvent.MOUSE_DOWN, __onArrowButtonPressed);
		arrowButton.addEventListener(ReleaseEvent.RELEASE, __onArrowButtonReleased);
    }
    
	protected function uninstallComponents():void{
		inputText.removeEventListener(MouseEvent.MOUSE_WHEEL, __onInputTextMouseWheel);
		arrowButton.removeEventListener(MouseEvent.MOUSE_DOWN, __onArrowButtonPressed);
		arrowButton.removeEventListener(ReleaseEvent.RELEASE, __onArrowButtonReleased);
		
		adjuster.removeChild(arrowButton);
		adjuster.removeChild(inputText);
		if(popup != null && popup.isVisible()){
			popup.dispose();
		}
    }
	
	protected function installListeners():void{
		adjuster.addStateListener(__onValueChanged);
		adjuster.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onInputTextKeyDown);
		adjuster.addEventListener(AWEvent.FOCUS_GAINED, __onFocusGained);
		adjuster.addEventListener(AWEvent.FOCUS_LOST, __onFocusLost);
	}
    
    protected function uninstallListeners():void{
		adjuster.removeStateListener(__onValueChanged);
		adjuster.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onInputTextKeyDown);
		adjuster.removeEventListener(AWEvent.FOCUS_GAINED, __onFocusGained);
		adjuster.removeEventListener(AWEvent.FOCUS_LOST, __onFocusLost);
    }
    
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		var text:String = getShouldFilledText();
		if(text != inputText.getText()){
			inputText.setText(text);
		}
		layoutAdjuster();
		getInputText().setEditable(adjuster.isEditable());
		getInputText().setEnabled(adjuster.isEnabled());
		arrowButton.setEnabled(adjuster.isEnabled());
	}
	
	
	//*******************************************************************************
	//              Override these methods to easily implement different look
	//*******************************************************************************
    /**
     * Returns the input text to receive the focus for the component.
     * @param c the component
     * @return the object to receive the focus.
     */
	override public function getInternalFocusObject(c:Component):InteractiveObject{
		return inputText.getTextField();
	}
	
	protected function initInputText():void{
		inputText.setColumns(adjuster.getColumns());
		inputText.setForeground(null);//make it grap the property from parent
		inputText.setFont(adjuster.getFont());
	}
	
	protected function initPopupSlider():void{
		popupSlider.setOrientation(adjuster.getOrientation());
	}
	
	protected function createArrowButton():Component{
		var btn:JButton = new JButton(null, createArrowIcon());
		btn.setMargin(new Insets(2, 2, 2, 2));
		btn.setForeground(null);//make it grap the property from parent
		btn.setBackground(null);//make it grap the property from parent
		btn.setFont(null);//make it grap the property from parent
		btn.setFocusable(false);
		return btn;
	}
	
	protected function createPopupSliderUI():SliderUI{
		return new PopupSliderUI();
	}
	
	protected function createArrowIcon() : Icon {
		return new ArrowIcon(Math.PI/2, 6,
				    arrowLightColor,
				    arrowShadowColor);
	}
		
	protected function getPopup():JPopup{
		if(popup == null){
			popup = new JPopup();
			popup.setBorder(new BevelBorder(null, BevelBorder.RAISED));
			popup.append(popupSlider, BorderLayout.CENTER);
			popup.setBackground(adjuster.getBackground());
		}
		return popup;
	}
	
	protected function fillInputTextWithCurrentValue():void{
		inputText.setText(getShouldFilledText());
	}
	
	protected function getShouldFilledText():String{
		var value:int = adjuster.getValue();
		var text:String = adjuster.getValueTranslator()(value);
		return text;
	}
	
	protected function getTextButtonGap():int{
		return 1;
	}
	
	protected function layoutAdjuster():void{
    	var td:IntDimension = adjuster.getSize();
		var insets:Insets = adjuster.getInsets();
		var top:int = insets.top;
		var left:int = insets.left;
		var right:int = td.width - insets.right;
		var gap:int = getTextButtonGap();
		
		var height:int = td.height - insets.top - insets.bottom;
    	var buttonSize:IntDimension = arrowButton.getPreferredSize(); 
    	arrowButton.setSizeWH(buttonSize.width, height);
    	arrowButton.setLocationXY(right - buttonSize.width, top);
    	inputText.setLocationXY(left, top);
    	inputText.setSizeWH(td.width - insets.left - insets.right - buttonSize.width-gap, height);		
	}
    
    override public function getPreferredSize(c:Component):IntDimension{
    	var insets:Insets = adjuster.getInsets();
    	var textSize:IntDimension = inputText.getPreferredSize();
    	var btnSize:IntDimension = arrowButton.getPreferredSize();
    	var size:IntDimension = new IntDimension(
    		textSize.width + getTextButtonGap() + btnSize.width,
    		Math.max(textSize.height, btnSize.height)
    	);
    	return insets.getOutsideSize(size);
    }

    override public function getMinimumSize(c:Component):IntDimension{
    	return adjuster.getInsets().getOutsideSize(arrowButton.getPreferredSize());
    }

    override public function getMaximumSize(c:Component):IntDimension{
		return IntDimension.createBigDimension();
    }    	
	
	//--------------------- handlers--------------------
	
	private function __onValueChanged(e:Event):void{
		fillInputTextWithCurrentValue();
	}
	
	private function __onInputTextMouseWheel(e:MouseEvent):void{
		adjuster.setValue(adjuster.getValue()+e.delta*getUnitIncrement());
	}
	
	private function __inputTextAction(fireActOnlyIfChanged:Boolean=false):void{
		var text:String = inputText.getText();
		var value:int = adjuster.getValueParser()(text);
		adjuster.setValue(value);
		//revalidte a legic text
		fillInputTextWithCurrentValue();
		if(!fireActOnlyIfChanged){
			fireActionEvent();
		}else if(value != startEditingValue){
			fireActionEvent();
		}
	}
	
	protected var startEditingValue:int;
	protected function fireActionEvent():void{
		startEditingValue = adjuster.getValue();
		adjuster.dispatchEvent(new AWEvent(AWEvent.ACT));
	}
	
	private function __onFocusGained(e:AWEvent):void{
		startEditingValue = adjuster.getValue();
	}
	
	private function __onFocusLost(e:AWEvent):void{
		__inputTextAction(true);
	}
	
	private function __onInputTextKeyDown(e:FocusKeyEvent):void{
    	var code:uint = e.keyCode;
    	var unit:int = getUnitIncrement();
    	var block:int = popupSlider.getMajorTickSpacing() > 0 ? popupSlider.getMajorTickSpacing() : unit*10;
    	var delta:int = 0;
    	if(code == Keyboard.ENTER){
    		__inputTextAction(false);
    		return;
    	}
    	if(code == Keyboard.UP){
    		delta = unit;
    	}else if(code == Keyboard.DOWN){
    		delta = -unit;
    	}else if(code == Keyboard.PAGE_UP){
    		delta = block;
    	}else if(code == Keyboard.PAGE_DOWN){
    		delta = -block;
    	}else if(code == Keyboard.HOME){
    		adjuster.setValue(adjuster.getMinimum());
    		return;
    	}else if(code == Keyboard.END){
    		adjuster.setValue(adjuster.getMaximum() - adjuster.getExtent());
    		return;
    	}
    	adjuster.setValue(adjuster.getValue() + delta);
	}
	
	private function __onArrowButtonPressed(e:Event):void{
		var popupWindow:JPopup = getPopup();
		if(popupWindow.isOnStage()){
			popupWindow.dispose();
		}
		popupWindow.changeOwner(AsWingUtils.getOwnerAncestor(adjuster));
		popupWindow.pack();
		popupWindow.show();
		var max:Number = adjuster.getMaximum();
		var min:Number = adjuster.getMinimum();
		var pw:Number = popupWindow.getWidth();
		var ph:Number = popupWindow.getHeight();
		var sw:Number = getSliderTrackWidth();
		var sh:Number = getSliderTrackHeight();
		var insets:Insets = popupWindow.getInsets();
		var sliderInsets:Insets = popupSliderUI.getTrackMargin();
		insets.top += sliderInsets.top;
		insets.left += sliderInsets.left;
		insets.bottom += sliderInsets.bottom;
		insets.right += sliderInsets.right;
		var mouseP:IntPoint = adjuster.getMousePosition();
		var windowP:IntPoint = new IntPoint(mouseP.x - pw/2, mouseP.y - ph/2);
		var value:Number = adjuster.getValue();
		var valueL:Number;
		if(adjuster.getOrientation() == JAdjuster.VERTICAL){
			valueL = (value - min)/(max - min) * sh;
			windowP.y = mouseP.y - (sh - valueL) - insets.top;
		}else{
			valueL = (value - min)/(max - min) * sw;
			windowP.x = mouseP.x - valueL - insets.left;
			windowP.y += adjuster.getHeight()/4;
		}
		var agp:IntPoint = adjuster.getGlobalLocation();
		agp.move(windowP.x, windowP.y);
		popupWindow.setLocation(agp);
		
		startMousePoint = adjuster.getMousePosition();
		startValue = adjuster.getValue();
		if(adjuster.stage){
			adjuster.stage.addEventListener(MouseEvent.MOUSE_MOVE, __onMouseMoveOnSlider, false, 0, true);
			adjuster.addEventListener(Event.REMOVED_FROM_STAGE, __onMouseMoveOnSliderRemovedFromStage, false, 0, true);
		}
	}
	
	private function __onMouseMoveOnSliderRemovedFromStage(e:Event):void{
		adjuster.stage.removeEventListener(MouseEvent.MOUSE_MOVE, __onMouseMoveOnSlider);
		adjuster.removeEventListener(Event.REMOVED_FROM_STAGE, __onMouseMoveOnSliderRemovedFromStage);
	}
	
	private function __onArrowButtonReleased(e:Event):void{
		if(adjuster.stage){
			__onMouseMoveOnSliderRemovedFromStage(null);
		}
		popup.dispose();
		fireActionEvent();
	}
	
	private function __onMouseMoveOnSlider(e:MouseEvent):void{
		var delta:Number = 0;
		var valueDelta:Number = 0;
		var range:Number = adjuster.getMaximum() - adjuster.getMinimum();
		var p:IntPoint = adjuster.getMousePosition();
		if(adjuster.getOrientation() == JAdjuster.VERTICAL){
			delta = -p.y + startMousePoint.y;
			valueDelta = delta/(getSliderTrackHeight()) * range;
		}else{
			delta = p.x - startMousePoint.x;
			valueDelta = delta/(getSliderTrackWidth()) * range;			
		}
		adjuster.setValue(startValue + valueDelta);
		e.updateAfterEvent();
	}	
	
    protected function getUnitIncrement():int{
    	var unit:int = 0;
    	if(popupSlider.getMinorTickSpacing() >0 ){
    		unit = popupSlider.getMinorTickSpacing();
    	}else if(popupSlider.getMajorTickSpacing() > 0){
    		unit = popupSlider.getMajorTickSpacing();
    	}else{
    		var range:Number = popupSlider.getMaximum() - popupSlider.getMinimum();
    		if(range > 2){
    			unit = Math.max(1, Math.round(range/500));
    		}else{
    			unit = range/100;
    		}
    	}
    	return unit;
    }
	
	protected function getSliderTrackWidth():Number{
		var sliderInsets:Insets = popupSliderUI.getTrackMargin();
		var w:Number = popupSlider.getWidth();
		if(w == 0){
			w = popupSlider.getPreferredWidth();
		}
		return w - sliderInsets.left - sliderInsets.right;
	}
	
	protected function getSliderTrackHeight():Number{
		var sliderInsets:Insets = popupSliderUI.getTrackMargin();
		var h:Number = popupSlider.getHeight();
		if(h == 0){
			h = popupSlider.getPreferredHeight();
		}
		return h - sliderInsets.top - sliderInsets.bottom;
	}
}
}