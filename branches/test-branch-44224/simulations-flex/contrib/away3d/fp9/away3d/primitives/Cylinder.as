package away3d.primitives
{
	import away3d.arcane;
    import away3d.core.base.*;
    import away3d.core.utils.*;
    
	use namespace arcane;
	
    /**
    * Creates a 3d cylinder primitive.
    */ 
    public class Cylinder extends AbstractPrimitive
    {
        private var grid:Array;
        private var jMin:int;
        private var jMax:int;
        private var _radius:Number;
        private var _height:Number;
        private var _segmentsW:int;
        private var _segmentsH:int;
        private var _openEnded:Boolean;
        private var _yUp:Boolean;
        
        private function buildCylinder():void
        {
            var i:int;
            var j:int;

            _height /= 2;

            grid = [];
            
			if (!_openEnded) {
	            _segmentsH += 2;
				jMin = 1;
				jMax = _segmentsH - 1;
			
	            var bottom:Vertex = _yUp? createVertex(0, -_height, 0) : createVertex(0, 0, -_height);
	            grid[0] = new Array(_segmentsW);
	            for (i = 0; i < _segmentsW; ++i) 
	                grid[0][i] = bottom;
	                
	            var top:Vertex = _yUp? createVertex(0, _height, 0) : createVertex(0, 0, _height);
	            grid[_segmentsH] = new Array(_segmentsW);
	            for (i = 0; i < _segmentsW; ++i) 
	                grid[_segmentsH][i] = top;
	                
			} else {
				jMin = 0;
				jMax = _segmentsH;
			}
			
            for (j = jMin; j <= jMax; ++j)  
            { 
                var z:Number = -_height + 2 * _height * (j-jMin) / (jMax-jMin);

                grid[j] = new Array(_segmentsW);
                for (i = 0; i < _segmentsW; ++i) 
                { 
                    var verangle:Number = 2 * i / _segmentsW * Math.PI;
                    var x:Number = _radius * Math.sin(verangle);
                    var y:Number = _radius * Math.cos(verangle);
                    if (yUp)
                    	grid[j][i] = createVertex(y, z, x);
                    else
                    	grid[j][i] = createVertex(y, -x, z);
                }
            }
			

            for (j = 1; j <= _segmentsH; ++j) 
                for (i = 0; i < _segmentsW; ++i) 
                {
                    var a:Vertex = grid[j][i];
                    var b:Vertex = grid[j][(i-1+_segmentsW) % _segmentsW];
                    var c:Vertex = grid[j-1][(i-1+_segmentsW) % _segmentsW];
                    var d:Vertex = grid[j-1][i];
					
					var i2:int = i;
					if (i == 0) i2 = _segmentsW;
					
                    var vab:Number = j / _segmentsH;
                    var vcd:Number = (j-1) / _segmentsH;
                    var uad:Number = i2 / _segmentsW;
                    var ubc:Number = (i2-1) / _segmentsW;
                    var uva:UV = createUV(uad,vab);
                    var uvb:UV = createUV(ubc,vab);
                    var uvc:UV = createUV(ubc,vcd);
                    var uvd:UV = createUV(uad,vcd);

                    if (j <= jMax)
                        addFace(createFace(a,b,c, null, uva,uvb,uvc));
                    if (j > jMin)                
                        addFace(createFace(a,c,d, null, uva,uvc,uvd));
                }
		}
        
		/**
		 * @inheritDoc
		 */
    	protected override function buildPrimitive():void
    	{
    		super.buildPrimitive();
    		
            var i:int;
            var j:int;

            _height /= 2;

            grid = [];
            
			if (!_openEnded) {
	            _segmentsH += 2;
				jMin = 1;
				jMax = _segmentsH - 1;
			
	            var bottom:Vertex = _yUp? createVertex(0, -_height, 0) : createVertex(0, 0, -_height);
	            grid[0] = new Array(_segmentsW);
	            
	            for (i = 0; i < _segmentsW; ++i) 
	                grid[0][i] = bottom;
	                
	            var top:Vertex = _yUp? createVertex(0, _height, 0) : createVertex(0, 0, _height);
	            grid[_segmentsH] = new Array(_segmentsW);
	            
	            for (i = 0; i < _segmentsW; ++i) 
	                grid[_segmentsH][i] = top;
	                
			} else {
				jMin = 0;
				jMax = _segmentsH;
			}
			
            for (j = jMin; j <= jMax; ++j) { 
                var z:Number = -_height + 2 * _height * (j-jMin) / (jMax-jMin);

                grid[j] = new Array(_segmentsW);
                for (i = 0; i < _segmentsW; ++i) { 
                    var verangle:Number = 2 * i / _segmentsW * Math.PI;
                    var x:Number = _radius * Math.sin(verangle);
                    var y:Number = _radius * Math.cos(verangle);
                    if (yUp)
                    	grid[j][i] = createVertex(y, z, x);
                    else
                    	grid[j][i] = createVertex(y, -x, z);
                }
            }
			

            for (j = 1; j <= _segmentsH; ++j) {
                for (i = 0; i < _segmentsW; ++i) {
                    var a:Vertex = grid[j][i];
                    var b:Vertex = grid[j][(i-1+_segmentsW) % _segmentsW];
                    var c:Vertex = grid[j-1][(i-1+_segmentsW) % _segmentsW];
                    var d:Vertex = grid[j-1][i];
					
					var i2:int = i;
					if (i == 0) i2 = _segmentsW;
					
                    var vab:Number = j / _segmentsH;
                    var vcd:Number = (j-1) / _segmentsH;
                    var uad:Number = i2 / _segmentsW;
                    var ubc:Number = (i2-1) / _segmentsW;
                    var uva:UV = createUV(uad,vab);
                    var uvb:UV = createUV(ubc,vab);
                    var uvc:UV = createUV(ubc,vcd);
                    var uvd:UV = createUV(uad,vcd);

                    if (j <= jMax)
                        addFace(createFace(a,b,c, null, uva,uvb,uvc));
                    if (j > jMin)                
                        addFace(createFace(a,c,d, null, uva,uvc,uvd));
                }
            }
    	}
    	
    	/**
    	 * Defines the radius of the cylinder. Defaults to 100.
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
    	 * Defines the height of the cylinder. Defaults to 200.
    	 */
    	public function get height():Number
    	{
    		return _height;
    	}
    	
    	public function set height(val:Number):void
    	{
    		if (_height == val)
    			return;
    		
    		_height = val;
    		_primitiveDirty = true;
    	}
    	
    	/**
    	 * Defines the number of horizontal segments that make up the cylinder. Defaults to 8.
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
    	 * Defines the number of vertical segments that make up the cylinder. Defaults to 1.
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
    	 * Defines whether the ends of the cylinder are left open (true) or closed (false). Defaults to false.
    	 */
    	public function get openEnded():Boolean
    	{
    		return _openEnded;
    	}
    	
    	public function set openEnded(val:Boolean):void
    	{
    		if (_openEnded == val)
    			return;
    		
    		_openEnded = val;
    		_primitiveDirty = true;
    	}
    	
    	/**
    	 * Defines whether the coordinates of the cylinder points use a yUp orientation (true) or a zUp orientation (false). Defaults to true.
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
		 * Creates a new <code>Cylinder</code> object.
		 *
		 * @param	init			[optional]	An initialisation object for specifying default instance properties.
		 */
        public function Cylinder(init:Object = null)
        {
            super(init);

            _radius = ini.getNumber("radius", 100, {min:0});
            _height = ini.getNumber("height", 200, {min:0});
            _segmentsW = ini.getInt("segmentsW", 8, {min:3});
            _segmentsH = ini.getInt("segmentsH", 1, {min:1});
			_openEnded = ini.getBoolean("openEnded", false);
			_yUp = ini.getBoolean("yUp", true);
			
			buildCylinder();
			
			type = "Cylinder";
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