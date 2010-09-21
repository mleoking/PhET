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
	/**
	 * @author	Tim Knip 
	 */
	public class DaeAddressSyntax 
	{
		/** */
		public var targetID:String;
		
		/** */
		public var targetSID:String;
		
		/** */
		public var member:String;
		
		/** */
		public var isArrayAccess:Boolean;
		
		/** */
		public var isDotAccess:Boolean;
		
		/** */
		public var isFullAccess:Boolean;
		
		/** */
		public var arrayIndex0 : int;
		
		/** */
		public var arrayIndex1 : int;
		
		/**
		 * Constructor.
		 */
		public function DaeAddressSyntax()
		{
			this.isDotAccess = this.isArrayAccess = this.isFullAccess = false;
			this.arrayIndex0 = -1;
			this.arrayIndex1 = -1;
		}

		/**
		 * 
		 */
		public static function parse(target : String) : DaeAddressSyntax
		{
			var syntax : DaeAddressSyntax = new DaeAddressSyntax();
			
			var pattern : RegExp = /\((\d+)\)\((\d+)\)/;
			var matches : Array = target.match(pattern);
			if(!matches)
			{
				pattern = /\((\d+)\)/;
				matches = target.match(pattern);
			}
			
			if(matches)
			{
				// array access
				target = target.replace(pattern, "");
				syntax.isArrayAccess = true;
				syntax.arrayIndex0 = parseInt(matches[1], 10);
				if(matches.length > 2)
				{
					syntax.arrayIndex1 = parseInt(matches[2], 10);
				}
			}
			else
			{
				var pos : int = target.lastIndexOf(".");
				if(pos != -1)
				{
					// dot access
					syntax.member = target.substr(pos+1);
					syntax.isDotAccess = true;
					target = target.substr(0, pos);
				}
				else
				{
					syntax.isFullAccess = true;
				}
			}
			
			if(target.indexOf("/") != -1)
			{
				var parts : Array = target.split("/");	
				
				syntax.targetID = String(parts.shift());
				syntax.targetSID = parts.join("/");
			} 
			else
			{
				syntax.targetID = target;
			}
			
			return syntax;
		}
		
		/**
		 * 
		 */
		public function toString():String
		{
			return "\ntarget: " + targetID + 
				"\nsid: " + targetSID + 
				"\nmember: " + member +
				"\narrayIndex0: " + arrayIndex0 + 
				"\narrayIndex1: " + arrayIndex1;
		}
	}	
}
