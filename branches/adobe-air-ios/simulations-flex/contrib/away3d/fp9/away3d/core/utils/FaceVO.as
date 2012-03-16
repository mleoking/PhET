package away3d.core.utils
{
	import away3d.core.base.*;
	import away3d.materials.*;
	
	import flash.geom.*;
	
	public class FaceVO
	{
		public var generated:Boolean;
		
		public var commands:Array = new Array();
		
		public var vertices:Array = new Array();
		
		public var v0:Vertex;
		
        public var v1:Vertex;
		
        public var v2:Vertex;
        
        public var uv0:UV;
		
        public var uv1:UV;
		
        public var uv2:UV;
		
		public var material:ITriangleMaterial;
		
		public var back:ITriangleMaterial;
		
		public var face:Face;
		
		public var bitmapRect:Rectangle;
		
		public var reverseArea:Boolean;
		        
    	/**
    	 * Returns the maximum u value of the face
    	 * 
    	 * @see	away3d.core.base.UV#u
    	 */
        public function get maxU():Number
        {
            if (uv0.u > uv1.u)
            {
                if (uv0.u > uv2.u)
                    return uv0.u;
                else
                    return uv2.u;
            }
            else
            {
                if (uv1.u > uv2.u)
                    return uv1.u;
                else
                    return uv2.u;
            }
        }
        
    	/**
    	 * Returns the minimum u value of the face
    	 * 
    	 * @see away3d.core.base.UV#u
    	 */
        public function get minU():Number
        {
            if (uv0.u < uv1.u)
            {
                if (uv0.u < uv2.u)
                    return uv0.u;
                else
                    return uv2.u;
            }
            else
            {
                if (uv1.u < uv2.u)
                    return uv1.u;
                else
                    return uv2.u;
            }
        }
        
    	/**
    	 * Returns the maximum v value of the face
    	 * 
    	 * @see away3d.core.base.UV#v
    	 */
        public function get maxV():Number
        {
            if (uv0.v > uv1.v)
            {
                if (uv0.v > uv2.v)
                    return uv0.v;
                else
                    return uv2.v;
            }
            else
            {
                if (uv1.v > uv2.v)
                    return uv1.v;
                else
                    return uv2.v;
            }
        }
        
    	/**
    	 * Returns the minimum v value of the face
    	 * 
    	 * @see	away3d.core.base.UV#v
    	 */
        public function get minV():Number
        {
            if (uv0.v < uv1.v)
            {
                if (uv0.v < uv2.v)
                    return uv0.v;
                else
                    return uv2.v;
            }
            else
            {
                if (uv1.v < uv2.v)
                    return uv1.v;
                else
                    return uv2.v;
            }
        }
        
	}
}