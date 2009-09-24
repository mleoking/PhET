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
 
package org.ascollada.fx {
	import org.ascollada.core.DaeDocument;	
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.utils.Logger;	

	public class DaeNewParam extends DaeEntity
	{
		public var type:String;
		
		public var surface:DaeSurface;
		
		public var sampler2D:DaeSampler2D;
		
		/**
		 * 
		 * @param	node
		 */
		public function DaeNewParam( document:DaeDocument, node:XML = null )
		{
			super( document, node );
		}
		
		/**
		 * 
		 * @param	node
		 */
		override public function read( node:XML ):void
		{
			if( node.localName() != ASCollada.DAE_FXCMN_NEWPARAM_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_FXCMN_NEWPARAM_ELEMENT + "' element" );
				
			super.read( node );
			
			Logger.log( " => newparam @sid=" + this.sid );
			
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( child.localName() )
				{
					case ASCollada.DAE_FXCMN_SURFACE_ELEMENT:
						this.type = ASCollada.DAE_FXCMN_SURFACE_ELEMENT;
						this.surface = new DaeSurface(this.document, child);
						break;
						
					case ASCollada.DAE_FXCMN_SAMPLER2D_ELEMENT:
						this.type = ASCollada.DAE_FXCMN_SAMPLER2D_ELEMENT;
						this.sampler2D = new DaeSampler2D(this.document, child);
						break;
						
					default:
						break;
				}
			}
		}
	}	
}
