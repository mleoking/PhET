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
	import flash.events.EventDispatcher;	
	
	import org.ascollada.ASCollada;
	import org.ascollada.namespaces.*;
	import org.ascollada.utils.StringUtil;	

	/**
	 * 
	 */
	public class DaeEntity extends EventDispatcher {

		/** */
		public var id:String;
		
		/** */
		public var name:String;
		
		/** */
		public var sid:String;
		
		/** */
		public var asset:DaeAsset;
		
		/** */
		public var extras:Object;
		
		/** */
		public var async:Boolean;
		
		/** */
		public var document : DaeDocument;

		/**
		 * 
		 * @param	node
		 */
		public function DaeEntity( document : DaeDocument, node:XML = null, async:Boolean = false ) {
			super();
			this.document = document;
			this.async = async;
			if( node )
				read( node );
		}
		
		/**
		 * 
		 */
		public function destroy() : void {
			this.document = null;	
		}
		
		/**
		 * 
		 * @param	node
		 * @param	name
		 * @return
		 */
		public function getAttributeAsFloat( node:XML, name:String, defaultValue:Number = 0 ):Number {
			var attr:String = getAttribute(node, name);
			return (isNaN(parseFloat(attr)) ? defaultValue : parseFloat(attr));
		}
		
		/**
		 * 
		 * @param	node
		 * @param	name
		 * @return
		 */
		public function getAttributeAsInt( node:XML, name:String, defaultValue:int = 0 ):int {
			var attr:String = getAttribute(node, name);
			return (isNaN(parseInt(attr, 10)) ? defaultValue : parseInt(attr, 10));
		}
		
		/**
		 * 
		 * @param	node
		 * @param	name
		 * @param	stripPound
		 * @return
		 */
		public function getAttribute( node:XML, name:String, stripPound:Boolean = true ):String {
			var attr:XMLList = node.attribute(name);
			var ret:String = attr.length() ? attr.toString() : "";
			if( stripPound && ret.indexOf("#") == 0 )
				ret = ret.split("#")[1];
			return ret;
		}

		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function getBools( node:XML ):Array {
			var arr:Array = getStrings( node );
			for( var i:int = 0; i < arr.length; i++ )
				arr[i] = (arr[i] == "true" ? true : false);
			return arr;
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function getFloats( node:XML ):Array {
			var arr:Array = getStrings( node );
			for( var i:int = 0; i < arr.length; i++ )
			{
				var s : String = arr[i];
				s = s.replace(/,/, ".");
				arr[i] = parseFloat(s);
			}
			return arr;
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function getInts( node:XML ):Array {
			var arr:Array = getStrings( node );
			for( var i:int = 0; i < arr.length; i++ )
				arr[i] = parseInt( StringUtil.trim(arr[i]), 10 );
			return arr;
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function getStrings( node:XML ):Array {
			return StringUtil.trim(node.text().toString()).split(/\s+/);
		}
		
		/**
		 * 
		 * @param	parent
		 * @param	nodeName
		 * @return
		 */
		public function getNode( parent:XML, nodeName:String ):XML {
			return parent.collada::[nodeName][0];
		}
				
		/**
		 * 
		 * @param	parent
		 * @param	nodeName
		 * @return
		 */
		public function getNodeById( parent:XML, nodeName:String, id:String ):XML {
			return parent..collada::[nodeName].(@[ASCollada.DAE_ID_ATTRIBUTE] == id)[0];
		}

		/**
		 * 
		 * @param	parent
		 * @return
		 */
		public function getNodeContent( parent:XML ):String {
			return (parent ? parent.text().toString() : "");
		}
		
		/**
		 * 
		 * @param	parent
		 * @param	nodeName
		 * @return
		 */
		public function getNodeList( parent:XML, nodeName:String ):XMLList {
			return parent.collada::[nodeName];
		}
		
		/**
		 * 
		 * @return
		 */
		public function read( node:XML ):void {
			this.extras = new Object();
			this.id = getAttribute(node, ASCollada.DAE_ID_ATTRIBUTE);
			this.name = getAttribute(node, ASCollada.DAE_NAME_ATTRIBUTE);
			this.sid = getAttribute(node, ASCollada.DAE_SID_ATTRIBUTE);
		}
		
		public function write( indent:String = "" ):String {
			return indent;
		}
		
		public function writeSimpleEndElement( nodeName:String, indent:String = "" ):String {
			return indent + '</' + nodeName + '>\n';
		}
		
		public function writeSimpleStartElement( nodeName:String, indent:String = "" ):String {
			return indent + '<' + nodeName + '>\n';
		}
	}	
}
