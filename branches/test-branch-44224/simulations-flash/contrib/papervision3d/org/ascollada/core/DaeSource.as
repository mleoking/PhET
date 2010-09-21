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
	import org.ascollada.core.DaeAccessor;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.types.DaeArray;	

	/**
 	 * 
	 */
	public class DaeSource extends DaeEntity
	{
		/** */
		public var accessor:DaeAccessor;
		
		/** */
		public var values:Array;
		
		/**
		 * constructor.
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		public function DaeSource( document:DaeDocument, node:XML ):void
		{
			super( document, node );
		}

		override public function destroy() : void 
		{
			super.destroy();
			
			if(this.accessor)
			{
				this.accessor.destroy();
				this.accessor = null;
			}
			
			if(this.values)
			{
				this.values = null;
			}
		}

		/**
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		override public function read( node:XML ):void
		{		
			this.values = new Array();
			
			if( node.localName() != ASCollada.DAE_SOURCE_ELEMENT )
				return;
				
			super.read( node );
			
			var data:DaeArray = new DaeArray(this.document, node);
			
			var technique_common:XML = getNode( node, ASCollada.DAE_TECHNIQUE_COMMON_ELEMENT);
			if( !technique_common )
			{
				this.values = data.values;
				return;
			}	
			
			// As a child of <source>, this element must contain exactly one <accessor> element.
			var acc:XML = getNode( technique_common, ASCollada.DAE_ACCESSOR_ELEMENT );
			if( !acc )	
				throw new Error("As a child of <source>, this element must contain exactly one <accessor> element.");
				
			this.accessor = new DaeAccessor(this.document, acc);
			
			for( var i:int = 0; i < data.count; i += this.accessor.stride )
			{
				if( this.accessor.stride > 1 )
				{
					var tmp:Array = new Array();
					for( var j:int = 0; j < this.accessor.stride; j++ )
					{
						// FIXME: spec says things depend on accessor params...
						// if( !this.accessor.params[j] || this.accessor.params[j].name == "" ) continue;
						tmp.push( data.values[i + j] );
					}
					this.values.push( tmp );
				}
				else
					this.values.push( data.values[i] );
			}
		}
	}	
}
