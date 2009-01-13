/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.ButtonGroup;
import org.aswing.util.HashMap;

/**
 * Allows to manage button groups using global group name values.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.ButtonGroupManager {
	
	/**
	 * Stores associations between {@link org.aswing.ButtonGroup} and
	 * its identifiers (names).
	 */
	private static var groups:HashMap;
	
	/** 
	 * Initializes group associations storage.
	 */
	private static function init():Void {
		if (groups == null) groups = new HashMap();	
	}

	/**
	 * Appends button to the existed group with the specified id. If group with
	 * the specified id doesn't exist manager creates it. 
	 * 
	 * @param groupID the id of the button group
	 * @param button the button to add to the specified group
	 */
	public static function append(groupID:String, button:AbstractButton):Void {
		init();
		
		if (groupID == null) return;
		var group:ButtonGroup = groups.get(groupID);
		
		if (group == null) {
			group = new ButtonGroup();	
			groups.put(groupID, group);
		}
		
		group.append(button);
	}
	
	/**
	 * Removes button from the specified group. 
	 * 
	 * @param groupID the id of the button group to remove button from
	 * @param button the button to remove from the specified group
	 */
	public static function remove(groupID:String, button:AbstractButton):Void {
		init();
		
		var group:ButtonGroup = groups.get(groupID);
		
		if (group != null) {
			group.remove(button);
			
			// if button group is empty purge group
			if (group.getButtonCount() == 0) {
				groups.remove(groupID);
				delete group;	
			}	
		}
	}
	
	/**
	 * Returns the button group by id.
	 * @param groupID the id of the button group
	 * @return the button group or null if the specifiy id of group is not exist.
	 */
	public static function get(groupID:String):ButtonGroup{
		return groups.get(groupID);
	}
	
	/**
	 * Private Constructor.
	 */
	private function ButtonGroupManager(Void) {
		//
	}	
	
}