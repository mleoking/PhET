/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.plaf.UIResource;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.ASColorUIResource extends ASColor implements UIResource{
	
	public function ASColorUIResource(rgb:Number, alpha:Number){
		super(rgb, alpha);
	}
	
	public static function createResourceColor(color:ASColor):ASColorUIResource{
		return new ASColorUIResource(color.getRGB(), color.getAlpha());
	}
}
