 /*
  * Copyright 2009 (c) Guojian Miguel Wu
  * 
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  * 	http://www.apache.org/licenses/LICENSE-2.0
  * 	
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * 
  * limitations under the License.
  * 
  */
package wumedia.vector {
	import wumedia.parsers.swf.DefineFont;
	import wumedia.parsers.swf.SWFParser;
	import wumedia.parsers.swf.ShapeRecord;
	import wumedia.parsers.swf.TagTypes;
	
	import flash.geom.Rectangle;
	import flash.utils.ByteArray;
	import flash.utils.Dictionary;
	import flash.utils.getQualifiedClassName;	

	/**
	 * Write text using flash's drawing API instead of the TextField class.
	 * Advantages: Simpler API, faster for animations
	 * Disadvantages: Not as crisp as the native font engine at smaller sizes (usually < 12px)
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class VectorText {
		
		static public const TOP_LEFT_CENTER		:String = "TLC";
		static public const TOP_LEFT			:String = "TL";
		static public const TOP_RIGHT			:String = "TR";
		static public const BOTTOM_LEFT			:String = "BL";
		static public const BOTTOM_LEFT_CENTER	:String = "BLC";
		static public const BOTTOM_RIGHT		:String = "BR";
		static public const LEFT				:String = "L";
		static public const LEFT_CENTER			:String = "LC";
		static public const RIGHT				:String = "R";
		static public const TOP					:String = "T";
		static public const BOTTOM				:String = "B";
		static public const CENTER				:String = "C";
		
		static private const EM_SIZE			:Number = 1024;
		static private var _fontDefinitions		:Dictionary = new Dictionary();
		static private var _width				:Number = 0;
		
		/**
		 * Extract font information from the ByteArray of a swf file<br/>
		 * 		DynamicText.extractFontInformation(root.loaderInfo.bytes);<br/><br/>
		 * @param swfBytes:ByteArray		The source
		 * @param fontNames:Array<String>	If provided only parse for these fonts, if null, parse for all
		 * 									It is recommended that you pass the list of fonts you want, parsing
		 * 									parsing every font can be slow and lock up your machine
		 * @param traceAdditions:Boolean	If true, trace out a list of names we extracted from the swf
		 */
		static public function extractFont(swfBytes:ByteArray, fontNames:Array = null, traceAdditions:Boolean = false):void {
			try {
				var swf:SWFParser = new SWFParser(swfBytes);
				var tags:Array = swf.parseTags(TagTypes.DEFINE_FONT3, true);
				var i:int = tags.length;
				while ( --i > -1 ) {
					var font:DefineFont = new DefineFont(tags[i], false);
					if ( !_fontDefinitions[font.name] ) {
						if ((fontNames == null) || (fontNames is Array && fontNames.indexOf(font.name) != -1)) {
							font.parseBody();
						} else {
							font = null;
						}
						if ( font ) {
							_fontDefinitions[font.name] = font;
							if ( traceAdditions ) {
								trace(font.name);
							}
						}
					} else {
						trace("Font ", font.name, "already exists, skipping");
					}
				}
			} catch ( err:Error ) {
				trace("************************************************");
				trace("** Error: unable to extract fonts from swf bytes");
				trace("************************************************");
			}
		}

		/**
		 * Write a string to the target graphics.
		 * @param graphics:Graphics	The drawing target, This is type * so it can support PV3D and five3D where the drawing target
		 * 							have the same method as Graphics but are not Graphics
		 * @param font:String	The name of the font to use
		 * @param size:Number	The size (pixels) of the font
		 * @param kerning:Number	The kering, this is the same as photoshop's kerning (or close) which is ~10x that of flash's
		 * @param leading:Number	The leading, this is the same as photoshop's leading
		 * @param text:String	The string to write
		 * @param x:Number		The x position where we want to start drawing
		 * @param y:Number		The y position where we want to start drawing
		 * @param width:Number	The width of the drawing area, if the text is greater then this number then we start wrapping to the next line.
		 * 						If you don't want any wrapping, set this number to Number.POSITIVE_INFINITY
		 * @param align:String	A value from the DynamicText class that specifies the alignment to the x and y property
		 * @param alignToBaseLine:Boolean	if true, we'll align to the baseline of the font instead of the top or bottom edges
		 */
		static public function write(graphics:*, font:String, size:Number, leading:Number, kerning:Number = 0.0, text:String = "",
			x:Number = 0.0, y:Number = 0.0, width:Number = Number.POSITIVE_INFINITY, align:String = TOP_LEFT, alignToBaseLine:Boolean = false):void {
			if ( !_fontDefinitions[font] ) {
				trace("ERROR: missing " + font + " font");
				return;
			}
			var fontDef:DefineFont = _fontDefinitions[font];
			var scale:Number = 1 / (EM_SIZE / size);
			var offsetX:Number;
			var offsetY:Number;
			var char:String;
			var charNum:int;
			var charLen:int;
			var lines:Array;
			var linesNum:int;
			var linesLen:int;
			var lineText:String;
			var widths:Array;
			var alignDir:int;
			var overrideCenter:Boolean = false;
			var advance:Number;
			
			text = text.replace(/\r/g, "\n");
			lines = breakLines(fontDef, size, text, kerning, width);
			widths = measureLines(fontDef, size, kerning, lines);
			kerning *= .0016;
			leading = (fontDef.ascent / (EM_SIZE / leading));
			
			if ( align == TOP_LEFT
					|| align == BOTTOM_LEFT
					|| align == LEFT
					|| align == TOP_LEFT_CENTER
					|| align == BOTTOM_LEFT_CENTER
					|| align == LEFT_CENTER ) {
				alignDir = 1;
				if ( align == TOP_LEFT_CENTER
						|| align == BOTTOM_LEFT_CENTER
						|| align == LEFT_CENTER ) {
					overrideCenter = true;
				}
			} else if ( align == TOP_RIGHT
					|| align == BOTTOM_RIGHT
					|| align == RIGHT ) {
				alignDir = -1;
			} else {
				alignDir = 0;
			}
			if ( align == TOP_LEFT
					|| align == TOP_RIGHT
					|| align == TOP
					|| align == TOP_LEFT_CENTER) {
				offsetY = y + (alignToBaseLine ? 0 : -fontDef.top * scale);
			} else if ( align == BOTTOM_LEFT
					|| align == BOTTOM_RIGHT
					|| align == BOTTOM
					|| align == BOTTOM_LEFT_CENTER) {
				offsetY = y + (fontDef.ascent - (alignToBaseLine ? 0 : fontDef.descent)) * scale - lines.length * leading;
			} else {
				offsetY = y + (fontDef.ascent - fontDef.descent) * scale - lines.length * leading * .5;
			}
			
			linesNum = -1;
			linesLen = lines.length;
			while ( ++linesNum < linesLen ) {
				lineText = lines[linesNum];
				charNum = -1;
				charLen = lineText.length;
				if ( alignDir == 1 ) {
					offsetX = x + (overrideCenter ? (width - widths[linesNum]) * .5 : 0);
				} else if ( alignDir == -1 ) {
					offsetX = x - widths[linesNum];
				} else {
					offsetX = x - widths[linesNum] * .5;
				}
				while ( ++charNum < charLen ) {
					char = lineText.charAt(charNum);
					if (fontDef.glyphs[char]) {
						advance = fontDef.advances[char];
						if ( charNum == 0 ) {
							// flush the X to the 0 delta position
							offsetX -= fontDef.glyphs[char].bounds.left * scale;
						}
						if ( graphics.hasOwnProperty("startNewShape") ) {
							graphics["startNewShape"]();
						}
						ShapeRecord.drawShape(graphics, fontDef.glyphs[char], scale, offsetX, offsetY);
						offsetX += (advance + advance * kerning) * scale;
					}
				}
				offsetY += leading;
			}
		}
		
		/**
		 * Write a string to the target graphics aligned to the font's baseline
		 * @param graphics:Graphics	The drawing target, This is type * so it can support PV3D and five3D where the drawing target
		 * 							have the same method as Graphics but are not Graphics
		 * @param font:String	The name of the font to use
		 * @param size:Number	The size (pixels) of the font
		 * @param kerning:Number	The kering, this is the same as photoshop's kerning (or close) which is ~10x that of flash's
		 * @param leading:Number	The leading, this is the same as photoshop's leading
		 * @param text:String	The string to write
		 * @param x:Number		The x position where we want to start drawing
		 * @param y:Number		The y position where we want to start drawing
		 * @param width:Number	The width of the drawing area, if the text is greater then this number then we start wrapping to the next line.
		 * 						If you don't want any wrapping, set this number to Number.POSITIVE_INFINITY
		 * @param align:String	A value from the DynamicText class that specifies the alignment to the x and y property
		 */
		static public function writeBaseLine(graphics:*, font:String, size:Number, leading:Number, kerning:Number = 0.0, text:String = "",
			x:Number = 0.0, y:Number = 0.0, width:Number = Number.POSITIVE_INFINITY, align:String = TOP_LEFT):void {
			
			write(graphics, font, size, leading, kerning, text, x, y, width, align, true);	
		}
		
		/**
		 * Return an array of lines of text after breaking up a string for wrapping in the given width
		 * @private
		 * 
		 * @param font:String	The name of the font to use
		 * @param size:Number	The size (pixels) of the font
		 * @param text:String	The string to write
		 * @param kerning:Number	The kering, this number should be very similar to the kerning in photshop which is ~10x that of flash's
		 * @param width:Number	Wrap to the next line if the current line exceeds this width
		 * @return Array of String
		 */
		static private function breakLines(fontDef:DefineFont, size:Number, text:String, kerning:Number, width:Number ):Array {
			var scale:Number = 1.0 / (EM_SIZE / size);
			var w:Number = 0;
			var char:String;
			var charNum:int = -1;
			var charLen:int = text.length;
			var lines:Array = [""];
			var lineNum:uint = 0;
			var advance:Number;
			kerning *= .0016;
			_width = 0;
			while ( ++charNum < charLen ) {
				char = text.charAt(charNum);
				if ( char == "\n" ) {
					lines[++lineNum] = "";
					w = 0;
				} if (fontDef.glyphs[char]) {
					advance = fontDef.advances[char];
					w += (advance + advance * kerning ) * scale;
					if ( w > width ) {
						var backTrack:uint = lines[lineNum].lastIndexOf(" ");
						if ( backTrack > 0 && backTrack < 0xffffffff ) {
							var len:int = lines[lineNum].length;
							lines[lineNum] = lines[lineNum].slice(0, backTrack - len);
							lines[++lineNum] = "";
							charNum -= (len - backTrack);
							w = 0;
						} else if ( lines[lineNum].length == 0 ) {
							lines[lineNum] += char;
							lines[++lineNum] = "";
							w = 0;
						} else {
							lines[++lineNum] = char == " " ? "" : char;
							w = (advance + advance * kerning) * scale;
						}
					} else {
						lines[lineNum] += char;
					}
				}
			}
			if (lines[lineNum] == "" ) lines.pop();
			return lines;
		}
		
		/**
		 * Return an array that contains the width for each lines of text
		 * @private
		 */
		static private function measureLines(fontDef:DefineFont, size:Number, kerning:Number, lines:Array):Array {
			var scale:Number = 1.0 / (EM_SIZE / size);
			var metrics:Array = new Array(lines.length);
			var char:String;
			var charNum:int;
			var charLen:int;
			var lineText:String;
			var lineW:Number;
			var i:int = lines.length;
			var maxW:Number = 0;
			var advance:Number;
			var bounds:Rectangle;
			kerning *= .0016;
			while ( --i > -1 ) {
				lineText = lines[i];
				lineW = 0;
				charNum = -1;
				charLen = lineText.length;
				while ( ++charNum < charLen ) {
					char = lineText.charAt(charNum);
					if (fontDef.glyphs[char]) {
						advance = fontDef.advances[char];
						bounds = fontDef.glyphs[char].bounds;
						lineW += (advance + advance * kerning ) * scale;
						if ( charNum == 0 ) {
							lineW -= bounds.left * scale;
						}
					}
				}
				lineW -= (advance * kerning + (advance - bounds.right)) * scale;
				metrics[i] = lineW;
				if ( lineW > maxW ) {
					maxW = lineW;
					}
			}
			return metrics;
			
		}
		

		/**
		 * Get the FontDefinition for font
		 * @param font:String	The name of the font
		 * @return SWFDefineFont3
		 */
		static public function getFontDefinition(font:String):DefineFont {
			return _fontDefinitions[font] || new DefineFont(null);
		}
		
		
		public function VectorText() {
			throw new Error(getQualifiedClassName(this) + " can not be instantiated");
		}
		
	}
}

