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

package org.ascollada.io 
{
	import org.ascollada.ASCollada;
	import org.ascollada.core.*;
	import org.papervision3d.Papervision3D;
	
	/**
	 * 
	 */
	public class DaeWriter 
	{
		/** asset */
		public var asset:DaeAsset;
		
		/**
		 * 
		 * @return
		 */
		public function DaeWriter( asset:DaeAsset = null ):void
		{
			this.asset = asset;
		}
		
		public function write():void
		{
			var xml:String = '<?xml version="1.0" encoding="utf-8"?>\n';
			
			xml += '<COLLADA xmlns="' + ASCollada.DAE_SCHEMA_LOCATION + '" version="' + ASCollada.DAE_SCHEMA_VERSION + '">\n';
			
			xml += writeAsset("\t");
			
			xml += '</COLLADA>\n';
		}
		
		/**
		 * 
		 * @param	indent
		 * @return
		 */
		private function writeAsset( indent:String = "" ):String
		{
			if( !asset )
			{
				asset = new DaeAsset(null);
				asset.contributors = new Array();
				
				var contributor:DaeContributor = new DaeContributor(null);
				contributor.author = "Tim Knip";
				contributor.authoring_tool = "Papervision3D version " + Papervision3D.VERSION;
				contributor.comment = "ExportTriangles=1;";
				
				asset.contributors = [contributor];
			}
			
			return asset.write(indent);
		}
		
		protected function writeSimpleEndElement( nodeName:String, indent:String = "" ):String
		{
			return indent + '</' + nodeName + '>\n';
		}
		
		protected function writeSimpleStartElement( nodeName:String, indent:String = "" ):String
		{
			return indent + '<' + nodeName + '>\n';
		}
	}
}
