/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASTextFormat;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.core.ObjectParser;

/**
 *  Parses {@link org.aswing.ASTextFormat} element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.common.TextFormatParser extends ObjectParser {
	
	private static var ATTR_ALIGN:String = "align";
	private static var ATTR_BLOCK_INDENT:String = "block-indent";
	private static var ATTR_BOLD:String = "bold";
	private static var ATTR_BULLET:String = "bullet";
	private static var ATTR_EMBEDDED_FONT:String = "embedded-font";
	private static var ATTR_INDENT:String = "indent";
	private static var ATTR_ITALIC:String = "italic";
	private static var ATTR_LEFT_MARGIN:String = "left-margin";
	private static var ATTR_NAME:String = "name";
	private static var ATTR_RIGHT_MARGIN:String = "right-margin";
	private static var ATTR_SIZE:String = "size";
	private static var ATTR_UNDERLINE:String = "underline"; 
	
	private static var ALIGN_LEFT:String = "left";
	private static var ALIGN_CENTER:String = "center";
	private static var ALIGN_RIGHT:String = "right";
	
	/**
	 * Constructor.
	 */
	public function TextFormatParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, format:ASTextFormat):ASTextFormat {
		if (format == null) {
			format = ASTextFormat.getDefaultASTextFormat();	
		}	
	
		super.parse(awml, format);
	
		// init block ident
		format.setBlockIndent(getAttributeAsNumber(awml, ATTR_BLOCK_INDENT, format.getBlockIndent()));
	
		// init bold
		format.setBold(getAttributeAsBoolean(awml, ATTR_BOLD, format.getBold()));
		
		// init bullet
		format.setBullet(getAttributeAsBoolean(awml, ATTR_BULLET, format.getBullet()));
		
		// init embedded font
		format.setEmbedFonts(getAttributeAsBoolean(awml, ATTR_EMBEDDED_FONT, format.getEmbedFonts()));
	
		// init ident
		format.setIndent(getAttributeAsNumber(awml, ATTR_INDENT, format.getIndent()));
	
		// init italic
		format.setItalic(getAttributeAsBoolean(awml, ATTR_ITALIC, format.getItalic()));
		
		// init margins
		format.setLeftMargin(getAttributeAsNumber(awml, ATTR_LEFT_MARGIN, format.getLeftMargin()));
		format.setRightMargin(getAttributeAsNumber(awml, ATTR_RIGHT_MARGIN, format.getRightMargin()));
	
		// init name
		format.setName(getAttributeAsString(awml, ATTR_NAME, format.getName()));
	
		// init size
		format.setSize(getAttributeAsNumber(awml, ATTR_SIZE, format.getSize()));
		
		// init underline
		format.setUnderline(getAttributeAsBoolean(awml, ATTR_UNDERLINE, format.getUnderline()));
	
		// init align
		var align:String = getAttributeAsString(awml, ATTR_ALIGN, null);
		switch (align) {
			case ALIGN_LEFT:
				format.setAlign(ASTextFormat.LEFT);
				break;	
			case ALIGN_CENTER:
				format.setAlign(ASTextFormat.CENTER);
				break;	
			case ALIGN_RIGHT:
				format.setAlign(ASTextFormat.RIGHT);
				break;	
		}
	
		return format;
	}

	private function parseChild(awml:XMLNode, nodeName:String, format:ASTextFormat):Void {
		
		super.parseChild(awml, nodeName, format);
		
		switch (nodeName) {
			case AwmlConstants.NODE_COLOR:
				var color:ASColor = AwmlParser.parse(awml);
				if (color != null) format.setColor(color.getRGB());
				break;	
			case AwmlConstants.NODE_FONT:
				var font:ASFont = AwmlParser.parse(awml);
				if (font != null) format.setASFont(font);
				break;	
		}
	}
	
}