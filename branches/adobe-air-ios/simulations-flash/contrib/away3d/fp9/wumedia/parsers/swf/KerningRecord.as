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
	public class KerningRecord {
		
		public function KerningRecord(data:Data, wideCodes:Boolean) {
			_kerningCode0 = wideCodes ? data.readUnsignedShort() : data.readUnsignedByte();
			_kerningCode1 = wideCodes ? data.readUnsignedShort() : data.readUnsignedByte();
			_kerningChar0 = String.fromCharCode(kerningCode0);
			_kerningChar1 = String.fromCharCode(kerningCode1);
			_kerningAdjustment = data.readShort();
		}
		
		private var _kerningCode0 		:uint;
		private var _kerningCode1 		:uint;
		private var _kerningChar0		:String;
		private var _kerningChar1		:String;
		private var _kerningAdjustment	:uint;
		public function get kerningCode0():uint { return _kerningCode0; }
		public function get kerningCode1():uint { return _kerningCode1; }
		public function get kerningAdjustment():uint { return _kerningAdjustment; }
		public function get kerningChar0():String { return _kerningChar0; }
		public function get kerningChar1():String { return _kerningChar1; }
		
		
	}
	
}