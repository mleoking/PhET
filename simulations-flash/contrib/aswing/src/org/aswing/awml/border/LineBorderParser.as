/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.border.DecorateBorderParser;
import org.aswing.border.LineBorder;

/**
 *  Parses {@link org.aswing.border.LineBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.LineBorderParser extends DecorateBorderParser {
	
	private static var ATTR_THICKNESS:String = "thickness";
	private static var ATTR_ROUND:String = "round";
	
	/**
	 * Constructor.
	 */
	public function LineBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, border:LineBorder) {
		
		border = super.parse(awml, border);
		
		// init thickness
		border.setThickness(getAttributeAsNumber(awml, ATTR_THICKNESS, border.getThickness()));
		
		// init round
		border.setRound(getAttributeAsNumber(awml, ATTR_ROUND, border.getRound()));
		
		return border;
	}

	private function parseChild(awml:XMLNode, nodeName:String, border:LineBorder):Void {
		
		super.parseChild(awml, nodeName, border);

		switch(nodeName) {
			case AwmlConstants.NODE_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setColor(color);
				break; 
		}
	}
	
    private function getClass(Void):Function {
    	return LineBorder;	
    }
	
}