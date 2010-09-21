/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.colorchooser.ColorSelectionModel;
import org.aswing.colorchooser.DefaultColorSelectionModel;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.colorchooser.AbstractColorChooserPanel extends Container {
	

	/**
	 * When the color selection changed.
	 *<br>
	 * onStateChanged(source:AbstractColorChooserPanel)
	 * @see org.aswing.Component#ON_STATE_CHANGED
	 * @see org.aswing.ColorSelectionModel#addChangeListener()
	 */	
	public static var ON_STATE_CHANGED:String = Component.ON_STATE_CHANGED;
	
	/**
	 * When user adjusting to a new color.
	 *<br>
	 * onColorAdjusting(source:AbstractColorChooserPanel, color:ASColor)
	 * @return the listener added.
	 * @see org.aswing.ColorSelectionModel#addColorAdjustingListener()
	 */	
	public static var ON_COLOR_ADJUSTING:String = "onColorAdjusting";
	
	
	private var alphaSectionVisible:Boolean;
	private var hexSectionVisible:Boolean;
	private var noColorSectionVisible:Boolean;
	private var model:ColorSelectionModel;
	private var modelListener:Object;
	
	public function AbstractColorChooserPanel() {
		super();
		alphaSectionVisible = true;
		hexSectionVisible   = true;
		noColorSectionVisible = false;
		modelListener = new Object();
		modelListener[ON_STATE_CHANGED] = Delegate.create(this, __modelValueChanged);
		modelListener[ON_COLOR_ADJUSTING] = Delegate.create(this, __colorAdjusting);
		setModel(new DefaultColorSelectionModel());
	}
	
	/**
	 * Added a listener to listen the color selection change event.
	 * <br>
	 * onStateChanged(source:AbstractColorChooserPanel)
	 * @see #ON_STATE_CHANGED
	 */
	public function addChangeListener(func:Function, contexObj:Object):Object{
		return addEventListener(ON_STATE_CHANGED, func, contexObj);
	}
	
	/**
	 * Added a listener to listen the color selection change event.
	 * <br>
	 * onColorAdjusting(source:AbstractColorChooserPanel, color:ASColor)
	 * @see #ON_COLOR_ADJUSTING
	 */	
	public function addColorAdjustingListener(func:Function, contexObj:Object):Object{
		return addEventListener(ON_COLOR_ADJUSTING, func, contexObj);
	}
	
	/**
	 * Sets the color selection model to this chooser panel.
	 * @param model the color selection model
	 */
	public function setModel(model:ColorSelectionModel):Void{
		if(model == null) return;
		if(this.model != model){
			this.model.removeEventListener(modelListener);
			this.model = model;
			model.addEventListener(modelListener);
			repaint();
		}
	}
	private function __modelValueChanged():Void{
		fireStateChanged();
	}
	private function __colorAdjusting(model:ColorSelectionModel, color:ASColor):Void{
		dispatchEvent(createEventObj(ON_COLOR_ADJUSTING, color));
	}
	
	/**
	 * Returns the color selection model of this chooser panel
	 * @return the color selection model of this chooser panel
	 */
	public function getModel():ColorSelectionModel{
		return model;
	}
	
	/**
	 * Sets the color selected, null indicate that no color is selected.
	 * @param c the color to be selected, null indicate no color to be selected.
	 */
	public function setSelectedColor(c:ASColor):Void{
		getModel().setSelectedColor(c);
	}
	
	/**
	 * Returns the color selected, null will be return if there is no color selected.
	 * @return the color selected, or null.
	 */
	public function getSelectedColor():ASColor{
		return getModel().getSelectedColor();
	}
	
	/**
	 * Sets whether showing the alpha editing section.
	 * <p>
	 * Default value is true.
	 * @param b true to show the alpha editing section, false no.
	 */
	public function setAlphaSectionVisible(b:Boolean):Void{
		if(alphaSectionVisible != b){
			alphaSectionVisible = b;
			repaint();
		}
	}
	
	/**
	 * Returns true if the alpha editing section is shown, otherwise false.
	 * @return true if the alpha editing section is shown, otherwise false.
	 */
	public function isAlphaSectionVisible():Boolean{
		return alphaSectionVisible;
	}
	
	/**
	 * Sets whether showing the hex editing section.
	 * <p>
	 * Default value is true.
	 * @param b true to show the hex editing section, false no.
	 */	
	public function setHexSectionVisible(b:Boolean):Void{
		if(hexSectionVisible != b){
			hexSectionVisible = b;
			repaint();
		}
	}
	
	/**
	 * Returns true if the hex editing section is shown, otherwise false.
	 * @return true if the hex editing section is shown, otherwise false.
	 */	
	public function isHexSectionVisible():Boolean{
		return hexSectionVisible;
	}	
	
	/**
	 * Sets whether showing the no color toggle button section. Depend on LAF, not 
	 * every LAFs will implement this functionity.
	 * <p>
	 * Default value is false.
	 * @param b true to show the no color toggle button section, false no.
	 */	
	public function setNoColorSectionVisible(b:Boolean):Void{
		if(noColorSectionVisible != b){
			noColorSectionVisible = b;
			repaint();
		}
	}
	
	/**
	 * Returns true if the  no color toggle button is shown, otherwise false.
	 * @return true if the  no color toggle button is shown, otherwise false.
	 */	
	public function isNoColorSectionVisible():Boolean{
		return noColorSectionVisible;
	}		
}