import org.aswing.IEventDispatcher;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * A model that supports at most one indexed selection.
 * @author iiley
 */
interface org.aswing.SingleSelectionModel extends IEventDispatcher{
	
    /**
     * Returns the model's selection.
     *
     * @return  the model's selection, or -1 if there is no selection
     * @see     #setSelectedIndex()
     */
    public function getSelectedIndex():Number;

    /**
     * Sets the model's selected index to <I>index</I>.
     *
     * Notifies any listeners if the model changes
     *
     * @param index an int specifying the model selection
     * @see   #getSelectedIndex()
     * @see   #addChangeListener()
     */
    public function setSelectedIndex(index:Number):Void;

    /**
     * Clears the selection (to -1).
     */
    public function clearSelection():Void;

    /**
     * Returns true if the selection model currently has a selected value.
     * @return true if a value is currently selected
     */
    public function isSelected():Boolean;

    /**
     * addChangeListener(func:Function, obj:Object):Object<br>
     * addChangeListener(func:Function):Object<br>
     * Adds a listener to changes in the model.
     * @param listener the ChangeListener to add
     */
    public function addChangeListener(func:Function, obj:Object):Object;
}