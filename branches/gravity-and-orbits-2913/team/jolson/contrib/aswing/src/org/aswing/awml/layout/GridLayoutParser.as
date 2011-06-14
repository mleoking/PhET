/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.GridLayout;

/**
 * Parses {@link org.aswing.GridLayout} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.GridLayoutParser extends EmptyLayoutParser {
	
	private static var ATTR_ROWS:String = "rows";
	private static var ATTR_COLUMNS:String = "columns";
	private static var ATTR_VGAP:String = "vgap";
	private static var ATTR_HGAP:String = "hgap";
	
	
	/**
     * Constructor.
     */
	public function GridLayoutParser(Void) {
		super();
	}

	public function parse(awml:XMLNode, layout:GridLayout) {
		
		layout = super.parse(awml, layout);
		
		// init rows and columns
		layout.setRows(getAttributeAsNumber(awml, ATTR_ROWS, layout.getRows()));
		layout.setColumns(getAttributeAsNumber(awml, ATTR_COLUMNS, layout.getColumns()));
		
		// init gaps
		layout.setHgap(getAttributeAsNumber(awml, ATTR_HGAP, layout.getHgap()));
		layout.setVgap(getAttributeAsNumber(awml, ATTR_VGAP, layout.getVgap()));
		
		return layout;
	}	
	
    private function getClass(Void):Function {
    	return GridLayout;	
    }
	
}