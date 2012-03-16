/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree { 

import flash.events.EventDispatcher;
import org.aswing.DefaultListSelectionModel;
import org.aswing.event.*;
import org.aswing.tree.*;
import org.aswing.util.*;

/**
 * Default implementation of TreeSelectionModel.  Listeners are notified
 * whenever
 * the paths in the selection change, not the rows. In order
 * to be able to track row changes you may wish to become a listener 
 * for expansion events on the tree and test for changes from there.
 * <p>resetRowSelection is called from any of the methods that update
 * the selected paths. If you subclass any of these methods to
 * filter what is allowed to be selected, be sure and message
 * <code>resetRowSelection</code> if you do not message super.
 * 
 * @author iiley
 */
public class DefaultTreeSelectionModel extends EventDispatcher implements TreeSelectionModel{
	
    /** Selection can only contain one path at a time. */
    public static const SINGLE_TREE_SELECTION:int = 1;

    /** Selection can only be contiguous. This will only be enforced if
     * a RowMapper instance is provided. That is, if no RowMapper is set
     * this behaves the same as DISCONTIGUOUS_TREE_SELECTION. */
    public static const CONTIGUOUS_TREE_SELECTION:int = 2;

    /** Selection can contain any number of items that are not necessarily
     * contiguous. */
    public static const DISCONTIGUOUS_TREE_SELECTION:int = 4;
	
    /** Property name for selectionMode. */
    public static const SELECTION_MODE_PROPERTY:String = "selectionMode";
    
    /**
     * onPropertyChanged(source:DefaultTreeSelectionModel, name:String, oldValue, newValue); 
	 */
    public static const ON_PROPERTY_CHANGED:String = "onPropertyChanged";  
    
    /**
     * onSelectionChanged(source:DefaultTreeSelectionModel, e:TreeSelectionEvent); 
     */
    public static const ON_SELECTION_CHANGED:String = "onSelectionChanged";  

    /** Paths that are currently selected.  Will be null if nothing is
      * currently selected. */
    private var selection:Array; //TreePath[]

    /** Provides a row for a given path. */
    private var rowMapper:RowMapper;

    /** Handles maintaining the list selection model. The RowMapper is used
     * to map from a TreePath to a row, and the value is then placed here. */
    private var listSelectionModel:DefaultListSelectionModel;

    /** Mode for the selection, will be either SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
     */
    private var selectionMode:int;

    /** Last path that was added. */
    private var leadPath:TreePath;
    /** Index of the lead path in selection. */
    private var leadIndex:int;
    /** Lead row. */
    private var leadRow:int;

    /** Used to make sure the paths are unique, will contain all the paths
     * in <code>selection</code>.
     */
    private var uniquePaths:TreePathMap;
    private var lastPaths:TreePathMap;
    private var tempPaths:Array; //TreePath[]


    /**
     * Creates a new instance of DefaultTreeSelectionModel that is
     * empty, with a selection mode of DISCONTIGUOUS_TREE_SELECTION.
     */
    public function DefaultTreeSelectionModel() {
		listSelectionModel = new DefaultListSelectionModel();
		selectionMode = DISCONTIGUOUS_TREE_SELECTION;
		leadIndex = leadRow = -1;
		uniquePaths = new TreePathMap();
		lastPaths = new TreePathMap();
		tempPaths = new Array(1);
    }

    /**
     * Sets the RowMapper instance. This instance is used to determine
     * the row for a particular TreePath.
     */
    public function setRowMapper(newMapper:RowMapper):void {
		rowMapper = newMapper;
		resetRowSelection();
    }

    /**
     * Returns the RowMapper instance that is able to map a TreePath to a
     * row.
     */
    public function getRowMapper():RowMapper {
		return rowMapper;
    }

    /**
     * Sets the selection model, which must be one of SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION. If mode
     * is not one of the defined value,
     * <code>DISCONTIGUOUS_TREE_SELECTION</code> is assumed.
     * <p>This may change the selection if the current selection is not valid
     * for the new mode. For example, if three TreePaths are 
     * selected when the mode is changed to <code>SINGLE_TREE_SELECTION</code>,
     * only one TreePath will remain selected. It is up to the particular
     * implementation to decide what TreePath remains selected.
     * <p>
     * Setting the mode to something other than the defined types will
     * result in the mode becoming <code>DISCONTIGUOUS_TREE_SELECTION</code>.
     */
    public function setSelectionMode(mode:int):void {
		var oldMode:int = selectionMode;
	
		selectionMode = mode;
		if(selectionMode != SINGLE_TREE_SELECTION &&
		   selectionMode != CONTIGUOUS_TREE_SELECTION &&
		   selectionMode != DISCONTIGUOUS_TREE_SELECTION){
		    selectionMode = DISCONTIGUOUS_TREE_SELECTION;
		}
		if(oldMode != selectionMode){
		    firePropertyChange(SELECTION_MODE_PROPERTY, oldMode, selectionMode);
		}
    }
    
    protected function firePropertyChange(name:String, oldValue:*, newValue:*):void{
    	dispatchEvent(new PropertyChangeEvent(name, oldValue, newValue));
    }

    /**
     * Returns the selection mode, one of <code>SINGLE_TREE_SELECTION</code>,
     * <code>DISCONTIGUOUS_TREE_SELECTION</code> or 
     * <code>CONTIGUOUS_TREE_SELECTION</code>.
     */
    public function getSelectionMode():int {
		return selectionMode;
    }

    /**
      * Sets the selection to path. If this represents a change, then
      * the TreeSelectionListeners are notified. If <code>path</code> is
      * null, this has the same effect as invoking <code>clearSelection</code>.
      *
      * @param path new path to select.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function setSelectionPath(path:TreePath, programmatic:Boolean=true):void {
		if(path == null){
		    setSelectionPaths(null, programmatic);
		}else {
		    setSelectionPaths([path], programmatic);
		}
    }

    /**
      * Sets the selection to the paths in paths.  If this represents a
      * change the TreeSelectionListeners are notified.  Potentially
      * paths will be held by this object; in other words don't change
      * any of the objects in the array once passed in.
      * <p>If <code>paths</code> is
      * null, this has the same effect as invoking <code>clearSelection</code>.
      * <p>The lead path is set to the last path in <code>pPaths</code>.
      * <p>If the selection mode is <code>CONTIGUOUS_TREE_SELECTION</code>,
      * and adding the new paths would make the selection discontiguous,
      * the selection is reset to the first TreePath in <code>paths</code>.
      *
      * @param pPaths new selection.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function setSelectionPaths(pPaths:Array, programmatic:Boolean=true):void {
		var newCount:int, newCounter:int, oldCount:int, oldCounter:int;
		var paths:Array = pPaths;
	
		if(paths == null)
		    newCount = 0;
		else
		    newCount = paths.length;
		if(selection == null)
		    oldCount = 0;
		else
		    oldCount = selection.length;
		if((newCount + oldCount) != 0) {
		    if(selectionMode == SINGLE_TREE_SELECTION) {
				/* If single selection and more than one path, only allow
				   first. */
				if(newCount > 1) {
				    paths = [pPaths[0]];
				    newCount = 1;
				}
		    }else if(selectionMode == CONTIGUOUS_TREE_SELECTION) {
				/* If contiguous selection and paths aren't contiguous,
				   only select the first path item. */
				if(newCount > 0 && !arePathsContiguous(paths)) {
				    paths = [pPaths[0]];
				    newCount = 1;
				}
		    }
	
		    var validCount:int = 0;
		    var beginLeadPath:TreePath = leadPath;
		    var cPaths:ArrayList = new ArrayList();
		    var path:TreePath;
	
		    lastPaths.clear();
		    leadPath = null;
		    /* Find the paths that are new. */
		    for(newCounter = 0; newCounter < newCount; newCounter++) {
		    	path = paths[newCounter]; 
				if(path != null && lastPaths.get(path) == null) {
				    validCount++;
				    lastPaths.put(path, true);
				    if (uniquePaths.get(path) == null) {
						cPaths.append(new PathPlaceHolder(path, true));
				    }
				    leadPath = path;
				}
		    }
	
		    /* If the validCount isn't equal to newCount it means there
		       are some null in paths, remove them and set selection to
		       the new path. */
		    var newSelection:Array;
	
		    if(validCount == 0) {
				newSelection = null;
		    } else if (validCount != newCount) {
				var keys:Array = lastPaths.keys();
		
				newSelection = new Array(validCount);
				validCount = 0;
				for(var i:int=0; i<keys.length; i++){
					newSelection[validCount++] = keys[i];
				}
		    }else {
				newSelection = paths.concat();
		    }
	
		    /* Get the paths that were selected but no longer selected. */
		    for(oldCounter = 0; oldCounter < oldCount; oldCounter++){
		    	path = selection[oldCounter];
				if(path != null &&  lastPaths.get(path) == null){
			    	cPaths.append(new PathPlaceHolder(path, false));
				}
		    }
		    selection = newSelection;
	
		    var tempHT:TreePathMap = uniquePaths;
	
		    uniquePaths = lastPaths;
		    lastPaths = tempHT;
		    lastPaths.clear();
	
		    // No reason to do this now, but will still call it.
		    if(selection != null){
				insureUniqueness();
		    }
	
		    updateLeadIndex();
	
		    resetRowSelection();
		    /* Notify of the change. */
		    if(cPaths.size() > 0){
				notifyPathChange(cPaths, beginLeadPath, programmatic);
		    }
		}
    }

    /**
      * Adds path to the current selection. If path is not currently
      * in the selection the TreeSelectionListeners are notified. This has
      * no effect if <code>path</code> is null.
      *
      * @param path the new path to add to the current selection.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function addSelectionPath(path:TreePath, programmatic:Boolean=true):void {
		if(path != null) {
		    addSelectionPaths([path], programmatic);
		}
    }

    /**
      * Adds paths(TreePath[]) to the current selection. If any of the paths in
      * paths are not currently in the selection the TreeSelectionListeners
      * are notified. This has
      * no effect if <code>paths</code> is null.
      * <p>The lead path is set to the last element in <code>paths</code>.
      * <p>If the selection mode is <code>CONTIGUOUS_TREE_SELECTION</code>,
      * and adding the new paths would make the selection discontiguous.
      * Then two things can result: if the TreePaths in <code>paths</code>
      * are contiguous, then the selection becomes these TreePaths,
      * otherwise the TreePaths aren't contiguous and the selection becomes
      * the first TreePath in <code>paths</code>.
      *
      * @param paths the new path to add to the current selection.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function addSelectionPaths(paths:Array, programmatic:Boolean=true):void {
		var newPathLength:int = ((paths == null) ? 0 : paths.length);
		if(newPathLength <= 0){
			return;
		}
	    if(selectionMode == SINGLE_TREE_SELECTION) {
			setSelectionPaths(paths);
	    }else if(selectionMode == CONTIGUOUS_TREE_SELECTION && !canPathsBeAdded(paths)) {
			if(arePathsContiguous(paths)) {
			    setSelectionPaths(paths);
			}else {
			    setSelectionPaths([paths[0]]);
			}
	    } else {
			var counter:int, validCount:int, oldCount:int;
			var beginLeadPath:TreePath = leadPath;
			var cPaths:ArrayList = null;
	
			if(selection == null)
			    oldCount = 0;
			else
			    oldCount = selection.length;
			/* Determine the paths that aren't currently in the
			   selection. */
			lastPaths.clear();
			counter = 0;
			validCount = 0;
			for(; counter < newPathLength; counter++) {
				var path:TreePath = paths[counter];
			    if(path != null) {
					if (uniquePaths.get(path) == null) {
					    validCount++;
					    if(cPaths == null){
							cPaths = new ArrayList();
					    }
					    cPaths.append(new PathPlaceHolder(path, true));
					    uniquePaths.put(path, true);
					    lastPaths.put(path, true);
					}
					leadPath = path;
			    }
			}
	
			if(leadPath == null) {
			    leadPath = beginLeadPath;
			}
	
			if(validCount > 0) {
			    var newSelection:Array = new Array();
	
			    /* And build the new selection. */
			    if(oldCount > 0){
					newSelection = selection.concat();
			    }
			    if(validCount != paths.length) {
					/* Some of the paths in paths are already in
					   the selection. */
					var newPaths:Array = lastPaths.keys();
					newSelection = newSelection.concat(newPaths);
			    } else {
			    	newSelection = newSelection.concat(paths);
			    }
	
			    selection = newSelection;
	
			    insureUniqueness();
	
			    updateLeadIndex();
	
			    resetRowSelection();
	
			    notifyPathChange(cPaths, beginLeadPath, programmatic);
			}else{
			    leadPath = beginLeadPath;
			}
			lastPaths.clear();
	    }
    }

    /**
      * Removes path from the selection. If path is in the selection
      * The TreeSelectionListeners are notified. This has no effect if
      * <code>path</code> is null.
      *
      * @param path the path to remove from the selection.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function removeSelectionPath(path:TreePath, programmatic:Boolean=true):void {
		if(path != null) {
		    removeSelectionPaths([path], programmatic);
		}
    }

    /**
      * Removes paths from the selection.  If any of the paths in paths
      * are in the selection the TreeSelectionListeners are notified.
      * This has no effect if <code>paths</code> is null.
      *
      * @param paths the paths to remove from the selection.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function removeSelectionPaths(paths:Array, programmatic:Boolean=true):void {
		if (paths != null && selection != null && paths.length > 0) {
		    if(!canPathsBeRemoved(paths)) {
				/* Could probably do something more interesting here! 
				 * Yes, maybe:) */
				clearSelection();
		    }else{
				var pathsToRemove:ArrayList = null;
		
				/* Find the paths that can be removed. */
				for (var removeCounter:int = paths.length - 1; removeCounter >= 0; removeCounter--) {
					var path:TreePath = paths[removeCounter];
				    if(path != null) {
						if (uniquePaths.get(path) != null) {
						    if(pathsToRemove == null){
								pathsToRemove = new ArrayList();
						    }
						    uniquePaths.remove(path);
						    pathsToRemove.append(new PathPlaceHolder(path, false));
						}
				    }
				}
				if(pathsToRemove != null) {
				    var removeCount:int = pathsToRemove.size();
				    var beginLeadPath:TreePath = leadPath;
		
				    if(removeCount == selection.length) {
						selection = null;
				    }else {
						var pEnum:Array = uniquePaths.keys();
						var validCount:int = 0;
			
						selection = new Array(selection.length - removeCount);
						for(var i:int=0; i<pEnum.length; i++){
							selection[validCount++] = pEnum[i];
						}
				    }
				    if (leadPath != null && uniquePaths.get(leadPath) == null) {
						if (selection != null) {
						    leadPath = selection[selection.length - 1];
						}else {
						    leadPath = null;
						}
				    }else if (selection != null) {
						leadPath = selection[selection.length - 1];
				    }else {
						leadPath = null;
				    }
				    updateLeadIndex();
		
				    resetRowSelection();
		
				    notifyPathChange(pathsToRemove, beginLeadPath, programmatic);
				}
		    }
		}
    }

    /**
      * Returns the first path in the selection. This is useful if there
      * if only one item currently selected.
      */
    public function getSelectionPath():TreePath {
		if(selection != null){
		    return selection[0];
		}else{
			return null;
		}
    }

    /**
      * Returns the paths(TreePath[]) in the selection. This will return null (or an
      * empty array) if nothing is currently selected.
      */
    public function getSelectionPaths():Array {
		if(selection != null) {
		    return selection.concat();
		}
		return null;
    }

    /**
     * Returns the number of paths that are selected.
     */
    public function getSelectionCount():int {
		return (selection == null) ? 0 : selection.length;
    }

    /**
      * Returns true if the path, <code>path</code>,
      * is in the current selection.
      */
    public function isPathSelected(path:TreePath):Boolean {
		return (path != null) ? (uniquePaths.get(path) != null) : false;
    }

    /**
      * Returns true if the selection is currently empty.
      */
    public function isSelectionEmpty():Boolean {
		return (selection == null);
    }

    /**
      * Empties the current selection.  If this represents a change in the
      * current selection, the selection listeners are notified.
      * @param programmatic indicate if this is a programmatic change.
      */
    public function clearSelection(programmatic:Boolean=true):void {
		if(selection != null) {
		    var selSize:int = selection.length;
		    var newness:Array = new Array(selSize); //boolean[]
	
		    for(var counter:int = 0; counter < selSize; counter++){
				newness[counter] = false;
		    }
	
		    var	event:TreeSelectionEvent = new TreeSelectionEvent(this, programmatic, selection, newness, leadPath, null);
	
		    leadPath = null;
		    leadIndex = leadRow = -1;
		    uniquePaths.clear();
		    selection = null;
		    resetRowSelection();
		    fireValueChanged(event);
		}
    }

    /**
     * Notifies all listeners that are registered for
     * tree selection events on this object.  
     * @see #addTreeSelectionListener
     * @see EventListenerList
     */
    protected function fireValueChanged(e:TreeSelectionEvent):void {
    	dispatchEvent(e);
    }

    /**
      * Returns all of the currently selected rows(int[]). This will return
      * null (or an empty array) if there are no selected TreePaths or
      * a RowMapper has not been set.
      * This may return an array of length less that than of the selected
      * TreePaths if some of the rows are not visible (that is the
      * RowMapper returned -1 for the row corresponding to the TreePath).
      */
    public function getSelectionRows():Array {
		// This is currently rather expensive.  Needs
		// to be better support from ListSelectionModel to speed this up.
		if(rowMapper != null && selection != null) {
			var counter:int;
		    var rows:Array = rowMapper.getRowsForPaths(selection);
	
		    if (rows != null) {
				var invisCount:int = 0;
		
				for (counter = rows.length - 1; counter >= 0; counter--) {
				    if (rows[counter] == -1) {
						invisCount++;
				    }
				}
				if (invisCount > 0) {
				    if (invisCount == rows.length) {
						rows = null;
				    }else {
						var tempRows:Array = new Array(rows.length - invisCount);
						counter = rows.length - 1;
						var visCounter:int = 0;
						for (; counter >= 0; counter--) {
						    if (rows[counter] != -1) {
								tempRows[visCounter++] = rows[counter];
						    }
						}
						rows = tempRows;
				    }
				}
		    }
		    return rows;
		}
		return null;
    }

    /**
     * Returns the smallest value obtained from the RowMapper for the
     * current set of selected TreePaths. If nothing is selected,
     * or there is no RowMapper, this will return -1.
      */
    public function getMinSelectionRow():int {
		return listSelectionModel.getMinSelectionIndex();
    }

    /**
     * Returns the largest value obtained from the RowMapper for the
     * current set of selected TreePaths. If nothing is selected,
     * or there is no RowMapper, this will return -1.
      */
    public function getMaxSelectionRow():int {
		return listSelectionModel.getMaxSelectionIndex();
    }

    /**
      * Returns true if the row identified by <code>row</code> is selected.
      */
    public function isRowSelected(row:int):Boolean {
		return listSelectionModel.isSelectedIndex(row);
    }

    /**
     * Updates this object's mapping from TreePath to rows. This should
     * be invoked when the mapping from TreePaths to integers has changed
     * (for example, a node has been expanded).
     * <p>You do not normally have to call this, JTree and its associated
     * Listeners will invoke this for you. If you are implementing your own
     * View class, then you will have to invoke this.
     * <p>This will invoke <code>insureRowContinuity</code> to make sure
     * the currently selected TreePaths are still valid based on the
     * selection mode.
     */
    public function resetRowSelection():void {
		listSelectionModel.clearSelection();
		if(selection != null && rowMapper != null) {
		    var aRow:int;
		    var rows:Array = rowMapper.getRowsForPaths(selection);
			var counter:int = 0;
			var maxCounter:int = selection.length;
		    for(; counter < maxCounter; counter++) {
				aRow = rows[counter];
				if(aRow != -1) {
				    listSelectionModel.addSelectionInterval(aRow, aRow);
				}
		    }
		    if(leadIndex != -1 && rows != null) {
				leadRow = rows[leadIndex];
		    }else if (leadPath != null) {
				// Lead selection path doesn't have to be in the selection.
				tempPaths[0] = leadPath;
				rows = rowMapper.getRowsForPaths(tempPaths);
				leadRow = (rows != null) ? rows[0] : -1;
		    }else {
				leadRow = -1;
		    }
		    insureRowContinuity();
		}else{
		    leadRow = -1;
		}
    }

    /**
     * Returns the lead selection index. That is the last index that was
     * added.
     */
    public function getLeadSelectionRow():int {
		return leadRow;
    }

    /**
     * Returns the last path that was added. This may differ from the 
     * leadSelectionPath property maintained by the JTree.
     */
    public function getLeadSelectionPath():TreePath {
		return leadPath;
    }
    
    
    /**
     * Adds a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired when the selection mode
     * changes.
     *
     * @param listener the propertyChangeListener to be added
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
     * @see org.aswing.JTree#ON_PROPERTY_CHANGED
     */
    public function addPropertyChangeListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
    	addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, listener, false, priority, useWeakReference);
    }
	
	/**
	 * Removed a propertyChangeListener.
	 * @param listener the listener to be removed.
	 */
	public function removePropertyChangeListener(listener:Function):void{
		removeEventListener(PropertyChangeEvent.PROPERTY_CHANGE, listener);
	}
	
    /**
      * Adds x to the list of listeners that are notified each time the
      * set of selected TreePaths changes.
      *
      * @param listener the new listener to be added
	  * @param priority the priority
	  * @param useWeakReference Determines whether the reference to the listener is strong or weak.
      * @see org.aswing.JTree#ON_SELECTION_CHANGED
      */
    public function addTreeSelectionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
    	addEventListener(TreeSelectionEvent.TREE_SELECTION_CHANGED, listener, 
    		false, priority, useWeakReference);
    }
    
	/**
	 * Removed a treeSelectionListener.
	 * @param listener the listener to be removed.
	 */    
    public function removeTreeSelectionListener(listener:Function):void{
    	removeEventListener(TreeSelectionEvent.TREE_SELECTION_CHANGED, listener);
    }

    /**
     * Makes sure the currently selected <code>TreePath</code>s are valid
     * for the current selection mode.
     * If the selection mode is <code>CONTIGUOUS_TREE_SELECTION</code>
     * and a <code>RowMapper</code> exists, this will make sure all
     * the rows are contiguous, that is, when sorted all the rows are
     * in order with no gaps.
     * If the selection isn't contiguous, the selection is
     * reset to contain the first set, when sorted, of contiguous rows.
     * <p>
     * If the selection mode is <code>SINGLE_TREE_SELECTION</code> and
     * more than one TreePath is selected, the selection is reset to
     * contain the first path currently selected.
     */
    private function insureRowContinuity():void {
		if(selectionMode == CONTIGUOUS_TREE_SELECTION && selection != null && rowMapper != null) {
		    var lModel:DefaultListSelectionModel = listSelectionModel;
		    var min:int = lModel.getMinSelectionIndex();
	
		    if(min != -1) {
		    	var counter:int = min;
		    	var maxCounter:int = lModel.getMaxSelectionIndex();
				for(; counter <= maxCounter; counter++) {
				    if(!lModel.isSelectedIndex(counter)) {
						if(counter == min) {
						    clearSelection();
						}else {
						    var newSel:Array = new Array(counter - min);
						    var selectionIndex:Array = rowMapper.getRowsForPaths(selection);
						    // find the actual selection pathes corresponded to the
						    // rows of the new selection
						    for (var i:int = 0; i < selectionIndex.length; i++) {
								if (selectionIndex[i]<counter) {
								    newSel[selectionIndex[i]-min] = selection[i];
								}
						    }
						    setSelectionPaths(newSel);
						    break;
						}
				    }
				}
		    }
		}else if(selectionMode == SINGLE_TREE_SELECTION &&
			selection != null && selection.length > 1) {
		    setSelectionPath(selection[0]);
		}
    }

    /**
     * Returns true if the paths() are contiguous,
     * or this object has no RowMapper.
     */
    private function arePathsContiguous(paths:Array):Boolean {
		if(rowMapper == null || paths.length < 2){
		    return true;
		}else {
		    var bitSet:Array = new Array(32);
		    var anIndex:int, counter:int, min:int;
		    var pathCount:int = paths.length;
		    var validCount:int = 0;
		    var tempPath:Array = [paths[0]];
		    
		    min = rowMapper.getRowsForPaths(tempPath)[0];
		    for(counter = 0; counter < pathCount; counter++) {
				if(paths[counter] != null) {
				    tempPath[0] = paths[counter];
				    var rows:Array = rowMapper.getRowsForPaths(tempPath);
				    if (rows == null) {
						return false;
				    }
				    anIndex = rows[0];
				    if(anIndex == -1 || anIndex < (min - pathCount) || anIndex > (min + pathCount)){
						return false;
				    }
				    if(anIndex < min){
						min = anIndex;
				    }
				    if(!(bitSet[anIndex] == true)) {
						bitSet[anIndex] = true;
						validCount++;
				    }
				}
		    }
		    var maxCounter:int = validCount + min;
	
		    for(counter = min; counter < maxCounter; counter++){
				if(!(bitSet[counter] == true)){
				    return false;
				}
		    }
		}
		return true;
    }

    /**
     * Used to test if a particular set of <code>TreePath</code>s can
     * be added. This will return true if <code>paths</code> is null (or
     * empty), or this object has no RowMapper, or nothing is currently selected,
     * or the selection mode is <code>DISCONTIGUOUS_TREE_SELECTION</code>, or
     * adding the paths to the current selection still results in a
     * contiguous set of <code>TreePath</code>s.
     */
    private function canPathsBeAdded(paths:Array):Boolean {
		if(paths == null || paths.length == 0 || rowMapper == null ||
		   selection == null || selectionMode == DISCONTIGUOUS_TREE_SELECTION){
		    return true;
		}else {
		    var bitSet:Array = new Array(32);
		    var lModel:DefaultListSelectionModel = listSelectionModel;
		    var anIndex:int;
		    var counter:int;
		    var min:int = lModel.getMinSelectionIndex();
		    var max:int = lModel.getMaxSelectionIndex();
		    var tempPath:Array = new Array(1);
	
		    if(min != -1) {
				for(counter = min; counter <= max; counter++) {
				    if(lModel.isSelectedIndex(counter)){
						bitSet[counter] = true;
				    }
				}
		    }else {
				tempPath[0] = paths[0];
				min = max = rowMapper.getRowsForPaths(tempPath)[0];
		    }
		    for(counter = paths.length - 1; counter >= 0; counter--) {
				if(paths[counter] != null) {
				    tempPath[0] = paths[counter];
				    var rows:Array = rowMapper.getRowsForPaths(tempPath);
				    if (rows == null) {
						return false;
				    }
				    anIndex = rows[0];
				    min = Math.min(anIndex, min);
				    max = Math.max(anIndex, max);
				    if(anIndex == -1){
						return false;
				    }
				    bitSet[anIndex] = true;
				}
		    }
		    for(counter = min; counter <= max; counter++){
				if(!(bitSet[counter] == true)){
				    return false;
				}
		    }
		}
		return true;
    }

    /**
     * Returns true if the paths can be removed without breaking the
     * continuity of the model.
     * This is rather expensive.
     */
    private function canPathsBeRemoved(paths:Array):Boolean {
		if(rowMapper == null || selection == null || selectionMode == DISCONTIGUOUS_TREE_SELECTION){
		    return true;
		}else {
		    var bitSet:Array = new Array();
		    var counter:int;
		    var pathCount:int = paths.length;
		    var min:int = -1;
		    var validCount:int = 0;
		    var tempPath:Array = new Array(1);
		    var rows:Array;
	
		    /* Determine the rows for the removed entries. */
		    lastPaths.clear();
		    for (counter = 0; counter < pathCount; counter++) {
				if (paths[counter] != null) {
				    lastPaths.put(paths[counter], true);
				}
		    }
		    for(counter = selection.length - 1; counter >= 0; counter--) {
				if(lastPaths.get(selection[counter]) == null) {
				    tempPath[0] = selection[counter];
				    rows = rowMapper.getRowsForPaths(tempPath);
				    if(rows != null && rows[0] != -1 && !(bitSet[rows[0]] == true)) {
						validCount++;
						if(min == -1)
						    min = rows[0];
						else
						    min = Math.min(min, rows[0]);
						bitSet[rows[0]] = true;
				    }
				}
		    }
		    lastPaths.clear();
		    /* Make sure they are contiguous. */
		    if(validCount > 1) {
				for(counter = min + validCount - 1; counter >= min; counter--){
				    if(!(bitSet[counter] == true)){
						return false;
				    }
				}
		    }
		}
		return true;
    }

    /**
      * Notifies listeners of a change in path. changePaths should contain
      * instances of PathPlaceHolder.
      */
    private function notifyPathChange(changedPaths:ArrayList, oldLeadSelection:TreePath, programmatic:Boolean):void {
		var cPathCount:int = changedPaths.size();
		var newness:Array = new Array(cPathCount); //boolean[]
		var paths:Array = new Array(cPathCount); //TreePath[]
		var placeholder:PathPlaceHolder;
		
		for(var counter:int = 0; counter < cPathCount; counter++) {
		    placeholder = PathPlaceHolder(changedPaths.get(counter));
		    newness[counter] = placeholder.isNew;
		    paths[counter] = placeholder.path;
		}
		
		var event:TreeSelectionEvent = new TreeSelectionEvent(this, programmatic, paths, newness, oldLeadSelection, leadPath);
		
		fireValueChanged(event);
    }

    /**
     * Updates the leadIndex instance variable.
     */
    private function updateLeadIndex():void {
		if(leadPath != null) {
		    if(selection == null) {
				leadPath = null;
				leadIndex = leadRow = -1;
		    } else {
				leadRow = leadIndex = -1;
				for(var counter:int = selection.length - 1; counter >= 0; counter--) {
				    // Can use == here since we know leadPath came from
				    // selection
				    if(selection[counter] == leadPath) {
						leadIndex = counter;
						break;
				    }
				}
		    }
		}else {
		    leadIndex = -1;
		}
    }

    /**
     * This method is obsolete and its implementation is now a noop.  It's
     * still called by setSelectionPaths and addSelectionPaths, but only
     * for backwards compatability.
     */
    private function insureUniqueness():void {
    }


    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    override public function toString():String {
		return "DefaultTreeSelectionModel[" + getSelectionPaths() + "]";
    }
}
}