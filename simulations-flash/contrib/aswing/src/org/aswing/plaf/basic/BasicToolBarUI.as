/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractButton;
import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FocusManager;
import org.aswing.JCheckBox;
import org.aswing.JRadioButton;
import org.aswing.JToolBar;
import org.aswing.LookAndFeel;
import org.aswing.plaf.basic.border.ToolBarButtonBorder;
import org.aswing.plaf.ToolBarUI;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;

/**
 * This UI used some reflection way, so take care this when this things changed:
 * <ul>
 * <li> Component.opaque property name, see method ____judgeBackgroundPainting of this class
 * <li> Component.isOpaque method
 * </ul>
 * @author iiley
 */
class org.aswing.plaf.basic.BasicToolBarUI extends ToolBarUI{
	
	private var toolBar:JToolBar;
	private var buttonBordersBak:HashMap;
	private var opaqueBak:HashMap;
	private var additionListener:Object;
	
	private var buttonAddRemoveListener:Object;
	
	public function BasicToolBarUI() {
		super();
		buttonBordersBak    = new HashMap();
		opaqueBak           = new HashMap();
		additionListener = new Object();
		additionListener[Component.ON_FOCUS_GAINED] = Delegate.create(this, __onButtonGainedFocus);
		additionListener[Component.ON_FOCUS_LOST] = Delegate.create(this, __onButtonLostFocus);
	}
	
	
    public function installUI(c:Component):Void{
    	var b:JToolBar = JToolBar(c);
    	toolBar = b;
        installDefaults(b);
        initallComponents(b);
        installListeners(b);
    }

	private function installDefaults(b:JToolBar):Void{
        // load shared instance defaults
        var pp:String = "ToolBar.";
        LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");
        LookAndFeel.installBasicProperties(b, pp);
	}
			
	private function initallComponents(b:JToolBar):Void{
		for(var i:Number=0; i<toolBar.getComponentCount(); i++){
			__childAdded(null, toolBar.getComponent(i));
		}
	}
	
	private function installListeners(b:JToolBar):Void{
		buttonAddRemoveListener = new Object();
		buttonAddRemoveListener[Container.ON_COM_ADDED] = Delegate.create(this, __childAdded);
		buttonAddRemoveListener[Container.ON_COM_REMOVED] = Delegate.create(this, __childRemoved);
		b.addEventListener(buttonAddRemoveListener);
	}
	
	
    public function uninstallUI(c:Component):Void{
    	var b:JToolBar = JToolBar(c);
        uninstallDefaults(b);
        uninstallListeners(b);
        uninstallComponents(b);
    }
    
    private function uninstallDefaults(b:JToolBar):Void{
    	LookAndFeel.uninstallBorder(b);
    }
    
    private function uninstallComponents(b:JToolBar):Void{
		for(var i:Number=0; i<toolBar.getComponentCount(); i++){
			__childRemoved(null, toolBar.getComponent(i));
		}
    }    
    
    private function uninstallListeners(b:JToolBar):Void{
    	b.removeEventListener(buttonAddRemoveListener);
    }
	
	//-------------------------------------------------------------------
	private function getOriginalBorder(button:AbstractButton):Border{
		return Border(buttonBordersBak.remove(button.getID()));
	}
	//set to toolbar button style border
	private function __childAdded(source:Container, child:Component):Void{
		var button:AbstractButton = AbstractButton(child);
		if(isNormalButton(button)){
			var border:Border = button.getBorder();
			if(border == null){
				buttonBordersBak.put(button.getID(), border);
			}
			var toolBarButtonBorder:ToolBarButtonBorder = new ToolBarButtonBorder(button);
			button.setBorder(toolBarButtonBorder);
			opaqueBak.put(button.getID(), button.isOpaque);
			var o:Object = button;
			o.isOpaque = Delegate.create(this, ____judgeBackgroundPainting, button);
			button.addEventListener(additionListener);
		}
	}
	
	private function ____judgeBackgroundPainting(button:AbstractButton):Boolean{
		var obj:Object = button;
		var opaque:Boolean = (obj.opaque == true || obj.opaque == undefined) 
							&& ((button.isFocusOwner() && FocusManager.getCurrentManager().isTraversing()) 
									|| button.getModel().isRollOver() || button.getModel().isSelected());
		return opaque;
	}
	
	//back to the button's original border
	private function __childRemoved(source:Container, child:Component):Void{
		var button:AbstractButton = AbstractButton(child);
		if(isNormalButton(button)){
			var originalBorder:Border = getOriginalBorder(button);
			if(button.getBorder() instanceof ToolBarButtonBorder){
				button.setBorder(originalBorder);
			}
			var o:Object = button;
			o.isOpaque = opaqueBak.remove(button.getID());
			button.removeEventListener(additionListener);
		}
	}
	
	private function __onButtonGainedFocus(source:AbstractButton):Void{
		source.repaint();
	}
	private function __onButtonLostFocus(source:AbstractButton):Void{
		source.repaint();
	}
	
	private function isNormalButton(button:AbstractButton):Boolean{
		return button != null && !(button instanceof JCheckBox || button instanceof JRadioButton);
	}
}
