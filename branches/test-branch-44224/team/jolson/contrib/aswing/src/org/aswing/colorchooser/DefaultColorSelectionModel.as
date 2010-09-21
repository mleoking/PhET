/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.colorchooser.ColorSelectionModel;
import org.aswing.EventDispatcher;

/**
 * A generic implementation of <code>ColorSelectionModel</code>.
 * @author iiley
 */
class org.aswing.colorchooser.DefaultColorSelectionModel extends EventDispatcher implements ColorSelectionModel{
		
	private var selectedColor:ASColor;
	
	/**
	 * DefaultColorSelectionModel(selectedColor:ASColor)<br>
	 * DefaultColorSelectionModel() default to ASColor.WHITE
	 * <p>
     * Creates a <code>DefaultColorSelectionModel</code> with the
     * current color set to <code>color</code>.  Note that setting the color to
     * <code>null</code> means default to WHITE.
	 */
	public function DefaultColorSelectionModel(selectedColor:ASColor) {
		super();
		if(selectedColor == undefined) selectedColor = ASColor.WHITE;
		this.selectedColor = selectedColor;
	}

	public function getSelectedColor() : ASColor {
		return selectedColor;
	}

	public function setSelectedColor(color : ASColor) : Void {
		if((selectedColor == null && color != null)
			 || (color == null && selectedColor != null)
			 || (!color.equals(selectedColor))){
			selectedColor = color;
			fireStateChanged();
		}else{
			selectedColor = color;
		}
	}

	public function addChangeListener(func : Function, contexObj : Object) : Object {
		return addEventListener(AbstractColorChooserPanel.ON_STATE_CHANGED, func, contexObj);
	}
	
    public function addColorAdjustingListener(func:Function, contexObj:Object):Object{
    	return this.addEventListener(AbstractColorChooserPanel.ON_COLOR_ADJUSTING, func, contexObj);
    }
	
	private function fireStateChanged():Void{
		dispatchEvent(createEventObj(AbstractColorChooserPanel.ON_STATE_CHANGED));
	}
	
	public function fireColorAdjusting(color : ASColor) : Void {
		dispatchEvent(createEventObj(AbstractColorChooserPanel.ON_COLOR_ADJUSTING, color));
	}

}