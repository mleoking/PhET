/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.plaf.UIResource;
import org.aswing.table.GeneralTableCellFactory;

/**
 * @author iiley
 */
class org.aswing.table.GeneralTableCellFactoryUIResource extends GeneralTableCellFactory implements UIResource{
	public function GeneralTableCellFactoryUIResource(cellClass:Function){
		super(cellClass);
	}
}