/*
nochump.util.zip.Deflater
Copyright (C) 2007 David Chang (dchang@nochump.com)

This file is part of nochump.util.zip.

nochump.util.zip is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

nochump.util.zip is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/
package nochump.util.zip {
	import flash.utils.ByteArray;	

	/**
	 * This is the Deflater class.  The deflater class compresses input
	 * with the deflate algorithm described in RFC 1951.  It uses the
	 * ByteArray compress method to deflate.
	 * 
	 * @author David Chang
	 */
	public class Deflater {
		
		private var buf:ByteArray;
		private var compressed:Boolean;
		private var totalIn:uint;
		private var totalOut:uint;
		
		/**
		 * Creates a new deflater.
		 */
		public function Deflater() {
			reset();
		}
		
		/** 
		 * Resets the deflater.  The deflater acts afterwards as if it was
		 * just created.
		 */
		public function reset():void {
			buf = new ByteArray();
			//buf.endian = Endian.LITTLE_ENDIAN;
			compressed = false;
			totalOut = totalIn = 0;
		}
		
		/**
		 * Sets the data which should be compressed next.
		 * 
		 * @param input the buffer containing the input data.
		 */
		public function setInput(input:ByteArray):void {
			buf.writeBytes(input);
			totalIn = buf.length;
		}
		
		/**
		 * Deflates the current input block to the given array.
		 * 
		 * @param output the buffer where to write the compressed data.
		 */
		public function deflate(output:ByteArray):uint {
			if(!compressed) {
				buf.compress();
				compressed = true;
			}
			output.writeBytes(buf, 2, buf.length - 6); // remove 2-byte header and last 4-byte addler32 checksum
			totalOut = output.length;
			return 0;
		}
		
		/**
		 * Gets the number of input bytes.
		 */
		public function getBytesRead():uint {
			return totalIn;
		}
		
		/**
		 * Gets the number of output bytes.
		 */
		public function getBytesWritten():uint {
			return totalOut;
		}
		
	}
	
}