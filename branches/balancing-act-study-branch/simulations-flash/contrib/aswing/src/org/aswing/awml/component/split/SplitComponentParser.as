/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.Component;
import org.aswing.overflow.JSplitPane;

/**
 *  Parses components for {@link org.aswing.overflow.JSplitPane}.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.split.SplitComponentParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function SplitComponentParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, split:JSplitPane, namespace:AwmlNamespace) {

		for (var i = 0; i < awml.childNodes.length; i++) {
			if(AwmlUtils.isComponentNode(AwmlUtils.getNodeName(awml.childNodes[i]))) {
				var component:Component = Component(AwmlParser.parse(awml.childNodes[i], null, namespace));
				if (component != null) return component;
			}	
		}
		
		return null;
	}

}