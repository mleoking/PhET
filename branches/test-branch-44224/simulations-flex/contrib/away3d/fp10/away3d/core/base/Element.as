package away3d.core.base
{
    import away3d.arcane;
    import away3d.events.*;
    
    import flash.events.EventDispatcher;
    
    use namespace arcane;
    
	 /**
	 * Dispatched when the vertex of a 3d element changes.
	 * 
	 * @eventType away3d.events.ElementEvent
	 */
	[Event(name="vertexChanged",type="away3d.events.ElementEvent")]
    
	 /**
	 * Dispatched when the vertex value of a 3d element changes.
	 * 
	 * @eventType away3d.events.ElementEvent
	 */
	[Event(name="vertexvalueChanged",type="away3d.events.ElementEvent")]
    
	 /**
	 * Dispatched when the visiblity of a 3d element changes.
	 * 
	 * @eventType away3d.events.ElementEvent
	 */
	[Event(name="visibleChanged",type="away3d.events.ElementEvent")]
	
	/**
	 * Basic 3d element object
     * Not intended for direct use - use <code>Segment</code> or <code>Face</code>.
	 */
    public class Element extends EventDispatcher
    {
		/** @private */
        arcane var _visible:Boolean = true;
		/** @private */
        arcane function notifyVertexChange():void
        {
            if (!hasEventListener(ElementEvent.VERTEX_CHANGED))
                return;
			
            if (_vertexchanged == null)
                _vertexchanged = new ElementEvent(ElementEvent.VERTEX_CHANGED, this);
            
            dispatchEvent(_vertexchanged);
        }
		/** @private */
        arcane function notifyVertexValueChange():void
        {
            if (!hasEventListener(ElementEvent.VERTEXVALUE_CHANGED))
                return;
			
            if (_vertexvaluechanged == null)
                _vertexvaluechanged = new ElementEvent(ElementEvent.VERTEXVALUE_CHANGED, this);
            
            dispatchEvent(_vertexvaluechanged);
        }
		/** @private */
        arcane function notifyVisibleChange():void
        {
            if (!hasEventListener(ElementEvent.VISIBLE_CHANGED))
                return;
			
            if (_visiblechanged == null)
                _visiblechanged = new ElementEvent(ElementEvent.VISIBLE_CHANGED, this);
            
            dispatchEvent(_visiblechanged);
        }
		/** @private */
        arcane function notifyMappingChange():void
         {	
            if (!hasEventListener(ElementEvent.MAPPING_CHANGED))
                return;
			
            if (_mappingchanged == null)
                _mappingchanged = new ElementEvent(ElementEvent.MAPPING_CHANGED, this);
            
            dispatchEvent(_mappingchanged);
        }
        
		private var _vertexchanged:ElementEvent;
		private var _vertexvaluechanged:ElementEvent;
		private var _visiblechanged:ElementEvent;
		private var _mappingchanged:ElementEvent;
		
		public var vertexDirty:Boolean;
		
    	/**
    	 * An optional untyped object that can contain used-defined properties.
    	 */
        public var extra:Object;
        
    	/**
    	 * Defines the parent 3d object of the segment.
    	 */
		public var parent:Geometry;
		
		/**
		 * Returns an array of vertex objects that make up the 3d element.
		 */
        public function get vertices():Array
        {
            throw new Error("Not implemented");
        }
        
		/**
		 * Returns an array of drawing command strings that make up the 3d element.
		 */
        public function get commands():Array
        {
            throw new Error("Not implemented");
        }
        
		/**
		 * Determines whether the 3d element is visible in the scene.
		 */
        public function get visible():Boolean
        {
            return _visible;
        }
		
        public function set visible(value:Boolean):void
        {
            if (value == _visible)
                return;

            _visible = value;

            notifyVisibleChange();
        }
		
		/**
		 * Returns the squared bounding radius of the 3d element
		 */
        public function get radius2():Number
        {
            var maxr:Number = 0;
            for each (var vertex:Vertex in vertices)
            {
                var r:Number = vertex._x*vertex._x + vertex._y*vertex._y + vertex._z*vertex._z;
                if (r > maxr)
                    maxr = r;
            }
            return maxr;
        }
		
		/**
		 * Returns the maximum x value of the 3d element
		 */
        public function get maxX():Number
        {
            return Math.sqrt(radius2);
        }
		
		/**
		 * Returns the minimum x value of the 3d element
		 */
        public function get minX():Number
        {
            return -Math.sqrt(radius2);
        }
		
		/**
		 * Returns the maximum y value of the 3d element
		 */
        public function get maxY():Number
        {
            return Math.sqrt(radius2);
        }
		
		/**
		 * Returns the minimum y value of the 3d element
		 */
        public function get minY():Number
        {
            return -Math.sqrt(radius2);
        }
		
		/**
		 * Returns the maximum z value of the 3d element
		 */
        public function get maxZ():Number
        {
            return Math.sqrt(radius2);
        }
		
		/**
		 * Returns the minimum z value of the 3d element
		 */
        public function get minZ():Number
        {
            return -Math.sqrt(radius2);
        }
		
		/**
		 * Default method for adding a vertexchanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnVertexChange(listener:Function):void
        {
            addEventListener(ElementEvent.VERTEX_CHANGED, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a vertexchanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnVertexChange(listener:Function):void
        {
            removeEventListener(ElementEvent.VERTEX_CHANGED, listener, false);
        }
		
		/**
		 * Default method for adding a vertexvaluechanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnVertexValueChange(listener:Function):void
        {
            addEventListener(ElementEvent.VERTEXVALUE_CHANGED, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a vertexvaluechanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnVertexValueChange(listener:Function):void
        {
            removeEventListener(ElementEvent.VERTEXVALUE_CHANGED, listener, false);
        }
		
		/**
		 * Default method for adding a visiblechanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnVisibleChange(listener:Function):void
        {
            addEventListener(ElementEvent.VISIBLE_CHANGED, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a visiblechanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnVisibleChange(listener:Function):void
        {
            removeEventListener(ElementEvent.VISIBLE_CHANGED, listener, false);
        }
        
		/**
		 * Default method for adding a mappingchanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnMappingChange(listener:Function):void
        {
            addEventListener(ElementEvent.MAPPING_CHANGED, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a mappingchanged event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnMappingChange(listener:Function):void
        {
            removeEventListener(ElementEvent.MAPPING_CHANGED, listener, false);
        }
    }
}
