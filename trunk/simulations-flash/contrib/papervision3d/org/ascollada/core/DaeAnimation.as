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
	import org.ascollada.core.DaeChannel;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.core.DaeSampler;
	import org.ascollada.namespaces.collada;

	/**
	 * 
	 */
	public class DaeAnimation extends DaeEntity
	{	
		use namespace collada;
		
		/** channels */
		public var channels:Array;
		
		/** child animations */
		public var animations:Array;
		
		private static var _newID : int = 0;
		
		/**
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		public function DaeAnimation( document : DaeDocument, node:XML = null ):void
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
			this.animations = new Array();
			this.channels = new Array();
			var samplers : Array = new Array();
			
			if( node.localName() != ASCollada.DAE_ANIMATION_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_ANIMATION_ELEMENT + "' element" );
				
			super.read( node );
			
			this.id = (this.id && this.id.length) ? this.id : "animation_" + (_newID++);

			node.@id = this.id;
			
			var list : XMLList = node.children();
			var child : XML;
			var num : int = list.length();
			var i : int;
			
			for(i = 0; i < num; i++) {
				child = list[i];
				
				switch(child.localName() as String) {
					case "animation":
						this.animations.push(new DaeAnimation(this.document, child));
						break;
					
					case "channel":
						this.channels.push(new DaeChannel(this.document, child));
						break;
					
					case "sampler":
						samplers.push(new DaeSampler(this.document, child));
						break;
								
					default:
						break;	
				}
			}
			
			if(this.channels.length && this.channels.length == samplers.length) 
			{
				var tmp : Array = new Array();
				for(i = 0; i < this.channels.length; i++) {
					var channel : DaeChannel = this.channels[i];
					
					channel.sampler = samplers[i];
					
					if(channel.sampler.input && channel.sampler.input.values && 
					   channel.sampler.input.values.length)
					{
						tmp.push(channel);
						
						var animID : String = channel.syntax.targetID;
						
						this.document.animatables[animID] = this.document.animatables[animID] || new Array();
						this.document.animatables[animID].push(channel);
					}
				}
				this.channels = tmp;
			}
		}
	}
}
