/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.IEventDispatcher;

/**
 * @author iiley
 * A model that supports selecting a <code>Color</code>.
 * 
 * @see org.aswing.ASColor
 */
interface org.aswing.colorchooser.ColorSelectionModel extends  IEventDispatcher{
	
    /**
     * Returns the selected <code>ASColor</code> which should be
     * non-<code>null</code>.
     *
     * @return  the selected <code>ASColor</code>
     * @see     #setSelectedColor()
     */
    public function getSelectedColor():ASColor;

    /**
     * Sets the selected color to <code>ASColor</code>.
     * Note that setting the color to <code>null</code>
     * is undefined and may have unpredictable results.
     * This method fires a state changed event if it sets the
     * current color to a new non-<code>null</code> color.
     *
     * @param color the new <code>ASColor</code>
     * @see   #getSelectedColor()
     * @see   #addChangeListener()
     */
    public function setSelectedColor(color:ASColor):Void;
    
    /**
     * Fires a event to indicate a color is adjusting, for example:
     * durring swatched rollover or mixer modification.
     * <p>
     * This should be called by AsWing core(generally called by UI classes), 
     * client app should not call this generally.
     * 
     * @param color the new adjusting <code>ASColor</code>
     * @see #addColorAdjustingListener()
     */
    public function fireColorAdjusting(color:ASColor):Void;

	/**
	 * addChangeListener(func:Function)<br>
	 * addChangeListener(func:Function, contexObj:Object)
	 * <p>
	 * Add a listener to listen the Model's change event.
	 * <p>
	 * When the selected color changed.
	 * 
	 * onStateChanged(source:ColorSelectionModel)
	 * @see org.aswing.Component#ON_STATE_CHANGED
	 * @return the listener added.
	 * @see org.aswing.IEventDispatcher#addEventListener()
	 */
    public function addChangeListener(func:Function, contexObj:Object):Object;
    
    /**
	 * addColorAdjustingListener(func:Function)<br>
	 * addColorAdjustingListener(func:Function, contexObj:Object)
	 * <p>
	 * Add a listener to listen the color adjusting event.
	 * <p>
	 * When user adjusting to a new color.
	 * 
	 * onColorAdjusting(source:ColorSelectionModel, color:ASColor)
	 * @see org.aswing.AbstractColorChooserPanel#ON_COLOR_ADJUSTING
	 * @return the listener added.
	 * @see org.aswing.IEventDispatcher#addEventListener()
     */
    public function addColorAdjustingListener(func:Function, contexObj:Object):Object;
}