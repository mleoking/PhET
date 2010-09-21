package away3d.primitives
{
	import away3d.arcane;
    import away3d.core.base.*;
    
	use namespace arcane;
	
    /**
    * Creates a 3d wire sphere primitive.
    */ 
    public class WireSphere extends AbstractPrimitive
    {
        private var grid:Array;
        private var _radius:Number;
        private var _segmentsW:int;
        private var _segmentsH:int;
        private var _yUp:Boolean;
        
		/**
		 * @inheritDoc
		 */
    	protected override function buildPrimitive():void
    	{
    		super.buildPrimitive();
    		
            var i:int;
            var j:int;
			
            grid = new Array(_segmentsH + 1);
            
            var bottom:Vertex = _yUp? createVertex(0, -_radius, 0) : createVertex(0, 0, -_radius);
            grid[0] = new Array(_segmentsW);
            
            for (i = 0; i < _segmentsW; ++i)
                grid[0][i] = bottom;
            
            for (j = 1; j < _segmentsH; ++j) { 
                var horangle:Number = j / _segmentsH * Math.PI;
                var z:Number = -_radius * Math.cos(horangle);
                var ringradius:Number = _radius * Math.sin(horangle);

                grid[j] = new Array(_segmentsW);
				
                for (i = 0; i < _segmentsW; ++i) { 
                    var verangle:Number = 2 * i / _segmentsW * Math.PI;
                    var x:Number = ringradius * Math.sin(verangle);
                    var y:Number = ringradius * Math.cos(verangle);
                    
					if (_yUp)
                    	grid[j][i] = createVertex(y, z, x);
                    else
                    	grid[j][i] = createVertex(y, -x, z);
                }
            }
			
			var top:Vertex = _yUp? createVertex(0, _radius, 0) : createVertex(0, 0, _radius);
            grid[_segmentsH] = new Array(_segmentsW);
            
            for (i = 0; i < _segmentsW; ++i)
                grid[_segmentsH][i] = top;
			
            for (j = 1; j <= _segmentsH; ++j) {
                for (i = 0; i < _segmentsW; ++i) {
                    var a:Vertex = grid[j][i];
                    var b:Vertex = grid[j][(i-1+_segmentsW) % _segmentsW];
                    var c:Vertex = grid[j-1][(i-1+_segmentsW) % _segmentsW];
                    var d:Vertex = grid[j-1][i];
					
                    addSegment(createSegment(a, d));
                    addSegment(createSegment(b, c));
                    if (j < _segmentsH)  
                        addSegment(createSegment(a, b));
                }
            }
    	}
    	
    	/**
    	 * Defines the radius of the wire sphere. Defaults to 100.
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
    	 * Defines the number of horizontal segments that make up the wire sphere. Defaults to 8.
    	 */
    	public function get segmentsW():Number
    	{
    		return _segmentsW;
    	}
    	
    	public function set segmentsW(val:Number):void
    	{
    		if (_segmentsW == val)
    			return;
    		
    		_segmentsW = val;
    		_primitiveDirty = true;
    	}
    	
    	/**
    	 * Defines the number of vertical segments that make up the wire sphere. Defaults to 1.
    	 */
    	public function get segmentsH():Number
    	{
    		return _segmentsH;
    	}
    	
    	public function set segmentsH(val:Number):void
    	{
    		if (_segmentsH == val)
    			return;
    		
    		_segmentsH = val;
    		_primitiveDirty = true;
    	}
    	
    	/**
    	 * Defines whether the coordinates of the wire sphere points use a yUp orientation (true) or a zUp orientation (false). Defaults to true.
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
		 * Creates a new <code>WireSphere</code> object.
		 *
		 * @param	init			[optional]	An initialisation object for specifying default instance properties.
		 */
        public function WireSphere(init:Object = null)
        {
            super(init);

            _radius = ini.getNumber("radius", 100, {min:0});
            _segmentsW = ini.getInt("segmentsW", 8, {min:3});
            _segmentsH = ini.getInt("segmentsH", 6, {min:2});
			_yUp = ini.getBoolean("yUp", true);
            
			type = "WireSphere";
        	url = "primitive";
        }
        
		/**
		 * Returns the vertex object specified by the grid position of the mesh.
		 * 
		 * @param	w	The horizontal position on the primitive mesh.
		 * @param	h	The vertical position on the primitive mesh.
		 */
        public function vertex(w:int, h:int):Vertex
        {
        	if (_primitiveDirty)
    			updatePrimitive();
    		
            return grid[h][w];
        }
    }
}