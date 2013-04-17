package away3d.core.light
{
	import away3d.arcane;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.math.*;
	import away3d.events.*;
	import away3d.lights.*;
	
	import flash.display.*;
	import flash.filters.ColorMatrixFilter;
	import flash.geom.*;
	import flash.utils.Dictionary;

	use namespace arcane;
	
    /**
    * Directional light primitive.
    */
    public class DirectionalLight extends LightPrimitive
    {
    	private var _light:DirectionalLight3D;
        private var _normalMatrix:ColorMatrixFilter = new ColorMatrixFilter();
    	private var _matrix:Matrix = new Matrix();
    	private var _shape:Shape = new Shape();
    	private var transform:MatrixAway3D = new MatrixAway3D();
    	private var nx:Number;
    	private var ny:Number;
    	private var mod:Number;
        private var cameraTransform:MatrixAway3D;
        private var cameraDirection:Number3D = new Number3D();
        private var halfVector:Number3D = new Number3D();
        private var halfTransform:MatrixAway3D = new MatrixAway3D();
        private var _red:Number;
		private var _green:Number;
		private var _blue:Number;
        private var _szx:Number;
        private var _szy:Number;
        private var _szz:Number;
		
        private var direction:Number3D = new Number3D();
        
        /**
        * Transform dictionary for the diffuse lightmap used by shading materials.
        */
        public var diffuseTransform:Dictionary;
        
        /**
        * Transform dictionary for the specular lightmap used by shading materials.
        */
        public var specularTransform:Dictionary;
        
        /**
        * Color transform used in cached shading materials for combined ambient and diffuse color intensities.
        */
        public var ambientColorTransform:ColorTransform;
        
        /**
        * Color transform used in cached shading materials for ambient intensities.
        */
        public var diffuseColorTransform:ColorTransform;
        
        /**
        * Colormatrix transform used in DOT3 materials for resolving normal values in the normal map.
        */
        public var normalMatrixDiffuseTransform:Dictionary = new Dictionary(true);
        
        /**
        * Colormatrix transform used in DOT3 materials for resolving normal values in the normal map.
        */
        public var normalMatrixSpecularTransform:Dictionary = new Dictionary(true);
        
    	/**
    	 * A reference to the <code>DirectionalLight3D</code> object used by the light primitive.
    	 */
        public function get light():DirectionalLight3D
        {
        	return _light;
        }
        public function set light(val:DirectionalLight3D):void
        {
        	_light = val;
        	val.addOnSceneTransformChange(updateDirection);
        }
        
        /**
        * Updates the bitmapData object used as the lightmap for ambient light shading.
        * 
        * @param	ambient		The coefficient for ambient light intensity.
        */
		public function updateAmbientBitmap():void
        {
        	ambientBitmap = new BitmapData(256, 256, false, int(ambient*red*0xFF << 16) | int(ambient*green*0xFF << 8) | int(ambient*blue*0xFF));
        	ambientBitmap.lock();
        	
        	//update colortransform
        	ambientColorTransform = new ColorTransform(1, 1, 1, 1, ambient*red*0xFF, ambient*green*0xFF, ambient*blue*0xFF, 0);
        }
        
        /**
        * Updates the bitmapData object used as the lightmap for diffuse light shading.
        * 
        * @param	diffuse		The coefficient for diffuse light intensity.
        */
        public function updateDiffuseBitmap():void
        {
    		diffuseBitmap = new BitmapData(256, 256, false, 0x000000);
    		diffuseBitmap.lock();
    		_matrix.createGradientBox(256, 256, 0, 0, 0);
    		var colArray:Array = [];
    		var alphaArray:Array = [];
    		var pointArray:Array = [];
    		var i:int = 15;
    		while (i--) {
    			var r:Number = (i*diffuse/14);
    			if (r > 1) r = 1;
    			var g:Number = (i*diffuse/14);
    			if (g > 1) g = 1;
    			var b:Number = (i*diffuse/14);
    			if (b > 1) b = 1;
    			colArray.push((r*red*0xFF << 16) | (g*green*0xFF << 8) | b*blue*0xFF);
    			alphaArray.push(1);
    			pointArray.push(int(30+225*2*Math.acos(i/14)/Math.PI));
    		}
    		_shape.graphics.clear();
    		_shape.graphics.beginGradientFill(GradientType.LINEAR, colArray, alphaArray, pointArray, _matrix);
    		_shape.graphics.drawRect(0, 0, 256, 256);
    		diffuseBitmap.draw(_shape);
        	
        	//update colortransform
        	diffuseColorTransform = new ColorTransform(diffuse*red, diffuse*green, diffuse*blue, 1, 0, 0, 0, 0);
        }
        
        /**
        * Updates the bitmapData object used as the lightmap for the combined ambient and diffue light shading.
        * 
        * @param	ambient		The coefficient for ambient light intensity.
        * @param	diffuse		The coefficient for diffuse light intensity.
        */
        public function updateAmbientDiffuseBitmap():void
        {
    		ambientDiffuseBitmap = new BitmapData(256, 256, false, 0x000000);
    		ambientDiffuseBitmap.lock();
    		_matrix.createGradientBox(256, 256, 0, 0, 0);
    		var colArray:Array = [];
    		var alphaArray:Array = [];
    		var pointArray:Array = [];
    		var i:int = 15;
    		while (i--) {
    			var r:Number = (i*diffuse/14 + ambient);
    			if (r > 1) r = 1;
    			var g:Number = (i*diffuse/14 + ambient);
    			if (g > 1) g = 1;
    			var b:Number = (i*diffuse/14 + ambient);
    			if (b > 1) b = 1;
    			colArray.push((r*red*0xFF << 16) | (g*green*0xFF << 8) | b*blue*0xFF);
    			alphaArray.push(1);
    			pointArray.push(int(30+225*2*Math.acos(i/14)/Math.PI));
    		}
    		_shape.graphics.clear();
    		_shape.graphics.beginGradientFill(GradientType.LINEAR, colArray, alphaArray, pointArray, _matrix);
    		_shape.graphics.drawRect(0, 0, 256, 256);
    		ambientDiffuseBitmap.draw(_shape);
        }
        
        /**
        * Updates the bitmapData object used as the lightmap for specular light shading.
        * 
        * @param	specular		The coefficient for specular light intensity.
        */
        public function updateSpecularBitmap():void
        {
    		specularBitmap = new BitmapData(512, 512, false, 0x000000);
    		specularBitmap.lock();
    		_matrix.createGradientBox(512, 512, 0, 0, 0);
    		var colArray:Array = [];
    		var alphaArray:Array = [];
    		var pointArray:Array = [];
    		var i:int = 15;
    		while (i--) {
    			colArray.push((i*specular*red*0xFF/14 << 16) + (i*specular*green*0xFF/14 << 8) + i*specular*blue*0xFF/14);
    			alphaArray.push(1);
    			pointArray.push(int(30+225*2*Math.acos(Math.pow(i/14,1/20))/Math.PI));
    		}
    		_shape.graphics.clear();
    		_shape.graphics.beginGradientFill(GradientType.RADIAL, colArray, alphaArray, pointArray, _matrix);
    		_shape.graphics.drawCircle(255, 255, 255);
    		specularBitmap.draw(_shape);
        }
        
        /**
        * Clears the transform and matrix dictionaries used in the shading materials.
        */
        public function clearTransform():void
        {
        	diffuseTransform = new Dictionary(true);
        	specularTransform = new Dictionary(true);
        	normalMatrixDiffuseTransform = new Dictionary(true);
        	normalMatrixSpecularTransform = new Dictionary(true);
        }
		
    	/**
    	 * Updates the direction vector of the directional light.
    	 */
        public function updateDirection(e:Object3DEvent):void
        {
        	//update direction vector
        	direction.x = _light.x;
        	direction.y = _light.y;
        	direction.z = _light.z;
        	direction.normalize();
        	
        	nx = direction.x;
        	ny = direction.y;
        	mod = Math.sqrt(nx*nx + ny*ny);
        	transform.rotationMatrix(ny/mod, -nx/mod, 0, -Math.acos(-direction.z));
        	clearTransform();
        }
        
        /**
        * Updates the transform matrix for the diffuse lightmap.
        * 
        * @see diffuseTransform
        */
        public function setDiffuseTransform(source:Object3D):void
        {
        	if (!diffuseTransform[source])
        		diffuseTransform[source] = new MatrixAway3D();
        	
        	diffuseTransform[source].multiply3x3(transform, source.sceneTransform);
        	diffuseTransform[source].normalize(diffuseTransform[source]);
        }
        
        /**
        * Updates the transform matrix for the specular lightmap.
        * 
        * @see specularTransform
        */
        public function setSpecularTransform(source:Object3D, view:View3D):void
        {
			//find halfway matrix between camera and direction matricies
			cameraTransform = view.camera.transform;
			cameraDirection.x = -cameraTransform.sxz;
			cameraDirection.y = -cameraTransform.syz;
			cameraDirection.z = -cameraTransform.szz;
			halfVector.add(cameraDirection, direction);
			halfVector.normalize();
			nx = halfVector.x;
        	ny = halfVector.y;
        	mod = Math.sqrt(nx*nx + ny*ny);
        	halfTransform.rotationMatrix(-ny/mod, nx/mod, 0, Math.acos(-halfVector.z));
			
			if (!specularTransform[source][view])
				specularTransform[source][view] = new MatrixAway3D();
				
        	specularTransform[source][view].multiply3x3(halfTransform, source.sceneTransform);
        	specularTransform[source][view].normalize(specularTransform[source][view]);
        }
        
        /**
        * Updates the normal transform matrix.
        * 
        * @see normalMatrixTransform
        */
        public function setNormalMatrixDiffuseTransform(source:Object3D):void
        {
        	_red = red*2*diffuse;
			_green = green*2*diffuse;
			_blue = blue*2*diffuse;
			
        	_szx = diffuseTransform[source].szx;
			_szy = -diffuseTransform[source].szy;
			_szz = diffuseTransform[source].szz;
			
        	//multipication of [_szx, 0, 0, 0, 127 - _szx*127, 0, -_szy, 0, 0, 127 + _szy*127, 0, 0, _szz, 0, 127 - _szz*127, 0, 0, 0, 1, 0]*[_red, _red, _red, 0, -381*_red, _green, _green, _green, 0, -381*_green, _blue, _blue, _blue, 0, -381*_blue, 0, 0, 0, 1, 0]
        	_normalMatrix.matrix = [_szx*_red, _green*_szy, _blue*_szz, 0, -_red   *127*(_szx + _szy + _szz),
        						    _szx*_red, _green*_szy, _blue*_szz, 0, -_green *127*(_szx + _szy + _szz),
        						    _szx*_red, _green*_szy, _blue*_szz, 0, -_blue  *127*(_szx + _szy + _szz),
        						   0, 0, 0, 1, 0];
        	normalMatrixDiffuseTransform[source] = _normalMatrix.clone();
        }
        
        /**
        * Updates the normal transform matrix.
        * 
        * @see colorMatrixTransform
        */
        public function setNormalMatrixSpecularTransform(source:Object3D, view:View3D, specular:Number, shininess:Number):void
        {
        	if (!normalMatrixSpecularTransform[source])
				normalMatrixSpecularTransform[source] = new Dictionary(true);
			
        	_red = (red*2 + shininess)*specular;
			_green = (green*2 + shininess)*specular;
			_blue = (blue*2 + shininess)*specular;
			
        	_szx = specularTransform[source][view].szx;
			_szy = -specularTransform[source][view].szy;
			_szz = specularTransform[source][view].szz;
			
        	//multipication of [_szx, 0, 0, 0, 127 - _szx*127, 0, -_szy, 0, 0, 127 + _szy*127, 0, 0, _szz, 0, 127 - _szz*127, 0, 0, 0, 1, 0]*[_red, _red, _red, 0, -127*shininess-381*_red, _green, _green, _green, 0, -127*shininess-381*_green, _blue, _blue, _blue, 0, -127*shininess-381*_blue, 0, 0, 0, 1, 0];
        	_normalMatrix.matrix = [_szx*_red, _green*_szy, _blue*_szz, 0, -_red   *127*(_szx + _szy + _szz) -127*shininess*specular,
        						    _szx*_red, _green*_szy, _blue*_szz, 0, -_green *127*(_szx + _szy + _szz) -127*shininess*specular,
        						    _szx*_red, _green*_szy, _blue*_szz, 0, -_blue  *127*(_szx + _szy + _szz) -127*shininess*specular,
        						   0, 0, 0, 1, 0];
        	
        	normalMatrixSpecularTransform[source][view] = _normalMatrix.clone();
        }
    }
}

