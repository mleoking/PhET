package org.aswing{

import flash.events.Event;

import org.aswing.event.AWEvent;
import org.aswing.event.FrameEvent;
import org.aswing.event.InteractiveEvent;
import org.aswing.event.WindowEvent;
import org.aswing.plaf.UIResource;

/**
 * The default Imp of FrameTitleBar
 */
public class JFrameTitleBar extends Container implements FrameTitleBar, UIResource{
	
	protected var iconifiedButton:AbstractButton;
	protected var maximizeButton:AbstractButton;
	protected var restoreButton:AbstractButton;
	protected var closeButton:AbstractButton;
	protected var activeTextColor:ASColor;
	protected var inactiveTextColor:ASColor;
	protected var titleLabel:JLabel;
	protected var icon:Icon;
	protected var text:String;
	protected var titleEnabled:Boolean;
	
	protected var buttonPane:Container;
	protected var buttonPaneLayout:SoftBoxLayout;
	
	protected var owner:JWindow;
	protected var frame:JFrame;
	
	public function JFrameTitleBar(){
		super();
		titleEnabled = true;
		setLayout(new FrameTitleBarLayout());
		
		buttonPane = new Container();
		buttonPane.setCachePreferSizes(false);
		buttonPaneLayout = new SoftBoxLayout(SoftBoxLayout.X_AXIS, 0);
		buttonPane.setLayout(buttonPaneLayout);
		titleLabel = new JLabel();
		titleLabel.mouseEnabled = false;
		titleLabel.mouseChildren = false;
		titleLabel.setHorizontalAlignment(JLabel.LEFT);
		append(titleLabel, BorderLayout.CENTER);
		append(buttonPane, BorderLayout.EAST);
		
		setIconifiedButton(createIconifiedButton());
		setMaximizeButton(createMaximizeButton());
		setRestoreButton(createRestoreButton());
		setCloseButton(createCloseButton());
		setMaximizeButtonVisible(false);
		buttonPane.appendAll(iconifiedButton, restoreButton, maximizeButton, closeButton);
		
		setUIElement(true);
		addEventListener(AWEvent.PAINT, __framePainted);
	}
	
	protected function createPureButton():JButton{
		var b:JButton = new JButton();
		b.setBackgroundDecorator(null);
		b.setMargin(new Insets());
		return b;
	}
	
	protected function createIconifiedButton():AbstractButton{
		return createPureButton();
	}
	
	protected function createMaximizeButton():AbstractButton{
		return createPureButton();
	}
	
	protected function createRestoreButton():AbstractButton{
		return createPureButton();
	}
	
	protected function createCloseButton():AbstractButton{
		return createPureButton();
	}
	
	public function updateUIPropertiesFromOwner():void{
		if(getIconifiedButton()){
			getIconifiedButton().setIcon(getFrameIcon("Frame.iconifiedIcon"));
		}
		if(getMaximizeButton()){
			getMaximizeButton().setIcon(getFrameIcon("Frame.maximizeIcon"));
		}
		if(getRestoreButton()){
			getRestoreButton().setIcon(getFrameIcon("Frame.normalIcon"));
		}
		if(getCloseButton()){
			getCloseButton().setIcon(getFrameIcon("Frame.closeIcon"));
		}
		
		activeTextColor     = getFrameUIColor("Frame.activeCaptionText");
		inactiveTextColor   = getFrameUIColor("Frame.inactiveCaptionText"); 	
		setBackgroundDecorator(getTitleBGD("Frame.titleBarBG"));
		buttonPaneLayout.setGap(getFrameUIInt("Frame.titleBarButtonGap"));
		revalidateIfNecessary();
		__activeChange(null);
		__framePainted(null);
	}
	
	protected function getFrameIcon(key:String):Icon{
		if(owner.getUI()){
			return owner.getUI().getIcon(key);
		}else{
			return UIManager.getIcon(key);
		}
	}
	
	protected function getTitleBGD(key:String):GroundDecorator{
		if(owner.getUI()){
			return owner.getUI().getGroundDecorator(key);
		}else{
			return UIManager.getGroundDecorator(key);
		}
	}
	
	protected function getFrameUIInt(key:String):int{
		if(owner.getUI()){
			return owner.getUI().getInt(key);
		}else{
			return UIManager.getInt(key);
		}
	}
	
	protected function getFrameUIColor(key:String):ASColor{
		if(owner.getUI()){
			return owner.getUI().getColor(key);
		}else{
			return UIManager.getColor(key);
		}
	}
	
	public function getSelf():Component{
		return this;
	}
	
	public function setFrame(f:JWindow):void{
		if(owner){
			owner.removeEventListener(FrameEvent.FRAME_ABILITY_CHANGED, __frameAbilityChanged);
			owner.removeEventListener(AWEvent.PAINT, __framePainted);
			owner.removeEventListener(InteractiveEvent.STATE_CHANGED, __stateChanged);
			owner.removeEventListener(WindowEvent.WINDOW_ACTIVATED, __activeChange);
			owner.removeEventListener(WindowEvent.WINDOW_DEACTIVATED, __activeChange);
		}
		owner = f;
		frame = f as JFrame;
		if(owner){
			owner.addEventListener(FrameEvent.FRAME_ABILITY_CHANGED, __frameAbilityChanged, false, 0, true);
			owner.addEventListener(AWEvent.PAINT, __framePainted, false, 0, true);
			owner.addEventListener(InteractiveEvent.STATE_CHANGED, __stateChanged, false, 0, true);
			owner.addEventListener(WindowEvent.WINDOW_ACTIVATED, __activeChange, false, 0, true);
			owner.addEventListener(WindowEvent.WINDOW_DEACTIVATED, __activeChange, false, 0, true);
			
			updateUIPropertiesFromOwner();
		}
		__stateChanged(null);
	}
	
	public function getFrame():JWindow{
		return owner;
	}
	
	public function setTitleEnabled(b:Boolean):void{
		titleEnabled = b;
	}
	
	public function isTitleEnabled():Boolean{
		return titleEnabled;
	}
		
	public function addExtraControl(c:Component, position:int):void{
		if(position == AsWingConstants.LEFT){
			buttonPane.insert(0, c);
		}else{
			buttonPane.append(c);
		}
	}
	
	public function removeExtraControl(c:Component):Component{
		return buttonPane.remove(c);
	}
	
	public function getLabel():JLabel{
		return titleLabel;
	}
	
	public function setIcon(i:Icon):void{
		icon = i;
		if(titleLabel){
			titleLabel.setIcon(i);
		}
	}
	
	public function getIcon():Icon{
		return icon;
	}
	
	public function setText(t:String):void{
		text = t;
		if(titleLabel){
			titleLabel.setText(t);
		}
	}
	
	public function getText():String{
		return text;
	}
	
	public function setIconifiedButton(b:AbstractButton):void{
		if(iconifiedButton != b){
			var index:int = -1;
			if(iconifiedButton){
				index = buttonPane.getIndex(iconifiedButton);
				buttonPane.removeAt(index);
				iconifiedButton.removeActionListener(__iconifiedPressed);
			}
			iconifiedButton = b;
			if(iconifiedButton){
				buttonPane.insert(index, iconifiedButton);
				iconifiedButton.addActionListener(__iconifiedPressed);
			}
		}
	}
	
	public function setMaximizeButton(b:AbstractButton):void{
		if(maximizeButton != b){
			var index:int = -1;
			if(maximizeButton){
				index = buttonPane.getIndex(maximizeButton);
				buttonPane.removeAt(index);
				maximizeButton.removeActionListener(__maximizePressed);
			}
			maximizeButton = b;
			if(maximizeButton){
				buttonPane.insert(index, maximizeButton);
				maximizeButton.addActionListener(__maximizePressed);
			}
		}
	}
	
	public function setRestoreButton(b:AbstractButton):void{
		if(restoreButton != b){
			var index:int = -1;
			if(restoreButton){
				index = buttonPane.getIndex(restoreButton);
				buttonPane.removeAt(index);
				restoreButton.removeActionListener(__restorePressed);
			}
			restoreButton = b;
			if(restoreButton){
				buttonPane.insert(index, restoreButton);
				restoreButton.addActionListener(__restorePressed);
			}
		}
	}
	
	public function setCloseButton(b:AbstractButton):void{
		if(closeButton != b){
			var index:int = -1;
			if(closeButton){
				index = buttonPane.getIndex(closeButton);
				buttonPane.removeAt(index);
				closeButton.removeActionListener(__closePressed);
			}
			closeButton = b;
			if(closeButton){
				buttonPane.insert(index, closeButton);
				closeButton.addActionListener(__closePressed);
			}
		}
	}
	
	public function getIconifiedButton():AbstractButton{
		return iconifiedButton;
	}
	
	public function getMaximizeButton():AbstractButton{
		return maximizeButton;
	}
	
	public function getRestoreButton():AbstractButton{
		return restoreButton;
	}
	
	public function getCloseButton():AbstractButton{
		return closeButton;
	}
	
	public function setIconifiedButtonVisible(b:Boolean):void{
		if(getIconifiedButton()){
			getIconifiedButton().setVisible(b);
		}
	}
	
	public function setMaximizeButtonVisible(b:Boolean):void{
		if(getMaximizeButton()){
			getMaximizeButton().setVisible(b);
		}
	}
	
	public function setRestoreButtonVisible(b:Boolean):void{
		if(getRestoreButton()){
			getRestoreButton().setVisible(b);
		}
	}
	
	public function setCloseButtonVisible(b:Boolean):void{
		if(getCloseButton()){
			getCloseButton().setVisible(b);
		}
	}
	
	private function __iconifiedPressed(e:Event):void{
		if(frame && isTitleEnabled()){
			frame.setState(JFrame.ICONIFIED, false);
		}
	}
	
	private function __maximizePressed(e:Event):void{
		if(frame && isTitleEnabled()){
			frame.setState(JFrame.MAXIMIZED, false);
		}
	}
	
	private function __restorePressed(e:Event):void{
		if(frame && isTitleEnabled()){
			frame.setState(JFrame.NORMAL, false);
		}
	}
	
	private function __closePressed(e:Event):void{
		if(frame && isTitleEnabled()){
			frame.closeReleased();
		}
	}
	
	private function __activeChange(e:Event):void{
		if(getLabel()){
			getLabel().setForeground(owner.isActive() ? activeTextColor : inactiveTextColor);
			getLabel().repaint();
		}
		repaint();
	}
	
	private function __framePainted(e:AWEvent):void{
		if(getLabel()){
			getLabel().setFont(owner.getFont());
		}
	}
	
	private function __frameAbilityChanged(e:FrameEvent):void{
		__stateChanged(null);
	}
	
	private function __stateChanged(e:InteractiveEvent):void{
		if(frame == null){
			return;
		}
		var state:int = frame.getState();
		if(state != JFrame.ICONIFIED 
			&& state != JFrame.NORMAL
			&& state != JFrame.MAXIMIZED_HORIZ
			&& state != JFrame.MAXIMIZED_VERT
			&& state != JFrame.MAXIMIZED){
			state = JFrame.NORMAL;
		}
		if(state == JFrame.ICONIFIED){
			setIconifiedButtonVisible(false);
			setMaximizeButtonVisible(false);
			setRestoreButtonVisible(true);
			setCloseButtonVisible(frame.isClosable());
		}else if(state == JFrame.NORMAL){
			setIconifiedButtonVisible(frame.isResizable());
			setRestoreButtonVisible(false);
			setMaximizeButtonVisible(frame.isResizable());
			setCloseButtonVisible(frame.isClosable());
		}else{
			setIconifiedButtonVisible(frame.isResizable());
			setRestoreButtonVisible(frame.isResizable());
			setMaximizeButtonVisible(false);
			setCloseButtonVisible(frame.isClosable());
		}
		revalidateIfNecessary();
	}
}
}