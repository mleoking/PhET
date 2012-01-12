/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.FloorPane;
import org.aswing.geom.Dimension;
import org.aswing.util.Delegate;
import org.aswing.util.MCUtils;

/**
 * JLoadPane, a container load a external image/animation to be its floor.
 * @see org.aswing.JAttachPane
 * @author iiley
 */
class org.aswing.overflow.JLoadPane extends FloorPane {
	
	/**
	 * When the file that was loaded.
	 *<br>
	 * onLoadComplete(source:Component)
	 */	
	public static var ON_LOAD_COMPLETE:String = "onLoadComplete";
	/**
	 * When the file loaded has failed to load.<br>
	 * errorCode:A string that explains the reason for the failure, either "URLNotFound" or "LoadNeverCompleted".
	 *<br>
	 * onLoadError(source:Component, errorCode:String)
	 */	
	public static var ON_LOAD_ERROR:String = "onLoadError";
	/**
	 * When the actions on the first frame of the loaded clip have been executed.
	 *<br>
	 * onLoadInit(source:Component)
	 */	
	public static var ON_LOAD_INIT:String = "onLoadInit";
	/**
	 * Fires every time the loading content is written to the hard disk during the loading process
	 *<br>
	 * onLoadProgress(source:Component, loadedBytes:Number, totalBytes:Number)
	 */	
	public static var ON_LOAD_PROGRESS:String = "onLoadProgress";
	/**
	 * When the a call has begun to download a file.
	 *<br>
	 * onLoadStart(source:Component)
	 */	
	public static var ON_LOAD_START:String = "onLoadStart";
	
	
	private var movieClipLoader:MovieClipLoader;
	private var loaderMC:MovieClip;
	private var loadedError:Boolean;
	private var lockroot:Boolean;
	
	/**
	 * JLoadPane(path:String, prefferSizeStrategy:Number) <br>
	 * JLoadPane(path:String) prefferSizeStrategy default to PREFER_SIZE_BOTH<br>
	 * JLoadPane() path default to null,prefferSizeStrategy default to PREFER_SIZE_BOTH<br>
	 * Creates a JLoadPane with a path to load external image or animation file.
	 * @param path the path of the extenal image/animation file.
	 * @param prefferSizeStrategy the prefferedSize count strategy. Must be one of below:
	 * <ul>
	 * <li>{@link org.aswing.FloorPane#PREFER_SIZE_BOTH}
	 * <li>{@link org.aswing.FloorPane#PREFER_SIZE_IMAGE}
	 * <li>{@link org.aswing.FloorPane#PREFER_SIZE_LAYOUT}
	 * </ul>
	 * @see #setPath()
	 */
	public function JLoadPane(path:String, prefferSizeStrategy:Number) {
		super(path, prefferSizeStrategy);
		setName("JLoadPane");
		lockroot = false;
		movieClipLoader = new MovieClipLoader();
		var lis:Object = new Object();
		lis["onLoadComplete"] = Delegate.create(this, __onLoadComplete);
		lis["onLoadError"] = Delegate.create(this, __onLoadError);
		lis["onLoadInit"] = Delegate.create(this, __onLoadInit);
		lis["onLoadProgress"] = Delegate.create(this, __onLoadProgress);
		lis["onLoadStart"] = Delegate.create(this, __onLoadStart);
		movieClipLoader.addListener(lis);
		loadedError = false;
	}
	
	/**
	 * Returns the loaded target movieclip.
	 * @return the loaded target movieclip.
	 * @see #getFloorMC()
	 */
	public function getFloorMC():MovieClip{
		return loaderMC;
	}
	
	/**
	 * Returns is error loaded.
	 * @see #ON_LOAD_ERROR
	 */
	public function isLoadedError():Boolean{
		return loadedError;
	}
	
	/**
	 * load the floor content.
	 * <p> here it is empty.
	 * Subclass must override this method to make loading.
	 */
	private function loadFloor():Void{
		if(MCUtils.isMovieClipExist(loaderMC)){
			loadedError = false;
			loaderMC._lockroot = lockroot;
			movieClipLoader.loadClip(getPath(), loaderMC);
		}
	}
	
	/**
	 * Create the floor mc.
	 * <p> here it is empty.
	 * Subclass must override this method to make creating.
	 */
	private function createFloorMC():MovieClip{
		if(MCUtils.isMovieClipExist(target_mc)){
			floorMC = creater.createMC(target_mc, "floor", getFloorDepth());
			loaderMC = creater.createMCWithName(floorMC, "loader");
		}
		return floorMC;
	}
	
	/**
	 * Sets whether the _root refers of the loaded swf file is locked to itself.(default value is false)
	 * @param b whether the _root refers of the loaded swf file is locked to itself.
	 * @see adobe doc about this {@link http://livedocs.macromedia.com/flash/8/main/wwhelp/wwhimpl/js/html/wwhelp.htm?href=Part4_ASLR2.html}
	 */
	public function setLockroot(b:Boolean):Void{
		lockroot = b;
		loaderMC._lockroot = b;
	}
	
	/**
	 * Returns a Boolean value that specifies what _root refers to when the SWF file is loaded into the pane.(default value is false)
	 * @return whether the _root refers of the loaded swf file is locked to itself
	 * @see adobe doc about this {@link http://livedocs.macromedia.com/flash/8/main/wwhelp/wwhimpl/js/html/wwhelp.htm?href=Part4_ASLR2.html}
	 */
	public function isLockroot():Boolean{
		return lockroot;
	}
	
	/**
	 * Returns a object contains <code>bytesLoaded</code> and <code>bytesTotal</code> 
	 * properties that indicate the current loading status.
	 */
	public function getProgress():Object{
		return movieClipLoader.getProgress(loaderMC);
	}
	
	//-----------------------------------------------

	private function __onLoadComplete(targetMC:MovieClip):Void{
		dispatchEvent(createEventObj(ON_LOAD_COMPLETE));
	}
	
	private function __onLoadError(targetMC:MovieClip, errorCode:String):Void{
		loadedError = true;
		dispatchEvent(createEventObj(ON_LOAD_ERROR, errorCode));
	}
	
	private function __onLoadInit(targetMC:MovieClip):Void{
		setFloorOriginalSize(new Dimension(floorMC._width, floorMC._height));
		setLoaded(true);
		valid = false;
		dispatchEvent(ON_LOAD_INIT, createEventObj(ON_LOAD_INIT));
		revalidate();
		validate();
	}
	
	private function __onLoadProgress(targetMC:MovieClip, loadedBytes:Number, totalBytes:Number):Void{
		dispatchEvent(createEventObj(ON_LOAD_PROGRESS, loadedBytes, totalBytes));
	}
	
	private function __onLoadStart(targetMC:MovieClip):Void{
		dispatchEvent(createEventObj(ON_LOAD_START));
	}
}
