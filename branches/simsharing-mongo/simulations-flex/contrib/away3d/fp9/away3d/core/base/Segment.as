package away3d.core.base
{
    import away3d.arcane;
    import away3d.core.utils.*;
    import away3d.events.*;
    import away3d.materials.*;
    
    import flash.events.Event;
    
    use namespace arcane;
    
	 /**
	 * Dispatched when the material of the segment changes.
	 * 
	 * @eventType away3d.events.FaceEvent
	 */
	[Event(name="materialchanged",type="away3d.events.FaceEvent")]
	
    /**
    * A line element used in the wiremesh and mesh object
    * 
    * @see away3d.core.base.WireMesh
    * @see away3d.core.base.Mesh
    */
    public class Segment extends Element
    {
		/** @private */
        arcane var _v0:Vertex;
		/** @private */
        arcane var _v1:Vertex;
		/** @private */
        arcane var _material:ISegmentMaterial;
        
        private var _index:int;
        private var _vertices:Array = new Array();
		private var _commands:Array = new Array();
		
        private function onVertexValueChange(event:Event):void
        {
            notifyVertexValueChange();
        }
		
  		private function addVertexAt(index:uint, vertex:Vertex, command:String):void
  		{
  			if(_vertices[index] && _vertices[index] == vertex)
  				return;
  			
  			if (_vertices[index]) {
	  			_index = _vertices[index].parents.indexOf(this);
	  				if(_index != -1)
	  					_vertices[index].parents.splice(_index, 1);
	  		}
	  		
  			_commands[index] = segmentVO.commands[index] = command;
  			_vertices[index] = segmentVO.vertices[index] = vertex;
  			
  			if(index == 0)
  				_v0 = segmentVO.v0 = vertex;
  			else if(index == 1)
  				_v1 = segmentVO.v1 = vertex;
  			
  			vertex.parents.push(this);
  			
  			vertexDirty = true;
  		}
  		
  		public function moveTo(vertex:Vertex):void
  		{
  			addVertexAt(_vertices.length, vertex, "M");
  		}
  		
  		public function lineTo(vertex:Vertex):void
  		{
  			addVertexAt(_vertices.length, vertex, "L");
  		}
  		
  		public function curveTo(controlVertex:Vertex, endVertex:Vertex):void
  		{
  			addVertexAt(_vertices.length, controlVertex, "C");
  			
  			addVertexAt(_vertices.length, endVertex, "P");
  		}
  		
		public var segmentVO:SegmentVO = new SegmentVO();
		
		/**
		 * Returns an array of vertex objects that are used by the segment.
		 */
        public override function get vertices():Array
        {
            return _vertices;
        }
		        
		/**
		 * Returns an array of drawing command strings that are used by the segment.
		 */
        public override function get commands():Array
        {
            return _commands;
        }
        
		/**
		 * Defines the v0 vertex of the segment.
		 */
        public function get v0():Vertex
        {
            return _v0;
        }

        public function set v0(value:Vertex):void
        {
            addVertexAt(0, value, "M");
        }
		
		/**
		 * Defines the v1 vertex of the segment.
		 */
        public function get v1():Vertex
        {
            return _v1;
        }

        public function set v1(value:Vertex):void
        {
            addVertexAt(1, value, "L");
        }
		
		/**
		 * Defines the material of the segment.
		 */
        public function get material():ISegmentMaterial
        {
            return _material;
        }

        public function set material(value:ISegmentMaterial):void
        {
            if (value == _material)
                return;
            
			if (_material != null && parent)
				parent.removeMaterial(this, _material);
			
            _material = segmentVO.material = value;
			
			if (_material != null && parent)
				parent.addMaterial(this, _material);
        }
		
		/**
		 * Returns the squared bounding radius of the segment.
		 */
        public override function get radius2():Number
        {
            var rv0:Number = _v0._x*_v0._x + _v0._y*_v0._y + _v0._z*_v0._z;
            var rv1:Number = _v1._x*_v1._x + _v1._y*_v1._y + _v1._z*_v1._z;

            if (rv0 > rv1)
                return rv0;
            else
                return rv1;
        }
        
    	/**
    	 * Returns the maximum x value of the segment
    	 * 
    	 * @see		away3d.core.base.Vertex#x
    	 */
        public override function get maxX():Number
        {
            if (_v0._x > _v1._x)
                return _v0._x;
            else
                return _v1._x;
        }
        
    	/**
    	 * Returns the minimum x value of the face
    	 * 
    	 * @see		away3d.core.base.Vertex#x
    	 */
        public override function get minX():Number
        {
            if (_v0._x < _v1._x)
                return _v0._x;
            else
                return _v1._x;
        }
        
    	/**
    	 * Returns the maximum y value of the segment
    	 * 
    	 * @see		away3d.core.base.Vertex#y
    	 */
        public override function get maxY():Number
        {
            if (_v0._y > _v1._y)
                return _v0._y;
            else
                return _v1._y;
        }
        
    	/**
    	 * Returns the minimum y value of the face
    	 * 
    	 * @see		away3d.core.base.Vertex#y
    	 */
        public override function get minY():Number
        {
            if (_v0._y < _v1._y)
                return _v0._y;
            else
                return _v1._y;
        }
        
    	/**
    	 * Returns the maximum z value of the segment
    	 * 
    	 * @see		away3d.core.base.Vertex#z
    	 */
        public override function get maxZ():Number
        {
            if (_v0._z > _v1._z)
                return _v0._z;
            else
                return _v1._z;
        }
        
    	/**
    	 * Returns the minimum y value of the face
    	 * 
    	 * @see		away3d.core.base.Vertex#y
    	 */
        public override function get minZ():Number
        {
            if (_v0._z < _v1._z)
                return _v0._z;
            else
                return _v1._z;
        }
    	
		/**
		 * Creates a new <code>Segment</code> object.
		 *
		 * @param	v0						The first vertex object of the segment
		 * @param	v1						The second vertex object of the segment
		 * @param	material	[optional]	The material used by the segment to render
		 */
        public function Segment(v0:Vertex, v1:Vertex, material:ISegmentMaterial = null)
        {
            this.v0 = v0;
            this.v1 = v1;
            this.material = material;
            
            //segmentVO.segment = this;
            
            vertexDirty = true;
        }
    }
}
