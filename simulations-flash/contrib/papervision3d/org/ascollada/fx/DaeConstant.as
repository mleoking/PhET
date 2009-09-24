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

	/**
	 * 
	 */
	public class DaeConstant extends DaeEntity
	{
		public var emission:DaeColorOrTexture;
		public var reflective:DaeColorOrTexture;
		public var reflectivity:Number = 0;
		public var transparent:DaeColorOrTexture;
		public var transparency:Number = 0;
		public var index_of_refraction:Number = 0;

		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeConstant( document:DaeDocument, node:XML = null ):void
		{
			super(document, node);
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
					case ASCollada.DAE_EMISSION_MATERIAL_PARAMETER:
						this.emission = new DaeColorOrTexture(this.document, child);
						break;
						
					case ASCollada.DAE_REFLECTIVE_MATERIAL_PARAMETER:
						this.reflective = new DaeColorOrTexture(this.document,  child);
						break;
						
					case ASCollada.DAE_REFLECTIVITY_MATERIAL_PARAMETER:
						this.reflectivity = parseFloat( getNodeContent(getNode(child, "float")) );
						break;
						
					case ASCollada.DAE_TRANSPARENT_MATERIAL_PARAMETER:
						this.transparent = new DaeColorOrTexture(this.document, child);
						break;
					
					case ASCollada.DAE_TRANSPARENCY_MATERIAL_PARAMETER:
						this.transparency = parseFloat( getNodeContent(getNode(child, "float")) );
						break;
						
					case ASCollada.DAE_INDEXOFREFRACTION_MATERIAL_PARAMETER:
						this.reflectivity = parseFloat( getNodeContent(getNode(child, "float")) );
						break;
						
					default:
						break;
				}
			}
		}
	}	
}
