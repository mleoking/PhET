package away3d.primitives
{
	import away3d.arcane;
    import away3d.core.base.*;
    import away3d.core.utils.*;
    
	use namespace arcane;
	
    /**
    * Creates a 3d wire polygon.
    */ 
    public class WireCircle extends AbstractPrimitive
    {
        private var _radius:Number;
        private var _sides:Number;
        private var _yUp:Boolean;
        
		/**
		 * @inheritDoc
		 */
    	protected override function buildPrimitive():void
    	{
    		super.buildPrimitive();
    		
            var vertices:Array = [];
            var i:int;
            for (i = 0; i < _sides; ++i)
            {
                var u:Number = i / _sides * 2 * Math.PI;
                if (_yUp)
                	vertices.push(createVertex(_radius*Math.cos(u), 0, -_radius*Math.sin(u)));
                else
                	vertices.push(createVertex(_radius*Math.cos(u), _radius*Math.sin(u), 0));
            }

            for (i = 0; i < _sides; ++i)
			{
                addSegment(createSegment(vertices[i], vertices[(i+1) % _sides]));
			}
    	}
    	
    	/**
    	 * Defines the radius of the polygon. Defaults to 100.
    	 */
    	public function get radius():Number
    	{
    		return _radius;
    	}
    	
    	public function set radius(val:Number):void
    	{
    		if (_radius == val)
    			return;
    		
    		_radius = val;
    		_primitiveDirty = true;
    	}
    			
    	/**
    	 * Defines the number of sides of the polygon. Defaults to 8 (octohedron).
    	 */
    	public function get sides():Number
    	{
    		return _sides;
    	}
    	
    	public function set sides(val:Number):void
    	{
    		if (_sides == val)
    			return;
    		
    		_sides = val;
    		_primitiveDirty = true;
    	}
    	
    	/**
    	 * Defines whether the coordinates of the polygon points use a yUp orientation (true) or a zUp orientation (false). Defaults to true.
    	 */
    	public function get yUp():Boolean
    	{
    		return _yUp;
    	}
    	
    	public function set yUp(val:Boolean):void
    	{
    		if (_yUp == val)
    			return;
    		
    		_yUp = val;
    		_primitiveDirty = true;
    	}
    	
		/**
		 * Creates a new <code>WireCircle</code> object.
		 *
		 * @param	init			[optional]	An initialisation object for specifying default instance properties.
		 */
        public function WireCircle(init:Object = null)
        {
            super(init);

            _radius = ini.getNumber("radius", 100, {min:0});
            _sides = ini.getInt("sides", 8, {min:3});
			_yUp = ini.getBoolean("yUp", true);
			
			type = "WireCircle";
        	url = "primitive";
        }
    }
}
