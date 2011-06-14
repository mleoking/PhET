/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.border.DecorateBorderParser;
import org.aswing.border.BevelBorder;

/**
 *  Parses {@link org.aswing.border.BevelBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.BevelBorderParser extends DecorateBorderParser {
	
	private static var ATTR_TYPE:String = "type";
	
	private static var TYPE_LOWERED:String = "lowered";
	private static var TYPE_RAISED:String = "raised";
	
	
	/**
	 * Constructor.
	 */
	public function BevelBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, border:BevelBorder) {
		
		border = super.parse(awml, border);
		
		// init type
		var type:String = getAttributeAsString(awml, ATTR_TYPE, null);
		switch (type) {
			case TYPE_LOWERED:
				border.setBevelType(BevelBorder.LOWERED);
				break;	
			case TYPE_RAISED:
				border.setBevelType(BevelBorder.RAISED);
				break;	
		}
		
		return border;
	}

	private function parseChild(awml:XMLNode, nodeName:String, border:BevelBorder):Void {
		
		super.parseChild(awml, nodeName, border);

		switch(nodeName) {
			case AwmlConstants.NODE_HIGHLIGHT_INNER_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setHighlightInnerColor(color);
				break;
			case AwmlConstants.NODE_HIGHLIGHT_OUTER_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setHighlightOuterColor(color);
				break;
			case AwmlConstants.NODE_SHADOW_INNER_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setShadowInnerColor(color);
				break;
			case AwmlConstants.NODE_SHADOW_OUTER_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setShadowOuterColor(color);
				break;
		}
	}
	
    private function getClass(Void):Function {
    	return BevelBorder;	
    }
	
}