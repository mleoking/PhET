/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
/**
 * Provides routines to parse object's properties.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.core.ObjectParser extends AbstractParser {
	
	/**
	 * Private constructor.
	 */
	private function ObjectParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, object:Object, namespace:AwmlNamespace) {

		super.parse(awml, object, namespace);		
		
		return object;
	}

	private function parseChild(awml:XMLNode, nodeName:String, object:Object, namespace:AwmlNamespace):Void {
		super.parseChild(awml, nodeName, object, namespace);
		
		switch (nodeName) {
			case AwmlConstants.NODE_PROPERTY:
				AwmlParser.parse(awml, object);
				break;	
			case AwmlConstants.NODE_METHOD:
				AwmlParser.parse(awml, object);
				break;	
		} 
	}
		
}
