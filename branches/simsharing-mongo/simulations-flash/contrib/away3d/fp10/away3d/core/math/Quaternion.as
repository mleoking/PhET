package away3d.core.math
{
    /**
    * A Quaternion object.
    */
    public final class Quaternion
    {
    	private var w1:Number;
        private var w2:Number;
        private var x1:Number;
        private var x2:Number;
        private var y1:Number;
        private var y2:Number;
        private var z1:Number;
        private var z2:Number;
	    private var sin_a:Number;
	    private var cos_a:Number;
	    private var fSinPitch:Number;
        private var fCosPitch:Number;
        private var fSinYaw:Number;
        private var fCosYaw:Number;
        private var fSinRoll:Number;
        private var fCosRoll:Number;
        private var fCosPitchCosYaw:Number;
        private var fSinPitchSinYaw:Number;
	    
        
    	/**
    	 * The x value of the quatertion.
    	 */
        public var x:Number;
        
        /**
    	 * The y value of the quatertion.
    	 */
        public var y:Number;
        
        /**
    	 * The z value of the quatertion.
    	 */
        public var z:Number;
        
        /**
    	 * The w value of the quatertion.
    	 */
        public var w:Number;
        
	    /**
	    * Returns the magnitude of the quaternion object.
	    */
	    public function get magnitude():Number
	    {
	        return(Math.sqrt(w*w + x*x + y*y + z*z));
	    }
	    
        /**
        * Fills the quaternion object with the result from a multipication of two quaternion objects.
        * 
        * @param	qa	The first quaternion in the multipication.
        * @param	qb	The second quaternion in the multipication.
        */
	    public function multiply(qa:Quaternion, qb:Quaternion):void
	    {
	        w1 = qa.w;  x1 = qa.x;  y1 = qa.y;  z1 = qa.z;
	        w2 = qb.w;  x2 = qb.x;  y2 = qb.y;  z2 = qb.z;
	   
	        w = w1*w2 - x1*x2 - y1*y2 - z1*z2;
	        x = w1*x2 + x1*w2 + y1*z2 - z1*y2;
	        y = w1*y2 + y1*w2 + z1*x2 - x1*z2;
	        z = w1*z2 + z1*w2 + x1*y2 - y1*x2;
	    }
	    
    	/**
    	 * Fills the quaternion object with values representing the given rotation around a vector.
    	 * 
    	 * @param	x		The x value of the rotation vector.
    	 * @param	y		The y value of the rotation vector.
    	 * @param	z		The z value of the rotation vector.
    	 * @param	angle	The angle in radians of the rotation.
    	 */
	    public function axis2quaternion(x:Number, y:Number, z:Number, angle:Number):void
	    {
	        sin_a = Math.sin(angle / 2);
	        cos_a = Math.cos(angle / 2);
	   
	        this.x = x*sin_a;
	        this.y = y*sin_a;
	        this.z = z*sin_a;
	        w = cos_a;
			normalize();
	    }
	    
    	/**
    	 * Fills the quaternion object with values representing the given euler rotation.
    	 * 
    	 * @param	ax		The angle in radians of the rotation around the x axis.
    	 * @param	ay		The angle in radians of the rotation around the y axis.
    	 * @param	az		The angle in radians of the rotation around the z axis.
    	 */
        public function euler2quaternion(ax:Number, ay:Number, az:Number):void
        {
            fSinPitch       = Math.sin(ax * 0.5);
            fCosPitch       = Math.cos(ax * 0.5);
            fSinYaw         = Math.sin(ay * 0.5);
            fCosYaw         = Math.cos(ay * 0.5);
            fSinRoll        = Math.sin(az * 0.5);
            fCosRoll        = Math.cos(az * 0.5);
            fCosPitchCosYaw = fCosPitch * fCosYaw;
            fSinPitchSinYaw = fSinPitch * fSinYaw;
    
            x = fSinRoll * fCosPitchCosYaw     - fCosRoll * fSinPitchSinYaw;
            y = fCosRoll * fSinPitch * fCosYaw + fSinRoll * fCosPitch * fSinYaw;
            z = fCosRoll * fCosPitch * fSinYaw - fSinRoll * fSinPitch * fCosYaw;
            w = fCosRoll * fCosPitchCosYaw     + fSinRoll * fSinPitchSinYaw;
        }
        
        /**
        * Normalises the quaternion object.
        */
	    public function normalize(val:Number = 1):void
	    {
	        var mag:Number = magnitude*val;
	   
	        x /= mag;
	        y /= mag;
	        z /= mag;
	        w /= mag;
	    }
		
		/**
		 * Used to trace the values of a quaternion.
		 * 
		 * @return A string representation of the quaternion object.
		 */
	    public function toString(): String
        {
            return "{x:" + x + " y:" + y + " z:" + z + " w:" + w + "}";
        }
    }
}