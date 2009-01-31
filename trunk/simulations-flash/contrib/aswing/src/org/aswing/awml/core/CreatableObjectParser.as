/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.core.ObjectParser;
import org.aswing.util.Reflection;

/**
 * Provides functionality for objects which can create its instances 
 * by itself using "class" attribute. 
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.core.CreatableObjectParser extends ObjectParser {

    private static var ATTR_CLASS:String = "class";
    
    /**
     * Private Constructor.
     */
    private function CreatableObjectParser(Void) {
        super();
    }
        
    public function parse(awml:XMLNode, object:Object, namespace:AwmlNamespace) {

        super.parse(awml, object, namespace);
        
        return object;
    }

	private function parseChild(awml:XMLNode, nodeName:String, object:Object, namespace:AwmlNamespace):Void {
		super.parseChild(awml, nodeName, object, namespace);
	}

	/**
	 * Creates new object blased on class info from the AWML.
	 */
    private function create(awml:XMLNode) {

		// get class name
		var className:String = getAttributeAsString(awml, ATTR_CLASS, null);
		
		// construct class
		var object;
		
		try {
			object = (className != null) ? createByClassName(className, getArguments(awml)) : 
					createByClass(getClass(), getArguments(awml));
		} catch (E:Error) {
			trace("AWML Warning: Can't create object instance for node: " + awml.toString());	
		}
			
        return object;
    }

	/**
	 * Constructs new instance using the given {@code clazzName} class name 
	 * and {@code args} array.
	 * 
	 * @param clazzName the name of the class 
	 * @param args the constructor arguments
	 * @return created instance
	 */
	private function createByClassName(clazzName:String, args:Array) {
		
		// get reference to class constructor
		var clazz:Function = Reflection.getClass(clazzName);
		
		return createByClass(clazz, args);
	}
	
	/**
	 * Constructs new instance using the given {@code clazz} constructor
	 * reference and {@code args} array.
	 * 
	 * @param clazz the reference to the constructor
	 * @param args the constructor arguments
	 * @return created instance
	 */
	private function createByClass(clazz:Function, args:Array) {
		return Reflection.createInstance(clazz, args);
	}
    
    /**
     * Abstract method. Returns reference to the class constructor. 
     */
    private function getClass(Void):Function {
    	return null;	
    }
    
    /**
     * Abstract method. Returns arguments array. 
     */
    private function getArguments(awml:XMLNode):Array {
    	return [];	
    }
        
}
