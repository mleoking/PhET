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
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeEntity;	

	/**
	 * 
	 */
	public class DaeArray extends DaeEntity
	{	
		/** */
		public var values:Array;
		
		/** */
		public var count:int;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeArray( document:DaeDocument, node:XML = null ):void
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
			super.read( node );

			this.count = 0;
			this.values = getData( node );
						
			if( !this.values )
				throw new Error( " no data!");
		}
		
		
		/**
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		private function getData( node:XML ):Array
		{
			var children:XMLList = node.children();
			var cnt:int = children.length();
			
			for( var i:int = 0; i < cnt; i++ )
			{
				var child:XML = children[i];
				
				switch( child.localName() )
				{
					case ASCollada.DAE_BOOL_ARRAY_ELEMENT:
						this.count = getAttributeAsInt(child, ASCollada.DAE_COUNT_ATTRIBUTE);
						return getBools( child );
						
					case ASCollada.DAE_INT_ARRAY_ELEMENT:
						this.count = getAttributeAsInt(child, ASCollada.DAE_COUNT_ATTRIBUTE);
						return getInts( child );
						
					case ASCollada.DAE_IDREF_ARRAY_ELEMENT:
						this.count = getAttributeAsInt(child, ASCollada.DAE_COUNT_ATTRIBUTE);
						return getStrings( child );
						
					case ASCollada.DAE_FLOAT_ARRAY_ELEMENT:
						this.count = getAttributeAsInt(child, ASCollada.DAE_COUNT_ATTRIBUTE);
						return getFloats( child );
						
					case ASCollada.DAE_NAME_ARRAY_ELEMENT:
						this.count = getAttributeAsInt(child, ASCollada.DAE_COUNT_ATTRIBUTE);
						return getStrings( child );
						
					default:
						break;
				}
			}
			
			return null;
		}
	}	
}
