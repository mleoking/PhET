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

	public class DaeCamera extends DaeEntity
	{
		public static const TYPE_X:uint = 0;
		public static const TYPE_Y:uint = 1;
		
		public var ortho:Boolean = false;
		
		public var fov:Number;
		
		public var mag:Number;
		
		public var aspect_ratio:Number;
		
		public var near:Number;
		
		public var far:Number;
		
		public var target:String;
		
		public var type:uint = 0;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeCamera( document : DaeDocument, node:XML ):void
		{
			super(document, node);
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void
		{							
			if( node.localName() != ASCollada.DAE_CAMERA_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_CAMERA_ELEMENT + "' element" );
				
			super.read( node );
			
			this.target = null;
			
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( String(child.localName()) )
				{
					case ASCollada.DAE_OPTICS_ELEMENT:
						readOptics(child);
						break;
					case ASCollada.DAE_EXTRA_ELEMENT:
						readExtra(child);
						break;
					default:
						break;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readOptics( node:XML ):void
		{
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( String(child.localName()) )
				{
					case ASCollada.DAE_TECHNIQUE_COMMON_ELEMENT:
						readTechniqueCommon(child);
						break;
					default:
						break;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readTechniqueCommon( node:XML ):void
		{
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( String(child.localName()) )
				{
					case ASCollada.DAE_CAMERA_PERSP_ELEMENT:
						this.ortho = false;
						readPerspective(child);
						break;
					case ASCollada.DAE_CAMERA_ORTHO_ELEMENT:
						this.ortho = true;
						readOrthogonal(child);
						break;
					default:
						break;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readPerspective( node:XML ):void
		{
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( String(child.localName()) )
				{
					case ASCollada.DAE_XFOV_CAMERA_PARAMETER:
						this.type = TYPE_X;
						this.fov = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_YFOV_CAMERA_PARAMETER:
						this.type = TYPE_Y;
						this.fov = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_ASPECT_CAMERA_PARAMETER:
						this.aspect_ratio = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_ZNEAR_CAMERA_PARAMETER:
						this.near = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_ZFAR_CAMERA_PARAMETER:
						this.far = parseFloat(getNodeContent(child)); 
						break;
					default:
						break;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readOrthogonal( node:XML ):void
		{
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( String(child.localName()) )
				{
					case ASCollada.DAE_XMAG_CAMERA_PARAMETER:
						this.type = TYPE_X;
						this.fov = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_YMAG_CAMERA_PARAMETER:
						this.type = TYPE_Y;
						this.fov = parseFloat(getNodeContent(child)); 
						break;	
					case ASCollada.DAE_ASPECT_CAMERA_PARAMETER:
						this.aspect_ratio = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_ZNEAR_CAMERA_PARAMETER:
						this.near = parseFloat(getNodeContent(child)); 
						break;
					case ASCollada.DAE_ZFAR_CAMERA_PARAMETER:
						this.far = parseFloat(getNodeContent(child)); 
						break;
					default:
						break;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readExtra( node:XML ):void
		{
			var children:XMLList = node.children();
			var numChildren:int = children.length();
			
			for( var i:int = 0; i < numChildren; i++ )
			{
				var child:XML = children[i];
				
				switch( String(child.localName()) )
				{
					case ASCollada.DAE_TECHNIQUE_ELEMENT:
						var c:XML = getNode(child, ASCollada.DAEMAX_TARGET_CAMERA_PARAMETER);
						if( c )
						{
							this.target = getNodeContent(c);
							this.target = this.target.split("#")[1];
						}
						break;
					default:
						break;
				}
			}
		}
	}
}
