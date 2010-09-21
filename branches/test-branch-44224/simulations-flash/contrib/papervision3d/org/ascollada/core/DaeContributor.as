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

	/**
	 * 
	 */
	public class DaeContributor extends DaeEntity
	{
		public var author:String;
		
		public var authoring_tool:String;
		
		public var comment:String;
		
		public var source_data:String;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeContributor( document : DaeDocument, node:XML = null ):void
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
	
			var authorNode:XML = getNode( node, ASCollada.DAE_AUTHOR_ASSET_PARAMETER );
			var authToolNode:XML = getNode( node, ASCollada.DAE_AUTHORINGTOOL_ASSET_PARAMETER );
			var commentNode:XML = getNode( node, ASCollada.DAE_COMMENTS_ASSET_PARAMETER );
			var sourceDataNode:XML = getNode( node, ASCollada.DAE_SOURCEDATA_ASSET_PARAMETER );
			
			this.author = authorNode ? authorNode.toString() : "";
			this.authoring_tool = authToolNode ? authToolNode.toString() : "";
			this.comment = commentNode ? commentNode.toString() : "";
			this.source_data = sourceDataNode ? sourceDataNode.toString() : "";
		}
		
		/**
		 * 
		 * @param	indent
		 */
		override public function write( indent:String = "" ):String 
		{
			var xml:String = writeSimpleStartElement( ASCollada.DAE_CONTRIBUTOR_ASSET_ELEMENT, indent );
			
			xml += writeSimpleEndElement( ASCollada.DAE_CONTRIBUTOR_ASSET_ELEMENT, indent );
			
			return xml;
		}
	}	
}
