/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.IEventDispatcher;
import org.aswing.tree.RowMapper;
import org.aswing.tree.TreePath;

/**
  * This interface represents the current state of the selection for
  * the tree component.
  * For information and examples of using tree selection models,
  * see <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html">How to Use Trees</a>
  * in <em>The Java Tutorial.</em>
  *
  * <p>
  * The state of the tree selection is characterized by
  * a set of TreePaths, and optionally a set of integers. The mapping
  * from TreePath to integer is done by way of an instance of RowMapper.
  * It is not necessary for a TreeSelectionModel to have a RowMapper to
  * correctly operate, but without a RowMapper <code>getSelectionRows</code>
  * will return null.
  *
  * <p>
  * 
  * A TreeSelectionModel can be configured to allow only one
  * path (<code>SINGLE_TREE_SELECTION</code>) a number of
  * continguous paths (<code>CONTIGUOUS_TREE_SELECTION</code>) or a number of
  * discontiguous paths (<code>DISCONTIGUOUS_TREE_SELECTION</code>).
  * A <code>RowMapper</code> is used to determine if TreePaths are
  * contiguous.
  * In the absence of a RowMapper <code>CONTIGUOUS_TREE_SELECTION</code> and
  * <code>DISCONTIGUOUS_TREE_SELECTION</code> behave the same, that is they
  * allow any number of paths to be contained in the TreeSelectionModel.
  *
  * <p>
  * 
  * For a selection model of <code>CONTIGUOUS_TREE_SELECTION</code> any
  * time the paths are changed (<code>setSelectionPath</code>,
  * <code>addSelectionPath</code> ...) the TreePaths are again checked to
  * make they are contiguous. A check of the TreePaths can also be forced
  * by invoking <code>resetRowSelection</code>. How a set of discontiguous
  * TreePaths is mapped to a contiguous set is left to implementors of
  * this interface to enforce a particular policy.
  *
  * <p>
  *
  * Implementations should combine duplicate TreePaths that are
  * added to the selection. For example, the following code
  * <pre>
  *   var paths:Array = [ treePath, treePath ];
  *   treeSelectionModel.setSelectionPaths(paths);
  * </pre>
  * should result in only one path being selected:
  * <code>treePath</code>, and
  * not two copies of <code>treePath</code>.
  *
  * <p>
  *
  * The lead TreePath is the last path that was added (or set). The lead
  * row is then the row that corresponds to the TreePath as determined
  * from the RowMapper.
  * <p>
 * (Fully quoted from java swing's tree doc)
  *
 * @author iiley
 */
interface org.aswing.tree.TreeSelectionModel extends IEventDispatcher{
	 
	 /**
     * Sets the selection model, which must be one of SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
     * <p>
     * This may change the selection if the current selection is not valid
     * for the new mode. For example, if three TreePaths are
     * selected when the mode is changed to <code>SINGLE_TREE_SELECTION</code>,
     * only one TreePath will remain selected. It is up to the particular
     * implementation to decide what TreePath remains selected.
     */
    public function setSelectionMode(mode:Number):Void;

    /**
     * Returns the current selection mode, one of
     * <code>SINGLE_TREE_SELECTION</code>,
     * <code>CONTIGUOUS_TREE_SELECTION</code> or
     * <code>DISCONTIGUOUS_TREE_SELECTION</code>.
     */
    public function getSelectionMode():Number;

    /**
      * Sets the selection to path. If this represents a change, then
      * the TreeSelectionListeners are notified. If <code>path</code> is
      * null, this has the same effect as invoking <code>clearSelection</code>.
      *
      * @param path new path to select
      */
    public function setSelectionPath(path:TreePath):Void;

    /**
      * Sets the selection to path(TreePath[]) . If this represents a change, then
      * the TreeSelectionListeners are notified. If <code>paths</code> is
      * null, this has the same effect as invoking <code>clearSelection</code>.
      *
      * @param paths new selection
      */
    public function setSelectionPaths(paths:Array):Void;

    /**
      * Adds path to the current selection. If path is not currently
      * in the selection the TreeSelectionListeners are notified. This has
      * no effect if <code>path</code> is null.
      *
      * @param path the new path to add to the current selection
      */
    public function addSelectionPath(path:TreePath):Void;

    /**
      * Adds paths(TreePath[]) to the current selection.  If any of the paths in
      * paths are not currently in the selection the TreeSelectionListeners
      * are notified. This has
      * no effect if <code>paths</code> is null.
      *
      * @param paths the new paths to add to the current selection
      */
    public function addSelectionPaths(paths:Array):Void;

    /**
      * Removes path from the selection. If path is in the selection
      * The TreeSelectionListeners are notified. This has no effect if
      * <code>path</code> is null.
      *
      * @param path the path to remove from the selection
      */
    public function removeSelectionPath(path:TreePath):Void;

    /**
      * Removes paths(TreePath[]) from the selection.  If any of the paths in 
      * <code>paths</code>
      * are in the selection, the TreeSelectionListeners are notified.
      * This method has no effect if <code>paths</code> is null.
      *
      * @param paths the path to remove from the selection
      */
    public function removeSelectionPaths(paths:Array):Void;

    /**
      * Returns the first path in the selection. How first is defined is
      * up to implementors, and may not necessarily be the TreePath with
      * the smallest integer value as determined from the
      * <code>RowMapper</code>.
      */
    public function getSelectionPath():TreePath;

    /**
      * Returns the paths(TreePath[]) in the selection. This will return null (or an
      * empty array) if nothing is currently selected.
      */
    public function getSelectionPaths():Array;

    /**
     * Returns the number of paths that are selected.
     */
    public function getSelectionCount():Number;

    /**
      * Returns true if the path, <code>path</code>, is in the current
      * selection.
      */
    public function isPathSelected(path:TreePath):Boolean;

    /**
      * Returns true if the selection is currently empty.
      */
    public function isSelectionEmpty():Boolean;

    /**
      * Empties the current selection.  If this represents a change in the
      * current selection, the selection listeners are notified.
      */
    public function clearSelection():Void;

    /**
     * Sets the RowMapper instance. This instance is used to determine
     * the row for a particular TreePath.
     */
    public function setRowMapper(newMapper:RowMapper):Void;

    /**
     * Returns the RowMapper instance that is able to map a TreePath to a
     * row.
     */
    public function getRowMapper():RowMapper;

    /**
      * Returns all of the currently selected rows. This will return
      * null (or an empty array) if there are no selected TreePaths or
      * a RowMapper has not been set.
      */
    public function getSelectionRows():Array;

    /**
     * Returns the smallest value obtained from the RowMapper for the
     * current set of selected TreePaths. If nothing is selected,
     * or there is no RowMapper, this will return -1.
      */
    public function getMinSelectionRow():Number;

    /**
     * Returns the largest value obtained from the RowMapper for the
     * current set of selected TreePaths. If nothing is selected,
     * or there is no RowMapper, this will return -1.
      */
    public function getMaxSelectionRow():Number;

    /**
      * Returns true if the row identified by <code>row</code> is selected.
      */
    public function isRowSelected(row:Number):Boolean;

    /**
     * Updates this object's mapping from TreePaths to rows. This should
     * be invoked when the mapping from TreePaths to integers has changed
     * (for example, a node has been expanded).
     * <p>
     * You do not normally have to call this; JTree and its associated
     * listeners will invoke this for you. If you are implementing your own
     * view class, then you will have to invoke this.
     */
    public function resetRowSelection():Void;

    /**
     * Returns the lead selection index. That is the last index that was
     * added.
     */
    public function getLeadSelectionRow():Number;

    /**
     * Returns the last path that was added. This may differ from the 
     * leadSelectionPath property maintained by the JTree.
     */
    public function getLeadSelectionPath():TreePath;

    /**
     * Adds a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired when the selection mode
     * changes.
     *
     * @param listener  the PropertyChangeListener to be added
     * @see org.aswing.overflow.JTree#ON_PROPERTY_CHANGED
     */
    public function addPropertyChangeListener(func:Function, contextObj:Object):Object;

    /**
      * Adds x to the list of listeners that are notified each time the
      * set of selected TreePaths changes.
      *
      * @param x the new listener to be added
      * @see org.aswing.overflow.JTree#ON_SELECTION_CHANGED
      */
    public function addTreeSelectionListener(func:Function, contextObj:Object):Object;
}