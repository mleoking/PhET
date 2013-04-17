/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.event { 
	
/**
 * Exception used to stop and expand/collapse from happening.
 * @author iiley
 */
public class ExpandVetoException extends Error {
	
	public function ExpandVetoException(message : String) {
		super(message);
	}

}
}