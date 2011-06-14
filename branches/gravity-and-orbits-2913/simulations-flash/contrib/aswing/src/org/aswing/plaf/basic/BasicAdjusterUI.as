/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.border.BevelBorder;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.overflow.JAdjuster;
import org.aswing.JButton;
import org.aswing.overflow.JPopup;
import org.aswing.JSlider;
import org.aswing.JTextField;
import org.aswing.LookAndFeel;
import org.aswing.MouseManager;
import org.aswing.plaf.AdjusterUI;
import org.aswing.plaf.basic.adjuster.PopupSliderUI;
import org.aswing.plaf.basic.icon.ArrowIcon;
import org.aswing.SoftBoxLayout;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.MathUtils;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicAdjusterUI extends AdjusterUI {
	
	private var adjuster:JAdjuster;
	private var inputText:JTextField;
	private var arrowButton:AbstractButton;
	private var popupSlider:JSlider;
	private var popupSliderUI:PopupSliderUI;
	
	private var thumbLightHighlightColor:ASColor;
    private var thumbHighlightColor:ASColor;
    private var thumbLightShadowColor:ASColor;
    private var thumbDarkShadowColor:ASColor;
    private var thumbColor:ASColor;
    private var arrowShadowColor:ASColor;
    private var arrowLightColor:ASColor;
    
    private var highlightColor:ASColor;
    private var shadowColor:ASColor;
    private var darkShadowColor:ASColor;
    private var lightColor:ASColor;	
    private var valueChangeListener:Object;
    private var inputTextListener:Object;
    private var arrowButtonListener:Object;
    private var popupSliderListener:Object;
	private var mouseMoveListener:Object;
	
	public function BasicAdjusterUI() {
		super();
	}

    public function installUI(c:Component):Void{
		adjuster = JAdjuster(c);
		adjuster.setLayout(new SoftBoxLayout(SoftBoxLayout.X_AXIS, 2, SoftBoxLayout.RIGHT));
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		adjuster = JAdjuster(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():Void{
		configureScrollBarColors();
		var pp:String = "Adjuster.";
        LookAndFeel.installColorsAndFont(adjuster, pp + "background", pp + "foreground", pp + "font");
		LookAndFeel.installBasicProperties(adjuster, pp);
        LookAndFeel.installBorder(adjuster, pp + "border");
	}
    private function configureScrollBarColors():Void{
    	LookAndFeel.installColorsAndFont(adjuster, "Adjuster.background", "Adjuster.foreground", "Adjuster.font");
    	
		thumbLightHighlightColor = UIManager.getColor("Adjuster.thumbLightHighlight");
		thumbHighlightColor = UIManager.getColor("Adjuster.thumbHighlight");
		thumbLightShadowColor = UIManager.getColor("Adjuster.thumbShadow");
		thumbDarkShadowColor = UIManager.getColor("Adjuster.thumbDarkShadow");
		thumbColor = UIManager.getColor("Adjuster.thumb");
		
		highlightColor = UIManager.getColor("Adjuster.highlight");
		shadowColor = UIManager.getColor("Adjuster.shadow");
		darkShadowColor = UIManager.getColor("Adjuster.darkShadow");
		lightColor = UIManager.getColor("Adjuster.light");
		
		arrowShadowColor = UIManager.getColor("Adjuster.arrowShadowColor");
		arrowLightColor = UIManager.getColor("Adjuster.arrowLightColor");
    }
    
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(adjuster);
    }
    
	private function installComponents():Void{
		inputText   = createInputText();
		arrowButton = createArrowButton();
		popupSlider = createPopupSlider();
		popupSliderUI = createPopupSliderUI();
		popupSlider.setUI(popupSliderUI);
		popupSlider.setModel(adjuster.getModel());
		adjuster.append(inputText);
		adjuster.append(arrowButton);
		arrowButton.setFocusable(false);
    }
	private function uninstallComponents():Void{
		adjuster.remove(inputText);
		adjuster.remove(arrowButton);
    }
	
	private function installListeners():Void{
		valueChangeListener = adjuster.addChangeListener(__valueChanged, this);
		
		inputTextListener = new Object();
		inputTextListener[JTextField.ON_ACT] = Delegate.create(this, __onInputTextAction);
		inputTextListener[JTextField.ON_FOCUS_LOST] = inputTextListener[JTextField.ON_ACT];
		inputTextListener[JTextField.ON_KEY_DOWN] = Delegate.create(this, __onInputTextKeyDown);
		inputTextListener[JTextField.ON_MOUSE_WHEEL] = Delegate.create(this, __onInputTextMouseWheel);
		getInputText().addEventListener(inputTextListener);
		
		arrowButtonListener = new Object();
		arrowButtonListener[JButton.ON_PRESS] = Delegate.create(this, __arrowButtonPressed);
		arrowButtonListener[JButton.ON_RELEASE] = Delegate.create(this, __arrowButtonReleased);
		arrowButtonListener[JButton.ON_RELEASEOUTSIDE] = arrowButtonListener[JButton.ON_RELEASE];
		arrowButton.addEventListener(arrowButtonListener);
		
		mouseMoveListener = new Object();
		mouseMoveListener[MouseManager.ON_MOUSE_MOVE] = Delegate.create(this, __onMouseMoveOnSlider);
	}
    
    private function uninstallListeners():Void{
    	adjuster.removeEventListener(valueChangeListener);
    	getInputText().removeEventListener(inputTextListener);
    	arrowButton.removeEventListener(arrowButtonListener);
    	MouseManager.removeEventListener(mouseMoveListener);
    }
    
	public function getInputText():JTextField{
		return inputText;
	}
	
	public function getPopupSlider():JSlider{
		return popupSlider;
	}    
	
	private function fillInputTextWithCurrentValue():Void{
		var value:Number = adjuster.getValue();
		var text:String = adjuster.getValueTranslator()(value);
		getInputText().setText(text);
	}
	//---------------------
	private function __valueChanged():Void{
		fillInputTextWithCurrentValue();
	}	
	private function __onInputTextMouseWheel(source:JTextField, delta:Number):Void{
		adjuster.setValue(adjuster.getValue()+delta*getUnitIncrement());
	}
	private function __onInputTextAction():Void{
		var text:String = getInputText().getText();
		var value:Number = adjuster.getValueParser()(text);
		adjuster.setValue(value);
		//revalidte a legic text
		fillInputTextWithCurrentValue();
		adjuster.fireActionEvent();
	}
	private function __onInputTextKeyDown():Void{
    	var code:Number = Key.getCode();
    	var unit:Number = getUnitIncrement();
    	var block:Number = popupSlider.getMajorTickSpacing() > 0 ? popupSlider.getMajorTickSpacing() : unit*10;
    	var delta:Number = 0;
    	if(code == Key.UP){
    		delta = unit;
    	}else if(code == Key.DOWN){
    		delta = -unit;
    	}else if(code == Key.PGUP){
    		delta = block;
    	}else if(code == Key.PGDN){
    		delta = -block;
    	}else if(code == Key.HOME){
    		adjuster.setValue(adjuster.getMinimum());
    		return;
    	}else if(code == Key.END){
    		adjuster.setValue(adjuster.getMaximum() - adjuster.getExtent());
    		return;
    	}
    	adjuster.setValue(adjuster.getValue() + delta);
	}
    private function getUnitIncrement():Number{
    	var unit:Number = 0;
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
	
	private var popup:JPopup;
	private var startMousePoint:Point;
	private var startValue:Number;
	
	private function getPopup():JPopup{
		if(popup == null){
			popup = new JPopup();
			popup.setBorder(new BevelBorder(null, BevelBorder.RAISED));
			popup.append(popupSlider, BorderLayout.CENTER);
		}
		return popup;
	}
	
	private function getSliderTrackWidth():Number{
		var sliderInsets:Insets = popupSliderUI.getTrackMargin();
		var w:Number = popupSlider.getWidth();
		if(w == 0){
			w = popupSlider.getPreferredWidth();
		}
		return w - sliderInsets.left - sliderInsets.right;
	}
	
	private function getSliderTrackHeight():Number{
		var sliderInsets:Insets = popupSliderUI.getTrackMargin();
		var h:Number = popupSlider.getHeight();
		if(h == 0){
			h = popupSlider.getPreferredHeight();
		}
		return h - sliderInsets.top - sliderInsets.bottom;
	}
	
	private function __arrowButtonPressed():Void{
		var popupWindow:JPopup = getPopup();
		if(popupWindow.isDisplayable()){
			popupWindow.dispose();
		}
		popupWindow.changeOwner(ASWingUtils.getOwnerAncestor(adjuster));
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
		var mouseP:Point = adjuster.getMousePosition();
		var windowP:Point = new Point(mouseP.x - pw/2, mouseP.y - ph/2);
		var value:Number = adjuster.getValue();
		if(adjuster.getOrientation() == JAdjuster.VERTICAL){
			var valueL:Number = (value - min)/(max - min) * sh;
			windowP.y = mouseP.y - (sh - valueL) - insets.top;
		}else{
			var valueL:Number = (value - min)/(max - min) * sw;
			windowP.x = mouseP.x - valueL - insets.left;
			windowP.y += adjuster.getHeight()/4;
		}
		var agp:Point = adjuster.getGlobalLocation();
		agp.move(windowP.x, windowP.y);
		MathUtils.roundPoint(agp);
		popupWindow.setLocation(agp);
		
		startMousePoint = adjuster.getMousePosition();
		startValue = adjuster.getValue();
		MouseManager.removeEventListener(mouseMoveListener);
		MouseManager.addEventListener(mouseMoveListener);
	}
	private function __arrowButtonReleased():Void{
		popup.dispose();
		MouseManager.removeEventListener(mouseMoveListener);
		adjuster.fireActionEvent();
	}
	private function __onMouseMoveOnSlider():Void{
		var delta:Number = 0;
		var valueDelta:Number = 0;
		var range:Number = adjuster.getMaximum() - adjuster.getMinimum();
		var p:Point = adjuster.getMousePosition();
		if(adjuster.getOrientation() == JAdjuster.VERTICAL){
			delta = -p.y + startMousePoint.y;
			valueDelta = delta/(getSliderTrackHeight()) * range;
		}else{
			delta = p.x - startMousePoint.x;
			valueDelta = delta/(getSliderTrackWidth()) * range;			
		}
		adjuster.setValue(startValue + valueDelta);
		updateAfterEvent();
	}
	//--------------------
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		inputText.setEditable(adjuster.isEditable());
		arrowButton.setEnabled(adjuster.isEditable() && adjuster.isEnabled());
		inputText.setEnabled(adjuster.isEnabled());
		inputText.setForeground(adjuster.getForeground());
		inputText.setFont(adjuster.getFont());
		arrowButton.setForeground(adjuster.getForeground());
		arrowButton.setFont(adjuster.getFont());
		popupSlider.setOrientation(adjuster.getOrientation());
		fillInputTextWithCurrentValue();
	}	
	
	//*******************************************************************************
	//              Override these methods to easiy implement different look
	//*******************************************************************************
	private function createInputText():JTextField{
		var tf:JTextField = new JTextField("", adjuster.getColumns());
		tf.setForeground(adjuster.getForeground());
		tf.setFont(adjuster.getFont());
		tf.setRegainTextFocusEnabled(false); //this make arrow button can be press continuesly
		return tf;
	}
	private function createArrowButton():AbstractButton{
		var btn:JButton = new JButton(createArrowIcon());
		btn.setMargin(new Insets(0, 0, 0, 1));
		btn.setForeground(adjuster.getForeground());
		btn.setFont(adjuster.getFont());
		return btn;
	}
	private function createPopupSlider():JSlider{
		var slider:JSlider = new JSlider(adjuster.getOrientation());
		return slider;
	}
	private function createPopupSliderUI():PopupSliderUI{
		return new PopupSliderUI();
	}
	private function createArrowIcon() : Icon {
		return new ArrowIcon(Math.PI/2, 6,
				    thumbColor,
				    arrowLightColor,
				    arrowShadowColor,
				    thumbHighlightColor);
	}

}