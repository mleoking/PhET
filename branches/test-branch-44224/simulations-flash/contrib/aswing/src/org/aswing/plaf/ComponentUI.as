/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.UIManager;
 
/**
 * The base class for all UI delegate objects in the Swing pluggable look and feel architecture. 
 * The UI delegate object for a Swing component is responsible for implementing 
 * the aspects of the component that depend on the look and feel. The JComponent 
 * class invokes methods from this class in order to delegate operations (painting, 
 * layout calculations, etc.) that may vary depending on the look and feel installed. 
 * <b>Client programs should not invoke methods on this class directly.</b>
 * 
 * @author iiley
 */
class org.aswing.plaf.ComponentUI{
	
    /**
     * Utils method to paint the background of the component with the background 
     * color in the component rect.
     * 
     * @param c the component whose background being painted.
     * @param g the Graphics context in which to paint.
     * @param b the bounds to paint background in.
     */
    public static function fillRectBackGround(c:Component, g:Graphics, b:Rectangle):Void{
 		var bgColor:ASColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
		g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
    }
	
    /**
     * Sole constructor. (For invocation by subclass constructors,
     * typically implicit.)
     */
    public function ComponentUI() {
    }

    /**
     * Configures the specified component appropriate for the look and feel.
     * This method is invoked when the <code>ComponentUI</code> instance is being installed
     * as the UI delegate on the specified component.  This method should
     * completely configure the component for the look and feel,
     * including the following:
     * <ol>
     * <li>Install any default property values for color, fonts, borders,
     *     icons, opacity, etc. on the component.  Whenever possible, 
     *     property values initialized by the client program should <i>not</i> 
     *     be overridden.
     * <li>Install a <code>LayoutManager</code> on the component if necessary.
     * <li>Create/add any required sub-components to the component.
     * <li>Create/install event listeners on the component.
     * <li>Install keyboard UI (mnemonics, traversal, etc.) on the component.
     * <li>Initialize any appropriate instance data.
     * </ol>
     * @param c the component where this UI delegate is being installed
     *
     * @see #uninstallUI
     * @see org.aswing.Component#setUI
     * @see org.aswing.Component#updateUI
     */
    public function installUI(c:Component):Void{
    }

    /**
     * Reverses configuration which was done on the specified component during
     * <code>installUI</code>.  This method is invoked when this 
     * <code>ComponentUI</code> instance is being removed as the UI delegate 
     * for the specified component.  This method should undo the
     * configuration performed in <code>installUI</code>, being careful to 
     * leave the <code>Component</code> instance in a clean state (no 
     * extraneous listeners, look-and-feel-specific property objects, etc.).
     * This should include the following:
     * <ol>
     * <li>Remove any UI-set borders from the component.
     * <li>Remove any UI-set layout managers on the component.
     * <li>Remove any UI-added sub-components from the component.
     * <li>Remove any UI-added event listeners from the component.
     * <li>Remove any UI-installed keyboard UI from the component.
     * <li>Remove any UI-added MCs from this component.
     * <li>Nullify any allocated instance data objects to allow for GC.
     * </ol>
     * @param c the component from which this UI delegate is being removed;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #installUI
     * @see org.aswing.Component#updateUI
     */
    public function uninstallUI(c:Component):Void{
    }
    
    /**
     * Notifies this UI delegate that it's time to create the specified
     * component's ui MCs.  This method is invoked by <code>Component</code> 
     * when the specified component is being created.
     *
     * <p>In general this method need not be overridden by subclasses;
     * all look-and-feel ui creating code should reside in this method.
     * 
     * @param c the component being painted;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     * 
     * @see org.aswing.Component#create
     */    
    public function create(c:Component):Void{
    }
    
    /**
     * Notifies this UI delegate that it's time to paint the specified
     * component.  This method is invoked by <code>Component</code> 
     * when the specified component is being painted.
     *
     * <p>In general this method need be overridden by subclasses;
     * all look-and-feel rendering code should reside in this method.
     * And there is a default background paint method, you should call
     * it in your overridden paint method.
     * 
     * @param c the component being painted;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components.
     * @param g the Graphics context in which to paint.
     * @param b the bounds to paint UI in.
     * 
     * @see org.aswing.Component#paint
     * @see #paintBackGround
     */
    public function paint(c:Component, g:Graphics, b:Rectangle):Void{
    	paintBackGround(c, g, b);
    	paintFocusIfItsFocusOwner(c);
    }
    
    /**
     * Paints focus representation to the component.
     */
    public function paintFocus(c:Component, g:Graphics):Void{
    	var dim:Dimension = c.getSize();
    	g.drawRectangle(new Pen(getDefaultFocusColorInner(), 1), 0.5, 0.5, dim.width-1, dim.height-1);
    	g.drawRectangle(new Pen(getDefaultFocusColorOutter(), 1), 1.5, 1.5, dim.width-3, dim.height-3);
    }
    
    private function getDefaultFocusColorInner():ASColor{
    	return UIManager.getColor("focusInner");
    }
    private function getDefaultFocusColorOutter():ASColor{
    	return UIManager.getColor("focusOutter");
    }
    
    /**
     * Clears the focus representation of the component.
     */    
    public function clearFocus(c:Component):Void{
    	c.clearFocusGraphics();
    }
    
    /**
     * Paints focus representation to the component if it is the focus owner.
     */
    private function paintFocusIfItsFocusOwner(c:Component):Void{
    	if(c.isFocusOwner() && FocusManager.getCurrentManager().isTraversing()){
    		paintFocus(c, c.getFocusGraphics());
    	}
    }
    
    /**
     * The default background paint function. 
     * paint the background with the background color in the component rect.
     * If your UI want to paint a different background for your component, 
     * override this method.
     * <p>
     * Generally you should call this method in the <code>paint</code> method.
     * override this method to paint different background.
     * @param c the component whose background being painted.
     * @param g the Graphics context in which to paint.
     * @param b the bounds to paint background in.
     * 
     * @see #paint
     */
    private function paintBackGround(c:Component, g:Graphics, b:Rectangle):Void{
    	if(c.isOpaque())
    		fillBackGround(c, g, b);
    }
    
    /**
     * Utils method to paint the background of the component with the background 
     * color in the component rect.
     * 
     * @param c the component whose background being painted.
     * @param g the Graphics context in which to paint.
     * @param b the bounds to paint background in.
     */
    private function fillBackGround(c:Component, g:Graphics, b:Rectangle):Void{
    	fillRectBackGround(c, g, b);
    }
    
    /**
     * Returns the specified component's preferred size appropriate for
     * the look and feel.  If <code>null</code> is returned, the preferred
     * size will be calculated by the component's layout manager instead 
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this 
     * method returns <code>null</code>.
     *
     * @param c the component whose preferred size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see org.aswing.Component#getPreferredSize
     * @see org.aswing.LayoutManager#preferredLayoutSize
     */
    public function getPreferredSize(c:Component):Dimension{
		return null;
    }

    /**
     * Returns the specified component's minimum size appropriate for
     * the look and feel.  If <code>null</code> is returned, the minimum
     * size will be calculated by the component's layout manager instead 
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this 
     * method invokes <code>getPreferredSize</code> and returns that value.
     *
     * @param c the component whose minimum size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @return a <code>Dimension</code> object or <code>null</code>
     *
     * @see org.aswing.Component#getMinimumSize
     * @see org.aswing.LayoutManager#minimumLayoutSize
     * @see #getPreferredSize
     */
    public function getMinimumSize(c:Component):Dimension{
		return getPreferredSize(c);
    }

    /**
     * Returns the specified component's maximum size appropriate for
     * the look and feel.  If <code>null</code> is returned, the maximum
     * size will be calculated by the component's layout manager instead 
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this 
     * method invokes <code>getPreferredSize</code> and returns that value.
     *
     * @param c the component whose maximum size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     * @return a <code>Dimension</code> object or <code>null</code>
     *
     * @see org.aswing.Component#getMaximumSize
     * @see org.aswing.LayoutManager#maximumLayoutSize
     * @see #getPreferredSize
     */
    public function getMaximumSize(c:Component):Dimension{
		return getPreferredSize(c);
    }    
}
