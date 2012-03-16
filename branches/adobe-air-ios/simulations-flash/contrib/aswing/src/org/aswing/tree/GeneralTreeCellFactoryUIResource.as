/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.plaf.UIResource;
import org.aswing.tree.GeneralTreeCellFactory;

/**
 * @author iiley
 */
class org.aswing.tree.GeneralTreeCellFactoryUIResource extends GeneralTreeCellFactory 
	implements UIResource{
	
	public function GeneralTreeCellFactoryUIResource(cellClass : Function) {
		super(cellClass);
	}

}