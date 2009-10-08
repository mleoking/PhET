package away3d.core.geom
{
	import away3d.core.base.*;
	import away3d.core.math.*;
	
	public class Frustum
	{
		public static const LEFT:int = 0;
		public static const RIGHT:int = 1;
		public static const TOP:int = 2;
		public static const BOTTOM:int = 3;
		public static const NEAR:int = 4;
		public static const FAR:int = 5;
		
		//clasification
		public static const OUT:int = 0;
		public static const IN:int = 1;
		public static const INTERSECT:int = 2;
		
		public var planes:Array;
		
		private var _matrix:MatrixAway3D = new MatrixAway3D();
		private var _distance:Number;
    	
		/**
		 * Creates a frustum consisting of 6 planes in 3d space.
		 */
		public function Frustum()
		{
			planes = new Array(6);
			planes[LEFT] = new Plane3D();
			planes[RIGHT] = new Plane3D();
			planes[TOP] = new Plane3D();
			planes[BOTTOM] = new Plane3D();
			planes[NEAR] = new Plane3D();
			planes[FAR] = new Plane3D();
		}
		
		/**
		 * Classify this Object3D against this frustum
		 * @return int Frustum.IN, Frustum.OUT or Frustum.INTERSECT
		 */
		public function classifyObject3D(obj:Object3D):int
		{
			return classifySphere(obj.sceneTransform.position, obj.boundingRadius);
		}
		
		/**
		 * Classify this sphere against this frustum
		 * @return int Frustum.IN, Frustum.OUT or Frustum.INTERSECT
		 */
		public function classifySphere(center:Number3D, radius:Number):int
		{
			var _plane:Plane3D;
			for each(_plane in planes) {
				_distance = _plane.distance(center);
				
				if(_distance < -radius)
					return OUT;
				
				if(Math.abs(_distance) < radius)
					return INTERSECT;
			}
			
			return IN;
		}
		
		/**
		 * Classify this radius against this frustum
		 * @return int Frustum.IN, Frustum.OUT or Frustum.INTERSECT
		 */
		public function classifyRadius(radius:Number):int
		{
			var _plane:Plane3D;
			for each(_plane in planes) {
				if(_plane.d < -radius)
					return OUT;
				
				if(Math.abs(_plane.d) < radius)
					return INTERSECT;	
				
			}
			
			return IN;
		}
		
		/**
		 * Classify this axis aligned bounding box against this frustum
		 * @return int Frustum.IN, Frustum.OUT or Frustum.INTERSECT
		 */		
		public function classifyAABB(points:Array):int
		{
			var planesIn:int = 0;
			
			for(var p:int = 0; p < 6; p++)
			{
				var plane:Plane3D = Plane3D(planes[p]);
				var pointsIn:int = 0;	
				
				for( var i:int = 0; i < 8; ++i)
				{
					if(plane.classifyPoint( points[i]) == Plane3D.FRONT)
						pointsIn++;
				}
				
				if(pointsIn == 0) return OUT;
				
				if(pointsIn == 8) planesIn++;
			}	
			
			if(planesIn == 6) return IN;
			
			return INTERSECT;
		}
		
		
		/**
		 * Extract this frustum's plane from the 4x4 projection matrix m.
		 */	
		public function extractFromMatrix(m:MatrixAway3D):void
		{
			_matrix = m;
			
			var sxx:Number = m.sxx, sxy:Number = m.sxy, sxz:Number = m.sxz, tx:Number = m.tx,
			    syx:Number = m.syx, syy:Number = m.syy, syz:Number = m.syz, ty:Number = m.ty,
			    szx:Number = m.szx, szy:Number = m.szy, szz:Number = m.szz, tz:Number = m.tz,
			    swx:Number = m.swx, swy:Number = m.swy, swz:Number = m.swz, tw:Number = m.tw;
			
			
			var near:Plane3D = Plane3D(planes[NEAR]);
			near.a = swx+szx;
			near.b = swy+szy;
			near.c = swz+szz;
			near.d = tw+tz;
			near.normalize();
			
			var far:Plane3D = Plane3D(planes[FAR]);
			far.a = -szx+swx;
			far.b = -szy+swy;
			far.c = -szz+swz;
			far.d = -tz+tw;
			far.normalize();
			
			var left:Plane3D = Plane3D(planes[LEFT]);
			left.a = swx+sxx;
			left.b = swy+sxy;
			left.c = swz+sxz;
			left.d = tw+tx;
			left.normalize();
			
			var right:Plane3D = Plane3D(planes[RIGHT]);
			right.a = -sxx+swx;
			right.b = -sxy+swy;
			right.c = -sxz+swz;
			right.d = -tx+tw;
			right.normalize();
			
			var top:Plane3D = Plane3D(planes[TOP]);
			top.a = swx+syx;
			top.b = swy+syy;
			top.c = swz+syz;
			top.d = tw+ty;
			top.normalize();
			
			var bottom:Plane3D = Plane3D(planes[BOTTOM]);
			bottom.a = -syx+swx;
			bottom.b = -syy+swy;
			bottom.c = -syz+swz;
			bottom.d = -ty+tw;	
			bottom.normalize();
		}
	}
}
