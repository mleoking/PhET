package org.aswing{

public interface FrameTitleBar{
	
	/**
	 * Returns the component(must be FrameTitleBar instance self, that means the implamentation 
	 * must extends Component and getPane() return itself) that represents the title bar.
	 */
	function getSelf():Component;
	
	/**
	 * Sets the owner of this title bar. null to uninstall from current frame.
	 * You can set a JFrame or a JWindow, if it is JWindow, some function will be lost.
	 */
	function setFrame(frame:JWindow):void;
	
	function getFrame():JWindow;
	
	/**
	 * This method will be call when owner ui changed.
	 */
	function updateUIPropertiesFromOwner():void;
	
	/**
	 * Adds extra control to title bar
	 * @param c the control
	 * @param position left or right behind the title buttons. 
	 * 			<code>AsWingConstants.LEFT</code> or <code>AsWingConstants.RIGHT</code>
	 */
	function addExtraControl(c:Component, position:int):void;
	
	/**
	 * Returns the extra control already added.
	 * @returns the removed control, null will be returned if the control is not in title bar.
	 */
	function removeExtraControl(c:Component):Component;
	
	/**
	 * Sets the enabled property, if enabled, the title should have ability to iconified, maximize, restore, close, move frame.
	 * If not enabled, that abilities should be disabled.
	 */
	function setTitleEnabled(b:Boolean):void;
	
	/**
	 * Returns is title enabled.
	 * @see #setTitleEnabled()
	 */
	function isTitleEnabled():Boolean;
	
	function setIcon(i:Icon):void;
	
	function getIcon():Icon;
	
	function setText(t:String):void;
	
	function getText():String;
	
	function getLabel():JLabel;
	
	function setIconifiedButton(b:AbstractButton):void;
	
	function setMaximizeButton(b:AbstractButton):void;
	
	function setRestoreButton(b:AbstractButton):void;
	
	function setCloseButton(b:AbstractButton):void;
	
	function getIconifiedButton():AbstractButton;
	
	function getMaximizeButton():AbstractButton;
	
	function getRestoreButton():AbstractButton;
	
	function getCloseButton():AbstractButton;
	
}
}