import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.GradientBrush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.basic.BasicTextComponentUI;
import org.aswing.plaf.ComponentUI;
import org.aswing.UIManager;

/**
 * @author Tomato
 */
class org.aswing.plaf.basic.BasicTextAreaUI extends BasicTextComponentUI {

	private static var textUI:BasicTextAreaUI;
	
	private var highlight:ASColor;
	private var shadow:ASColor;
	
	public static function createInstance(c:Component):ComponentUI {
		if(textUI == null){
			textUI = new BasicTextAreaUI();
		}
		return textUI;
	}
	
	public function BasicTextAreaUI(){
		super();
		highlight = UIManager.getColor("TextArea.highlight");
		shadow = UIManager.getColor("TextArea.shadow");		
	}
	
	//override this to the sub component's prefix
    private function getPropertyPrefix():String {
        return "TextArea.";
    }

	public function paint(c:Component , g:Graphics , r:Rectangle):Void{
		super.paint(c, g, r);
	}
    
    private function paintBackGround(c:Component, g:Graphics, r:Rectangle):Void{
    	if(c.isOpaque()  && c.isEnabled()){
			var x:Number = r.x;
			var y:Number = r.y;
			var w:Number = r.width;
			var h:Number = r.height;
			g.fillRectangle(new SolidBrush(c.getBackground()), x,y,w,h);
			
			//var colors:Array = [0xF7F7F7, c.getBackground().getRGB()];
            var colors:Array = [c.getBackground().getRGB(), c.getBackground().getRGB()];
			var alphas:Array = [50, 0];
			var ratios:Array = [0, 100];
			var matrix:Object = {matrixType:"box", x:x, y:y, w:w, h:h, r:(90/180)*Math.PI};        
		    var brush:GradientBrush=new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
		    g.fillRectangle(brush,x,y,w,h);
		    
    	}
    	
    }
    
    /**
     * Return null, make it to count in JTextFiled's countPreferredSize method.
     */
    public function getPreferredSize(c:Component):Dimension{
    	return null;
    }
}