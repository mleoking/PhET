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

	/**
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class Edge {
		static public const CURVE	:uint = 0;
		static public const LINE	:uint = 1;
		public function Edge(type:uint, data:Data, startX:int = 0.0, startY:int = 0.0) {
			this.type = type;
			if (data) {
				sx = startX;
				sy = startY;
				if (type == CURVE) {
					parseCurve(data);
				} else {
					parseLine(data);
				}
			}
		}
		
		public var type:uint;
		public var cx:int;
		public var cy:int;
		public var sx:int;
		public var sy:int;
		public var x:int;
		public var y:int;
		
		public function reverse():Edge {
			var edge:Edge = new Edge(type, null);
			edge.x = sx;
			edge.y = sy;
			edge.cx = cx;
			edge.cy = cy;
			edge.sx = x;
			edge.sy = y;
			return edge;
		}

		public function apply(graphics:*, scale:Number = 1.0, offsetX:Number = 0.0, offsetY:Number = 0.0):void {
			if (type == CURVE) {
				graphics["curveTo"](cx * scale + offsetX, cy * scale + offsetY, x * scale + offsetX, y * scale + offsetY);
			} else {
				graphics["lineTo"](x * scale + offsetX, y * scale + offsetY);
			}
		}
		
		private function parseLine(data:Data) : void {
			var numBits:uint = data.readUBits(4) + 2;
			var generaLine:Boolean = data.readUBits(1) == 1;
			if ( generaLine ) {
				x = data.readSBits(numBits) + sx;
				y = data.readSBits(numBits) + sy;
			} else {
				var isVertical:Boolean = data.readUBits(1) == 1;
				if ( isVertical ) {
					x = sx;
					y = data.readSBits(numBits) + sy;
				} else {
					x = data.readSBits(numBits) + sx;
					y = sy;
				}
			}
		}

		private function parseCurve(data:Data) : void {
			var numBits:uint = data.readUBits(4) + 2;
			cx = data.readSBits(numBits) + sx;
			cy = data.readSBits(numBits) + sy;
			x = data.readSBits(numBits) + cx;
			y = data.readSBits(numBits) + cy;
		}

	}
}