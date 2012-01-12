package away3d.materials
{
	import away3d.core.utils.*;
	import away3d.materials.shaders.*;
	
	import flash.display.*;
	import flash.geom.ColorTransform;
	
	/**
	 * Bitmap material with depth shading.
	 */
	public class DepthBitmapMaterial extends CompositeMaterial
	{
		private var _bitmapMaterial:BitmapMaterial;
		private var _depthShader:DepthShader;
		
		private var _minZ:Number;
		private var _maxZ:Number;
		private var _minColor:Number;
		private var _maxColor:Number;
		
		/**
		 * Coefficient for the minimum Z of the depth map.
		 */
        public function get minZ():Number
        {
        	return _minZ;
        }
        
        public function set minZ(val:Number):void
        {
        	if (_minZ == val)
        		return;
        	
        	_minZ = val;
            _depthShader.minZ = val;
        }
				
		/**
		 * Coefficient for the maximum Z of the depth map.
		 */
        public function get maxZ():Number
        {
        	return _maxZ;
        }
        
        public function set maxZ(val:Number):void
        {
        	if (_maxZ == val)
        		return;
        	
        	_maxZ = val;
        	
            _depthShader.maxZ = val;
        }
		
		/**
		 * Coefficient for the color shading at minZ.
		 */
        public function get minColor():uint
        {
        	return _minColor;
        }
        
        public function set minColor(val:uint):void
        {
        	if (_minColor == val)
        		return;
        	
        	_minColor = val;
        	
        	_bitmapMaterial.color = _minColor;
        }
				
		/**
		 * Coefficient for the color shading at maxZ.
		 */
        public function get maxColor():uint
        {
        	return _maxColor;
        }
        
        public function set maxColor(val:uint):void
        {
        	if (_maxColor == val)
        		return;
        	
        	_maxColor = val;
        	
            _depthShader.color = val;
        }
        
        /**
        * Returns the bitmapData object being used as the material texture.
        */
		public function get bitmap():BitmapData
		{
			return _bitmapMaterial.bitmap;
		}
		
		/**
		 * Creates a new <code>DepthBitmapMaterial</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	enviroMap			The bitmapData object to be used as the material's normal map.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function DepthBitmapMaterial(bitmap:BitmapData, init:Object = null)
		{
			//remove any reference to materials
			if (init && init["materials"])
				delete init["materials"];
			
			super(init);
			
			_minZ = ini.getNumber("minZ", 500);
			_maxZ = ini.getNumber("maxZ", 2000);
			_minColor = ini.getNumber("minColor", 0xFFFFFF);
			_maxColor = ini.getNumber("maxColor", 0x000000);
			
			//create new materials
			_bitmapMaterial = new BitmapMaterial(bitmap, ini);
			_bitmapMaterial.color = _minColor;
			_depthShader = new DepthShader({minZ:_minZ, maxZ:_maxZ, color:_maxColor});
			
			//add to materials array
			addMaterial(_bitmapMaterial);
			addMaterial(_depthShader);
			
		}
		
	}
}