/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.geom.*;
import org.aswing.event.*;
import org.aswing.error.*;
import org.aswing.plaf.basic.BasicScrollPaneUI;

/**
 * Dispatched when one of the scrollpane's scrollbar state changed.
 * @eventType org.aswing.event.ScrollPaneEvent.SCROLLBAR_STATE_CHANGED
 */
[Event(name="scrollbarStateChanged", type="org.aswing.event.ScrollPaneEvent")]
	
/**
 *  Dispatched when the viewport changed.
 *  @eventType org.aswing.event.ScrollPaneEvent.VIEWPORT_CHANGED
 */
[Event(name="viewportChanged", type="org.aswing.event.ScrollPaneEvent")]

/**
 * JScrollPane is a container with two scrollbar controllin the viewport's beeing viewed area.
 * <p>
 * If you want to change the unit or block increment of the scrollbars in a scrollpane, you shoud 
 * controll it with viewport instead of scrollbar directly, because the scrollbar's increment will 
 * be set to same to viewport's always. I mean use <code>JViewport.setHorizontalUnitIncrement()</code> instead of 
 * <code>JScrollBar.setUnitIncrement()</code>
 * 
 * @see org.aswing.Viewportable
 * @see org.aswing.JViewport
 * @see org.aswing.JScrollBar
 * @author iiley
 */
public class JScrollPane extends Container{
		
    /**
     * scrollbar are displayed only when needed.
     */
    public static const SCROLLBAR_AS_NEEDED:int = 0;
    /**
     * scrollbar are never displayed.
     */
    public static const SCROLLBAR_NEVER:int = 1;
    /**
     * scrollbar are always displayed.
     */
    public static const SCROLLBAR_ALWAYS:int = 2;
	
	private var viewport:Viewportable;
	private var vScrollBar:JScrollBar;
	private var hScrollBar:JScrollBar;
	private var vsbPolicy:int;
	private var hsbPolicy:int;
	
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
	 * @throw TypeError when viewOrViewport is not component or viewportable.
	 * @see #SCROLLBAR_AS_NEEDED
	 * @see #SCROLLBAR_NEVER
	 * @see #SCROLLBAR_ALWAYS
	 * @see #setViewportView()
	 * @see #setViewport()
	 * @see org.aswing.Viewportable
	 * @see org.aswing.JViewport
	 * @see org.aswing.JList
	 * @see org.aswing.JTextArea
	 */
	public function JScrollPane(viewOrViewport:*=null, vsbPolicy:int=SCROLLBAR_AS_NEEDED, hsbPolicy:int=SCROLLBAR_AS_NEEDED){
		super();
		setName("JScrollPane");
		this.vsbPolicy = vsbPolicy;
		this.hsbPolicy = hsbPolicy;
				
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
	
    override public function updateUI():void{
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicScrollPaneUI;
    }
    	
	override public function getUIClassID():String{
		return "ScrollPaneUI";
	}	
	
	/**
	 * @throws ArgumentError when the layout is not ScrollPaneLayout instance.
	 */
	override public function setLayout(layout:LayoutManager):void{
		if(layout is ScrollPaneLayout){
			super.setLayout(layout);
		}else{
			throw new ArgumentError("Only can set ScrollPaneLayout to JScrollPane");
		}
	}
	
	/**
	 * @return true always here.
	 */
	override public function isValidateRoot():Boolean{
		return true;
	}
	
	/**
	 * setView(view:Component)<br>
	 * setView(view:Viewportable)<br>
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
	 * </p>
	 * @param viewOrViewport a component or a Viewportable object.
	 * @see Viewportable
	 * @throw TypeError when viewOrViewport is not component or viewportable.
	 */
	public function setView(viewOrViewport:*):void{
		if(viewOrViewport is Viewportable){
			setViewport(Viewportable(viewOrViewport));
		}else if(viewOrViewport is Component){
			setViewportView(Component(viewOrViewport));
		}else{
			throw new TypeError("Only accept Component or Viewportable instance here!");
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
     * @see org.aswing.JViewport#setView()
     */
	public function setViewportView(view:Component):void{
		var jviewport:JViewport = getViewport() as JViewport;
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
		var jviewport:JViewport = getViewport() as JViewport;
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
     * @see org.aswing.JList
     * @see org.aswing.JTextArea
     * @see org.aswing.JTable
     */
	public function setViewport(vp:Viewportable):void{
		if(viewport != vp){
			if(viewport != null){
				remove(viewport.getViewportPane());
			}
			viewport = vp;
			if(viewport != null){
				insertImp(-1, viewport.getViewportPane());
			}
			revalidate();
			dispatchEvent(new ScrollPaneEvent(ScrollPaneEvent.VIEWPORT_CHANGED, true, null, true));
		}
	}
	
	public function getViewport():Viewportable{
		return viewport;
	}
	
	/**
	 * Returns the visible extent rectangle related the current scroll properties.
	 * @return the visible extent rectangle
	 */
	public function getVisibleRect():IntRectangle{
		return new IntRectangle(getHorizontalScrollBar().getValue(),
							 getVerticalScrollBar().getValue(),
							 getHorizontalScrollBar().getVisibleAmount(),
							 getVerticalScrollBar().getVisibleAmount());
	}
	
	/**
	 * Adds a scrollbar scrolled listener.
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.ScrollPaneEvent#SCROLLBAR_STATE_CHANGED
	 */
	public function addAdjustmentListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(ScrollPaneEvent.SCROLLBAR_STATE_CHANGED, listener, false, priority);
	}
	
	/**
	 * Removes a state listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.ScrollPaneEvent#SCROLLBAR_STATE_CHANGED
	 */
	public function removeAdjustmentListener(listener:Function):void{
		removeEventListener(ScrollPaneEvent.SCROLLBAR_STATE_CHANGED, listener);
	}
		
	/**
	 * Event handler for scroll bars
	 */
	private function __onBarScroll(e:InteractiveEvent):void{
		dispatchEvent(new ScrollPaneEvent(ScrollPaneEvent.SCROLLBAR_STATE_CHANGED, 
		e.isProgrammatic(), JScrollBar(e.target), false));
	}
		
	/**
	 * Adds the scrollbar that controls the viewport's horizontal view position to the scrollpane. 
	 */
	public function setHorizontalScrollBar(horizontalScrollBar:JScrollBar):void{
		if(hScrollBar != horizontalScrollBar){
			if(hScrollBar != null){
				hScrollBar.removeStateListener(__onBarScroll);
				remove(hScrollBar);
			}
			hScrollBar = horizontalScrollBar;
			if(hScrollBar != null){
				hScrollBar.setName("HorizontalScrollBar");
				insertImp(-1, hScrollBar);
				hScrollBar.addStateListener(__onBarScroll);
			}
			revalidate();
		}
	}
	
	public function getHorizontalScrollBar():JScrollBar{
		return hScrollBar;
	}
	
	public function setHorizontalScrollBarPolicy(policy:Number):void{
		hsbPolicy = policy;
	} 
 
	public function getHorizontalScrollBarPolicy():Number{
		return hsbPolicy;
	} 
	
 	/**
	 * Adds the scrollbar that controls the viewport's vertical view position to the scrollpane. 
	 */
	public function setVerticalScrollBar(verticalScrollBar:JScrollBar):void{
		if(vScrollBar != verticalScrollBar){
			if(vScrollBar != null){
				vScrollBar.removeStateListener(__onBarScroll);
				remove(vScrollBar);
			}
			vScrollBar = verticalScrollBar;
			if(vScrollBar != null){
				vScrollBar.setName("verticalScrollBar");
				insertImp(-1, vScrollBar);
				vScrollBar.addStateListener(__onBarScroll);
			}
			revalidate();
		}
	}
	
	public function getVerticalScrollBar():JScrollBar{
		return vScrollBar;
	}
	
	public function setVerticalScrollBarPolicy(policy:Number):void{
		vsbPolicy = policy;
	} 

	public function getVerticalScrollBarPolicy():Number{
		return vsbPolicy;
	}
	
	/**
	 * Sets the com to be the view.
	 */	
	override public function append(com:Component, constraints:Object=null):void{
		setView(com);
	}
	
	/**
	 * Sets the com to be the view.
	 */	
	override public function insert(i:int, com:Component, constraints:Object=null):void{
		setView(com);
	}
	
	override protected function getFocusTransmit():Component{
		return getViewport().getViewportPane();
	}	
}
}