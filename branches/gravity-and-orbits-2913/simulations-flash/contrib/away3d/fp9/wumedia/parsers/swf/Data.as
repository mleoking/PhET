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
	import flash.geom.Matrix;
	import flash.geom.Rectangle;
	import flash.utils.ByteArray;
	import flash.utils.Endian;
	import flash.utils.IDataInput;	

	/**
	 * @author guojian@wu-media.com | guojian.wu@ogilvy.com
	 */
	public class Data implements IDataInput {
		
		public function Data(data:ByteArray) {
			_data = data;
			_data.endian = Endian.LITTLE_ENDIAN;
			synchBits();
		}
		
		protected var _data			:ByteArray;
		protected var _bitBuff		:int;
		protected var _bitPos		:int;
		
		public function synchBits():void {
			_bitBuff = 0;
			_bitPos = 0;
		}
		
		public function readSBits( bits:uint ):int {
			var result:int = readUBits(bits);
			if ( (result & (1 << (bits - 1))) != 0 ) {
				result |= -1 << bits;
			}
			return result;
		}
		
		public function readUBits( bits:uint ):uint	{			
			if ( bits == 0 ) {
				return 0;
			}
			var bitsLeft:int = bits;
			var result:int = 0;
			if ( _bitPos == 0 ) {
				_bitBuff = _data.readUnsignedByte();
				_bitPos = 8;
			}
			while ( true ) {
				var shift:int = bitsLeft - _bitPos;
				if ( shift > 0 ) {
					result |= _bitBuff << shift;
					bitsLeft -= _bitPos;
					
					_bitBuff = _data.readUnsignedByte();
					_bitPos = 8;
				} else {
					result |= _bitBuff >> - shift;
					_bitPos -= bitsLeft;
					_bitBuff &= 0xff >> (8 - _bitPos);
					break;
				}
			}
			return result;
        }
		
		public function readBoolean():Boolean{
			synchBits();
			return _data.readBoolean();
		}
		
		public function readByte():int{
			synchBits();
			return readByte();
		}
		
		public function readBytes(bytes:ByteArray, offset:uint = 0, length:uint = 0):void {
			synchBits();
			_data.readBytes( bytes, offset, length );
		}
		
		public function readDouble():Number{
			var bytes:ByteArray = new ByteArray();
			var double:ByteArray = new ByteArray();
			_data.readBytes(bytes, 0, 8);
			double.length = 8;
			double[0] = bytes[3];
			double[1] = bytes[2];
			double[2] = bytes[1];
			double[3] = bytes[0];
			double[4] = bytes[7];
			double[5] = bytes[6];
			double[6] = bytes[5];
			double[7] = bytes[4];
			double.position = 0;
			return double.readDouble();
		}
		
		public function readMatrix():Matrix {
			var scaleX:Number;
			var scaleY:Number;
			var rotate0:Number;
			var rotate1:Number;
			var translateX:Number;
			var translateY:Number;
			var bits:uint;
			synchBits();
			if ( readUBits(1) == 1) {
				bits = readUBits(5);
				scaleX = readSBits(bits) / 65536.0;
				scaleY = readSBits(bits) / 65536.0;
			} else {
				scaleX = 1;
				scaleY = 1;
			}
			if ( readUBits(1) == 1) {
				bits = readUBits(5);
				rotate0 = readSBits(bits) / 65536.0;
				rotate1 = readSBits(bits) / 65536.0;
			} else {
				rotate0 = 0;
				rotate1 = 0;
			}
			bits = readUBits(5);
			translateX = readSBits(bits) * 0.05;
			translateY = readSBits(bits) * 0.05;
			
			return new Matrix(scaleX, rotate0, rotate1, scaleY, translateX, translateY);
		}
		
		public function readRect():Rectangle {
			var bits:uint = readUBits(5);
			var xMin:Number = readSBits(bits) * 0.05;
			var xMax:Number	= readSBits(bits) * 0.05;
			var yMin:Number = readSBits(bits) * 0.05;
			var yMax:Number	= readSBits(bits) * 0.05;
			synchBits();
			return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		}
		
		public function readFloat():Number{
			synchBits();
			return _data.readFloat();
		}
		
		public function readInt():int{
			synchBits();
			return _data.readInt();
		}
		
		public function readMultiByte(length:uint, charSet:String):String{
			synchBits();
			return _data.readMultiByte(length, charSet);
		}
		
		public function readObject():*{
			synchBits();
			return _data.readObject();
		}
		
		public function readShort():int {
			synchBits();
			return _data.readShort();
		}
		
		public function readUnsignedByte():uint{
			synchBits();
			return _data.readUnsignedByte();
		}
		
		public function readUnsignedInt():uint{
			synchBits();
			return _data.readUnsignedInt();
		}
		
		public function readUnsignedShort():uint{
			synchBits();
			return _data.readUnsignedShort();
		}
		
		public function readUTF():String{
			synchBits();
			return _data.readUTF();
		}
		
		public function readUTFBytes(length:uint):String{
			synchBits();
			return _data.readUTFBytes(length);
		}
		
		public function readString():String {
			var val:String  = "";
			var char:uint;
			while ( (char = readUnsignedByte()) != 0 ) {
				val += String.fromCharCode(char);
			}
			return val;
		}
		
		public function get data()							:ByteArray { return _data; }
		public function set data(data:ByteArray)			:void { _data = data; synchBits(); }
		public function get position()						:int { return _data.position;	}
		public function set position(pos:int)				:void { _data.position = pos; }
		public function get bytesAvailable()				:uint{ return _data.bytesAvailable; }
		public function get endian()						:String{ return _data.endian; }
		public function set endian(type:String)				:void{ _data.endian = type; }
		public function get objectEncoding()				:uint{ return _data.objectEncoding; }
		public function set objectEncoding(version:uint)	:void{ _data.objectEncoding = version; }
		public function get length()						:uint{ return _data.length; }
	}
	
}