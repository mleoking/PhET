/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.overflow.JProgressBar;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.icon.ProgressBarIcon implements Icon, UIResource {
	
	private var indeterminatePercent:Number;
	private var color:ASColor;
	
	public function ProgressBarIcon(){
		indeterminatePercent = 0;
		color = UIManager.getColor("ProgressBar.progressColor");
	}
	
	public function getIconWidth() : Number {
		return 0;
	}

	public function getIconHeight() : Number {
		return 0;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		var pb:JProgressBar = JProgressBar(com);
		if(pb == null){
			return;
		}
		
		var percent:Number;
		if(pb.isIndeterminate()){
			percent = indeterminatePercent;
			indeterminatePercent += 0.1;
			if(indeterminatePercent > 1){
				indeterminatePercent = 0;
			}
		}else{
			percent = pb.getPercentComplete();
		}
		
		var b:Rectangle = com.getPaintBounds();
		var box:Rectangle = new Rectangle(b);
		var boxWidth:Number = 5;
		var gap:Number = 1;
		g.beginFill(new SolidBrush(color));
		
		if(pb.getOrientation() == JProgressBar.VERTICAL){
			box.height = boxWidth;
			var minY:Number = b.y + b.height - b.height * percent;
			for(box.y = b.y+b.height-boxWidth; box.y >= minY; box.y -= (boxWidth+gap)){
				g.rectangle(box.x, box.y, box.width, box.height);
			}
			if(box.y < minY && box.y + boxWidth > minY){
				box.height = boxWidth - (minY - box.y);
				box.y = minY;
				g.rectangle(box.x, box.y, box.width, box.height);
			}
		}else{
			box.width = boxWidth;
			var maxX:Number = b.x + b.width * percent;
			for(;box.x <= maxX - boxWidth; box.x += (boxWidth+gap)){
				g.rectangle(box.x, box.y, box.width, box.height);
			}
			box.width = maxX - box.x;
			if(box.width > 0){
				g.rectangle(box.x, box.y, box.width, box.height);
			}
		}
		g.endFill();
	}

	public function uninstallIcon(com : Component) : Void {
	}

}
