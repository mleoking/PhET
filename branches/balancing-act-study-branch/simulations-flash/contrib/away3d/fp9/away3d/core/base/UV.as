package away3d.core.base
{
    import away3d.arcane;
    import away3d.core.utils.*;
    
    use namespace arcane;
    
	/**
	 * Texture coordinates value object.
	 * Properties u and v represent the horizontal and vertical texture axes.
	 */
    public class UV extends ValueObject
    {
		/** @private */
        arcane var _u:Number;
		/** @private */
        arcane var _v:Number;
		/** @private */
        arcane static function median(a:UV, b:UV):UV
        {
            if (a == null)
                return null;
            if (b == null)
                return null;
            return new UV((a._u + b._u)/2, (a._v + b._v)/2);
        }
		/** @private */
        arcane static function weighted(a:UV, b:UV, aw:Number, bw:Number):UV
        {                
            if (a == null)
                return null;
            if (b == null)
                return null;
            var d:Number = aw + bw;
            var ak:Number = aw / d;
            var bk:Number = bw / d;
            return new UV(a._u*ak+b._u*bk, a._v*ak + b._v*bk);
        }
        
    	/**
    	 * An optional untyped object that can contain used-defined properties.
    	 */
        public var extra:Object;
		
		/**
		 * Defines the vertical corrdinate of the texture value.
		 */
        public function get v():Number
        {
            return _v;
        }

        public function set v(value:Number):void
        {
            if (value == _v)
                return;

            _v = value;

            notifyChange();
        }
		
		/**
		 * Defines the horizontal corrdinate of the texture value.
		 */
        public function get u():Number
        {
            return _u;
        }

        public function set u(value:Number):void
        {
            if (value == _u)
                return;

            _u = value;

            notifyChange();
        }
    	
		/**
		 * Creates a new <code>UV</code> object.
		 *
		 * @param	u		[optional]	The horizontal corrdinate of the texture value. Defaults to 0.
		 * @param	v		[optional]	The vertical corrdinate of the texture value. Defaults to 0.
		 */
        public function UV(u:Number = 0, v:Number = 0)
        {
            _u = u;
            _v = v;
        }
		
		/**
		 * Duplicates the vertex properties to another <code>Vertex</code> object
		 * 
		 * @return	The new vertex instance with duplicated properties applied
		 */
        public function clone():UV
        {
            return new UV(_u, _v);
        }
		
		/**
		 * Used to trace the values of a uv object.
		 * 
		 * @return A string representation of the uv object.
		 */
        public override function toString():String
        {
            return "new UV("+_u+", "+_v+")";
        }


    }
}