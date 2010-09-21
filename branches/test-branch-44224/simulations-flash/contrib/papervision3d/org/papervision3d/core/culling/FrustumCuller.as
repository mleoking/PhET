package org.papervision3d.core.culling
{
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.AxisAlignedBoundingBox;
	import org.papervision3d.core.math.BoundingSphere;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.objects.DisplayObject3D;
	
	/**
	 * @author Tim Knip
	 */ 
	public class FrustumCuller implements IObjectCuller
	{
		public static const INSIDE:int = 1;
		public static const OUTSIDE:int = -1;
		public static const INTERSECT:int = 0;
		
		/** */
		public var transform	: Matrix3D;

		/**
		 * Constructor.
		 */ 
		public function FrustumCuller()
		{
			this.transform = Matrix3D.IDENTITY;
			
			this.initialize();
		}
		
		/**
		 * Intializes the frustum.
		 * 
		 * @param	fovY	Vertical Field Of View in degrees.
		 * @param	ratio	Aspect ratio (ie: viewport.width / viewport.height).
		 * @param	near	Distance to near plane (ie: camera.focus).
		 * @param	far		Distance to far plane.
		 */ 
		public function initialize(fovY:Number=60, ratio:Number=1.333, near:Number=1, far:Number=5000):void
		{
			// store the information
			_fov = fovY;
			_ratio = ratio;
			_near = near;
			_far = far;

			var angle : Number = (Math.PI/180) * _fov * 0.5;
			
			// compute width and height of the near and far section
			_tang = Math.tan(angle);
			_nh = _near * _tang;
			_nw = _nh * _ratio;
			_fh = _far * _tang;
			_fw = _fh * _ratio;
		
			var anglex : Number = Math.atan(_tang * _ratio);
		
			// used for bounding-sphere culling
			_sphereX = 1.0 / Math.cos(anglex);		
			_sphereY = 1.0 / Math.cos(angle);
		}
		
		/**
		 * Tests whether an axis aligned boundingbox is inside, outside or intersecting the frustum. 
		 * When earlyOut is set to true, the method returns INSIDE when a single point of the aabb is
		 * inside the frustum (fast). Set earlyOut to false if you want to test for INTERSECT. 
		 * 
		 * @param	object	The object to test.
		 * @param	aabb	AxisAlignedBoundingBox.
		 * @param	earlyOut	Early out. Default is true.
		 * 
		 * @return Integer indicating inside(1), outside(-1) or intersecting(0) the frustum.
		 */
		public function aabbInFrustum(object:DisplayObject3D, aabb:AxisAlignedBoundingBox, earlyOut:Boolean=true):int
		{
			var vertex:Vertex3D;
			var num:Number3D;
			var numInside:int = 0;
			var numOutside:int = 0;
			var vertices:Array = aabb.getBoxVertices();
			
			// Transform the boundingbox to world and test...
			for each(vertex in vertices)
			{
				num = vertex.toNumber3D();
				Matrix3D.multiplyVector(object.world, num);
				if(pointInFrustum(num.x, num.y, num.z) == INSIDE)
				{
					numInside++;
					if(earlyOut)
						return INSIDE;	
				}
				else
					numOutside++;
				
				// aabb has points both inside and outside the frustum, must be intersecting.
				if(numInside && numOutside)
					return INTERSECT;
			}
				
			if(numInside)
				return (numInside < 8 ? INTERSECT : INSIDE);
			else
				return OUTSIDE;
		}
		
		/**
		 * Tests whether a point is inside the frustum.
		 *
		 * @param 	x
		 * @param 	y
		 * @param 	z
		 *
		 * @return	Integer indicating inside (1) or outside (-1) the frustum.
		 */
		public function pointInFrustum(x : Number, y : Number, z : Number) : int
		{
			var m	:Matrix3D = this.transform;
			
			// compute vector from camera position to p
			var px	:Number = x - m.n14;
			var py	:Number = y - m.n24;
			var pz	:Number = z - m.n34;
			
			// compute and test the Z coordinate
			var pcz : Number = px * m.n13 + py * m.n23 + pz * m.n33;
			if (pcz > _far || pcz < _near)
				return OUTSIDE;
			
			// compute and test the Y coordinate
			var pcy : Number = px * m.n12 + py * m.n22 + pz * m.n32;
			var aux : Number = pcz * _tang;
			if(pcy > aux || pcy < -aux)
				return OUTSIDE;
	
			// compute and test the X coordinate
			var pcx : Number = px * m.n11 + py * m.n21 + pz * m.n31;
			aux = aux * _ratio;
			if (pcx > aux || pcx < -aux)
				return OUTSIDE;
			
			return INSIDE;
		}
		
		/**
		 * Tests whether a sphere is inside the frustum.
		 *
		 * @param 	object	The object to test.
		 * @param	boundingSphere	The bounding sphere.
		 *
		 * @return	Integer indicating inside (1), outside (0) or intersecting (-1) the frustum.
		 */
		public function sphereInFrustum(obj:DisplayObject3D, boundingSphere:BoundingSphere) : int
		{
			var radius:Number = boundingSphere.radius * Math.max(obj.scaleX, Math.max(obj.scaleY, obj.scaleZ));
			var d : Number;
			var ax : Number;
			var ay : Number;
			var az : Number;
			var result : int = INSIDE;
		
			var m:Matrix3D = this.transform;

			// compute vector from camera position to p
			var px : Number = obj.world.n14 - m.n14;
			var py : Number = obj.world.n24 - m.n24;
			var pz : Number = obj.world.n34 - m.n34;
			
			// near and far
			az =  px * m.n13 + py * m.n23 + pz * m.n33;
			if(az > _far + radius || az < _near-radius)
				return OUTSIDE;			
			if(az > _far - radius || az < _near+radius)
				result = INTERSECT;

			// top and bottom
			ay = px * m.n12 + py * m.n22 + pz * m.n32;
			d = _sphereY * radius;
			az *= _tang;
			if(ay > az+d || ay < -az-d)
				return OUTSIDE;
			if(ay > az-d || ay < -az+d)
				result = INTERSECT;
	
			// left and right
			ax = px * m.n11 + py * m.n21 + pz * m.n31;
			az *= _ratio;
			d = _sphereX * radius;
			if(ax > az+d || ax < -az-d)
				return OUTSIDE;
			if(ax > az-d || ax < -az+d)
				result = INTERSECT;
				
			return result;
		}
		
		/**
		 * Tests whether an object is inside the frustum.
		 * 
		 * @param	obj		The object to test
		 * 
		 * @return	Integer indicating inside(1), outside(-1) or intersecting(0)
		 */
		public function testObject( obj:DisplayObject3D ):int
		{	
			var result	: int = INSIDE;
			
			if(!obj.geometry || !obj.geometry.vertices || !obj.geometry.vertices.length)
				return result;	
			
			switch(obj.frustumTestMethod)
			{
				case FrustumTestMethod.BOUNDING_SPHERE:
					result = sphereInFrustum(obj, obj.geometry.boundingSphere); 
					break;
				case FrustumTestMethod.BOUNDING_BOX:
					result = aabbInFrustum(obj, obj.geometry.aabb);
					break;
				case FrustumTestMethod.NO_TESTING:
					break;
				default:
					break;	
			}

			return result;
		}
		
		public function set far(value : Number):void
		{
			this.initialize(_fov, _ratio, _near, value);
		}
		
		public function get far() : Number
		{
			return _far;
		}
		
		public function set fov(value : Number):void
		{
			this.initialize(value, _ratio, _near, _far);
		}
		
		public function get fov() : Number
		{
			return _fov;
		}
		
		public function set near(value : Number):void
		{
			this.initialize(_fov, _ratio, value, _far);
		}
		
		public function get near() : Number
		{
			return _near;
		}
		
		public function set ratio(value : Number):void
		{
			this.initialize(_fov, value, _near, _far);
		}
		
		public function get ratio() : Number
		{
			return _ratio;
		}
		
		private var _fov		: Number;
		private var _far		: Number;
		private var _near		: Number;
		private var _nw			: Number;
		private var _nh			: Number;
		private var _fw			: Number;
		private var _fh			: Number;
		private var _tang		: Number;
		private var _ratio  	: Number;
		private var _sphereX 	: Number;
		private var _sphereY	: Number;
	}
}