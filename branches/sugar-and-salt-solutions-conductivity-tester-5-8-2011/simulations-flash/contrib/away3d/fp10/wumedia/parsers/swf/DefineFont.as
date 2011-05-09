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
package wumedia.parsers.swf {
	import flash.utils.ByteArray;

	/**
	 * ...
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class DefineFont {
		
		public function DefineFont(tag:Tag, doParseBody:Boolean = true) {
			_data = tag.data;
			_tagType = tag.type;
			_ascent = 0;
			_descent = 0;
			_leading = 0;
			if ( _data ) {
				parseHeader();
				if ( doParseBody ) {
					parseBody();
				}
			}
		}
		
		private var _data		:Data;
		private var _tagType	:uint;
		private var _flags		:uint;
		private var _name		:String;
		private var _ascent		:Number;
		private var _descent	:Number;
		private var _leading	:Number;
		private var _numGlyphs	:uint;
		private var _shapes		:Array;
		private var _glyphs		:Object;
		private var _advances	:Object;
		private var _top		:Number;
		private var _bottom		:Number;
		
		
		private function parseHeader():void {
			var nLen:uint;
			_glyphs = { };
			_advances = { };
			_top = Number.POSITIVE_INFINITY;
			_bottom = Number.NEGATIVE_INFINITY;
			
			_data.position += 2;	// id
			_flags = _data.readUnsignedByte();
			_data.position += 1;	// language
			nLen = _data.readUnsignedByte();
			_name = _data.readUTFBytes(nLen);
		}
		
		public function parseBody():void {
			if ( _shapes is Array ) {
				return;
			}
			var i:int;
			var gSize:uint;
			var offsets:Array;
			var off32:Boolean;
			
			_numGlyphs = _data.readUnsignedShort();
			
			offsets = new Array(_numGlyphs + 1);
			off32 = (_flags & 0x08) != 0;
			for ( i = 0; i <= _numGlyphs; ++i ) {
				offsets[i] = off32 ? _data.readUnsignedInt() : _data.readUnsignedShort();
			}
			
			_shapes = new Array(_numGlyphs);
			for ( i = 1; i <= _numGlyphs; ++i ) {
				gSize = offsets[i] - offsets[i - 1];
				var bytes:ByteArray = new ByteArray();
				_data.readBytes(bytes, 0, gSize);
				bytes.position = 0;
				_shapes[i - 1] = new ShapeRecord(new Data(bytes), _tagType);
			}
			var chars:Array = new Array(_numGlyphs);
			for ( i = 0; i < _numGlyphs; ++i ) {
				var char:String = String.fromCharCode(_data.readUnsignedShort());
				var shape:ShapeRecord = _shapes[i];
				_glyphs[char] = shape;
				chars[i] = char;
				if ( char > "A" && char < "Z" ) {
					if ( shape.bounds.top < _top ) {
						_top = shape.bounds.top;
					}
				}
				if ( shape.bounds.bottom > _bottom ) {
					_bottom = shape.bounds.bottom;
				}
			}
			var hasStyles:Boolean = (_flags & 0x80) != 0;
			if ( hasStyles ) {
				_ascent = _data.readShort() * 0.05;
				_descent = _data.readShort() * 0.05;
				_leading = _data.readShort() * 0.05;
				for ( i = 0; i < _numGlyphs; ++i ) {
					_advances[chars[i]] = _data.readShort() * 0.05;
				}
				/*
				// not being used at the moment
				// bounds
				for ( i = 0; i < _numGlyphs; ++i ) {
					new SWFRect(_data);
				}
				var kerningCount:uint = _data.readUnsignedShort();
				for ( i = 0; i < kerningCount; ++i ) {
					new SWFKerningRecord(_data, (flags & 0x04) != 0);
				}
				*/
			}
		}
		
		public function get name():String { return _name; }
		public function get ascent():Number { return _ascent; }
		public function get descent():Number { return _descent; }
		public function get leading():Number { return _leading; }
		public function get glyphs():Object { return _glyphs; }
		public function get advances():Object { return _advances; }
		public function get isBold():Boolean { return (_flags & 0x01) != 0; }
		public function get isItalic():Boolean { return (_flags & 0x02) != 0; }
		public function get isBoldItalic():Boolean { return isBold && isItalic; }
		public function get isRegular():Boolean { return !isBold && !isItalic; }
		public function get top():Number { return _top; }
		public function get bottom():Number { return _bottom; }
	}
}