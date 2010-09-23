package org.aswing.plaf
{
import org.aswing.*;

/**
 * Pluggable look and feel interface for JSplitPane.
 * @author iiley
 */
public class SplitPaneUI extends BaseComponentUI
{
	public function SplitPaneUI() {
		super();
	}

    /**
     * Messaged to relayout the JSplitPane based on the preferred size
     * of the children components.
     */
    public function resetToPreferredSizes(jc:JSplitPane):void{
    	trace("Subclass need to override this method!");
    }	
	
}
}