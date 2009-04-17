/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASWingConstants;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.util.DepthManager;
import org.aswing.util.MCUtils;

/**
 * Abstract class for A container with a decorative floor movieclip.
 * <p> External content will be load automatically when the pane was create on the stage if floorEnabled.
 * @see org.aswing.overflow.JLoadPane
 * @see org.aswing.JAttachPane
 * @author iiley
 */
class org.aswing.FloorPane extends Container {
	
	/**
	 * preffered size of this component will be the fit to contain both size of extenal image/animation
	 *  and counted from <code>LayoutManager</code>
	 */
	public static var PREFER_SIZE_BOTH:Number = 0;
	/**
	 * preffered size of this component will be the size of extenal image/animation
	 */
	public static var PREFER_SIZE_IMAGE:Number = 1;
	/**
	 * preffered size of this component will be counted by <code>LayoutManager</code>
	 */	
	public static var PREFER_SIZE_LAYOUT:Number = 2; 	
	
	/**
	 * Image scale mode is disabled.
	 */
	public static var SCALE_NONE:Number = 0;
	/**
	 * Proportional scale mode to fit pane.
	 */
	public static var SCALE_FIT_PANE:Number = 1;
	/**
	 * Stretch content to fill whole pane.
	 */
	public static var SCALE_STRETCH_PANE:Number = 2;
	/**
	 * Proportional image scale mode to fit pane's width.
	 */
	public static var SCALE_FIT_WIDTH:Number = 3;
	/**
	 * Proportional scale mode to fit pane's height.
	 */
	public static var SCALE_FIT_HEIGHT:Number = 4;
	/**
	 * Custom scaling of the image.
	 * @see setCustomScale
	 */
	public static var SCALE_CUSTOM:Number = 5;
	
	
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
    
    private var path:String;
	private var floorEnabled:Boolean;
	private var floorMC:MovieClip;
	private var floorMCDepth:Number;
	private var floorMCMask:MovieClip;
	private var floorMCMaskDepth:Number;
	private var maskFloor:Boolean;
	private var floorLoaded:Boolean;
	private var prefferSizeStrategy:Number;
    private var verticalAlignment:Number;
    private var horizontalAlignment:Number;
    private var scaleMode:Number;
    private var customScale:Number;
    private var actualScale:Number;
    private var floorOriginalSize:Dimension;
    private var hadscaled:Boolean;
    private var offsetX:Number;
    private var offsetY:Number;
	
	/**
	 * FloorPane(path:String, prefferSizeStrategy:Number) <br>
	 * FloorPane(path:String) prefferSizeStrategy default to PREFER_SIZE_BOTH<br>
	 * FloorPane() path default to null,prefferSizeStrategy default to PREFER_SIZE_BOTH
	 * <p>
	 * Creates a FloorPane with a path to load external content.
	 * @param path the path of the external content.
	 * @param prefferSizeStrategy the prefferedSize count strategy. Must be one of below:
	 * <ul>
	 * <li>{@link #PREFER_SIZE_BOTH}
	 * <li>{@link #PREFER_SIZE_IMAGE}
	 * <li>{@link #PREFER_SIZE_LAYOUT}
	 * </ul>
	 * @see #setPath()
	 */
	private function FloorPane(path:String, prefferSizeStrategy:Number) {
		super();
		
		this.path = path;
		if(prefferSizeStrategy == undefined){
			prefferSizeStrategy = PREFER_SIZE_BOTH;
		}
		this.prefferSizeStrategy = prefferSizeStrategy;
		
    	verticalAlignment = TOP;
    	horizontalAlignment = LEFT;
    	scaleMode = SCALE_NONE;
    	actualScale = 100;
    	customScale = 100;
    	hadscaled = false;
    	maskFloor = true;
		floorOriginalSize = null;
		floorEnabled = true;
		floorLoaded = false;
		offsetX = 0;
		offsetY = 0;
		
		setFocusable(false);
	}

	
	/**
	 * Sets the path to load/attach image/animation file or symbol.
	 * This method will cause <code>reload()</code> action if the path 
	 * is different from old one.
	 * @param path the path of external image/animation file or the linkageID of a symbol.
	 * @see #reload()
	 */
	public function setPath(path:String):Void{
		if(path != this.path){
			this.path = path;
			reload();
		}
	}
	
	public function getPath():String{
		return path;
	}
	
	/**
	 * Sets the preffered size counting strategy. Must be one of below:
	 * <ul>
	 * <li>{@link #PREFER_SIZE_BOTH}
	 * <li>{@link #PREFER_SIZE_IMAGE}
	 * <li>{@link #PREFER_SIZE_LAYOUT}
	 * </ul>
	 */
	public function setPrefferSizeStrategy(p:Number):Void{
		prefferSizeStrategy = p;
	}
	
	/**
	 * Returns the preffered size counting strategy.
	 * @see #setPrefferSizeStrategy()
	 */
	public function getPrefferSizeStrategy():Number{
		return prefferSizeStrategy;
	}	
	
    /**
     * Returns the vertical alignment of the image/animation.
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
     * Sets the vertical alignment of the image/animation. 
     * @param alignment  one of the following values:
     * <ul>
     * <li>ASWingConstants.CENTER (the default)
     * <li>ASWingConstants.TOP
     * <li>ASWingConstants.BOTTOM
     * </ul>
     * Default is TOP.
     */
    public function setVerticalAlignment(alignment:Number):Void {
        if (alignment == verticalAlignment){
        	return;
        }else{
        	verticalAlignment = alignment;
        	revalidate();
        }
    }
    
    /**
     * Returns the horizontal alignment of the image/animation.
     * @return the <code>horizontalAlignment</code> property,
     *		one of the following values:
     * <ul>
     * <li>ASWingConstants.RIGHT (the default)
     * <li>ASWingConstants.LEFT
     * <li>ASWingConstants.CENTER
     * </ul>
     * Default is LEFT.
     */
    public function getHorizontalAlignment():Number{
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the image/animation.
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
        }
    }
    
    /**
     * Sets new content scale mode.
     * <p><b>Note:</b>Take care to use #scaleMode to load a swf, 
     * because swf has different size at different frame or
     * when some symbol invisible/visible.
     * @param mode the new image scale mode.
	 * <ul>
	 * <li>{@link #SCALE_NONE}
	 * <li>{@link #SCALE_PROPORTIONAL}
	 * <li>{@link #SCALE_COMPLETE}
	 * </ul>
     */
    public function setScaleMode(mode:Number):Void{
    	if(scaleMode != mode){
    		scaleMode = mode;
    		revalidate();
    	}
    }
    
    /**
     * Returns current image scale mode. 
     * @return current image scale mode.
	 * <ul>
	 * <li>{@link #SCALE_NONE}
	 * <li>{@link #SCALE_PROPORTIONAL}
	 * <li>{@link #SCALE_COMPLETE}
	 * </ul>
     */
    public function getScaleMode():Number{
    	return scaleMode;
    }
    
    /**
     * Sets new custom scale value in percents. Automatically turns scale mode into #SCALE_CUSTOM.
     * @param scale the new scale 
     * @see #setScaleMode
     */
    public function setCustomScale(scale:Number):Void {
    	setScaleMode(SCALE_CUSTOM);
    	if (customScale != scale) {
    		customScale = scale;
    		revalidate();	
    	}
    }
    
    /**
     * Returns current actual scale value in percents. If <code>scaleMode</code> is
     * #SCALE_STRETCH_PANE returns <code>null</code>. 
     */
    public function getActualScale():Number {
    	return actualScale;	
    }

    /**
     * Returns current custom scale value in percents.
     */
    public function getCustomScale():Number {
    	return customScale;	
    }
    
    /**
     * Sets the x offset of the position of the loaded image/animation.
     * If you dont want to locate the content to the topleft of the pane, you can set the offsets.
     * @param offset the x offset 
     */
    public function setOffsetX(offset:Number):Void{
    	if(offsetX != offset){
    		offsetX = offset;
    		revalidate();
    	}
    }
    
    /**
     * Sets the y offset of the position of the loaded image/animation.
     * If you dont want to locate the content to the topleft of the pane, you can set the offsets.
     * @param offset the y offset 
     */
    public function setOffsetY(offset:Number):Void{
    	if(offsetY != offset){
    		offsetY = offset;
    		revalidate();
    	}
    }    
    
    /**
     * @see #setOffsetX()
     */
    public function getOffsetX():Number{
    	return offsetX;
    }
    
    /**
     * @see #setOffsetY()
     */
    public function getOffsetY():Number{
    	return offsetY;
    }
		
	/**
	 * Returns the floor target movie clip.<br>
	 * You should take care to do operation at this MC, if you remove it, 
	 * the component will create another instead when next reload.
	 * @return the movieclip where the extenal image/animation will be loaded in or 
	 * the movieclip attached.
	 * @see #getFloorMC()
	 * @see org.aswing.overflow.JLoadPane
	 * @see org.aswing.JAttachPane
	 */
	public function getFloorMC():MovieClip{
		return floorMC;
	}
	
	/**
	 * Disable the load ability.
	 * Removes loaded image or animation(by remove the LoadTarget MovieClip). And will not load any thing from now on.
	 * @see #getFloorMC()
	 * @see #enableFloor()
	 * @see #isEnabledFloor()
	 * @see #reload()
	 */
	public function disableFloor():Void{
		if(floorEnabled){
			floorEnabled = false;
			setLoaded(false);
			removeFloorMCs();
		}
	}
	
	/**
	 * Enable the load ability, can call reload to try to load content if it is not loaded yet.
	 * @see #isEnabledFloor()
	 * @see #enableFloor()
	 * @see #reload()
	 */
	public function enableFloor():Void{
		if(!floorEnabled){
			floorEnabled = true;
			reload();
		}
	}
	
	/**
	 * Returns whether load function is enabled. Default is true.
	 * @see #enableFloor()
	 * @see #disableFloor()
	 */
	public function isEnabledFloor():Boolean{
		return floorEnabled;
	}
	
	/**
	 * Returns is the extenal image/animation file was loaded ok.
	 * @return true if the file loaded ok, otherwish return false
	 */
	public function isLoaded():Boolean{
		return floorLoaded;
	}
	
	/**
	 * Returns the extenal image/animation/symbol 's original size.
	 * If the external content are not loaded yet, return null.
	 * @return the extenal content's original size. null if it is not loaded yet.
	 */
	public function getFloorOriginalSize():Dimension{
		if(isLoaded()){
			return floorOriginalSize;
		}else{
			return null;
		}
	}
	

	
	private function create():Void{
		super.create();
		if(MCUtils.isMovieClipExist(target_mc)){
			floorMCDepth = DepthManager.getNextAvailableDepth(target_mc);
			floorMCMaskDepth = floorMCDepth + 1;
			reload();
		}
	}
	
	/**
	 * layout this container
	 */
	public function doLayout():Void{
		super.doLayout();
		fitImage();
	}	
	
	private function fitImage():Void{
		if(isLoaded()){
			// for child classes which redefines floorMC
			var floorMC:MovieClip = getFloorMC();
			var b:Rectangle = getPaintBounds();
			var s:Dimension = countFloorSize();
			floorMCMask._x = b.x;
			floorMCMask._y = b.y;
			floorMCMask._width = b.width;
			floorMCMask._height = b.height;
			if(scaleMode == SCALE_STRETCH_PANE){
				floorMC._x = b.x - offsetX;
				floorMC._y = b.y - offsetY;
				floorMC._width = s.width;
				floorMC._height = s.height;
				hadscaled = true;
			} else if (scaleMode == SCALE_FIT_PANE || scaleMode == SCALE_FIT_WIDTH || scaleMode == SCALE_FIT_HEIGHT || scaleMode == SCALE_CUSTOM) {
				floorMC._width = s.width;
				floorMC._height = s.height;
				alignFloor();
				hadscaled = true;
			}else{
				if(hadscaled){
					if(floorMC._width != floorOriginalSize.width){
						floorMC._width = floorOriginalSize.width;
					}
					if(floorMC._height != floorOriginalSize.height){
						floorMC._height = floorOriginalSize.height;
					}
					hadscaled = false;
				}
				alignFloor();
			}
			// calc current scale
			if (scaleMode != SCALE_STRETCH_PANE) {
				actualScale = floorMC._width / floorOriginalSize.width * 100;
			} else {
				actualScale = null;	
			}
		}
	}
	
	/**
	 * Aligns floorMC clip. 
	 */
	private function alignFloor(b:Rectangle):Void {
		// for child classes which redefines floorMC
		var floorMC:MovieClip = getFloorMC();
		if (b == null) b = getPaintBounds();
		
		var mx:Number, my:Number;
		if(horizontalAlignment == CENTER){
			mx = b.x + (b.width - floorMC._width)/2;
		}else if(horizontalAlignment == RIGHT){
			mx = b.x + (b.width - floorMC._width);
		}else{
			mx = b.x;
		}
		if(verticalAlignment == CENTER){
			my = b.y + (b.height - floorMC._height)/2;
		}else if(verticalAlignment == BOTTOM){
			my = b.y + (b.height - floorMC._height);
		}else{
			my = b.y;
		}
		floorMC._x = mx - offsetX;
		floorMC._y = my - offsetY;
	}	
	
	/**
	 * count preffered size base on prefferSizeStrategy.
	 */
	private function countPreferredSize():Dimension{
		var size:Dimension = super.countPreferredSize();
		var sizeByMC:Dimension;
		var floorMC:MovieClip = getFloorMC();
		
		if(isLoaded()){
			sizeByMC = countFloorSize();
			sizeByMC = getInsets().getOutsideSize(sizeByMC);
		}else{
			sizeByMC = size;
		}
		
		if(prefferSizeStrategy == PREFER_SIZE_IMAGE){
			return sizeByMC;
		}else if(prefferSizeStrategy == PREFER_SIZE_LAYOUT){
			return size;
		}else{
			return new Dimension(
				Math.max(sizeByMC.width, size.width), 
				Math.max(sizeByMC.height, size.height));
		}
	}	
	
	private function countFloorSize():Dimension {
		var b:Rectangle = getPaintBounds();
		var size:Dimension = new Dimension();
		
		if(scaleMode == SCALE_STRETCH_PANE){
			size.width = b.width;
			size.height = b.height;
		} else if (scaleMode == SCALE_FIT_PANE || scaleMode == SCALE_FIT_WIDTH || scaleMode == SCALE_FIT_HEIGHT) {
			var hScale:Number = floorOriginalSize.width / b.width;
			var vScale:Number = floorOriginalSize.height / b.height; 
			var scale:Number = 1;
			if (scaleMode == SCALE_FIT_WIDTH) {
				scale = hScale;
			} else if (scaleMode == SCALE_FIT_HEIGHT) {
				scale = vScale;
			} else {
				scale = Math.max(hScale, vScale);
			}
			size.width = floorOriginalSize.width/scale;
			size.height = floorOriginalSize.height/scale;
		} else if (scaleMode == SCALE_CUSTOM){
			size.width = floorOriginalSize.width*(customScale/100);
			size.height = floorOriginalSize.height*(customScale/100);
		} else {
			size.width = floorOriginalSize.width - offsetX;
			size.height = floorOriginalSize.height - offsetY;
		}
		
		return size; 
	}
	
	/**
	 * Reload the floor image/animation when enabledFoor. otherwish do nothing.
	 * @see #loadFloor()
	 * @see #createFloorMC()
	 * @see #createFloorMaskMC()
	 * @see org.aswing.overflow.JLoadPane
	 * @see org.aswing.JAttachPane
	 */
	public function reload():Void{
		if(isEnabledFloor()){
			removeFloorMCs();
			floorMC = createFloorMC();
			floorMCMask = createFloorMaskMC(floorMC);
			setMaskFloor(maskFloor);
			setLoaded(false);
			loadFloor();
		}
	}
	
	public function isMaskFloor():Boolean{
		return maskFloor;
	}
	
	public function setMaskFloor(m:Boolean):Void{
		maskFloor = m;
		if(m){
			floorMC.setMask(floorMCMask);
		}else{
			floorMC.setMask(null);
		}
	}
	
	private function removeFloorMCs():Void{
		floorMC.setMask(null);
		floorMC.unloadMovie();
		floorMC.removeMovieClip();
		floorMCMask.clear(); //just clear it, not remove, intend to hold the depths
		floorMC = floorMCMask = null;
	}
	
	/**
	 * Creates mask movieclip at the depth above floorMC.
	 */
	private function createFloorMaskMC(floorMC:MovieClip):MovieClip{
		if(MCUtils.isMovieClipExist(floorMC)){
			var parentMC:MovieClip = MovieClip(floorMC._parent);
			floorMCMask = creater.createMC(parentMC, "floorMask", getFloorMaskDepth());
			var g:Graphics = new Graphics(floorMCMask);
			g.fillRectangle(new SolidBrush(0xFF0000), 0, 0, 1, 1);
			floorMCMask._visible = false;
			return floorMCMask;
		}else{
			return null;
		}
	}
	
	private function setLoaded(b:Boolean):Void{
		floorLoaded = b;
	}
	
	private function setFloorOriginalSize(size:Dimension):Void{
		floorOriginalSize = new Dimension(size.width, size.height);
	}
	
	/**
	 * Return the depth to create floor mc.
	 */
	private function getFloorDepth():Number{
		return floorMCDepth;
	}
	
	/**
	 * Return the depth to create floor mask mc.
	 */
	private function getFloorMaskDepth():Number{
		return floorMCMaskDepth;
	}
	//////////////////////
	
	/**
	 * load the floor content.
	 * <p> here it is empty.
	 * Subclass must override this method to make loading.
	 */
	private function loadFloor():Void{
	}
	
	/**
	 * Create the floor mc.
	 * <p> here it is empty.
	 * Subclass must override this method to make creating.
	 */
	private function createFloorMC():MovieClip{
		return null;
	}
}
