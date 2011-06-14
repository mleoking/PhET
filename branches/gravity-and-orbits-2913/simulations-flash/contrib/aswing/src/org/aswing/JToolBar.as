/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASWingConstants;
import org.aswing.Container;
import org.aswing.Insets;
import org.aswing.plaf.ToolBarUI;
import org.aswing.SoftBoxLayout;
import org.aswing.UIManager;

/**
 * <code>JToolBar</code> provides a component that is useful for
 * displaying commonly used <code>Action</code>s or controls.
 *
 * <p>
 * JToolBar will change buttons's isOpaque() method, so if your programe's logic is related 
 * to button's opaque, take care to add buttons to JToolBar.
 *
 * <p>
 * With most look and feels,
 * the user can drag out a tool bar into a separate window
 * (unless the <code>floatable</code> property is set to <code>false</code>).
 * For drag-out to work correctly, it is recommended that you add
 * <code>JToolBar</code> instances to one of the four "sides" of a
 * container whose layout manager is a <code>BorderLayout</code>,
 * and do not add children to any of the other four "sides".
 * <p>
 * 
 * @author iiley
 */
class org.aswing.JToolBar extends Container {
	
	public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
	public static var VERTICAL:Number  = ASWingConstants.VERTICAL;
	
	private var margin:Insets;
    private var floatable:Boolean;
    private var orientation:Number;
    private var title:String;
    
    /**
     * JToolBar(title:String, orientation:Number)<br>
     * JToolBar(title:String)<br> default orientation to HORIZONTAL
     * JToolBar()<br>default title to null, orientation to HORIZONTAL
     * <p>
     * Creates a new tool bar with a specified <code>title</code> and
     * <code>orientation</code>.
     * title is only shown when the tool bar is undocked. 
     * @param title the title of the tool bar
     * @param orientation orientation  the initial orientation -- it must be
     *		either <code>HORIZONTAL</code> or <code>VERTICAL</code>
     */
	public function JToolBar(title:String, orientation:Number) {
		super();
		this.title = (title == undefined ? null : title);
		this.orientation = (orientation == undefined ? HORIZONTAL : orientation);
		setLayoutWidthOrientation();
		updateUI();
	}

    public function updateUI():Void{
    	setUI(ToolBarUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:ToolBarUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "ToolBarUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicToolBarUI;
    }
	
     /**
      * Sets the margin between the tool bar's border and
      * its buttons. Setting to <code>null</code> causes the tool bar to
      * use the default margins. The tool bar's default <code>Border</code>
      * object uses this value to create the proper margin.
      * However, if a non-default border is set on the tool bar,
      * it is that <code>Border</code> object's responsibility to create the
      * appropriate margin space (otherwise this property will
      * effectively be ignored).
      *
      * @param m an <code>Insets</code> object that defines the space
      * 	between the border and the buttons
      * @see Insets
      */	
	public function setMargin(m:Insets):Void{
		if(m != margin || !m.equals(margin)){
			margin = m;
			revalidate();
			repaint();
		}
	}
	
     /**
      * Returns the margin between the tool bar's border and
      * its buttons.
      *
      * @return an <code>Insets</code> object containing the margin values
      * @see Insets
      */	
	public function getMargin():Insets{
		if(margin == null){
			return new Insets(0, 0, 0, 0);
		}else{
			return (new Insets()).addInsets(margin);//return a copy
		}
	}
	
    /**
     * Gets the <code>floatable</code> property.
     * @return the value of the <code>floatable</code> property
     * @see #setFloatable()
     */	
	public function isFloatable():Boolean{
		return floatable;
	}
	
     /**
      * Sets the <code>floatable</code> property,
      * which must be <code>true</code> for the user to move the tool bar.
      * Typically, a floatable tool bar can be
      * dragged into a different position within the same container
      * or out into its own window.
      * The default value of this property is <code>true</code>.
      * Some look and feels might not implement floatable tool bars;
      * they will ignore this property.
      *
      * @param b if <code>true</code>, the tool bar can be moved;
      *          <code>false</code> otherwise
      * @see #isFloatable()
      */	
	public function setFloatable(b:Boolean):Void{
		if(b != floatable){
			floatable = b;
			revalidate();
			repaint();
		}
	}
	
    /**
     * Returns the current orientation of the tool bar.  The value is either
     * <code>HORIZONTAL</code> or <code>VERTICAL</code>.
     *
     * @return an integer representing the current orientation -- either
     *		<code>HORIZONTAL</code> or <code>VERTICAL</code>
     * @see #setOrientation()
     */
    public function getOrientation():Number{
        return orientation;
    }

    /**
     * Sets the orientation of the tool bar.  The orientation must have
     * either the value <code>HORIZONTAL</code> or <code>VERTICAL</code>.
     * If <code>orientation</code> is
     * an invalid value, default it to HORIZONTAL.
     *
     * @param o  the new orientation -- either <code>HORIZONTAL</code> or
     *			</code>VERTICAL</code>
     * @see #getOrientation()
     */
    public function setOrientation(o:Number):Void
    {
        if(o == undefined){
        	o = HORIZONTAL;
        }

		if (orientation != o){
		    orientation = o;
		    setLayoutWidthOrientation();
		    revalidate();
		    repaint();
	    }
    }
    
    private function setLayoutWidthOrientation():Void{
	    if(orientation == VERTICAL){
	    	setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
	    }else{
	    	setLayout(new SoftBoxLayout(SoftBoxLayout.X_AXIS));
	    }
    }
	
}
