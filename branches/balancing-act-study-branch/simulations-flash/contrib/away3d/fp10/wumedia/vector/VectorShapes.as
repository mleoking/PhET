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
	import wumedia.parsers.swf.Data;	import wumedia.parsers.swf.SWFParser;	import wumedia.parsers.swf.ShapeRecord;	import wumedia.parsers.swf.Tag;	import wumedia.parsers.swf.TagTypes;		import flash.geom.Matrix;	import flash.utils.ByteArray;	import flash.utils.getQualifiedClassName;	
	/**
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class VectorShapes {
		static private var _library						:Object = new Object();
		
		/**
		 * Draw a vector shape
		 * @param graphics	the target where to draw to
		 * @param id	The id of the vector to draw
		 * @param scale	Scale the shape, 1.0 = no scale
		 * @param offsetX	The x offset
		 * @param offsetY	The y offset
		 */
		static public function draw(graphics:*, id:String, scale:Number = 1.0, offsetX:Number = 0.0, offsetY:Number = 0.0):void {
			if ( _library[id] ) {
				ShapeRecord.drawShape(graphics, _library[id], scale, offsetX, offsetY);
			} else {
				trace("ERROR: missing " + id + " vector");
			}
		}

		/**
		 * Extract vector shapes from the swf Sprite/MovieClip library.
		 * @param bytes	The ByteArray for the source (swf)
		 * @param ids	An Array of class names/linkage names where the shapes are contained
		 */
		static public function extractFromLibrary( bytes:ByteArray, ids:Array ):void {
			try {
				var swf:SWFParser = new SWFParser(bytes);
				var classes:Object = parseClassIdTable(swf);
				var sprites:Array = swf.parseTags([
					TagTypes.DEFINE_SPRITE
					], true).filter(function(tag:Tag, ...args):Boolean {
						var spriteId:uint = tag.data.readUnsignedShort();
						tag.data.position -= 2;
						return ids.indexOf(classes[spriteId]) != -1;
					});
				var i:int = -1;
				var l:int = sprites.length;
				while ( ++i < l ) {
					sprites[i].data.readUnsignedInt(); // move read position to tags
					var placeTags:Array = parsePlacetags(swf.parseTags([
						TagTypes.PLACE_OBJECT,
						TagTypes.PLACE_OBJECT2,
						TagTypes.PLACE_OBJECT3
						], true, 1, sprites[i].data));
					var defineShape:Tag = getShapeFromPlaceTags(swf, placeTags);
					if ( defineShape ) {
						sprites[i].data.position = 0;
						var spriteId:uint = sprites[i].data.readUnsignedShort();
						defineShape.data.position = 0;
						shiftDataPosToShapeRecord(defineShape.data, defineShape.type);
						if ( !_library[classes[spriteId]] ) {
							_library[classes[spriteId]] = new ShapeRecord(defineShape.data, defineShape.type);
						} else {
							trace("WARNING: vector shape with id " + classes[spriteId] +  " already exists. skipping.");
						}
					}
				}
			} catch (err:Error) {
				trace("**************************************************");
				trace("** Error: unable to extract vectors from swf bytes");
				trace(err.getStackTrace());
				trace("**************************************************");
			}
		}
		/**
		 * Extract vector shapes from 1st frame of the main timeline
		 * @param bytes	The ByteArray for the source (swf)
		 * @param id	The name you want to give this shape
		 */
		static public function extractFromStage( bytes:ByteArray, id:String):void {
			try {
				var swf:SWFParser = new SWFParser(bytes);
				var placeTags:Array = parsePlacetags(swf.parseTags([
					TagTypes.PLACE_OBJECT,
					TagTypes.PLACE_OBJECT2,
					TagTypes.PLACE_OBJECT3
					], true, 1));
				var defineShape:Tag = getShapeFromPlaceTags(swf, placeTags);
				if ( defineShape ) {
					defineShape.data.position = 0;
					shiftDataPosToShapeRecord(defineShape.data, defineShape.type);
					if ( !_library[id] ) {
						_library[id] = new ShapeRecord(defineShape.data, defineShape.type);
					} else {
						trace("WARNING: vector shape with id " + id +  " already exists, skipping.");
					}
				} else {
					throw new Error("no shapes found");
				}
			} catch (err:Error) {
				trace("**************************************************");
				trace("** Error: unable to extract vectors from swf bytes");
				trace(err.getStackTrace());
				trace("**************************************************");
			}
		}

		static private function getShapeFromPlaceTags(swf:SWFParser, placeTags:Array):Tag {
			var lib:Array = swf.parseTags([
				TagTypes.DEFINE_SHAPE,
				TagTypes.DEFINE_SHAPE2,
				TagTypes.DEFINE_SHAPE3,
				TagTypes.DEFINE_SHAPE4
				], true).filter(function(tag:Tag, ...args):Boolean {
					var shapeId:uint = tag.data.readUnsignedShort();
					tag.data.position = 0;
					return placeTags.indexOf(shapeId) != -1;
				});
			return lib[0];
		}
		
		static private function parsePlacetags( tags:Array ):Array {
			var result:Array = [];
			var i:int = -1;
			var l:int = tags.length;
			var shapeId:uint;
			var matrix:Matrix;
			var placeData:Data;
			var placeType:uint;
			var placeFlags:uint;
			while ( ++i < l ) {
				placeData = tags[i].data;
				placeType = tags[i].type;
				if ( placeType == TagTypes.PLACE_OBJECT ) {
					shapeId = placeData.readUnsignedShort();
					placeData.readUnsignedShort(); // depth
					matrix = placeData.readMatrix();
				} else if ( placeType == TagTypes.PLACE_OBJECT2 ) {
					placeData.readUBits(8); // flags
					placeData.readUnsignedShort(); // depth
					shapeId = placeData.readUnsignedShort();
					matrix = placeData.readMatrix();
				} else if ( placeType == TagTypes.PLACE_OBJECT3 ) {
					placeFlags = placeData.readUBits(16); // tags
					placeData.readUnsignedShort(); // depth
					if ( (placeFlags & 8 ) != 0
							|| (placeFlags & 16 ) != 0
							|| (placeFlags & 512 ) != 0 ) {
						placeData.readString(); // name of class for placed object
					}
					shapeId = placeData.readUnsignedShort();
					matrix = placeData.readMatrix();
				} else {
					shapeId = 0;
				}
				if ( shapeId != 0 ) {
					result.push(shapeId);
					result.push(matrix);
				}
			}
			return result;
		}
		
		static private function parseClassIdTable( swf:SWFParser ):Object {
			var result:Object = {};
			var tags:Array = swf.parseTags(TagTypes.SYMBOL_CLASS, true);
			if ( tags.length != 1 ) {
				// no symbol class found
				return result;
			}
			var tagData:Data = tags[0].data;
			var numSymbols:uint = tagData.readUnsignedShort();
			var i:int = -1;
			while ( ++i < numSymbols ) {
				var symbolId:uint = tagData.readUnsignedShort();
				var symbolName:String = tagData.readString();
				result[symbolId] = symbolName;
			}
			return result;
		}
		
		static private function shiftDataPosToShapeRecord(data:Data, defineShapeType:uint ):void {
			data.readUnsignedShort();
			data.readRect();
			if ( defineShapeType == TagTypes.DEFINE_SHAPE4 ) {
				data.readRect(); // bounds
				data.readUBits(5); // reversed - must be 0;
				data.readUBits(1); // UsesFillWindingRule if 1 (swf 10)
				data.readUBits(1); // UsesNonScalingStrokes
				data.readUBits(1); // UsesScalingStrokes
			}
		}
		
		static public function get library():Object { return _library; }

		public function VectorShapes() {
			throw new Error(getQualifiedClassName(this) + " can not be instantiated");
		}
		
		
	}
}
