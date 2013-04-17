/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.border.DecorateBorderParser;
import org.aswing.border.TitledBorder;

/**
 *  Parses {@link org.aswing.border.TitledBorder} level element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.border.TitledBorderParser extends DecorateBorderParser {
	
	private static var ATTR_TITLE:String = "title";
	private static var ATTR_EDGE:String = "edge";
	private static var ATTR_ALIGN:String = "align";
	private static var ATTR_POSITION:String = "position";
	private static var ATTR_ROUND:String = "round";
	private static var ATTR_LINE_THICKNESS:String = "line-thickness";
	
	private static var POSITION_TOP:String = "top";
	private static var POSITION_BOTTOM:String = "bottom";
	
	private static var ALIGN_LEFT:String = "left";
	private static var ALIGN_CENTER:String = "center";
	private static var ALIGN_RIGHT:String = "right"; 
	
	/**
	 * Constructor.
	 */
	public function TitledBorderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, border:TitledBorder) {
		
		border = super.parse(awml, border);
		
		// init title
		border.setTitle(getAttributeAsString(awml, ATTR_TITLE, border.getTitle()));
		
		// init offset
		border.setEdge(getAttributeAsNumber(awml, ATTR_EDGE, border.getEdge()));
		
		// init round
		border.setRound(getAttributeAsNumber(awml, ATTR_ROUND, border.getRound()));
		
		// init line thickness
		border.setLineThickness(getAttributeAsNumber(awml, ATTR_LINE_THICKNESS, border.getLineThickness()));
		
		// init position
		var position:String = getAttributeAsString(awml, ATTR_POSITION, null);
		switch (position) {
			case POSITION_TOP:
				border.setPosition(TitledBorder.TOP);
				break;
			case POSITION_BOTTOM:
				border.setPosition(TitledBorder.BOTTOM);	
				break;
		}

		// init align
		var align:String = getAttributeAsString(awml, ATTR_ALIGN, null);
		switch (align) {
			case ALIGN_LEFT:
				border.setAlign(TitledBorder.LEFT);
				break;
			case ALIGN_CENTER:
				border.setAlign(TitledBorder.CENTER);	
				break;
			case ALIGN_RIGHT:
				border.setAlign(TitledBorder.RIGHT);	
				break;
		}
		
		return border;
	}

	private function parseChild(awml:XMLNode, nodeName:String, border:TitledBorder):Void {
		
		super.parseChild(awml, nodeName, border);

		switch(nodeName) {
			case AwmlConstants.NODE_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setColor(color);
				break; 
			case AwmlConstants.NODE_FONT:
				var font:ASFont = AwmlParser.parse(awml);
				if (font != null) border.setFont(font);
				break; 
			case AwmlConstants.NODE_LINE_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setLineColor(color);
				break; 
			case AwmlConstants.NODE_LINE_LIGHT_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) border.setLineLightColor(color);
				break; 
		}
	}
	
    private function getClass(Void):Function {
    	return TitledBorder;	
    }
	
}