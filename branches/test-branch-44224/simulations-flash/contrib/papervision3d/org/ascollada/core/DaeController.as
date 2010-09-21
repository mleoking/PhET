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
	import org.ascollada.core.DaeMorph;
	import org.ascollada.core.DaeSkin;
	import org.ascollada.utils.Logger;
		
	/**
	 * 
	 */
	public class DaeController extends DaeEntity
	{	
		public static const TYPE_SKIN:uint = 0;
		public static const TYPE_MORPH:uint = 1;
		
		/** */
		public var type:uint;
		
		/** */
		public var skin:DaeSkin;
		
		/** */
		public var morph:DaeMorph;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeController( document : DaeDocument, node:XML = null ):void
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
			if( node.localName() != ASCollada.DAE_CONTROLLER_ELEMENT ) return;
			
			super.read( node );
			
			Logger.log( "reading controller: " + this.id );
			
			// <skin> or <morph> 
			var skinNode:XML = getNode(node, ASCollada.DAE_CONTROLLER_SKIN_ELEMENT);			
			if( skinNode )
			{
				this.skin = new DaeSkin(this.document, skinNode );
				this.type = TYPE_SKIN;
			}
			else 
			{
				// ok, must be a morph!
				var morphNode:XML = getNode(node, ASCollada.DAE_CONTROLLER_MORPH_ELEMENT);
				if( morphNode )
				{
					this.morph = new DaeMorph(this.document, morphNode );
					this.type = TYPE_MORPH;
				}
			}
			
			if( !this.skin && !this.morph )
				throw new Error( "controller element should contain a <skin> or a <morph> element!" );
				
			// <asset> 0..1
			// TODO: implement
			
			// <extra> 0..N
			// TODO: implement
			
			if( this.type == TYPE_SKIN )
			{
				//this.skin.joints.inputs
			}
			else
			{
				
			}
		}
	}	
}
