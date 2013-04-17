package away3d.core.math
{
	import flash.geom.Matrix;
	
    /**
    * A 3D transformation 4x4 matrix
    */
    public final class MatrixAway3D
    {
        private const toDEGREES:Number = 180 / Math.PI;
        private var _position:Number3D = new Number3D();
        
        //vectors
        private var _forward:Number3D = new Number3D();
        private var _up:Number3D = new Number3D();
        private var _right:Number3D = new Number3D();
        
		private var m111:Number, m211:Number;
        private var m121:Number, m221:Number;
        private var m131:Number, m231:Number;
        private var m112:Number, m212:Number;
        private var m122:Number, m222:Number;
        private var m132:Number, m232:Number;
        private var m113:Number, m213:Number;
        private var m123:Number, m223:Number;
        private var m133:Number, m233:Number;
        private var m114:Number, m214:Number;
        private var m124:Number, m224:Number;
        private var m134:Number, m234:Number;
        private var m141:Number, m241:Number;
        private var m142:Number, m242:Number;
        private var m143:Number, m243:Number;
        private var m144:Number, m244:Number;
        
    	private var nCos:Number;
        private var nSin:Number;
        private var scos:Number;
    
        private var suv:Number;
        private var svw:Number;
        private var suw:Number;
        private var sw:Number;
        private var sv:Number;
        private var su:Number;
        
        private var d:Number;
        
        private var x:Number;
        private var y:Number;
        private var z:Number;
        private var w:Number;
        
    	private var xx:Number;
        private var xy:Number;
        private var xz:Number;
        private var xw:Number;
    
        private var yy:Number;
        private var yz:Number;
        private var yw:Number;
    
        private var zz:Number;
        private var zw:Number;
        
        
    	/**
    	 * The value in the first row and first column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var sxx:Number = 1;
        
    	/**
    	 * The value in the first row and second column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var sxy:Number = 0;
        
    	/**
    	 * The value in the first row and third column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var sxz:Number = 0;
        
    	/**
    	 * The value in the first row and forth column of the Matrix object,
    	 * which affects the positioning along the x axis of a 3d object.
    	 */
        public var tx:Number = 0;
        
    	/**
    	 * The value in the second row and first column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var syx:Number = 0;
        
    	/**
    	 * The value in the second row and second column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var syy:Number = 1;
        
    	/**
    	 * The value in the second row and third column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var syz:Number = 0;
        
    	/**
    	 * The value in the second row and fourth column of the Matrix object,
    	 * which affects the positioning along the y axis of a 3d object.
    	 */
        public var ty:Number = 0;
        
    	/**
    	 * The value in the third row and first column of the Matrix object,
    	 * which affects the rotation and scaling of a 3d object.
    	 */
        public var szx:Number = 0;
        
    	/**
    	 * The value in the third row and second column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var szy:Number = 0;
                
    	/**
    	 * The value in the third row and third column of the Matrix object,
    	 * which affect the rotation and scaling of a 3d object.
    	 */
        public var szz:Number = 1;
        
    	/**
    	 * The value in the third row and fourth column of the Matrix object,
    	 * which affects the positioning along the z axis of a 3d object.
    	 */
        public var tz:Number = 0;

    	/**
    	 * The value in the 4th row and first column of the Matrix object,
    	 * --
    	 */
		public var swx:Number = 0;
		
		 /**
    	 * The value in the 4th row and second column of the Matrix object,
    	 * --
    	 */
		public var swy:Number = 0;
		
		 /**
    	 * The value in the 4th row and third column of the Matrix object,
    	 * --
    	 */
		public var swz:Number = 0;
		
		/**
    	 * The value in the 4th row and 4th column of the Matrix object,
    	 * --
    	 */
		public var tw:Number = 1;
		
		
        /**
        * Returns a 3d number representing the translation imposed by the 3dmatrix.
        */
        public function get position():Number3D
        {
        	_position.x = tx;
        	_position.y = ty;
        	_position.z = tz;
        	
            return _position;
        }
        
    	/**
    	 * Returns the 3d matrix object's determinant.
    	 * 
    	 * @return	The determinant of the 3d matrix.
    	 */
        public function get det():Number
        {
            return  (sxx * syy - syx * sxy) * szz 
                  - (sxx * szy - szx * sxy) * syz 
                  + (syx * szy - szx * syy) * sxz;
        }
        
     	public function get det4x4():Number
		{
			return (sxx * syy - syx * sxy) * (szz * tw - swz * tz)
			- (sxx * szy - szx * sxy) * (syz * tw - swz * ty)
			+ (sxx * swy - swx * sxy) * (syz * tz - szz * ty)
			+ (syx * szy - szx * syy) * (sxz * tw - swz * tx)
			- (syx * swy - swx * syy) * (sxz * tz - szz * tx)
			+ (szx * swy - swx * szy) * (sxz * ty - syz * tx);
		}
		
		/**
		 * Creates a new <code>MatrixAway3D</code> object.
		 */
        public function MatrixAway3D()
        {
        }
		
		/**
		 * Fills the 3d matrix object with values from an array with 3d matrix values
		 * ordered from right to left and up to down.
		 */
        public function array2matrix(ar:Array, yUp:Boolean, scaling:Number):void
        {
            if (ar.length >= 12)
            {
            	if (yUp) {
            		
	                sxx = ar[0];
	                sxy = -ar[1];
	                sxz = -ar[2];
	                tx = -ar[3]*scaling;
	                syx = -ar[4];
	                syy = ar[5];
	                syz = ar[6];
	                ty = ar[7]*scaling;
	                szx = -ar[8];
	                szy = ar[9];
	                szz = ar[10];
	                tz = ar[11]*scaling;
            	} else {
            		sxx = ar[0];
	                sxz = ar[1];
	                sxy = ar[2];
	                tx = ar[3]*scaling;
	                szx = ar[4];
	                szz = ar[5];
	                szy = ar[6];
	                tz = ar[7]*scaling;
	                syx = ar[8];
	                syz = ar[9];
	                syy = ar[10];
	                ty = ar[11]*scaling;
            	}
            }
            if(ar.length >= 16)
            {               
            	swx = ar[12];
                swy = ar[13];
                swz = ar[14];
                tw =  ar[15];
            }
            else
            {
            	swx = swy = swz = 0;
            	tw = 1;
            }
        }
		
		/**
		 * Used to trace the values of a 3d matrix.
		 * 
		 * @return A string representation of the 3d matrix object.
		 */
        public function toString(): String
        {
            var s:String = "";
        
            s += int(sxx*1000) / 1000 + "\t\t" + int(sxy*1000) / 1000 + "\t\t" + int(sxz*1000) / 1000 + "\t\t" + int(tx*1000) / 1000 + "\n";
            s += int(syx*1000) / 1000 + "\t\t" + int(syy*1000) / 1000 + "\t\t" + int(syz*1000) / 1000 + "\t\t" + int(ty*1000) / 1000 + "\n";
            s += int(szx*1000) / 1000 + "\t\t" + int(szy*1000) / 1000 + "\t\t" + int(szz*1000) / 1000 + "\t\t" + int(tz*1000) / 1000 + "\n";
         	s += int(swx*1000) / 1000 + "\t\t" + int(swy*1000) / 1000 + "\t\t" + int(swz*1000) / 1000 + "\t\t" + int(tw*1000) / 1000 + "\n";
        
            return s;
        }
        
        /**
        * Fills the 3d matrix object with the result from a 3x3 multipication of two 3d matrix objects.
        * The translation values are taken from the first matrix.
        * 
        * @param	m1	The first 3d matrix in the multipication.
        * @oaram	m2	The second 3d matrix in the multipication.
        */
        public function multiply3x3(m1:MatrixAway3D, m2:MatrixAway3D):void
        {
            m111 = m1.sxx; m211 = m2.sxx;
            m121 = m1.syx; m221 = m2.syx;
            m131 = m1.szx; m231 = m2.szx;
            m112 = m1.sxy; m212 = m2.sxy;
            m122 = m1.syy; m222 = m2.syy;
            m132 = m1.szy; m232 = m2.szy;
            m113 = m1.sxz; m213 = m2.sxz;
            m123 = m1.syz; m223 = m2.syz;
            m133 = m1.szz; m233 = m2.szz;
        
            sxx = m111 * m211 + m112 * m221 + m113 * m231;
            sxy = m111 * m212 + m112 * m222 + m113 * m232;
            sxz = m111 * m213 + m112 * m223 + m113 * m233;
        
            syx = m121 * m211 + m122 * m221 + m123 * m231;
            syy = m121 * m212 + m122 * m222 + m123 * m232;
            syz = m121 * m213 + m122 * m223 + m123 * m233;
        
            szx = m131 * m211 + m132 * m221 + m133 * m231;
            szy = m131 * m212 + m132 * m222 + m133 * m232;
            szz = m131 * m213 + m132 * m223 + m133 * m233;
        
            tx = m1.tx;
            ty = m1.ty;
            tz = m1.tz;
        }
        
        /**
        * Fills the 3d matrix object with the result from a 4x3 multipication of two 3d matrix objects.
        * 
        * @param	m1	The first 3d matrix in the multipication.
        * @oaram	m2	The second 3d matrix in the multipication.
        */
        public function multiply4x3(m1:MatrixAway3D, m2:MatrixAway3D):void
        {
            m111 = m1.sxx; m211 = m2.sxx;
            m121 = m1.syx; m221 = m2.syx;
            m131 = m1.szx; m231 = m2.szx;
            m112 = m1.sxy; m212 = m2.sxy;
            m122 = m1.syy; m222 = m2.syy;
            m132 = m1.szy; m232 = m2.szy;
            m113 = m1.sxz; m213 = m2.sxz;
            m123 = m1.syz; m223 = m2.syz;
            m133 = m1.szz; m233 = m2.szz;
            m114 = m1.tx; m214 = m2.tx;
            m124 = m1.ty; m224 = m2.ty;
            m134 = m1.tz; m234 = m2.tz;
        	m141 = m1.swx; m241 = m2.swx;
        	m142 = m1.swy; m242 = m2.swy;
        	m143 = m1.swz; m243 = m2.swz;
        	m144 = m1.tw; m244 = m2.tw;
        	
            sxx = m111 * m211 + m112 * m221 + m113 * m231;
            sxy = m111 * m212 + m112 * m222 + m113 * m232;
            sxz = m111 * m213 + m112 * m223 + m113 * m233;
            tx = m111 * m214 + m112 * m224 + m113 * m234 + m114;
        
            syx = m121 * m211 + m122 * m221 + m123 * m231;
            syy = m121 * m212 + m122 * m222 + m123 * m232;
            syz = m121 * m213 + m122 * m223 + m123 * m233;
            ty = m121 * m214 + m122 * m224 + m123 * m234 + m124;
        
            szx = m131 * m211 + m132 * m221 + m133 * m231;
            szy = m131 * m212 + m132 * m222 + m133 * m232;
            szz = m131 * m213 + m132 * m223 + m133 * m233;
            tz = m131 * m214 + m132 * m224 + m133 * m234 + m134;
            
            swx = m141 * m211 + m142 * m221 + m143 * m231;
            swy = m141 * m212 + m142 * m222 + m143 * m232;
            swz = m141 * m213 + m142 * m223 + m143 * m233;
            tw =  m141 * m214 + m142 * m224 + m143 * m234 + m144;
        }

       /**
        * Fills the 3d matrix object with the result from a 4x4 multipication of two 3d matrix objects.
        * 
        * @param	m1	The first 3d matrix in the multipication.
        * @oaram	m2	The second 3d matrix in the multipication.
        */
        public function multiply4x4(m1:MatrixAway3D, m2:MatrixAway3D):void
        {
            m111 = m1.sxx; m211 = m2.sxx;
            m121 = m1.syx; m221 = m2.syx;
            m131 = m1.szx; m231 = m2.szx;
            m141 = m1.swx; m241 = m2.swx;
        	
            m112 = m1.sxy; m212 = m2.sxy;
            m122 = m1.syy; m222 = m2.syy;
            m132 = m1.szy; m232 = m2.szy;
            m142 = m1.swy; m242 = m2.swy;
        	
            m113 = m1.sxz; m213 = m2.sxz;
            m123 = m1.syz; m223 = m2.syz;
            m133 = m1.szz; m233 = m2.szz;
            m143 = m1.swz; m243 = m2.swz;
        	
            m114 = m1.tx; m214 = m2.tx;
            m124 = m1.ty; m224 = m2.ty;
            m134 = m1.tz; m234 = m2.tz;
        	m144 = m1.tw; m244 = m2.tw;
        	
			sxx = m111 * m211 + m112 * m221 + m113 * m231 + m114 * m241;
			sxy = m111 * m212 + m112 * m222 + m113 * m232 + m114 * m242;
			sxz = m111 * m213 + m112 * m223 + m113 * m233 + m114 * m243;
			 tx = m111 * m214 + m112 * m224 + m113 * m234 + m114 * m244;

			syx = m121 * m211 + m122 * m221 + m123 * m231 + m124 * m241;
			syy = m121 * m212 + m122 * m222 + m123 * m232 + m124 * m242;
			syz = m121 * m213 + m122 * m223 + m123 * m233 + m124 * m243;
			 ty = m121 * m214 + m122 * m224 + m123 * m234 + m124 * m244;

			szx = m131 * m211 + m132 * m221 + m133 * m231 + m134 * m241;
			szy = m131 * m212 + m132 * m222 + m133 * m232 + m134 * m242;
			szz = m131 * m213 + m132 * m223 + m133 * m233 + m134 * m243;
			 tz = m131 * m214 + m132 * m224 + m133 * m234 + m134 * m244;

			swx = m141 * m211 + m142 * m221 + m143 * m231 + m144 * m241;
			swy = m141 * m212 + m142 * m222 + m143 * m232 + m144 * m242;
			swz = m141 * m213 + m142 * m223 + m143 * m233 + m144 * m243;
			 tw = m141 * m214 + m142 * m224 + m143 * m234 + m144 * m244;
        }
              
         /**
        * Fills the 3d matrix object with the result from a 3x4 multipication of two 3d matrix objects.
        * 
        * @param	m1	The first 3d matrix in the multipication.
        * @oaram	m2	The second 3d matrix in the multipication.
        */
        public function multiply(m1:MatrixAway3D, m2:MatrixAway3D):void
        {
            m111 = m1.sxx; m211 = m2.sxx;
            m121 = m1.syx; m221 = m2.syx;
            m131 = m1.szx; m231 = m2.szx;
            m112 = m1.sxy; m212 = m2.sxy;
            m122 = m1.syy; m222 = m2.syy;
            m132 = m1.szy; m232 = m2.szy;
            m113 = m1.sxz; m213 = m2.sxz;
            m123 = m1.syz; m223 = m2.syz;
            m133 = m1.szz; m233 = m2.szz;
            m114 = m1.tx; m214 = m2.tx;
            m124 = m1.ty; m224 = m2.ty;
            m134 = m1.tz; m234 = m2.tz;
        
            sxx = m111 * m211 + m112 * m221 + m113 * m231;
            sxy = m111 * m212 + m112 * m222 + m113 * m232;
            sxz = m111 * m213 + m112 * m223 + m113 * m233;
            tx = m111 * m214 + m112 * m224 + m113 * m234 + m114;
        
            syx = m121 * m211 + m122 * m221 + m123 * m231;
            syy = m121 * m212 + m122 * m222 + m123 * m232;
            syz = m121 * m213 + m122 * m223 + m123 * m233;
            ty = m121 * m214 + m122 * m224 + m123 * m234 + m124;
        
            szx = m131 * m211 + m132 * m221 + m133 * m231;
            szy = m131 * m212 + m132 * m222 + m133 * m232;
            szz = m131 * m213 + m132 * m223 + m133 * m233;
            tz = m131 * m214 + m132 * m224 + m133 * m234 + m134;

        }
    	
    	/**
    	 * Scales the 3d matrix by the given amount in each dimension
    	 * 
		 * @param	m	The 3d matrix to scale from.
    	 * @param	x	The scale value along the x axis.
    	 * @param	y	The scale value along the y axis.
    	 * @param	z	The scale value along the z axis.
    	 */
        public function scale(m1:MatrixAway3D, x:Number, y:Number, z:Number):void
        {
            sxx = m1.sxx*x;
            syx = m1.syx*x;
            szx = m1.szx*x;
            sxy = m1.sxy*y;
            syy = m1.syy*y;
            szy = m1.szy*y;
            sxz = m1.sxz*z;
            syz = m1.syz*z;
            szz = m1.szz*z;
        }
		
		/**
		 * Fill the 3d matrix with the 3x3 rotation matrix section of the given 3d matrix.
		 * 
		 * @param	m	The 3d matrix to copy from.
		 */
        public function copy3x3(m:MatrixAway3D):MatrixAway3D
        {
            sxx = m.sxx;   sxy = m.sxy;   sxz = m.sxz;
            syx = m.syx;   syy = m.syy;   syz = m.syz;
            szx = m.szx;   szy = m.szy;   szz = m.szz;
    
            return this;
        }
		
		/**
		 * Fill the 3d matrix with all matrix values of the given 3d matrix.
		 * 
		 * @param	m	The 3d matrix to copy from.
		 */
        public function clone(m:MatrixAway3D):MatrixAway3D
        {
            sxx = m.sxx;   sxy = m.sxy;   sxz = m.sxz;   tx = m.tx;
            syx = m.syx;   syy = m.syy;   syz = m.syz;   ty = m.ty;
            szx = m.szx;   szy = m.szy;   szz = m.szz;   tz = m.tz;
            swx = m.swx;   swy = m.swy;   swz = m.swz;   tw = m.tw;
                            
            return m;
        }
    	
    	/**
    	 * Fills the 3d matrix object with values representing the given rotation around a vector.
    	 * 
    	 * @param	u		The x value of the rotation vector.
    	 * @param	v		The y value of the rotation vector.
    	 * @param	w		The z value of the rotation vector.
    	 * @param	angle	The angle in radians of the rotation.
    	 */
        public function rotationMatrix(u:Number, v:Number, w:Number, angle:Number):void
        {
            nCos = Math.cos(angle);
            nSin = Math.sin(angle);
            scos = 1 - nCos;
    
            suv = u * v * scos;
            svw = v * w * scos;
            suw = u * w * scos;
            sw = nSin * w;
            sv = nSin * v;
            su = nSin * u;
    
            sxx =  nCos + u * u * scos;       // nCos + u*u*(1-nCos)
            sxy = -sw   + suv;                                                         // -nSin * w
            sxz =  sv   + suw;                                                         // -nSin * v
    
            syx =  sw   + suv;                // nSin*w + u*v*(1-nCos)
            syy =  nCos + v * v * scos;
            syz = -su   + svw;
    
            szx = -sv   + suw;                // -nSin*v + u*w*(1-nCos)
            szy =  su   + svw;
            szz =  nCos + w * w * scos;
        }
    	
    	/**
    	 * Fills the 3d matrix object with values representing the given translation.
    	 * 
    	 * @param	u		The translation along the x axis.
    	 * @param	v		The translation along the y axis.
    	 * @param	w		The translation along the z axis..
    	 */
        public function translationMatrix(u:Number, v:Number, w:Number):void
        {
        	sxx = syy = szz = 1;
        	sxy = sxz = syz = syz = szx = szy = 0;
        	
            tx = u;
            ty = v;
            tz = w;
        }
    	
    	/**
    	 * Fills the 3d matrix object with values representing the given scaling.
    	 * 
    	 * @param	u		The scale along the x axis.
    	 * @param	v		The scale along the y axis.
    	 * @param	w		The scale along the z axis..
    	 */
        public function scaleMatrix(u:Number, v:Number, w:Number):void
        {
        	tx = sxy = sxz = 0;
        	syz = ty = syz = 0;
        	szx = szy = tz = 0;
        	
            sxx = u;
            syy = v;
            szz = w;
        }
    	/**
    	 * Clears the 3d matrix object and fills it with the identity matrix.
    	 */
        public function clear():void
        {
        	tx = sxy = sxz = syz = ty = syz = szx = szy = tz = 0;
            sxx = syy = szz = 1;
        }
        
        public function compare(m:MatrixAway3D):Boolean
        {
        	if (sxx != m.sxx || sxy != m.sxy || sxz != m.sxz || tx != m.tx || syx != m.syx || syy != m.syy || syz != m.syz || ty != m.ty || szx != m.szx || szy != m.szy || szz != m.szz || tz != m.tz)
        		return false;
        	
        	return true;
        }
        
        /**
         * Fills the 3d matrix with a 4x4 transformation that produces a perspective projection.
		 * 
		 * @param	fov
		 * @param	aspect
		 * @param	near
		 * @param	far
		 * @return
		 */
		public function perspectiveProjectionMatrix( fov:Number, aspect:Number, near:Number, far:Number ):void
		{
			var fov2:Number = (fov/2) * (Math.PI/180);
			var tan:Number = Math.tan(fov2);
			var f:Number = 1 / tan;
			
			sxx = f/aspect;
			sxy = sxz = tx = 0;
			syy = f;
			syx = syz = ty = 0;
			szx = szy = 0;
			//negate for left hand
			szz = -((near+far)/(near-far));
			tz = (2*far*near)/(near-far);
			swx = swy = tw = 0;
			swz = 1;
		}	
        
        
		/**
		 * Fills the 3d matrix with a 4x4 transformation that produces an orthographic projection.
		 * 
		 * @param	left
		 * @param	right
		 * @param	bottom
		 * @param	top
		 * @param	near
		 * @param	far
		 * @return
		 */
		public function orthographicProjectionMatrix( left:Number, right:Number, bottom:Number, top:Number, near:Number, far:Number):void
		{
			sxx = 2/(right-left);
			sxy = sxz = 0;
			tx = (right+left)/(right-left);
			
			
			syy = 2/(top-bottom);
			syx = syz = 0;
			ty = (top+bottom)/(top-bottom);
			
			szx = szy = 0;
			szz = -2/(far-near);
			tz = (far+near)/(far-near);
			
			swx = swy = swz = 0;
			tw = 1;
			
			//go to left handed
			var flipY:MatrixAway3D = new MatrixAway3D();
			flipY.scaleMatrix(1,1,-1);
			
			this.multiply(flipY, this);
		}
		
        
        
        /**
        * Fills the 3d matrix object with the result from the inverse 3x3 calulation of the given 3d matrix.
        * 
        * @param	m	The 3d matrix object used for the inverse calulation.
        */
        public function inverse(m:MatrixAway3D):void
        {
            d = m.det;
            if (Math.abs(d) < 0.001) {
                // Determinant zero, there's no inverse
                return;
            }
    
            d = 1 / d;
    
            m111 = m.sxx; m121 = m.syx; m131 = m.szx;
            m112 = m.sxy; m122 = m.syy; m132 = m.szy;
            m113 = m.sxz; m123 = m.syz; m133 = m.szz;
            m114 = m.tx;  m124 = m.ty;  m134 = m.tz;
            
            sxx = d * (m122 * m133 - m132 * m123),
            sxy = -d* (m112 * m133 - m132 * m113),
            sxz = d * (m112 * m123 - m122 * m113),
            tx = -d* (m112 * (m123*m134 - m133*m124) - m122 * (m113*m134 - m133*m114) + m132 * (m113*m124 - m123*m114)),
            syx = -d* (m121 * m133 - m131 * m123),
            syy = d * (m111 * m133 - m131 * m113),
            syz = -d* (m111 * m123 - m121 * m113),
            ty = d * (m111 * (m123*m134 - m133*m124) - m121 * (m113*m134 - m133*m114) + m131 * (m113*m124 - m123*m114)),
            szx = d * (m121 * m132 - m131 * m122),
            szy = -d* (m111 * m132 - m131 * m112),
            szz = d * (m111 * m122 - m121 * m112),
            tz = -d* (m111 * (m122*m134 - m132*m124) - m121 * (m112*m134 - m132*m114) + m131 * (m112*m124 - m122*m114));
        }
       
       /**
        * Fills the 3d matrix object with the result from the inverse 4x4 calulation of the given 3d matrix.
        * 
        * @param	m	The 3d matrix object used for the inverse calulation.
        */
        public function inverse4x4(m:MatrixAway3D):void
        {
            d = m.det4x4;
            
            if (Math.abs(d) < 0.001) {
                // Determinant zero, there's no inverse
                return;
            }
    
            d = 1 / d;
    
            m111 = m.sxx; m121 = m.syx; m131 = m.szx; m141 = m.swx;
            m112 = m.sxy; m122 = m.syy; m132 = m.szy; m142 = m.swy;
            m113 = m.sxz; m123 = m.syz; m133 = m.szz; m143 = m.swz;
            m114 = m.tx;  m124 = m.ty;  m134 = m.tz;  m144 = m.tw;
            
			sxx = d * ( m122*(m133*m144 - m143*m134) - m132*(m123*m144 - m143*m124) + m142*(m123*m134 - m133*m124) );
			sxy = -d* ( m112*(m133*m144 - m143*m134) - m132*(m113*m144 - m143*m114) + m142*(m113*m134 - m133*m114) );
			sxz = d * ( m112*(m123*m144 - m143*m124) - m122*(m113*m144 - m143*m114) + m142*(m113*m124 - m123*m114) );
			tx = -d* ( m112*(m123*m134 - m133*m124) - m122*(m113*m134 - m133*m114) + m132*(m113*m124 - m123*m114) );

			syx = -d* ( m121*(m133*m144 - m143*m134) - m131*(m123*m144 - m143*m124) + m141*(m123*m134 - m133*m124) );
			syy = d * ( m111*(m133*m144 - m143*m134) - m131*(m113*m144 - m143*m114) + m141*(m113*m134 - m133*m114) );
			syz = -d* ( m111*(m123*m144 - m143*m124) - m121*(m113*m144 - m143*m114) + m141*(m113*m124 - m123*m114) );
			ty = d * ( m111*(m123*m134 - m133*m124) - m121*(m113*m134 - m133*m114) + m131*(m113*m124 - m123*m114) );

			szx = d * ( m121*(m132*m144 - m142*m134) - m131*(m122*m144 - m142*m124) + m141*(m122*m134 - m132*m124) );
			szy = -d* ( m111*(m132*m144 - m142*m134) - m131*(m112*m144 - m142*m114) + m141*(m112*m134 - m132*m114) );
			szz = d * ( m111*(m122*m144 - m142*m124) - m121*(m112*m144 - m142*m114) + m141*(m112*m124 - m122*m114) );
			tz = -d* ( m111*(m122*m134 - m132*m124) - m121*(m112*m134 - m132*m114) + m131*(m112*m124 - m122*m114) );

			swx = -d* ( m121*(m132*m143 - m142*m133) - m131*(m122*m143 - m142*m123) + m141*(m122*m133 - m132*m123) );
			swy = d * ( m111*(m132*m143 - m142*m133) - m131*(m112*m143 - m142*m113) + m141*(m112*m133 - m132*m113) );
			swz = -d* ( m111*(m122*m143 - m142*m123) - m121*(m112*m143 - m142*m113) + m141*(m112*m123 - m122*m113) );
			tw = d * ( m111*(m122*m133 - m132*m123) - m121*(m112*m133 - m132*m113) + m131*(m112*m123 - m122*m113) );
        }
  
   
        /**
        * Fills the 3d matrix object with values representing the transformation made by the given quaternion.
        * 
        * @param	quarternion	The quarterion object to convert.
        */
        public function quaternion2matrix(quarternion:Quaternion):void
        {
        	x = quarternion.x;
        	y = quarternion.y;
        	z = quarternion.z;
        	w = quarternion.w;
        	
            xx = x * x;
            xy = x * y;
            xz = x * z;
            xw = x * w;
    
            yy = y * y;
            yz = y * z;
            yw = y * w;
    
            zz = z * z;
            zw = z * w;
    
            sxx = 1 - 2 * (yy + zz);
            sxy =     2 * (xy - zw);
            sxz =     2 * (xz + yw);
    
            syx =     2 * (xy + zw);
            syy = 1 - 2 * (xx + zz);
            syz =     2 * (yz - xw);
    
            szx =     2 * (xz - yw);
            szy =     2 * (yz + xw);
            szz = 1 - 2 * (xx + yy);
        }
       
       /**
        * normalizes the axis vectors of the given 3d matrix.
        * 
        * @param	m	The 3d matrix object used for the normalize calulation.
        */
        public function normalize(m1:MatrixAway3D):void
        {
        	d = Math.sqrt(sxx*sxx + sxy*sxy + sxz*sxz);
			sxx /= d;
			sxy /= d;
			sxz /= d;
			d = Math.sqrt(syx*syx + syy*syy + syz*syz);
			syx /= d;
			syy /= d;
			syz /= d;
        	d = Math.sqrt(szx*szx + szy*szy + szz*szz);
			szx /= d;
			szy /= d;
			szz /= d;
        }
        
     	/**
        * Returns a Number3D representing the forward vector of this matrix.
        */
        public function get forward():Number3D
        {
        	_forward.x = szx;
        	_forward.y = szy;
        	_forward.z = szz;
        	return _forward;
        }
     	
     	/**
        * Set the forward vector (row3) of this matrix.
        */
     	public function set forward(n:Number3D):void
     	{
     		this.szx = n.x;
     		this.szy = n.y;
     		this.szz = n.z;
     	}   
     	
     	/**
        * Returns a Number3D representing the up vector of this matrix.
        */
        public function get up():Number3D
        {
        	_up.x = syx;
        	_up.y = syy;
        	_up.z = syz;
        	return _up;
        }
        
        /**
        * Set the up vector (row2) of this matrix.
        */
     	public function set up(n:Number3D):void
     	{
     		this.syx = n.x;
     		this.syy = n.y;
     		this.syz = n.z;
     	}   
     	
     	/**
        * Returns a Number3D representing the right vector of this matrix.
        */
        public function get right():Number3D
        {
        	_right.x = sxx;
        	_right.y = sxy;
        	_right.z = sxz;
        	return _right;
        }
        
        /**
        * Set the right vector (row1) of this matrix.
        */
     	public function set right(n:Number3D):void
     	{
     		this.sxx = n.x;
     		this.sxy = n.y;
     		this.sxz = n.z;
     	}   
     	
		public function multiplyVector3x3( v:Number3D ):void
		{
			var vx:Number = v.x;
			var vy:Number = v.y;
			var vz:Number = v.z;
			
			v.x = vx * sxx + vy * sxy + vz * sxz;
			v.y = vx * syx + vy * syy + vz * syz;
			v.z = vx * szx + vy * szy + vz * szz;
		}
    }
}