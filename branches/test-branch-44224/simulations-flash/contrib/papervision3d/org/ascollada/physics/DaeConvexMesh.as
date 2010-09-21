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
 
package org.ascollada.physics {
	import org.ascollada.core.DaeDocument;	
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeGeometry;
	import org.ascollada.core.DaeMesh;
	
	/**
	 * 
	 */
	public class DaeConvexMesh extends DaeMesh {
		
		public var convex_hull_off:String;
		
		public var isHull:Boolean;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeConvexMesh(document:DaeDocument, geometry:DaeGeometry, node:XML = null):void {
			super(document, geometry, node);
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void {			
			
			if( node.localName() != ASCollada.DAE_CONVEX_MESH_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_CONVEX_MESH_ELEMENT + "' element" );
			
			this.convex_hull_off = getAttribute(node, ASCollada.DAE_CONVEX_HULL_OF_ATTRIBUTE, true);
			
			this.isHull = (this.convex_hull_off.length > 0);
			if( this.isHull )
				return;
				
			super.read(node);
		}
	}
}