package away3d.materials
{
	import away3d.core.utils.*;
	import away3d.materials.shaders.*;
	
	import flash.display.*;
	
	/**
	 * Animated movie material with phong shading.
	 */
	public class PhongMovieMaterial extends CompositeMaterial
	{
		private var _shininess:Number;
		private var _specular:Number;
		private var _movieMaterial:MovieMaterial;
		private var _phongShader:CompositeMaterial;
		private var _ambientShader:AmbientShader;
		private var _diffusePhongShader:DiffusePhongShader;
		private var _specularPhongShader:SpecularPhongShader;
		
		/**
		 * The exponential dropoff value used for specular highlights.
		 */
		public function get shininess():Number
		{
			return _shininess;
		}
		
		public function set shininess(val:Number):void
		{
			_shininess = val;
			_specularPhongShader.shininess = val;
		}
		
		/**
		 * Coefficient for specular light level.
		 */
		public function get specular():Number
		{
			return _specular;
		}
		
		public function set specular(val:Number):void
		{
			if (_specular == val)
				return;
			
			_specular = val;
			_specularPhongShader.specular = val;
			
			if (_specular && materials.length < 3)
        		addMaterial(_specularPhongShader);
   			else if (materials.length > 2)
            	removeMaterial(_specularPhongShader);
		}
		
		/**
		 * Creates a new <code>PhongMovieMaterial</code> object.
		 * 
		 * @param	movie				The movieclip to be used as the material's texture.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function PhongMovieMaterial(movie:Sprite, init:Object=null)
		{
			if (init && init["materials"])
				delete init["materials"];
			
			super(init);
			
			_shininess = ini.getNumber("shininess", 20);
			_specular = ini.getNumber("specular", 0.7, {min:0, max:1});
			
			//create new materials
			_movieMaterial = new MovieMaterial(movie, ini);
			_phongShader = new CompositeMaterial({blendMode:BlendMode.MULTIPLY});
			_phongShader.addMaterial(_ambientShader = new AmbientShader({blendMode:BlendMode.ADD}));
			_phongShader.addMaterial(_diffusePhongShader = new DiffusePhongShader({blendMode:BlendMode.ADD}));
			_specularPhongShader = new SpecularPhongShader({shininess:_shininess, specular:_specular, blendMode:BlendMode.ADD});
			
			//add to materials array
			addMaterial(_movieMaterial);
			addMaterial(_phongShader);
			
			if (_specular)
				addMaterial(_specularPhongShader);
		}
	}
}