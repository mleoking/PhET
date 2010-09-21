package org.papervision3d.cameras
{
	import org.papervision3d.core.math.*;
	import org.papervision3d.objects.DisplayObject3D;  

	/** b at turbulent dot ca - http://agit8.turbulent.ca 
	 * v1 - 2009-01-21
	 **/
	public class SpringCamera3D extends Camera3D
	{
		/**
		 * [optional] Target object3d that camera should follow. If target is null, camera behaves just like a normal Camera3D.
		 */
		public var _camTarget:DisplayObject3D;

		public override function set target(object:DisplayObject3D):void
		{
			_camTarget = object;
		}

		public override function get target():DisplayObject3D
		{
			return _camTarget;
		}

		/**
		 * Stiffness of the spring, how hard is it to extend. The higher it is, the more "fixed" the cam will be.
		 * A number between 1 and 20 is recommended.
		 */
		public var stiffness:Number = 1;
		/**
		 * Damping is the spring internal friction, or how much it resists the "boinggggg" effect. Too high and you'll lose it!
		 * A number between 1 and 20 is recommended.
		 */
		public var damping:Number = 4;
		/**
		 * Mass of the camera, if over 120 and it'll be very heavy to move.
		 */
		public var mass:Number = 40;
		/**
		 * Offset of spring center from target in target object space, ie: Where the camera should ideally be in the target object space.
		 */
		public var positionOffset:Number3D = new Number3D(0, 5, -50);
		/**
		 * offset of facing in target object space, ie: where in the target object space should the camera look.
		 */
		public var lookOffset:Number3D = new Number3D(0, 2, 10);
		//zrot to apply to the cam
		private var _zrot:Number = 0;
		//private physics members
		private var _velocity:Number3D = new Number3D();
		private var _dv:Number3D = new Number3D();
		private var _stretch:Number3D = new Number3D();
		private var _force:Number3D = new Number3D();
		private var _acceleration:Number3D = new Number3D();
		//private target members
		private var _desiredPosition:Number3D = new Number3D();
		private var _lookAtPosition:Number3D = new Number3D();
		private var _targetTransform:Matrix3D = new Matrix3D();
		//private transformed members
		private var _xPositionOffset:Number3D = new Number3D();
		private var _xLookOffset:Number3D = new Number3D();
		private var _xPosition:Number3D = new Number3D();
		private var _xLookAtObject:DisplayObject3D = new DisplayObject3D();

		/**
		 * Constructor.
		 * 
		 * @param   fov     This value is the vertical Field Of View (FOV) in degrees.
		 * @param   near    Distance to the near clipping plane.
		 * @param   far     Distance to the far clipping plane.
		 * @param   useCulling      Boolean indicating whether to use frustum culling. When true all objects outside the view will be culled.
		 * @param   useProjection   Boolean indicating whether to use a projection matrix for perspective.
		 */  
		public function SpringCamera3D(fov:Number = 60, near:Number = 10, far:Number = 5000, useCulling:Boolean = false, useProjection:Boolean = false)
		{
			super(fov, near, far, useCulling, useProjection);
		}

		/**
		 * Rotation in degrees along the camera Z vector to apply to the camera after it turns towards the target .
		 */
		public function set zrot(n:Number):void
		{
			_zrot = n;
			if(_zrot < 0.001) n = 0;
		}

		public function get zrot():Number
		{
			return _zrot;
		}

		public override function transformView(transform:Matrix3D = null):void
		{
			if(_camTarget != null)
			{
				_targetTransform.n31 = _camTarget.transform.n31;
				_targetTransform.n32 = _camTarget.transform.n32;
				_targetTransform.n33 = _camTarget.transform.n33;
            
				_targetTransform.n21 = _camTarget.transform.n21;
				_targetTransform.n22 = _camTarget.transform.n22;
				_targetTransform.n23 = _camTarget.transform.n23;
            
				_targetTransform.n11 = _camTarget.transform.n11;
				_targetTransform.n12 = _camTarget.transform.n12;
				_targetTransform.n13 = _camTarget.transform.n13;
            
				_xPositionOffset.x = positionOffset.x;
				_xPositionOffset.y = positionOffset.y;
				_xPositionOffset.z = positionOffset.z;
            
				Matrix3D.multiplyVector(_targetTransform, _xPositionOffset);
            
				_xLookOffset.x = lookOffset.x;
				_xLookOffset.y = lookOffset.y;
				_xLookOffset.z = lookOffset.z;
            
				Matrix3D.multiplyVector(_targetTransform, _xLookOffset);
            
				_desiredPosition.x = _camTarget.x + _xPositionOffset.x;
				_desiredPosition.y = _camTarget.y + _xPositionOffset.y;
				_desiredPosition.z = _camTarget.z + _xPositionOffset.z;
            
				_lookAtPosition.x = _camTarget.x + _xLookOffset.x;
				_lookAtPosition.y = _camTarget.y + _xLookOffset.y;
				_lookAtPosition.z = _camTarget.z + _xLookOffset.z;
            
            
				_stretch.x = (x - _desiredPosition.x) * -stiffness;
				_stretch.y = (y - _desiredPosition.y) * -stiffness;
				_stretch.z = (z - _desiredPosition.z) * -stiffness;
            
				_dv.x = _velocity.x * damping;
				_dv.y = _velocity.y * damping;
				_dv.z = _velocity.z * damping;
            
				_force.x = _stretch.x - _dv.x;
				_force.y = _stretch.y - _dv.y;
				_force.z = _stretch.z - _dv.z;
            
				_acceleration.x = _force.x * (1 / mass);
				_acceleration.y = _force.y * (1 / mass);
				_acceleration.z = _force.z * (1 / mass);
            
				_velocity.plusEq(_acceleration);
            
            
				_xPosition.x = x + _velocity.x;
				_xPosition.y = y + _velocity.y;
				_xPosition.z = z + _velocity.z;
            
				x = _xPosition.x;
				y = _xPosition.y;
				z = _xPosition.z;
            
				_xLookAtObject.x = _lookAtPosition.x;
				_xLookAtObject.y = _lookAtPosition.y;
				_xLookAtObject.z = _lookAtPosition.z;
            
				lookAt(_xLookAtObject);
            
            
				if(Math.abs(_zrot) > 0) this.rotationZ = _zrot;
			}  
        
			super.transformView(transform);
		}
	}
}
