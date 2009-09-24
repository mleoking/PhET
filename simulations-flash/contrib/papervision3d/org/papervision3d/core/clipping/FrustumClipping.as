package org.papervision3d.core.clipping
{
	import org.papervision3d.core.dyn.DynamicTriangles;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.math.Plane3D;
	import org.papervision3d.core.math.util.ClassificationUtil;
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.objects.DisplayObject3D;
	
	public class FrustumClipping extends DefaultClipping
	{
		public static const NONE 	: int = 0x0;
		public static const NEAR 	: int = 0x1;
		public static const LEFT 	: int = 0x2;
		public static const RIGHT	: int = 0x4;
		public static const TOP 	: int = 0x8;
		public static const BOTTOM 	: int = 0x10;
		public static const FAR 	: int = 0x20;
		
		public static const DEFAULT : int = NEAR + LEFT + RIGHT + TOP + BOTTOM;
		public static const ALL 	: int = DEFAULT + FAR;
		
		/**
		 * 
		 */ 
		public function FrustumClipping( planes:int=-1 )
		{	
			_cleft = Plane3D.fromCoefficients(0, 1, 0, 0);
			_cright = Plane3D.fromCoefficients(0, 1, 0, 0);
			_ctop = Plane3D.fromCoefficients(0, 1, 0, 0);
			_cbottom = Plane3D.fromCoefficients(0, 1, 0, 0);
			_cnear = Plane3D.fromCoefficients(0, 1, 0, 0);
			_cfar = Plane3D.fromCoefficients(0, 1, 0, 0);
			
			_wleft = Plane3D.fromCoefficients(0, 1, 0, 0);
			_wright = Plane3D.fromCoefficients(0, 1, 0, 0);
			_wtop = Plane3D.fromCoefficients(0, 1, 0, 0);
			_wbottom = Plane3D.fromCoefficients(0, 1, 0, 0);
			_wnear = Plane3D.fromCoefficients(0, 1, 0, 0);
			_wfar = Plane3D.fromCoefficients(0, 1, 0, 0);
			
			_nc = new Number3D();
			_fc = new Number3D();
			_ntl = new Number3D();
			_ntr = new Number3D();
			_nbr = new Number3D();
			_nbl = new Number3D();
			_ftl = new Number3D();
			_ftr = new Number3D();
			_fbr = new Number3D();
			_fbl = new Number3D();
			
			_camPos = new Number3D();
			_axisX = new Number3D();
			_axisY = new Number3D();
			_axisZ = new Number3D();
			_axisZi = new Number3D();
			
			_matrix = Matrix3D.IDENTITY;
			_world = Matrix3D.IDENTITY;
			_dynTriangles = new DynamicTriangles();
			
			this.planes = planes < 0 ? DEFAULT : planes;
		}

		/**
		 * Bitmask indicating which planes are used for clipping.
		 */
		public function get planes() : int
		{
			return _planes;
		} 
		
		/**
		 * Bitmask indicating which planes are used for clipping.
		 */
		public function set planes( value:int ) : void
		{
			_planes = value;
			
			_cplanes = new Array();
			_wplanes = new Array();
			_planePoints = new Array();
			
			if( _planes & NEAR )
			{
				_cplanes.push( _cnear );
				_wplanes.push( _wnear );
				_planePoints.push( _nc );
			}
			
			if( _planes & FAR )
			{
				_cplanes.push( _cfar );
				_wplanes.push( _wfar );
				_planePoints.push( _fc );
			}
			
			if( _planes & LEFT )
			{
				_cplanes.push( _cleft );
				_wplanes.push( _wleft );
				_planePoints.push( _camPos );
			}
			
			if( _planes & RIGHT )
			{
				_cplanes.push( _cright );
				_wplanes.push( _wright );
				_planePoints.push( _camPos );
			}
			
			if( _planes & TOP )
			{
				_cplanes.push( _ctop );
				_wplanes.push( _wtop );
				_planePoints.push( _camPos );
			}
			
			if( _planes & BOTTOM )
			{
				_cplanes.push( _cbottom );
				_wplanes.push( _wbottom );
				_planePoints.push( _camPos );
			}
		} 
		
		/**
		 * 
		 */ 	
		public override function reset(renderSessionData:RenderSessionData):void
		{
			var camera	: CameraObject3D = renderSessionData.camera;
			
			var vpw : Number = renderSessionData.viewPort.viewportWidth;
			var vph : Number = renderSessionData.viewPort.viewportHeight;
			var tan : Number = Math.tan( (camera.fov/2) * TO_RADIANS );
			var d   : Number = camera.focus;
			
			_matrix.copy( renderSessionData.camera.transform );
			
			_axisX.reset(_matrix.n11, _matrix.n21, _matrix.n31);
			_axisY.reset(_matrix.n12, _matrix.n22, _matrix.n32);
			_axisZ.reset(_matrix.n13, _matrix.n23, _matrix.n33);
			_axisZi.reset( -_axisZ.x, -_axisZ.y, -_axisZ.z );

			var hnear:Number = 2 * tan * d;
			var wnear:Number = hnear * (vpw/vph)

			_camPos.reset(camera.x, camera.y, camera.z);

			_nc.x = _camPos.x + (d * _axisZ.x);
			_nc.y = _camPos.y + (d * _axisZ.y);
			_nc.z = _camPos.z + (d * _axisZ.z);
			
			_fc.x = _camPos.x + (camera.far * _axisZ.x);
			_fc.y = _camPos.y + (camera.far * _axisZ.y);
			_fc.z = _camPos.z + (camera.far * _axisZ.z);
			
			_ntl.copyFrom( _nc );
			_nbl.copyFrom( _nc );
			_ntr.copyFrom( _nc );
			_nbr.copyFrom( _nc );
			
			hnear /= 2;
			wnear /= 2;
			
			_ntl.x -= wnear * _axisX.x;
			_ntl.y -= wnear * _axisX.y;
			_ntl.z -= wnear * _axisX.z;
			
			_ntl.x += hnear * _axisY.x;
			_ntl.y += hnear * _axisY.y;
			_ntl.z += hnear * _axisY.z;
			
			_nbl.x -= wnear * _axisX.x;
			_nbl.y -= wnear * _axisX.y;
			_nbl.z -= wnear * _axisX.z;
			
			_nbl.x -= hnear * _axisY.x;
			_nbl.y -= hnear * _axisY.y;
			_nbl.z -= hnear * _axisY.z;
			
			_nbr.x += wnear * _axisX.x;
			_nbr.y += wnear * _axisX.y;
			_nbr.z += wnear * _axisX.z;
			
			_nbr.x -= hnear * _axisY.x;
			_nbr.y -= hnear * _axisY.y;
			_nbr.z -= hnear * _axisY.z;
			
			_ntr.x += wnear * _axisX.x;
			_ntr.y += wnear * _axisX.y;
			_ntr.z += wnear * _axisX.z;
			
			_ntr.x += hnear * _axisY.x;
			_ntr.y += hnear * _axisY.y;
			_ntr.z += hnear * _axisY.z;
			
			if( _planes & NEAR )
			{
				_cnear.setNormalAndPoint( _axisZ, _nc );
			}
			
			if( _planes & FAR )
			{
				_cfar.setNormalAndPoint( _axisZi, _fc );
			}
			
			if( _planes & LEFT )
			{
				_cleft.setThreePoints( _camPos, _nbl, _ntl );
			}
			
			if( _planes & RIGHT )
			{
				_cright.setThreePoints( _camPos, _ntr, _nbr );
			}
			
			if( _planes & TOP )
			{
				_ctop.setThreePoints( _camPos, _ntl, _ntr );
			}
			
			if( _planes & BOTTOM )
			{
				_cbottom.setThreePoints( _camPos, _nbr, _nbl );
			}
			
			_dynTriangles.releaseAll();
		}
		
		/**
		 * 
		 */ 
		public override function setDisplayObject(object:DisplayObject3D, renderSessionData:RenderSessionData):void
		{
			_world.copy( object.world );
			_world.invert();
			
			var pt : Number3D = new Number3D();
			
			for( var i:int = 0; i < _cplanes.length; i++ )
			{
				var cplane : Plane3D = _cplanes[i];
				var wplane : Plane3D = _wplanes[i];
				
				pt.copyFrom( _planePoints[i] );
				wplane.normal.copyFrom( cplane.normal );

				Matrix3D.multiplyVector3x3( _world, wplane.normal );
				Matrix3D.multiplyVector( _world, pt );

				wplane.setNormalAndPoint( wplane.normal, pt );
			}
		}
		
		/**
		 * 
		 */ 
		public override function testFace(triangle:Triangle3D, object:DisplayObject3D, renderSessionData:RenderSessionData):Boolean
		{
			for( var i:int = 0; i < _wplanes.length; i++ )
			{
				var plane : Plane3D = _wplanes[i];
				
				var side : int = ClassificationUtil.classifyTriangle( triangle, plane );
				
				if( side == ClassificationUtil.BACK || side == ClassificationUtil.COINCIDING )
				{
					return false;
				}
				else if( side == ClassificationUtil.STRADDLE )
				{
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 
		 */
		public override function clipFace(triangle:Triangle3D, object:DisplayObject3D, material:MaterialObject3D, renderSessionData:RenderSessionData, outputArray:Array):Number 
		{
			var points	:Array = [triangle.v0, triangle.v1, triangle.v2];
			var uvs		:Array = [triangle.uv0, triangle.uv1, triangle.uv2];
			var clipped :Boolean = false;
			
			for( var i:int = 0; i < _wplanes.length; i++ )
			{
				var plane : Plane3D = _wplanes[i];
			
				var side : int = ClassificationUtil.classifyPoints( points, plane );
				
				try
				{
					if( side == ClassificationUtil.STRADDLE )
					{
						points = clipPointsToPlane( triangle.instance, points, uvs, plane );
						clipped = true;
					}
				}
				catch( e:Error )
				{
					PaperLogger.error( "FrustumClipping#clipFace : " + e.message );
				}
			}
			
			if( !clipped )
			{
				outputArray.push( triangle );
				return 1;
			}
			
			var v0 : Vertex3D = points[0];
			var t0 : NumberUV = uvs[0];
			
			for( var j:int = 1; j < points.length; j++ )
			{
				var k:int = (j+1) % points.length;
				
				var v1 : Vertex3D = points[j];
				var v2 : Vertex3D = points[k];
				
				var t1 : NumberUV = uvs[j];
				var t2 : NumberUV = uvs[k];
				
				//var tri : Triangle3D = new Triangle3D(triangle.instance, [v0, v1, v2], triangle.material, [t0, t1, t2]);
				var tri :Triangle3D = _dynTriangles.getTriangle(triangle.instance, triangle.material, v0, v1, v2, t0, t1, t2);
				// make sure we got a valid triangle!
				if( tri.faceNormal.modulo )
				{
					outputArray.push( tri );
				}
			}
			
			return outputArray.length;
		}
		
		/**
		 * Sutherland-Hodgman clipping of an Array of points.
		 * 
		 * @param	points
		 * @param	plane
		 * @return
		 */
		public function clipPointsToPlane( object:DisplayObject3D, points:Array, uvs:Array, plane:Plane3D ):Array
		{
			var verts:Array = new Array();
			var texels:Array = new Array();

			var dist1:Number = plane.distance(points[0]);
			
			for( var j:int = 0; j < points.length; j++ )
			{
				var k:int = (j+1) % points.length;
				
				var pt0:Vertex3D = points[j];
				var pt1:Vertex3D = points[k];
				
				var t0:NumberUV = uvs[j];
				var t1:NumberUV = uvs[k];
		
				var dist2:Number = plane.distance(pt1);
				var d:Number = dist1 / (dist1-dist2);
				var t:Vertex3D;
				var uv:NumberUV;
				
				var status:uint = compareDistances( dist1, dist2 );
				
				switch( status )
				{
					case INSIDE:
						verts.push( pt1 );
						texels.push( t1 );
						break;
				
					case IN_OUT:
						t = new Vertex3D();
						t.x = pt0.x + (pt1.x - pt0.x) * d;
						t.y = pt0.y + (pt1.y - pt0.y) * d;
						t.z = pt0.z + (pt1.z - pt0.z) * d;
						
						uv = new NumberUV();
						uv.u = t0.u + (t1.u - t0.u) * d;
						uv.v = t0.v + (t1.v - t0.v) * d;
						texels.push( uv );
						
						verts.push( t );
						
						object.geometry.vertices.push( t );
						break;
					
					case OUT_IN:
						uv = new NumberUV();
						uv.u = t0.u + (t1.u - t0.u) * d;
						uv.v = t0.v + (t1.v - t0.v) * d;
						texels.push( uv );
						texels.push( t1 );
						
						t = new Vertex3D();
						t.x = pt0.x + (pt1.x - pt0.x) * d;
						t.y = pt0.y + (pt1.y - pt0.y) * d;
						t.z = pt0.z + (pt1.z - pt0.z) * d;
						verts.push( t );
						verts.push( pt1 );
						
						object.geometry.vertices.push( t );
						break;
							
					default:
						break;
				}
				dist1 = dist2;
			}
			
			for( var i:int = 0; i < texels.length; i++ )
			{
				uvs[i] = texels[i];
			}
			
			return verts;			
		}
		
		/**
		 * 
		 * @param	pDist1
		 * @param	pDist2
		 * @return
		 */
		private function compareDistances( pDist1:Number, pDist2:Number ):uint
		{			
			if( pDist1 < 0 && pDist2 < 0 )
				return OUTSIDE;
			else if( pDist1 > 0 && pDist2 > 0 )
				return INSIDE;
			else if( pDist1 > 0 && pDist2 < 0 )
				return IN_OUT;	
			else
				return OUT_IN;
		}	

		private static const OUTSIDE:uint = 0;
		private static const INSIDE:uint = 1;
		private static const OUT_IN:uint = 2;
		private static const IN_OUT:uint = 3;

		private static const TO_DEGREES : Number = 180/Math.PI;
		private static const TO_RADIANS : Number = Math.PI/180;
		
		private var _planes		: int;
		
		// frustum planes
		private var _cnear 		: Plane3D;
		private var _cfar 		: Plane3D;
		private var _ctop 		: Plane3D;
		private var _cbottom	: Plane3D;
		private var _cleft 		: Plane3D;
		private var _cright	 	: Plane3D;
		
		// frustum planes transformed by object's world matrix
		private var _wnear 		: Plane3D;
		private var _wfar 		: Plane3D;
		private var _wtop 		: Plane3D;
		private var _wbottom 	: Plane3D;
		private var _wleft 		: Plane3D;
		private var _wright	 	: Plane3D;
		
		// frustum geometry
		private var _nc			: Number3D;
		private var _fc			: Number3D;
		private var _ntl		: Number3D;
		private var _ntr		: Number3D;
		private var _nbr		: Number3D;
		private var _nbl		: Number3D;
		private var _ftl		: Number3D;
		private var _ftr		: Number3D;
		private var _fbr		: Number3D;
		private var _fbl		: Number3D;
		
		private var _camPos		: Number3D;
		private var _axisX		: Number3D;
		private var _axisY		: Number3D;
		private var _axisZ		: Number3D;
		private var _axisZi		: Number3D;
		
		private var _cplanes : Array;
		private var _wplanes : Array;
		
		private var _matrix	: Matrix3D;
		private var _world	: Matrix3D;
		
		private var _planePoints : Array;
		private var _dynTriangles :DynamicTriangles;
	}
}