/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Insets;
import org.aswing.util.ArrayUtils;
import org.aswing.util.MCUtils;

/**
 * A Container base on a existing movieclip.
 * 
 * <p>This component is useful when you want to add component onto a existing movieclip.
 * 
 * @author iiley
 */
class org.aswing.MCPanel extends Container{
	
	private static var mcpanels:Array;
	
	/**
	 * MCPanel(panelMC:MovieClip, width:Number, height:Number)<br>
	 * MCPanel(panelMC:MovieClip)<br>
	 */
	public function MCPanel(panelMC:MovieClip, width:Number, height:Number){
		super();
		setName("MCPanel");
		root_mc = panelMC;
		bounds = new Rectangle(panelMC._x, panelMC._y,
			 width==undefined ? panelMC._width : width,
			 height==undefined ? panelMC._height : height);
		displayable = true;
		setLayout(new EmptyLayout());
		
		if(mcpanels == undefined){
			mcpanels = new Array();
		}
		setFocusable(true);
	}
	
	/**
	 * Returns all living <code>MCPanel</code>s.
	 * All MCPanel being exist when it was created and has children, being not exist when it's 
	 * destroy or removeFromContainer was called or just has not any children yet.
	 * @see #destroy()
	 * @see #removeFromContainer()
	 * @see #isLiving()
	 */
	public static function getMCPanels():Array{
		if(mcpanels == undefined){
			mcpanels = new Array();
		}
		//check and remove all not exist mcpanels
		var n:Number = mcpanels.length;
		for(var i:Number=0; i<n; i++){
			var mcp:MCPanel = MCPanel(mcpanels[i]);
			if(!mcp.isLiving()){
				mcpanels.splice(i, 1);
				i--;
				n--;
			}
		}
		return ArrayUtils.cloneArray(mcpanels);
	}
	
	public function insert(i:Number, com:Component, constraints:Object):Void{
		var liveBefore:Boolean = isLiving();
		super.insert(i, com, constraints);
		if(isLiving() && !liveBefore){
			mcpanels.push(this);
		}
	}
	
	public function removeAt(i:Number):Component{
		var com:Component = super.removeAt(i);
		if(!isLiving()){
			ArrayUtils.removeAllFromArray(mcpanels, this);
		}
		return com;
	}
	
	/**
	 * Returns is this MCPanel is living.
	 * A MCPanel is living when it is created base on a exist movieclip and was appended some children.
	 * <p>
	 * A MCPanel is not living when is was destroied, or its panel movieclip is not exist or it has no children any more. 
	 */
	public function isLiving():Boolean{
		return MCUtils.isMovieClipExist(root_mc) && getComponentCount() > 0;
	}
	
	/**
	 * You can't set a border to a MCPanel, it will throw error.
	 * @throws Error when you set any border to MCPanel.
	 */
	public function setBorder(b:Border):Void{
		trace("Can not set a Border to MCPanel");
		throw new Error("Can not set a Border to MCPanel");
	}
	
	public function getInsets():Insets{
		return new Insets();
	}
	
	public function getPanelMC():MovieClip{
		return root_mc;
	}
	
	/**
	 * @return true always here.
	 */
	public function isValidateRoot():Boolean{
		return true;
	}	
		
	/**
	 * cannot add this Com to a container.
	 * @throws Error when you add it to any parent.
	 */
	public function addTo(parent:Container):Void{
		trace("Cannot add a MCPanel to a Container");
		throw new Error("Cannot add a MCPanel to a Container");
	}
	
	public function createChildMC(nameStart:String):MovieClip{
		return creater.createMC(root_mc, nameStart);
	}	
	
	/**
	 * Note this method will removeMoviClip of this MCPanel's panelMC, 
	 * And after destory, there is no way make it reshow again.
	 */
	public function destroy():Void{
		super.destroy();
		ArrayUtils.removeFromArray(mcpanels, this);
	}
	
	/**
	 * Note this method will removeMoviClip of this MCPanel's panelMC.
	 * @see #destroy()
	 */
	public function removeFromContainer():Void{
		super.removeFromContainer();
	}	
	
	public function setGlobalLocation():Void{
		var gp:Point = new Point();
		root_mc.localToGlobal(gp);
		gp.move(-getX(), -getY());
		var newGlobalPos:Point = new Point(arguments[0], arguments[1]);
		newGlobalPos.move(gp.x, gp.y);
		setLocation(newGlobalPos);
	}
	
	public function getGlobalLocation(p:Point):Point{
		var gp:Point = new Point();
		root_mc.localToGlobal(gp);
		if(p != undefined){
			p.setLocation(gp);
			return p;
		}else{
			return gp;
		}
	}	
	
    /**
     * Does nothing because MCPanels must always be roots of a focus traversal
     * cycle. The passed-in value is ignored.
     *
     * @param focusCycleRoot this value is ignored
     * @see #isFocusCycleRoot()
     * @see Container#setFocusTraversalPolicy()
     * @see Container#getFocusTraversalPolicy()
     */
    public function setFocusCycleRoot(focusCycleRoot:Boolean):Void {
    	this.focusCycleRoot = true;
    }
  
    /**
     * Always returns <code>true</code> because all MCPanels must be roots of a
     * focus traversal cycle.
     *
     * @return <code>true</code>
     * @see #setFocusCycleRoot()
     * @see Container#setFocusTraversalPolicy()
     * @see Container#getFocusTraversalPolicy()
     */
    public function isFocusCycleRoot():Boolean {
		return true;
    }
  
    /**
     * Always returns <code>null</code> because MCPanels have no ancestors; they
     * represent the top of the Component hierarchy.
     *
     * @return <code>null</code>
     * @see Container#isFocusCycleRoot()
     */
    public function getFocusCycleRootAncestor():Container {
		return null;
    }
    
    /**
     * Returns null because create focus graphics mc at this component's root_mc is not safe.
     */
	public function getFocusGraphics():Graphics{
		return null;
	}
	
	/**
	 * do not create new mc
	 */
	private function create():Void{
	}
			
	private function paint(b:Rectangle):Void{
	}
}
