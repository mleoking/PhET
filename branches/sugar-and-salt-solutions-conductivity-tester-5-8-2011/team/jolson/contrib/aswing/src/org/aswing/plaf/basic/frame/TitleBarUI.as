/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.ASWingConstants;
import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.plaf.basic.frame.FrameTitleBar;
import org.aswing.plaf.basic.frame.TitleBarLayout;
import org.aswing.plaf.ComponentUI;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.frame.TitleBarUI extends ComponentUI {
	
	private static var ICON_TEXT_GAP:Number = 2;
	
	private var titleTextField:TextField;
	private var titleBar:FrameTitleBar;
	private var frame:JFrame;
	private var defaultIcon:Icon;
	
	private var iconifiedButton:JButton;
	private var resizeButton:JButton;
	private var closeButton:JButton;
	
	private var iconifiedIcon:Icon;
	private var normalIcon:Icon;
	private var maximizeIcon:Icon;
	private var closeIcon:Icon;
	
	private var activeColor:ASColor;
	private var activeTextColor:ASColor;
	private var inactiveColor:ASColor;
	private var inactiveTextColor:ASColor;
	private var activeBorderColor:ASColor;
	private var inactiveBorderColor:ASColor;
	
	
	
	private var stateChangedListener:Object;
	private var sizeChangedListener:Object;
	private var locationChangedListener:Object;
	private var stageChangedListener:Object;
	private var activeListener:Object;
	private var unactiveListener:Object;
	private var stateChangeSize:Boolean;
	private var stateChangeLocation:Boolean;
	private var lastNormalStateBounds:Rectangle;
	
	public function TitleBarUI() {
		super();
		stateChangeSize = false;
		stateChangeLocation = false;
		lastNormalStateBounds = new Rectangle();
		checkRectsForCountLayout();
	}
	
	public function installUI(c:Component):Void{
    	titleBar = FrameTitleBar(c);
    	titleBar.setFocusable(false);
    	frame = titleBar.getFrame();
		
		installDefaults();
		installComponents();
		installListeners();
    }
    private function installDefaults():Void{
    	
		titleBar.setLayout(TitleBarLayout.createInstance());
		
    	defaultIcon = UIManager.getIcon("Frame.icon");
    	
		activeColor         = UIManager.getColor("Frame.activeCaption");
		activeTextColor     = UIManager.getColor("Frame.activeCaptionText");
		inactiveColor       = UIManager.getColor("Frame.inactiveCaption");
		inactiveTextColor   = UIManager.getColor("Frame.inactiveCaptionText"); 	
		activeBorderColor   = UIManager.getColor("Frame.activeCaptionBorder");
		inactiveBorderColor = UIManager.getColor("Frame.inactiveCaptionBorder"); 	
		
		iconifiedIcon = UIManager.getIcon("Frame.iconifiedIcon");
	 	normalIcon    = UIManager.getIcon("Frame.normalIcon");
		maximizeIcon  = UIManager.getIcon("Frame.maximizeIcon");
		closeIcon     = UIManager.getIcon("Frame.closeIcon");
		
		
        titleBar.setBackground(activeColor);
        titleBar.setForeground(activeTextColor);
        titleBar.setOpaque(true);
    }
	private function installComponents() : Void {
		iconifiedButton = new JButton(null, iconifiedIcon);
		resizeButton    = new JButton(null, maximizeIcon);
		closeButton     = new JButton(null, closeIcon);
		titleBar.append(iconifiedButton);
		titleBar.append(resizeButton);
		titleBar.append(closeButton);
		
		iconifiedButton.addActionListener(__iconifiedPressed, this);
		resizeButton.addActionListener(__resizePressed, this);
		closeButton.addActionListener(__closePressed, this);
	}
	private function installListeners():Void{
		stateChangedListener = frame.addEventListener(JFrame.ON_STATE_CHANGED, __stateChanged, this);
		sizeChangedListener = frame.addEventListener(JFrame.ON_RESIZED, __sizeChanged, this);
		locationChangedListener = frame.addEventListener(Component.ON_MOVED, __frameMoved, this);
		stageChangedListener = new Object();
		stageChangedListener.onResize = Delegate.create(this, __stageChanged);
		activeListener = frame.addEventListener(JFrame.ON_WINDOW_ACTIVATED, __frameActived, this);
		unactiveListener = frame.addEventListener(JFrame.ON_WINDOW_DEACTIVATED, __frameUnactived, this);
	}
	
    public function uninstallUI(c:Component):Void{
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    	titleTextField.removeTextField();
    }
	private function uninstallDefaults():Void{
	}
	private function uninstallComponents():Void{
		titleBar.remove(iconifiedButton);
		titleBar.remove(resizeButton);
		titleBar.remove(closeButton);
	}
	private function uninstallListeners():Void{
		frame.removeEventListener(stateChangedListener);
		frame.removeEventListener(sizeChangedListener);
		frame.removeEventListener(locationChangedListener);
		Stage.removeListener(stageChangedListener);
		frame.removeEventListener(activeListener);
		frame.removeEventListener(unactiveListener);
	}

	public function create(c:Component):Void{
		titleTextField = c.createTextField("titleText");
		
		adjustButtons();
		adjustResizerEnable();
		
		if(frame.getState() == JFrame.NORMAL){
			lastNormalStateBounds.setRect(frame.getBounds());
		}
	}
	
	//------------------------------------------
	private function __frameActived():Void{
		titleBar.repaint();
	}
	private function __frameUnactived():Void{
		titleBar.repaint();
	}	
	private function __stageChanged():Void{
		if(Stage.scaleMode != "noScale"){
			return;
		}
		if(isMaximized()){
			setSizeToFixMaxmimized();
			frame.revalidateIfNecessary();
		}
	}
	private function __iconifiedPressed():Void{
		frame.setState(JFrame.ICONIFIED);
	}
	private function __resizePressed():Void{
		if(isNormalIcon()){
			frame.setState(JFrame.NORMAL);
		}else{
			frame.setState(JFrame.MAXIMIZED);
		}
	}
	private function __closePressed():Void{
		frame.closeReleased();
	}
	
	private function __frameMoved():Void{
		if(stateChangeLocation){
			stateChangeLocation = false;
		}else{
			lastNormalStateBounds.setLocation(frame.getLocation());
		}
	}
	
	private function __sizeChanged():Void{
		if(stateChangeSize || frame.getState() == JFrame.ICONIFIED){
			stateChangeSize = false;
		}else{
			lastNormalStateBounds.setSize(frame.getSize());
		}
	}
	
	private function __stateChanged():Void{
		var state:Number = frame.getState();
		if(state != JFrame.ICONIFIED 
			&& state != JFrame.NORMAL
			&& state != JFrame.MAXIMIZED_HORIZ
			&& state != JFrame.MAXIMIZED_VERT
			&& state != JFrame.MAXIMIZED){
			state = JFrame.NORMAL;
		}
		if(state == JFrame.ICONIFIED){
			iconifiedButton.setVisible(false);
			switchResizeIcon();
			var iconifiedSize:Dimension = titleBar.getMinimumSize();
			stateChangeSize = true;
			frame.setSize(frame.getInsets().getOutsideSize(iconifiedSize));
			stateChangeSize = false;
			Stage.removeListener(stageChangedListener);
		}else if(state == JFrame.NORMAL){
			stateChangeSize = true;
			frame.setBounds(lastNormalStateBounds);
			stateChangeSize = false;
			if(isNeedToViewIconifiedButton())
				iconifiedButton.setVisible(true);
			switchToMaximizButton();
			Stage.removeListener(stageChangedListener);
		}else{
			setSizeToFixMaxmimized();
		}
		frame.revalidateIfNecessary();
	}
	
	private function setSizeToFixMaxmimized():Void{
		var state:Number = frame.getState();
		var maxBounds:Rectangle = frame.getMaximizedBounds();
		var b:Rectangle = frame.getBounds();
		if((state & JFrame.MAXIMIZED_HORIZ) == JFrame.MAXIMIZED_HORIZ){
			b.x = maxBounds.x;
			b.width = maxBounds.width;
		}
		if((state & JFrame.MAXIMIZED_VERT) == JFrame.MAXIMIZED_VERT){
			b.y = maxBounds.y;
			b.height = maxBounds.height;
		}
		stateChangeSize = true;
		stateChangeLocation = true;
		frame.setBounds(b);
		stateChangeSize = false;
		stateChangeLocation = false;
		if(isNeedToViewIconifiedButton())
			iconifiedButton.setVisible(true);
		switchToNormalButton();
		
		Stage.addListener(stageChangedListener);
	}
	
	public function isNormalIcon():Boolean{
		return resizeButton.getIcon() == normalIcon;
	}
		
	public function switchResizeIcon():Void{
		if(isNormalIcon()){
			switchToMaximizButton();
		}else{
			switchToNormalButton();
		}
	}
	
	public function switchToMaximizButton():Void{
		resizeButton.setIcon(maximizeIcon);
	}
	
	public function switchToNormalButton():Void{
		resizeButton.setIcon(normalIcon);
	}
	
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
    	
	public function paint(c:Component, g:Graphics, r:Rectangle):Void{
		super.paint(c, g, r);
		
		var icon:Icon = frame.getIcon();
		
		if(icon == null){
			icon = defaultIcon;
		}
		
		icon.uninstallIcon(c);
		
    	viewRect.setRect(r);
    	var tlayout:TitleBarLayout = TitleBarLayout(titleBar.getLayout());
    	var buttonsWidth:Number = 0;
    	var buttonCount:Number = 0;
    	if(iconifiedButton.isVisible()){
    		buttonCount ++;
    		buttonsWidth += iconifiedButton.getWidth();
    	} 
    	if(resizeButton.isVisible()){
    		buttonCount ++;
    		buttonsWidth += resizeButton.getWidth();
    	} 
    	if(closeButton.isVisible()){
    		buttonCount ++;
    		buttonsWidth += closeButton.getWidth();
    	} 
    	viewRect.width -= buttonsWidth + Math.max(0, buttonCount-1)*tlayout.getHorizontalGap();
    		
    	textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        // layout the text and icon
        var text:String = ASWingUtils.layoutCompoundLabel(
            c.getFont(), frame.getTitle(), icon, 
            ASWingConstants.CENTER, ASWingConstants.LEFT,
            ASWingConstants.CENTER, ASWingConstants.RIGHT,
            viewRect, iconRect, textRect, 
	    	frame.getTitle() == null ? 0 : ICON_TEXT_GAP);
	   	
    	if(icon != null){
    		icon.paintIcon(c, g, iconRect.x, iconRect.y);
    	}
    	
        if (text != null && text != ""){
			titleTextField.text = text;
	    	ASWingUtils.applyTextFontAndColor(titleTextField, frame.getFont(), 
	    		frame.isActive() ? activeTextColor : inactiveTextColor);
			titleTextField._x = textRect.x;
			titleTextField._y = textRect.y;
        }
		
		adjustButtons();
		adjustResizerEnable();
	}
	
	private function isMaximized():Boolean{
		var state:Number = frame.getState();
		return ((state & JFrame.MAXIMIZED_HORIZ) == JFrame.MAXIMIZED_HORIZ)
			|| ((state & JFrame.MAXIMIZED_VERT) == JFrame.MAXIMIZED_VERT);
	}
	
	private function isNeedToViewIconifiedButton():Boolean{
		return frame.isResizable() && frame.getState() != JFrame.ICONIFIED;
	}
	
	private function isNeedToViewResizeButton():Boolean{
		return frame.isResizable();
	}
	
	private function isNeedToViewCloseButton():Boolean{
		return frame.isClosable();
	}

	private function adjustButtons() : Void {
		iconifiedButton.setVisible(isNeedToViewIconifiedButton());
		resizeButton.setVisible(isNeedToViewResizeButton());
		closeButton.setVisible(isNeedToViewCloseButton());
	}

	private function adjustResizerEnable() : Void {
		frame.getResizer().setEnabled(frame.isResizable() && frame.getState() == JFrame.NORMAL);
	}

}
