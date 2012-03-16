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
	 * ...
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class Color {
		
		public function Color(data:Data, hasAlpha:Boolean) {
			r = data.readUnsignedByte();
			g = data.readUnsignedByte();
			b = data.readUnsignedByte();
			if ( hasAlpha ) {
				a = data.readUnsignedByte();
			} else {
				a = 0xff;
			}
		}
		
		public var r:uint;
		public var g:uint;
		public var b:uint;
		public var a:uint;

		public function toString():String {
			return value.toString(16); 
		}
		
		public function get value():uint {
			return a << 24 | r << 16 | g << 8 | b;
		}
		
		public function get color():uint {
			return r << 16 | g << 8 | b;
		}
		
		public function get alpha():Number {
			return a / 0xff;
		}
		
	}
	
}