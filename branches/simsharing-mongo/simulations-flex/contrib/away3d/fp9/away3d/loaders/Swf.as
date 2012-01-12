package away3d.loaders
{
	import away3d.arcane;
	import away3d.containers.ObjectContainer3D;
	import away3d.core.base.Face;
	import away3d.core.base.Mesh;
	import away3d.core.base.Object3D;
	import away3d.core.base.Vertex;
	import away3d.core.utils.Cast;
	import away3d.core.utils.Init;
	
	import flash.utils.ByteArray;
	
	import wumedia.vector.VectorShapes;
	
	use namespace arcane;
	
	/**
    * File loader for swfs with injected vector data.
    */
	public class Swf extends AbstractParser
	{
		/** @private */
    	arcane var ini:Init;
		/** @private */
        arcane override function prepareData(data:*):void
        {
        	swf = Cast.bytearray(data);
        	
			if(libraryClips.length > 0)
				getAllLibraryClips();
			else
				getAllClipsFromStage();
			
			if(perspectiveOffset == 0)
				return;
			
			//apply perspective offfset
			var faceCounter:uint;
			for each(var child:Object3D in ObjectContainer3D(container).children)
			{
				if(child is Mesh)
				{
					var mesh:Mesh = child as Mesh;
					for each(var face:Face in mesh.faces)
					{
						for each(var vertex:Vertex in face.vertices)
						{
							vertex.x *= 1 + perspectiveOffset*faceCounter/perspectiveFocus;
							vertex.y *= 1 + perspectiveOffset*faceCounter/perspectiveFocus;
							vertex.z += perspectiveOffset*faceCounter;
						}
						
						faceCounter++;
					}
				}
			}
        }
        
		private var swf:ByteArray;
		
		private function getAllLibraryClips():void
		{
			VectorShapes.extractFromLibrary(swf, libraryClips);
			
			for each(var id:String in libraryClips)
				generateMesh(id);
		}
		
		private function getAllClipsFromStage():void
		{
			VectorShapes.extractFromStage(swf, "shapes");
			generateMesh("shapes");
		}
		
		private function generateMesh(shapeId:String):void
		{
			var clipMesh:Mesh = new Mesh();
			clipMesh.bothsides = true;
			ObjectContainer3D(container).addChild(clipMesh);
			
			VectorShapes.draw(clipMesh.geometry.graphics, shapeId, scaling);
		}
       	
    	/**
    	 * A scaling factor for all geometry in the model. Defaults to 1.
    	 */
		public var scaling:Number;
        
    	/**
    	 * An array of library ids to extract from the swf.
    	 * If no library ids are defined, all library items are used.
    	 * If no library items exist, the content found on the stage is used.
    	 */
		public var libraryClips:Array;
        
    	/**
    	 * An offset used to separate individual faces in a clip to counteract sorting artifacts.
    	 */
		public var perspectiveOffset:Number;
        
    	/**
    	 * A perspective scaling value used in conjuction with <code>perspectiveOffset</code>.
    	 */
		public var perspectiveFocus:Number;
		
		/**
		 * Creates a new <code>Swf</code> object.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 * 
		 * @see away3d.loaders.Swf#parse()
		 * @see away3d.loaders.Swf#load()
		 */
		public function Swf(init:Object = null)
		{
			super(init);
			
			libraryClips = ini.getArray("libraryClips");
			scaling = ini.getNumber("scaling", 1);
			perspectiveOffset = ini.getNumber("perspectiveOffset", 0);
			perspectiveFocus = ini.getNumber("perspectiveFocus", 1000);
			
			container = new ObjectContainer3D();
			
			binary = true;
		}

		/**
		 * Creates a 3d mesh object from the raw binary data of an swf file.
		 * 
		 * @param	data				The binary data of a loaded file.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 * 
		 * @return						A 3d container object representation of the swf file.
		 */
		public static function parse(data:*, init:Object = null):ObjectContainer3D
        {
        	return Loader3D.parseGeometry(data, Swf, init).handle as ObjectContainer3D;
        }
		
    	/**
    	 * Loads and parses a swf file into a 3d container object.
    	 *
    	 * @param	url					The url location of the file to load.
    	 * @param	init	[optional]	An initialisation object for specifying default instance properties.
    	 * 
    	 * @return						A 3d loader object that can be used as a placeholder in a scene while the file is loading.
    	 */
        public static function load(url:String, init:Object = null):Loader3D
        {
			return Loader3D.loadGeometry(url, Swf, init);
        }
	}
}