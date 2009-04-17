/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Action;
import org.aswing.ASWingConstants;
import org.aswing.ButtonModel;
import org.aswing.Component;
import org.aswing.overflow.GrayFilteredIcon;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.plaf.ButtonUI;
import org.aswing.plaf.InsetsUIResource;
import org.aswing.plaf.UIResource;
 
/**
 * Defines common behaviors for buttons and menu items.
 * @author iiley
 */
class org.aswing.AbstractButton extends Component{
	/**
	 * When the button's state changed.
	 *<br>
	 * onStateChanged(source:AbstractButton)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";//Component.ON_STATE_CHANGED; 	
 	
	/**
	 * When the button's selection changed. fired when diselected or selected.
	 *<br>
	 * onSelectionChanged(source:AbstractButton)
	 */	
	public static var ON_SELECTION_CHANGED:String = "onSelectionChanged";  	
 	
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var CENTER:Number  = ASWingConstants.CENTER;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var TOP:Number     = ASWingConstants.TOP;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var LEFT:Number    = ASWingConstants.LEFT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var BOTTOM:Number  = ASWingConstants.BOTTOM;
 	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var RIGHT:Number   = ASWingConstants.RIGHT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */        
	public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
        
    /** The data model that determines the button's state. */
    private var model:ButtonModel;
    private var modelStateListener:Object;
    private var modelSelectionListener:Object;
    
    private var action:Action;

    private var text:String; // for BeanBox
    private var margin:Insets;
    private var defaultMargin:Insets;

    // Button icons
    private var       defaultIcon:Icon;
    private var       pressedIcon:Icon;
    private var       disabledIcon:Icon;

    private var       selectedIcon:Icon;
    private var       disabledSelectedIcon:Icon;

    private var       rolloverIcon:Icon;
    private var       rolloverSelectedIcon:Icon;
    
    // Display properties
    private var    paintBorder:Boolean;
    private var    rolloverEnabled:Boolean;

    // Icon/Label Alignment
    private var        verticalAlignment:Number;
    private var        horizontalAlignment:Number;
    
    private var        verticalTextPosition:Number;
    private var        horizontalTextPosition:Number;

    private var        iconTextGap:Number;
    
    /**
     * AbstractButton(text:String, icon:Icon)<br>
     * AbstractButton(text:String)<br>
     * AbstractButton(icon:Icon)
     * <p>
     * AbstractButton is abstract, can't be constructed.
     */
    private function AbstractButton(text, icon:Icon){
    	super();
    	if(text instanceof Icon){
    		icon = Icon(text);
    		text = null;
    	}
		setName("AbstractButton");
    	paintBorder = true;
    	rolloverEnabled = false;
    	
    	verticalAlignment = CENTER;
    	horizontalAlignment = CENTER;
    	verticalTextPosition = CENTER;
    	horizontalTextPosition = RIGHT;
    	
    	iconTextGap = 2;
    	
    	setText(text);
    	setIcon(icon);
    }

    /**
     * Returns the model that this button represents.
     * @return the <code>model</code> property
     * @see #setModel()
     */
    public function getModel():ButtonModel{
        return model;
    }
    
    /**
     * Sets the model that this button represents.
     * @param m the new <code>ButtonModel</code>
     * @see #getModel()
     */
    public function setModel(newModel:ButtonModel):Void {
        
        var oldModel:ButtonModel = getModel();
        
        if (oldModel != null) {
            oldModel.removeEventListener(modelStateListener);
            oldModel.removeEventListener(modelSelectionListener);
            modelStateListener = null;
            modelSelectionListener = null;
        }
        
        model = newModel;
        
        if (newModel != null) {
            modelStateListener = newModel.addChangeListener(__onModelStateChanged, this);
            modelSelectionListener = newModel.addSelectionListener(__onModelSelectionChanged, this);
        }

        if (newModel != oldModel) {
            revalidate();
            repaint();
        }
    }
	
	private function __onModelStateChanged():Void{
		fireStateChanged();
	}
	
	private function __onModelSelectionChanged():Void{
		dispatchEvent(createEventObj(ON_SELECTION_CHANGED));
	}
    
    /**
     * Returns the L&F object that renders this component.
     * @return the ButtonUI object
     * @see #setUI()
     */
    public function getUI():ButtonUI {
        return ButtonUI(ui);
    }

    
    /**
     * Sets the L&F object that renders this component.
     * @param ui the <code>ButtonUI</code> L&F object
     * @see #getUI()
     */
    public function setUI(ui:ButtonUI):Void {
        super.setUI(ui);
    }

    
    /**
     * Resets the UI property to a value from the current look
     * and feel.  Subtypes of <code>AbstractButton</code>
     * should override this to update the UI. For
     * example, <code>JButton</code> might do the following:
     * <pre>
     *      setUI(ButtonUI(UIManager.getUI(this)));
     * </pre>
     */
    public function updateUI():Void{
    }
    
    /**
     * addActionListener(fuc:Function, obj:Object)<br>
     * addActionListener(fuc:Function)<br>
     * Adds a action listener to this button. Buttons fire a action event when 
     * user released on it.
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
     * @return the listener just added.
     * @see org.aswing.EventDispatcher#ON_ACT
     * @see Component#ON_RELEASE
     */
    public function addActionListener(fuc:Function, obj:Object):Object{
    	return addEventListener(ON_ACT, fuc, obj);
    }
    
    /**
     * addSelectionListener(fuc:Function, obj:Object)<br>
     * addSelectionListener(fuc:Function)
     * <p>
     * Adds a action listener to this button's selection change event. 
     * @param fuc the listener function.
     * @param Context in which to run the function of param func.
     * @return the listener just added.
     * @see #ON_SELECTION_CHANGED
     * @see ButtonModel#addSelectionListener()
     */    
    public function addSelectionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_SELECTION_CHANGED, func, obj);
	}
	
	/**
	 * Sets the action for the ON_ACT event hand.
	 */
	public function setAction(ac:Action):Void{
		if(action != ac){
			removeEventListener(action);
			action = ac;
			addEventListener(action);
		}
	}
	
	/**
	 * Returns the action.
	 */
	public function getAction():Action{
		return action;
	}
    
    /**
     * Enabled (or disabled) the button.
     * @param b  true to enable the button, otherwise false
     */
	public function setEnabled(b:Boolean):Void{
		if (!b && model.isRollOver()) {
	    	model.setRollOver(false);
		}
        super.setEnabled(b);
        model.setEnabled(b);
    }    

    /**
     * Returns the state of the button. True if the
     * toggle button is selected, false if it's not.
     * @return true if the toggle button is selected, otherwise false
     */
    public function isSelected():Boolean{
        return model.isSelected();
    }
    
    /**
     * Sets the state of the button. Note that this method does not
     * trigger an Event for users.
     * Call <code>click</code> to perform a programatic action change.
     *
     * @param b  true if the button is selected, otherwise false
     */
    public function setSelected(b:Boolean):Void{
        model.setSelected(b);
    }
    
    /**
     * Sets the <code>rolloverEnabled</code> property, which
     * must be <code>true</code> for rollover effects to occur.
     * The default value for the <code>rolloverEnabled</code>
     * property is <code>false</code>.
     * Some look and feels might not implement rollover effects;
     * they will ignore this property.
     * 
     * @param b if <code>true</code>, rollover effects should be painted
     * @see #isRollOverEnabled()
     */
    public function setRollOverEnabled(b:Boolean):Void{
    	if(rolloverEnabled != b){
    		rolloverEnabled = b;
    		repaint();
    	}
    }
    
    /**
     * Gets the <code>rolloverEnabled</code> property.
     *
     * @return the value of the <code>rolloverEnabled</code> property
     * @see #setRollOverEnabled()
     */    
    public function isRollOverEnabled():Boolean{
    	return rolloverEnabled;
    }

	/**
	 * Sets space for margin between the button's border and
     * the label. Setting to <code>null</code> will cause the button to
     * use the default margin.  The button's default <code>Border</code>
     * object will use this value to create the proper margin.
     * However, if a non-default border is set on the button, 
     * it is that <code>Border</code> object's responsibility to create the
     * appropriate margin space (else this property will
     * effectively be ignored).
     *
     * @param m the space between the border and the label
	 */
	public function setMargin(m:Insets):Void{
        // Cache the old margin if it comes from the UI
        if(m instanceof UIResource) {
            defaultMargin = m;
        }
        
        // If the client passes in a null insets, restore the margin
        // from the UI if possible
        if(m == null && defaultMargin != null) {
            m = defaultMargin;
        }

        var old:Insets = margin;
        margin = m;
        if (old == null || !m.equals(old)) {
            revalidate();
            //repaint();
        }
	}
	
	public function getMargin():Insets{
		var m:Insets = margin;
		if(margin == null){
			m = defaultMargin;
		}
		if(m == null || m instanceof UIResource){//make it can be replaced by LAF
			return new InsetsUIResource(m.top, m.left, m.bottom, m.right);
		}else{
			return new Insets(m.top, m.left, m.bottom, m.right);
		}
	}
	
	/**
	 * Sets text and icon at one method here.
	 * @param text the text for the button
	 * @param icon the default icon for the button
	 * @see #setText()
	 * @see #setIcon()
	 */
	public function setContent(text:String, icon:Icon):Void{
		if(this.text != text){
			this.text = text;
		}
		if(this.defaultIcon != icon){
			uninstallIconWhenNextPaint(this.defaultIcon);
			this.defaultIcon = icon;
		}
		repaint();
		invalidate();
	}
	
	public function setText(text:String):Void{
		if(this.text != text){
			this.text = text;
			repaint();
			invalidate();
		}
	}
	
	public function getText():String{
		return text;
	}
	
	public function setIcon(defaultIcon:Icon):Void{
		if(this.defaultIcon != defaultIcon){
			uninstallIconWhenNextPaint(this.defaultIcon);
			this.defaultIcon = defaultIcon;
			repaint();
			invalidate();
		}
	}

	public function getIcon():Icon{
		return defaultIcon;
	}
    
    /**
     * Returns the pressed icon for the button.
     * @return the <code>pressedIcon</code> property
     * @see #setPressedIcon()
     */
    public function getPressedIcon():Icon {
        return pressedIcon;
    }
    
    /**
     * Sets the pressed icon for the button.
     * @param pressedIcon the icon used as the "pressed" image
     * @see #getPressedIcon()
     */
    public function setPressedIcon(pressedIcon:Icon):Void {
        var oldValue:Icon = this.pressedIcon;
        this.pressedIcon = pressedIcon;
        if (pressedIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
            if (getModel().isPressed()) {
                repaint();
            }
        }
    }

    /**
     * Returns the selected icon for the button.
     * @return the <code>selectedIcon</code> property
     * @see #setSelectedIcon()
     */
    public function getSelectedIcon():Icon {
        return selectedIcon;
    }
    
    /**
     * Sets the selected icon for the button.
     * @param selectedIcon the icon used as the "selected" image
     * @see #getSelectedIcon()
     */
    public function setSelectedIcon(selectedIcon:Icon):Void {
        var oldValue:Icon = this.selectedIcon;
        this.selectedIcon = selectedIcon;
        if (selectedIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
            if (isSelected()) {
                repaint();
            }
        }
    }

    /**
     * Returns the rollover icon for the button.
     * @return the <code>rolloverIcon</code> property
     * @see #setRollOverIcon()
     */
    public function getRollOverIcon():Icon {
        return rolloverIcon;
    }
    
    /**
     * Sets the rollover icon for the button.
     * @param rolloverIcon the icon used as the "rollover" image
     * @see #getRollOverIcon()
     */
    public function setRollOverIcon(rolloverIcon:Icon):Void {
        var oldValue:Icon = this.rolloverIcon;
        this.rolloverIcon = rolloverIcon;
        setRollOverEnabled(true);
        if (rolloverIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
            if(getModel().isRollOver()){
            	repaint();
            }
        }
      
    }
    
    /**
     * Returns the rollover selection icon for the button.
     * @return the <code>rolloverSelectedIcon</code> property
     * @see #setRollOverSelectedIcon()
     */
    public function getRollOverSelectedIcon():Icon {
        return rolloverSelectedIcon;
    }
    
    /**
     * Sets the rollover selected icon for the button.
     * @param rolloverSelectedIcon the icon used as the
     *		"selected rollover" image
     * @see #getRollOverSelectedIcon()
     */
    public function setRollOverSelectedIcon(rolloverSelectedIcon:Icon):Void {
        var oldValue:Icon = this.rolloverSelectedIcon;
        this.rolloverSelectedIcon = rolloverSelectedIcon;
        setRollOverEnabled(true);
        if (rolloverSelectedIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
            if (isSelected()) {
                repaint();
            }
        }
    }
    
    /**
     * Returns the icon used by the button when it's disabled.
     * If no disabled icon has been set, the button constructs
     * one from the default icon. 
     * <p>
     * The disabled icon really should be created 
     * (if necessary) by the L&F.-->
     *
     * @return the <code>disabledIcon</code> property
     * @see #getPressedIcon()
     * @see #setDisabledIcon()
     */
    public function getDisabledIcon():Icon {
        if(disabledIcon == null) {
            if(defaultIcon != null) {
                return new GrayFilteredIcon(defaultIcon);
            }
        }
        return disabledIcon;
    }
    
    /**
     * Sets the disabled icon for the button.
     * @param disabledIcon the icon used as the disabled image
     * @see #getDisabledIcon()
     */
    public function setDisabledIcon(disabledIcon:Icon):Void {
        var oldValue:Icon = this.disabledIcon;
        this.disabledIcon = disabledIcon;
        if (disabledIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
            if (!isEnabled()) {
                repaint();
            }
        }
    }
    
    /**
     * Returns the icon used by the button when it's disabled and selected.
     * If not no disabled selection icon has been set, the button constructs
     * one from the selection icon. 
     * <p>
     * The disabled selection icon really should be 
     * created (if necessary) by the L&F. -->
     *
     * @return the <code>disabledSelectedIcon</code> property
     * @see #getPressedIcon()
     * @see #setDisabledIcon()
     */
    public function getDisabledSelectedIcon():Icon {
        if(disabledSelectedIcon == null) {
            if(selectedIcon != null) {
                disabledSelectedIcon = new GrayFilteredIcon(selectedIcon);
            } else {
                return getDisabledIcon();
            }
        }
        return disabledSelectedIcon;
    }

    /**
     * Sets the disabled selection icon for the button.
     * @param disabledSelectedIcon the icon used as the disabled
     * 		selection image
     * @see #getDisabledSelectedIcon()
     */
    public function setDisabledSelectedIcon(disabledSelectedIcon:Icon):Void {
        var oldValue:Icon = this.disabledSelectedIcon;
        this.disabledSelectedIcon = disabledSelectedIcon;
        if (disabledSelectedIcon != oldValue) {
        	uninstallIconWhenNextPaint(oldValue);
            if (!isEnabled() && isSelected()) {
                repaint();
                revalidate();
            }
        }
    }

    /**
     * Returns the vertical alignment of the text and icon.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():Number {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:Number):Void {
        if (alignment == verticalAlignment){
        	return;
        }else{
        	verticalAlignment = alignment;
        	repaint();
        }
    }
    
    /**
     * Returns the horizontal alignment of the icon and text.
     * @return the <code>horizontalAlignment</code> property,
     *		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalAlignment():Number{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:Number):Void {
        if (alignment == horizontalAlignment){
        	return;
        }else{
        	horizontalAlignment = alignment;     
        	repaint();
        }
    }

    
    /**
     * Returns the vertical position of the text relative to the icon.
     * @return the <code>verticalTextPosition</code> property, 
     *		one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER  (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalTextPosition():Number{
        return verticalTextPosition;
    }
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalTextPosition(textPosition:Number):Void {
        if (textPosition == verticalTextPosition){
	        return;
        }else{
        	verticalTextPosition = textPosition;
        	repaint();
            revalidate();
        }
    }
    
    /**
     * Returns the horizontal position of the text relative to the icon.
     * @return the <code>horizontalTextPosition</code> property, 
     * 		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalTextPosition():Number {
        return horizontalTextPosition;
    }
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalTextPosition(textPosition:Number):Void {
        if (textPosition == horizontalTextPosition){
        	return;
        }else{
        	horizontalTextPosition = textPosition;
        	repaint();
            revalidate();
        }
    }
    
    /**
     * Returns the amount of space between the text and the icon
     * displayed in this button.
     *
     * @return an int equal to the number of pixels between the text
     *         and the icon.
     * @see #setIconTextGap()
     */
    public function getIconTextGap():Number {
        return iconTextGap;
    }

    /**
     * If both the icon and text properties are set, this property
     * defines the space between them.  
     * <p>
     * The default value of this property is 4 pixels.
     * 
     * @see #getIconTextGap()
     */
    public function setIconTextGap(iconTextGap:Number):Void {
        var oldValue:Number = this.iconTextGap;
        this.iconTextGap = iconTextGap;
        if (iconTextGap != oldValue) {
            revalidate();
            repaint();
        }
    }
    

	//-----------------------button's event method to change state-------------
	private function __onPress():Void{
		getModel().setPressed(true);
		super.__onPress();
	}
	
	private function __onRelease():Void{
		getModel().setReleased(true);
		super.__onRelease();
		fireActionEvent();
	}
	
	private function __onReleaseOutside():Void{
		getModel().setRollOver(false);
		getModel().setReleased(true);
		super.__onReleaseOutside();
	}
	
	private function __onRollOver():Void{
		getModel().setRollOver(true);
		super.__onRollOver();
	}
	
	private function __onRollOut():Void{
		getModel().setRollOver(false);
		super.__onRollOut();
	}    
}
