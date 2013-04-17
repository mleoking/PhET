/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.FloorPane;
import org.aswing.geom.Dimension;
import org.aswing.util.MCUtils;

/**
 * JAttachPane, a container attach flash symbol in library to be its floor.
 * @see org.aswing.overflow.JLoadPane
 * @author iiley
 */
class org.aswing.JAttachPane extends FloorPane {
	
	/**
	 * Event onAttached, when the symbol was attached.
	 * onAttached(source:JAttachPane)
	 */	
	public static var ON_ATTACHED:String = "onAttached";
		
	/**
	 * JAttachPane(path:String, prefferSizeStrategy:Number) <br>
	 * JAttachPane(path:String) prefferSizeStrategy default to PREFER_SIZE_BOTH<br>
	 * JAttachPane() path default to null,prefferSizeStrategy default to PREFER_SIZE_BOTH<br>
	 * Creates a JAttachPane with a path to attach a symbol from library
	 * @param path the linkageID of the symbol in library
	 * @param prefferSizeStrategy the prefferedSize count strategy. Must be one of below:
	 * <ul>
	 * <li>{@link org.aswing.FloorPane#PREFER_SIZE_BOTH}
	 * <li>{@link org.aswing.FloorPane#PREFER_SIZE_IMAGE}
	 * <li>{@link org.aswing.FloorPane#PREFER_SIZE_LAYOUT}
	 * </ul>
	 * @see #setPath()
	 */
	public function JAttachPane(path:String, prefferSizeStrategy:Number) {
		super(path, prefferSizeStrategy);
		setName("JAttachPane");
	}
	
	/**
	 * Returns the attached movieclip.
	 * @return the movieclip attached.
	 */
	public function getFloorMC():MovieClip{
		return floorMC;
	}	
	
	/**
	 * load the floor content.
	 * <p> here it is empty.
	 * Subclass must override this method to make loading.
	 */
	private function loadFloor():Void{
		if(MCUtils.isMovieClipExist(floorMC)){
			setLoaded(true);
			revalidate();
		}
	}
	
	/**
	 * Create the floor mc.
	 * <p> here it is empty.
	 * Subclass must override this method to make creating.
	 */
	private function createFloorMC():MovieClip{
		if(MCUtils.isMovieClipExist(target_mc)){
			floorMC = creater.attachMC(target_mc, getPath(), "floor", getFloorDepth());
			setFloorOriginalSize(new Dimension(floorMC._width, floorMC._height));
			dispatchEvent(ON_ATTACHED, createEventObj(ON_ATTACHED));
		}
		return floorMC;
	}
}
