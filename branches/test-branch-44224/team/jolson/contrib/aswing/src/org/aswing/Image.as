/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * A interface that represent graphical images, can paint to any target movieclip.
 * @author iiley
 */
interface org.aswing.Image {
	/**
	 * Returns the image's width.
	 */
	public function getImageWidth():Number;
	/**
	 * Returns the image's height.
	 */
	public function getImageHeight():Number;
	/**
	 * Paints the image to a specified movieclip at point(x, y)
	 */
	public function paintImage(target:MovieClip, x:Number, y:Number):Void;
	
}