package org.papervision3d.core.math
{
	/**
	 * @author Tim Knip 
	 */
	public class Quaternion 
	{
		private var _matrix:Matrix3D;
		
		public static const EPSILON:Number = 0.000001;
		public static const DEGTORAD:Number = (Math.PI/180.0);
		public static const RADTODEG:Number = (180.0/Math.PI);
		
		/** */
		public var x:Number;
		
		/** */
		public var y:Number;
		
		/** */
		public var z:Number;
		
		/** */
		public var w:Number;
		
		/**
		 * constructor.
		 * 
		 * @param	x
		 * @param	y
		 * @param	z
		 * @param	w
		 * @return
		 */
		public function Quaternion( x:Number = 0, y:Number = 0, z:Number = 0, w:Number = 1 )
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
			
			_matrix = Matrix3D.IDENTITY;
		}
		
		/**
		 * Clone.
		 * 
		 */
		public function clone():Quaternion
		{
			return  new Quaternion(this.x, this.y, this.z, this.w);
		}
		
		/**
		 * Multiply.
		 * 
		 * @param	a
		 * @param	b
		 */
		public function calculateMultiply( a:Quaternion, b:Quaternion ):void
		{
			this.x = a.w*b.x + a.x*b.w + a.y*b.z - a.z*b.y;
			this.y = a.w*b.y - a.x*b.z + a.y*b.w + a.z*b.x;
			this.z = a.w*b.z + a.x*b.y - a.y*b.x + a.z*b.w;
			this.w = a.w*b.w - a.x*b.x - a.y*b.y - a.z*b.z;
		}
		
		/**
		 * Creates a Quaternion from a axis and a angle.
		 * 
		 * @param	x 	X-axis
		 * @param	y 	Y-axis
		 * @param	z 	Z-axis
		 * @param	angle	angle in radians.
		 * 
		 * @return
		 */
		public function setFromAxisAngle( x:Number, y:Number, z:Number, angle:Number ):void
		{
			var sin:Number = Math.sin( angle / 2 );
			var cos:Number = Math.cos( angle / 2 );
			this.x = x * sin;
			this.y = y * sin;
			this.z = z * sin;
			this.w = cos;
			this.normalize();
		}
		
		/**
		 * Sets this Quaternion from Euler angles.
		 * 
		 * @param	ax	X-angle in radians.
		 * @param	ay	Y-angle in radians.
		 * @param	az	Z-angle in radians.
		 */ 
		public function setFromEuler(ax:Number, ay:Number, az:Number, useDegrees:Boolean=false):void
		{
			if( useDegrees )
			{
				ax *= DEGTORAD;
				ay *= DEGTORAD;
				az *= DEGTORAD;
			}
			
			var fSinPitch       :Number = Math.sin( ax * 0.5 );
			var fCosPitch       :Number = Math.cos( ax * 0.5 );
			var fSinYaw         :Number = Math.sin( ay * 0.5 );
			var fCosYaw         :Number = Math.cos( ay * 0.5 );
			var fSinRoll        :Number = Math.sin( az * 0.5 );
			var fCosRoll        :Number = Math.cos( az * 0.5 );
			var fCosPitchCosYaw :Number = fCosPitch * fCosYaw;
			var fSinPitchSinYaw :Number = fSinPitch * fSinYaw;

			this.x = fSinRoll * fCosPitchCosYaw     - fCosRoll * fSinPitchSinYaw;
			this.y = fCosRoll * fSinPitch * fCosYaw + fSinRoll * fCosPitch * fSinYaw;
			this.z = fCosRoll * fCosPitch * fSinYaw - fSinRoll * fSinPitch * fCosYaw;
			this.w = fCosRoll * fCosPitchCosYaw     + fSinRoll * fSinPitchSinYaw;
		}
		
		/**
		 * Modulo.
		 * 
		 * @param	a
		 * @return
		 */
		public function get modulo():Number
		{
			return Math.sqrt(x*x + y*y + z*z + w*w);
		}
		
		/**
		 * Conjugate.
		 * 
		 * @param	a
		 * @return
		 */
		public static function conjugate( a:Quaternion ):Quaternion
		{
			var q:Quaternion = new Quaternion();
			q.x = -a.x;
			q.y = -a.y;
			q.z = -a.z;
			q.w = a.w;
			return q;
		}
		
		/**
		 * Creates a Quaternion from a axis and a angle.
		 * 
		 * @param	x 	X-axis
		 * @param	y 	Y-axis
		 * @param	z 	Z-axis
		 * @param	angle	angle in radians.
		 * 
		 * @return
		 */
		public static function createFromAxisAngle( x:Number, y:Number, z:Number, angle:Number ):Quaternion
		{
			var q:Quaternion = new Quaternion();

			q.setFromAxisAngle(x, y, z, angle);
			
			return q;
		}
		
		/**
		 * Creates a Quaternion from Euler angles.
		 * 
		 * @param	ax	X-angle in radians.
		 * @param	ay	Y-angle in radians.
		 * @param	az	Z-angle in radians.
		 * 
		 * @return
		 */
		public static function createFromEuler( ax:Number, ay:Number, az:Number, useDegrees:Boolean = false ):Quaternion
		{
			if( useDegrees )
			{
				ax *= DEGTORAD;
				ay *= DEGTORAD;
				az *= DEGTORAD;
			}
			
			var fSinPitch       :Number = Math.sin( ax * 0.5 );
			var fCosPitch       :Number = Math.cos( ax * 0.5 );
			var fSinYaw         :Number = Math.sin( ay * 0.5 );
			var fCosYaw         :Number = Math.cos( ay * 0.5 );
			var fSinRoll        :Number = Math.sin( az * 0.5 );
			var fCosRoll        :Number = Math.cos( az * 0.5 );
			var fCosPitchCosYaw :Number = fCosPitch * fCosYaw;
			var fSinPitchSinYaw :Number = fSinPitch * fSinYaw;

			var q:Quaternion = new Quaternion();

			q.x = fSinRoll * fCosPitchCosYaw     - fCosRoll * fSinPitchSinYaw;
			q.y = fCosRoll * fSinPitch * fCosYaw + fSinRoll * fCosPitch * fSinYaw;
			q.z = fCosRoll * fCosPitch * fSinYaw - fSinRoll * fSinPitch * fCosYaw;
			q.w = fCosRoll * fCosPitchCosYaw     + fSinRoll * fSinPitchSinYaw;

			return q;
		}
				
		/**
		 * Creates a Quaternion from a matrix.
		 * 
		 * @param	matrix	a matrix. @see org.papervision3d.core.Matrix3D
		 * 
		 * @return	the created Quaternion
		 */
		public static function createFromMatrix( matrix:Matrix3D ):Quaternion
		{
			var quat:Quaternion = new Quaternion();
			
			var s:Number;
			var q:Array = new Array(4);
			var i:int, j:int, k:int;
			
			var tr:Number = matrix.n11 + matrix.n22 + matrix.n33;

			// check the diagonal
			if (tr > 0.0) 
			{
				s = Math.sqrt(tr + 1.0);
				quat.w = s / 2.0;
				s = 0.5 / s;
				
				quat.x = (matrix.n32 - matrix.n23) * s;
				quat.y = (matrix.n13 - matrix.n31) * s;
				quat.z = (matrix.n21 - matrix.n12) * s;
			} 
			else 
			{		
				// diagonal is negative
				var nxt:Array = [1, 2, 0];

				var m:Array = [
					[matrix.n11, matrix.n12, matrix.n13, matrix.n14],
					[matrix.n21, matrix.n22, matrix.n23, matrix.n24],
					[matrix.n31, matrix.n32, matrix.n33, matrix.n34]
				];
				
				i = 0;

				if (m[1][1] > m[0][0]) i = 1;
				if (m[2][2] > m[i][i]) i = 2;

				j = nxt[i];
				k = nxt[j];
				s = Math.sqrt((m[i][i] - (m[j][j] + m[k][k])) + 1.0);

				q[i] = s * 0.5;

				if (s != 0.0) s = 0.5 / s;

				q[3] = (m[k][j] - m[j][k]) * s;
				q[j] = (m[j][i] + m[i][j]) * s;
				q[k] = (m[k][i] + m[i][k]) * s;

				quat.x = q[0];
				quat.y = q[1];
				quat.z = q[2];
				quat.w = q[3];
			}
			return quat;
		}
		
		/**
		 * Creates a Quaternion from a orthonormal matrix.
		 * 
		 * @param	m	a orthonormal matrix. @see org.papervision3d.core.Matrix3D
		 * 
		 * @return  the created Quaternion
		 */
		public static function createFromOrthoMatrix( m:Matrix3D ):Quaternion
		{
			var q:Quaternion = new Quaternion();

			q.w = Math.sqrt( Math.max(0, 1 + m.n11 + m.n22 + m.n33) ) / 2;
			q.x = Math.sqrt( Math.max(0, 1 + m.n11 - m.n22 - m.n33) ) / 2;
			q.y = Math.sqrt( Math.max(0, 1 - m.n11 + m.n22 - m.n33) ) / 2;
			q.z = Math.sqrt( Math.max(0, 1 - m.n11 - m.n22 + m.n33) ) / 2;
			
			// recover signs
			q.x = m.n32 - m.n23 < 0 ? (q.x < 0 ? q.x : -q.x) : (q.x < 0 ? -q.x : q.x);
			q.y = m.n13 - m.n31 < 0 ? (q.y < 0 ? q.y : -q.y) : (q.y < 0 ? -q.y : q.y);
			q.z = m.n21 - m.n12 < 0 ? (q.z < 0 ? q.z : -q.z) : (q.z < 0 ? -q.z : q.z);

			return q;
		}
		
		/**
		 * Dot product.
		 * 
		 * @param	a
		 * @param	b
		 * 
		 * @return
		 */
		public static function dot( a:Quaternion, b:Quaternion ):Number
		{
			return (a.x * b.x) + (a.y * b.y) + (a.z * b.z) + (a.w * b.w);
		}
		
		/**
		 * Multiply.
		 * 
		 * @param	a
		 * @param	b
		 * @return
		 */
		public static function multiply( a:Quaternion, b:Quaternion ):Quaternion
		{
			var c:Quaternion = new Quaternion();
			c.x = a.w*b.x + a.x*b.w + a.y*b.z - a.z*b.y;
			c.y = a.w*b.y - a.x*b.z + a.y*b.w + a.z*b.x;
			c.z = a.w*b.z + a.x*b.y - a.y*b.x + a.z*b.w;
			c.w = a.w*b.w - a.x*b.x - a.y*b.y - a.z*b.z;
			return c;
		}
		
		/**
		 * Multiply by another Quaternion.
		 * 
		 * @param	b	The Quaternion to multiply by.
		 */
		public function mult( b:Quaternion ):void
		{
			var aw:Number = this.w,
				ax:Number = this.x,
				ay:Number = this.y,
				az:Number = this.z;
				
			x = aw*b.x + ax*b.w + ay*b.z - az*b.y;
			y = aw*b.y - ax*b.z + ay*b.w + az*b.x;
			z = aw*b.z + ax*b.y - ay*b.x + az*b.w;
			w = aw*b.w - ax*b.x - ay*b.y - az*b.z;
		}
		
		public function toString():String{
			return "Quaternion: x:"+this.x+" y:"+this.y+" z:"+this.z+" w:"+this.w;
		}
		
		/**
		 * Normalize.
		 * 
		 * @param	a
		 * 
		 * @return
		 */
		public function normalize():void
		{
			var len:Number = this.modulo;
			
			if( Math.abs(len) < EPSILON )
			{
				x = y = z = 0.0;
				w = 1.0;
			}
			else
			{
				var m:Number = 1 / len;
				x *= m;
				y *= m;
				z *= m;
				w *= m;
			}
		}

		/**
		 * SLERP (Spherical Linear intERPolation). @author Trevor Burton
		 * 
		 * @param	qa		start quaternion
		 * @param	qb		end quaternion
		 * @param	alpha	a value between 0 and 1
		 * 
		 * @return the interpolated quaternion.
		 */	
		public static function slerp( qa:Quaternion, qb:Quaternion, alpha:Number ):Quaternion
		{
			var angle:Number = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;
 
	         if (angle < 0.0)
	         {
	                 qa.x *= -1.0;
	                 qa.y *= -1.0;
	                 qa.z *= -1.0;
	                 qa.w *= -1.0;
	                 angle *= -1.0;
	         }
	 
	         var scale:Number;
	         var invscale:Number;
	 
	         if ((angle + 1.0) > EPSILON) // Take the shortest path
	         {
	                 if ((1.0 - angle) >= EPSILON)  // spherical interpolation
	                 {
	                         var theta:Number = Math.acos(angle);
	                         var invsintheta:Number = 1.0 / Math.sin(theta);
	                         scale = Math.sin(theta * (1.0-alpha)) * invsintheta;
	                         invscale = Math.sin(theta * alpha) * invsintheta;
	                 }
	                 else // linear interploation
	                 {
	                         scale = 1.0 - alpha;
	                         invscale = alpha;
	                 }
	         }
	         else // long way to go...
	         {
				 qb.y = -qa.y;
				 qb.x = qa.x;
				 qb.w = -qa.w;
				 qb.z = qa.z;

                 scale = Math.sin(Math.PI * (0.5 - alpha));
                 invscale = Math.sin(Math.PI * alpha);
	         }
	 
			return new Quaternion(  scale * qa.x + invscale * qb.x, 
									scale * qa.y + invscale * qb.y,
									scale * qa.z + invscale * qb.z,
									scale * qa.w + invscale * qb.w );
		}
		
		/**
		 * SLERP (Spherical Linear intERPolation).
		 * 
		 * @param	qa		start quaternion
		 * @param	qb		end quaternion
		 * @param	alpha	a value between 0 and 1
		 * 
		 * @return the interpolated quaternion.
		 */
		public static function slerpOld( qa:Quaternion, qb:Quaternion, alpha:Number ):Quaternion
		{
			var qm:Quaternion = new Quaternion();
			
			// Calculate angle between them.
			var cosHalfTheta:Number = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;

			// if qa=qb or qa=-qb then theta = 0 and we can return qa
			if(Math.abs(cosHalfTheta) >= 1.0)
			{
				qm.w = qa.w;
				qm.x = qa.x;
				qm.y = qa.y;
				qm.z = qa.z;
				return qm;
			}
			
			// Calculate temporary values.
			var halfTheta:Number = Math.acos(cosHalfTheta);
			var sinHalfTheta:Number = Math.sqrt(1.0 - cosHalfTheta*cosHalfTheta);
			
			// if theta = 180 degrees then result is not fully defined
			// we could rotate around any axis normal to qa or qb
			if(Math.abs(sinHalfTheta) < 0.001)
			{
				qm.w = (qa.w * 0.5 + qb.w * 0.5);
				qm.x = (qa.x * 0.5 + qb.x * 0.5);
				qm.y = (qa.y * 0.5 + qb.y * 0.5);
				qm.z = (qa.z * 0.5 + qb.z * 0.5);
				return qm;
			}
			
			var ratioA:Number = Math.sin((1 - alpha) * halfTheta) / sinHalfTheta;
			var ratioB:Number = Math.sin(alpha * halfTheta) / sinHalfTheta; 
			
			//calculate Quaternion.
			qm.w = (qa.w * ratioA + qb.w * ratioB);
			qm.x = (qa.x * ratioA + qb.x * ratioB);
			qm.y = (qa.y * ratioA + qb.y * ratioB);
			qm.z = (qa.z * ratioA + qb.z * ratioB);
			
			return qm;
		}
		
		public function toEuler():Number3D
		{
			var euler	:Number3D = new Number3D();
			var q1		:Quaternion = this;
			
			var test :Number = q1.x*q1.y + q1.z*q1.w;
			if (test > 0.499) { // singularity at north pole
				euler.x = 2 * Math.atan2(q1.x,q1.w);
				euler.y = Math.PI/2;
				euler.z = 0;
				return euler;
			}
			if (test < -0.499) { // singularity at south pole
				euler.x = -2 * Math.atan2(q1.x,q1.w);
				euler.y = - Math.PI/2;
				euler.z = 0;
				return euler;
			}
		    
		    var sqx	:Number = q1.x*q1.x;
		    var sqy	:Number = q1.y*q1.y;
		    var sqz	:Number = q1.z*q1.z;
		    
		    euler.x = Math.atan2(2*q1.y*q1.w-2*q1.x*q1.z , 1 - 2*sqy - 2*sqz);
			euler.y = Math.asin(2*test);
			euler.z = Math.atan2(2*q1.x*q1.w-2*q1.y*q1.z , 1 - 2*sqx - 2*sqz);
			
			return euler;
		}
		
		/**
		 * Gets the matrix representation of this Quaternion.
		 * 
		 * @return matrix. @see org.papervision3d.core.Matrix3D
		 */
		public function get matrix():Matrix3D
		{
			var xx:Number = x * x;
			var xy:Number = x * y;
			var xz:Number = x * z;
			var xw:Number = x * w;
			var yy:Number = y * y;
			var yz:Number = y * z;
			var yw:Number = y * w;
			var zz:Number = z * z;
			var zw:Number = z * w;

			_matrix.n11 = 1 - 2 * ( yy + zz );
			_matrix.n12 =     2 * ( xy - zw );
			_matrix.n13 =     2 * ( xz + yw );
			
			_matrix.n21 =     2 * ( xy + zw );
			_matrix.n22 = 1 - 2 * ( xx + zz );
			_matrix.n23 =     2 * ( yz - xw );
			
			_matrix.n31 =     2 * ( xz - yw );
			_matrix.n32 =     2 * ( yz + xw );
			_matrix.n33 = 1 - 2 * ( xx + yy );
			
			return _matrix;
		}
		
		public static function sub(a:Quaternion, b:Quaternion):Quaternion
		{
			return new Quaternion(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);	
		}
		
		public static function add(a:Quaternion, b:Quaternion):Quaternion
		{
			return new Quaternion(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);	
		}
	}
}
