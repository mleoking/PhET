/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;

/**
 * Layout for JWindow and JFrame.
 * @author iiley
 */
class org.aswing.WindowLayout extends EmptyLayout {
	
	private var titleBar:Component;

	private var contentPane:Component;

    /**
     * The title bar layout constraint.
     */
    public static var TITLE:String  = "Title";

    /**
     * The content pane layout constraint.
     */
    public static var CONTENT:String  = "Content";
    
	public function WindowLayout() {
		super();
	}
	
	/**
	 * @param comp the child to add to the layout
	 * @param constraints the constraints indicate what position the child will be added.
	 * @throws Error when the constraints is not <code>TITLE</code> either <code>CONTENT</code>.
	 */
    public function addLayoutComponent(comp:Component, constraints:Object):Void {
	    if(constraints == TITLE){
	    	titleBar = comp;
	    }else if(constraints == CONTENT){
	    	contentPane = comp;
	    }else{
	    	trace("ERROR When add component to JFrame, constraints must be TITLE or CONTENT : " + constraints);
	    	throw new Error("ERROR When add component to JFrame, constraints must be TITLE or CONTENT : " + constraints);
	    }
    }
    
    public function getTitleBar():Component{
    	return titleBar;
    }
    
    public function getContentPane():Component{
    	return contentPane;
    }
    
    public function removeLayoutComponent(comp:Component):Void {
     	if(comp == titleBar){
     		titleBar = null;
     	}else if(comp == contentPane){
     		contentPane = null;
     	}
     }

	public function minimumLayoutSize(target:Container):Dimension {
		var insets:Insets = target.getInsets();
		var size:Dimension = insets.getOutsideSize();
		if(titleBar != null){
			size.increase(titleBar.getMinimumSize());
		}
		return size;
	}
	
	public function preferredLayoutSize(target:Container):Dimension {
		var insets:Insets = target.getInsets();
		var size:Dimension = insets.getOutsideSize();
		var titleBarSize:Dimension, contentSize:Dimension;
		if(titleBar != null){
			titleBarSize = titleBar.getPreferredSize();
		}else{
			titleBarSize = new Dimension(0, 0);
		}
		if(contentPane != null){
			contentSize = contentPane.getPreferredSize();
		}else{
			contentSize = new Dimension(0, 0);
		}
		size.increase(new Dimension(Math.max(titleBarSize.width, contentSize.width), titleBarSize.height + contentSize.height));
		return size;
	}
	
	public function layoutContainer(target:Container):Void{	
    	var td:Dimension = target.getSize();
		var insets:Insets = target.getInsets();
		var r:Rectangle = insets.getInsideBounds(td.getBounds());
		
		var d:Dimension;
		if(titleBar != null){
			d = titleBar.getPreferredSize();
			titleBar.setBounds(r.x, r.y, r.width, d.height);
			r.y += d.height;
			r.height -= d.height;
		}
		if(contentPane != null){
			contentPane.setBounds(r.x, r.y, r.width, r.height);
		}
	}
	
    public function toString():String {
		return "WindowLayout[]";
    }
}
