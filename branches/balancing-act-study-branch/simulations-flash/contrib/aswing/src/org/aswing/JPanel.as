/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.LayoutManager;
import org.aswing.plaf.PanelUI;
import org.aswing.UIManager;

/**
 * The general container - panel.
 * @author iiley
 */
class org.aswing.JPanel extends Container{
	
	public function JPanel(layout:LayoutManager){
		super();
		setName("JPanel");
		if(layout == undefined) layout = new FlowLayout();
		this.layout = layout;
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(PanelUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:PanelUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "PanelUI";
	}	
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingPanelUI;
    }
}
