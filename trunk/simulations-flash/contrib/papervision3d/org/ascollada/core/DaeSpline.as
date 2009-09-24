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
	import org.ascollada.core.DaeInput;
	import org.ascollada.core.DaeSource;	

	/**
	 * 
	 */
	public class DaeSpline extends DaeEntity
	{		
		/** */
		public var vertices:Array;
		
		/** */
		public var closed:Boolean;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeSpline( document:DaeDocument, node:XML ):void
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
			if( node.localName() != ASCollada.DAE_SPLINE_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_SPLINE_ELEMENT + "' element" );
				
			super.read( node );
			
			this.closed = getAttribute(node,ASCollada.DAE_CLOSED_ATTRIBUTE)  == "true" ? true : false;
				
			var sourceList:XMLList = getNodeList( node, ASCollada.DAE_SOURCE_ELEMENT );
			if( sourceList == new XMLList() )
				throw new Error( "<spline> requires at least one <source> element!" );
				
			var cvsNode:XML = getNode( node, ASCollada.DAE_CONTROL_VERTICES_ELEMENT );
			if( !cvsNode )
				throw new Error( "<spline> requires exactly one <control_vertices> element!" );

			var inputList:XMLList = getNodeList( cvsNode, ASCollada.DAE_INPUT_ELEMENT );
			var inputNode:XML;
			var input:DaeInput;
			var sourceNode:XML;
			var source:DaeSource;
			
			for each( inputNode in inputList )
			{
				input = new DaeInput(this.document, inputNode );
				
				switch( input.semantic )
				{
					// The position of the control vertex
					case "POSITION":
						sourceNode = getNodeById( node, ASCollada.DAE_SOURCE_ELEMENT, input.source);
						if( !sourceNode )
							throw new Error( "source with id=" + input.source + " not found!" );
						source = new DaeSource(this.document, sourceNode[0] );
						this.vertices = source.values;
						break;
					
					// The type of polynomial interpolation to represent the segment
					// starting at the CV. Common-profile types are:
					// LINEAR, BEZIER, HERMITE, CARDINAL, BSPLINE, STEP, and
					// NURBS
					case "INTERPOLATION":
						break;
					
					// The tangent that controls the shape of the segment preceding the
					// CV (BEZIER and HERMITE)
					case "IN_TANGENT":
						break;
					
					// The tangent that controls the shape of the segment following the
					// CV (BEZIER and HERMITE)
					case "OUT_TANGENT":
						break;
						
					// Defines the continuity constraint at the CV.
					// The common-profile types are: C0, C1, G1
					case "CONTINUITY":
						break;
					
					// The number of piece-wise linear approximation steps to use for
					// the spline segment that follows this CV
					case "LINEAR_STEPS":
						break;
						
					default:
						break;
				}
			}
		}
	}	
}
