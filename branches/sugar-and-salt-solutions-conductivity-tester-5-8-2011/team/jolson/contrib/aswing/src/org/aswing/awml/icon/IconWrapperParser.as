/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.Icon;

/**
 * Wraps custom icon.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.icon.IconWrapperParser extends AbstractParser {
	
	/**
	 * Constructor.
	 */
	public function IconWrapperParser(Void) {
		super();
	}
	
	/**
	 * Searches for {@link org.aswing.Icon} implementation among child nodes, creates icon 
	 * instance and returns it.
	 */
	public function parse(awml:XMLNode):Icon {
		
		for (var i = 0; i < awml.childNodes.length; i++) {
			if(AwmlUtils.isIconNode(AwmlUtils.getNodeName(awml.childNodes[i]))) {
				var icon:Icon = Icon(AwmlParser.parse(awml.childNodes[i]));
				if (icon != null) return icon;
			}	
		}
		
		return null;
	}

}