/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.core.ComponentObjectParser;
import org.aswing.Component;

/**
 *  Parses component AWML reflection element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.ComponentParser extends ComponentObjectParser {
	
	/**
	 * Constructor.
	 */
	public function ComponentParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, component:Component, namespace:AwmlNamespace) {
		
		component = super.parse(awml, component, namespace);
		
		return component;
	}

	private function parseChild(awml:XMLNode, nodeName:String, component:Component, namespace:AwmlNamespace):Void {
		
		super.parseChild(awml, nodeName, component, namespace);

		switch (nodeName) {
			case AwmlConstants.NODE_COMPONENT:
				var child:Component = AwmlParser.parse(awml, null, namespace);
				if (child != null) {
					if (component["getContentPane"] != null) {
						component["getContentPane"]().append(child);
					} else {
						component["append"](child);
					}
				}
				break;	
		}
	}

	private function getConstructorArgs(awml:XMLNode):Array {
		
		var args = new Array();
		
		var constructorNode:XMLNode = AwmlUtils.getNodeChild(awml, AwmlConstants.NODE_CONSTRUCTOR);
		if (constructorNode != null) {
			args = AwmlParser.parse(constructorNode);
		}
		
		return args;
	}

    private function getArguments(awml:XMLNode):Array {
    	return getConstructorArgs(awml);	
    }

}