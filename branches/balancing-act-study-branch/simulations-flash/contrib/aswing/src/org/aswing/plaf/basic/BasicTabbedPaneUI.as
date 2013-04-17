/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.border.EmptyBorder;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.ElementCreater;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.overflow.JTabbedPane;
import org.aswing.JToolTip;
import org.aswing.LookAndFeel;
import org.aswing.MCPanel;
import org.aswing.plaf.basic.icon.ArrowIcon;
import org.aswing.plaf.InsetsUIResource;
import org.aswing.plaf.TabbedPaneUI;
import org.aswing.plaf.UIResource;
import org.aswing.SoftBoxLayout;
import org.aswing.UIDefaults;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.DepthManager;
import org.aswing.util.MCUtils;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicTabbedPaneUI extends TabbedPaneUI {
	
	private static var topBlankSpace:Number = 4;
	
    private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;
    private var arrowShadowColor:ASColor;
    private var arrowLightColor:ASColor;
    
	private var tabbedPane:JTabbedPane;
	private var tabBarSize:Dimension;
	private var maxTabSize:Dimension;
	private var prefferedSize:Dimension;
	private var minimumSize:Dimension;
	private var tabBoundArray:Array;
	private var drawnTabBoundArray:Array;
	private var baseLineThickness:Number;
	private var maxTabWidth:Number;
	//both the 2 values are just the values considering when placement is TOP
	private var tabBorderInsets:Insets;
	
	private var textFields:Array;
	private var textFieldsBevel:Array;
	private var toolTip:JToolTip;
	private var tabbedPaneListener:Object;
	private var toolTipTrigger:Object;
	
	private var firstIndex:Number; //first viewed tab index
	private var lastIndex:Number;  //last perfectly viewed tab index
	private var prevButton:AbstractButton;
	private var nextButton:AbstractButton;
	private var buttonMCPane:Container;
	private var isSuppliedPress:Boolean;
	
	private var uiRootMC:MovieClip;
	private var tabBarMC:MovieClip;
	private var tabBarMaskMC:MovieClip;
	private var focusGraphicsMC:MovieClip;
	private var buttonHolderMC:MovieClip;
	
	public function BasicTabbedPaneUI() {
		super();
		checkRectsForCountLayout();
		tabBorderInsets = new Insets(2, 2, 0, 2);
		textFields = new Array();
		textFieldsBevel = new Array();
		firstIndex = 0;
		lastIndex = 0;
		isSuppliedPress = false;
	}

    public function installUI(c:Component):Void{
		tabbedPane = JTabbedPane(c);
		tabbedPane.setLayout(this);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		tabbedPane = JTabbedPane(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
    

	private function installDefaults():Void{
		var table:UIDefaults = UIManager.getLookAndFeelDefaults();
		shadow = table.getColor("TabbedPane.shadow");
		darkShadow = table.getColor("TabbedPane.darkShadow");
		highlight = table.getColor("TabbedPane.light");
		lightHighlight = table.getColor("TabbedPane.highlight");
		arrowShadowColor = table.getColor("TabbedPane.arrowShadowColor");
		arrowLightColor = table.getColor("TabbedPane.arrowLightColor");
		
		baseLineThickness = table.getNumber("TabbedPane.baseLineThickness");
		if(baseLineThickness == null) baseLineThickness = 8;
		maxTabWidth = table.getNumber("TabbedPane.maxTabWidth");
		if(maxTabWidth == null) maxTabWidth = 1000;
		
		var tabMargin:Insets = table.getInsets("TabbedPane.tabMargin");
		if(tabMargin == null) tabMargin = new InsetsUIResource(1, 1, 1, 1);    	
		var i:Insets = tabbedPane.getMargin();
		if (i === undefined || i instanceof UIResource) {
	    	tabbedPane.setMargin(tabMargin);
		}
		
		var pp:String = "TabbedPane.";
        LookAndFeel.installBorder(tabbedPane, pp + "border");
        LookAndFeel.installColorsAndFont(tabbedPane, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBasicProperties(tabbedPane, pp);
	}
    
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(tabbedPane);
    }
    
	private function installComponents():Void{
		prevButton = createPrevButton();
		nextButton = createNextButton();
		prevButton.setFocusable(false);
		prevButton.setVisible(false);
		nextButton.setFocusable(false);
		nextButton.setVisible(false);
		toolTip = new JToolTip();
		toolTip.setTargetComponent(tabbedPane);
    }
	private function uninstallComponents():Void{
		buttonMCPane.removeAll();
		buttonMCPane.destroy();
    	tabBarMC.removeMovieClip();
    	tabBarMaskMC.removeMovieClip();
    	buttonHolderMC.removeMovieClip();
    	uiRootMC.removeMovieClip();
    	toolTip.setTargetComponent(null);
    	toolTip.disposeToolTip();
    }
	
	private function installListeners():Void{
		tabbedPaneListener = new Object();
		tabbedPaneListener[JTabbedPane.ON_STATE_CHANGED] = Delegate.create(this, __selectionChanged);
		tabbedPaneListener[JTabbedPane.ON_PRESS] = Delegate.create(this, __tabPanePressed);
		tabbedPaneListener[JTabbedPane.ON_KEY_DOWN] = Delegate.create(this, __onNavKeyDown);
		tabbedPane.addEventListener(tabbedPaneListener);
		
		toolTipTrigger = new Object();
		toolTipTrigger[Component.ON_ROLLOUT] = Delegate.create(this, __onPaneRollOut);
		toolTipTrigger[Component.ON_HIDDEN] = toolTipTrigger[Component.ON_ROLLOUT];
		toolTipTrigger[Component.ON_DESTROY] = toolTipTrigger[Component.ON_ROLLOUT];
		toolTipTrigger[Component.ON_PRESS] = toolTipTrigger[Component.ON_ROLLOUT];
		tabbedPane.addEventListener(toolTipTrigger);
		
		prevButton.addActionListener(__prevButtonReleased, this);
		nextButton.addActionListener(__nextButtonReleased, this);
		prevButton.addEventListener(Component.ON_PRESS, __buttonPressed, this);
		nextButton.addEventListener(Component.ON_PRESS, __buttonPressed, this);
		
		toolTip.addEventListener(JToolTip.ON_TIP_SHOWING, __toolTipShowing, this);
	}
    private function uninstallListeners():Void{
    	tabbedPane.removeEventListener(tabbedPaneListener);
    	tabbedPane.removeEventListener(toolTipTrigger);
    }
    //----------
    private var tipedIndex:Number;
    private function __toolTipShowing():Void{
    	var index:Number = getMousedOnTabIndex();
    	if(index >= 0){
    		toolTip.setTipText(tabbedPane.getTipAt(index));
    		tipedIndex = index;
	    	if(tabBarMC.onMouseMove == undefined){
	    		tabBarMC.onMouseMove = Delegate.create(this, __onTabMouseMove);
	    	}
    	}else{
    		toolTip.setTipText(null);
    		toolTip.startWaitToPopup();
    	}
    }
    
    private function __onPaneRollOut():Void{
    	tabBarMC.onMouseMove = undefined;
    	delete tabBarMC.onMouseMove;
    }
    
    private function __onTabMouseMove():Void{
    	var index:Number = getMousedOnTabIndex();
		if(toolTip.isShowing()){
			if(index == tipedIndex){
				return;
			}
	    	if(index >= 0){
	    		tipedIndex = index;
	    		toolTip.moveLocationRelatedTo(tabbedPane.componentToGlobal(tabbedPane.getMousePosition()));
	    		toolTip.setTipText(tabbedPane.getTipAt(index));
	    	}else{
	    		toolTip.setTipText(null);
    			toolTip.startWaitToPopup();
	    	}
		}else{
			if(index >= 0){
				toolTip.startWaitToPopup();
			}
		}
    }
    
    private function getMousedOnTabIndex():Number{
    	var p:Point = tabbedPane.getMousePosition();
    	var n:Number = tabbedPane.getComponentCount();
    	for(var i:Number=firstIndex; i<n && i<=lastIndex+1; i++){
    		var b:Rectangle = getDrawnTabBounds(i);
    		if(b.containsPoint(p)){
    			return i;
    		}
    	}
    	return -1;
    }
    
    private function __selectionChanged():Void{
    	tabbedPane.revalidate();
    	tabbedPane.repaint();
    }
    
    private function __tabPanePressed():Void{
    	if(isSuppliedPress){
    		isSuppliedPress = false;
    		return;
    	}
    	var index:Number = getMousedOnTabIndex();
    	if(index >= 0 && tabbedPane.isEnabledAt(index)){
    		tabbedPane.setSelectedIndex(index);
    	}
    }
    
    private function __onNavKeyDown():Void{
		if(!tabbedPane.isEnabled()){
			return;
		}
    	var n:Number = tabbedPane.getComponentCount();
    	if(n > 0){
			var index:Number = tabbedPane.getSelectedIndex();
    		var code:Number = Key.getCode();
	    	if(code == Key.DOWN || code == Key.RIGHT){
	    		FocusManager.getCurrentManager().setTraversing(true);
		    	index++;
		    	var count:Number = 1;
		    	while((!tabbedPane.isEnabledAt(index) || !tabbedPane.isVisibleAt(index)) && count<=n){
		    		index++;
		    		count++;
			    	if(index >= n){
			    		index = n-1;
			    	}
		    	}
		    	if(count > n){
		    		return;
		    	}
		    	if(lastIndex < n-1){
		    		firstIndex = Math.min(firstIndex + count, n-1);
		    	}
	    	}else if(Key.getCode() == Key.UP || code == Key.LEFT){
	    		FocusManager.getCurrentManager().setTraversing(true);
		    	index--;
		    	var count:Number = 1;
		    	while((!tabbedPane.isEnabledAt(index) || !tabbedPane.isVisibleAt(index)) && count<=n){
		    		index--;
		    		count++;
			    	if(index < 0){
			    		index = 0;
			    	}
		    	}
		    	if(count > n){
		    		return;
		    	}
		    	if(firstIndex > 0){
		    		firstIndex = Math.max(0, firstIndex - count);
		    	}
	    	}
		    tabbedPane.setSelectedIndex(index);
    	}
    }
    
    private function __prevButtonReleased():Void{
    	if(firstIndex > 0){
    		firstIndex--;
    		tabbedPane.repaint();
    	}
    }
    
    private function __nextButtonReleased():Void{
    	if(lastIndex < tabbedPane.getComponentCount()-1){
    		firstIndex++;
    		tabbedPane.repaint();
    	}
    }
    
    private function __buttonPressed():Void{
    	isSuppliedPress = true;
    	tabbedPane.supplyOnPress();
    }
    //----------
    
    
    private function isTabHorizontalPlacing():Boolean{
    	return tabbedPane.getTabPlacement() == JTabbedPane.TOP || tabbedPane.getTabPlacement() == JTabbedPane.BOTTOM;
    }
    /**
     * This is just the value when placement is TOP
     */
    private function getTabBorderInsets():Insets{
    	return tabBorderInsets;
    }
    
    private function createPrevButton():AbstractButton{
    	var b:JButton = new JButton(null, createArrowIcon(Math.PI, true));
    	b.setMargin(new Insets(2, 2, 2, 2));
    	b.setDisabledIcon(createArrowIcon(Math.PI, false));
    	return b;
    }
    private function createNextButton():AbstractButton{
    	var b:JButton = new JButton(null, createArrowIcon(0, true));
    	b.setMargin(new Insets(2, 2, 2, 2));
    	b.setDisabledIcon(createArrowIcon(0, false));
    	return b;
    }
    private function createArrowIcon(direction:Number, enable:Boolean):Icon{
    	var icon:Icon;
    	if(enable){
    		icon = new ArrowIcon(direction, 8,
				    shadow,
				    arrowLightColor,
				    arrowShadowColor,
				    highlight);
    	}else{
    		icon = new ArrowIcon(direction, 8,
				    shadow,
				    arrowLightColor.brighter(0.4),
				    arrowShadowColor.brighter(0.4),
				    highlight);
    	}
		return icon;
    }
        
    private function getTabBarSize():Dimension{
    	if(tabBarSize != null){
    		return tabBarSize;
    	}
    	var isHorizontalPlacing:Boolean = isTabHorizontalPlacing();
    	tabBarSize = new Dimension(0, 0);
    	var n:Number = tabbedPane.getComponentCount();
    	tabBoundArray = new Array(n);
    	var x:Number = 0;
    	var y:Number = 0;
    	for(var i:Number=0; i<n; i++){
    		var ts:Dimension = countPreferredTabSizeAt(i);
    		var tbounds:Rectangle = new Rectangle(x, y, ts.width, ts.height);
    		tabBoundArray[i] = tbounds;
    		
    		if(isHorizontalPlacing){
    			tabBarSize.height = Math.max(tabBarSize.height, ts.height);
    			tabBarSize.width += ts.width;
    			x += ts.width;
    		}else{
    			tabBarSize.width = Math.max(tabBarSize.width, ts.width);
    			tabBarSize.height += ts.height;
    			y += ts.height;
    		}
    	}
    	maxTabSize = new Dimension(tabBarSize);
		if(isHorizontalPlacing){
			tabBarSize.height += (topBlankSpace + baseLineThickness);
			//blank space at start and end for selected tab expanding
			tabBarSize.width += (tabBorderInsets.left + tabBorderInsets.right);
		}else{
			tabBarSize.width += (topBlankSpace + baseLineThickness);
			//blank space at start and end for selected tab expanding
			tabBarSize.height += (tabBorderInsets.left + tabBorderInsets.right);
		}
		return tabBarSize;
    }
    
    private function getTabBoundArray():Array{
    	if(tabBoundArray != null){
    		return tabBoundArray;
    	}
    	getTabBarSize();
    	if(tabBoundArray == null){
    		trace("Debug : Error tabBoundArray == null but tabBarSize = " + tabBarSize);
    	}
    	return tabBoundArray;
    }
        
    /* Re-using rectangles rather than 
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.
     */
	private static var viewRect:Rectangle;
    private static var textRect:Rectangle;
    private static var iconRect:Rectangle;
       
    private static function checkRectsForCountLayout():Void{
    	if(viewRect == null){
			viewRect = new Rectangle();
    		textRect = new Rectangle();
    		iconRect = new Rectangle();
    	}
    }    
    private function countPreferredTabSizeAt(index:Number):Dimension{
    	viewRect.setRect(0, 0, 100000, 100000);
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        
        var text:String = tabbedPane.getTitleAt(index);
        var icon:Icon = tabbedPane.getIconAt(index);
        var b:JTabbedPane = tabbedPane;
        ASWingUtils.layoutCompoundLabel(
            b.getFont(), text, icon, 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	text == null ? 0 : b.getIconTextGap()
        );
        /* The preferred size is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        var r:Rectangle = iconRect.union(textRect);
        var size:Dimension = r.getSize();
        size.width = Math.min(size.width, maxTabWidth);
        var tabMargin:Insets = tabbedPane.getMargin();
        if(isTabHorizontalPlacing()){
        	size.height += (tabMargin.top + tabMargin.bottom + tabBorderInsets.top + tabBorderInsets.bottom);
        	size.width += (tabMargin.left + tabMargin.right + tabBorderInsets.left + tabBorderInsets.right);
        }else{
        	size.width += (tabMargin.top + tabMargin.bottom + tabBorderInsets.top + tabBorderInsets.bottom);
        	size.height += (tabMargin.left + tabMargin.right + tabBorderInsets.left + tabBorderInsets.right);
        }
        return size;
    }
    
    private function setDrawnTabBounds(index, b:Rectangle, paneBounds:Rectangle):Void{
    	b = new Rectangle(b);
    	if(b.x < paneBounds.x){
    		b.x = paneBounds.x;
    	}
    	if(b.y < paneBounds.y){
    		b.y = paneBounds.y;
    	}
    	if(b.x + b.width > paneBounds.x + paneBounds.width){
    		b.width = paneBounds.x + paneBounds.width - b.x;
    	}
    	if(b.y + b.height > paneBounds.y + paneBounds.height){
    		b.height = paneBounds.y + paneBounds.height - b.y;
    	}
    	drawnTabBoundArray[index] = b;
    }
    private function getDrawnTabBounds(index:Number):Rectangle{
    	return drawnTabBoundArray[index];
    }    
    
	public function create(c:Component):Void{
    	super.create(c);
    	var creater:ElementCreater = ElementCreater.getInstance();;
    	uiRootMC = c.createMovieClip("ui_root");
    	
    	tabBarMC = creater.createMCWithName(uiRootMC, "tabBarMC");
    	tabBarMaskMC = creater.createMCWithName(uiRootMC, "tabBarMaskMC");
    	focusGraphicsMC = creater.createMCWithName(uiRootMC, "focusGraphicsMC");
    	buttonHolderMC = creater.createMCWithName(uiRootMC, "buttonHolderMC");
    	
    	tabBarMC.setMask(tabBarMaskMC);
    	var g:Graphics = new Graphics(tabBarMaskMC);
    	g.fillRectangle(new SolidBrush(0), 0, 0, 1, 1);
    	
    	buttonMCPane = new MCPanel(buttonHolderMC, 100, 100);
    	buttonMCPane.setFocusable(false);
    	buttonMCPane.setLayout(new BorderLayout());
    	var p:JPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.X_AXIS, 0));
    	p.setOpaque(false);
    	p.setFocusable(false);
    	p.setTriggerEnabled(false);
    	buttonMCPane.append(p, BorderLayout.CENTER);
    	var insets:Insets = new Insets(topBlankSpace, topBlankSpace, topBlankSpace, topBlankSpace);
    	p.setBorder(new EmptyBorder(null, insets));
    	p.append(prevButton);
    	p.append(nextButton);
	}
     
	private function createFocusGraphics():Graphics{
		focusGraphicsMC.clear();
		var g:Graphics = new Graphics(focusGraphicsMC);
		return g;
	}
	
	private function createTabBarGraphics():Graphics{
		tabBarMC.clear();
		var g:Graphics = new Graphics(tabBarMC);
		return g;
	}
    
    public function paintFocus(c:Component, g:Graphics):Void{
    	g = createFocusGraphics();
    	super.paintFocus(c, g);
    	var selectedIndex:Number = tabbedPane.getSelectedIndex();
    	if(selectedIndex >= 0){
	    	var rect:Rectangle = getDrawnTabBounds(selectedIndex);
	    	var x1:Number = rect.x;
	    	var y1:Number = rect.y;
	    	var x2:Number = rect.x + rect.width;
	    	var y2:Number = rect.y + rect.height;
	    	var paintBounds:Rectangle = c.getPaintBounds();
	    	x1 = Math.max(paintBounds.x, x1);
	    	y1 = Math.max(paintBounds.y, y1);
	    	x2 = Math.min(paintBounds.x + paintBounds.width, x2);
	    	y2 = Math.min(paintBounds.y + paintBounds.height, y2);
	    	if(rect.x + rect.width)
	    	g.drawRectangle(new Pen(getDefaultFocusColorInner(), 1), x1 + 0.5, y1 + 0.5, (x2-x1)-1, (y2-y1)-1);
	    	g.drawRectangle(new Pen(getDefaultFocusColorOutter(), 1), x1 + 1.5, y1 + 1.5, (x2-x1)-3, (y2-y1)-3);
    	}
    }
    
    public function clearFocus(c:Component):Void{
    	super.clearFocus(c);
    	focusGraphicsMC.clear();
    }    
    
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
    	tabBarMaskMC._x = b.x;
    	tabBarMaskMC._y = b.y;
    	tabBarMaskMC._width = b.width;
    	tabBarMaskMC._height = b.height;
    	g = createTabBarGraphics();
    	
    	var horizontalPlacing:Boolean = isTabHorizontalPlacing();
      	var contentBounds:Rectangle = new Rectangle(b);
    	var tabBarBounds:Rectangle = getTabBarSize().getBounds(0, 0);
    	tabBarBounds.x = contentBounds.x;
    	tabBarBounds.y = contentBounds.y;
    	tabBarBounds.width = Math.min(tabBarBounds.width, contentBounds.width);
    	tabBarBounds.height = Math.min(tabBarBounds.height, contentBounds.height);
        var tabMargin:Insets = tabbedPane.getMargin();
    	var transformedTabMargin:Insets = new Insets(tabMargin.top, tabMargin.left, tabMargin.bottom, tabMargin.right);
    	var placement:Number = tabbedPane.getTabPlacement();
    	if(placement == JTabbedPane.LEFT){
    		tabBarBounds.y += tabBorderInsets.left;//extra for expand 
    		transformedTabMargin.left = tabMargin.top;
    		transformedTabMargin.right = tabMargin.bottom;
    		transformedTabMargin.top = tabMargin.right;
    		transformedTabMargin.bottom = tabMargin.left;
    	}else if(placement == JTabbedPane.RIGHT){
    		tabBarBounds.x = contentBounds.x + contentBounds.width - tabBarBounds.width;
    		tabBarBounds.y += tabBorderInsets.left;//extra for expand 
    		transformedTabMargin.left = tabMargin.bottom;
    		transformedTabMargin.right = tabMargin.top;
    		transformedTabMargin.top = tabMargin.left;
    		transformedTabMargin.bottom = tabMargin.right;
    	}else if(placement == JTabbedPane.BOTTOM){
    		tabBarBounds.y = contentBounds.y + contentBounds.height - tabBarBounds.height;
    		tabBarBounds.x += tabBorderInsets.left;//extra for expand 
    		transformedTabMargin.top = tabMargin.bottom;
    		transformedTabMargin.bottom = tabMargin.top;
    	}else{ //others value are all considered as TOP
    		tabBarBounds.x += tabBorderInsets.left;//extra for expand
    	}
    	
    	var n:Number = tabbedPane.getComponentCount();
    	var tba:Array = getTabBoundArray();
    	drawnTabBoundArray = new Array(n);
    	var selectedIndex:Number = tabbedPane.getSelectedIndex();
    	
    	//count not viewed front tabs's width and invisible them
    	var offsetPoint:Point = new Point();
    	for(var i:Number=0; i<firstIndex; i++){
    		if(horizontalPlacing){
    			offsetPoint.x -= tba[i].width;
    		}else{
    			offsetPoint.y -= tba[i].height;
    		}
    		var tf:TextField = TextField(textFields[i]);
    		var btf:TextField = TextField(textFieldsBevel[i]);
    		tf._visible = false;
    		btf._visible = false;
        	tabbedPane.getIconAt(i).uninstallIcon(c);
    	}
    	//draw from firstIndex to last viewable tabs
    	for(var i:Number=firstIndex; i<n; i++){
    		if(i != selectedIndex){
    			var viewedFlag:Number = drawTabWithFullInfosAt(i, b, tba[i], g, tabBarBounds, offsetPoint, transformedTabMargin);
    			if(viewedFlag < 0){
    				lastIndex = i;
    			}
    			if(viewedFlag >= 0){
    				break;
    			}
    		}
    	}
    	drawBaseLine(tabBarBounds, g, b);
    	if(selectedIndex >= 0){
    		if(drawTabWithFullInfosAt(selectedIndex, b, tba[selectedIndex], g, tabBarBounds, offsetPoint, transformedTabMargin) < 0){
    			lastIndex = Math.max(lastIndex, selectedIndex);
    		}
    	}
    	while(textFields.length > n){
    		var tf:TextField = TextField(textFields.pop());
    		var btf:TextField = TextField(textFieldsBevel.pop());
    		tf.removeTextField();
    		btf.removeTextField();
    	}
    	//invisible tab after last
    	for(var i:Number=lastIndex+2; i<n; i++){
    		var tf:TextField = TextField(textFields[i]);
    		var btf:TextField = TextField(textFieldsBevel[i]);
    		tf._visible = false;
    		btf._visible = false;
        	tabbedPane.getIconAt(i).uninstallIcon(c);
    	}
    	
    	//view prev and next buttons
    	if(firstIndex > 0 || lastIndex < n-1){
    		prevButton.setVisible(true);
    		nextButton.setVisible(true);
    		prevButton.setEnabled(firstIndex > 0);
    		nextButton.setEnabled(lastIndex < n-1);
    		var bps:Dimension = buttonMCPane.getPreferredSize();
    		buttonMCPane.setSize(bps);
    		var bpl:Point = new Point();
	    	if(placement == JTabbedPane.LEFT){
	    		bpl.x = contentBounds.x;
	    		bpl.y = contentBounds.y + contentBounds.height - bps.height;
	    	}else if(placement == JTabbedPane.RIGHT){
	    		bpl.x = contentBounds.x + contentBounds.width - bps.width;
	    		bpl.y = contentBounds.y + contentBounds.height - bps.height;
	    	}else if(placement == JTabbedPane.BOTTOM){
	    		bpl.x = contentBounds.x + contentBounds.width - bps.width;
	    		bpl.y = contentBounds.y + contentBounds.height - bps.height;
	    	}else{
	    		bpl.x = contentBounds.x + contentBounds.width - bps.width;
	    		bpl.y = contentBounds.y;
	    	}
    		buttonMCPane.setLocation(bpl);
    		buttonMCPane.revalidate();
    	}else{
    		prevButton.setVisible(false);
    		nextButton.setVisible(false);
    	}
    	DepthManager.bringToTop(uiRootMC);//make it at top of tabPaneMCs
    	
    	super.paint(c, g, b);
    }
    
    /**
     * Returns whether the tab painted out of tabbedPane bounds or not viewable or viewable.<br>
     * @return -1 , viewable whole area;
     *         0, viewable but end out of bounds
     *         1, not viewable in the bounds. 
     */
    private function drawTabWithFullInfosAt(index:Number, paneBounds:Rectangle, bounds:Rectangle,
     g:Graphics, tabBarBounds:Rectangle, offsetPoint:Point, transformedTabMargin:Insets):Number{
		var tb:Rectangle = new Rectangle(bounds);
		tb.x += (tabBarBounds.x + offsetPoint.x);
		tb.y += (tabBarBounds.y + offsetPoint.y);
		var placement:Number = tabbedPane.getTabPlacement();
    	if(placement == JTabbedPane.LEFT){
			tb.width = maxTabSize.width;
    		tb.x += topBlankSpace;
    	}else if(placement == JTabbedPane.RIGHT){
			tb.width = maxTabSize.width;
			tb.x += baseLineThickness;
    	}else if(placement == JTabbedPane.BOTTOM){
    		tb.y += baseLineThickness;
			tb.height = maxTabSize.height;
    	}else{
			tb.height = maxTabSize.height;
			tb.y += topBlankSpace;
    	}
    	if(isTabHorizontalPlacing()){
    		if(tb.x > paneBounds.x + paneBounds.width){
    			//do not need paint
    			return 1;
    		}
    	}else{
    		if(tb.y > paneBounds.y + paneBounds.height){
    			//do not need paint
    			return 1;
    		}
    	}
		drawTabAt(index, tb, paneBounds, g, transformedTabMargin);
    	if(isTabHorizontalPlacing()){
    		if(tb.x + tb.width > paneBounds.x + paneBounds.width){
    			return 0;
    		}
    	}else{
    		if(tb.y + tb.height > paneBounds.y + paneBounds.height){
    			return 0;
    		}
    	}
    	return -1;
    }
    
    /**
     * override this method to draw different tab base line for your LAF
     */
    private function drawBaseLine(tabBarBounds:Rectangle, g:Graphics, fullB:Rectangle):Void{
    	var b:Rectangle = new Rectangle(tabBarBounds);
    	var placement:Number = tabbedPane.getTabPlacement();
    	if(isTabHorizontalPlacing()){
    		if(placement != JTabbedPane.BOTTOM){
    			b.y = b.y + b.height - baseLineThickness;
    		}
    		b.height = baseLineThickness;
    		b.width = fullB.width;
    		b.x = fullB.x;
    		g.fillRectangle(new SolidBrush(tabbedPane.getBackground()), b.x, b.y, b.width, b.height);
    		var pen:Pen = new Pen(lightHighlight, 1);
    		g.drawLine(pen, b.x, b.y + 0.5, b.x+b.width, b.y+0.5);
    		pen.setASColor(shadow);
    		g.drawLine(pen, b.x, b.y + b.height - 0.5, b.x+b.width, b.y + b.height - 0.5);
    	}else{
    		if(placement != JTabbedPane.RIGHT){
    			b.x = b.x + b.width - baseLineThickness;
    		}
    		b.width = baseLineThickness;
    		b.height = fullB.height;
    		b.y = fullB.y;
    		g.fillRectangle(new SolidBrush(tabbedPane.getBackground()), b.x, b.y, b.width, b.height);
    		var pen:Pen = new Pen(lightHighlight, 1);
    		g.drawLine(pen, b.x+0.5, b.y, b.x+0.5, b.y+b.height);
    		pen.setASColor(shadow);
    		g.drawLine(pen, b.x+b.width-0.5, b.y, b.x+b.width-0.5, b.y+b.height);
    	}
    }
    
    private function drawTabAt(index:Number, bounds:Rectangle, paneBounds:Rectangle, g:Graphics, transformedTabMargin:Insets):Void{
    	//trace("drawTabAt : " + index + ", bounds : " + bounds + ", g : " + g);
    	drawTabBorderAt(index, bounds, paneBounds, g);
    	
    	viewRect = transformedTabMargin.getInsideBounds(bounds);
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        
        var text:String = tabbedPane.getTitleAt(index);
        var icon:Icon = tabbedPane.getIconAt(index);
        var b:JTabbedPane = tabbedPane;
        ASWingUtils.layoutCompoundLabel(
            b.getFont(), text, icon, 
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    	text == null ? 0 : b.getIconTextGap()
        );
        icon.paintIcon(tabbedPane, g, iconRect.x, iconRect.y);
        
		//enable/disable inteface
		var textColor:ASColor;
		if(tabbedPane.isEnabledAt(index)){
			textColor = tabbedPane.getForeground();
		}else{
			textColor = tabbedPane.getBackground().darker();
		}
        var tf:TextField = TextField(textFields[index]);
        var btf:TextField = TextField(textFieldsBevel[index]);

        if(!MCUtils.isTextFieldExist(tf)){
        	textFields[index] = tf = ElementCreater.getInstance().createTF(tabBarMC, "tabTitleTxt");
        }
        if(!MCUtils.isTextFieldExist(btf)){
        	textFieldsBevel[index] = btf = ElementCreater.getInstance().createTF(tabBarMC, "tabTitleTxtBe");
        }
        
		tf.text = text;
    	ASWingUtils.applyTextFontAndColor(tf, tabbedPane.getFont(), textColor);
		tf._x = textRect.x;
		tf._y = textRect.y;
		tf._visible = true;
		
		if(!tabbedPane.isEnabledAt(index)){
			btf.text = text;
	    	ASWingUtils.applyTextFontAndColor(btf, tabbedPane.getFont(), tabbedPane.getBackground().brighter());
			btf._x = textRect.x + 1;
			btf._y = textRect.y + 1;
			btf._visible = true;
		}else{
			btf._visible = false;
		}
    }
    
    private function getTabColor(index:Number):ASColor{
    	return tabbedPane.getBackground();
    }
    
    /**
     * override this method to draw different tab border for your LAF.<br>
     * Note, you must call setDrawnTabBounds() to set the right bounds for each tab in this method
     */
    private function drawTabBorderAt(index:Number, b:Rectangle, paneBounds:Rectangle, g:Graphics):Void{
    	var placement:Number = tabbedPane.getTabPlacement();
    	if(index == tabbedPane.getSelectedIndex()){
    		b = new Rectangle(b);//make a clone to be safty modification
    		if(isTabHorizontalPlacing()){
    			b.x -= tabBorderInsets.left;
    			b.width += (tabBorderInsets.left + tabBorderInsets.right);
	    		b.height += Math.round(topBlankSpace/2+2);
    			if(placement == JTabbedPane.BOTTOM){
	    			b.y -= 2;
    			}else{
	    			b.y -= Math.round(topBlankSpace/2);
    			}
    		}else{
    			b.y -= tabBorderInsets.left;
    			b.height += (tabBorderInsets.left + tabBorderInsets.right);
	    		b.width += Math.round(topBlankSpace/2+2);
    			if(placement == JTabbedPane.RIGHT){
	    			b.x -= 2;
    			}else{
	    			b.x -= Math.round(topBlankSpace/2);
    			}
    		}
    	}
    	//This is important, should call this in sub-implemented drawTabBorderAt method
    	setDrawnTabBounds(index, b, paneBounds);
    	var x1:Number = b.x;
    	var y1:Number = b.y;
    	var x2:Number = b.x + b.width;
    	var y2:Number = b.y + b.height;
    	if(placement == JTabbedPane.LEFT){
    		g.fillRectangle(new SolidBrush(getTabColor(index)), b.x, b.y, b.width, b.height-2);
    		//left 1
    		var pen:Pen = new Pen(lightHighlight, 1);
    		g.drawLine(pen, x1+0.5, y1, x1+0.5, y2-2);
    		//top 1
    		g.drawLine(pen, x1, y1+0.5, x2, y1+0.5);
    		//bottom 2
    		pen.setASColor(shadow);
    		g.drawLine(pen, x1+1, y2-1.5, x2, y2-1.5);
    		pen.setASColor(darkShadow);
    		g.drawLine(pen, x1+2, y2-0.5, x2, y2-0.5);
    	}else if(placement == JTabbedPane.RIGHT){
    		g.fillRectangle(new SolidBrush(getTabColor(index)), b.x, b.y, b.width-1, b.height-2);
    		//top 1
    		var pen:Pen = new Pen(lightHighlight, 1);
    		g.drawLine(pen, x1, y1+0.5, x2-1, y1 + 0.5);
    		//bottom 2
    		pen.setASColor(shadow);
    		g.drawLine(pen, x1, y2-1.5, x2-1, y2-1.5);
    		pen.setASColor(darkShadow);
    		g.drawLine(pen, x1, y2-0.5, x2-1, y2-0.5);
    		//right 2
    		pen.setASColor(shadow);
    		g.drawLine(pen, x2-1.5, y1+1, x2-1.5, y2-1.5);
    		pen.setASColor(darkShadow);
    		g.drawLine(pen, x2-0.5, y1+2, x2-0.5, y2-0.5);
    	}else if(placement == JTabbedPane.BOTTOM){
    		g.fillRectangle(new SolidBrush(getTabColor(index)), b.x, b.y, b.width-1, b.height-2);
    		//left 1
    		var pen:Pen = new Pen(lightHighlight, 1);
    		g.drawLine(pen, x1+0.5, y1+1, x1+0.5, y2-2);
    		//bottom 2
    		pen.setASColor(shadow);
    		g.drawLine(pen, x1+1, y2-1.5, x2-1, y2-1.5);
    		pen.setASColor(darkShadow);
    		g.drawLine(pen, x1+2, y2-0.5, x2-1, y2-0.5);
    		//right 2
    		pen.setASColor(shadow);
    		g.drawLine(pen, x2-1.5, y1, x2-1.5, y2-1);
    		pen.setASColor(darkShadow);
    		g.drawLine(pen, x2-0.5, y1, x2-0.5, y2-1);
    	}else{
    		g.fillRectangle(new SolidBrush(getTabColor(index)), b.x, b.y, b.width-1, b.height);
    		//left 1
    		var pen:Pen = new Pen(lightHighlight, 1);
    		g.drawLine(pen, x1+0.5, y1+1, x1+0.5, y2);
    		//top 1
    		g.drawLine(pen, x1+1, y1+0.5, x2-2, y1 + 0.5);
    		//right 2
    		pen.setASColor(shadow);
    		g.drawLine(pen, x2-1.5, y1, x2-1.5, y2);
    		pen.setASColor(darkShadow);
    		g.drawLine(pen, x2-0.5, y1+1, x2-0.5, y2);    		
    	}
    }
    //----------------------------LayoutManager Implementation-----------------------------
    
    public function addLayoutComponent(comp:Component, constraints:Object):Void{
    	tabbedPane.repaint();
    }
	
    public function removeLayoutComponent(comp:Component):Void{
    	tabbedPane.repaint();
    }
	
    public function preferredLayoutSize(target:Container):Dimension{
    	if(target != tabbedPane){
    		trace("Error : BasicTabbedPaneUI Can't layout " + target);
    		return null;
    	}
    	if(prefferedSize != null){
    		return prefferedSize;
    	}
    	var insets:Insets = tabbedPane.getInsets();
    	
    	var w:Number = 0;
    	var h:Number = 0;
    	
    	for(var i:Number=tabbedPane.getComponentCount()-1; i>=0; i--){
    		var size:Dimension = tabbedPane.getComponent(i).getPreferredSize();
    		w = Math.max(w, size.width);
    		h = Math.max(h, size.height);
    	}
    	var tbs:Dimension = getTabBarSize();
    	if(isTabHorizontalPlacing()){
    		w = Math.max(w, tbs.width);
	    	h += tbs.height;
    	}else{
    		h = Math.max(h, tbs.height);
	    	w += tbs.width;
    	}
    	
    	prefferedSize = insets.getOutsideSize(new Dimension(w, h));
    	return prefferedSize;
    }

    public function minimumLayoutSize(target:Container):Dimension{
    	if(target != tabbedPane){
    		trace("Error : BasicTabbedPaneUI Can't layout " + target);
    		return null;
    	}
    	if(minimumSize != null){
    		return minimumSize;
    	}
    	var insets:Insets = tabbedPane.getInsets();
    	
    	var w:Number = 0;
    	var h:Number = 0;
    	
    	for(var i:Number=tabbedPane.getComponentCount()-1; i>=0; i--){
    		var size:Dimension = tabbedPane.getComponent(i).getMinimumSize();
    		w = Math.max(w, size.width);
    		h = Math.max(h, size.height);
    	}
    	var tbs:Dimension = getTabBarSize();
    	if(isTabHorizontalPlacing()){
    		h += tbs.height;
    	}else{
    		w += tbs.width;
    	}
    	
    	minimumSize = insets.getOutsideSize(new Dimension(w, h));
    	return minimumSize;
    }
	    
    public function layoutContainer(target:Container):Void{
    	if(target != tabbedPane){
    		trace("Error : BasicTabbedPaneUI Can't layout " + target);
    		return;
    	}
    	var n:Number = tabbedPane.getComponentCount();
    	var selectedIndex:Number = tabbedPane.getSelectedIndex();
    	
    	var insets:Insets = tabbedPane.getInsets();
    	var paneBounds:Rectangle = insets.getInsideBounds(new Rectangle(0, 0, tabbedPane.getWidth(), tabbedPane.getHeight()));
    	var tbs:Dimension = getTabBarSize();
    	if(isTabHorizontalPlacing()){
    		paneBounds.height -= tbs.height;
    	}else{
    		paneBounds.width -= tbs.width;
    	}
    	var placement:Number = tabbedPane.getTabPlacement();
    	if(placement == JTabbedPane.LEFT){
    		paneBounds.x += tbs.width;
    	}else if(placement == JTabbedPane.RIGHT){
    		//do not need offset
    	}else if(placement == JTabbedPane.BOTTOM){
    		//do not need offset
    	}else{ //others value are all considered as TOP
    		paneBounds.y += tbs.height;
    	}
    	
    	for(var i:Number=0; i<n; i++){
    		tabbedPane.getComponent(i).setBounds(paneBounds);
    		tabbedPane.getComponent(i).setVisible(i == selectedIndex);
		}
    }
    
    public function invalidateLayout(target:Container):Void{
    	if(target != tabbedPane){
    		trace("Error : BasicTabbedPaneUI Can't layout " + target);
    		return;
    	}
    	prefferedSize = null;
    	minimumSize = null;
    	tabBarSize = null;
    	tabBoundArray = null;
    }
}