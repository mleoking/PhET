/*
 * Copyright 2007 (c) Tim Knip, suite75.com.
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
	import org.ascollada.types.DaeColorOrTexture;
	import org.ascollada.utils.Logger;
	
	/**
	 * 
	 */
	public class DaeBlinn extends DaeLambert
	{
		public var specular:DaeColorOrTexture;
		public var shininess:Number = 0;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeBlinn( document:DaeDocument, node:XML = null ):void
		{
			super( document, node );
		}
		
		/**
		 * 
		 * @param	node
		 */
		override public function read( node:XML ):void
		{
			super.read( node );
			
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
								
				switch( child.localName() )
				{						
					case ASCollada.DAE_SPECULAR_MATERIAL_PARAMETER:
						this.specular = new DaeColorOrTexture(this.document, child);
						break;
						
					case ASCollada.DAE_SHININESS_MATERIAL_PARAMETER:
						this.shininess = parseFloat( getNodeContent( getNode(child, "float") ) );
						Logger.log( " => shininess: " + this.shininess );
						break;
						
					default:
						break;
				}
			}
		}
	}	
}
