/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingUtils;
import org.aswing.geom.Rectangle;
import org.aswing.Icon;
import org.aswing.JWindow;
import org.aswing.plaf.WindowUI;
import org.aswing.resizer.Resizer;
import org.aswing.resizer.ResizerController;
import org.aswing.UIManager;
import org.aswing.util.StringUtils;
import org.aswing.WindowLayout;

/**
 * JFrame is a window with title and maximized/iconified/normal state, and resizer. 
 * @author iiley
 */
class org.aswing.JFrame extends JWindow {
	/**
	 * When the frame's state changed.
	 *<br>
	 * onStateChanged(source:JFrame)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";//Component.ON_STATE_CHANGED; 
	
	/**
	 * When the frame's ability changed. Include:
	 * <ul>
	 * <li> resizable
	 * <li> closable
	 * <li> dragable
	 * </ul>
	 *<br>
	 * onAbilityChanged(source:JFrame)
	 */	
	public static var ON_ABILITY_CHANGED:String = "onAbilityChanged"; 
	
	
	/**
	 * @see #setState()
	 */
	public static var NORMAL:Number = 0; //0
	/**
	 * @see #setState()
	 */
	public static var ICONIFIED:Number = 2; //10
	/**
	 * @see #setState()
	 */
	public static var MAXIMIZED_HORIZ:Number = 4;  //100
	/**
	 * @see #setState()
	 */
	public static var MAXIMIZED_VERT:Number = 8;  //1000
	/**
	 * @see #setState()
	 */
	public static var MAXIMIZED:Number = 12;  //1100
	//-----------------------------------------
	
	/**
	 * @see #setDefaultCloseOperation()
	 */
	public static var DO_NOTHING_ON_CLOSE:Number = 0;
	/**
	 * @see #setDefaultCloseOperation()
	 */
	public static var HIDE_ON_CLOSE:Number = 1;
	/**
	 * @see #setDefaultCloseOperation()
	 */
	public static var DISPOSE_ON_CLOSE:Number = 2;
		
	//--------------------------------------------------------
	
	private var title:String;
	private var icon:Icon;
	private var state:Number;
	private var defaultCloseOperation:Number;
	private var maximizedBounds:Rectangle;
	
	private var dragable:Boolean;
	private var resizable:Boolean;
	private var closable:Boolean;
	private var dragDirectly:Boolean;
	
	private var resizer:Resizer;
	
	/**
	 * JFrame(title:String, modal:Boolean)<br>
	 * JFrame(title:String)<br>
	 * JFrame(owner:JWindow, title:String, modal:Boolean)<br>
	 * JFrame(owner:JWindow, title:String)<br>
	 * JFrame(owner:JWindow)<br>
	 * JFrame(owner:MovieClip, title:String, modal:Boolean)<br>
	 * JFrame(owner:MovieClip, title:String)<br>
	 * JFrame(owner:MovieClip)<br>
	 * JFrame()<br>
	 * Constructs a new frame that is initially invisible.
	 * @param owner the owner of this window, a <code>JWindow</code> 
	 * or a <code>MovieClip</code>, default is <code>ASWingUtils.getRootMovieClip()</code> 
	 * @param title the String to display in the dialog's title bar. default is undefined.
	 * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time,
	 *  default is false.
	 * @see org.aswing.JWindow
	 * @see org.aswing.ASWingUtils#getRootMovieClip()
	 */	
	public function JFrame(owner, title, modal : Boolean) {
		super(StringUtils.isString(title) ? owner : (StringUtils.isString(owner) ? undefined : owner), //judge owner parameter for JWindow
		 	  StringUtils.isString(title) ? modal : title); //judge modal parameter for JWindow
		 	  
		if(StringUtils.isString(title)){
			this.title = StringUtils.castString(title);
		}else if(StringUtils.isString(owner)){
			this.title = StringUtils.castString(owner);
		}
		
		state = NORMAL;
		defaultCloseOperation = HIDE_ON_CLOSE;
		dragable  = true;
		resizable = true;
		closable  = true;
		
		setName("JFrame");
		
		updateUINow = true;
		updateUI();
	}
	
	private var updateUINow:Boolean;
	public function updateUI():Void{
		if(updateUINow){
    		setUI(WindowUI(UIManager.getUI(this)));
		}
    }
    
	public function getUIClassID():String{
		return "FrameUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingFrameUI;
    }
		
	/**
	 * Sets the text to be displayed in the title bar for this frame.
	 * @param t the text to be displayed in the title bar, 
	 * null to display no text in the title bar.
	 */
	public function setTitle(t:String):Void{
		if(title != t){
			title = t;
			repaint();
			revalidate();
			WindowLayout(getLayout()).getTitleBar().repaint();
		}
	}
	
	/**
	 * Returns the text displayed in the title bar for this frame.
	 * @return the text displayed in the title bar for this frame.
	 */
	public function getTitle():String{
		return title;
	}
	
	/**
	 * Sets the icon to be displayed in the title bar for this frame.
	 * @param ico the icon to be displayed in the title bar, 
	 * null to display no icon in the title bar.
	 */	
	public function setIcon(ico:Icon):Void{
		if(icon != ico){
			icon = ico;
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns the icon displayed in the title bar for this frame.
	 * @return the icon displayed in the title bar for this frame.
	 */
	public function getIcon():Icon{
		return icon;
	}
		
	/**
	 * Sets whether this frame is resizable by the user.
	 * 
	 * <p>"resizable" means include capability of restore normal resize, maximize, iconified and resize by drag.
	 * @param b true user can resize the frame by click resize buttons or drag to scale the frame, false user can't.
	 * @see #isResizable()
	 */
	public function setResizable(b:Boolean):Void{
		if(resizable != b){
			resizable = b;
			getResizer().setEnabled(b);
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns whether this frame is resizable by the user. By default, all frames are initially resizable. 
	 * 
	 * <p>"resizable" means include capability of restore normal resize, maximize, iconified and resize by drag.
	 * @see #setResizable()
	 */
	public function isResizable():Boolean{
		return resizable;
	}
	
	/**
	 * Sets whether this frame can be dragged by the user.  By default, it's true.
	 * 
	 * <p>"dragable" means drag to move the frame.
	 * @param b 
	 * @see #isDragable()
	 */
	public function setDragable(b:Boolean):Void{
		if(dragable != b){
			dragable = b;
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns whether this frame can be dragged by the user. By default, it's true.
	 * @see #setDragnable()
	 */
	public function isDragable():Boolean{
		return dragable;
	}
	

	/**
	 * Sets whether this frame can be closed by the user. By default, it's true.
	 * Whether the frame will be hide or dispose, depend on the value returned by <code>getDefaultCloseOperation</code>.
	 * @param b true user can click close button to generate the close event, false user can't.
	 * @see #getClosable()
	 */	
	public function setClosable(b:Boolean):Void{
		if(closable != b){
			closable = b;
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns whether this frame can be closed by the user. By default, it's true.
	 * @see #setClosable()
	 */		
	public function isClosable():Boolean{
		return closable;
	}
	
	/**
	 * Only did effect when state is <code>NORMAL</code>
	 */
	public function pack():Void{
		if(getState() == NORMAL){
			super.pack();
		}
	}	
	
	/**
	 * Gets maximized bounds for this frame.<br>
	 * If the maximizedBounds was setted by setMaximizedBounds it will return the setted value.
	 * else if the owner is a JWindow it will return the owner's content pane's bounds, if
	 * the owner is a movieclip it will return the movie's stage bounds.
	 */
	public function getMaximizedBounds():Rectangle{
		if(maximizedBounds == null){
			return ASWingUtils.getVisibleMaximizedBounds(root_mc._parent);
		}else{
			return new Rectangle(maximizedBounds);
		}
	}
	
	/**
	 * Sets the maximized bounds for this frame. 
	 * <br>
	 * @param b bounds for the maximized state, null to back to use default bounds descripted in getMaximizedBounds's comments.
	 * @see #getMaximizedBounds()
	 */
	public function setMaximizedBounds(b:Rectangle):Void{
		if(b != null){
			maximizedBounds = new Rectangle(b);
			revalidate();
		}else{
			maximizedBounds = null;
		}
	}	

    /**                   
     * Sets the operation that will happen by default when
     * the user initiates a "close" on this frame.
     * You must specify one of the following choices:
     * <p>
     * <ul>
     * <li><code>DO_NOTHING_ON_CLOSE</code>
     * (defined in <code>WindowConstants</code>):
     * Don't do anything; require the
     * program to handle the operation in the <code>windowClosing</code>
     * method of a registered EventListener object.
     *
     * <li><code>HIDE_ON_CLOSE</code>
     * (defined in <code>WindowConstants</code>):
     * Automatically hide the frame after
     * invoking any registered EventListener objects.
     *
     * <li><code>DISPOSE_ON_CLOSE</code>
     * (defined in <code>WindowConstants</code>):
     * Automatically hide and dispose the 
     * frame after invoking any registered EventListener objects.
     * </ul>
     * <p>
     * The value is set to <code>HIDE_ON_CLOSE</code> by default.
     * if you set a value is not three of them, think of it is will be changed to default value.
     * @param operation the operation which should be performed when the
     *        user closes the frame
     * @see org.aswing.Component#addEventListener()
     * @see #getDefaultCloseOperation()
     */
    public function setDefaultCloseOperation(operation:Number):Void {
    	if(operation != DO_NOTHING_ON_CLOSE 
    		&& operation != HIDE_ON_CLOSE
    		&& operation != DISPOSE_ON_CLOSE)
    	{
    			operation = HIDE_ON_CLOSE;
    	}
    	defaultCloseOperation = operation;
    }
    
	/**
	 * Returns the operation that will happen by default when
     * the user initiates a "close" on this frame.
	 * @see #setDefaultCloseOperation()
	 */
	public function getDefaultCloseOperation():Number{
		return defaultCloseOperation;
	}
	
	public function setState(s:Number):Void{
		if(state != s){
			state = s;
			fireStateChanged();
			if(state == ICONIFIED){
				dispatchEvent(ON_WINDOW_ICONIFIED, createEventObj(ON_WINDOW_ICONIFIED));
			}else if((state & MAXIMIZED_HORIZ == MAXIMIZED_HORIZ) || (state & MAXIMIZED_VERT == MAXIMIZED_VERT)){
				dispatchEvent(ON_WINDOW_MAXIMIZED, createEventObj(ON_WINDOW_MAXIMIZED));
			}else{
				dispatchEvent(ON_WINDOW_RESTORED, createEventObj(ON_WINDOW_RESTORED));
			}
			return;
		}
	}
	
	public function getState():Number{
		return state;
	}
	
	public function setResizer(r:Resizer):Void{
		if(r != resizer && r != null){
			resizer = r;
			ResizerController.init(this, r);
			r.setEnabled(isResizable());
		}
	}
	
	public function getResizer():Resizer{
		return resizer;
	}
	
	/**
	 * Indicate whether need resize frame directly when drag the resizer arrow.
	 * if set to false, there will be a rectange to represent then size what will be resized to.
	 * if set to true, the frame will be resize directly when drag, but this is need more cpu counting.<br>
	 * Default is false.
	 * @see org.aswing.Resizer#setResizeDirectly()
	 */
	public function setResizeDirectly(b:Boolean):Void{
		resizer.setResizeDirectly(b);
	}
	
	/**
	 * Return whether need resize frame directly when drag the resizer arrow.
	 * @see #setResizeDirectly()
	 */
	public function isResizeDirectly():Boolean{
		return resizer.isResizeDirectly();
	}
	
	/**
	 * Indicate whether need move frame directly when drag the frame.
	 * if set to false, there will be a rectange to represent then bounds what will be move to.
	 * if set to true, the frame will be move directly when drag, but this is need more cpu counting.<br>
	 * Default is false.
	 */	
	public function setDragDirectly(b:Boolean):Void{
		dragDirectly = b;
	}
	
	/**
	 * Return whether need move frame directly when drag the frame.
	 * @see #setDragDirectly()
	 */	
	public function isDragDirectly():Boolean{
		return dragDirectly;
	}
	
	/**
	 * User pressed close button to close the Frame depend on the <code>defaultCloseOperation</code>
	 * <p>
	 * This method will fire a ON_WINDOW_CLOSING event.
	 * @see #tryToClose()
	 */
	public function closeReleased():Void{
		dispatchEvent(ON_WINDOW_CLOSING, createEventObj(ON_WINDOW_CLOSING));
		tryToClose();
	}
	
	/**
	 * Try to close the Frame depend on the <code>defaultCloseOperation</code>
	 * @see #closeReleased()
	 */
	public function tryToClose():Void{
		if(defaultCloseOperation == HIDE_ON_CLOSE){
			hide();
		}else if(defaultCloseOperation == DISPOSE_ON_CLOSE){
			dispose();
		}		
	}
}
