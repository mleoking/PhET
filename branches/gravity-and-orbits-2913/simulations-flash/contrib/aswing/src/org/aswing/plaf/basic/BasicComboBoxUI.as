import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.border.LineBorder;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.ElementCreater;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JComboBox;
import org.aswing.overflow.JList;
import org.aswing.JScrollPane;
import org.aswing.LayoutManager;
import org.aswing.LookAndFeel;
import org.aswing.MCPanel;
import org.aswing.plaf.basic.icon.ArrowIcon;
import org.aswing.plaf.ComboBoxUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.Timer;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicComboBoxUI extends ComboBoxUI implements LayoutManager{
		
	private var dropDownButton:Component;
	private var box:JComboBox;
	private var mcPane:MCPanel;
	private var maskMC:MovieClip;
	private var scollPane:JScrollPane;
	private var mouseListener:Object;
	private var listListener:Object;
	private var boxListener:Object;
	private var editorListener:Object;
	private var containerListener:Object;
	private var oldRequestFocusMethod:Function;
    private var arrowShadowColor:ASColor;
    private var arrowLightColor:ASColor;
	
	private var popupTimer:Timer;
	private var popupDestinationY:Number;
	private var moveDir:Number;
		
	public function BasicComboBoxUI() {
		super();
	}
	
    public function installUI(c:Component):Void{
    	box = JComboBox(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
    	box = JComboBox(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():Void{
		var pp:String = "ComboBox.";
        LookAndFeel.installBorder(box, pp + "border");
        LookAndFeel.installColorsAndFont(box, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBasicProperties(box, pp);
		arrowShadowColor = UIManager.getColor("ComboBox.arrowShadowColor");
		arrowLightColor = UIManager.getColor("ComboBox.arrowLightColor");
        box.setLayout(this);
	}
    
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(box);
    }
    
	private function installComponents():Void{
		dropDownButton = createDropDownButton();
		box.append(dropDownButton);
		oldRequestFocusMethod = box.requestFocus;
		box.requestFocus = Delegate.create(this, __requestFocus);
    }
	private function uninstallComponents():Void{
		box.remove(dropDownButton);
		box.requestFocus = oldRequestFocusMethod;
    }
	
	private var dropDownListener:Object;
	private function installListeners():Void{
		dropDownListener = new Object();
		dropDownListener[Component.ON_ACT]      = Delegate.create(this, __onDropDownPressed);
		dropDownListener[Component.ON_PRESS]    = Delegate.create(this, __onDropDownPressed);
		dropDownListener[Component.ON_KEY_DOWN] = Delegate.create(this, __onKeyDownFromEditor);
		dropDownButton.addEventListener(dropDownListener);
		
		mouseListener = {onMouseDown:Delegate.create(this, __onMouseDown)};
		
		listListener = new Object();
		listListener[JList.ON_ITEM_RELEASE] = listListener[JList.ON_ITEM_RELEASEOUTSIDE] = Delegate.create(this, __listItemReleased);
		listListener[JList.ON_KEY_DOWN] = Delegate.create(this, __onKeyDownFromList);
		getPopupList().addEventListener(listListener);
		
		boxListener = box.addEventListener(Component.ON_PRESS, __onDropDownPressed, this);
		
		popupTimer = new Timer(40);
		popupTimer.addActionListener(__movePopup, this);
		
		containerListener = new Object();
		containerListener[Container.ON_COM_ADDED]   = Delegate.create(this, __onChildAdded);
		containerListener[Container.ON_COM_REMOVED] = Delegate.create(this, __onChildRemoved);
		box.addEventListener(containerListener);
		
		editorListener = new Object();
		editorListener[Component.ON_KEY_DOWN] = Delegate.create(this, __onKeyDownFromEditor);
		if(box.getEditor().getEditorComponent() != undefined){
			box.getEditor().getEditorComponent().addEventListener(editorListener);
		}
	}
    
    private function uninstallListeners():Void{
    	dropDownButton.removeEventListener(dropDownListener);
    	getPopupList().removeEventListener(listListener);
    	box.removeEventListener(boxListener);
    	popupTimer.stop();
    	popupTimer = null;
    }
        
    private function paintBackGround(c:Component, g:Graphics, b:Rectangle):Void{
    	if(c.isOpaque()){
	 		var bgColor:ASColor;
	 		bgColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
	 		if(!box.isEnabled()){
	 			bgColor = bgColor.darker();
	 		}
			g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
    	}
    }
    
    private function __requestFocus():Void{
    }
    
    /**
     * Just override this method if you want other LAF drop down buttons.
     */
    private function createDropDownButton():Component{
    	var btn:JButton = new JButton(new ArrowIcon(
    				Math.PI/2, 8,
				    box.getBackground(),
				    arrowLightColor,
				    arrowShadowColor,
				    box.getBackground()
    	));
    	btn.setPreferredSize(16, 16);
    	return btn;
    }
    
    private function getScollPane():JScrollPane{
    	if(scollPane == null){
    		scollPane = new JScrollPane(getPopupList());
    		scollPane.setBorder(new LineBorder());
    		scollPane.setOpaque(true);
    	}
    	return scollPane;
    }
    private function getPopupList():JList{
    	return box.getPopupList();
    }
    private function viewPopup():Void{
    	var popupPane:JScrollPane = getScollPane();
    	if(mcPane==null || !mcPane.contains(popupPane)){
    		mcPane = new MCPanel(box.getComponentRootAncestorMovieClip(), 10000, 10000);
    		mcPane.append(popupPane);
			var paneMC:MovieClip = mcPane.getPanelMC();
			var width:Number = box.getWidth();
			var cellHeight:Number;
			if(box.getListCellFactory().isAllCellHasSameHeight()){
				cellHeight = box.getListCellFactory().getCellHeight();
			}else{
				cellHeight = box.getPreferredSize().height;
			}
			var height:Number = Math.min(box.getItemCount(), box.getMaximumRowCount())*cellHeight;
			var i:Insets = getScollPane().getInsets();
			height += i.top + i.bottom;
			i = getPopupList().getInsets();
			height += i.top + i.bottom;
			popupPane.setSize(width, height);
			//trace("popupPane.setSize(width, height) : " + width + ", " + height);
			var p:Point = startMoveToView(height);
			Mouse.removeListener(mouseListener);
			Mouse.addListener(mouseListener);
			dropDownButton.setTriggerEnabled(false);
			box.setTriggerEnabled(false);
			
			//hack to get ScrollPane root mc
			var o:Object = popupPane;
			var popupPaneMC:MovieClip = MovieClip(o.root_mc);
			maskMC = ElementCreater.getInstance().createMC(paneMC, "comb_pop_mask");
			if(popupPaneMC != undefined){
				var g:Graphics = new Graphics(maskMC);
				maskMC._parent.globalToLocal(p);
				g.fillRectangle(new SolidBrush(0), p.x, p.y, width, height);
				popupPaneMC.setMask(maskMC);
				getPopupList().requestFocus();
			}else{
				trace("Mask popup list failed!");
				maskMC.removeMovieClip();
				maskMC = null;
			}
    	}
    }
    private function hidePopup():Void{
    	mcPane.remove(getScollPane());
    	maskMC.unloadMovie();
    	maskMC.removeMovieClip();
    	maskMC = null;
		Mouse.removeListener(mouseListener);
		dropDownButton.setTriggerEnabled(true);
		box.setTriggerEnabled(true);
		popupTimer.stop();
		returnFocusFromListToCombobox();
    }
    //return the destination pos
    private function startMoveToView(height:Number):Point{
    	var popupPane:JScrollPane = getScollPane();
    	var popupPaneHeight:Number = height;
    	var downDest:Point = box.componentToGlobal(new Point(0, box.getHeight()));
    	var upDest:Point = new Point(downDest.x, downDest.y - box.getHeight() - popupPaneHeight);
    	var visibleBounds:Rectangle = ASWingUtils.getVisibleMaximizedBounds();
    	//visibleBounds.setLocation(box.componentToGlobal(visibleBounds.getLocation()));
    	var distToBottom:Number = visibleBounds.y + visibleBounds.height - downDest.y - popupPaneHeight;
    	var distToTop:Number = upDest.y - visibleBounds.y;
    	var dest:Point;
    	if(distToBottom > 0 || (distToBottom < 0 && distToTop < 0 && distToBottom > distToTop)){
    		moveDir = 1;
			popupDestinationY = downDest.y;
			popupPane.setGlobalLocation(downDest.x, popupDestinationY - popupPaneHeight);
			dest = downDest;
    	}else{
    		moveDir = -1;
			popupDestinationY = upDest.y;
			popupPane.setGlobalLocation(upDest.x, popupDestinationY + popupPaneHeight);
			dest = upDest;
    	}
    	
		popupTimer.restart();
		
		return new Point(dest);
    }
    private function setComboBoxValueFromListSelection():Void{
		var selectedValue:Object = getPopupList().getSelectedValue();
		box.setSelectedItem(selectedValue);
    }
    //-----------------------------
    private function __movePopup():Void{
    	var popupPane:JScrollPane = getScollPane();
    	var popupPaneHeight:Number = popupPane.getHeight();
    	var maxTime:Number = 10;
    	var minTime:Number = 3;
    	var speed:Number = 50;
    	if(popupPaneHeight < speed*minTime){
    		speed = Math.ceil(popupPaneHeight/minTime);
    	}else if(popupPaneHeight > speed*maxTime){
    		speed = Math.ceil(popupPaneHeight/maxTime);
    	}
		var p:Point = popupPane.getGlobalLocation();
		if(Math.abs(popupDestinationY - p.y) < speed){
			p.y = popupDestinationY;
			popupPane.setGlobalLocation(p);
			popupTimer.stop();
			getPopupList().ensureIndexIsVisible(getPopupList().getSelectedIndex());
    		getPopupList().requestFocus();//ensure the focus again
		}else if(moveDir > 0){
			p.y += speed;
			popupPane.setGlobalLocation(p);
		}else{
			p.y -= speed;
			popupPane.setGlobalLocation(p);
		}
		//trace("popupPane global pos : " + p);
		//trace("popupPane = " + popupPane);
		popupPane.revalidate();
		updateAfterEvent();
    }
    private function __listItemReleased():Void{
    	hidePopup();
    	setComboBoxValueFromListSelection();
    }
    private function __onDropDownPressed():Void{
    	//trace("__onDropDownPressed");
    	if(!isPopupVisible(box)){
    		//trace("__onDropDownPressed set pop up");
    		setPopupVisible(box, true);
    	}
    }
    private function __onMouseDown():Void{
    	if(popupTimer.isRunning() || !getScollPane().hitTestMouse()){
    		//trace("!getScollPane().hitTestMouse() hide pop up");
    		hidePopup();
    	}
    }
    
    private function __onChildAdded(source:Container, com:Component):Void{
    	if(com == box.getEditor().getEditorComponent()){
    		com.addEventListener(editorListener);
    	}
    }
    private function __onChildRemoved(source:Container, com:Component):Void{
    	if(com == box.getEditor().getEditorComponent()){
    		com.removeEventListener(editorListener);
    	}
    }
    private function __onKeyDownFromEditor():Void{
		if(!box.isEnabled()){
			return;
		}
    	if(Key.getCode() == Key.DOWN){
    		FocusManager.getCurrentManager().setTraversing(true);
    		setPopupVisible(box, true);
    	}
    }
    private function __onKeyDownFromList():Void{
    	if(Key.getCode() == Key.ENTER){
    		setPopupVisible(box, false);
    		setComboBoxValueFromListSelection();
    	}else if(Key.getCode() == Key.BACKSPACE || Key.getCode() == Key.ESCAPE){
    		setPopupVisible(box, false);
    		getPopupList().setSelectedValue(box.getSelectedItem());
    	}
    }
    
    private function returnFocusFromListToCombobox():Void{
    	if(dropDownButton.hitTestMouse()){
    		dropDownButton.requestFocus();
    		if(dropDownButton.isFocusOwner()){
    			return;
    		}
    	}
    	var editorCom:Component = box.getEditor().getEditorComponent();
    	if(editorCom.isEnabled()){
    		editorCom.requestFocus();
    		if(editorCom.isFocusOwner()){
    			return;
    		}
    	}
		dropDownButton.requestFocus();
		if(!dropDownButton.isFocusOwner()){
			box.transferFocus();
		}
    }
    
	/**
     * Set the visiblity of the popup
     */
	public function setPopupVisible(c:JComboBox, v:Boolean):Void{
		if(v){
			viewPopup();
		}else{
			hidePopup();
		}
	}
	
	/** 
     * Determine the visibility of the popup
     */
	public function isPopupVisible(c:JComboBox):Boolean{
		return getScollPane().isShowing();
	}
	
	/** 
     * Determine whether or not the combo box itself is traversable 
     */
	public function isFocusTraversable(c:JComboBox):Boolean{
		return false;
	}
	
	//---------------------Layout Implementation---------------------------

    /**
     * may need override in subclass
     */
    public function addLayoutComponent(comp:Component, constraints:Object):Void{
    }

    /**
     * may need override in subclass
     */
    public function removeLayoutComponent(comp:Component):Void{
    }
	
    public function preferredLayoutSize(target:Container):Dimension{
    	var insets:Insets = box.getInsets();
    	var listPreferSize:Dimension = getPopupList().getPreferredSize();
    	var ew:Number = listPreferSize.width;
    	var wh:Number = box.getEditor().getEditorComponent().getPreferredSize().height;
    	var buttonSize:Dimension = dropDownButton.getPreferredSize(); 
    	buttonSize.width += ew;
    	if(wh > buttonSize.height){
    		buttonSize.height = wh;
    	}
    	return insets.getOutsideSize(buttonSize);
    }

    public function minimumLayoutSize(target:Container):Dimension{
    	return box.getInsets().getOutsideSize(dropDownButton.getPreferredSize());
    }
	
	/**
     * may need override in subclass
	 */
    public function maximumLayoutSize(target:Container):Dimension{
    	return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }
    
    /**
     * may need override in subclass
     */
    public function layoutContainer(target:Container):Void{
    	var td:Dimension = target.getSize();
		var insets:Insets = target.getInsets();
		var top:Number = insets.top;
		var bottom:Number = td.height - insets.bottom;
		var left:Number = insets.left;
		var right:Number = td.width - insets.right;
		
		var height:Number = td.height - insets.top - insets.bottom;
    	var buttonSize:Dimension = dropDownButton.getPreferredSize(); 
    	dropDownButton.setSize(buttonSize.width, height);
    	dropDownButton.setLocation(right - buttonSize.width, top);
    	box.getEditor().getEditorComponent().setLocation(left, top);
    	box.getEditor().getEditorComponent().setSize(td.width-insets.left-insets.right- buttonSize.width, height);
    }
    
	/**
     * may need override in subclass
	 */
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0.5;
    }

	/**
     * may need override in subclass
	 */
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0.5;
    }

    /**
     * may need override in subclass
     */
    public function invalidateLayout(target:Container):Void{
    }	
}