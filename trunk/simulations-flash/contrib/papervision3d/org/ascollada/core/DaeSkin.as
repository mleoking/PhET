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
	import org.ascollada.core.DaeSource;
	import org.ascollada.core.DaeVertexWeights;
	import org.ascollada.utils.Logger;	

	/**
	 * 
	 */
	public class DaeSkin extends DaeEntity
	{	
		/** bind shape matrix */
		public var bind_shape_matrix:Array;
		
		/** */
		public var source:String;
		
		/** */
		public var joints:Array;
		
		/** */
		public var bind_matrices:Array;
		
		/** */
		public var vertex_weights:Array;
		
		/** */
		public var blendWeightsByJointID:Object;
		
		/** */
		public var jointsType:String;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeSkin( document:DaeDocument, node:XML = null ):void
		{
			super( document, node );
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function findJointBindMatrix( node:DaeNode ):Array
		{
			var id:String = this.jointsType == "IDREF" ? node.id : node.sid;
			
			for( var i:int = 0; i < this.joints.length; i++ )
			{
				if( id == this.joints[i] )
					return this.bind_matrices[i];
			}
			return null;
		}
		
		/**
		 * 
		 * @param	id
		 * @return
		 */
		public function findJointBindMatrix2( id:String ):Array
		{
			for( var i:int = 0; i < this.joints.length; i++ )
			{
				if( id == this.joints[i] )
					return this.bind_matrices[i];
			}
			return null;
		}
		
		/**
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		public function findJointVertexWeights( node:DaeNode ):Array
		{
			var id:String = this.jointsType == "IDREF" ? node.id : node.sid;
			
			var weights:Array = new Array();
			for( var i:int = 0; i < this.vertex_weights.length; i++  )
			{
				var arr:Array = this.vertex_weights[i];
				for( var j:int = 0; j < arr.length; j++ )
					if( arr[j].joint == id )
						weights.push( arr[j] );
			}
			return weights;
		}
		
		/**
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		public function findJointVertexWeightsByIDOrSID( id:String ):Array
		{
			var weights:Array = new Array();
			for( var i:int = 0; i < this.vertex_weights.length; i++  )
			{
				var arr:Array = this.vertex_weights[i];
				for( var j:int = 0; j < arr.length; j++ )
					if( arr[j].joint == id )
						weights.push( arr[j] );
			}
			return weights;
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void
		{			
			this.joints = new Array();
			this.vertex_weights = new Array();
			this.bind_matrices = new Array();
			
			if( node.localName() != ASCollada.DAE_CONTROLLER_SKIN_ELEMENT )
				return;
				
			super.read( node );
										
			// required - ref to skin's geometry
			this.source = getAttribute(node, ASCollada.DAE_SOURCE_ATTRIBUTE);
			
			Logger.log( "reading skin, source: " + this.source );
						
			// optional - bind_shape_matrix, defaults to identity matrix
			var bindList:XMLList = getNodeList(node, ASCollada.DAE_BINDSHAPEMX_SKIN_PARAMETER);	
			if( bindList.length() )
				this.bind_shape_matrix = getFloats( bindList[0] );
			else
				this.bind_shape_matrix = [1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1];
				
			// 3 or more <source> elements
			var sourceList:XMLList = getNodeList(node, ASCollada.DAE_SOURCE_ELEMENT);
			if( sourceList.length() < 3 )
				throw new Error( "<skin> requires a minimum of 3 <source> elements!" );
				
			// exactly 1 <joints> element
			var jointsNode:XML = getNode(node, ASCollada.DAE_JOINTS_ELEMENT);
			if( !jointsNode )
				throw new Error( "need exactly one <joints> element!" );
			
			// exactly 1 <vertex_weights> element
			var weightsNode:XML = getNode(node, ASCollada.DAE_WEIGHTS_ELEMENT);
			if( !weightsNode )
				throw new Error( "need exactly one <vertex_weights> element!" );
			
			var jointsList:XMLList = getNodeList(jointsNode, ASCollada.DAE_INPUT_ELEMENT);
			var weights:DaeVertexWeights = new DaeVertexWeights(this.document, weightsNode );
			
			var srcNode:XML;
			var input:DaeInput;
			
			// fetch sources for <joints>
			var src:DaeSource;
			//var sources:Object = new Object();
			for each( var inputNode:XML in jointsList )
			{
				input = new DaeInput(this.document, inputNode );
				srcNode = getNodeById(node, ASCollada.DAE_SOURCE_ELEMENT, input.source);
				if( !srcNode )
					throw new Error( "source not found! (id='" + input.source + "')" );
				
				src = new DaeSource(this.document, srcNode);
				
				switch( input.semantic )
				{
					case ASCollada.DAE_JOINT_SKIN_INPUT:
						this.joints = src.values;
						this.jointsType = src.accessor.params[ASCollada.DAE_JOINT_SKIN_INPUT];
						//Logger.log( " => => joints: " + this.joints );
						break;
						
					case ASCollada.DAE_BINDMATRIX_SKIN_INPUT:
						this.bind_matrices = src.values;
						//Logger.log( " => => bind_matrices: " + this.bind_matrices );
						break;
						
					default:
						break;
				}
			}
			
			var maxOffset:int = 0;
			var jointOffset:uint = 0;
			var weightOffset:uint = 1;
			var tmpWeights:Array;
			
			// fetch sources for <vertex_weights>
			for each( input in weights.inputs )
			{
				srcNode = getNodeById(node, ASCollada.DAE_SOURCE_ELEMENT, input.source);
				if( !srcNode )
					throw new Error( "source not found! (id='" + input.source + "')" );
					
				src = new DaeSource(this.document, srcNode);
				
				switch( input.semantic )
				{	
					case ASCollada.DAE_JOINT_SKIN_INPUT:
						jointOffset = input.offset;
						maxOffset++;
						break;
						
					case ASCollada.DAE_WEIGHT_SKIN_INPUT:
						tmpWeights = src.values;
						weightOffset = input.offset;
						maxOffset++;
						
						break;
						
					default:
						break;
				}
			}
			
			var cur:int = 0;
			
			for( var i:int = 0; i < weights.vcounts.length; i++ )
			{
				var vcount:int = weights.vcounts[i];
				
				var tmp:Array = new Array();
					
				for( var j:int = 0; j < vcount; j++ )
				{
					var jidx:int = weights.v[cur + jointOffset ];
					var widx:int = weights.v[cur + weightOffset];
					
					var w:Number = tmpWeights[ widx ];
					var jnt:String = jidx < 0 ? null : this.joints[ jidx ];
					
					var blendWeight:DaeBlendWeight = new DaeBlendWeight( i, jnt, w );
					tmp.push( blendWeight );
					
					cur += maxOffset;
				}
				
				this.vertex_weights[i] = tmp;
			}
			
			Logger.log( " => => #vertex_weights " + vertex_weights.length );
		}
		
		/**
		 * normalize blendweights.
		 * 
		 * @param	blendWeights	the weights to normalize.
		 */
		public function normalizeBlendWeights( blendWeights:Array ):void
		{
			var i:int, j:int;
						
			for( i = 0; i < blendWeights.length; i++  )
			{
				var arr:Array = blendWeights[i];
				
				var weightSum:Number = 0;
				for( j = 0; j < arr.length; j++ )
				{
					weightSum += arr[j].weight;
					arr[j].originalWeight = arr[j].weight;
				}	
				if( weightSum == 0.0 || weightSum == 1.0 ) continue;
				
				var invWeightSum:Number = 1.0 / weightSum;
				
				for( j = 0; j < arr.length; j++ )
					arr[j].weight *= invWeightSum;
			}
		}
	}	
}
