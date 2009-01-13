/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.TextComponentParser;
import org.aswing.JTextArea;

/**
 * Parses {@link org.aswing.JTextArea} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.TextAreaParser extends TextComponentParser {
    
	private static var ATTR_HORIZONTAL_BLOCK_INCREMENT:String = "horizontal-block-increment";
	private static var ATTR_VERTICAL_BLOCK_INCREMENT:String = "vertical-block-increment";
	private static var ATTR_HORIZONTAL_UNIT_INCREMENT:String = "horizontal-unit-increment";
	private static var ATTR_VERTICAL_UNIT_INCREMENT:String = "vertical-unit-increment";
    private static var ATTR_COLUMNS:String = "columns";
    private static var ATTR_ROWS:String = "rows";
    private static var ATTR_MULTILINE:String = "multiline";
    private static var ATTR_WORD_WRAP:String = "word-wrap";
    
    /**
     * Constructor.
     */
    public function TextAreaParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, text:JTextArea, namespace:AwmlNamespace) {
    	
        text = super.parse(awml, text, namespace);
        
		// init block increments
		text.setHorizontalBlockIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_BLOCK_INCREMENT, text.getHorizontalBlockIncrement()));
		text.setVerticalBlockIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_BLOCK_INCREMENT, text.getVerticalBlockIncrement()));
		text.setHorizontalUnitIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_UNIT_INCREMENT, text.getHorizontalUnitIncrement()));
		text.setVerticalUnitIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_UNIT_INCREMENT, text.getVerticalUnitIncrement()));
        
        // init columns and rows
        text.setColumns(getAttributeAsNumber(awml, ATTR_COLUMNS, text.getColumns()));
        text.setRows(getAttributeAsNumber(awml, ATTR_ROWS, text.getRows()));
        
        // init multiline
        text.setMultiline(getAttributeAsBoolean(awml, ATTR_MULTILINE, text.isMultiline()));
        
        // init word wrap
        text.setWordWrap(getAttributeAsBoolean(awml, ATTR_WORD_WRAP, text.isWordWrap()));
        
        // init events
        attachEventListeners(text, JTextArea.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        
        return text;
    }
    
    private function getClass(Void):Function {
    	return JTextArea;	
    }
    
}
