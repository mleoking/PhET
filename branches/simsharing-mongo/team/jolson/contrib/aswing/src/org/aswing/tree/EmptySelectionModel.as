/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.tree.DefaultTreeSelectionModel;

/**
 * <code>EmptySelectionModel</code> is a <code>TreeSelectionModel</code>
 * that does not allow anything to be selected.
 * @author iiley
 */
class org.aswing.tree.EmptySelectionModel extends DefaultTreeSelectionModel {
	
	public function EmptySelectionModel() {
		super();
	}

    /** Unique shared instance. */
    private static var _sharedInstance:EmptySelectionModel;

    /** Returns a shared instance of an empty selection model. */
    public static function sharedInstance():EmptySelectionModel {
    	if(_sharedInstance == null){
    		_sharedInstance = new EmptySelectionModel();
    	}
        return _sharedInstance;
    }

    /** A <code>null</code> implementation that selects nothing. */
    public function setSelectionPaths(pPaths:Array):Void {}
    /** A <code>null</code> implementation that adds nothing. */
    public function addSelectionPaths(pPaths:Array):Void {}
    /** A <code>null</code> implementation that removes nothing. */
    public function removeSelectionPaths(pPaths:Array):Void {}
}