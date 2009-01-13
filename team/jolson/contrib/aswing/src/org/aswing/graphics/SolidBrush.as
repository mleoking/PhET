/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.graphics.Brush;

/**
 * 
 * @author iiley
 */
class org.aswing.graphics.SolidBrush implements Brush{
	private var color:Number;
	private var alpha:Number;
	
	/**
	 * <p>
	 * SolidBrush(color:ASColor)<br>
	 * SolidBrush(color:Number, alpha:Number)<br>
	 */
	function SolidBrush(color:Object, alpha:Number){
		if(color instanceof ASColor){			
			setASColor(ASColor(color));
		}
		else{
			this.color=(color==undefined) ? ASColor.BLACK.getRGB():Number(color);
			this.alpha=(alpha==undefined) ? 100:Number(alpha);
		}
	}
	
	public function getColor():Number{
		return color;
	}
	
	public function setColor(color:Number):Void{		
		if(color!=null){
			this.color=color;
		}		
	}
	
	public function setAlpha(alpha:Number):Void{
			if(alpha!=null){
				this.alpha=alpha;
			}
	}
	
	public function getAlpha():Number{
		return alpha;
	}
	
	public function beginFill(target:MovieClip):Void{
		target.beginFill(color,alpha);
	}
	
	public function endFill(target:MovieClip):Void{
		target.endFill();
	}
	
	public function setASColor(color:ASColor):Void{
		if(color!=null){
			this.color=color.getRGB();
			this.alpha=color.getAlpha();
		}
	}
}
