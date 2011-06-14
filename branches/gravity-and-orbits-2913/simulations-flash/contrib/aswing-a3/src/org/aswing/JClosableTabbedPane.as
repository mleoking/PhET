package org.aswing{

import org.aswing.plaf.basic.BasicClosableTabbedPaneUI;	

/**
 * Dispatched when a tab clos button is clicked. 
 * @eventType org.aswing.event.TabCloseEvent.TAB_CLOSING
 */
[Event(name="tabClosing", type="org.aswing.event.TabCloseEvent")]

/**
 * A TabbedPane with each tab a close button, you must listen the TabCloseEvent 
 * and then remove the related tab component if you want. 
 * By default, any thing will happen for close button click.
 * @author iiley
 */
public class JClosableTabbedPane extends JTabbedPane{
	
    protected var closeEnables:Array;
    
	public function JClosableTabbedPane(){
		super();
		closeEnables = new Array();
		setName("JClosableTabbedPane");
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicClosableTabbedPaneUI;
    }
	
	override public function getUIClassID():String{
		return "ClosableTabbedPaneUI";
	}
	
	/**
	 * Sets whether or not the tab close button at index is enabled. 
	 * Nothing will happen if there is no tab at that index.
	 * @param index the tab index which should be enabled/disabled
	 * @param enabled whether or not the tab close button should be enabled 
	 */
	public function setCloseEnabledAt(index:int, enabled:Boolean):void{
		if(closeEnables[index] != enabled){
			closeEnables[index] = enabled;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Returns whether or not the tab close button at index is currently enabled. 
	 * false will be returned if there is no tab at that index.
	 * @param index  the index of the item being queried 
	 * @return if the tab close button at index is enabled; false otherwise.
	 */
	public function isCloseEnabledAt(index:int):Boolean{
		return closeEnables[index] == true;
	}
	
	override protected function insertProperties(i:int, title:String="", icon:Icon=null, tip:String=null):void{
		super.insertProperties(i, title, icon, tip);
		insertToArray(closeEnables, i, true);
	}
	
	override protected function removeProperties(i:int):void{
		super.removeProperties(i);
		removeFromArray(closeEnables, i);
	}
}
}