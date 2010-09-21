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
 
package org.ascollada.fx {
	import org.ascollada.core.DaeDocument;	
	import org.ascollada.ASCollada;
	import org.ascollada.core.DaeEntity;
	import org.ascollada.types.DaeColorOrTexture;
	import org.ascollada.utils.Logger;
		
	/**
	 * 
	 */
	public class DaeEffect extends DaeEntity
	{
		/** */
		public var color:DaeConstant;
		
		/** */
		public var newparams:Object;
		
		/** **/
		public var texture_url:String;
		
		/** */
		public var double_sided:Boolean;
		
		/** */
		public var wireframe:Boolean;
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		public function DaeEffect( document:DaeDocument, node:XML = null ):void
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
			
			this.double_sided = false;
			this.wireframe = false;
			
			// The <profile_COMMON> elements encapsulate all the values and declarations for 
			// a platform-independent fixed-function shader. All platforms are required to 
			// support <profile_COMMON>. <profile_COMMON> effects are designed to be used as 
			// the reliable fallback when no other profile is recognized by the current
			// effects runtime.
			var profile:XML = getNode( node, ASCollada.DAE_PROFILE_COMMON_ELEMENT );
			
			if( !profile )
			{
				Logger.error( "Can't handle profiles other then profile_COMMON!" );
				return;	
			}
			
			// Declares a standard COLLADA image resource. 0 or more.
			var images:XMLList = getNodeList( profile, ASCollada.DAE_IMAGE_ELEMENT );
			
			// Creates a new parameter from a constrained set of
			// types recognizable by all platforms <float>,
			// <float2>, <float3>, <float4>, <surface>, and
			// <sampler2D>, with an additional semantic. 0 or more.
			var newparams:XMLList = getNodeList( profile, ASCollada.DAE_FXCMN_NEWPARAM_ELEMENT );
			
			// Declares the only technique for this effect. This node
			// contains <asset>, <image>, and <extra>, along
			// with one of <constant>, <lambert>, <phong>, or
			// <blinn>.
			var technique:XML = getNode( profile, ASCollada.DAE_TECHNIQUE_ELEMENT );
			
			// check for a SID
			var techSID:String = technique.attribute(ASCollada.DAE_SID_ATTRIBUTE);
			
			Logger.log( "reading effect: " + this.id );
			Logger.log( " => #images: " + images.length() );
			Logger.log( " => #newparams: " + newparams.length() );
			Logger.log( " => technique sid: " + techSID );
			
			this.newparams = new Object();
			for each( var paramNode:XML in newparams )
			{
				var p:DaeNewParam = new DaeNewParam(this.document, paramNode );
				this.newparams[ p.type ] = p;
			}
			
					
			var phong:XML = getNode( technique, ASCollada.DAE_FXSTD_PHONG_ELEMENT );
			var lambert:XML = getNode( technique, ASCollada.DAE_FXSTD_LAMBERT_ELEMENT );
			var blinn:XML = getNode( technique, ASCollada.DAE_FXSTD_BLINN_ELEMENT );
			var constant:XML = getNode( technique, ASCollada.DAE_FXSTD_CONSTANT_ELEMENT );
			if( phong )
			{
				Logger.log( " => shader: phong" );
				this.color = new DaePhong(this.document, phong);
			}
			else if( lambert )
			{
				Logger.log( " => shader: lambert" );
				this.color = new DaeLambert(this.document, lambert);
			}
			else if( blinn )
			{
				Logger.log( " => shader: blinn" );
				this.color = new DaeBlinn(this.document, blinn);
			}
			else if( constant )
			{
				Logger.log( " => shader: constant" );
				this.color = new DaeConstant(this.document, constant );
			}
			
			var surface:DaeNewParam = this.newparams[ASCollada.DAE_FXCMN_SURFACE_ELEMENT];
			var sampler2D:DaeNewParam = this.newparams[ASCollada.DAE_FXCMN_SAMPLER2D_ELEMENT];
			
			var ph:DaeLambert = this.color as DaeLambert;
			if( ph && ph.diffuse && ph.diffuse.type == DaeColorOrTexture.TYPE_TEXTURE && sampler2D && surface )
			{
				if( sampler2D.sid == ph.diffuse.texture.texture && sampler2D.sampler2D.source == surface.sid )
				{						
					this.texture_url = surface.surface.init_from;
				}
			}
			
			readExtra(node);
		}
		
		private function readExtra(node:XML):void
		{
			var extraList:XMLList = getNodeList(node, ASCollada.DAE_EXTRA_ELEMENT);
			
			for each(var extraNode:XML in extraList)
			{
				var techniqueNode:XML = getNode(extraNode, ASCollada.DAE_TECHNIQUE_ELEMENT);
				var profile:String = techniqueNode.@profile.toString();
				var tmp:XML;
				var text:String;
				
				switch(profile)
				{
					case "MAX3D":
						tmp = getNode(techniqueNode, "double_sided");
						if(tmp)
						{
							text = getNodeContent(tmp);
							this.double_sided = (text == "1" || text == "true");
						}
						tmp = getNode(techniqueNode, "wireframe");
						if(tmp)
						{
							text = getNodeContent(tmp);
							this.wireframe = (text == "1" || text == "true");
						}
						break;
					default:
						break;
				}
			}
		}
	}	
}
