/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.core.ObjectParser;
import org.aswing.util.HashMap;

/**
 *  Parses {@link org.aswing.ASColor} elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.common.ColorParser extends ObjectParser {
	
	private static var ATTR_RGB:String = "rgb";
	private static var ATTR_ALPHA:String = "alpha";
	
	private static var COLOR_BLACK:String = "black";
	private static var COLOR_BLUE:String = "blue";
	private static var COLOR_CYAN:String = "cyan";
	private static var COLOR_DARK_GREY:String = "dark-grey";
	private static var COLOR_GRAY:String = "gray";
	private static var COLOR_GREEN:String = "green";
	private static var COLOR_LIGHT_GREY:String = "light-grey";
	private static var COLOR_MAGENTA:String = "magenta";
	private static var COLOR_ORANGE:String = "orange";
	private static var COLOR_PINK:String = "pink";
	private static var COLOR_RED:String = "red";
	private static var COLOR_WHITE:String = "white";
	private static var COLOR_YELLOW:String = "yellow";
	
	private var rgbMap:HashMap;
	
	/**
	 * Constructor.
	 */
	public function ColorParser(Void) {
		super();
		
		// init rgb map
		rgbMap = new HashMap();
		rgbMap.put(COLOR_BLACK, ASColor.BLACK.getRGB());
		rgbMap.put(COLOR_BLUE, ASColor.BLUE.getRGB());
		rgbMap.put(COLOR_CYAN, ASColor.CYAN.getRGB());
		rgbMap.put(COLOR_DARK_GREY, ASColor.DARK_GRAY.getRGB());
		rgbMap.put(COLOR_GRAY, ASColor.GRAY.getRGB());
		rgbMap.put(COLOR_GREEN, ASColor.GREEN.getRGB());
		rgbMap.put(COLOR_LIGHT_GREY, ASColor.LIGHT_GRAY.getRGB());
		rgbMap.put(COLOR_MAGENTA, ASColor.MAGENTA.getRGB());
		rgbMap.put(COLOR_ORANGE, ASColor.ORANGE.getRGB());
		rgbMap.put(COLOR_PINK, ASColor.PINK.getRGB());
		rgbMap.put(COLOR_RED, ASColor.RED.getRGB());
		rgbMap.put(COLOR_WHITE, ASColor.WHITE.getRGB());
		rgbMap.put(COLOR_YELLOW, ASColor.YELLOW.getRGB());
	}
	
	public function parse(awml:XMLNode):ASColor {
		
		// init rgb
		var rgbValue:String = getAttributeAsString(awml, ATTR_RGB, null);
		var rgb:Number = rgbMap.get(rgbValue);
		
		if (rgb == null) {
			rgb = parseInt(rgbValue, 16);
			if (isNaN(rgb)) rgb = null;	
		}
		
		// init alpha
		var alpha:Number = getAttributeAsNumber(awml, ATTR_ALPHA, null);
		
		// init color
		var color:ASColor = new ASColor(rgb, alpha);	
	
		// process super
		super.parse(awml, color);
	
		return color;
	}

}