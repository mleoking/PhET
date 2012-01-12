/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree { 

import org.aswing.plaf.UIResource;
import org.aswing.tree.GeneralTreeCellFactory;

/**
 * @author iiley
 */
public class GeneralTreeCellFactoryUIResource extends GeneralTreeCellFactory 
	implements UIResource{
	
	public function GeneralTreeCellFactoryUIResource(cellClass : Class) {
		super(cellClass);
	}

}
}