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
 
package org.ascollada.types {
	import org.ascollada.core.DaeDocument;	
	import org.ascollada.core.DaeEntity;
	import org.ascollada.fx.DaeTexture;	

	/**
	 * 
	 */
	public class DaeColorOrTexture extends DaeEntity
	{
		public static const TYPE_COLOR:uint = 0;
		public static const TYPE_TEXTURE:uint = 1;
		public static const TYPE_PARAM:uint = 2;
		
		public var type:uint;
		
		public var color:Array;
		public var texture:DaeTexture;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeColorOrTexture( document:DaeDocument, node:XML = null ):void
		{
			this.type = TYPE_COLOR;
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
					case "color":
						this.type = TYPE_COLOR;
						this.color = getFloats( child );
						
						return;
					
					case "texture":
						this.type = TYPE_TEXTURE;
						this.texture = new DaeTexture(this.document, child);
						
						return;
						
					case "param":
						
						this.type = TYPE_PARAM;
						return;
						
					default:
						break;
				}
			}
		}		
	}	
}
