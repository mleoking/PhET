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
	import flash.display.Graphics;	import flash.display.Shape;	import flash.geom.Rectangle;	
	/**
	 * ...
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class ShapeRecord {
		static private var _shape	:Shape = new Shape();
		
		static public function drawShape(graphics:*, shape:ShapeRecord, scale:Number = 1.0, offsetX:Number = 0.0, offsetY:Number = 0.0):void {
			var elems:Array = shape._elements;
			var elemNum:int = -1;
			var elemLen:int = elems.length;
			var dx:int = 0;
			var dy:int = 0;
			scale *= .05;
			while ( ++elemNum < elemLen ) {
				if ( elems[elemNum] is Edge )  {
					var edge:Edge = elems[elemNum];
					if ( dx != edge.sx || dy != edge.sy ) {
						graphics["moveTo"](offsetX + edge.sx * scale, offsetY + edge.sy * scale);
					}
					edge.apply(graphics, scale, offsetX, offsetY);
					dx = edge.x;
					dy = edge.y;
				} else if ( elems[elemNum] is FillStyle ) {
					if ( graphics.hasOwnProperty("startNewShape") ) {
						graphics["startNewShape"]();
					}
					(elems[elemNum] as FillStyle).apply(graphics, scale, offsetX, offsetY);
				}
				// lineStyles not supported yet
				//else if ( elems[elemNum] is LineStyle ) {
				//	(elems[elemNum] as LineStyle).apply(graphics);
				//}
			}
		}
		
		public function ShapeRecord(data:Data, tagType:uint) {
			_tagType = tagType;
			_hasStyle = _tagType == TagTypes.DEFINE_SHAPE
						|| _tagType == TagTypes.DEFINE_SHAPE2
						|| _tagType == TagTypes.DEFINE_SHAPE3
						|| _tagType == TagTypes.DEFINE_SHAPE4;
			_hasAlpha = _tagType == TagTypes.DEFINE_SHAPE3
						|| _tagType == TagTypes.DEFINE_SHAPE4;
			_hasExtendedFill = _tagType == TagTypes.DEFINE_SHAPE2
						|| _tagType == TagTypes.DEFINE_SHAPE3
						|| _tagType == TagTypes.DEFINE_SHAPE4;
			_hasStateNewStyle = _tagType == TagTypes.DEFINE_SHAPE2
						|| _tagType == TagTypes.DEFINE_SHAPE3;
						
			parse(data);
			if ( _elements.length > 0 ) {
				calculateBounds();
			} else {
				_bounds = new Rectangle(0, 0, 0, 0);
			}
		}
		
		private var _tagType				:uint;
		private var _fillBits				:uint;
		private var _lineBits				:uint;
		private var _hasStyle				:Boolean;
		private var _hasAlpha				:Boolean;
		private var _hasExtendedFill		:Boolean;
		private var _hasStateNewStyle		:Boolean;
		private var _elements				:Array;
		private var _bounds					:Rectangle;
		private var _fills					:Array;
		private var _fill0					:Array;
		private var _fill1					:Array;
		private var _fill0Index				:uint;
		private var _fill1Index				:uint;
		
		private function parse(data:Data):void {
			var stateMoveTo:Boolean;
			var stateFillStyle0:Boolean;
			var stateFillStyle1:Boolean;
			var stateLineStyle:Boolean;
			var stateNewStyles:Boolean;
			var moveBits:uint;
			var fillStyle0:int;
			var fillStyle1:int;
			var lineStyle:int;
			var flags:uint;
			var dx:int = 0;
			var dy:int = 0;
			var edge:Edge;
			_elements = new Array();
			_fills = new Array();
			data.synchBits();
			if ( _hasStyle ) {
				parseStyles(data);
				data.synchBits();
			} else {
				_fills = [[]];
				_fill0 = [];
				_fill0Index = 0;
			}
			_fillBits = data.readUBits(4);
			_lineBits = data.readUBits(4);
			while ( true ) {
				var type:uint = data.readUBits(1);
				if ( type == 1 ) {
					// Edge shape-record
					edge = new Edge(data.readUBits(1) == 0 ? Edge.CURVE : Edge.LINE, data, dx, dy);
					if ( _fill0 ) {
						_fill0.push(edge.reverse());
					}
					if ( _fill1 ) {
						_fill1.push(edge);
					}
					dx = edge.x;
					dy = edge.y;
				} else {
					// Change Record or End
					flags = data.readUBits(5);
					if ( flags == 0 ) {
						// end
						break;
					}
					stateMoveTo = (flags & 0x01) != 0;
					stateFillStyle0 = (flags & 0x02) != 0;
					stateFillStyle1 = (flags & 0x04) != 0;
					stateLineStyle = (flags & 0x08) != 0;
					stateNewStyles = (flags & 0x10) != 0;
					if ( stateMoveTo ) {
						moveBits = data.readUBits(5);
						dx = data.readSBits(moveBits);
						dy = data.readSBits(moveBits);
					}
					if ( stateFillStyle0 ) {
						fillStyle0 = data.readUBits(_fillBits);
					}
					if ( stateFillStyle1 ) {
						fillStyle1 = data.readUBits(_fillBits);
					}
					if ( stateLineStyle ) {
						lineStyle = data.readUBits(_lineBits);
					}
					if ( _hasStyle ) {
						queueEdges();
						_fill0Index = fillStyle0 - 1;
						if ( fillStyle0 > 0 && _fills[_fill0Index] ) {
							_fill0 = [];
						} else {
							_fill0 = null;
						}
						_fill1Index = fillStyle1 - 1;
						if ( fillStyle1 > 0 && _fills[_fill1Index] ) {
							_fill1 = [];
						} else {
							_fill1 = null;
						}
					}
					if ( _hasStateNewStyle && stateNewStyles ) {
						parseStyles(data);
						_fillBits = data.readUBits(4);
						_lineBits = data.readUBits(4);
					}
				}
			}
			saveEdges();
		}
		

		private function parseStyles(data:Data):void {
			var i:int;
			var num:int;
			saveEdges();
			num = data.readUnsignedByte();
			if ( _hasExtendedFill && num == 0xff ) {
				num = data.readUnsignedShort();
			}
			for ( i = 0; i < num; ++i ) {
				_fills.push([new FillStyle(data, _hasAlpha)]);
			}
			num = data.readUnsignedByte();
			if ( num == 0xff ) {
				num = data.readUnsignedShort();
			}
			for ( i = 0; i < num; ++i ) {
				// lineStyles not supported yet
				// we parse linestyles for data sanity but we don't use them
				new LineStyle(_tagType == TagTypes.DEFINE_SHAPE4 ? LineStyle.TYPE_2 : LineStyle.TYPE_1, data, _hasAlpha);
			}
		}
		
		/**
		 * Add the current edges back to the fill arrays and wait to be saved
		 * @private
		 */
		private function queueEdges():void {
			if ( _fill0 ) {
				_fills[_fill0Index] = _fills[_fill0Index].concat(_fill0);
			}
			if( _fill1 ) {
				_fills[_fill1Index] = _fills[_fill1Index].concat(_fill1);
			}
		}
		
		/**
		 * Sort and save the fill edges
		 * @private
		 */
		private function saveEdges():void {
			queueEdges();
			var i:int;
			var l:int;
			l = _fills.length;
			i = -1;
			while ( ++i < l ) {
				_fills[i] = sortEdges(_fills[i]);
				_elements = _elements.concat(_fills[i]);
			}
			_fills = new Array();
		}
		
		private function sortEdges(arr:Array):Array {
			var i:int;
			var j:int;
			var sorted:Array = [arr.shift()];
			var edge:Edge;
			
			while ( arr.length > 0 ) {
				sorted.push(edge = arr.pop());
				j = arr.length;
				while ( --j > -1 ) {
					i = arr.length;
					while ( --i > -1 ) {
						if ( edge.x == arr[i].sx && edge.y == arr[i].sy ) {
							edge = arr.splice(i,1)[0];
							sorted.push(edge);
							continue;
						}
					}
				}
			}
			return sorted;
		}
		
		private function calculateBounds():void {
			var g:Graphics = _shape.graphics;
			g.clear();
			g.beginFill(0);
			drawShape(g, this);
			g.endFill();
			_bounds = _shape.getRect(_shape);
		}
		
		public function get elements():Array { return _elements; }
		public function get bounds():Rectangle { return _bounds; }
	}
	
}