/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.core.CreatableObjectParser;
import org.aswing.Icon;
import org.aswing.LoadIcon;

/**
 *  Parses {@link org.aswing.LoadIcon} element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.icon.LoadIconParser extends CreatableObjectParser {
    
    private static var ATTR_URL:String = "url";
    private static var ATTR_WIDTH:String = "width";
    private static var ATTR_HEIGHT:String = "height";
    private static var ATTR_SCALE:String = "scale";
    
    /**
     * Constructor.
     */
    public function LoadIconParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode):Icon {
        
        // create icon
        var icon:Icon = create(awml);
    
        // process super
        super.parse(awml, icon);
    
        return icon;
    }

    private function getClass(Void):Function {
    	return LoadIcon;	
    }
    
    private function getArguments(awml:XMLNode):Array {
    	
		// init properties
        var url:String = getAttributeAsString(awml, ATTR_URL, null);
        var width:Number = getAttributeAsNumber(awml, ATTR_WIDTH, null);
        var height:Number = getAttributeAsNumber(awml, ATTR_HEIGHT, null);
        var scale:Boolean = getAttributeAsBoolean(awml, ATTR_SCALE, null);
    	
    	return [url, width, height, scale];	
    }

}