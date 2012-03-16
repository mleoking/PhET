/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractTabbedPane;
import org.aswing.LayoutManager;
import org.aswing.plaf.AccordionUI;
import org.aswing.UIManager;

/**
 * Accordion Container.
 * @author iiley
 */
class org.aswing.overflow.JAccordion extends AbstractTabbedPane {
	
    /**
     * JAccordion()
     * <p>
     */
	public function JAccordion() {
		super();
		setName("JAccordion");
		
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(AccordionUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:AccordionUI):Void{
    	super.setUI(newUI);
    }
    
    public function getUI():AccordionUI{
    	return AccordionUI(ui);
    }
	
	public function getUIClassID():String{
		return "AccordionUI";
	}    
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicAccordionUI;
    }
	
	/**
	 * Generally you should not set layout to JAccordion.
	 * @param layout layoutManager for JAccordion
	 * @throws Error when you set a non-AccordionUI layout to JAccordion.
	 */
	public function setLayout(layout:LayoutManager):Void{
		if(layout instanceof AccordionUI){
			super.setLayout(layout);
		}else{
			trace("Cannot set non-AccordionUI layout to JAccordion!");
			throw Error("Cannot set non-AccordionUI layout to JAccordion!");
		}
	}
}
