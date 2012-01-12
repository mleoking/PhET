/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingConstants;
import org.aswing.BoundedRangeModel;
import org.aswing.Component;
import org.aswing.DefaultBoundedRangeModel;
import org.aswing.Icon;
import org.aswing.plaf.ProgressBarUI;
import org.aswing.UIManager;
import org.aswing.util.Timer;

/**
 * A component that, by default, displays an integer value within a bounded 
 * interval. A progress bar typically communicates the progress of some 
 * work by displaying its percentage of completion and possibly a textual
 * display of this percentage.
 * @author iiley
 */
class org.aswing.overflow.JProgressBar extends Component {
	/**
	 * When the progressBar's progress state changed.
	 *<br>
	 * onStateChanged(source:JProgressBar)
	 * @see org.aswing.BoundedRangeModel#addChangeListener()
	 */	
	public static var ON_STATE_CHANGED:String = Component.ON_STATE_CHANGED;	
		
    /** 
     * Horizontal orientation.
     */
    public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
	
	private var orientation:Number;
	private var indeterminate:Boolean;
	private var icon:Icon;
	private var string:String;
	private var model:BoundedRangeModel;
	private var indeterminatePaintTimer:Timer;
	private var modelListener:Object;
	
	/**
	 * JProgressBar(orient:Number, min:Number, max:Number)<br>
	 * JProgressBar(orient:Number)<br>
	 * JProgressBar()
	 * <p>
	 * @param orient (optional)the desired orientation of the progress bar, 
	 *  just can be <code>JProgressBar.HORIZONTAL</code> or <code>JProgressBar.VERTICAL</code>,
	 *  default is <code>JProgressBar.HORIZONTAL</code>
	 * @param min (optional)the minimum value of the progress bar, default is 0
	 * @param max (optional)the maximum value of the progress bar, default is 100
	 */
	public function JProgressBar(orient:Number, min:Number, max:Number) {
		super();
		setName("ProgressBar");
		
		if(orient == undefined) orient = HORIZONTAL;
		if(min == undefined) min = 0;
		if(max == undefined) max = 100;
		
		orientation = orient;
		model = new DefaultBoundedRangeModel(min, 0, min, max);
		addListenerToModel();
		
		indeterminate = false;
		string = null;
		icon = undefined; //Pluginable from UI
		
		indeterminatePaintTimer = new Timer(50);
		indeterminatePaintTimer.addActionListener(__indeterminateInterval, this);
		
		addEventListener(ON_CREATED, __validateIndeterminateIntervalIfNecessary, this);
		addEventListener(ON_DESTROY, __validateIndeterminateIntervalIfNecessary, this);
		
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(ProgressBarUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:ProgressBarUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "ProgressBarUI";
	} 
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicProgressBarUI;
    }
	    
	/**
     * Returns the data model used by this progress bar.
     *
     * @return the <code>BoundedRangeModel</code> currently in use
     * @see    org.aswing.BoundedRangeModel
     */
	public function getModel():BoundedRangeModel {
		return model;
	}
	
    /**
     * Sets the data model used by the <code>JProgressBar</code>.
     *
     * @param  newModel the <code>BoundedRangeModel</code> to use
     */
	public function setModel(newModel:BoundedRangeModel):Void {
		var oldModel:BoundedRangeModel = model;
		if (model != null){
			model.removeEventListener(modelListener);
		}
		model = newModel;
		if (model != null){
			model.setExtent(0);
			addListenerToModel();
		}
	}
	
	/**
	 * Returns the icon used by this progress bar.
	 * 
	 * @return the <code>Icon</code> currently in use
	 */
	public function getIcon():Icon {
		return icon;
	}
	
	/**
	 * Sets the icon used by the <code>JProgressBar</code>.
	 * Genarally the LookAndFeel UI for <code>JProgressBar</code> will set 
	 * a icon to paint the progress, if you want a custom icon, you can 
	 * set a new icon by this method, and your custom icon should paint the 
	 * progress by the model data or indeterminate property.
	 * 
	 * @param icon the <code>Icon</code> to paint the progress.
	 * @see #getModel()
	 * @see #isIndeterminate()
	 */
	public function setIcon(icon:Icon):Void {
		if(this.icon != icon){
			if(this.icon != null){
				uninstallIconWhenNextPaint(this.icon);
			}
			this.icon = icon;
			repaint();
		}
	}
	
    /**
     * Returns the current value of the progress string.
     * @return the value of the progress string
     * @see    #setString
     */
	public function getString():String {
		return string;
	}

    /**
     * Sets the value of the progress string. By default,
     * this string is <code>null</code>, will paint nothing text.
     * @param  s the value of the progress string
     * @see    #getString()
     */
	public function setString(s:String):Void {
		if(string != s){
			string = s;
			repaint();
		}
	}

    /**
     * Returns <code>JProgressBar.VERTICAL</code> or 
     * <code>JProgressBar.HORIZONTAL</code>, depending on the orientation
     * of the progress bar. The default orientation is 
     * <code>HORIZONTAL</code>.
     *
     * @return <code>HORIZONTAL</code> or <code>VERTICAL</code>
     * @see #setOrientation()
     */
	public function getOrientation():Number {
		return orientation;
	}
	
    /**
     * Sets the progress bar's orientation to <code>newOrientation</code>, 
     * which must be <code>JProgressBar.VERTICAL</code> or 
     * <code>JProgressBar.HORIZONTAL</code>. The default orientation 
     * is <code>HORIZONTAL</code>.
     * <p>
     * Note that If the orientation is set to <code>VERTICAL</code>,
     *  the progress string can only be displayable when the progress bar's font 
     *  is a embedFonts.
     * 
     * @param  newOrientation  <code>HORIZONTAL</code> or <code>VERTICAL</code>
     * @see #getOrientation()
     * @see org.aswing.ASFont#getEmbedFonts()
     */
	public function setOrientation(newOrientation:Number):Void {
		if(newOrientation != HORIZONTAL && newOrientation!= VERTICAL){
			newOrientation = HORIZONTAL;
		}
		if(orientation != newOrientation){
			orientation = newOrientation;
			revalidate();
			repaint();
		}
	}
	
    /**
     * Returns the percent complete for the progress bar.
     * Note that this number is between 0.0 and 1.0.
     *
     * @return the percent complete for this progress bar
     */
    public function getPercentComplete():Number {
		var span:Number = model.getMaximum() - model.getMinimum();
		var currentValue:Number = model.getValue();
		var pc:Number = (currentValue - model.getMinimum()) / span;
		return pc;
    }
    
    /**
     * Returns the progress bar's current value,
     * which is stored in the progress bar's <code>BoundedRangeModel</code>.
     * The value is always between the 
     * minimum and maximum values, inclusive. By default, the 
     * value is initialized to be equal to the minimum value.
     *
     * @return  the current value of the progress bar
     * @see     #setValue()
     * @see     org.aswing.BoundedRangeModel#getValue()
     */
	public function getValue():Number{
		return getModel().getValue();
	}
    /**
     * Returns the progress bar's minimum value,
     * which is stored in the progress bar's <code>BoundedRangeModel</code>.
     * By default, the minimum value is <code>0</code>.
     *
     * @return  the progress bar's minimum value
     * @see     #setMinimum()
     * @see     org.aswing.BoundedRangeModel#getMinimum()
     */	
	public function getMinimum():Number{
		return getModel().getMinimum();
	}
	/**
     * Returns the progress bar's maximum value,
     * which is stored in the progress bar's <code>BoundedRangeModel</code>.
     * By default, the maximum value is <code>100</code>.
     *
     * @return  the progress bar's maximum value
     * @see     #setMaximum()
     * @see     org.aswing.BoundedRangeModel#getMaximum()
     */
	public function getMaximum():Number{
		return getModel().getMaximum();
	}
    /**
     * Sets the progress bar's current value 
     * (stored in the progress bar's data model) to <code>n</code>.
     * The data model (a <code>BoundedRangeModel</code> instance)
     * handles any mathematical
     * issues arising from assigning faulty values.
     * <p>
     * If the new value is different from the previous value,
     * all change listeners are notified.
     *
     * @param   n       the new value
     * @see     #getValue()
     * @see    #addChangeListener()
     * @see     org.aswing.BoundedRangeModel#setValue()
     */	
	public function setValue(n:Number):Void{
		getModel().setValue(n);
	}
    /**
     * Sets the progress bar's minimum value 
     * (stored in the progress bar's data model) to <code>n</code>.
     * The data model (a <code>BoundedRangeModel</code> instance)
     * handles any mathematical
     * issues arising from assigning faulty values.
     * <p>
     * If the minimum value is different from the previous minimum,
     * all change listeners are notified.
     *
     * @param  n       the new minimum
     * @see    #getMinimum()
     * @see    #addChangeListener()
     * @see    org.aswing.BoundedRangeModel#setMinimum()
     */	
	public function setMinimum(n:Number):Void{
		getModel().setMinimum(n);
	}
    /**
     * Sets the progress bar's maximum value
     * (stored in the progress bar's data model) to <code>n</code>.
     * The underlying <code>BoundedRangeModel</code> handles any mathematical
     * issues arising from assigning faulty values.
     * <p>
     * If the maximum value is different from the previous maximum,
     * all change listeners are notified.
     *
     * @param  n       the new maximum
     * @see    #getMaximum()
     * @see    #addChangeListener()
     * @see    org.aswing.BoundedRangeModel#setMaximum()
     */	
	public function setMaximum(n:Number):Void{
		getModel().setMaximum(n);
	}
    /**
     * Sets the <code>indeterminate</code> property of the progress bar,
     * which determines whether the progress bar is in determinate
     * or indeterminate mode.
     * An indeterminate progress bar continuously displays animation
     * indicating that an operation of unknown length is occurring.
     * By default, this property is <code>false</code>.
     * <p>
     * An indeterminate progress bar will start a <code>Timer</code> to 
     * call repaint continuously when it is displayable, it make the progress can paint continuously.
     * Make sure the current <code>Icon</code> for this bar support indeterminate 
     * if you set indeterminate to true.
     * <p>
     * @param newValue	<code>true</code> if the progress bar
     * 			should change to indeterminate mode;
     * 			<code>false</code> if it should revert to normal.
     *
     * @see #isIndeterminate()
     */	
	public function setIndeterminate(newValue:Boolean):Void{
		indeterminate = newValue;
		__validateIndeterminateIntervalIfNecessary();
	}
    /**
     * Returns the value of the <code>indeterminate</code> property.
     *
     * @return the value of the <code>indeterminate</code> property
     * @see    #setIndeterminate()
     */	
	public function isIndeterminate():Boolean{
		return indeterminate;
	}
	
	/**
	 * @see #ON_STATE_CHANGED
	 */
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(ON_STATE_CHANGED, func, obj);
	}
	
	//------------------
	    
	private function addListenerToModel():Void{
		modelListener = model.addChangeListener(__onModelStateChanged, this);		
	}
	
	private function __onModelStateChanged():Void{
		fireStateChanged();
	}
	
	private function __indeterminateInterval():Void{
		repaint();
	}
	private function __validateIndeterminateIntervalIfNecessary():Void{
		if(isDisplayable() && isIndeterminate()){
			if(!indeterminatePaintTimer.isRunning()){
				indeterminatePaintTimer.start();
			}
		}else{
			if(indeterminatePaintTimer.isRunning()){
				indeterminatePaintTimer.stop();
			}
		}
	}
}
