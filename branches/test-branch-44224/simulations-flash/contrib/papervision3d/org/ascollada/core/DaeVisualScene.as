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
	import org.ascollada.core.DaeNode;
	import org.ascollada.utils.Logger;	

	/**
	 * 
	 */
	public class DaeVisualScene extends DaeEntity
	{	
		/** */
		public var nodes:Array;
		
		private var _yUp:uint;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeVisualScene( document:DaeDocument, node:XML = null, yUp:uint = 1 ):void
		{
			_yUp = yUp;
			
			super( document, node );
		}
		
		public function get endTime():Number
		{
			return this.extras["end_time"];
		}
		
		public function get frameRate():Number
		{
			return this.extras[ASCollada.DAEMAX_FRAMERATE_PARAMETER];
		}
		
		public function get startTime():Number
		{
			return this.extras["start_time"];
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void
		{		
			this.nodes = new Array();
			
			if( node.localName() != ASCollada.DAE_VSCENE_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_VSCENE_ELEMENT + "' element" );
				
			super.read( node );
			
			Logger.log( "reading visual scene: " + this.id );
			
			var nodeList:XMLList = getNodeList(node, ASCollada.DAE_NODE_ELEMENT);
			if( !nodeList.length() )
				throw new Error( "require at least 1 <node> element!" );
				
			for( var i:int = 0; i < nodeList.length(); i++ )
				this.nodes.push( new DaeNode(this.document, nodeList[i], _yUp) );
				
			var extraList:XMLList = getNodeList(node, ASCollada.DAE_EXTRA_ELEMENT);		
			for each( var extraNode:XML in extraList )
			{
				var techniqueList:XMLList = getNodeList(extraNode, ASCollada.DAE_TECHNIQUE_ELEMENT);
				
				for each( var technique:XML in techniqueList )
				{
					var profile:String = getAttribute(technique, ASCollada.DAE_PROFILE_ATTRIBUTE);
					
					switch( profile )
					{
						case ASCollada.DAEMAX_MAX_PROFILE:
							var frameRate:XML = getNode(technique, ASCollada.DAEMAX_FRAMERATE_PARAMETER);
							if( frameRate )
								this.extras[ASCollada.DAEMAX_FRAMERATE_PARAMETER] = parseFloat(getNodeContent(frameRate));
							break;
							
						case "FCOLLADA":
							var startTime:XML = getNode(technique, "start_time");
							if( startTime )
								this.extras["start_time"] = parseFloat(getNodeContent(startTime));
							var endTime:XML = getNode(technique, "end_time");
							if( endTime )
								this.extras["end_time"] = parseFloat(getNodeContent(endTime));
							break;
							
						default:
							break;
					}
				}
			}
		}
	}	
}
