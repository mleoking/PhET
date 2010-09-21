/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.overflow.JTree;
import org.aswing.plaf.ComponentUI;
import org.aswing.tree.TreePath;

/**
 * Pluggable look and feel interface for JTree.
 * @author iiley
 */
class org.aswing.plaf.TreeUI extends ComponentUI {
	
	public function TreeUI(){
		super();
	}

    /**
      * Returns the Rectangle enclosing the label portion that the
      * last item in path will be drawn into.  Will return null if
      * any component in path is currently valid.
      */
    public function getPathBounds(tree:JTree, path:TreePath):Rectangle{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return null;
    };

    /**
      * Returns the path for passed in row.  If row is not visible
      * null is returned.
      */
    public function getPathForRow(tree:JTree, row:Number):TreePath{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return null;
    };

    /**
      * Returns the row that the last item identified in path is visible
      * at.  Will return -1 if any of the elements in path are not
      * currently visible.
      */
    public function getRowForPath(tree:JTree, path:TreePath):Number{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return 0;
    };

    /**
      * Returns the number of rows that are being displayed.
      */
    public function getRowCount(tree:JTree):Number{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return 0;
    };

    /**
      * Returns the path to the node that is closest to x,y.  If
      * there is nothing currently visible this will return null, otherwise
      * it'll always return a valid path.  If you need to test if the
      * returned object is exactly at x, y you should get the bounds for
      * the returned path and test x, y against that.
      */
    public function getClosestPathForLocation(tree:JTree, x:Number, y:Number):TreePath{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return null;
    }

    /**
      * Returns true if the tree is being edited.  The item that is being
      * edited can be returned by getEditingPath().
      */
    public function isEditing(tree:JTree):Boolean{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return false;
    };

    /**
      * Stops the current editing session.  This has no effect if the
      * tree isn't being edited.  Returns true if the editor allows the
      * editing session to stop.
      */
    public function stopEditing(tree:JTree):Boolean{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return false;
    };

    /**
      * Cancels the current editing session. This has no effect if the
      * tree isn't being edited.  Returns true if the editor allows the
      * editing session to stop.
      */
    public function cancelEditing(tree:JTree):Void{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    };

    /**
      * Selects the last item in path and tries to edit it.  Editing will
      * fail if the CellEditor won't allow it for the selected item.
      * 
      * @return true is started sucessful, editing fail
      */
    public function startEditingAtPath(tree:JTree, path:TreePath):Boolean{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return false;
    };

    /**
     * Returns the path to the element that is being edited.
     */
    public function getEditingPath(tree:JTree):TreePath{
    	trace("Error : Subclass must override this method!");
    	throw new Error("Subclass must override this method!");
    	return null;
    };
    
    /**
     * Returns the view size.
     */    
	public function getViewSize(tree:JTree):Dimension{
		trace("Error : Subclass must override this method!");
		throw new Error("Subclass must override this method!");
		return null;
	}

    /**
     * Returns the treePath that the user mouse pointed, null if no path was pointed.
     */	
	public function getMousePointedPath():TreePath{
		trace("Error : Subclass must override this method!");
		throw new Error("Subclass must override this method!");
		return null;
	}
}