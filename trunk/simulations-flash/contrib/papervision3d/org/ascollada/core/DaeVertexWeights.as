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
 
package org.ascollada.core
{
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.utils.Logger;
		
	/**
	 * 
	 */
	public class DaeVertexWeights extends DaeEntity
	{	
		/** */
		public var count:int;
		
		/** */
		public var inputs:Array;
		
		/** */
		public var v:Array;
		
		/** */
		public var vcounts:Array;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeVertexWeights( document:DaeDocument, node:XML = null ):void
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
			this.inputs = new Array();
			
			if( node.localName() != ASCollada.DAE_WEIGHTS_ELEMENT )
				throw new Error( "not a <" + ASCollada.DAE_WEIGHTS_ELEMENT + "> element!" );
				
			super.read( node );
			
			this.count = getAttributeAsInt(node,ASCollada.DAE_COUNT_ATTRIBUTE);
			
			Logger.log( " => reading vertex_weights" );
			
			// require 2 or more <input> elements
			var inputList:XMLList = getNodeList(node, ASCollada.DAE_INPUT_ELEMENT);
			if( inputList.length() < 2 )
				throw new Error( "<joints> requires at least 2 <input> elements!" );
				
			// parse <input> elements
			for( var i:int = 0; i < inputList.length(); i++ )
			{
				var input:DaeInput = new DaeInput(this.document, inputList[i]);
				this.inputs.push( input);
			}
			
			this.v = new Array();
			this.vcounts = new Array();
			
			var vNode:XML = getNode(node, ASCollada.DAE_VERTEX_ELEMENT);
			var vcountNode:XML = getNode(node, ASCollada.DAE_VERTEXCOUNT_ELEMENT);
			
			if( !vNode || !vcountNode )
				return;
				
			// Describes which bones and attributes are associated with each
			// vertex. An index of -1 into the array of joints refers to the bind shape.
			// Weights should be normalized before use.
			this.v = getInts( vNode );
			
			// Describes the number of bones associated with each vertex.
			this.vcounts = getInts( vcountNode );
		}
	}	
}
