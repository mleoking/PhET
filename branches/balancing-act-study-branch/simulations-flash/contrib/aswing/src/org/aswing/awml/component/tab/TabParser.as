/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlTabInfo;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.component.tab.AbstractTabParser;
import org.aswing.Component;
import org.aswing.Icon;

/**
 *  Parses tabs for {@link org.aswing.overflow.JTabbedPane} and
 *  {@link org.aswing.overflow.JAccordion}.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.tab.TabParser extends AbstractTabParser {
	
	private static var ATTR_TITLE:String = "title";
	private static var ATTR_TOOL_TIP:String = "tool-tip";
	
	/**
	 * Constructor.
	 */
	public function TabParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, tab:AwmlTabInfo, namespace:AwmlNamespace) {

		tab = super.parse(awml, tab, namespace);
		
		// init title
		tab.title = getAttributeAsString(awml, ATTR_TITLE, "");
		
		// init tool tip
		tab.tooltip = getAttributeAsString(awml, ATTR_TOOL_TIP, null);

		return tab;
	}

    private function parseChild(awml:XMLNode, nodeName:String, tab:AwmlTabInfo, namespace:AwmlNamespace):Void {
    	super.parseChild(awml, nodeName, tab, namespace);
    
    	if (AwmlUtils.isIconNode(nodeName)) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) tab.icon = icon;	
    	} else if (AwmlUtils.isComponentNode(nodeName)) {
    		var component:Component = AwmlParser.parse(awml, null, namespace);
    		if (component != null) tab.component = component;	
    	}
    }

}