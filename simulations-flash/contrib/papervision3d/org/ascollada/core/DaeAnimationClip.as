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
	public class DaeAnimationClip extends DaeEntity
	{	
		/** start time */
		public var start:Number;
		
		/** end time */
		public var end:Number;
		
		/** */
		public var instance_animation:Array;
		
		/**
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		public function DaeAnimationClip( document : DaeDocument, node:XML = null ):void
		{			
			this.start = 0.0;
			this.end = 0.0;
			this.instance_animation = new Array();
			
			super( document, node );
		}
				
		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void
		{							
			if( node.localName() != ASCollada.DAE_ANIMCLIP_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_ANIMCLIP_ELEMENT + "' element" );
				
			super.read( node );
			
			Logger.log( "reading animation_clip: " + this.id );
			
			this.name = this.name && this.name.length ? this.name : this.id;
			this.instance_animation = new Array();			
			this.start = getAttributeAsFloat( node, ASCollada.DAE_START_ATTRIBUTE );
			this.end = getAttributeAsFloat( node, ASCollada.DAE_END_ATTRIBUTE );
			
			var anims:XMLList = getNodeList( node, ASCollada.DAE_INSTANCE_ANIMATION_ELEMENT );
			
			for each( var animNode:XML in anims )
			{
				var url:String = getAttribute(animNode, ASCollada.DAE_URL_ATTRIBUTE);
				this.instance_animation.push( url );
			}
		}
	}
}
