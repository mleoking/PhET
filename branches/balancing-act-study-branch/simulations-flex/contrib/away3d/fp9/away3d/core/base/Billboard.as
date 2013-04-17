package away3d.core.base
{
    import away3d.arcane;
    import away3d.core.utils.*;
    import away3d.events.*;
    import away3d.materials.*;
    
    use namespace arcane;
    
	 /**
	 * Dispatched when the material of the billboard changes.
	 * 
	 * @eventType away3d.events.FaceEvent
	 */
	[Event(name="materialchanged",type="away3d.events.BillboardEvent")]
	
    /**
    * A graphics element used to represent objects that always face the camera
    * 
    * @see away3d.core.base.Mesh
    */
    public class Billboard extends Element
    {
		/** @private */
        arcane var _vertex:Vertex;
		/** @private */
        arcane var _material:IBillboardMaterial;
        
		private var _width:Number;
		private var _height:Number;
		private var _rotation:Number;
		private var _scaling:Number;
		private var _index:int;
		private var _vertices:Array = new Array();
		private var _commands:Array = new Array();
		
		public var billboardVO:BillboardVO = new BillboardVO();
		
		/**
		 * Returns an array of vertex objects that are used by the billboard.
		 */
        public override function get vertices():Array
        {
            return _vertices;
        }
        
		/**
		 * Returns an array of drawing command strings that are used by the billboard.
		 */
        public override function get commands():Array
        {
            return _commands;
        }
        
		/**
		 * Defines the vertex of the billboard.
		 */
        public function get vertex():Vertex
        {
            return _vertex;
        }

        public function set vertex(value:Vertex):void
        {
            if (value == _vertex)
                return;
			
			if (_vertex) {
	  			_index = _vertex.parents.indexOf(this);
	  				if(_index != -1)
	  					_vertex.parents.splice(_index, 1);
	  		}
	  		
			_commands[0] = billboardVO.command = "M";
            _vertices[0] = _vertex = billboardVO.vertex = value;
			
			if (_vertex)
				_vertex.parents.push(this);
  			
  			vertexDirty = true;
        }
        
    	/**
    	 * Defines the x coordinate of the billboard relative to the local coordinates of the parent <code>Mesh</code>.
    	 */
        public function get x():Number
        {
            return _vertex.x;
        }
    
        public function set x(value:Number):void
        {
            if (isNaN(value))
                throw new Error("isNaN(x)");
			
			if (_vertex.x == value)
				return;
			
            if (value == Infinity)
                Debug.warning("x == Infinity");

            if (value == -Infinity)
                Debug.warning("x == -Infinity");

            _vertex.x = value;
        }
		
    	/**
    	 * Defines the y coordinate of the billboard relative to the local coordinates of the parent <code>Mesh</code>.
    	 */
        public function get y():Number
        {
            return _vertex.y;
        }
    
        public function set y(value:Number):void
        {
            if (isNaN(value))
                throw new Error("isNaN(y)");
			
			if (_vertex.y == value)
				return;
			
            if (value == Infinity)
                Debug.warning("y == Infinity");

            if (value == -Infinity)
                Debug.warning("y == -Infinity");

            _vertex.y = value;
        }
		
    	/**
    	 * Defines the z coordinate of the billboard relative to the local coordinates of the parent <code>Mesh</code>.
    	 */
        public function get z():Number
        {
            return _vertex.z;
        }
    	
        public function set z(value:Number):void
        {
            if (isNaN(value))
                throw new Error("isNaN(z)");
			
			if (_vertex.z == value)
				return;
			
            if (value == Infinity)
                Debug.warning("z == Infinity");

            if (value == -Infinity)
                Debug.warning("z == -Infinity");

            _vertex.z = value;
            
        }
        
		/**
		 * Defines the material of the billboard.
		 */
        public function get material():IBillboardMaterial
        {
            return _material;
        }

        public function set material(value:IBillboardMaterial):void
        {
            if (_material == value)
                return;
			
			if (_material != null && parent)
				parent.removeMaterial(this, _material);
			
            _material = billboardVO.material = value;
			
			if (_material != null && parent)
				parent.addMaterial(this, _material);
        }
        
		/**
		 * Defines the width of the billboard.
		 */
        public function get width():Number
        {
            return _width;
        }

        public function set width(value:Number):void
        {
            if (_width == value)
                return;

            _width = billboardVO.width = value;
			
            notifyMappingChange();
        }
        
		/**
		 * Defines the height of the billboard.
		 */
        public function get height():Number
        {
            return _width;
        }

        public function set height(value:Number):void
        {
            if (_height == value)
                return;
			
            _height = billboardVO.height = value;
			
            notifyMappingChange();
        }
        
		/**
		 * Defines the scaling of the billboard when an <code>IUVMaterial</code> is used.
		 */
        public function get scaling():Number
        {
            return _scaling;
        }

        public function set scaling(value:Number):void
        {
            if (_scaling == value)
                return;
			
            _scaling = billboardVO.scaling = value;
			
            notifyMappingChange();
        }
        
		/**
		 * Defines the rotation of the billboard.
		 */
        public function get rotation():Number
        {
            return _rotation;
        }

        public function set rotation(value:Number):void
        {
            if (_rotation == value)
                return;
			
            _rotation = billboardVO.rotation = value;
			
            notifyMappingChange();
        }
        
		/**
		 * Returns the squared bounding radius of the billboard.
		 */
        public override function get radius2():Number
        {
            return 0;
        }
        
    	/**
    	 * Returns the maximum x value of the segment
    	 * 
    	 * @see		away3d.core.base.Vertex#x
    	 */
        public override function get maxX():Number
        {
            return _vertex._x;
        }
        
    	/**
    	 * Returns the minimum x value of the face
    	 * 
    	 * @see		away3d.core.base.Vertex#x
    	 */
        public override function get minX():Number
        {
            return _vertex._x;
        }
        
    	/**
    	 * Returns the maximum y value of the segment
    	 * 
    	 * @see		away3d.core.base.Vertex#y
    	 */
        public override function get maxY():Number
        {
            return _vertex._y;
        }
        
    	/**
    	 * Returns the minimum y value of the face
    	 * 
    	 * @see		away3d.core.base.Vertex#y
    	 */
        public override function get minY():Number
        {
            return _vertex._y;
        }
        
    	/**
    	 * Returns the maximum z value of the segment
    	 * 
    	 * @see		away3d.core.base.Vertex#z
    	 */
        public override function get maxZ():Number
        {
            return _vertex._z;
        }
        
    	/**
    	 * Returns the minimum y value of the face
    	 * 
    	 * @see		away3d.core.base.Vertex#y
    	 */
        public override function get minZ():Number
        {
            return _vertex._z;
        }
    	
		/**
		 * Creates a new <code>Billboard</code> object.
		 *
		 * @param	vertex					The vertex object of the billboard
		 * @param	material	[optional]	The material used by the billboard to render
		 */
        public function Billboard(vertex:Vertex, material:IBillboardMaterial = null, width:Number = 10, height:Number = 10, rotation:Number = 0, scaling:Number = 1)
        {
            this.vertex = vertex;
            this.material = material;
            this.width = width;
            this.height = height;
            this.rotation = rotation;
            this.scaling = scaling;
            
            billboardVO.billboard = this;
            
            vertexDirty = true;
        }
    }
}
