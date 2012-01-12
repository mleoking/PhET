/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.Insets;

/**
 * A flow layout arranges components in a left-to-right flow, much
 * like lines of text in a paragraph. Flow layouts are typically used
 * to arrange buttons in a panel. It will arrange
 * buttons left to right until no more buttons fit on the same line.
 * Each line is centered.
 * <p>
 * For example, the following picture shows an applet using the flow
 * layout manager (its default layout manager) to position three buttons:
 * <p>
 * A flow layout lets each component assume its natural (preferred) size.
 *
 * @author 	iiley
 */
class org.aswing.FlowLayout extends EmptyLayout{

    /**
     * This value indicates that each row of components
     * should be left-justified.
     */
    public static var LEFT:Number 	= 0;

    /**
     * This value indicates that each row of components
     * should be centered.
     */
    public static var CENTER:Number 	= 1;

    /**
     * This value indicates that each row of components
     * should be right-justified.
     */
    public static var RIGHT:Number 	= 2;

    /**
     * <code>align</code> is the property that determines
     * how each row distributes empty space.
     * It can be one of the following values:
     * <ul>
     * <code>LEFT</code>
     * <code>RIGHT</code>
     * <code>CENTER</code>
     * </ul>
     *
     * @see #getAlignment
     * @see #setAlignment
     */
    private var align:Number;

    /**
     * The flow layout manager allows a seperation of
     * components with gaps.  The horizontal gap will
     * specify the space between components.
     *
     * @see #getHgap()
     * @see #setHgap(int)
     */
    private var hgap:Number;

    /**
     * The flow layout manager allows a seperation of
     * components with gaps.  The vertical gap will
     * specify the space between rows.
     *
     * @see #getHgap()
     * @see #setHgap(int)
     */
    private var vgap:Number;

    /**
     * <pre>
     * FlowLayout() 
     * FlowLayout(align:Number)
     * FlowLayout(align:Number, hgap:Number)
     * FlowLayout(align:Number, hgap:Number, vgap:Number)
     * </pre>
     * Creates a new flow layout manager with the indicated alignment
     * and the indicated horizontal and vertical gaps.
     * <p>
     * The value of the alignment argument must be one of
     * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>,
     * or <code>FlowLayout.CENTER</code>.
     * @param      align   (optional)the alignment value, default is LEFT
     * @param      hgap    (optional)the horizontal gap between components, default 5
     * @param      vgap    (optional)the vertical gap between components, default 5
     */
    public function FlowLayout(align:Number, hgap:Number, vgap:Number) {
    	if(align == undefined) align = LEFT;
    	if(hgap == undefined) hgap = 5;
    	if(vgap == undefined) vgap = 5;
		this.hgap = hgap;
		this.vgap = vgap;
        setAlignment(align);
    }

    /**
     * Gets the alignment for this layout.
     * Possible values are <code>FlowLayout.LEFT</code>,
     * <code>FlowLayout.RIGHT</code>, <code>FlowLayout.CENTER</code>,
     * @return     the alignment value for this layout
     * @see        #setAlignment
     */
    public function getAlignment():Number {
		return align;
    }

    /**
     * Sets the alignment for this layout.
     * Possible values are
     * <ul>
     * <li><code>FlowLayout.LEFT</code>
     * <li><code>FlowLayout.RIGHT</code>
     * <li><code>FlowLayout.CENTER</code>
     * </ul>
     * @param      align one of the alignment values shown above
     * @see        #getAlignment()
     */
    public function setAlignment(align:Number):Void {
    	//Flashout.log("set align : " + align)
        this.align = align;
    }

    /**
     * Gets the horizontal gap between components.
     * @return     the horizontal gap between components
     * @see        #setHgap()
     */
    public function getHgap():Number {
		return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     * @param hgap the horizontal gap between components
     * @see        #getHgap()
     */
    public function setHgap(hgap:Number):Void {
		this.hgap = hgap;
    }

    /**
     * Gets the vertical gap between components.
     * @return     the vertical gap between components
     * @see        #setVgap()
     */
    public function getVgap():Number {
		return vgap;
    }

    /**
     * Sets the vertical gap between components.
     * @param vgap the vertical gap between components
     * @see        #getVgap()
     */
    public function setVgap(vgap:Number):Void {
		this.vgap = vgap;
    }

    /**
     * Returns the preferred dimensions for this layout given the 
     * <i>visible</i> components in the specified target container.
     * @param target the component which needs to be laid out
     * @return    the preferred dimensions to lay out the
     *            subcomponents of the specified container
     * @see Container
     * @see #doLayout()
     */
    public function preferredLayoutSize(target:Container):Dimension {
		var dim:Dimension = new Dimension(0, 0);
		var nmembers:Number = target.getComponentCount();

		var counted:Number = 0;
		for (var i:Number = 0 ; i < nmembers ; i++) {
	    	var m:Component = target.getComponent(i);
	    	if (m.isVisible()) {
				var d:Dimension = m.getPreferredSize();
				dim.height = Math.max(dim.height, d.height);
                if (counted > 0) {
                    dim.width += hgap;
                }
				dim.width += d.width;
				counted ++;
	    	}
		}
		var insets:Insets = target.getInsets();
		dim.width += insets.left + insets.right + hgap*2;
		dim.height += insets.top + insets.bottom + vgap*2;
    	return dim;
    }

    /**
     * Returns the minimum dimensions needed to layout the <i>visible</i>
     * components contained in the specified target container.
     * @param target the component which needs to be laid out
     * @return    the minimum dimensions to lay out the
     *            subcomponents of the specified container
     * @see #preferredLayoutSize()
     * @see Container
     * @see Container#doLayout()
     */
    public function minimumLayoutSize(target:Container):Dimension {
		return target.getInsets().getOutsideSize();
    }
    
    /**
     * Centers the elements in the specified row, if there is any slack.
     * @param target the component which needs to be moved
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimensions
     * @param height the height dimensions
     * @param rowStart the beginning of the row
     * @param rowEnd the the ending of the row
     */
    private function moveComponents(target:Container, x:Number, y:Number, width:Number, height:Number,
                                rowStart:Number, rowEnd:Number, ltr:Boolean):Void {
		switch (align) {
			case LEFT:
	    		x += ltr ? 0 : width;
	    		break;
			case CENTER:
	    		x += width / 2;
	   			break;
			case RIGHT:
	    		x += ltr ? width : 0;
	    		break;
		}
		for (var i:Number = rowStart ; i < rowEnd ; i++) {
	    	var m:Component = target.getComponent(i);
	    	var d:Dimension = m.getSize();
	    	var td:Dimension = target.getSize();
	    	if (m.isVisible()) {
	        	if (ltr) {
        	    	m.setLocation(x, y + (height - d.height) / 2);
	        	} else {
	            	m.setLocation(td.width - x - d.width, y + (height - d.height) / 2);
                }
                x += d.width + hgap;
	    	}
		}
    }

    /**
     * Lays out the container. This method lets each component take
     * its preferred size by reshaping the components in the
     * target container in order to satisfy the alignment of
     * this <code>FlowLayout</code> object.
     * @param target the specified component being laid out
     * @see Container
     * @see Container#doLayout
     */
    public function layoutContainer(target:Container):Void {
		var insets:Insets = target.getInsets();
	    var td:Dimension = target.getSize();
		var maxwidth:Number = td.width - (insets.left + insets.right + hgap*2);
		var nmembers:Number = target.getComponentCount();
		var x:Number = 0;
		var y:Number = insets.top + vgap;
		var rowh:Number = 0;
		var start:Number = 0;

        var ltr:Boolean = true;

		for (var i:Number = 0 ; i < nmembers ; i++) {
	    	var m:Component = target.getComponent(i);
	    	if (m.isVisible()) {
				var d:Dimension = m.getPreferredSize();
				m.setSize(d.width, d.height);

				if ((x == 0) || ((x + d.width) <= maxwidth)) {
		    		if (x > 0) {
						x += hgap;
		    		}
		    		x += d.width;
		    		rowh = Math.max(rowh, d.height);
				} else {
		    		moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, i, ltr);
		    		x = d.width;
		    		y += vgap + rowh;
		    		rowh = d.height;
		    		start = i;
				}
	    	}
		}
		moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers, ltr);
    }
    
    /**
     * Returns a string representation of this <code>FlowLayout</code>
     * object and its values.
     * @return     a string representation of this layout
     */
    public function toString():String {
		var str:String = "";
		switch (align) {
	 	 	case LEFT:        str = ",align=left"; break;
	 		case CENTER:      str = ",align=center"; break;
	  		case RIGHT:       str = ",align=right"; break;
		}
		return "FlowLayout[hgap=" + hgap + ",vgap=" + vgap + str + "]";
    }
}
