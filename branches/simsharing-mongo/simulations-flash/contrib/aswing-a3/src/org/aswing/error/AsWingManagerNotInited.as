/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.error{

/**
 * The error indicated that the operation you are doing shuold be after 
 * <code>AsWingManager.setRoot(a root)</code>.
 * 
 * @see org.aswing.AsWingManager
 * @author iiley
 */
public class AsWingManagerNotInited extends Error{
	
	public function AsWingManagerNotInited()
	{
		super("You have not call AsWingManager.setRoot() yet!");
	}
	
}
}