/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import org.aswing.*;
import org.aswing.graphics.*;
import org.aswing.geom.*;
import org.aswing.plaf.*;
import org.aswing.plaf.basic.icon.ArrowIcon;
import org.aswing.util.Timer;
import org.aswing.border.LineBorder;
import flash.events.MouseEvent;
import flash.events.Event;
import flash.geom.Rectangle;
import org.aswing.event.ListItemEvent;
import org.aswing.event.FocusKeyEvent;
import flash.ui.Keyboard;
import org.aswing.event.AWEvent;

/**
 * Basic combo box ui imp.
 * @author iiley
 * @private
 */
public class BasicComboBoxUI extends BaseComponentUI implements ComboBoxUI{
		
	protected var dropDownButton:Component;
	protected var box:JComboBox;
	protected var popup:JPopup;
	protected var scollPane:JScrollPane;
    protected var arrowShadowColor:ASColor;
    protected var arrowLightColor:ASColor;
	
	protected var popupTimer:Timer;
	protected var moveDir:Number;
		
	public function BasicComboBoxUI() {
		super();
	}
	
    override public function installUI(c:Component):void{
    	box = JComboBox(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
    	box = JComboBox(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
    
	protected function getPropertyPrefix():String {
		return "ComboBox.";
	}
	
	protected function installDefaults():void{
		var pp:String = getPropertyPrefix();
        LookAndFeel.installBorderAndBFDecorators(box, pp);
        LookAndFeel.installColorsAndFont(box, pp);
        LookAndFeel.installBasicProperties(box, pp);
		arrowShadowColor = getColor(pp+"arrowShadowColor");
		arrowLightColor = getColor(pp+"arrowLightColor");
	}
    
    protected function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(box);
    }
    
	protected function installComponents():void{
		dropDownButton = createDropDownButton();
		dropDownButton.setUIElement(true);
		box.addChild(dropDownButton);
    }
	protected function uninstallComponents():void{
		box.removeChild(dropDownButton);
		if(isPopupVisible(box)){
			setPopupVisible(box, false);
		}
    }
	
	protected function installListeners():void{
		getPopupList().setFocusable(false);
		box.addEventListener(MouseEvent.MOUSE_DOWN, __onBoxPressed);
		box.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onFocusKeyDown);
		box.addEventListener(AWEvent.FOCUS_LOST, __onFocusLost);
		box.addEventListener(Event.REMOVED_FROM_STAGE, __onBoxRemovedFromStage);
		getPopupList().addEventListener(ListItemEvent.ITEM_CLICK, __onListItemReleased);
		popupTimer = new Timer(40);
		popupTimer.addActionListener(__movePopup);
	}
    
    protected function uninstallListeners():void{
    	popupTimer.stop();
    	popupTimer = null;
		box.removeEventListener(MouseEvent.MOUSE_DOWN, __onBoxPressed);
		box.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onFocusKeyDown);
		box.removeEventListener(AWEvent.FOCUS_LOST, __onFocusLost);
		box.removeEventListener(Event.REMOVED_FROM_STAGE, __onBoxRemovedFromStage);
		getPopupList().removeEventListener(ListItemEvent.ITEM_CLICK, __onListItemReleased);
    }
    
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		layoutCombobox();
		dropDownButton.setEnabled(box.isEnabled());
	}
        
    override protected function paintBackGround(c:Component, g:Graphics2D, b:IntRectangle):void{
    	if(c.isOpaque()){
	 		var bgColor:ASColor;
	 		bgColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
	 		if(!box.isEnabled()){
	 			bgColor = BasicGraphicsUtils.getDisabledColor(c);
	 		}
			g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
    	}
    }
    
    /**
     * Just override this method if you want other LAF drop down buttons.
     */
    protected function createDropDownButton():Component{
    	var btn:JButton = new JButton("", new ArrowIcon(
    				Math.PI/2, 8,
				    arrowLightColor,
				    arrowShadowColor
    	));
    	btn.setFocusable(false);
    	btn.setPreferredSize(new IntDimension(16, 16));
    	return btn;
    }
    
    protected function getScollPane():JScrollPane{
    	if(scollPane == null){
    		scollPane = new JScrollPane(getPopupList());
    		scollPane.setBorder(getBorder(getPropertyPrefix()+"popupBorder"));
    		scollPane.setOpaque(true);
    	}
    	return scollPane;
    }
    
    protected function getPopup():JPopup{
    	if(popup == null){
    		popup = new JPopup(box.root, false);
    		popup.setLayout(new BorderLayout());
    		popup.append(getScollPane(), BorderLayout.CENTER);
    		popup.setClipMasked(false);
    	}
    	return popup;
    }
    
    protected function getPopupList():JList{
    	return box.getPopupList();
    }
    
    protected function viewPopup():void{
    	if(!box.isOnStage()){
    		return;
    	}
		var width:int = box.getWidth();
		var cellHeight:int;
		if(box.getListCellFactory().isAllCellHasSameHeight()){
			cellHeight = box.getListCellFactory().getCellHeight();
		}else{
			cellHeight = box.getPreferredSize().height;
		}
		var height:int = Math.min(box.getItemCount(), box.getMaximumRowCount())*cellHeight;
		var i:Insets = getScollPane().getInsets();
		height += i.top + i.bottom;
		i = getPopupList().getInsets();
		height += i.top + i.bottom;
		getPopup().changeOwner(AsWingUtils.getOwnerAncestor(box));
		getPopup().setSizeWH(width, height);
		getPopup().show();
		startMoveToView();
		AsWingManager.callLater(__addMouseDownListenerToStage);
    }
    
    private function __addMouseDownListenerToStage():void{
    	if(getPopup().isVisible() && box.stage){
			box.stage.addEventListener(MouseEvent.MOUSE_DOWN, __onMouseDownWhenPopuped, false, 0, true);
    	}
    }
    
    private function hidePopup():void{
    	if(box.stage){
    		box.stage.removeEventListener(MouseEvent.MOUSE_DOWN, __onMouseDownWhenPopuped);
    	}
		popupTimer.stop();
    	if(getPopup().isVisible()){
			getPopup().dispose();
    	}
    }
    
    private var scrollRect:Rectangle;
    //return the destination pos
    private function startMoveToView():void{
    	var popupPane:JPopup = getPopup();
    	var height:int = popupPane.getHeight();
    	var popupPaneHeight:int = height;
    	var downDest:IntPoint = box.componentToGlobal(new IntPoint(0, box.getHeight()));
    	var upDest:IntPoint = new IntPoint(downDest.x, downDest.y - box.getHeight() - popupPaneHeight);
    	var visibleBounds:IntRectangle = AsWingUtils.getVisibleMaximizedBounds(popupPane.parent);
    	var distToBottom:int = visibleBounds.y + visibleBounds.height - downDest.y - popupPaneHeight;
    	var distToTop:int = upDest.y - visibleBounds.y;
    	var gp:IntPoint = box.getGlobalLocation();
    	if(distToBottom > 0 || (distToBottom < 0 && distToTop < 0 && distToBottom > distToTop)){
    		moveDir = 1;
    		gp.y += box.getHeight();
			scrollRect = new Rectangle(0, height, popupPane.getWidth(), 0);
    	}else{
    		moveDir = -1;
			scrollRect = new Rectangle(0, 0, popupPane.getWidth(), 0);
    	}
    	popupPane.setGlobalLocation(gp);
    	popupPane.scrollRect = scrollRect;
    	
		popupTimer.restart();
    }
    
    private function setComboBoxValueFromListSelection():void{
		var selectedValue:Object = getPopupList().getSelectedValue();
		box.setSelectedItem(selectedValue);
    }
    
    //-----------------------------
    
    protected function __movePopup(e:Event):void{
    	var popupPane:JPopup = getPopup();
    	var popupPaneHeight:int = popupPane.getHeight();
    	var maxTime:int = 10;
    	var minTime:int = 3;
    	var speed:int = 50;
    	if(popupPaneHeight < speed*minTime){
    		speed = Math.ceil(popupPaneHeight/minTime);
    	}else if(popupPaneHeight > speed*maxTime){
    		speed = Math.ceil(popupPaneHeight/maxTime);
    	}
    	if(popupPane.height - scrollRect.height <= speed){
    		//motion ending
    		speed = popupPane.height - scrollRect.height;
			popupTimer.stop();
    	}
		if(moveDir > 0){
			scrollRect.y -= speed;
			scrollRect.height += speed;
		}else{
			popupPane.y -= speed;
			scrollRect.height += speed;
		}
    	popupPane.scrollRect = scrollRect;
    }
    
    protected function __onFocusKeyDown(e:FocusKeyEvent):void{
    	var code:uint = e.keyCode;
    	if(code == Keyboard.DOWN){
    		if(!isPopupVisible(box)){
    			setPopupVisible(box, true);
    			return;
    		}
    	}
    	if(code == Keyboard.ESCAPE){
    		if(isPopupVisible(box)){
    			setPopupVisible(box, false);
    			return;
    		}
    	}
    	if(code == Keyboard.ENTER && isPopupVisible(box)){
	    	hidePopup();
	    	setComboBoxValueFromListSelection();
	    	return;
    	}
    	var list:JList = getPopupList();
    	var index:int = list.getSelectedIndex();
    	if(code == Keyboard.DOWN){
    		index += 1;
    	}else if(code == Keyboard.UP){
    		index -= 1;
    	}else if(code == Keyboard.PAGE_DOWN){
    		index += box.getMaximumRowCount();
    	}else if(code == Keyboard.PAGE_UP){
    		index -= box.getMaximumRowCount();
    	}else if(code == Keyboard.HOME){
    		index = 0;
    	}else if(code == Keyboard.END){
    		index = list.getModel().getSize()-1;
    	}else{
    		return;
    	}
    	index = Math.max(0, Math.min(list.getModel().getSize()-1, index));
    	list.setSelectedIndex(index, false);
    	list.ensureIndexIsVisible(index);
    }
    
    protected function __onFocusLost(e:Event):void{
    	hidePopup();
    }
    
    protected function __onBoxRemovedFromStage(e:Event):void{
    	hidePopup();
    }
    
    protected function __onListItemReleased(e:Event):void{
    	hidePopup();
    	setComboBoxValueFromListSelection();
    }
    
    protected function __onBoxPressed(e:Event):void{
    	if(!isPopupVisible(box)){
    		if(box.isEditable()){
    			if(!box.getEditor().getEditorComponent().hitTestMouse()){
    				setPopupVisible(box, true);
    			}
    		}else{
    			setPopupVisible(box, true);
    		}
    	}else{
    		hidePopup();
    	}
    }
    
    protected function __onMouseDownWhenPopuped(e:Event):void{
    	if(!getPopup().hitTestMouse() && !box.hitTestMouse()){
    		hidePopup();
    	}
    }
    
	/**
     * Set the visiblity of the popup
     */
	public function setPopupVisible(c:JComboBox, v:Boolean):void{
		if(v){
			viewPopup();
		}else{
			hidePopup();
		}
	}
	
	/** 
     * Determine the visibility of the popup
     */
	public function isPopupVisible(c:JComboBox):Boolean{
		return getPopup().isVisible();
	}
	
	//---------------------Layout Implementation---------------------------
    protected function layoutCombobox():void{
    	var td:IntDimension = box.getSize();
		var insets:Insets = box.getInsets();
		var top:int = insets.top;
		var left:int = insets.left;
		var right:int = td.width - insets.right;
		
		var height:int = td.height - insets.top - insets.bottom;
    	var buttonSize:IntDimension = dropDownButton.getPreferredSize(); 
    	dropDownButton.setSizeWH(buttonSize.width, height);
    	dropDownButton.setLocationXY(right - buttonSize.width, top);
    	box.getEditor().getEditorComponent().setLocationXY(left, top);
    	box.getEditor().getEditorComponent().setSizeWH(td.width-insets.left-insets.right- buttonSize.width, height);
    }
    
    override public function getPreferredSize(c:Component):IntDimension{
    	var insets:Insets = box.getInsets();
    	var listPreferSize:IntDimension = getPopupList().getPreferredSize();
    	var ew:int = listPreferSize.width;
    	var wh:int = box.getEditor().getEditorComponent().getPreferredSize().height;
    	var buttonSize:IntDimension = dropDownButton.getPreferredSize(); 
    	buttonSize.width += ew;
    	if(wh > buttonSize.height){
    		buttonSize.height = wh;
    	}
    	return insets.getOutsideSize(buttonSize);
    }

    override public function getMinimumSize(c:Component):IntDimension{
    	return box.getInsets().getOutsideSize(dropDownButton.getPreferredSize());
    }

    override public function getMaximumSize(c:Component):IntDimension{
		return IntDimension.createBigDimension();
    }    
}
}