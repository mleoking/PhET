package away3d.loaders
{
    import away3d.arcane;
    import away3d.containers.*;
    import away3d.core.base.*;
    import away3d.core.utils.*;
    import away3d.events.*;
    import away3d.loaders.data.*;
    import away3d.loaders.utils.*;
    import away3d.materials.*;
    
    import flash.display.*;
    import flash.events.*;
    import flash.utils.*;
    
    import nochump.util.zip.*;

	use namespace arcane;
	
    /**
    * File loader for the KMZ 4 file format (exported from Google Sketchup).
    */
    public class Kmz extends AbstractParser
    {
    	/** @private */
        arcane override function prepareData(data:*):void
        {
        	kmz = Cast.bytearray(data);
        	
            kmzFile = new ZipFile(kmz);
			for(var i:int = 0; i < kmzFile.entries.length; ++i) {
				var entry:ZipEntry = kmzFile.entries[i];
				var data:ByteArray = kmzFile.getInput(entry);
				if(entry.name.indexOf(".dae")>-1 && entry.name.indexOf("models/")>-1) {
					collada = new XML(data.toString());
					container = Collada.parse(collada, ini);
					if (container is Loader3D) {
						(container as Loader3D).parser.container.materialLibrary.loadRequired = false;
						(container as Loader3D).addOnSuccess(onParseGeometry);
					} else {
						parseImages();
					}
				}
			}
        }
        /** @private */
        arcane override function parseNext():void
        {
        	notifySuccess();
        }
        
        private var kmz:ByteArray;
        private var collada:XML;
    	private var kmzFile:ZipFile;
        
        private function onParseGeometry(event:Loader3DEvent):void
        {
        	container = event.loader.handle;
        	parseImages();
        }
        
        private function parseImages():void
        {
        	materialLibrary = container.materialLibrary;
			materialLibrary.loadRequired = false;
			
			for(var i:int = 0; i < kmzFile.entries.length; ++i) {
				var entry:ZipEntry = kmzFile.entries[i];
				var data:ByteArray = kmzFile.getInput(entry);
				if((entry.name.indexOf(".jpg")>-1 || entry.name.indexOf(".png")>-1) && entry.name.indexOf("images/")>-1) {
					var _loader:Loader = new Loader();
					_loader.name = "../" + entry;
					_loader.contentLoaderInfo.addEventListener(Event.COMPLETE, loadBitmapCompleteHandler);
					_loader.loadBytes(data);
				}
			}
        }
        
        private function loadBitmapCompleteHandler(e:Event):void {
			var loader:Loader = Loader(e.target["loader"]);
			
			//pass material instance to correct materialData
			var _materialData:MaterialData;
			var _face:Face;
			for each (_materialData in materialLibrary) {
				if (_materialData.textureFileName == loader.name) {
					_materialData.textureBitmap = Bitmap(loader.content).bitmapData;
					_materialData.material = new BitmapMaterial(_materialData.textureBitmap);
					for each(_face in _materialData.elements)
						_face.material = _materialData.material as ITriangleMaterial;
				}
			}
		}
        
        /**
        * Reference container for all materials used in the kmz scene.
        */
    	public var materialLibrary:MaterialLibrary;
    	
    	/**
    	 * Container data object used for storing the parsed kmz data structure.
    	 */
        public var containerData:ContainerData;
		
		/**
		 * Creates a new <code>Kmz</code> object..
		 * This loader is only compatible with the kmz 4 googleearth format that is exported from Google Sketchup.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 * 
		 * @see away3d.loaders.Kmz#parse()
		 * @see away3d.loaders.Kmz#load()
		 */
        public function Kmz(init:Object = null)
        {
        	super(init);
        	
        	binary = true;
        }

		/**
		 * Creates a 3d container object from the raw binary data of a kmz file.
		 * 
		 * @param	data				The birnay zip data of a loaded file.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 * @param	loader	[optional]	Not intended for direct use.
		 * 
		 * @return						A 3d container object representation of the kmz file.
		 */
        public static function parse(data:*, init:Object = null):ObjectContainer3D
        {
            return Loader3D.parseGeometry(data, Kmz, init).handle as ObjectContainer3D;
        }
    	
    	/**
    	 * Loads and parses a kmz file into a 3d container object.
    	 * 
    	 * @param	url					The url location of the file to load.
    	 * @param	init	[optional]	An initialisation object for specifying default instance properties.
    	 * @return						A 3d loader object that can be used as a placeholder in a scene while the file is loading.
    	 */
        public static function load(url:String, init:Object = null):Loader3D
        {
            return Loader3D.loadGeometry(url, Kmz, init);
        }
    }
}
