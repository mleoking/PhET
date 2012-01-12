/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.geom.*;

/**
 * Layout for JWindow and JFrame.
 * @author iiley
 */
public class WindowLayout extends EmptyLayout{
	
	private var titleBar:Component;

	private var contentPane:Component;

    /**
     * The title bar layout constraint.
     */
    public static const TITLE:String  = "Title";

    /**
     * The content pane layout constraint.
     */
    public static const CONTENT:String  = "Content";
    
	public function WindowLayout() {
		super();
	}
	
	/**
	 * @param comp the child to add to the layout
	 * @param constraints the constraints indicate what position the child will be added.
	 * @throws ArgumentError when the constraints is not <code>TITLE</code> either <code>CONTENT</code>.
	 */
    override public function addLayoutComponent(comp:Component, constraints:Object):void {
	    if(constraints == TITLE){
	    	titleBar = comp;
	    }else if(constraints == CONTENT){
	    	contentPane = comp;
	    }else{
	    	throw new ArgumentError("ERROR When add component to JWindow/JFrame, constraints must be TITLE or CONTENT : " + constraints);
	    }
    }
    
    public function getTitleBar():Component{
    	return titleBar;
    }
    
    public function getContentPane():Component{
    	return contentPane;
    }
    
    override public function removeLayoutComponent(comp:Component):void {
     	if(comp == titleBar){
     		titleBar = null;
     	}else if(comp == contentPane){
     		contentPane = null;
     	}
     }

	override public function minimumLayoutSize(target:Container):IntDimension {
		var insets:Insets = target.getInsets();
		var size:IntDimension = insets.getOutsideSize();
		if(titleBar != null){
			size.increaseSize(titleBar.getMinimumSize());
		}
		return size;
	}
	
	override public function preferredLayoutSize(target:Container):IntDimension {
		var insets:Insets = target.getInsets();
		var size:IntDimension = insets.getOutsideSize();
		var titleBarSize:IntDimension, contentSize:IntDimension;
		if(titleBar != null){
			titleBarSize = titleBar.getPreferredSize();
		}else{
			titleBarSize = new IntDimension(0, 0);
		}
		if(contentPane != null){
			contentSize = contentPane.getPreferredSize();
		}else{
			contentSize = new IntDimension(0, 0);
		}
		size.increaseSize(new IntDimension(Math.max(titleBarSize.width, contentSize.width), titleBarSize.height + contentSize.height));
		return size;
	}
	
	override public function layoutContainer(target:Container):void{	
    	var td:IntDimension = target.getSize();
		var insets:Insets = target.getInsets();
		var r:IntRectangle = insets.getInsideBounds(td.getBounds());
		
		var d:IntDimension;
		if(titleBar != null){
			d = titleBar.getPreferredSize();
			titleBar.setBounds(new IntRectangle(r.x, r.y, r.width, d.height));
			r.y += d.height;
			r.height -= d.height;
		}
		if(contentPane != null){
			contentPane.setBounds(new IntRectangle(r.x, r.y, r.width, r.height));
		}
	}
	
    public function toString():String {
		return "WindowLayout[]";
    }
	
}
}