package away3d.primitives
{
	import away3d.arcane;
	import away3d.core.base.*;
    
	use namespace arcane;
	
    /**
    * Creates a 3d wire cube primitive.
    */ 
    public class WireCube extends AbstractPrimitive
    {
    	private var _width:Number;
    	private var _height:Number;
    	private var _depth:Number;
    	
		/**
		 * @inheritDoc
		 */
    	protected override function buildPrimitive():void
    	{
    		super.buildPrimitive();
    		
    		v000 = createVertex(-_width/2, -_height/2, -_depth/2); 
            v001 = createVertex(-_width/2, -_height/2, +_depth/2); 
            v010 = createVertex(-_width/2, +_height/2, -_depth/2); 
            v011 = createVertex(-_width/2, +_height/2, +_depth/2); 
            v100 = createVertex(+_width/2, -_height/2, -_depth/2); 
            v101 = createVertex(+_width/2, -_height/2, +_depth/2); 
            v110 = createVertex(+_width/2, +_height/2, -_depth/2); 
            v111 = createVertex(+_width/2, +_height/2, +_depth/2); 

            addSegment(createSegment(v000, v001));
            addSegment(createSegment(v011, v001));
            addSegment(createSegment(v011, v010));
            addSegment(createSegment(v000, v010));

            addSegment(createSegment(v100, v000));
            addSegment(createSegment(v101, v001));
            addSegment(createSegment(v111, v011));
            addSegment(createSegment(v110, v010));

            addSegment(createSegment(v100, v101));
            addSegment(createSegment(v111, v101));
            addSegment(createSegment(v111, v110));
            addSegment(createSegment(v100, v110));
    	}
    	
        public var v000:Vertex;
        public var v001:Vertex;
        public var v010:Vertex;
        public var v011:Vertex;
        public var v100:Vertex;
        public var v101:Vertex;
        public var v110:Vertex;
        public var v111:Vertex;
        
    	/**
    	 * Defines the width of the cube. Defaults to 100.
    	 */
    	public function get width():Number
    	{
    		return _width;
    	}
    	
    	public function set width(val:Number):void
    	{
    		if (_width == val)
    			return;
    		
    		_width = val;
    		_primitiveDirty = true;
    	}
    	
    	/**
    	 * Defines the height of the cube. Defaults to 100.
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
    	 * Defines the depth of the cube. Defaults to 100.
    	 */
    	public function get depth():Number
    	{
    		return _depth;
    	}
    	
    	public function set depth(val:Number):void
    	{
    		if (_depth == val)
    			return;
    		
    		_depth = val;
    		_primitiveDirty = true;
    	}
    	
		/**
		 * Creates a new <code>WireCube</code> object.
		 *
		 * @param	init			[optional]	An initialisation object for specifying default instance properties.
		 */
        public function WireCube(init:Object = null)
        {
            super(init);

            _width  = ini.getNumber("width", 100, {min:0});
            _height = ini.getNumber("height", 100, {min:0});
            _depth  = ini.getNumber("depth", 100, {min:0});
			
			type = "WireCube";
        	url = "primitive";
        }
    }
}