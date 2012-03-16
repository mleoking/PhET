/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultSingleSelectionModel;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.plaf.InsetsUIResource;
import org.aswing.plaf.UIResource;
import org.aswing.SingleSelectionModel;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;
import org.aswing.util.StringUtils;

/**
 * An abstract class for all Container pane class that have title, icon, tip for every sub pane.
 * For example JAccordion, JTabbedPane.
 * @author iiley
 */
class org.aswing.AbstractTabbedPane extends Container {
	
	/**
	 * When the selection changed.<br>
	 * onStateChanged(source:AbstractTabbedPane)
	 */	
	public static var ON_STATE_CHANGED:String = Component.ON_STATE_CHANGED;
	
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var CENTER:Number  = ASWingConstants.CENTER;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var TOP:Number     = ASWingConstants.TOP;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var LEFT:Number    = ASWingConstants.LEFT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var BOTTOM:Number  = ASWingConstants.BOTTOM;
 	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
    public static var RIGHT:Number   = ASWingConstants.RIGHT;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */        
	public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
	/**
	 * A fast access to ASWingConstants Constant
	 * @see org.aswing.ASWingConstants
	 */
	public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
	
    private var titles:Array;
    private var icons:Array;
    private var tips:Array;
    private var enables:Array;
    private var visibles:Array;
    private var model:SingleSelectionModel;
    private var modelListener:Object;
    
	// Icon/Label Alignment
    private var verticalAlignment:Number;
    private var horizontalAlignment:Number;
    private var verticalTextPosition:Number;
    private var horizontalTextPosition:Number;
    private var iconTextGap:Number;
    private var margin:Insets;
    
	private function AbstractTabbedPane() {
		super();
		//default
    	verticalAlignment = CENTER;
    	horizontalAlignment = CENTER;
    	verticalTextPosition = CENTER;
    	horizontalTextPosition = RIGHT;
    	iconTextGap = 4;
    	
		titles = new Array();
		icons = new Array();
		tips = new Array();
		enables = new Array();
		visibles = new Array();
		modelListener = new Object();
		modelListener[ON_STATE_CHANGED] = Delegate.create(this, __modelStateChanged);
		setModel(new DefaultSingleSelectionModel());
	}
	
    /**
     * Sets the model to be used with this tabbedpane.
     * @param model the model to be used
     * @see #getModel()
     */
	public function setModel(model:SingleSelectionModel):Void{
        var oldModel:SingleSelectionModel = getModel();
        if (oldModel != null) {
            oldModel.removeEventListener(modelListener);
        }
        this.model = model;
        if (model != null) {
            model.addEventListener(modelListener);
        }
        repaint();
	}
	
    /**
     * Returns the model associated with this tabbedpane.
     * @see #setModel()
     */
	public function getModel():SingleSelectionModel{
		return model;
	}
	
	/**
	 * addChangeListener(func : Function, obj : Object) : Object<br>
	 * addChangeListener(func : Function) : Object<br>
	 * listens the tab selection change event.
	 */
	public function addChangeListener(func : Function, obj : Object) : Object {
		return addEventListener(Component.ON_STATE_CHANGED, func, obj);
	}
	/**
	 * Adds a component to the tabbedpane. 
	 * If constraints is a String or an Icon or an Object(object.toString() as a title), 
	 * it will be used for the tab title, 
	 * otherwise the component's name will be used as the tab title. 
	 * Shortcut of <code>insert(-1, com, constraints)</code>. 
	 * @param com  the component to be displayed when this tab is clicked
	 * @param constraints  the object to be displayed in the tab
	 * @see Container#append()
	 * @see #insert()
	 * @see #insertTab()
	 */
	public function append(com:Component, constraints:Object):Void{
		insert(-1, com, constraints);
	}
	
	/**
	 * Adds a component to the tabbedpane with spesified index.
	 * If constraints is a String or an Icon or an Object(object.toString() as a title), 
	 * it will be used for the tab title, 
	 * otherwise the component's name will be used as the tab title. 
	 * Cover method for insertTab. 
	 * @param i index the position at which to insert the component, or less than 0 value to append the component to the end 
	 * @param com the component to be added
	 * @param constraints the object to be displayed in the tab
	 * @see Container#insert()
	 * @see #insertTab()
	 */
	public function insert(i:Number, com:Component, constraints:Object):Void{
		var title:String = null;
		var icon:Icon = null;
		if(constraints == undefined){
			title = com.getName();
		}else if(StringUtils.isString(constraints)){
			title = String(constraints);
		}else if(constraints instanceof Icon){
			icon = Icon(constraints);
		}else{
			title = constraints.toString();
		}
		insertTab(i, com, title, icon, null);
	}
	
	/**
	 * Adds a component and tip represented by a title and/or icon, either of which can be null.
	 * Shortcut of <code>insertTab(-1, com, title, icon, tip)</code>
	 * @param com The component to be displayed when this tab is clicked
	 * @param title the title to be displayed in this tab
	 * @param icon the icon to be displayed in this tab
	 * @param tip the tooltip to be displayed for this tab, can be null means no tool tip.
	 */
	public function appendTab(com:Component, title:String, icon:Icon, tip:String):Void{
		insertTab(-1, com, title, icon, tip);
	}
	
	/**
	 * Inserts a component, at index, represented by a title and/or icon, 
	 * either of which may be null.
	 * @param i the index position to insert this new tab, less than 0 means append to the end.
	 * @param com The component to be displayed when this tab is clicked
	 * @param title the title to be displayed in this tab
	 * @param icon the icon to be displayed in this tab
	 * @param tip the tooltip to be displayed for this tab, can be null means no tool tip.
	 * @throws Error when index > children count
	 */
	public function insertTab(i:Number, com:Component, title:String, icon:Icon, tip:String):Void{
		if(i > getComponentCount()){
			trace("illegal component position when insert comp to container");
			throw new Error("illegal component position when insert comp to container");
		}
		if(i < 0){
			i = getComponentCount();
		}
		insertToArray(titles, i, title);
		insertToArray(icons, i, icon);
		insertToArray(tips, i, tip);
		insertToArray(enables, i, true);
		insertToArray(visibles, i, true);
		
		var currentSelectedIndex:Number = getSelectedIndex();
		var selectedIndexAfterRemove:Number = currentSelectedIndex;
		if(i <= currentSelectedIndex){
			selectedIndexAfterRemove = currentSelectedIndex + 1;
		}else if(currentSelectedIndex < 0){
			selectedIndexAfterRemove = i;
		}
		super.insert(i, com);
		getModel().setSelectedIndex(selectedIndexAfterRemove);
	}
	
	/**
	 * Removes the specified child component.
	 * After the component is removed, its visibility is reset to true to ensure it will be visible if added to other containers. 
	 * @param i the index of component.
	 * @return the component just removed, or null there is not component at this position.
	 */
	public function removeTabAt(i:Number):Component{
		if(i >= getComponentCount() || getComponentCount() < 0){
			return null;
		}
		Icon(icons[i]).uninstallIcon(this);//uninstall icon first
		removeFromArray(titles, i);
		removeFromArray(icons, i);
		removeFromArray(tips, i);
		removeFromArray(enables, i);
		removeFromArray(visibles, i);
		
		var currentSelectedIndex:Number = getSelectedIndex();
		var selectedIndexAfterRemove:Number = currentSelectedIndex;
		if(i == currentSelectedIndex){
			selectedIndexAfterRemove = -1;
		}else if(i < currentSelectedIndex){
			selectedIndexAfterRemove = currentSelectedIndex - 1;
		}
		var rc:Component = super.removeAt(i);
		rc.setVisible(true);
		
		if(selectedIndexAfterRemove < 0){
			getModel().clearSelection();
		}else{
			getModel().setSelectedIndex(selectedIndexAfterRemove);
		}
		
		return rc;
	}
	
	/**
	 * Sets whether or not the tab at index is enabled. 
	 * Nothing will happen if there is no tab at that index.
	 * @param index the tab index which should be enabled/disabled
	 * @param enabled whether or not the tab should be enabled 
	 */
	public function setEnabledAt(index:Number, enabled:Boolean):Void{
		if(enables[index] != enabled){
			enables[index] = enabled;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Returns whether or not the tab at index is currently enabled. 
	 * false will be returned if there is no tab at that index.
	 * @param index  the index of the item being queried 
	 * @return if the tab at index is enabled; false otherwise.
	 */
	public function isEnabledAt(index:Number):Boolean{
		return enables[index] == true;
	}

	/**
	 * Sets whether or not the tab at index is visible. 
	 * Nothing will happen if there is no tab at that index.
	 * @param index the tab index which should be shown/hidden
	 * @param shown whether or not the tab should be visible 
	 */
	public function setVisibleAt(index:Number, visible:Boolean):Void{
		if(visibles[index] != visible){
			visibles[index] = visible;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Returns whether or not the tab at index is currently visible. 
	 * false will be returned if there is no tab at that index.
	 * @param index  the index of the item being queried 
	 * @return if the tab at index is visible; false otherwise.
	 */
	public function isVisibleAt(index:Number):Boolean{
		return visibles[index] == true;
	}
	
	/**
	 * Removes the specified child component.
	 * After the component is removed, its visibility is reset to true to ensure it will be visible if added to other containers. 
	 * 
	 * Cover method for removeTabAt. 
	 * @see Container#remove()
	 * @see #removeTabAt()
	 */
	public function remove(com:Component):Component{
		var index:Number = getIndex(com);
		if(index >= 0){
			return removeAt(index);
		}
		return null;
	}
	
	/**
	 * Removes the specified index child component. 
	 * After the component associated with index is removed, its visibility is reset to true to ensure it will be visible if added to other containers.
	 * Cover method for removeTabAt. 
	 * @see #removeTabAt() 
	 * @see Container#removeAt()
	 */	
	public function removeAt(index:Number):Component{
		return removeTabAt(index);
	}
	
	/**
	 * Remove all child components.
	 * After the component is removed, its visibility is reset to true to ensure it will be visible if added to other containers. 
	 * @see #removeAt()
	 * @see #removeTabAt()
	 * @see Container#removeAll()
	 */
	public function removeAll():Void{
		while(children.length > 0){
			removeAt(children.length - 1);
		}
	}
	
	/**
	 * Returns the count of tabs.
	 */
	public function getTabCount():Number{
		return getComponentCount();
	}
	
	/**
	 * Returns the tab title at specified index. 
	 * @param i the index
	 * @return the tab title
	 */
	public function getTitleAt(i:Number):String{
		return titles[i];
	}
	
	/**
	 * Returns the tab icon at specified index. 
	 * @param i the index
	 * @return the tab icon
	 */	
	public function getIconAt(i:Number):Icon{
		return Icon(icons[i]);
	}
	
	/**
	 * Returns the tab tool tip text at specified index. 
	 * @param i the index
	 * @return the tab tool tip text
	 */	
	public function getTipAt(i:Number):String{
		return tips[i];
	}
	
	/**
	 * Sets the title at index to title which can be null.
	 * Nothing will happen if there is no tab at that index. 
	 * @param i the index
	 * @param t the tab title
	 */
	public function setTitleAt(i:Number, t:String):Void{
		if(i < 0 || i >= getComponentCount()){
			return;
		}
		if(titles[i] != t){
			titles[i] = t;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Sets the icon at index to tab icon which can be null.
	 * Nothing will happen if there is no tab at that index. 
	 * @param i the index
	 * @param icon the tab icon
	 */	
	public function setIconAt(i:Number, icon:Icon):Void{
		if(i < 0 || i >= getComponentCount()){
			return;
		}
		if(icons[i] != icon){
			uninstallIconWhenNextPaint(icons[i]);
			icons[i] = icon;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Sets the tool tip at index to tab tooltip which can be null.
	 * Nothing will happen if there is no tab at that index. 
	 * @param i the index
	 * @param t the tab tool tip
	 */	
	public function setTipAt(i:Number, t:String):Void{
		if(i < 0 || i >= getComponentCount()){
			return;
		}
		if(tips[i] != t){
			tips[i] = t;
			revalidate();
			repaint();
		}
	}	
	
	/**
	 * Returns the first tab index with a given title, or -1 if no tab has this title. 
	 * @param title the title for the tab 
	 * @return the first tab index which matches title, or -1 if no tab has this title
	 */
	public function indexOfTitle(title:String):Number{
		return ArrayUtils.indexInArray(titles, title);
	}
	
	/**
	 * Returns the first tab index with a given icon, or -1 if no tab has this icon. 
	 * @param title the title for the tab 
	 * @return the first tab index which matches icon, or -1 if no tab has this icon
	 */	
	public function indexOfIcon(icon:Icon):Number{
		return ArrayUtils.indexInArray(icons, icon);
	}
	
	/**
	 * Returns the first tab index with a given tip, or -1 if no tab has this tip. 
	 * @param title the title for the tab 
	 * @return the first tab index which matches tip, or -1 if no tab has this tip
	 */		
	public function indexOfTip(tip:String):Number{
		return ArrayUtils.indexInArray(tips, tip);
	}
	
	/**
     * Sets the selected index for this tabbedpane. The index must be
     * a valid tab index or -1, which indicates that no tab should be selected
     * (can also be used when there are no tabs in the tabbedpane).  If a -1
     * value is specified when the tabbedpane contains one or more tabs, then
     * the results will be implementation defined.
     *
     * @param index  the index to be selected
	 */
	public function setSelectedIndex(i:Number):Void{
		if(i>=-1 && i<getComponentCount()){
			getModel().setSelectedIndex(i);
		}
	}
	/**
     * Sets the selected component for this tabbedpane.  This
     * will automatically set the <code>selectedIndex</code> to the index
     * corresponding to the specified component.
     *
     * @see #getSelectedComponent()
	 */
	public function setSelectedComponent(com:Component):Void{
		setSelectedIndex(getIndex(com));
	}
	
	public function getSelectedIndex():Number{
		return getModel().getSelectedIndex();
	}
	
	public function getSelectedComponent():Component{
		var index:Number = getModel().getSelectedIndex();
		if(index >= 0){
			return getComponent(index);
		}
		return null;
	}

    /**
     * Returns the vertical alignment of the text and icon.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():Number {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:Number):Void {
        if (alignment == verticalAlignment){
        	return;
        }else{
        	verticalAlignment = alignment;
        	revalidate();
        	repaint();
        }
    }
    
    /**
     * Returns the horizontal alignment of the icon and text.
     * @return the <code>horizontalAlignment</code> property,
     *		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalAlignment():Number{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalAlignment(alignment:Number):Void {
        if (alignment == horizontalAlignment){
        	return;
        }else{
        	horizontalAlignment = alignment;     
        	revalidate();
        	repaint();
        }
    }

    
    /**
     * Returns the vertical position of the text relative to the icon.
     * @return the <code>verticalTextPosition</code> property, 
     *		one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER  (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalTextPosition():Number{
        return verticalTextPosition;
    }
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalTextPosition(textPosition:Number):Void {
        if (textPosition == verticalTextPosition){
	        return;
        }else{
        	verticalTextPosition = textPosition;
        	revalidate();
        	repaint();
        }
    }
    
    /**
     * Returns the horizontal position of the text relative to the icon.
     * @return the <code>horizontalTextPosition</code> property, 
     * 		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function getHorizontalTextPosition():Number {
        return horizontalTextPosition;
    }
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     */
    public function setHorizontalTextPosition(textPosition:Number):Void {
        if (textPosition == horizontalTextPosition){
        	return;
        }else{
        	horizontalTextPosition = textPosition;
        	revalidate();
        	repaint();
        }
    }
    
    /**
     * Returns the amount of space between the text and the icon
     * displayed in this button.
     *
     * @return an int equal to the number of pixels between the text
     *         and the icon.
     * @see #setIconTextGap()
     */
    public function getIconTextGap():Number {
        return iconTextGap;
    }

    /**
     * If both the icon and text properties are set, this property
     * defines the space between them.  
     * <p>
     * The default value of this property is 4 pixels.
     * 
     * @see #getIconTextGap()
     */
    public function setIconTextGap(iconTextGap:Number):Void {
        var oldValue:Number = this.iconTextGap;
        this.iconTextGap = iconTextGap;
        if (iconTextGap != oldValue) {
            revalidate();
            repaint();
        }
    }
    
	/**
	 * Sets space for margin between the tab border and
     * the tab label.
     *
     * @param m the space between the border and the label
	 */
	public function setMargin(m:Insets):Void{
        if (m!=null && !m.equals(margin)) {
        	margin = m;
            revalidate();
            repaint();
        }
	}
	
	/**
	 * Returns the space for margin between the tab border and
     * the tab label.
	 */
	public function getMargin():Insets{
		if(margin == null){
			return new InsetsUIResource();//make it can be replaced by LAF
		}else{
			if(margin instanceof UIResource){//make it can be replaced by LAF
				return new InsetsUIResource(margin.top, margin.left, margin.bottom, margin.right);
			}else{
				return new Insets(margin.top, margin.left, margin.bottom, margin.right);
			}
		}
	}    
        	
	private function __modelStateChanged():Void{
		fireStateChanged();
	}
    //----------------------------------------------------------------
    
	private function insertToArray(arr:Array, i:Number, obj:Object):Void{
		if(i < 0){
			arr.push(obj);
		}else{
			arr.splice(i, 0, obj);
		}
	}	
	
	private function removeFromArray(arr:Array, i:Number):Void{
		if(i < 0){
			arr.pop();
		}else{
			arr.splice(i, 1);
		}
	}
}
