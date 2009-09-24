package org.papervision3d.materials.utils
{
	import org.papervision3d.Papervision3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.lights.PointLight3D;
	import org.papervision3d.objects.DisplayObject3D;

	/**
	 * @Author Tim Knip / Ralph Hauwert
	 */
	public class LightMatrix
	{
		
		private static var lightMatrix:Matrix3D = Matrix3D.IDENTITY;
		private static var invMatrix:Matrix3D = Matrix3D.IDENTITY;
	
		private static var _targetPos:Number3D = new Number3D();
		private static var _lightPos:Number3D = new Number3D();
		private static var _lightDir:Number3D = new Number3D();
		private static var _lightUp:Number3D = new Number3D();
		private static var _lightSide:Number3D = new Number3D();
		
		protected static var UP:Number3D = new Number3D(0, 1, 0);
		
		/**
		 * Gets the 'lightmatrix' for a light and object.
		 * 
		 * @param	light
		 * @param	object
		 * @return
		 */
		public static function getLightMatrix(light:LightObject3D, object:DisplayObject3D, renderSessionData:RenderSessionData, objectLightMatrix:Matrix3D):Matrix3D
		{
			var lightMatrix:Matrix3D = objectLightMatrix ? objectLightMatrix : Matrix3D.IDENTITY;
			if(light == null){
				light = new PointLight3D();
				light.copyPosition(renderSessionData.camera);
			}
			_targetPos.reset();
			_lightPos.reset();
			_lightDir.reset();
			_lightUp.reset();
			_lightSide.reset();
			
			if(!object)
			{
				return lightMatrix;
			}
			// NOTE: we basically perform a lookAt.
			var ml:Matrix3D = light.transform;
			var mo:Matrix3D = object.world;
			
			// invert light position!
			_lightPos.x = -ml.n14;
			_lightPos.y = -ml.n24;
			_lightPos.z = -ml.n34;
							
			// object position
			_targetPos.x = -mo.n14;
			_targetPos.y = -mo.n24;
			_targetPos.z = -mo.n34;
			
			// direction vector from light to object
			_lightDir.x = _targetPos.x - _lightPos.x;
			_lightDir.y = _targetPos.y - _lightPos.y;
			_lightDir.z = _targetPos.z - _lightPos.z;

			// account for object's transformation
			invMatrix.calculateInverse(object.world);
			Matrix3D.multiplyVector3x3(invMatrix, _lightDir);
		   
			// normalize!
			_lightDir.normalize();
		   	
			// inlined:  Number3D.cross(UP, _lightDir);
			_lightSide.x = (_lightDir.y * UP.z) - (_lightDir.z * UP.y);
			_lightSide.y = (_lightDir.z * UP.x) - (_lightDir.x * UP.z);
			_lightSide.z = (_lightDir.x * UP.y) - (_lightDir.y * UP.x);
			_lightSide.normalize(); // needed?
			
			// inlined: Number3D.cross(_lightDir, dir_x);
			_lightUp.x = (_lightSide.y * _lightDir.z) - (_lightSide.z * _lightDir.y);
			_lightUp.y = (_lightSide.z * _lightDir.x) - (_lightSide.x * _lightDir.z);
			_lightUp.z = (_lightSide.x * _lightDir.y) - (_lightSide.y * _lightDir.x);
			_lightUp.normalize(); // needed?

			if(Papervision3D.useRIGHTHANDED || object.flipLightDirection)
			{
				_lightDir.x = -_lightDir.x;
				_lightDir.y = -_lightDir.y;
				_lightDir.z = -_lightDir.z;
			}
			
			// copy values
			lightMatrix.n11 = _lightSide.x;
			lightMatrix.n12 = _lightSide.y;
			lightMatrix.n13 = _lightSide.z;
			lightMatrix.n21 = _lightUp.x;
			lightMatrix.n22 = _lightUp.y;
			lightMatrix.n23 = _lightUp.z;
			lightMatrix.n31 = _lightDir.x;
			lightMatrix.n32 = _lightDir.y;
			lightMatrix.n33 = _lightDir.z;
			
			
			return lightMatrix;
		}		
	}
}