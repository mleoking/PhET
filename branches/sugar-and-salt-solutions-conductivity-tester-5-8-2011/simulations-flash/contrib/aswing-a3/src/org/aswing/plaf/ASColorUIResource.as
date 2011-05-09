/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf
{
	
import org.aswing.ASColor;

/**
 * Color UI Resource.
 * @author iiley
 */
public class ASColorUIResource extends ASColor implements UIResource
{
	public function ASColorUIResource(color:uint=0x000000, alpha:Number=1)
	{
		super(color, alpha);
	}
	
	/**
	 * Create a color ui resource with a color.
	 */
	public static function createResourceColor(color:ASColor):ASColorUIResource{
		return new ASColorUIResource(color.getRGB(), color.getAlpha());
	}
}

}