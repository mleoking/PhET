/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.border.DecorateBorderParser;
import org.aswing.border.EmptyBorder;
import org.aswing.Insets;

/**
 *  Parses {@link org.aswing.border.EmptyBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.EmptyBorderParser extends DecorateBorderParser {
	
	/**
	 * Constructor.
	 */
	public function EmptyBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, border:EmptyBorder) {
		
		border = super.parse(awml, border);
		
		return border;
	}

	private function parseChild(awml:XMLNode, nodeName:String, border:EmptyBorder):Void {
		
		super.parseChild(awml, nodeName, border);

		switch(nodeName) {
			case AwmlConstants.NODE_INSETS:
				var insets:Insets = AwmlParser.parse(awml);
				if (insets != null) border.setInsets(insets);
				break; 
		}
	}
	
    private function getClass(Void):Function {
    	return EmptyBorder;	
    }
	
}