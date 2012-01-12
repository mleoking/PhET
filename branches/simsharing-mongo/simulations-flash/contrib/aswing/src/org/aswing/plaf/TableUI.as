/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Dimension;
import org.aswing.overflow.JTable;
import org.aswing.plaf.ComponentUI;

/**
 * Pluggable look and feel interface for JTable.
 * @author iiley
 */
class org.aswing.plaf.TableUI extends ComponentUI {
    /**
     * Returns the view size.
     */    
	public function getViewSize(table:JTable):Dimension{
		trace("Error : Subclass must override this method!");
		throw new Error("Subclass must override this method!");
		return null;
	}
}