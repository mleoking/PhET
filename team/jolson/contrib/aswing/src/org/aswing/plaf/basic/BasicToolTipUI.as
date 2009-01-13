/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.JLabel;
import org.aswing.JToolTip;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ComponentUI;
import org.aswing.plaf.ToolTipUI;
import org.aswing.util.HashMap;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.BasicToolTipUI extends ToolTipUI {
	
	//shared instance
	private static var toolTipUI:ToolTipUI;
    public static function createInstance(c:Component):ComponentUI {
    	if(toolTipUI == null){
    		toolTipUI = new BasicToolTipUI();
    	}
        return toolTipUI;
    }
	
	private var labels:HashMap;
	private var tipListeners:HashMap;
	
	public function BasicToolTipUI() {
		super();
		labels = new HashMap();
		tipListeners = new HashMap();
	}
	
    public function installUI(c:Component):Void{
    	var b:JToolTip = JToolTip(c);
        installDefaults(b);
        initallComponents(b);
        installListeners(b);
    }

	private function installDefaults(b:JToolTip):Void{
        // load shared instance defaults
        var pp:String = "ToolTip.";
        LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");
        LookAndFeel.installBasicProperties(b, pp);
	}
		
	private function getLabel(c:Component):JLabel{
		return JLabel(labels.get(c.getID()));
	}
	
	private function initallComponents(b:JToolTip):Void{
		b.setLayout(new BorderLayout());
		var label:JLabel = new JLabel(b.getTipText());
		label.setTriggerEnabled(false);
		label.setFont(null); //make it to use parent(JToolTip) font
		b.append(label, BorderLayout.CENTER);
		labels.put(b.getID(), label);
	}
	
	private function installListeners(b:JToolTip):Void{
		var lis:Object = b.addEventListener(JToolTip.ON_TIP_TEXT_CHANGED, __tipTextChanged, this);
		tipListeners.put(b.getID(), lis);
	}
	
	private function __tipTextChanged(toolTip:JToolTip):Void{
		getLabel(toolTip).setText(toolTip.getTipText());
	}
	
    public function uninstallUI(c:Component):Void{
    	var b:JToolTip = JToolTip(c);
        uninstallDefaults(b);
        uninstallListeners(b);
        uninstallComponents(b);
    }
    
    private function uninstallDefaults(b:JToolTip):Void{
    	LookAndFeel.uninstallBorder(b);
    }
    
    private function uninstallComponents(b:JToolTip):Void{
    	var label:JLabel = JLabel(labels.remove(b.getID()));
    	b.remove(label);
    }    
    
    private function uninstallListeners(b:JToolTip):Void{
    	b.removeEventListener(tipListeners.remove(b.getID()));
    }

}
