/*
 * Copyright 2007 (c) Tim Knip, ascollada.org.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
 
package org.ascollada.types 
{
	import org.ascollada.ASCollada;
	import org.ascollada.utils.Logger;
	
	/**
	 * @author	Tim Knip 
	 */
	public class DaeTransform 
	{
		/** type - required */
		public var type:String;
		
		/** sid - optional */
		public var sid:String;
		
		/** */
		public var values:Array;
		
		/** */
		public var animated:Boolean;
		
		/**
		 * 
		 * @param	type
		 * @param	values
		 * @return
		 */	
		public function DaeTransform(type:String, sid:String, values:Array):void
		{
			this.type = type;
			this.sid = sid;
			this.values = values;
			this.animated = false;
			
			if( !validateValues() )
			{
				Logger.log( "[ERROR] invalid values for this transform!" );
				throw new Error( "[ERROR] invalid values for this transform!" );
			}
		}
		
		/**
		 * 
		 * @return
		 */
		public function validateValues():Boolean
		{
			var valid:Boolean = false;
			
			if( !this.values || !this.values.length )
				return false;
				
			switch( this.type )
			{
				case ASCollada.DAE_ROTATE_ELEMENT:
					valid = (this.values.length == 4);
					break;
				case ASCollada.DAE_TRANSLATE_ELEMENT:
					valid = (this.values.length == 3);
					break;
				case ASCollada.DAE_SCALE_ELEMENT:
					valid = (this.values.length == 3);
					break;
				case ASCollada.DAE_MATRIX_ELEMENT:
					valid = (this.values.length == 16);
					break;
				default:
					break;
			}
			
			return valid;
		}
	}
}
