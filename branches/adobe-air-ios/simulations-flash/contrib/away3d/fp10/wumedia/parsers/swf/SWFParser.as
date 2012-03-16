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
	import flash.geom.Rectangle;
	import flash.utils.ByteArray;	

	/**
	 * ...
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class SWFParser {
		
		public function SWFParser(data:ByteArray) {
			if ( data ) {
				data.position = 0;
				var header:String = data.readUTFBytes(3);
				_version = data.readUnsignedByte();
				_size = data.readUnsignedInt();
				_data = new Data(data);
				if ( header == "CWS" ) {
					var tmp:ByteArray = new ByteArray();
					_data.readBytes( tmp );
					tmp.position = 0;
					tmp.uncompress();
					tmp.position = 0;
					_data = new Data(tmp);
				}
				_rect = _data.readRect();
				_frameRate = _data.readUnsignedShort() >> 8;
				_frames = _data.readShort();
			}
		}
		
		protected var _data			:Data;
		protected var _version		:uint;
		protected var _size			:uint;
		protected var _rect			:Rectangle;
		protected var _frameRate	:uint;
		protected var _frames		:uint;
		
		public function parseTags(type:*, includeContent:Boolean, endTag:uint = 0, source:Data = null):Array {
			if ( source == null ) {
				source = _data;
			}
			var tagType:int = -1;
			var tagLength:uint;
			var tagPosition:uint;
			var bpos:uint = source.position;
			var tags:Array = [];
			var tag:Tag;
			while ( source.bytesAvailable && tagType != endTag ) {
				tagType = source.readUnsignedShort();
				tagLength = tagType & 0x3f;
				if ( tagLength == 0x3f ) {
					tagLength = source.readUnsignedInt();
				}
				tagType >>= 6;
				tagPosition = source.position;
				if ( tagType == type || (type is Array ? (type as Array).indexOf(tagType) != -1 : false) ) {
					tag = new Tag(tagType, source.position, tagLength);
					if ( includeContent ) {
						var content:ByteArray = new ByteArray();
						source.readBytes(content, 0, tag.length);
						content.position = 0;
						tag.data = new Data(content);
					}
					tags.push( tag );
				}
				if ( source.position == tagPosition ) {
					source.position += tagLength;	
				}
			}
			source.position = bpos;
			return tags;
		}
		
		
		public function get data()		:Data { return _data; }
		public function get ver()		:uint { return _version; }
		public function get size()		:uint { return _size; }
		public function get rect()		:Rectangle { return _rect; }
		public function get frameRate()	:uint { return _frameRate; }
		public function get frames()	:uint {	return _frames;}
	}
}