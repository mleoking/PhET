/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 

import org.aswing.plaf.*;
import org.aswing.plaf.basic.BasicToolBarUI;

/**
 * <code>JToolBar</code> provides a component that is useful for
 * displaying commonly used <code>Action</code>s or controls.
 *
 * <p>
 * JToolBar will change buttons's isOpaque() method, so if your programe's logic is related 
 * to button's opaque, take care to add buttons to JToolBar.
 * </p>
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
public class JToolBar extends Container implements Orientable{
	
	public static const HORIZONTAL:int = AsWingConstants.HORIZONTAL;
	public static const VERTICAL:int  = AsWingConstants.VERTICAL;
	
	private var margin:Insets;
    private var gap:int;
    private var orientation:int;
    
    /**
     * Creates a new tool bar with specified <code>orientation</code>.
     * title is only shown when the tool bar is undocked. 
     * @param orientation orientation  the initial orientation -- it must be
     *		either <code>HORIZONTAL</code> or <code>VERTICAL</code>
     * @param gap the gap between toolbar children
     */
	public function JToolBar(orientation:int=AsWingConstants.HORIZONTAL, gap:int=2) {
		super();
		this.orientation = orientation;
		this.gap = gap;
		setLayoutWidthOrientation();
		updateUI();
	}

    override public function updateUI():void{
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicToolBarUI;
    }
    	
	override public function getUIClassID():String{
		return "ToolBarUI";
	}
	
	/**
	 * Sets the gap.
	 * @param gap the gap between toolbar children
	 */
	public function setGap(gap:int):void{
		if(this.gap != gap){
			this.gap = gap;
			revalidate();
		}
	}
	
	/**
	 * Returns the gap.
	 * @return gap the gap between toolbar children
	 */
	public function getGap():int{
		return gap;
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
	public function setMargin(m:Insets):void{
		if(margin != m){
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
			return new InsetsUIResource(0, 0, 0, 0);
		}else if(margin is UIResource){
			return (new InsetsUIResource()).addInsets(margin);//return a copy
		}else{
			return margin.clone();
		}
	}
	
	override public function getInsets():Insets{
		var insets:Insets = super.getInsets();
		insets.addInsets(getMargin());
		return insets;
	}
	
    /**
     * Returns the current orientation of the tool bar.  The value is either
     * <code>HORIZONTAL</code> or <code>VERTICAL</code>.
     *
     * @return an integer representing the current orientation -- either
     *		<code>HORIZONTAL</code> or <code>VERTICAL</code>
     * @see #setOrientation()
     */
    public function getOrientation():int{
        return orientation;
    }

    /**
     * Sets the orientation of the tool bar.  The orientation must have
     * either the value <code>HORIZONTAL</code> or <code>VERTICAL</code>.
     *
     * @param o  the new orientation -- either <code>HORIZONTAL</code> or
     *			</code>VERTICAL</code>
     * @see #getOrientation()
     */
    public function setOrientation(o:int):void{
		if (orientation != o){
		    orientation = o;
		    setLayoutWidthOrientation();
		    revalidate();
		    repaint();
	    }
    }
    
    private function setLayoutWidthOrientation():void{
	    if(orientation == VERTICAL){
	    	setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS, gap));
	    }else{
	    	setLayout(new SoftBoxLayout(SoftBoxLayout.X_AXIS, gap));
	    }
    }
	
}

}