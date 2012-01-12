/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{
	
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.ui.Keyboard;
import flash.utils.Timer;

import org.aswing.*;
import org.aswing.event.FocusKeyEvent;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;
import org.aswing.plaf.basic.accordion.*;
import org.aswing.plaf.basic.tabbedpane.Tab;

/**
 * Basic accordion ui imp.
 * @author iiley
 * @private
 */
public class BasicAccordionUI extends BaseComponentUI implements LayoutManager{
	
	private static const MOTION_SPEED:int = 50;
	
	protected var accordion:JAccordion;
	protected var headers:Array;
	protected var motionTimer:Timer;
	protected var headerDestinations:Array;
	protected var childrenDestinations:Array;
	protected var childrenOrderYs:Array;
	protected var destSize:IntDimension;
	protected var motionSpeed:int;
	
	protected var headerContainer:Sprite;
	
	public function BasicAccordionUI(){
		super();
	}
	
    override public function installUI(c:Component):void{
    	headers = new Array();
    	destSize = new IntDimension();
    	accordion = JAccordion(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
    	accordion = JAccordion(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
    
	protected function getPropertyPrefix():String {
		return "Accordion.";
	}
	
	protected function installDefaults():void{
		accordion.setLayout(this);
		var pp:String = getPropertyPrefix();
        LookAndFeel.installBorderAndBFDecorators(accordion, pp);
        LookAndFeel.installColorsAndFont(accordion, pp);
        LookAndFeel.installBasicProperties(accordion, pp);
        motionSpeed = getInt(pp + "motionSpeed");
        if(motionSpeed <=0 || isNaN(motionSpeed)){
        	motionSpeed = MOTION_SPEED;
        }
       	var tabMargin:Insets = getInsets(pp + "tabMargin");
		if(tabMargin == null){
			tabMargin = new InsetsUIResource(1, 1, 1, 1);	
		}
		var i:Insets = accordion.getMargin();
		if (i == null || i is UIResource) {
	    	accordion.setMargin(tabMargin);
		}
	}
    
    protected function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(accordion);
    }
    
	protected function installComponents():void{
		headerContainer = new Sprite();
		headerContainer.tabEnabled = false;
		accordion.addChild(headerContainer);
		synTabs();
		synHeaderProperties();
    }
    
	protected function uninstallComponents():void{
		for(var i:int=0; i<headers.length; i++){
			var header:Tab = getHeader(i);
			headerContainer.removeChild(header.getTabComponent());
    		header.getTabComponent().removeEventListener(MouseEvent.CLICK, __tabClick);
		}
		headers.splice(0);
		accordion.removeChild(headerContainer);
    }
	
	protected function installListeners():void{
		accordion.addStateListener(__onSelectionChanged);
		accordion.addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
		motionTimer = new Timer(40);
		motionTimer.addEventListener(TimerEvent.TIMER, __onMotion);
	}
    
    protected function uninstallListeners():void{
		accordion.removeStateListener(__onSelectionChanged);
		accordion.removeEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onKeyDown);
		motionTimer.stop();
		motionTimer = null;
    }
    
   	override public function paintFocus(c:Component, g:Graphics2D, b:IntRectangle):void{
    	var header:Tab = getSelectedHeader();
    	if(header != null){
    		header.getTabComponent().paintFocusRect(true);
    	}else{
    		super.paintFocus(c, g, b);
    	}
    } 
    
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
    	super.paint(c, g, b);
    }
    
    /**
     * Just override this method if you want other LAF headers.
     */
    protected function createNewHeader():Tab{
    	var header:Tab = getInstance(getPropertyPrefix() + "header") as Tab;
    	if(header == null){
    		header = new BasicAccordionHeader();
    	}
    	header.initTab(accordion);
    	header.getTabComponent().setFocusable(false);
    	return header;
    }
        
    protected function getHeader(i:int):Tab{
    	return Tab(headers[i]);
    }

    protected function synTabs():void{
    	var comCount:int = accordion.getComponentCount();
    	if(comCount != headers.length){
    		var i:int;
    		var header:Tab;
    		if(comCount > headers.length){
    			for(i = headers.length; i<comCount; i++){
    				header = createNewHeader();
    				header.setTextAndIcon(accordion.getTitleAt(i), accordion.getIconAt(i));
    				setHeaderProperties(header);
    				header.getTabComponent().setToolTipText(accordion.getTipAt(i));
    				header.getTabComponent().addEventListener(MouseEvent.CLICK, __tabClick);
    				headerContainer.addChild(header.getTabComponent());
    				headers.push(header);
    			}
    		}else{
    			for(i = headers.length-comCount; i>0; i--){
    				header = Tab(headers.pop());
    				header.getTabComponent().removeEventListener(MouseEvent.CLICK, __tabClick);
    				headerContainer.removeChild(header.getTabComponent());
    			}
    		}
    	}
    }
        
    protected function synHeaderProperties():void{
    	for(var i:int=0; i<headers.length; i++){
    		var header:Tab = getHeader(i);
    		header.setTextAndIcon(accordion.getTitleAt(i), accordion.getIconAt(i));
    		setHeaderProperties(header);
    		header.getTabComponent().setUIElement(true);
    		header.getTabComponent().setEnabled(accordion.isEnabledAt(i));
    		header.getTabComponent().setVisible(accordion.isVisibleAt(i));
    		header.getTabComponent().setToolTipText(accordion.getTipAt(i));
    	}
    }
    
    protected function setHeaderProperties(header:Tab):void{
    	header.setHorizontalAlignment(accordion.getHorizontalAlignment());
    	header.setHorizontalTextPosition(accordion.getHorizontalTextPosition());
    	header.setIconTextGap(accordion.getIconTextGap());
    	header.setMargin(accordion.getMargin());
    	header.setVerticalAlignment(accordion.getVerticalAlignment());
    	header.setVerticalTextPosition(accordion.getVerticalTextPosition());
    	header.setFont(accordion.getFont());
    	header.setForeground(accordion.getForeground());
    }
    
    protected function ensureHeadersOnTopDepths():void{
    	accordion.bringToTop(headerContainer);
    }
    
    protected function getSelectedHeader():Tab{
    	if(accordion.getSelectedIndex() >= 0){
    		return getHeader(accordion.getSelectedIndex());
    	}else{
    		return null;
    	}
    }
    
    protected function indexOfHeaderComponent(tab:Component):int{
    	for(var i:int=0; i<headers.length; i++){
    		if(getHeader(i).getTabComponent() == tab){
    			return i;
    		}
    	}
    	return -1;
    }
    
    //------------------------------Handlers--------------------------------
    
    private function __tabClick(e:Event):void{
    	accordion.setSelectedIndex(indexOfHeaderComponent(e.currentTarget as Component));
    }
    
    private function __onSelectionChanged(e:Event):void{
    	accordion.revalidate();
    	accordion.repaint();
    }
    
    private function __onKeyDown(e:FocusKeyEvent):void{
    	if(headers.length > 0){
    		var n:int = accordion.getComponentCount();
    		var code:uint = e.keyCode;
    		var index:int;
	    	if(code == Keyboard.DOWN){
	    		setTraversingTrue();
		    	index = accordion.getSelectedIndex();
		    	index++;
		    	while(index<n && (!accordion.isEnabledAt(index) || !accordion.isVisibleAt(index))){
		    		index++;
		    	}
		    	if(index >= n){
		    		return;
		    	}
		    	accordion.setSelectedIndex(index);
	    	}else if(code == Keyboard.UP){
	    		setTraversingTrue();
		    	index = accordion.getSelectedIndex();
		    	index--;
		    	while(index >= 0 && (!accordion.isEnabledAt(index) || !accordion.isVisibleAt(index))){
		    		index--;
		    	}
		    	if(index < 0){
		    		return;
		    	}
		    	accordion.setSelectedIndex(index);
	    	}
    	}
    }
    
    protected function setTraversingTrue():void{
    	var fm:FocusManager = FocusManager.getManager(accordion.stage);
    	if(fm){
    		fm.setTraversing(true);
    	}
    }
    
    private function __onMotion(e:TimerEvent):void{
    	var isFinished:Boolean = true;
    	var n:int = headerDestinations.length;
    	var selected:int = accordion.getSelectedIndex();
    	var i:int = 0;
    	var child:Component;
    	
    	for(i=0; i<n; i++){
    		var header:Tab = getHeader(i);
    		var tab:Component = header.getTabComponent();
    		var curY:int = tab.getY();
    		var desY:int = headerDestinations[i];
    		var toY:int;
    		if(Math.abs(desY - curY) <= motionSpeed){
    			toY = desY;
    		}else{
    			if(desY > curY){
    				toY = curY + motionSpeed;
    			}else{
    				toY = curY - motionSpeed;
    			}
    			isFinished = false;
    		}
    		tab.setLocationXY(tab.getX(), toY);
    		tab.validate();
    		child = accordion.getComponent(i);
    		child.setLocationXY(child.getX(), toY + tab.getHeight());
    	}
    	
    	adjustClipSizes();
    	
    	if(isFinished){
    		motionTimer.stop();
    		for(i=0; i<n; i++){
	    		child = accordion.getComponent(i);
	    		if(selected == i){
	    			child.setVisible(true);
	    		}else{
	    			child.setVisible(false);
	    		}
    		}
    	}
    	
    	for(i=0; i<n; i++){
    		child = accordion.getComponent(i);
    		child.validate();
    	}
    	if(e != null)
    		e.updateAfterEvent();
    }
    
    private function adjustClipSizes():void{
    	var n:int = headerDestinations.length;
    	for(var i:int=0; i<n; i++){
    		var child:Component = accordion.getComponent(i);
    		var orderY:int = childrenOrderYs[i];
    		if(child.isVisible()){
    			child.setClipSize(new IntDimension(destSize.width, destSize.height - (child.getY()-orderY)));
    		}
    	}
    }
    
	//---------------------------LayoutManager Imp-------------------------------
	
	public function addLayoutComponent(comp:Component, constraints:Object):void{
		synTabs();
	}
	
	public function removeLayoutComponent(comp:Component):void{
		synTabs();
	}
	
	public function invalidateLayout(target:Container):void{
	}
	
	public function layoutContainer(target:Container):void{
    	synHeaderProperties();
    	
    	var insets:Insets = accordion.getInsets();
    	var i:int = 0;
    	var x:int = insets.left;
    	var y:int = insets.top;
    	var w:int = accordion.getWidth() - x - insets.right;
    	var h:int = accordion.getHeight() - y - insets.bottom;
		var header:Tab;
		var tab:Component;
		var size:IntDimension;
		
    	var count:int = accordion.getComponentCount();
    	var selected:int = accordion.getSelectedIndex();
    	if(selected < 0){
    		if(count > 0){
    			accordion.setSelectedIndex(0);
    		}
    		return;
    	}
    	
    	headerDestinations = new Array(count);
    	childrenOrderYs = new Array(count);
    	
    	var vX:int, vY:int, vWidth:int, vHeight:int;
    	vHeight = h;
    	vWidth = w;
    	vX = x;
    	for(i=0; i<=selected; i++){
    		if (!accordion.isVisibleAt(i)) continue;
    		header = getHeader(i);
    		tab = header.getTabComponent();
    		size = tab.getPreferredSize();
    		tab.setSizeWH(w, size.height);
    		tab.setLocationXY(x, tab.getY());
    		accordion.getComponent(i).setLocationXY(x, tab.getY()+size.height);
    		headerDestinations[i] = y;
    		y += size.height;
    		childrenOrderYs[i] = y;
    		vHeight -= size.height;
    		if(i == selected){
    			header.setSelected(true);
    			accordion.getComponent(i).setVisible(true);
    		}else{
    			header.setSelected(false);
    		}
    		tab.validate();
    	}
    	vY = y;
    	for(i=selected+1; i<count; i++){
    		if (!accordion.isVisibleAt(i)) continue;
    		header = getHeader(i);
    		tab = header.getTabComponent();
    		y += tab.getPreferredSize().height;
    		childrenOrderYs[i] = y;
    	}
    	
    	y = accordion.getHeight() - insets.bottom;
    	for(i=count-1; i>selected; i--){
    		if (!accordion.isVisibleAt(i)) continue;
    		header = getHeader(i);
    		tab = header.getTabComponent();
    		size = tab.getPreferredSize();
    		y -= size.height;
    		headerDestinations[i] = y;
    		tab.setSizeWH(w, size.height);
    		tab.setLocationXY(x, tab.getY());
    		accordion.getComponent(i).setLocationXY(x, tab.getY()+size.height);
    		header.setSelected(false);
    		vHeight -= size.height;
    		tab.validate();
    	}
    	destSize.setSizeWH(vWidth, vHeight);
    	for(i=0; i<count; i++){
    		if (!accordion.isVisibleAt(i)) continue;
    		if(accordion.getComponent(i).isVisible()){
    			accordion.getComponent(i).setSize(destSize);
    		}
    	}
    	motionTimer.start();
    	__onMotion(null);
    	ensureHeadersOnTopDepths();
	}
	
	public function preferredLayoutSize(target:Container):IntDimension{
    	if(target === accordion){
    		synHeaderProperties();
    		
	    	var insets:Insets = accordion.getInsets();
	    	
	    	var w:int = 0;
	    	var h:int = 0;
	    	var i:int = 0;
	    	var size:IntDimension;
	    	
	    	for(i=accordion.getComponentCount()-1; i>=0; i--){
	    		size = accordion.getComponent(i).getPreferredSize();
	    		w = Math.max(w, size.width);
	    		h = Math.max(h, size.height);
	    	}
	    	
	    	for(i=accordion.getComponentCount()-1; i>=0; i--){
	    		size = getHeader(i).getTabComponent().getPreferredSize();
	    		w = Math.max(w, size.width);
	    		h += size.height;
	    	}
	    	
	    	return insets.getOutsideSize(new IntDimension(w, h));
    	}
    	return null;
	}
	
	public function minimumLayoutSize(target:Container):IntDimension{
    	if(target === accordion){
    		synHeaderProperties();
    		
	    	var insets:Insets = accordion.getInsets();
	    	
	    	var w:int = 0;
	    	var h:int = 0;
	    	var i:int = 0;
	    	var size:IntDimension;
	    	
	    	for(i=accordion.getComponentCount()-1; i>=0; i--){
	    		size = accordion.getComponent(i).getMinimumSize();
	    		w = Math.max(w, size.width);
	    		h = Math.max(h, size.height);
	    	}
	    	
	    	for(i=accordion.getComponentCount()-1; i>=0; i--){
	    		size = getHeader(i).getTabComponent().getMinimumSize();
	    		w = Math.max(w, size.width);
	    		h += size.height;
	    	}
	    	
	    	return insets.getOutsideSize(new IntDimension(w, h));
    	}
    	return null;
	}
	
	public function maximumLayoutSize(target:Container):IntDimension
	{
		return IntDimension.createBigDimension();
	}
	
	public function getLayoutAlignmentX(target:Container):Number{
		return 0;
	}
	
	public function getLayoutAlignmentY(target:Container):Number{
		return 0;
	}
	
	override public function getMaximumSize(c:Component):IntDimension{
		return maximumLayoutSize(accordion);
	}
	
	override public function getMinimumSize(c:Component):IntDimension{
		return minimumLayoutSize(accordion);
	}
	
	override public function getPreferredSize(c:Component):IntDimension{
		return preferredLayoutSize(accordion);
	}	
	
}
}