/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.error
{
/**
 * This error indicates that an abstract class's abstract method missing overriden error.
 */
public class ImpMissError extends Error
{
	public function ImpMissError(){
		super("Subclass should override this method to do implementation!!");
	}
}

}