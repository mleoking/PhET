/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.core.CreatableObjectParser;
import org.aswing.border.Border;
import org.aswing.border.DecorateBorder;

/**
 *  Parses {@link org.aswing.border.DecorateBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.DecorateBorderParser extends CreatableObjectParser {
	
	/**
	 * Private Constructor.
	 */
	private function DecorateBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, border:DecorateBorder) {
		
		if (border == null) {
			border = create(awml);	
		}
		
		super.parse(awml, border);
		
		return border;
	}

	private function parseChild(awml:XMLNode, nodeName:String, border:DecorateBorder):Void {
		
		super.parseChild(awml, nodeName, border);
		
		if (AwmlUtils.isBorderNode(nodeName)) {
			var interior:Border = AwmlParser.parse(awml);
			if (interior != null) border.setInterior(interior); 
		}
	}	

}