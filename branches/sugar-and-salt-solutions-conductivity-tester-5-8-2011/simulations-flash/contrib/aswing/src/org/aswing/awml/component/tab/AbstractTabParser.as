/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlTabInfo;

/**
 *  Parses tabs for tab-based components.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.tab.AbstractTabParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	private function AbstractTabParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, tab:AwmlTabInfo, namespace:AwmlNamespace) {
		
		if (tab == null) {
			tab = new AwmlTabInfo();
		}
		
		tab = super.parse(awml, tab, namespace);
		
		return tab;
	}

}