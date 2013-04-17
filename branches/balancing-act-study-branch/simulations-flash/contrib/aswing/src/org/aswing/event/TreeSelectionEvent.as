/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Event;
import org.aswing.tree.TreePath;

/**
 * An event that characterizes a change in the current
 * selection.  The change is based on any number of paths.
 * TreeSelectionListeners will generally query the source of
 * the event for the new selected status of each potentially
 * changed row.
 * 
 * @author iiley
 */
class org.aswing.event.TreeSelectionEvent extends Event {
    /** Paths this event represents. */
    private var paths:Array;
    /** For each path identifies if that is path is in fact new. */
    private var areNew:Array; //boolean[]
    /** leadSelectionPath before the paths changed, may be null. */
    private var oldLeadSelectionPath:TreePath;
    /** leadSelectionPath after the paths changed, may be null. */
    private var newLeadSelectionPath:TreePath;

    /**
      * Represents a change in the selection of a TreeSelectionModel.
      * paths identifies the paths that have been either added or
      * removed from the selection.
      *
      * @param source source of event
      * @param paths the paths that have changed in the selection
      */
    public function TreeSelectionEvent(source:Object, paths:Array,
			      areNew:Array, oldLeadSelectionPath:TreePath,
			      newLeadSelectionPath:TreePath)
    {
		super(source);
		this.paths = paths;
		this.areNew = areNew;
		this.oldLeadSelectionPath = oldLeadSelectionPath;
		this.newLeadSelectionPath = newLeadSelectionPath;
    }

    /**
      * Returns the paths(TreePath[]) that have been added or removed from the
      * selection.
      */
    public function getPaths():Array{
		return paths.concat();
    }

    /**
      * Returns the first path element.
      */
    public function getPath():TreePath{
		return paths[0];
    }

    /**
     * isAddedPath(path:TreePath)<br>
     * isAddedPath(index:Number)<br>
     * isAddedPath()
     * <p>
     * Returns true if the first path element has been added to the
     * selection, a return value of false means the first path has been
     * removed from the selection.
     * @see #isAddedPathOfPath()
     * @see #isAddedPathOfIndex()
     */
    public function isAddedPath():Boolean {
    	var p = arguments[0];
    	if(p instanceof TreePath){
    		return isAddedPathOfPath(p);
    	}else if(typeof(p) == "number" || p instanceof Number){
    		return isAddedPathOfIndex(p);
    	}
		return areNew[0] == true;
    }

    /**
     * Returns true if the path identified by path was added to the
     * selection. A return value of false means the path was in the
     * selection but is no longer in the selection. This will raise if
     * path is not one of the paths identified by this event.
     */
    public function isAddedPathOfPath(path:TreePath):Boolean {
		for(var counter:Number = paths.length - 1; counter >= 0; counter--){
		    if(paths[counter].equals(path)){
				return areNew[counter]==true;
		    }
		}
		trace("Error : path is not a path identified by the TreeSelectionEvent");
		throw new Error("path is not a path identified by the TreeSelectionEvent");
    }

    /**
     * Returns true if the path identified by <code>index</code> was added to
     * the selection. A return value of false means the path was in the
     * selection but is no longer in the selection. This will raise if
     * index < 0 || >= <code>getPaths</code>.length.
     *
     * @since 1.3
     */
    public function isAddedPathOfIndex(index:Number):Boolean {
		if (paths == null || index < 0 || index >= paths.length) {
			trace("Error : index is beyond range of added paths identified by TreeSelectionEvent");
		    throw new Error("index is beyond range of added paths identified by TreeSelectionEvent");
		}
		return areNew[index]==true;
    }

    /**
     * Returns the path that was previously the lead path.
     */
    public function getOldLeadSelectionPath():TreePath {
		return oldLeadSelectionPath;
    }

    /**
     * Returns the current lead path.
     */
    public function getNewLeadSelectionPath():TreePath {
		return newLeadSelectionPath;
    }

    /**
     * Returns a copy of the receiver, but with the source being newSource.
     */
    public function cloneWithSource(newSource:Object):TreeSelectionEvent {
		return new TreeSelectionEvent(newSource, paths, areNew, oldLeadSelectionPath, newLeadSelectionPath);
    }
}