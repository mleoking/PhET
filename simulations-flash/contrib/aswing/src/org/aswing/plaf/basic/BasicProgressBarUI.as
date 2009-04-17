/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASTextExtent;
import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.overflow.JProgressBar;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ProgressBarUI;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicProgressBarUI extends ProgressBarUI {
	
	private var stringText:TextField;
	private var stateListener:Object;
	
	public function BasicProgressBarUI() {
		super();
	}

	public function installUI(c:Component):Void{
		installDefaults(JProgressBar(c));
		installListeners(JProgressBar(c));
	}
	
	public function uninstallUI(c:Component):Void{
		uninstallDefaults(JProgressBar(c));
		uninstallListeners(JProgressBar(c));
		stringText.removeTextField();
	}
	
	public function installDefaults(s:JProgressBar):Void{
		LookAndFeel.installColorsAndFont(s, "ProgressBar.background", "ProgressBar.foreground", "ProgressBar.font");
		LookAndFeel.installBasicProperties(s, "ProgressBar.");
		LookAndFeel.installBorder(s, "ProgressBar.border");
		
		if(s.getIcon() === undefined || s.getIcon() instanceof UIResource){
			s.setIcon(UIManager.getIcon("ProgressBar.icon"));
		}
	}
	
	public function uninstallDefaults(s:JProgressBar):Void{
		LookAndFeel.uninstallBorder(s);
		if(s.getIcon() instanceof UIResource){
			s.setIcon(undefined);
		}
	}
	public function installListeners(s:JProgressBar):Void{
		stateListener = s.addChangeListener(__stateChanged, this);
	}
	public function uninstallListeners(s:JProgressBar):Void{
		s.removeEventListener(stateListener);
	}
	
	public function create(c:Component):Void{
		stringText = c.createTextField("p_text");
	}
	
	private function __stateChanged(source:JProgressBar):Void{
		source.repaint();
	}
	
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		var sp:JProgressBar = JProgressBar(c);
		
		sp.getIcon().paintIcon(c, g, b.x, b.y);
		
		if(sp.getString() != null && sp.getString().length>0){
			stringText.text = sp.getString();
	    	ASWingUtils.applyTextFontAndColor(stringText, sp.getFont(), sp.getForeground());
			
			if (sp.getOrientation() == JProgressBar.VERTICAL){
				stringText._rotation = -90;
				stringText._x = Math.round(b.x + (b.width - stringText._width)/2);
				stringText._y = Math.round(b.y + (b.height - stringText._height)/2 + stringText._height);
			}else{
				stringText._rotation = 0;
				stringText._x = Math.round(b.x + (b.width - stringText._width)/2);
				stringText._y = Math.round(b.y + (b.height - stringText._height)/2);
			}
		}else{
			stringText.text = "";
		}
	}
		
	public function getPreferredSize(c:Component):Dimension{
		var sp:JProgressBar = JProgressBar(c);
		var size:Dimension;
		if (sp.getOrientation() == JProgressBar.VERTICAL){
			size = getPreferredInnerVertical();
		}else{
			size = getPreferredInnerHorizontal();
		}
		
		if(sp.getString() != null){
			var extent:ASTextExtent = c.getFont().getASTextFormat().getTextExtent(sp.getString());
			if (sp.getOrientation() == JProgressBar.VERTICAL){
				size.width = Math.max(size.width, extent.getTextFieldHeight());
				size.height = Math.max(size.height, extent.getTextFieldWidth());
			}else{
				size.width = Math.max(size.width, extent.getTextFieldWidth());
				size.height = Math.max(size.height, extent.getTextFieldHeight());
			}
		}
		return sp.getInsets().getOutsideSize(size);
	}
    public function getMaximumSize(c:Component):Dimension{
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }
    public function getMinimumSize(c:Component):Dimension{
		return c.getInsets().getOutsideSize(new Dimension(1, 1));
    }
    
    private function getPreferredInnerHorizontal():Dimension{
    	return new Dimension(80, 12);
    }
    private function getPreferredInnerVertical():Dimension{
    	return new Dimension(12, 80);
    }
}
