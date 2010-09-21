package org.papervision3d.materials
	{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.Loader;
	import flash.events.*;
	import flash.geom.Matrix;
	import flash.net.URLRequest;
	import flash.system.LoaderContext;
	import flash.utils.Dictionary;
	import flash.utils.Timer;
	
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.command.RenderTriangle;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	import org.papervision3d.events.FileLoadEvent;

	/**
	* The BitmapFileMaterial class creates a texture by loading a bitmap from an external file.
	*
	* Materials collect data about how objects appear when rendered.
	*/
	public class BitmapFileMaterial extends BitmapMaterial implements ITriangleDrawer
	{
		// ___________________________________________________________________ PUBLIC
		
		/**
		* The URL that has been requested.
		*/
		public var url :String = "";

		/**
		* Whether or not the texture has been loaded.
		*/
		public var loaded :Boolean;

		/**
		* Function to call when the last image has loaded.
		*/
		static public var callback :Function;

		/**
		* The color to use in materials before loading has finished.
		*/
		static public var LOADING_COLOR :int = MaterialObject3D.DEFAULT_COLOR;
		
		/**
		 * The color to use for the lines when there is an error.
		 */
		static public var ERROR_COLOR:int = MaterialObject3D.DEBUG_COLOR;
		
		/**
		 * A temporary bitmap to use if the file hasn't loaded yet. 
		 */
		static public var loadingBitmap : BitmapData = new BitmapData(1,1,false,0x000000); 
		
		
		/**
		* A texture object.
		*/		
		override public function get texture():Object
		{
			return this._texture;
		}
		
		/**
		 * Sets to check for the policy file or not.
		 */
		 
		 public var checkPolicyFile:Boolean = false;
		/**
		* @private
		*/
		override public function set texture( asset:Object ):void
		{
			if( asset is String == false )
			{
				PaperLogger.error("BitmapFileMaterial.texture requires a String for the texture");
				return;
			}
			
			bitmap   = createBitmapFromURL( String(asset) );
			_texture = asset;
		}
		
		/**
		 * Internal
		 * 
		 * Used to define if the loading had failed.
		 */
		protected var errorLoading:Boolean = false;

		// ___________________________________________________________________ NEW

		/**
		* The BitmapFileMaterial class creates a texture by loading a bitmap from an external file.
		*
		* @param	url					The URL of the requested bitmap file.
		*/
		public function BitmapFileMaterial( url :String="", precise:Boolean=false )
		{
			super(null, precise);
			
			// save URL reference
			this.url = url;

			// set the loaded flag
			this.loaded = false;

			// Loading color
			this.fillAlpha = 1;
			this.fillColor = LOADING_COLOR;
			
			// start the loading by setting the texture
			if( url.length > 0 ) texture = url;
		}

		// ___________________________________________________________________ CREATE BITMAP

		/**
		* [internal-use]
		*
		* @param	asset
		* @return
		*/
		protected function createBitmapFromURL( asset:String ):BitmapData
		{
			// Empy string?
			if( asset == "" )
			{
				return null;
			}
			// Already loaded?
			else 
			{
				
				var bmp:BitmapData = getBitmapForFilename( asset );
				if(bmp)
				{
					bitmap = super.createBitmap( bmp );
					
					// this fixes the problem where the event is not getting 
					// picked up because we usually add event listeners to the 
					// BitmapFileMaterial after we've instantiated it!
					setupAsyncLoadCompleteCallback();
					//this.loadComplete();

					return bmp;
				}
				else
				{
					queueBitmap( asset );
				}
			}
			return loadingBitmap;
		}

		// ___________________________________________________________________ QUEUE BITMAP

		private function queueBitmap( file:String ):void
		{
			// New filename?
			if( ! _subscribedMaterials[ file ] )
			{
				// Queue file
				_waitingBitmaps.push( file );

				// Init subscription
				_subscribedMaterials[ file ] = new Array();
			}

			// Subscribe material
			_subscribedMaterials[ file ].push( this );

			// Launch loading if needed
			if( _loadingIdle )
				loadNextBitmap();
		}

		// ___________________________________________________________________ LOAD NEXT BITMAP

		protected function loadNextBitmap():void
		{
			// Retrieve next filename in queue
			var file:String = _waitingBitmaps[0];

			var request:URLRequest = new URLRequest( file );
			bitmapLoader = new Loader();
			
			bitmapLoader.contentLoaderInfo.addEventListener( ProgressEvent.PROGRESS, loadBitmapProgressHandler );
			bitmapLoader.contentLoaderInfo.addEventListener( Event.COMPLETE, loadBitmapCompleteHandler );
			bitmapLoader.contentLoaderInfo.addEventListener( IOErrorEvent.IO_ERROR, loadBitmapErrorHandler );
			
			try
			{
				// Load bitmap
				var loaderContext:LoaderContext=new LoaderContext();
				loaderContext.checkPolicyFile=checkPolicyFile;

				bitmapLoader.load( request, loaderContext);

				// Save original url
				_loaderUrls[ bitmapLoader ] = file;

				// Busy loading
				_loadingIdle = false;

				PaperLogger.info( "BitmapFileMaterial: Loading bitmap from " + file );
			}
			catch( error:Error )
			{
				// Remove from queue
				_waitingBitmaps.shift();

				// Loading finished
				_loadingIdle = true;

				PaperLogger.info( "[ERROR] BitmapFileMaterial: Unable to load file " + error.message );
			}
		}
		
		// ___________________________________________________________________ LOAD BITMAP ERROR HANDLER

		protected function loadBitmapErrorHandler( e:IOErrorEvent ):void
		{
			
			var failedAsset:String = String(_waitingBitmaps.shift());
			// force the IOErrorEvent to trigger on any reload.
			// ie: no reload on retry if we don't clear these 2 statics below.
			//_loadedBitmaps[failedAsset] = null;
			_subscribedMaterials[failedAsset] = null;
			
			this.errorLoading = true;
			this.lineColor = ERROR_COLOR;
			this.lineAlpha = 1;
			this.lineThickness = 1;
			PaperLogger.error( "BitmapFileMaterial: Unable to load file " + failedAsset );
			
			removeLoaderListeners(); 
			
			// Queue finished?
			if( _waitingBitmaps.length > 0 )
			{
				// Continue loading
				loadNextBitmap();
			}
			else
			{
				// Loading finished
				_loadingIdle = true;
				
				if( Boolean( callback ) ) callback();
			}
						
			var event:FileLoadEvent = new FileLoadEvent(FileLoadEvent.LOAD_ERROR, failedAsset, -1, -1, e.text);
			
			dispatchEvent(event);
		}
		
		// ___________________________________________________________________ LOAD BITMAP PROGRESS HANDLER

		protected function loadBitmapProgressHandler( e:ProgressEvent ):void
		{
			var progressEvent:FileLoadEvent = new FileLoadEvent( FileLoadEvent.LOAD_PROGRESS, url, e.bytesLoaded, e.bytesTotal);
			dispatchEvent( progressEvent );
		}

		// ___________________________________________________________________ LOAD BITMAP COMPLETE HANDLER

		protected function loadBitmapCompleteHandler( e:Event ):void
		{
			
			var loadedBitmap:Bitmap = Bitmap( bitmapLoader.content );
			
			removeLoaderListeners(); 
			
			// Retrieve original url
			var url:String = _loaderUrls[ bitmapLoader ];

			// Retrieve loaded bitmapdata
			var bmp:BitmapData = super.createBitmap( loadedBitmap.bitmapData );
				
			// Update subscribed materials
			for each( var material:BitmapFileMaterial in _subscribedMaterials[ url ] )
			{
				material.bitmap = bmp;
				material.maxU = this.maxU;
				material.maxV = this.maxV;
				material.resetMapping();
				material.loadComplete();
			}
			
			// clear the loader from the list of materials
			_subscribedMaterials[url] = null;
			
			// Include in library
			//_loadedBitmaps[ url ] = bmp;
			_bitmapMaterials[this] = true; 
			// Remove from queue
			_waitingBitmaps.shift();

			// Queue finished?
			if( _waitingBitmaps.length > 0 )
			{
				// Continue loading
				loadNextBitmap();
			}
			else
			{
				// Loading finished
				_loadingIdle = true;
				
				if( Boolean( callback ) ) callback();
			}
		}

	// ___________________________________________________________________ SET UP ASYNCHRONOUS LOAD COMPLETE CALLBACK

		protected function setupAsyncLoadCompleteCallback() : void
		{
			var timer : Timer = new Timer(1,1); 
			timer.addEventListener(TimerEvent.TIMER_COMPLETE, dispatchAsyncLoadCompleteEvent); 
			timer.start(); 
			
		}
		
		// ___________________________________________________________________ DISPATCH ASYNCHRONOUS LOAD COMPLETE CALLBACK

		protected function dispatchAsyncLoadCompleteEvent(e : TimerEvent) : void
		{
			loadComplete(); 
			
		}

		
		// ___________________________________________________________________ LOAD COMPLETE

		protected function loadComplete():void
		{
			
			this.fillAlpha = 0;
			this.fillColor = 0;
			this.loaded = true;
			
			// add the bitmap into the dictionary for this material. 
			//_bitmapsByMaterial[this] = bitmap; 
			
			
			// Dispatch event
			var fileEvent:FileLoadEvent = new FileLoadEvent( FileLoadEvent.LOAD_COMPLETE, this.url );
			this.dispatchEvent( fileEvent );
		}
		
		protected function removeLoaderListeners() : void
		{
			bitmapLoader.contentLoaderInfo.removeEventListener( ProgressEvent.PROGRESS, loadBitmapProgressHandler );
			bitmapLoader.contentLoaderInfo.removeEventListener( Event.COMPLETE, loadBitmapCompleteHandler );
			bitmapLoader.contentLoaderInfo.removeEventListener( IOErrorEvent.IO_ERROR, loadBitmapErrorHandler );
			
		}
		
		
		/**
		 *  drawFace3D
		 */
		override public function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData, altBitmap:BitmapData=null, altUV:Matrix=null):void
		{
			if (bitmap == null || errorLoading)
			{
				if(errorLoading){
					graphics.lineStyle(lineThickness,lineColor,lineAlpha);
				}
				
				graphics.beginFill( fillColor, fillAlpha );
				graphics.moveTo( tri.v0.x, tri.v0.y );
				graphics.lineTo( tri.v1.x, tri.v1.y );
				graphics.lineTo( tri.v2.x, tri.v2.y );
				graphics.lineTo( tri.v0.x, tri.v0.y );
				graphics.endFill();
				
				if(errorLoading){
					graphics.lineStyle();
				}
				
				renderSessionData.renderStatistics.triangles++;
			}
			super.drawTriangle(tri, graphics, renderSessionData);
		}
		
		
		protected function getBitmapForFilename(filename:String) : BitmapData
		{
			for (var ref : * in _bitmapMaterials)
			{
				var bfm : BitmapFileMaterial = ref as BitmapFileMaterial; 
				if(bfm.url == filename) return bfm.bitmap;
				
			}
			return null; 
		}

		// ___________________________________________________________________ PRIVATE


		//bitmap Loader
		protected var bitmapLoader : Loader; 

		// Filenames in the queue
		static protected var _waitingBitmaps :Array = new Array();

		// URLs per loader
		static protected var _loaderUrls :Dictionary = new Dictionary(true);

		// bitmaps by material
		static protected var _bitmapMaterials : Dictionary = new Dictionary(true); 

		// Materials subscribed  to the loading queue
		static protected var _subscribedMaterials :Object = new Object();

		// Loading status
		static protected var _loadingIdle :Boolean = true;
		
		public function get subscribedMaterials() : Object
		{
			
			return _subscribedMaterials; 
		
		}
		public function get bitmapMaterials() : Dictionary
		{
			
			return _bitmapMaterials; 
		
		}		
		
		override public function destroy() : void 
		{
			if(bitmapLoader) bitmapLoader.unload(); 	
			super.destroy(); 
		}
	}
}