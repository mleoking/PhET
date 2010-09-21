/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ContainerParser;
import org.aswing.Insets;
import org.aswing.JToolBar;

/**
 * Parses {@link org.aswing.JToolBar} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ToolBarParser extends ContainerParser {
	
	private static var ATTR_FLOATABLE:String = "floatable";
	private static var ATTR_ORIENTATION:String = "orientation";
	
	private static var ORIENTATION_HORIZONTAL:String = "horizontal";
	private static var ORIENTATION_VERTICAL:String = "vertical"; 
	
	/**
	 * Constructor.
	 */
	public function ToolBarParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, bar:JToolBar, namespace:AwmlNamespace) {
		
		bar = super.parse(awml, bar, namespace);
		
		// init floatable
		bar.setFloatable(getAttributeAsBoolean(awml, ATTR_FLOATABLE, bar.isFloatable()));
		
		// set orientation
		var orientation:String = getAttributeAsString(awml, ATTR_ORIENTATION, null);
		switch (orientation) {
			case ORIENTATION_HORIZONTAL:
				bar.setOrientation(JToolBar.HORIZONTAL);
				break;
			case ORIENTATION_VERTICAL:
				bar.setOrientation(JToolBar.VERTICAL);
				break;
		}
		
		return bar;
	}

	private function parseChild(awml:XMLNode, nodeName:String, bar:JToolBar, namespace:AwmlNamespace):Void {

		super.parseChild(awml, nodeName, bar, namespace);
		
		switch (nodeName) {
			case AwmlConstants.NODE_MARGINS:
				var margins:Insets = AwmlParser.parse(awml);
				if (margins != null) bar.setMargin(margins);
				break;
		}
	}

    private function getClass(Void):Function {
    	return JToolBar;	
    }
	
}
