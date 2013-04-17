package away3d.core.math
{
    /**
    * A point in 3D space.
    */
    public final class Number3D
    {
    	private const MathPI:Number = Math.PI;
    	private var mod:Number;
        private var dist:Number;
        private var num:Number3D;
        private var vx:Number;
        private var vy:Number;
        private var vz:Number;
        private var m1:MatrixAway3D;
        
        /**
        * The horizontal coordinate of the 3d number object.
        */ 
        public var x:Number;
    
        /**
        * The vertical coordinate of the 3d number object.
        */ 
        public var y:Number;
    
        /**
        * The depth coordinate of the 3d number object.
        */ 
        public var z:Number;
    	
    	/**
    	 * The modulo of the 3d number object.
    	 */
        public function get modulo():Number
        {
            return Math.sqrt(x*x + y*y + z*z);
        }
    	
    	/**
    	 * The squared modulo of the 3d number object.
    	 */
        public function get modulo2():Number
        {
            return x*x + y*y + z*z;
        }
        
		/**
		 * Creates a new <code>Number3D</code> object.
		 *
		 * @param	x	[optional]	A default value for the horizontal coordinate of the 3d number object. Defaults to 0.
		 * @param	y	[optional]	A default value for the vertical coordinate of the 3d number object. Defaults to 0.
		 * @param	z	[optional]	A default value for the depth coordinate of the 3d number object. Defaults to 0.
		 * @param	n	[optional]	Determines of the resulting 3d number object should be normalised. Defaults to false.
		 */
        public function Number3D(x:Number = 0, y:Number = 0, z:Number = 0, n:Boolean = false)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            
            if (n)
            	normalize();
        }
        
        
        /**
         * Compares the 3d number to another and returns a boolean indicating whether
         * they match or not.
         * 
         * @param v The 3d number to compare against.
         * 
         * @return Boolean indicating match.
        */
        public function equals(v:Number3D) : Boolean
        {
        	return (v.x==x && v.y==y && v.z==z);
        }
        
		
		/**
		 * Duplicates the 3d number's properties to another <code>Number3D</code> object
		 * 
		 * @return	The new 3d number instance with duplicated properties applied
		 */
        public function clone(v:Number3D):void
        {
            x = v.x;
            y = v.y;
            z = v.z;
        }
		
    	/**
    	 * Fills the 3d number object with scaled values from the given 3d number.
    	 * 
    	 * @param	v	The 3d number object used for the scaling calculation.
    	 * @param	s	The scaling value.
    	 */
        public function scale(v:Number3D, s:Number):void
        {
            x = v.x * s;
            y = v.y * s;
            z = v.z * s;
        }  
		
    	/**
    	 * Fills the 3d number object with the result from an addition of two 3d numbers.
    	 * 
    	 * @param	v	The first 3d number in the addition.
    	 * @param	w	The second 3d number in the addition.
    	 */
        public function add(v:Number3D, w:Number3D):void
        {
            x = v.x + w.x;
            y = v.y + w.y;
            z = v.z + w.z;
        }
		
    	/**
    	 * Fills the 3d number object with the result from a subtraction of two 3d numbers.
    	 * 
    	 * @param	v	The starting 3d number in the subtraction.
    	 * @param	w	The subtracting 3d number in the subtraction.
    	 */
        public function sub(v:Number3D, w:Number3D):void
        {
            x = v.x - w.x;
            y = v.y - w.y;
            z = v.z - w.z;
        }
    	
    	/**
    	 * Calculates the distance from the 3d number object to the given 3d number.
    	 * 
    	 * @param	w	The 3d number object whose distance is calculated.
    	 */
        public function distance(w:Number3D):Number
        {
            return Math.sqrt((x - w.x)*(x - w.x) + (y - w.y)*(y - w.y) + (z - w.z)*(z - w.z));
        }
    	
    	/**
    	 * Calculates the dot product of the 3d number object with the given 3d number.
    	 * 
    	 * @param	w	The 3d number object to use in the calculation.
    	 * @return		The dot product result.
    	 */
        public function dot(w:Number3D):Number
        {
            return (x * w.x + y * w.y + z * w.z);
        }
		
    	/**
    	 * Fills the 3d number object with the result from an cross product of two 3d numbers.
    	 * 
    	 * @param	v	The first 3d number in the cross product calculation.
    	 * @param	w	The second 3d number in the cross product calculation.
    	 */
        public function cross(v:Number3D, w:Number3D):void
        {
        	if (this == v || this == w)
        		throw new Error("resultant cross product cannot be the same instance as an input");
        	x = w.y * v.z - w.z * v.y;
        	y = w.z * v.x - w.x * v.z;
        	z = w.x * v.y - w.y * v.x;
        }
    	
    	/**
    	 * Returns the angle in radians made between the 3d number obejct and the given 3d number.
    	 * 
    	 * @param	w	[optional]	The 3d number object to use in the calculation.
    	 * @return					An angle in radians representing the angle between the two 3d number objects. 
    	 */
        public function getAngle(w:Number3D = null):Number
        {
            if (w == null)
            	w = new Number3D();
            return Math.acos(dot(w)/(modulo*w.modulo));
        }
        
        /**
        * Normalises the 3d number object.
        * @param	val	[optional]	A normalisation coefficient representing the length of the resulting 3d number object. Defaults to 1.
        */
        public function normalize(val:Number = 1):void
        {
            mod = modulo/val;
    
            if (mod != 0 && mod != 1)
            {
                x /= mod;
                y /= mod;
                z /= mod;
            }
        }
    	
    	/**
    	 * Fills the 3d number object with the result of a 3d matrix rotation performed on a 3d number.
    	 * 
    	 * @param	v	The 3d number object to use in the calculation.
    	 * @param	m	The 3d matrix object representing the rotation.
    	 */
        public function rotate(v:Number3D, m:MatrixAway3D):void
        {
        	vx = v.x;
        	vy = v.y;
        	vz = v.z;
        	
            x = vx * m.sxx + vy * m.sxy + vz * m.sxz;
            y = vx * m.syx + vy * m.syy + vz * m.syz;
            z = vx * m.szx + vy * m.szy + vz * m.szz;
        }
    	
    	/**
    	 * Fills the 3d number object with the result of a 3d matrix tranformation performed on a 3d number.
    	 * 
    	 * @param	v	The 3d number object to use in the calculation.
    	 * @param	m	The 3d matrix object representing the tranformation.
    	 */
        public function transform(v:Number3D, m:MatrixAway3D):void
        {
        	vx = v.x;
        	vy = v.y;
        	vz = v.z;
        	
            x = vx * m.sxx + vy * m.sxy + vz * m.sxz + m.tx;
            y = vx * m.syx + vy * m.syy + vz * m.syz + m.ty;
            z = vx * m.szx + vy * m.szy + vz * m.szz + m.tz;
        }
            	
    	/**
    	 * Fill the 3d number object with the euler angles represented by the 3x3 matrix rotation.
    	 * 
    	 * @param	m	The 3d matrix object to use in the calculation.
    	 */
        public function matrix2euler(m:MatrixAway3D):void
        {
            if (!m1)
            	m1 = new MatrixAway3D();
            
		    // Extract the first angle, rotationX
			x = -Math.atan2(m.szy, m.szz); // rot.x = Math<T>::atan2 (M[1][2], M[2][2]);
			
			// Remove the rotationX rotation from m1, so that the remaining
			// rotation, m2 is only around two axes, and gimbal lock cannot occur.
			m1.rotationMatrix(1, 0, 0,  x);
			m1.multiply(m, m1);
			
			// Extract the other two angles, rot.y and rot.z, from m1.
			var cy:Number = Math.sqrt(m1.sxx*m1.sxx + m1.syx*m1.syx); // T cy = Math<T>::sqrt (N[0][0]*N[0][0] + N[0][1]*N[0][1]);
			
			y = Math.atan2(-m1.szx, cy); // rot.y = Math<T>::atan2 (-N[0][2], cy);
			z = Math.atan2(-m1.sxy, m1.syy); //rot.z = Math<T>::atan2 (-N[1][0], N[1][1]);
			
			// Fix angles
			if(Math.round(z/MathPI) == 1) {
				if(y > 0)
					y = -(y - MathPI);
				else
					y = -(y + MathPI);
	
				z -= MathPI;
				
				if (x > 0)
					x -= MathPI;
				else
					x += MathPI;
			} else if(Math.round(z/MathPI) == -1) {
				if(y > 0)
					y = -(y - MathPI);
				else
					y = -(y + MathPI);
	
				z += MathPI;
				
				if (x > 0)
					x -= MathPI;
				else
					x += MathPI;
			} else if(Math.round(x/MathPI) == 1) {
				if(y > 0)
					y = -(y - MathPI);
				else
					y = -(y + MathPI);
	
				x -= MathPI;
				
				if (z > 0)
					z -= MathPI;
				else
					z += MathPI;
			} else if(Math.round(x/MathPI) == -1) {
				if(y > 0)
					y = -(y - MathPI);
				else
					y = -(y + MathPI);
	
				x += MathPI;
				
				if (z > 0)
					z -= MathPI;
				else
					z += MathPI;
			}
        }
        
        public function quaternion2euler(quarternion:Quaternion):void
		{
			
			var test :Number = quarternion.x*quarternion.y + quarternion.z*quarternion.w;
			if (test > 0.499) { // singularity at north pole
				x = 2 * Math.atan2(quarternion.x,quarternion.w);
				y = Math.PI/2;
				z = 0;
				return;
			}
			if (test < -0.499) { // singularity at south pole
				x = -2 * Math.atan2(quarternion.x,quarternion.w);
				y = - Math.PI/2;
				z = 0;
				return;
			}
		    
		    var sqx	:Number = quarternion.x*quarternion.x;
		    var sqy	:Number = quarternion.y*quarternion.y;
		    var sqz	:Number = quarternion.z*quarternion.z;
		    
		    x = Math.atan2(2*quarternion.y*quarternion.w - 2*quarternion.x*quarternion.z , 1 - 2*sqy - 2*sqz);
			y = Math.asin(2*test);
			z = Math.atan2(2*quarternion.x*quarternion.w-2*quarternion.y*quarternion.z , 1 - 2*sqx - 2*sqz);
		}
		
    	/**
    	 * Fill the 3d number object with the scale values represented by the 3x3 matrix.
    	 * 
    	 * @param	m	The 3d matrix object to use in the calculation.
    	 */
        public function matrix2scale(m:MatrixAway3D):void
        {
            x = Math.sqrt(m.sxx*m.sxx + m.syx*m.syx + m.szx*m.szx);
            y = Math.sqrt(m.sxy*m.sxy + m.syy*m.syy + m.szy*m.szy);
            z = Math.sqrt(m.sxz*m.sxz + m.syz*m.syz + m.szz*m.szz);
        }
        
        /**
         * Fills the 3d number object with values representing a point between the current and the
         * 3d number specified in parameter v. The f parameter defines the degree of interpolation 
         * between the two endpoints, where 0 represents the unmodified current values, and 1.0 
         * those of the v parameter.
         * 
         * @param w The target point.
         * @param f The level of interpolation between the current 3d number and the parameter v. 
         * 
         * @see flash.geom.Point.interpolate()
        */
        public function interpolate(w:Number3D, f:Number):void
        {
        	var d:Number3D = new Number3D;
        	
        	d.sub(w, this);
        	d.scale(d, f);
        	add(this, d);
        }
        
        /**
         * Returns a 3d number object representing a point between the two 3d number parameters w 
         * and v. The f parameter defines the degree of interpolation between the two ednpoints, 
         * where 0 or 1 will return 3d number objects equal to v and w respectively.
         * 
         * @param w The target point.
         * @param v The zero point.
         * @param f The level of interpolation where 0.0 will return a 3d number object equal to v,
         * and 1.0 will return a 3d number object equal to w.
         * 
         * @see flash.geom.Point.interpolate()
        */
        public static function getInterpolated(w:Number3D, v:Number3D, f:Number):Number3D
        {
        	var d:Number3D = new Number3D;
        	
        	d.sub(w, v);
        	d.scale(d, f);
        	d.add(d, v);
        	
        	return d;
        }
        
        
        /**
        * A 3d number object representing a relative direction forward.
        */
        public static var FORWARD :Number3D = new Number3D( 0,  0,  1);
        
        /**
        * A 3d number object representing a relative direction backward.
        */
        public static var BACKWARD:Number3D = new Number3D( 0,  0, -1);
        
        /**
        * A 3d number object representing a relative direction left.
        */
        public static var LEFT    :Number3D = new Number3D(-1,  0,  0);
        
        /**
        * A 3d number object representing a relative direction right.
        */
        public static var RIGHT   :Number3D = new Number3D( 1,  0,  0);
        
        /**
        * A 3d number object representing a relative direction up.
        */
        public static var UP      :Number3D = new Number3D( 0,  1,  0);
        
        /**
        * A 3d number object representing a relative direction down.
        */
        public static var DOWN    :Number3D = new Number3D( 0, -1,  0);
        
        /**
        * Calculates a 3d number object representing the closest point on a given plane to a given 3d point.
        * 
        * @param	p	The 3d point used in teh calculation.
        * @param	k	The plane offset used in the calculation.
        * @param	n	The plane normal used in the calculation.
        * @return		The resulting 3d point.
        */
        public function closestPointOnPlane(p:Number3D, k:Number3D, n:Number3D):Number3D
        {
        	if (!num)
        		num = new Number3D();
        	
        	num.sub(p, k);
            dist = n.dot(num);
            num.scale(n, dist);
            num.sub(p, num);
            return num;
        }
		
		/**
		 * Used to trace the values of a 3d number.
		 * 
		 * @return A string representation of the 3d number object.
		 */
        public function toString(): String
        {
            return 'x:' + x + ' y:' + y + ' z:' + z;
        }
    }
}