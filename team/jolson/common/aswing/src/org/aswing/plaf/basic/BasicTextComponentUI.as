import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.JTextComponent;
import org.aswing.LookAndFeel;
import org.aswing.plaf.TextUI;

/**
 * @author Tomato, iiley
 */
class org.aswing.plaf.basic.BasicTextComponentUI extends TextUI {
	
	public function BasicTextComponentUI(){
		super();
	}
	
	//override this to the sub component's prefix
    private function getPropertyPrefix():String {
        return "";
    }

    public function installUI(c:Component):Void{
    	super.installUI(c);
    	var b:JTextComponent = JTextComponent(c);
        installDefaults(b);
        installListeners(b);
    }
    
	private function installDefaults(b:JTextComponent):Void{
        // load shared instance defaults
        var pp:String = getPropertyPrefix();

        LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");
        LookAndFeel.installBasicProperties(b, getPropertyPrefix());
	}
	
	private function installListeners(b:JTextComponent):Void{
	}
		
	
    public function uninstallUI(c:Component):Void{
    	super.uninstallUI(c);
    	
    	var b:JTextComponent = JTextComponent(c);
        uninstallDefaults(b);
        uninstallListeners(b);
    }
    
    private function uninstallDefaults(b:JTextComponent):Void{
    	LookAndFeel.uninstallBorder(b);
    }
    
    private function uninstallListeners(b:JTextComponent):Void{
    }

	public function paint(c:Component , g:Graphics , r:Rectangle):Void{
		super.paint(c, g, r);
	}
	
    public function getMinimumSize(c:Component):Dimension{
    	return c.getInsets().getOutsideSize();
    }

    public function getMaximumSize(c:Component):Dimension{
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }	
}