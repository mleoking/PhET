/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.LayoutManager;
import org.aswing.plaf.ComponentUI;

/**
 * Pluggable look and feel interface for Accordion.
 *
 * @author iiley
 */
class org.aswing.plaf.AccordionUI extends ComponentUI implements LayoutManager{
	
	public function AccordionUI() {
		super();
	}

    /**
     * may need override in subclass
     */
    public function addLayoutComponent(comp:Component, constraints:Object):Void{
    }

    /**
     * may need override in subclass
     */
    public function removeLayoutComponent(comp:Component):Void{
    }
	
	/**
     * may need override in subclass
	 */
    public function preferredLayoutSize(target:Container):Dimension{
    	return target.getSize();
    }

	/**
     * may need override in subclass
	 */
    public function minimumLayoutSize(target:Container):Dimension{
    	return new Dimension(0, 0);
    }
	
	/**
     * may need override in subclass
	 */
    public function maximumLayoutSize(target:Container):Dimension{
    	return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }
    
    /**
     * may need override in subclass
     */
    public function layoutContainer(target:Container):Void{
    }
    
	/**
     * may need override in subclass
	 */
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0;
    }

	/**
     * may need override in subclass
	 */
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0;
    }

    /**
     * may need override in subclass
     */
    public function invalidateLayout(target:Container):Void{
    }
}
