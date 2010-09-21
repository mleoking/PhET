import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;

/**
 * @author iiley
 */
class test.CircleIcon implements Icon {
	
	private var color:ASColor;
	private var width:Number;
	private var height:Number;
	
	public function CircleIcon(color:ASColor, width:Number, height:Number){
		this.color = color;
		this.width = Math.round(width);
		this.height = Math.round(height);
	}
	
	public function getIconWidth() : Number {
		return width;
	}

	public function getIconHeight() : Number {
		return height;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		g.fillEllipse(new SolidBrush(color), x, y, width, height);
	}

	public function uninstallIcon(com : Component) : Void {
	}

}