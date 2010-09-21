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
 
package org.ascollada.core {
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.types.DaeAddressSyntax;	

	/**
	 * 
	 */
	public class DaeChannel extends DaeEntity
	{	
		/** source - required */
		public var source:String;
		
		/** target - required */
		public var target:String;
		
		/** some info on the target attribute */
		public var syntax:DaeAddressSyntax;
		
		/** */
		public var sampler : DaeSampler;
		
		/**
		 * 
		 * @param	node
		 *  
		 * @return
		 */
		public function DaeChannel( document : DaeDocument, node:XML ):void
		{
			super( document, node );
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void
		{							
			if( node.localName() != ASCollada.DAE_CHANNEL_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_CHANNEL_ELEMENT + "' element" );
				
			super.read( node );
			
			this.source = getAttribute(node, ASCollada.DAE_SOURCE_ATTRIBUTE);
			this.target = getAttribute(node, ASCollada.DAE_TARGET_ATTRIBUTE);
			
			this.syntax = DaeAddressSyntax.parse(this.target);
		}
	}
}
