/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.border.DecorateBorderParser;
import org.aswing.border.SideLineBorder;

/**
 *  Parses {@link org.aswing.border.SideLineBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.SideLineBorderParser extends DecorateBorderParser {
	
	private static var ATTR_THICKNESS:String = "thickness";
	private static var ATTR_SIDE:String = "side";
	
	private static var SIDE_NORTH:String = "north";
	private static var SIDE_SOUTH:String = "south";
	private static var SIDE_WEST:String = "west";
	private static var SIDE_EAST:String = "east";
	
	/**
	 * Constructor.
	 */
	public function SideLineBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, border:SideLineBorder) {
		
		border = super.parse(awml, border);
		
		// init thickness
		border.setThickness(getAttributeAsNumber(awml, ATTR_THICKNESS, border.getThickness()));
		
		// init side
		var side:String = getAttributeAsString(awml, ATTR_SIDE, null);
		switch (side) {
			case SIDE_NORTH:
				border.setSide(SideLineBorder.NORTH);
				break;	
			case SIDE_SOUTH:
				border.setSide(SideLineBorder.SOUTH);
				break;	
			case SIDE_WEST:
				border.setSide(SideLineBorder.WEST);
				break;	
			case SIDE_EAST:
				border.setSide(SideLineBorder.EAST);
				break;	
		}
				
		return border;
	}

	private function parseChild(awml:XMLNode, nodeName:String, border:SideLineBorder):Void {
		
		super.parseChild(awml, nodeName, border);

		switch(nodeName) {
			case AwmlConstants.NODE_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setColor(color);
				break; 
		}
	}
	
    private function getClass(Void):Function {
    	return SideLineBorder;	
    }
	
}