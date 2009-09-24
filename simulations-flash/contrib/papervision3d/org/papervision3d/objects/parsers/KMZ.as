package org.papervision3d.objects.parsers {
	import nochump.util.zip.*;
	
	import org.ascollada.namespaces.*;
	import org.papervision3d.core.geom.*;
	import org.papervision3d.events.FileLoadEvent;
	import org.papervision3d.materials.*;
	import org.papervision3d.materials.utils.*;
	
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.events.*;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.utils.*;	

	/**
	 * @author Tim Knip
	 */
	public class KMZ extends TriangleMesh3D {
		
		/** The DAE */
		public var dae : DAE;
		
		/**
		 * Constructor.
		 */
		public function KMZ( name : String = null ) : void {
			super(new WireframeMaterial(), [], [], name);
		}
		
		/**
		 * Loads a KMZ.
		 *
		 * @param	asset	URL or ByteArray.
		 */
		public function load( asset : *, materials : MaterialsList = null ) : void {
			
			this.materials = materials || new MaterialsList();
			
			if(asset is String) {
				var loader : URLLoader = new URLLoader();
				loader.dataFormat = URLLoaderDataFormat.BINARY;
				loader.addEventListener(Event.COMPLETE, onLoadComplete);
				loader.addEventListener(ProgressEvent.PROGRESS, progressHandler);
	            loader.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
	            loader.addEventListener(HTTPStatusEvent.HTTP_STATUS, httpStatusHandler);
	            loader.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
				loader.load(new URLRequest(String(asset)));
			} else if(asset is ByteArray) {
				parse(asset as ByteArray);
			} else {
				throw new Error("KMZ#load : don't know how to load asset: " + asset);
			}
		}
		
		private function progressHandler( event : ProgressEvent ) : void {
			dispatchEvent(event);
		}
		private function securityErrorHandler( event : SecurityErrorEvent ) : void {
			dispatchEvent(event);
		}
		private function httpStatusHandler( event : HTTPStatusEvent ) : void {
			dispatchEvent(event);
		}
		private function ioErrorHandler( event : IOErrorEvent ) : void {
			dispatchEvent(event);
		}
		
		/**
		 * Gets the COLLADA from the zip.
		 *
		 * @param	zipFile
		 */
		private function getColladaFromZip( zipFile : ZipFile ) : ByteArray  {
			for(var i:int = 0; i < zipFile.entries.length; i++) {
			    var entry:ZipEntry = zipFile.entries[i];
			
			    // extract the entry's data from the zip
			    var data:ByteArray = zipFile.getInput(entry);
		
				if(entry.name.toLowerCase().indexOf(".dae") != -1) {
					return data;
				}
			}
			return null;
		}
		
		/**
		 * The KMZ was successfully loaded.
		 *
		 * @param 	event
		 */
		private function onLoadComplete( event : Event ) : void {
			var loader : URLLoader = event.target as URLLoader;
			parse(loader.data);
		}
		
		/**
		 * A texture was successfully loaded.
		 *
		 * @param 	event
		 */
		private function onTextureComplete( event : Event = null ) : void {
			if(event && event.target is Bitmap) {
				
				_loadedTextures++;
				
				var loader : Loader = event.target.parent as Loader;
				var xml : XML = new XML(_loadedDAE);
				var effects : XMLList = xml..collada::library_effects..collada::effect;

				for each(var effect : XML in effects) {
					try {
						var id  :String = effect.@id.toString();
						var images : XMLList = effect..collada::init_from;
				
						for each(var image:XML in images) {
							var init_from : String = String(image.text());
							var img:XML = xml..collada::image.(@id == init_from)..collada::init_from[0];
							var img_url : String = img.toString();
							var url : String = "#" + id;
							var mat:XML = xml..collada::material.(collada::instance_effect.@url == url)[0];
					
							if(img_url.indexOf(loader.name) != -1) {
								var material : BitmapMaterial = new BitmapMaterial(event.target.bitmapData);
								
								material.tiled = true;
								
								this.materials.addMaterial(material, String(mat.@id));
							}
						}
					} catch(e:Error) {
				
					}
				}
			}
			
			if(_loadedTextures == _totalTextures) {
				this.dae = new DAE();
				this.dae.addEventListener(FileLoadEvent.LOAD_COMPLETE, onColladaComplete);
				this.dae.load(_loadedDAE, this.materials);
			}
		}
		
		/**
		 * 
		 */
		private function onColladaComplete( event : Event ) : void {
			this.addChild(this.dae);
			
			dispatchEvent(new FileLoadEvent(FileLoadEvent.LOAD_COMPLETE));
		}
		
		/**
		 * Parse the KMZ data.
		 *
		 * @param	data
		 */
		private function parse( data : ByteArray ) : void {
			
			var zipFile:ZipFile = new ZipFile(data);
			
			_loadedDAE = getColladaFromZip(zipFile);
			_totalTextures = numTexturesInZip(zipFile);
			_loadedTextures = 0;
			
			if(_totalTextures == 0) {
				onTextureComplete(null);
				return;
			}
			
			for(var i:int = 0; i < zipFile.entries.length; i++) {
			    var entry:ZipEntry = zipFile.entries[i];
			
			    // extract the entry's data from the zip
			    var dataInput:ByteArray = zipFile.getInput(entry);
		
				if(entry.name.toLowerCase().indexOf(".png") != -1 || entry.name.toLowerCase().indexOf(".jpg") != -1) {
					var loader:Loader = new Loader();
					loader.name = entry.name;
					loader.addEventListener("added", onTextureComplete);
					loader.loadBytes(dataInput);
				} 
			}
		}
		
		/**
		 * Gets the number of textures inside a zip.
		 *
		 * @param	zipFile
		 *
		 * @return	The number of textures.
		 */
		private function numTexturesInZip( zipFile : ZipFile ) : uint {
			var count : uint = 0;
			for(var i:int = 0; i < zipFile.entries.length; i++) {
			    var entry:ZipEntry = zipFile.entries[i];
			
			    // extract the entry's data from the zip
			    var data:ByteArray = zipFile.getInput(entry);
		
				if(entry.name.toLowerCase().indexOf(".png") != -1 || entry.name.toLowerCase().indexOf(".jpg") != -1) {
					count++;
				}
			}
			return count;
		}
		
		/** */
		private var _loadedTextures : uint;
		
		/** */
		private var _totalTextures  : uint;
		
		/** */
		private var _loadedDAE		: ByteArray;
	}
}
