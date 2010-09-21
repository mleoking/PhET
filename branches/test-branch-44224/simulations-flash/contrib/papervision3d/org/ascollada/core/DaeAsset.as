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
	public class DaeAsset extends DaeEntity
	{
		public var contributors:Array;
		
		public var created:String;
		
		public var keywords:String;
		
		public var modified:String;
		
		public var title:String;
		
		public var subject:String;
		
		public var revision:String;
		
		public var unit_meter:Number;
		
		public var unit_name:String;
		
		public var yUp:String;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeAsset( document : DaeDocument, node:XML = null ):void
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
			if( node.localName() != ASCollada.DAE_ASSET_ELEMENT )
				throw new Error( "expected a '" + ASCollada.DAE_ASSET_ELEMENT + "' element" );
				
			super.read( node );
			
			parseContributors( node );
						
			this.created = getNodeContent( getNode(node, ASCollada.DAE_CREATED_ASSET_PARAMETER) );
			this.keywords = getNodeContent( getNode(node, ASCollada.DAE_KEYWORDS_ASSET_PARAMETER) );
			this.modified = getNodeContent( getNode(node, ASCollada.DAE_MODIFIED_ASSET_PARAMETER) );
			this.revision = getNodeContent( getNode(node, ASCollada.DAE_REVISION_ASSET_PARAMETER) );
			this.subject = getNodeContent( getNode(node, ASCollada.DAE_SUBJECT_ASSET_PARAMETER) );
			this.title = getNodeContent(  getNode(node, ASCollada.DAE_TITLE_ASSET_PARAMETER) );

			var unitNode:XML = getNode( node, ASCollada.DAE_UNITS_ASSET_PARAMETER );
			this.unit_meter = unitNode ? getAttributeAsFloat(unitNode, ASCollada.DAE_METERS_ATTRIBUTE, 1.0) : 1.0;
			this.unit_name = unitNode ? getAttribute(unitNode, ASCollada.DAE_NAME_ATTRIBUTE) : "meter";
		
			// y-up
			var yUpNode:XML = getNode( node, ASCollada.DAE_UP );
			this.yUp = yUpNode ? yUpNode.toString() : ASCollada.DAE_Y_UP;			
		}
		
		/**
		 * 
		 * @param	indent
		 */
		override public function write( indent:String = "" ):String 
		{
			var xml:String = writeSimpleStartElement( ASCollada.DAE_ASSET_ELEMENT, indent );
			
			for( var i:int = 0; i < this.contributors.length; i++ )
			{
				var contributor:DaeContributor = this.contributors[i];
				xml += contributor.write(indent + "\t");
			}
			
			xml += writeSimpleEndElement( ASCollada.DAE_ASSET_ELEMENT, indent );
			
			return xml;
		}
		
		/**
		 * 
		 * @param	asset
		 * 
		 * @return
		 */
		private function parseContributors( asset:XML ):void
		{
			this.contributors = new Array();
			var contribs:XMLList = getNodeList( asset, ASCollada.DAE_CONTRIBUTOR_ASSET_ELEMENT);
			for each( var contributor:XML in contribs )
				this.contributors.push( new DaeContributor(this.document, contributor) );
				
			if( !this.contributors.length )
			{
				var c:DaeContributor = new DaeContributor(this.document);
				c.author = "Tim Knip";
				c.authoring_tool = "ASCollada";
				c.comment = "";
				c.source_data = "";
				this.contributors.push(c);
			}
		}
	}	
}
