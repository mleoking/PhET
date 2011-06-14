/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf
{
	
import org.aswing.Insets;

/**
 * Insets UI Resource.
 * @author iiley
 */
public class InsetsUIResource extends Insets implements UIResource
{
	public function InsetsUIResource(top:int=0, left:int=0, bottom:int=0, right:int=0)
	{
		super(top, left, bottom, right);
	}
	
	/**
	 * Create a insets ui resource with a insets.
	 */
	public function createInsetsResource(insets:Insets):InsetsUIResource{
		return new InsetsUIResource(insets.top, insets.left, insets.bottom, insets.right);
	}
	
}
}