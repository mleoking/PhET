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
	import flash.display.Bitmap;	
	import flash.display.LoaderInfo;	
	import flash.net.URLRequest;	
	import flash.events.IOErrorEvent;	
	import flash.events.Event;	
	import flash.display.Loader;	
	
	import org.ascollada.ASCollada;
	import org.ascollada.fx.DaeEffect;
	import org.ascollada.fx.DaeMaterial;
	import org.ascollada.namespaces.*;
	import org.ascollada.physics.DaePhysicsScene;
	import org.ascollada.utils.Logger;
	import org.ascollada.types.DaeAddressSyntax;	

	/**
	 * 
	 */
	public class DaeDocument extends DaeEntity
	{
		use namespace collada;
		
		public static const X_UP:uint = 0;
		public static const Y_UP:uint = 1;
		public static const Z_UP:uint = 2;
		
		public var COLLADA:XML;
	
		public var version:String;
		
		public var sources:Object;
		public var animation_clips:Object;
		public var animations:Object;
		public var animatables:Object;
		public var controllers:Object;
		public var effects:Object;
		public var images:Object;
		public var materials:Object;
		public var geometries:Object;
		public var physics_scenes:Object;
		public var visual_scenes:Object;
		public var nodes:Object;
		public var cameras:Object;
		
		public var vscene:DaeVisualScene;
		public var pscene:DaePhysicsScene;
		
		public var yUp:uint;
		
		public var materialSymbolToTarget:Object;
		public var materialTargetToSymbol:Object;
		
		public var numSources : int = 0;
		public var baseUrl : String;
		
		private var _waitingSources:Array;
		private var _queuedImages : Array;
		private var _fileSearchPaths : Array;
		
		/**
		 * 
		 */
		public function DaeDocument( object:Object, async:Boolean = false )
		{			
			this.COLLADA = object is XML ? object as XML : new XML( object );
			
			XML.ignoreWhitespace = true;
			
			_fileSearchPaths = new Array();
			_fileSearchPaths.push(".");
			
			super( this, this.COLLADA, async );
		}
		
		/**
		 * 
		 * @return
		 */
		private function buildMaterialTable():void
		{
			materialSymbolToTarget = new Object();
			materialTargetToSymbol = new Object();
						
			var nodes:XMLList = this.COLLADA..collada::instance_material; 
			
			for each( var child:XML in nodes )
			{
				var target:String = getAttribute(child, ASCollada.DAE_TARGET_ATTRIBUTE);
				var symbol:String = getAttribute(child, ASCollada.DAE_SYMBOL_ATTRIBUTE);
				
				materialSymbolToTarget[symbol] = target;
				materialTargetToSymbol[target] = symbol;
			}
		}
		
		/**
		 * 
		 * @param	id
		 * @return
		 */
		private function findDaeNodeById( node:DaeNode, id:String, useSID:Boolean = false  ):DaeNode
		{
			if( useSID )
			{
				if( node.sid == id )
					return node;
			}
			else
			{
				if( node.id == id )
					return node;
			}
			
			for( var i:int = 0; i < node.nodes.length; i++ )
			{
				var n:DaeNode = findDaeNodeById( node.nodes[i], id, useSID );
				if( n )
					return n;
			}
			
			return null;
		}
		
		/**
		 * 
		 */
		public function addFileSearchPath(path : String) : void
		{
			if(_fileSearchPaths.indexOf(path) == -1)
			{
				if(path.charAt(path.length-1) == "/")
				{
					path = path.substr(0, path.length-1);
				}
				_fileSearchPaths.unshift(path);		
			}
		}
		
		/**
		 * 
		 * @param	id
		 * @return
		 */
		public function getDaeNodeById( id:String, useSID:Boolean = false ):DaeNode
		{
			for each( var nod:DaeNode in this.nodes )
			{
				var nn:DaeNode = findDaeNodeById(nod, id, useSID);
				if( nn )
					return nn;
			}
			
			for( var i:int = 0; i < this.vscene.nodes.length; i++ )
			{				
				var node:DaeNode = this.vscene.nodes[i];
				
				var n:DaeNode = findDaeNodeById( node, id, useSID );
				
				if( n )
				{
					//Logger.log( "found '" + id + "' " + useSID + " ID:" + n.id + " Name:" + n.name + " SID:" + n.sid );
			
					return n;
				}
			}
			
			return null;
		}
		
		/**
		 * 
		 * @param	id
		 * @return
		 */
		public function getDaeNodeByIdOrSID( id:String ):DaeNode
		{
			var node:DaeNode = getDaeNodeById(id, false);
			if(!node)
				node = getDaeNodeById(id, true);
			return node;
		}
		
		/**
		 * 
		 */
		public function getDaeChannelsForID(id : String) : Array
		{
			var channels : Array = new Array();
			var animation : DaeAnimation;
			for each(animation in this.animations)
			{
				if(!animation.channels || !animation.channels.length)
				{
					continue;
				}
				
				for each(var channel:DaeChannel in animation.channels)
				{
					if(id == channel.syntax.targetID)
					{
						channels.push(channel);
					}
				}
			}
			return channels;
		}
		
		/**
		 * 
		 */
		private function findDaeInstanceGeometry( node:DaeNode, url:String ) : DaeInstanceGeometry
		{
			for each( var geometry:DaeInstanceGeometry in node.geometries )
			{
				if( geometry.url == url )
					return geometry;
			}	
			
			for each( var child:DaeNode in node.nodes )
			{
				var g:DaeInstanceGeometry = findDaeInstanceGeometry( child, url );
				if( g )
					return g;
			}
			
			return null;
		}
		
		/**
		 * 
		 */
		public function getDaeInstanceGeometry( url:String ) : DaeInstanceGeometry
		{
			for each(var node:DaeNode in this.vscene.nodes )
			{
				var geometry : DaeInstanceGeometry = findDaeInstanceGeometry( node, url );
				if( geometry )
					return geometry;
			}
			return null;
		} 
		
		/**
		 * 
		 */
		public function readNextSource():Boolean
		{
			if( _waitingSources.length )
			{
				var node : XML = _waitingSources.pop() as XML;
				var source : DaeSource = new DaeSource(this.document, node);
				
				this.sources[source.id] = source;
			}
			
		
			return (_waitingSources.length > 0);
		}
		
		/**
		 * 
		 */
		public function readNextImage() : Boolean
		{
			if(_loadingImage == null && _queuedImages.length)
			{
				_loadingImage = _queuedImages.pop() as DaeImage;	
				
				loadImage();
			}
			else
			{
				dispatchEvent(new Event(Event.COMPLETE));
			}
			return (_loadingImage == null && _queuedImages.length > 0);
		}
		
		private var _currentImagePath : int = -1;
		private var _loadingImage : DaeImage;
		
		private function loadImage() : void
		{
			var loader : Loader = new Loader();

			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onImageComplete);	
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onImageIOError);
			
			var path : String = _loadingImage.init_from;
			
			var imageUrl : String = _loadingImage.init_from;
			
			if(_currentImagePath < 0)
			{
				path = imageUrl;
				if(this.baseUrl)
				{
					path = buildImagePath(this.baseUrl, imageUrl);
				}
			} 
			else
			{
				path = _currentImagePath < _fileSearchPaths.length ? _fileSearchPaths[_currentImagePath] : "";
				
				if(imageUrl.indexOf("/") != -1)
				{
					imageUrl = imageUrl.split("/").pop() as String;	
				}
				
				path = path + "/" + imageUrl;
			}
			
			loader.load(new URLRequest(path));
		}

		private function onImageComplete(event : Event) : void
		{
			var loaderInfo : LoaderInfo = event.target as LoaderInfo;
			var bitmap : Bitmap = loaderInfo.content as Bitmap;
			
			if(bitmap)
			{
				_loadingImage.bitmapData = bitmap.bitmapData;
			}
			
			_currentImagePath = -1;
			_loadingImage = null;
			
			readNextImage();
		}
		
		private function onImageIOError(event : IOErrorEvent) : void
		{
			_currentImagePath++;
			if(_currentImagePath < _fileSearchPaths.length)
			{
				loadImage();
			}
			else
			{
				_currentImagePath = -1;
				_loadingImage = null;
				readNextImage();
			}
		}

		/**
		 * 
		 */
		override public function destroy() : void 
		{
			super.destroy();
			
			var element : DaeEntity;
			
			if(this.sources)
			{
				for each(element in this.sources)
				{
					element.destroy();
				}
				this.sources = null;
			}
			
			if(this.images)
			{
				for each(element in this.images)
				{
					element.destroy();
				}
				this.images = null;
			}
			this.COLLADA = null;
		}

		/**
		 * 
		 * @param	node
		 * @return
		 */
		override public function read( node:XML ):void
		{
			this.version = node.attribute(ASCollada.DAE_VERSION_ATTRIBUTE).toString();
			
			Logger.log( "version: " + this.version );
			
			// required!
			this.asset = new DaeAsset( this, getNode(this.COLLADA, ASCollada.DAE_ASSET_ELEMENT) );
			
			if( this.asset.contributors && this.asset.contributors[0].author ) Logger.log( "author: " + this.asset.contributors[0].author );
			Logger.log( "created: " + this.asset.created );
			Logger.log( "modified: " + this.asset.modified );
			Logger.log( "y-up: " + this.asset.yUp );
			Logger.log( "unit_meter: " + this.asset.unit_meter );
			Logger.log( "unit_name: " + this.asset.unit_name );
			
			if( this.asset.yUp == ASCollada.DAE_Y_UP )
				this.yUp = Y_UP;
			else
				this.yUp = Z_UP;
			
			buildMaterialTable();
			
			readLibImages();
			readSources();
			/*
			readLibAnimationClips();
			readLibCameras();
			readLibControllers();
			readLibAnimations();
			readLibImages();
			readLibMaterials();
			readLibEffects();
			readLibGeometries(this.async);
			readLibNodes();
			readLibPhysicsScenes();
			readLibVisualScenes();
			
			readScene();
			 
			 */
		}

		/**
		 * 
		 */
		public function readAfterSources() : void 
		{
			readLibAnimationClips();
			readLibAnimations();
			readLibCameras();
			readLibControllers();
			readLibMaterials();
			readLibEffects();
			readLibGeometries();
			readLibNodes();
			readLibPhysicsScenes();
			readLibVisualScenes();
			
			readScene();		
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibAnimations():void
		{
			this.animations = new Object();
			this.animatables = new Object();
			
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_ANIMATION_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_ANIMATION_ELEMENT );
				for each( var item:XML in list )
				{
					var animation : DaeAnimation = new DaeAnimation(this, item);
					
					readAnimation(animation);
				}
			}
		}

		/**
		 * 
		 */
		private function readAnimation(animation : DaeAnimation) : void 
		{
			var child : DaeAnimation;

			if(animation.channels && animation.channels.length) 
			{
				this.animations[ animation.id ] = animation;
			}
			
			for each(child in animation.animations) 
			{
				readAnimation(child);		
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibAnimationClips():void
		{
			this.animation_clips = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_ANIMATION_CLIP_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_ANIMCLIP_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaeAnimationClip = new DaeAnimationClip( this, item );
					this.animation_clips[ ent.id ] = ent;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibCameras():void
		{
			this.cameras = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_CAMERA_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_CAMERA_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaeCamera = new DaeCamera( this, item );
					this.cameras[ ent.id ] = ent;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibControllers():void
		{
			this.controllers = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_CONTROLLER_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_CONTROLLER_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaeController = new DaeController( this, item );
					this.controllers[ ent.id ] = ent;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibEffects():void
		{
			this.effects = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_EFFECT_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_EFFECT_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaeEffect = new DaeEffect( this, item );
					this.effects[ ent.id ] = ent;
				}
			}
		}
		
		/**
		 * 
		 * @param	async
		 * @return
		 */
		private function readLibGeometries():void
		{
			this.geometries = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_GEOMETRY_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_GEOMETRY_ELEMENT );
				for each( var item:XML in list )
				{
					var geometry:DaeGeometry = new DaeGeometry( this, item, false );
					
					this.geometries[ geometry.id ] = geometry;
					
					if(geometry.mesh)
					{
						
					}
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibImages():void
		{
			this.images = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_IMAGE_ELEMENT );
			
			_queuedImages = new Array();
			
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_IMAGE_ELEMENT );
				for each(var item:XML in list)
				{
					var image : DaeImage = new DaeImage(this, item);
					
					this.images[ image.id ] = image;
				
					_queuedImages.push(image);
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibMaterials():void
		{
			this.materials = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_MATERIAL_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_MATERIAL_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaeMaterial = new DaeMaterial( this, item );
					this.materials[ ent.id ] = ent;
				}
			}
		}

		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibNodes():void
		{
			this.nodes = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_NODE_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_NODE_ELEMENT );
				for each( var item:XML in list )
				{
					var node:DaeNode = new DaeNode( this, item );
					this.nodes[ node.id ] = node;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibPhysicsScenes():void
		{
			this.physics_scenes = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_PSCENE_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_PHYSICS_SCENE_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaePhysicsScene = new DaePhysicsScene( this, item );
					this.physics_scenes[ ent.id ] = ent;
				}
			}
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		private function readLibVisualScenes():void
		{
			this.visual_scenes = new Object();
			var library:XML = getNode( this.COLLADA, ASCollada.DAE_LIBRARY_VSCENE_ELEMENT );
			if( library )
			{
				var list:XMLList = getNodeList( library, ASCollada.DAE_VSCENE_ELEMENT );
				for each( var item:XML in list )
				{
					var ent:DaeVisualScene = new DaeVisualScene( this, item, yUp );
					this.visual_scenes[ ent.id ] = ent;
					this.vscene = ent;
				}
			}
		}
		
		/**
		 * 
		 * @return
		 */
		private function readScene():void
		{
			// try to find a valid scene...
			var sceneNode:XML = getNode( this.COLLADA, ASCollada.DAE_SCENE_ELEMENT );
			if( sceneNode )
			{
				var vsceneNode:XML = getNode( sceneNode, ASCollada.DAE_INSTANCE_VSCENE_ELEMENT );
				if( vsceneNode )
				{
					var vurl:String = getAttribute( vsceneNode, ASCollada.DAE_URL_ATTRIBUTE );
					if( this.visual_scenes[vurl] is DaeVisualScene )
					{
						Logger.log( "found visual scene: " + vurl );
						
						this.vscene = this.visual_scenes[ vurl ];
						
						Logger.log( " -> frameRate: " + this.vscene.frameRate );
						Logger.log( " -> startTime: " + this.vscene.startTime );
						Logger.log( " -> endTime: " + this.vscene.endTime );
					}
				}
				
				var psceneNode:XML = getNode( sceneNode, ASCollada.DAE_INSTANCE_PHYSICS_SCENE_ELEMENT );
				if( psceneNode )
				{
					var purl:String = getAttribute( psceneNode, ASCollada.DAE_URL_ATTRIBUTE );
					if( this.physics_scenes[purl] is DaePhysicsScene )
					{
						Logger.log( "found physics scene: " + purl );
						this.pscene = this.physics_scenes[ purl ];
					}
				}
			}
		}
		
		/**
		 * 
		 */
		private function readSources() : void
		{
			var list : XMLList = this.COLLADA..source.(hasOwnProperty("@id"));
			var element : XML;
			
			this.numSources = list.length();
			this.sources = new Object();
			
			_waitingSources = new Array();

			for each(element in list) {
				if(this.async) {
					_waitingSources.push(element);
				} else {
					var source : DaeSource = new DaeSource(this, element);
					this.sources[source.id] = source;
				}
			}
		}
		
		public function get waitingSources() : Array
		{
			return _waitingSources;
		}
		
		/**
		 *
		 * @return
		 */
		protected function buildImagePath( meshFolderPath:String, imgPath:String ):String
		{
			var baseParts:Array = meshFolderPath.split("/");
			var imgParts:Array = imgPath.split("/");
			
			while( baseParts[0] == "." )
				baseParts.shift();
				
			while( imgParts[0] == "." )
				imgParts.shift();
				
			while( imgParts[0] == ".." )
			{
				imgParts.shift();
				baseParts.pop();
			}
						
			var imgUrl:String = baseParts.length > 1 ? baseParts.join("/") : (baseParts.length?baseParts[0]:"");
						
			imgUrl = imgUrl != "" ? imgUrl + "/" + imgParts.join("/") : imgParts.join("/");
			
			return imgUrl;
		}
	}	
}
