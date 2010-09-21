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
	import org.ascollada.fx.DaeBindMaterial;	
	import org.ascollada.ASCollada;
	import org.ascollada.fx.DaeBindVertexInput;
	import org.ascollada.fx.DaeInstanceMaterial;	

	/**
	 * 
	 */
	public class DaeInstanceGeometry extends DaeEntity
	{		
		/** */
		public var url:String;
		
		/** */
		public var bindMaterial : DaeBindMaterial;

		/**
		 * 
		 */
		public function DaeInstanceGeometry( document:DaeDocument, node:XML = null )
		{
			super( document, node );
		}

		/**
		 * 
		 */
		override public function destroy() : void 
		{
			super.destroy();
			
			if(this.bindMaterial)
			{
				this.bindMaterial.destroy();
				this.bindMaterial = null;
			}
		}

		/**
		 * 
		 */ 
		public function findBindVertexInput( materialId:String, semantic:String ) : DaeBindVertexInput
		{
			var material : DaeInstanceMaterial = this.bindMaterial.getInstanceMaterialBySymbol(materialId);
			
			if(material)
			{
				return material.findBindVertexInput( semantic );
			}	
			
			return null;
		}
		
		/**
		 * 
		 * @param	node
		 */
		override public function read( node:XML ):void
		{
			super.read( node );
			
			this.url = getAttribute( node, ASCollada.DAE_URL_ATTRIBUTE );
		
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];

				switch( child.localName() )
				{	
					case ASCollada.DAE_BINDMATERIAL_ELEMENT:
						this.bindMaterial = new DaeBindMaterial(this.document, child);
						break;
						
					default:
						break;
				}
			}
		}
	}
}
