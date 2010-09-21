/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractTabbedPane;
import org.aswing.plaf.TabbedPaneUI;
import org.aswing.UIManager;

/**
 * A component that lets the user switch between a group of components by
 * clicking on a tab with a given title and/or icon.
 * <p>
 * Tabs/components are added to a <code>TabbedPane</code> object by using the
 * <code>addTab</code> and <code>insertTab</code> methods.
 * A tab is represented by an index corresponding
 * to the position it was added in, where the first tab has an index equal to 0
 * and the last tab has an index equal to the tab count minus 1.
 * @author iiley
 */
class org.aswing.overflow.JTabbedPane extends AbstractTabbedPane {
	
	private var tabPlacement:Number;
	
    /**
     * JTabbedPane()
     * <p>
     */
	public function JTabbedPane() {
		super();
		setName("JTabbedPane");
		tabPlacement = TOP;
		
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(TabbedPaneUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:TabbedPaneUI):Void{
    	super.setUI(newUI);
    }
    
    public function getUI():TabbedPaneUI{
    	return TabbedPaneUI(ui);
    }
	
	public function getUIClassID():String{
		return "TabbedPaneUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingTabbedPaneUI;
    }
	
    /**
     * Sets the tab placement for this tabbedpane.
     * Possible values are:<ul>
     * <li><code>JTabbedPane.TOP</code>
     * <li><code>JTabbedPane.BOTTOM</code>
     * <li><code>JTabbedPane.LEFT</code>
     * <li><code>JTabbedPane.RIGHT</code>
     * </ul>
     * The default value, if not set, is <code>SwingConstants.TOP</code>.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     */
    public function setTabPlacement(tabPlacement:Number):Void {
    	if(this.tabPlacement != tabPlacement){
    		this.tabPlacement = tabPlacement;
    		revalidate();
    		repaint();
    	}
    }
    
    /**
     * Returns the placement of the tabs for this tabbedpane.
     * @see #setTabPlacement()
     */
    public function getTabPlacement():Number{
    	return tabPlacement;
    }
    
    /**
     * Not support this function.
     * @throws Error("Not supported setVisibleAt!")
     */
    public function setVisibleAt(index:Number, visible:Boolean):Void{
    	throw new Error("Not supported setVisibleAt!");
    }
    
	/**
	 * Generally you should not set layout to JAccordion.
	 * @param layout layoutManager for JAccordion
	 * @throws Error when you set a non-AccordionUI layout to JAccordion.
	 */
//	public function setLayout(layout:LayoutManager):Void{
//		if(layout instanceof TabbedPaneUI){
//			super.setLayout(layout);
//		}else{
//			trace("Cannot set non-AccordionUI layout to JAccordion!");
//			throw Error("Cannot set non-AccordionUI layout to JAccordion!");
//		}
//	}
}