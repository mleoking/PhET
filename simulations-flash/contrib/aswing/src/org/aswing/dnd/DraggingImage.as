/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * The Image for dragging representing.
 * 
 * @author iiley
 */
interface org.aswing.dnd.DraggingImage {
	
	/**
	 * Paints the image for normal state of dragging.
	 */
	public function setCanvas(target : MovieClip) : Void ;
	
	/**
	 * Paints the image for accept state of dragging.(means drop allowed)
	 */
	public function switchToAcceptImage() : Void ;
	
	/**
	 * Paints the image for reject state of dragging.(means drop not allowed)
	 */
	public function switchToRejectImage() : Void ;
	
}