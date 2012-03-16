/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.overflow.JSplitPane;
import org.aswing.plaf.ComponentUI;

/**
 * Pluggable look and feel interface for JSplitPane.
 * @author iiley
 */
class org.aswing.plaf.SplitPaneUI extends ComponentUI {
	
	public function SplitPaneUI() {
		super();
	}

    /**
     * Messaged to relayout the JSplitPane based on the preferred size
     * of the children components.
     */
    public function resetToPreferredSizes(jc:JSplitPane):Void{
    	trace("Subclass need to override this method!");
    }
}