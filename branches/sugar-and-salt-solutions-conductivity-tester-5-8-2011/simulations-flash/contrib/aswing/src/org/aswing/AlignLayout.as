/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;
import org.aswing.ASWingConstants;

/**
 * Aligns the single component inside container by horizontal and vertical
 * according to specified aligns.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.AlignLayout extends EmptyLayout {
	
	/**
	 * Aligns component inside container to top.
	 */
	public static var TOP:Number = ASWingConstants.TOP;

	/**
	 * Aligns component inside container to bottom.
	 */
	public static var BOTTOM:Number = ASWingConstants.BOTTOM;

	/**
	 * Aligns component inside container to left.
	 */
	public static var LEFT:Number = ASWingConstants.LEFT;

	/**
	 * Aligns component inside container to right.
	 */
	public static var RIGHT:Number = ASWingConstants.RIGHT;

	/**
	 * Aligns component inside container to center.
	 */
	public static var CENTER:Number = ASWingConstants.CENTER;
	
	private var vAlign:Number;
	private var hAlign:Number;
	
	
	/**
	 * Creates new <code>AlignLayout</code> instance
	 * @param hAlign the horizontal alignment of the component inside container.
	 * Default is #LEFT
	 * @param vAlign the vertical alignment of the component inside container.
	 * Default is #TOP
	 */
	public function AlignLayout(hAlign:Number, vAlign:Number) {
		super();
		this.hAlign = (hAlign != undefined) ? hAlign : LEFT;
		this.vAlign = (vAlign != undefined) ? vAlign : TOP;
	}
	
	/**
	 * Sets new horizontal align.
	 * @see #LEFT
	 * @see #CENTER
	 * @see #RIGHT
	 */
	public function setHorizonalAlign(hAlign:Number):Void {
		this.hAlign = hAlign;	
	}
	
	/**
	 * Returns current horizontal align.
	 * @see #LEFT
	 * @see #CENTER
	 * @see #RIGHT
	 */
	public function getHorizonalAlign(Void):Number {
		return hAlign;	
	}

	/**
	 * Sets new vertical align.
	 * @see #TOP
	 * @see #CENTER
	 * @see #BOTTOM
	 */
	public function setVerticalAlign(vAlign:Number):Void {
		this.vAlign = vAlign;	
	}
	
	/**
	 * Returns current vertical align.
	 * @see #TOP
	 * @see #CENTER
	 * @see #BOTTOM
	 */
	public function getVerticallAlign(Void):Number {
		return vAlign;	
	}
	
	/**
	 * Calculates preferred layout size for the given container.
	 */
    public function preferredLayoutSize(target:Container):Dimension {
    	return ( (target.getComponentCount() > 0) ?
    		target.getInsets().getOutsideSize(target.getComponent(0).getPreferredSize()) :
    		target.getInsets().getOutsideSize() );
    }

    /**
     * Layouts component by center inside the given container. 
     *
     * @param target the container to lay out
     */
    public function layoutContainer(target:Container):Void {
    	if (target.getComponentCount() > 0) {
	        var size:Dimension = target.getSize();
	        var insets:Insets = target.getInsets();
	        var rd:Rectangle = insets.getInsideBounds(size.getBounds());
	        
	        var c:Component = target.getComponent(0);
	        var preferSize:Dimension = c.getPreferredSize();
	        
	        var cd:Rectangle = new Rectangle();
	        cd.setLocation(rd.getLocation());
	        cd.setSize(rd.getSize().intersect(preferSize));
	        
	        if (rd.width > preferSize.width) {
	        	if (hAlign == CENTER) {
	        		cd.x += (rd.width - preferSize.width) / 2;
	        	} else if (hAlign == RIGHT) {
	        		cd.x += rd.width - preferSize.width;
	        	}
	        }
	        if (rd.height > preferSize.height) {
	        	if (vAlign == CENTER) {
	        		cd.y += (rd.height - preferSize.height) / 2;
	        	} else if (vAlign == BOTTOM) {
	        		cd.y += rd.height - preferSize.height;
	        	}
	        }
	        
	     	c.setBounds(cd);   
        }
    }
}
