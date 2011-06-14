/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlManager;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.core.CreatableObjectParser;
import org.aswing.Component;

/**
 * Parses and registers AWML component elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.core.ComponentObjectParser extends CreatableObjectParser {

    private static var AWML_ID_COUNT:Number = 0;
    
    private static var ATTR_ID:String = "id";
    private static var ATTR_NAMESPACE:String = "namespace";
    
	private static var ATTR_OWNER:String = "owner";
	private static var ATTR_OWNER_ID:String = "owner-id";
	private static var ATTR_OWNER_NAMESPACE:String = "namespace-id";
    
    /**
     * Private Constructor.
     */
    private function ComponentObjectParser(Void) {
        super();
    }
    
    /**
     * Generates unique AWML ID.
     */
    private static function generateAwmlID(Void):String {
        return ("AwmlID_" + AWML_ID_COUNT++);
    }
    
    public function parse(awml:XMLNode, component:Component, namespace:AwmlNamespace) {

		// create object
		if (component == null) {
			component = create(awml);	
		}

        // set AWML ID
        var awmlID:String = getAttributeAsString(awml, ATTR_ID, null);
        if (awmlID == null) awmlID = generateAwmlID();
        component.setAwmlID(awmlID);

        // cheks for new namespace
        if (namespace == null) {
        	namespace = AwmlManager.getRootNamespace();
        } 
        
        var namespaceName:String = getAttributeAsString(awml, ATTR_NAMESPACE, null);
        if (namespaceName != null) {
        	var childNamespace:AwmlNamespace = namespace.getChildNamespace(namespaceName);
        	if (childNamespace == null) {
        		namespace = namespace.addChildNamespace(namespaceName);
        	} else {
        		namespace = childNamespace;	
        	}
        }
        	
        // check if component already existed in this namespace
        var existedComponent:Component = namespace.getComponent(awmlID);
        if (existedComponent != null) {
        	if (AwmlParser.getParsingStrategy() == AwmlParser.STRATEGY_APPEND) {
        		namespace.addComponent(component);
        	} else if (AwmlParser.getParsingStrategy() == AwmlParser.STRATEGY_IGNORE) {
        		return null;	
        	} else if (AwmlParser.getParsingStrategy() == AwmlParser.STRATEGY_REPLACE) {
        		namespace.removeComponent(existedComponent);
        		existedComponent.removeFromContainer();
        		namespace.addComponent(component);
        	} else if (AwmlParser.getParsingStrategy() == AwmlParser.STRATEGY_EXCEPTION) {
        		throw new Error("Component with ID \""+awmlID+"\" already exists in the namespace \""+namespace.toString()+"\"");	
        	}
        } else {
        	// add new component to the namespace
        	namespace.addComponent(component);
        }

        // set namespace
        component.setAwmlNamespace(namespace.getName());
    
        super.parse(awml, component, namespace);
        
        return component;
    }

    private function parseChild(awml:XMLNode, nodeName:String, component:Component, namespace:AwmlNamespace):Void {
        super.parseChild(awml, nodeName, component, namespace);
        
        switch (nodeName) {
        	case AwmlConstants.NODE_EVENT:
        		AwmlParser.parse(awml, component);
        		break;	
        }
    }   
 
 	/**
	 * Recognizes window's owner object from the passed-in <code>awml</code>
	 * element. If owner object wasn't able to recognized from the AWML, returns
	 * {@link #DEFAULT_OWNER} object.
	 * 
	 * @param awml the AWML XML node
	 * @return the owner object
	 */
	private function getOwner(awml:XMLNode) {
		
		var owner;
		var ownerTarget:String = getAttributeAsString(awml, ATTR_OWNER, null);
		
		if (ownerTarget != null) {
			owner = eval(ownerTarget);	
		} else {
			var ownerID:String = getAttributeAsString(awml, ATTR_OWNER_ID, null);
			var ownerNamespace:String = getAttributeAsString(awml, ATTR_OWNER_NAMESPACE, null);
			owner = AwmlManager.getComponent(ownerID, ownerNamespace);
		}
		
		if (owner == null) owner = ASWingUtils.getRootMovieClip();
		 
		return owner;
	}

}
