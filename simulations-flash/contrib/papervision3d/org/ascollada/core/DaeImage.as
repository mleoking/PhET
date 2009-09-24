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
	import flash.display.BitmapData;
	
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeAsset;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.utils.Logger;
	
	/**
	 * 
	 */
	public class DaeImage extends DaeEntity
	{
		/** */
		public var init_from : String;
		
		/** */
		public var bitmapData : BitmapData;
		
		/**
		 * 
		 */
		public function DaeImage( document : DaeDocument, node:XML = null )
		{
			this.init_from = "";
			
			super( document, node );
		}

		override public function destroy() : void 
		{
			super.destroy();
			
			if(this.bitmapData)
			{
				this.bitmapData.dispose();
				this.bitmapData = null;
			}
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
					case ASCollada.DAE_ASSET_ELEMENT:
						this.asset = new DaeAsset(this.document, child);
						break;
					
					case ASCollada.DAE_DATA_ELEMENT:
						break;
						
					case ASCollada.DAE_INITFROM_ELEMENT:
						this.init_from = unescape( child.text().toString() );
						this.init_from.split("\\").join("/");
						/*
						var urlParts:Array = this.init_from.split("/");
						if( urlParts.length )
						{
							while( urlParts[0] == ".." )
								urlParts.shift();
							this.init_from = urlParts.join("/");
						}
						*/
						Logger.log( " => " + this.id + " init_from: " + this.init_from );
						break;
					
					case ASCollada.DAE_EXTRA_ELEMENT:
						break;
						
					default:
						break;
				}
			}
		}
	}
}
