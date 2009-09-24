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

	/**
	 * 
	 */
	public class DaeMesh extends DaeEntity {
		
		/** */
		public var geometry : DaeGeometry;
		
		/** vertices */
		public var vertices : DaeVertices;
		
		/** */
		public var primitives:Array;
		
		/**
		 * 
		 * @param	node
		 */
		public function DaeMesh( document:DaeDocument, geometry:DaeGeometry, node:XML = null ) {
			super( document, node );
			this.geometry = geometry;
		}

		/**
		 * 
		 */
		override public function destroy() : void {
			super.destroy();
			
			if(this.vertices) {
				this.vertices.destroy();
				this.vertices = null;
			}
			
			if(this.primitives) {
				for each(var primitive : DaePrimitive in this.primitives) {
					primitive.destroy();
				}
				this.primitives = null;
			}
			
			this.geometry = null;
		}

		/**
		 * 
		 * @return
		 */
		override public function read( node:XML ):void {
			if( node.localName() != ASCollada.DAE_MESH_ELEMENT && node.localName() != ASCollada.DAE_CONVEX_MESH_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_MESH_ELEMENT + " or a '" + ASCollada.DAE_CONVEX_MESH_ELEMENT + "' element" );
				
			super.read( node );
		
			this.primitives = new Array();
			
			// fetch <vertices> element
			var verticesNode:XML = getNode(node, ASCollada.DAE_VERTICES_ELEMENT);
			
			this.vertices = new DaeVertices(this.document, verticesNode);
			
			var inputList:XMLList = getNodeList(verticesNode, ASCollada.DAE_INPUT_ELEMENT);
			var inputNode:XML;
			
			for each(inputNode in inputList) {
				var input : DaeInput = new DaeInput(this.document, inputNode);
				if( input.semantic == "POSITION" ) {
					this.vertices.source = this.document.sources[ input.source ];
				}
			}	
			
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ ) {
				var child:XML = children[i];
				
				switch( String(child.localName()) ) {
					case ASCollada.DAE_TRIANGLES_ELEMENT:
					case ASCollada.DAE_TRIFANS_ELEMENT:
					case ASCollada.DAE_TRISTRIPS_ELEMENT:
					case ASCollada.DAE_LINESTRIPS_ELEMENT:
					case ASCollada.DAE_LINES_ELEMENT:
					case ASCollada.DAE_POLYGONS_ELEMENT:
					case ASCollada.DAE_POLYLIST_ELEMENT:
						var primitive : DaePrimitive = new DaePrimitive(this.document, this, child);
						if( primitive.count > 0 )
						{
							this.primitives.push( primitive );
						}
						break;
					default:
						break;
				}
			}
		}
	}
}
