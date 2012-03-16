/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.IEventDispatcher;

/**
 * This interface represents the current state of the 
 * selection for any of the components that display a 
 * list of values with stable indices.  The selection is 
 * modeled as a set of intervals, each interval represents
 * a contiguous range of selected list elements.
 * The methods for modifying the set of selected intervals
 * all take a pair of indices, index0 and index1, that represent
 * a closed interval, i.e. the interval includes both index0 and
 * index1.
 * 
 * @author iiley
 * @see org.aswing.DefaultListSelectionModel
 */
interface org.aswing.ListSelectionModel extends IEventDispatcher {
	/** 
     * Change the selection to be between index0 and index1 inclusive.
     * If this represents a change to the current selection, then
     * notify each ListSelectionListener. Note that index0 doesn't have
     * to be less than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener()
     */	
	public function setSelectionInterval(index0:Number, index1:Number):Void;
	
    /** 
     * Change the selection to be the set union of the current selection
     * and the indices between index0 and index1 inclusive.  If this represents 
     * a change to the current selection, then notify each 
     * ListSelectionListener. Note that index0 doesn't have to be less
     * than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener()
     */	
	public function addSelectionInterval(index0:Number, index1:Number):Void;

    /** 
     * Change the selection to be the set difference of the current selection
     * and the indices between index0 and index1 inclusive.  If this represents 
     * a change to the current selection, then notify each 
     * ListSelectionListener.  Note that index0 doesn't have to be less
     * than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener()
     */	
	public function removeSelectionInterval(index0:Number, index1:Number):Void;

    /**
     * Returns the first selected index or -1 if the selection is empty.
     */	
	public function getMinSelectionIndex():Number;

    /**
     * Returns the last selected index or -1 if the selection is empty.
     */	
	public function getMaxSelectionIndex():Number;

    /** 
     * Returns true if the specified index is selected.
     */	
	public function isSelectedIndex(index:Number):Boolean;

    /**
     * Return the first index argument from the most recent call to 
     * setSelectionInterval(), addSelectionInterval() or removeSelectionInterval().
     * The most recent index0 is considered the "anchor" and the most recent
     * index1 is considered the "lead".  Some interfaces display these
     * indices specially, e.g. Windows95 displays the lead index with a 
     * dotted yellow outline.
     * 
     * @see #getLeadSelectionIndex()
     * @see #setSelectionInterval()
     * @see #addSelectionInterval()
     */	
	public function getAnchorSelectionIndex():Number;
 
    /**
     * Set the anchor selection index. 
     * 
     * @see #getAnchorSelectionIndex()
     */	
	public function setAnchorSelectionIndex(index:Number):Void;	

    /**
     * Return the second index argument from the most recent call to 
     * setSelectionInterval(), addSelectionInterval() or removeSelectionInterval().
     * 
     * @see #getAnchorSelectionIndex()
     * @see #setSelectionInterval()
     * @see #addSelectionInterval()
     */
	public function getLeadSelectionIndex():Number;

    /**
     * Set the lead selection index. 
     * 
     * @see #getLeadSelectionIndex()
     */
	public function setLeadSelectionIndex(index:Number):Void;

    /**
     * Change the selection to the empty set.  If this represents
     * a change to the current selection then notify each ListSelectionListener.
     * 
     * @see #addListSelectionListener()
     */
	public function clearSelection():Void;	

    /**
     * Returns true if no indices are selected.
     */
	public function isSelectionEmpty():Boolean;
 
    /** 
     * Insert length indices beginning before/after index.  This is typically 
     * called to sync the selection model with a corresponding change
     * in the data model.
     */
    public function insertIndexInterval(index:Number, length:Number, before:Boolean):Void;

    /** 
     * Remove the indices in the interval index0,index1 (inclusive) from
     * the selection model.  This is typically called to sync the selection
     * model width a corresponding change in the data model.
     */
    public function removeIndexInterval(index0:Number, index1:Number):Void;
 
    /**
     * Set the selection mode. The following selectionMode values are allowed:
     * <ul>
     * <li> <code>SINGLE_SELECTION</code> 
     *   Only one list index can be selected at a time.  In this
     *   mode the setSelectionInterval and addSelectionInterval 
     *   methods are equivalent, and only the second index
     *   argument (the "lead index") is used.
     * <li> <code>SINGLE_INTERVAL_SELECTION</code>
     *   One contiguous index interval can be selected at a time.
     *   In this mode setSelectionInterval and addSelectionInterval 
     *   are equivalent.
     * <li> <code>MULTIPLE_INTERVAL_SELECTION</code>
     *   In this mode, there's no restriction on what can be selected.
     * </ul>
     * 
     * @see #getSelectionMode()
     */
	public function setSelectionMode(selectionMode:Number):Void;

    /**
     * Returns the current selection mode.
     * @return The value of the selectionMode property.
     * @see #setSelectionMode()
     */
	public function getSelectionMode():Number;

    /**
     * Add a listener to the list that's notified each time a change
     * to the selection occurs.
     * @see org.aswing.overflow.JList#ON_SELECTION_CHANGED
     */  
	public function addListSelectionListener(func:Function, obj:Object):Object;
}
