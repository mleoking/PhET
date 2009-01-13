/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.Insets;

/**
 * A border layout lays out a container, arranging and resizing
 * its components to fit in five regions:
 * north, south, east, west, and center.
 * Each region may contain no more than one component, and 
 * is identified by a corresponding constant:
 * <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>,
 * <code>WEST</code>, and <code>CENTER</code>.  When adding a
 * component to a container with a border layout, use one of these
 * five constants, for example:
 * <pre>
 *    Panel p = new Panel();
 *    p.setLayout(new BorderLayout());
 *    p.add(new Button("Okay"), BorderLayout.SOUTH);
 * </pre>
 * As a convenience, <code>BorderLayout</code> interprets the
 * absence of a string specification the same as the constant
 * <code>CENTER</code>:
 * <pre>
 *    Panel p2 = new Panel();
 *    p2.setLayout(new BorderLayout());
 *    p2.add(new TextArea());  // Same as p.add(new TextArea(), BorderLayout.CENTER);
 * </pre>
 * 
 * @author iiley
 */
class org.aswing.BorderLayout extends EmptyLayout{
	
	private var hgap:Number;

	private var vgap:Number;

	private var strategy:Number;
	
	private var north:Component;

	private var west:Component;

	private var east:Component;

    private var south:Component;

	private var center:Component;
    
    private var firstLine:Component;

	private var lastLine:Component;

	private var firstItem:Component;

	private var lastItem:Component;
	
	private var defaultConstraints:String;

	/**
	 * North and south components fits horizontal bounds. 
	 */
	public static var STRATEGY_HORIZONTAL:Number = 0;

	/**
	 * West and East components fits vertical bounds. 
	 */
	public static var STRATEGY_VERTICAL:Number = 1;

    /**
     * The north layout constraint (top of container).
     */
    public static var NORTH:String  = "North";

    /**
     * The south layout constraint (bottom of container).
     */
    public static var SOUTH:String  = "South";

    /**
     * The east layout constraint (right side of container).
     */
    public static var EAST :String  = "East";

    /**
     * The west layout constraint (left side of container).
     */
    public static var WEST :String  = "West";

    /**
     * The center layout constraint (middle of container).
     */
    public static var CENTER:String  = "Center";

	
	public static var BEFORE_FIRST_LINE:String  = "First";


    public static var AFTER_LAST_LINE:String  = "Last";


    public static var BEFORE_LINE_BEGINS:String  = "Before";


    public static var AFTER_LINE_ENDS:String  = "After";


    public static var PAGE_START:String  = BEFORE_FIRST_LINE;


    public static var PAGE_END:String  = AFTER_LAST_LINE;


    public static var LINE_START:String  = BEFORE_LINE_BEGINS;


    public static var LINE_END:String  = AFTER_LINE_ENDS;

    /**
     * Constructs a border layout with the specified gaps
     * between components.
     * The horizontal gap is specified by <code>hgap</code>
     * and the vertical gap is specified by <code>vgap</code>.
     * @param   hgap   		the horizontal gap.
     * @param   vgap   		the vertical gap.
     * @param	strategy	the layout strategy.
     */
    public function BorderLayout(hgap:Number, vgap:Number, strategy:Number) {
    	if (hgap == undefined) hgap = 0;
    	if (vgap == undefined) vgap = 0;
    	if (strategy == undefined) strategy = STRATEGY_HORIZONTAL;
		this.hgap = hgap;
		this.vgap = vgap;
		this.strategy = strategy;
		this.defaultConstraints = CENTER; 
    }

	public function setDefaultConstraints(constraints:Object):Void {
		defaultConstraints = constraints.toString();
	}

    public function getHgap():Number {
		return hgap;
    }

    public function setHgap(hgap:Number):Void {
		this.hgap = hgap;
    }

    public function getVgap():Number {
		return vgap;
    }

    public function setVgap(vgap:Number):Void {
		this.vgap = vgap;
    }

	public function setStrategy(strategy:Number):Void {
		this.strategy = strategy;	
	}

	public function getStrategy():Number {
		return strategy;	
	}

    public function addLayoutComponent(comp:Component, constraints:Object):Void {
	    addLayoutComponentByAlign(constraints.toString(), comp);
    }

    private function addLayoutComponentByAlign(name:String, comp:Component):Void {
		if (name == null) {
	   		name = defaultConstraints;
		}

		if (CENTER == name) {
		    center = comp;
		} else if (NORTH == name) {
		    north = comp;
		} else if (SOUTH == name) {
		    south = comp;
		} else if (EAST == name) {
		    east = comp;
		} else if (WEST == name) {
		    west = comp;
		} else if (BEFORE_FIRST_LINE == name) {
		    firstLine = comp;
		} else if (AFTER_LAST_LINE == name) {
		    lastLine = comp;
		} else if (BEFORE_LINE_BEGINS == name) {
		    firstItem = comp;
		} else if (AFTER_LINE_ENDS == name) {
		    lastItem = comp;
		} else {
			//defaut center
		    center = comp;
		}
    }

    public function removeLayoutComponent(comp:Component):Void {
		if (comp == center) {
		    center = null;
		} else if (comp == north) {
		    north = null;
		} else if (comp == south) {
		    south = null;
		} else if (comp == east) {
		    east = null;
		} else if (comp == west) {
		    west = null;
		}
		if (comp == firstLine) {
		    firstLine = null;
		} else if (comp == lastLine) {
		    lastLine = null;
		} else if (comp == firstItem) {
		    firstItem = null;
		} else if (comp == lastItem) {
		    lastItem = null;
		}
    }

    public function minimumLayoutSize(target:Container):Dimension {
		return target.getInsets().getOutsideSize();
    }
	
    public function preferredLayoutSize(target:Container):Dimension {
    	var dim:Dimension = new Dimension(0, 0);
	    var ltr:Boolean = true;
	    var c:Component = null;
		
		var d:Dimension;
		if (strategy == STRATEGY_HORIZONTAL) {
			if ((c=getChild(EAST,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width += d.width + hgap;
			    dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(WEST,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width += d.width + hgap;
			    dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(CENTER,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width += d.width;
			    dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(NORTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height + vgap;
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height + vgap;
			}
		}
		else {
			if ((c=getChild(NORTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width += d.width + hgap;
			    dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width += d.width + hgap;
			    dim.height = Math.max(d.height, dim.height);
			}
			if ((c=getChild(CENTER,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height;
			}
			if ((c=getChild(WEST,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height + vgap;
			}
			if ((c=getChild(WEST,ltr)) != null) {
			    d = c.getPreferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height + vgap;
			}
		}
		var insets:Insets = target.getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
    }

    public function getLayoutAlignmentX(target:Container):Number{
    	return 0.5;
    }

    public function getLayoutAlignmentY(target:Container):Number{
    	return 0.5;
    }

    /**
     * Lays out the container argument using this border layout.
     * <p>
     * This method actually reshapes the components in the specified
     * container in order to satisfy the constraints of this
     * <code>BorderLayout</code> object. The <code>NORTH</code>
     * and <code>SOUTH</code> components, if any, are placed at
     * the top and bottom of the container, respectively. The
     * <code>WEST</code> and <code>EAST</code> components are
     * then placed on the left and right, respectively. Finally,
     * the <code>CENTER</code> object is placed in any remaining
     * space in the middle.
     * <p>
     * Most applications do not call this method directly. This method
     * is called when a container calls its <code>doLayout</code> method.
     * @param   target   the container in which to do the layout.
     * @see     Container
     * @see     Container#doLayout()
     */
    public function layoutContainer(target:Container):Void{
    	var td:Dimension = target.getSize();
		var insets:Insets = target.getInsets();
		var top:Number = insets.top;
		var bottom:Number = td.height - insets.bottom;
		var left:Number = insets.left;
		var right:Number = td.width - insets.right;
	    var ltr:Boolean = true;
	    var c:Component = null;
	
		var d:Dimension;
		if (strategy == STRATEGY_HORIZONTAL)
		{
			if ((c=getChild(NORTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(left, top, right - left, d.height);
			    top += d.height + vgap;
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(left, bottom - d.height, right - left, d.height);
			    bottom -= d.height + vgap;
			}
			if ((c=getChild(EAST,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(right - d.width, top, d.width, bottom - top);
			    right -= d.width + hgap;
			}
			if ((c=getChild(WEST,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(left, top, d.width, bottom - top);
			    left += d.width + hgap;
			}
			if ((c=getChild(CENTER,ltr)) != null) {
			    c.setBounds(left, top, right - left, bottom - top);
			}
		}
		else {
			if ((c=getChild(EAST,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(right - d.width, top, d.width, bottom - top);
			    right -= d.width + hgap;
			}
			if ((c=getChild(WEST,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(left, top, d.width, bottom - top);
			    left += d.width + hgap;
			}
			if ((c=getChild(NORTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(left, top, right - left, d.height);
			    top += d.height + vgap;
			}
			if ((c=getChild(SOUTH,ltr)) != null) {
			    d = c.getPreferredSize();
			    c.setBounds(left, bottom - d.height, right - left, d.height);
			    bottom -= d.height + vgap;
			}
			if ((c=getChild(CENTER,ltr)) != null) {
			    c.setBounds(left, top, right - left, bottom - top);
			}
		}
    }

    /**
     * Get the component that corresponds to the given constraint location
     *
     * @param   key     The desired absolute position,
     *                  either NORTH, SOUTH, EAST, or WEST.
     * @param   ltr     Is the component line direction left-to-right?
     */
    private function getChild(key:String, ltr:Boolean):Component {
        var result:Component = null;

        if (key == NORTH) {
            result = (firstLine != null) ? firstLine : north;
        }
        else if (key == SOUTH) {
            result = (lastLine != null) ? lastLine : south;
        }
        else if (key == WEST) {
            result = ltr ? firstItem : lastItem;
            if (result == null) {
                result = west;
            }
        }
        else if (key == EAST) {
            result = ltr ? lastItem : firstItem;
            if (result == null) {
                result = east;
            }
        }
        else if (key == CENTER) {
            result = center;
        }
        if (result != null && !result.isVisible()) {
            result = null;
        }
        return result;
    }

    public function toString():String {
		return "BorderLayout[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
