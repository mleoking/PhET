package away3d.loaders.data
{
	import away3d.core.base.*;
	import away3d.materials.*;
	
	import flash.display.BitmapData;
	
	/**
	 * Data class for the material data of a face.
	 * 
	 * @see away3d.loaders.data.FaceData
	 */
	public class MaterialData
	{
		private var _material:IMaterial;
		
		/**
		 * String representing a texture material.
		 */
		public static const TEXTURE_MATERIAL:String = "textureMaterial";
		
		/**
		 * String representing a shaded material.
		 */
		public static const SHADING_MATERIAL:String = "shadingMaterial";
		
		/**
		 * String representing a color material.
		 */
		public static const COLOR_MATERIAL:String = "colorMaterial";
		
		/**
		 * String representing a wireframe material.
		 */
		public static const WIREFRAME_MATERIAL:String = "wireframeMaterial";
		
		/**
		 * The name of the material used as a unique reference.
		 */
		public var name:String;
		
		/**
		 * Optional ambient color of the material.
		 */
		public var ambientColor:uint;
		
		/**
		 * Optional diffuse color of the material.
		 */
		public var diffuseColor:uint;
		
		/**
		 * Optional specular color of the material.
		 */
		public var specularColor:uint;
		
		/**
		 * Optional shininess of the material.
		 */
		public var shininess:Number;
		
		/**
		 * Reference to the filename of the texture image.
		 */
		public var textureFileName:String;
		
		/**
		 * Reference to the bitmapData object of the texture image.
		 */
		public var textureBitmap:BitmapData;
		
		/**
		 * defines the material object of the resulting material.
		 */
		public function get material():IMaterial
        {
        	return _material;
        }
		
		public function set material(val:IMaterial):void
        {
        	if (_material == val)
                return;
            
            _material = val;
            
            if (_material is IUVMaterial)
            	textureBitmap = (_material as IUVMaterial).bitmap;
            
            var _element:Element;
            
            if(_material is ITriangleMaterial)
            	for each(_element in elements)
            		(_element as Face).material = _material as ITriangleMaterial;		
			else if(_material is ISegmentMaterial)
            	for each(_element in elements)
            		(_element as Segment).material = _material as ISegmentMaterial;
        }
        		
		/**
		 * String representing the material type.
		 */
		public var materialType:String = WIREFRAME_MATERIAL;
		
		/**
		 * Array of indexes representing the elements that use the material.
		 */
		public var elements:Array = [];
		
		public function clone(targetObj:Object3D):MaterialData
		{
			var cloneMatData:MaterialData = targetObj.materialLibrary.addMaterial(name);
			
    		cloneMatData.materialType = materialType;
    		cloneMatData.ambientColor = ambientColor;
    		cloneMatData.diffuseColor = diffuseColor;
    		cloneMatData.shininess = shininess;
    		cloneMatData.specularColor = specularColor;
    		cloneMatData.textureBitmap = textureBitmap;
    		cloneMatData.textureFileName = textureFileName;
    		cloneMatData.material = material;
    		
    		for each(var element:Element in elements)
    		{
    			var parentGeometry:Geometry = element.parent;
    			var correspondingElement:Element = parentGeometry.cloneElementDictionary[element];
    			cloneMatData.elements.push(correspondingElement);
    		}
    		
    		return cloneMatData;
		}
	}
}