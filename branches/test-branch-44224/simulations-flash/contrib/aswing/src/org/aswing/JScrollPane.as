/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Rectangle;
import org.aswing.JScrollBar;
import org.aswing.overflow.JViewport;
import org.aswing.LayoutManager;
import org.aswing.plaf.ScrollPaneUI;
import org.aswing.ScrollPaneLayout;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.Viewportable;
 
/**
 * JScrollPane is a container with two scrollbar controllin the viewport's beeing viewed area.
 * <p>
 * If you want to change the unit or block increment of the scrollbars in a scrollpane, you shoud 
 * controll it with viewport instead of scrollbar directly, because the scrollbar's increment will 
 * be set to same to viewport's always. I mean use <code>JViewport.setHorizontalUnitIncrement()</code> instead of 
 * <code>JScrollBar.setUnitIncrement()</code>
 * 
 * @see org.aswing.Viewportable
 * @see org.aswing.overflow.JViewport
 * @see org.aswing.JScrollBar
 * @author iiley
 */
class org.aswing.JScrollPane extends Container{
	

	/**
	 * When one of the scrollpane's scrollbar Adjustment Value Changed.
	 *<br>
	 * onAdjustmentValueChanged(source:JScrollBar, scrollBar:JScrollBar)
	 * @see JScrollBar#ON_ADJUSTMENT_VALUE_CHANGED
	 */	
	public static var ON_ADJUSTMENT_VALUE_CHANGED:String = "onAdjustmentValueChanged";//JScrollBar.ON_ADJUSTMENT_VALUE_CHANGED; 		
	
	/**
	 * When the current viewport was replaced to another.
	 *<br>
	 * onViewportChanged(source:JScrollPane, oldViewport:Viewportable, newViewport:Viewportable)
	 */	
	public static var ON_VIEWPORT_CHANGED:String = "onViewportChanged"; 		
	
    /**
     * scrollbar are displayed only when needed.
     */
    public static var SCROLLBAR_AS_NEEDED:Number = 0;
    /**
     * scrollbar are never displayed.
     */
    public static var SCROLLBAR_NEVER:Number = 1;
    /**
     * scrollbar are always displayed.
     */
    public static var SCROLLBAR_ALWAYS:Number = 2;
	
	private var viewport:Viewportable;
	private var vScrollBar:JScrollBar;
	private var hScrollBar:JScrollBar;
	private var vsbPolicy:Number;
	private var hsbPolicy:Number;
	private var scrollbarListener:Object;
	
	/**
	 * JScrollPane(view:Component, vsbPolicy:Number, hsbPolicy:Number)<br>
	 * JScrollPane(view:Component, vsbPolicy:Number)<br>
	 * JScrollPane(view:Component)<br>
	 * JScrollPane(viewport:Viewportable, vsbPolicy:Number, hsbPolicy:Number)<br>
	 * JScrollPane(viewport:Viewportable, vsbPolicy:Number)<br>
	 * JScrollPane(viewport:Viewportable)<br>
	 * JScrollPane()
	 * <p>
	 * Create a JScrollPane, you can specified a Component to be view,
	 * then here will create a JViewport to manager the view's scroll,
	 * or a Viewportable to be the view, it mananger the scroll itself.
	 * If view is not instanceof either, no view will be viewed.
	 * 
	 * @param viewOrViewport the scroll content component or a Viewportable
	 * @param vsbPolicy SCROLLBAR_AS_NEEDED or SCROLLBAR_NEVER or SCROLLBAR_ALWAYS, default SCROLLBAR_AS_NEEDED
	 * @param hsbPolicy SCROLLBAR_AS_NEEDED or SCROLLBAR_NEVER or SCROLLBAR_ALWAYS, default SCROLLBAR_AS_NEEDED
	 * @see #SCROLLBAR_AS_NEEDED
	 * @see #SCROLLBAR_NEVER
	 * @see #SCROLLBAR_ALWAYS
	 * @see #setViewportView()
	 * @see #setViewport()
	 * @see org.aswing.Viewportable
	 * @see org.aswing.overflow.JViewport
	 * @see org.aswing.overflow.JList
	 * @see org.aswing.JTextArea
	 */
	public function JScrollPane(viewOrViewport:Object, vsbPolicy:Number, hsbPolicy:Number){
		super();
		setName("JScrollPane");
		if(vsbPolicy == undefined) vsbPolicy = SCROLLBAR_AS_NEEDED;
		if(hsbPolicy == undefined) hsbPolicy = SCROLLBAR_AS_NEEDED;
		this.vsbPolicy = vsbPolicy;
		this.hsbPolicy = hsbPolicy;
		
		scrollbarListener = new Object();
		scrollbarListener[JScrollBar.ON_ADJUSTMENT_VALUE_CHANGED] = Delegate.create(this, __onBarScroll);
		
		setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL));
		setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
		if(viewOrViewport != null){
			setView(viewOrViewport);
		}else{
			setViewport(new JViewport());
		}
		setLayout(new ScrollPaneLayout());
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(ScrollPaneUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:ScrollPaneUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "ScrollPaneUI";
	}	
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicScrollPaneUI;
    }
	
	/**
	 * @throws Error when the layout is not ScrollPaneLayout instance.
	 */
	public function setLayout(layout:LayoutManager):Void{
		if(layout instanceof ScrollPaneLayout){
			super.setLayout(layout);
		}else{
			trace("Only on set ScrollPaneLayout to JScrollPane");
			throw new Error("Only on set ScrollPaneLayout to JScrollPane");
		}
	}
	
	/**
	 * @return true always here.
	 */
	public function isValidateRoot():Boolean{
		return true;
	}
	
	/**
	 * Sets the view to viewed and scrolled by this scrollpane.
	 * if this view is not a Viewportable implementation,
	 * then here will create a JViewport to manager the view's scroll,
	 * else the Viewportable will be the viewport.
	 * <br>
	 * If view is not instanceof either, no view will be set.
	 * <br>If you want to make a component viewed by your way, you have two way:
	 * <p>
	 * <ul>
	 * <li>1.Make your component a <code>Viewportable</code> implementation.
	 * <li>2.Make a your new <code>Viewportable</code> likes <code>JViewport</code>, recommend
	 * you extends the <code>JViewport</code>, then make your component to be the viewport's view like
	 * <code>JViewport</code> does.
	 * </ul>
	 * <p>
	 * setView(view:Component)<br>
	 * setView(view:Viewportable)<br>
	 * @see Viewportable
	 * 
	 * @param viewOrViewport a component or a Viewportable object.
	 */
	public function setView(viewOrViewport:Object):Void{
		if(viewOrViewport instanceof Viewportable){
			setViewport(Viewportable(viewOrViewport));
		}else if(viewOrViewport instanceof Component){
			setViewportView(Component(viewOrViewport));
		}else{
			//do nothing
		}
	}
	
    /**
     * If currently viewport is a <code>JViewport</code> instance, 
     * set the view to it. If not, then creates a <code>JViewport</code> and then sets this view. 
     * Applications that don't provide the view directly to the <code>JScrollPane</code>
     * constructor should use this method to specify the scrollable child that's going
     * to be displayed in the scrollpane. For example:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * </pre>
     * Applications should not add children directly to the scrollpane.
     *
     * @param view the component to add to the viewport
     * @see #setViewport()
     * @see org.aswing.overflow.JViewport#setView()
     */
	public function setViewportView(view:Component):Void{
		var jviewport:JViewport = JViewport(getViewport());
		if(jviewport != null){
			jviewport.setView(view);
		}else{
			setViewport(new JViewport(view));
		}
	}
	
	/**
	 * Returns the view currently in the scrollpane's viewport if the viewport 
	 * is a JViewport instance, otherwise, null will be returned.
	 */
	public function getViewportView():Component{
		var jviewport:JViewport = JViewport(getViewport());
		if(jviewport != null){
			return jviewport.getView();
		}else{
			return null;
		}
	}
	
    /**
     * Removes the old viewport (if there is one); and syncs the scrollbars and
     * headers with the new viewport.
     * <p>
     * Most applications will find it more convenient to use 
     * <code>setView</code>
     * to add a viewport or a view to the scrollpane.
     * 
     * @param viewport the new viewport to be used; if viewport is
     *		<code>null</code>, the old viewport is still removed
     *		and the new viewport is set to <code>null</code>
     * @see #getViewport()
     * @see #setViewportView()
     * @see org.aswing.overflow.JList
     * @see org.aswing.JTextArea
     * @see org.aswing.overflow.JTable
     */
	private function setViewport(vp:Viewportable):Void{
		if(viewport != vp){
			var oldViewport:Viewportable = viewport;
			var newViewport:Viewportable = vp;
			if(viewport != null){
				remove(viewport.getViewportPane());
			}
			viewport = vp;
			if(viewport != null){
				super.insert(-1, viewport.getViewportPane());
			}
			revalidate();
			dispatchEvent(createEventObj(ON_VIEWPORT_CHANGED, oldViewport, newViewport));
		}
	}
	
	public function getViewport():Viewportable{
		return viewport;
	}
	
	/**
	 * Returns the visible extent rectangle related the current scroll properties.
	 * @return the visible extent rectangle
	 */
	public function getVisibleRect():Rectangle{
		return new Rectangle(getHorizontalScrollBar().getValue(),
							 getVerticalScrollBar().getValue(),
							 getHorizontalScrollBar().getVisibleAmount(),
							 getVerticalScrollBar().getVisibleAmount());
	}
	
	/**
	 * Shortcut to and ON_ADJUSTMENT_VALUE_CHANGED listener.
	 * <p>
	 * addAdjustmentListener(func:Function)<br>
	 * addAdjustmentListener(func:Function, obj:Object)<br>
	 * @param func the function which want to handler the event.
	 * @param obj context in which to run the function of param func.
	 * @see #ON_ADJUSTMENT_VALUE_CHANGED
	 */
	public function addAdjustmentListener(func:Function, obj:Object):Object{
		return addEventListener(ON_ADJUSTMENT_VALUE_CHANGED, func, obj);
	}
	
	/**
	 * Event handler for scroll bars
	 */
	private function __onBarScroll(scrollBar:JScrollBar):Void{
		dispatchEvent(createEventObj(ON_ADJUSTMENT_VALUE_CHANGED, scrollBar));
	}
		
	/**
	 * Adds the scrollbar that controls the viewport's horizontal view position to the scrollpane. 
	 */
	public function setHorizontalScrollBar(horizontalScrollBar:JScrollBar):Void{
		if(hScrollBar != horizontalScrollBar){
			hScrollBar.removeEventListener(scrollbarListener);
			remove(hScrollBar);
			hScrollBar = horizontalScrollBar;
			hScrollBar.setName("HorizontalScrollBar");
			super.insert(-1, hScrollBar);
			hScrollBar.addEventListener(scrollbarListener);
			revalidate();
		}
	}
	
	public function getHorizontalScrollBar():JScrollBar{
		return hScrollBar;
	}
	
	public function setHorizontalScrollBarPolicy(policy:Number):Void{
		hsbPolicy = (policy == undefined ? SCROLLBAR_AS_NEEDED : policy);
	} 
 
	public function getHorizontalScrollBarPolicy():Number{
		return hsbPolicy;
	} 
	
 	/**
	 * Adds the scrollbar that controls the viewport's vertical view position to the scrollpane. 
	 */
	public function setVerticalScrollBar(verticalScrollBar:JScrollBar):Void{
		if(vScrollBar != verticalScrollBar){
			vScrollBar.removeEventListener(scrollbarListener);
			remove(vScrollBar);
			vScrollBar = verticalScrollBar;
			vScrollBar.setName("verticalScrollBar");
			super.insert(-1, vScrollBar);
			vScrollBar.addEventListener(scrollbarListener);
			revalidate();
		}
	}
	
	public function getVerticalScrollBar():JScrollBar{
		return vScrollBar;
	}
	
	public function setVerticalScrollBarPolicy(policy:Number):Void{
		vsbPolicy = (policy == undefined ? SCROLLBAR_AS_NEEDED : policy);
	} 

	public function getVerticalScrollBarPolicy():Number{
		return vsbPolicy;
	}
	
	/**
	 * @throws Error when append child to JScrollPane
	 */
	public function append(com:Component, constraints:Object):Void{
		trace("Can not add comp to JScrollPane");
		throw new Error("Can not add comp to JScrollPane");
	}
	
	/**
	 * @throws Error when append child to JScrollPane
	 */	
	public function insert(i:Number, com:Component, constraints:Object):Void{
		trace("Can not add comp to JScrollPane");
		throw new Error("Can not add comp to JScrollPane");	
	}	
}
