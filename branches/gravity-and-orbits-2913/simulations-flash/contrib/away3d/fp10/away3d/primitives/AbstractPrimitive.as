package away3d.primitives
{
	import away3d.*;
	import away3d.core.base.*;
	import away3d.materials.*;
    
	use namespace arcane;
	
    /**
    * Abstract base class for shaded primitives
    */ 
    public class AbstractPrimitive extends Mesh
    {
		/** @private */
		arcane var _v:Vertex;
		/** @private */
		arcane var _vStore:Array = [];
		/** @private */
        arcane var _vActive:Array = [];
		/** @private */
		arcane var _uv:UV;
		/** @private */
		arcane var _uvStore:Array = [];
		/** @private */
        arcane var _uvActive:Array = [];
		/** @private */
		arcane var _face:Face;
		/** @private */
		arcane var _faceStore:Array = [];
		/** @private */
        arcane var _faceActive:Array = [];
        /** @private */
		arcane var _segment:Segment;
		/** @private */
		arcane var _segmentStore:Array = [];
		/** @private */
        arcane var _segmentActive:Array = [];
		/** @private */
        arcane var _primitiveDirty:Boolean;
		/** @private */
		arcane function createVertex(x:Number = 0, y:Number = 0, z:Number = 0):Vertex
		{
			if (_vStore.length) {
            	_vActive.push(_v = _vStore.pop());
	            _v.x = x;
	            _v.y = y;
	            _v.z = z;
   			} else {
            	_vActive.push(_v = new Vertex(x, y, z));
      		}
            return _v;
		}
		/** @private */
		arcane function createUV(u:Number = 0, v:Number = 0):UV
		{
			if (_uvStore.length) {
            	_uvActive.push(_uv = _uvStore.pop());
	            _uv.u = u;
	            _uv.v = v;
   			} else {
            	_uvActive.push(_uv = new UV(u, v));
      		}
            return _uv;
		}
		/** @private */
		arcane function createFace(v0:Vertex, v1:Vertex, v2:Vertex, material:ITriangleMaterial = null, uv0:UV = null, uv1:UV = null, uv2:UV = null):Face
		{
			if (_faceStore.length) {
            	_faceActive.push(_face = _faceStore.pop());
	            _face.v0 = v0;
	            _face.v1 = v1;
	            _face.v2 = v2;
	            _face.material = material;
	            _face.uv0 = uv0;
	            _face.uv1 = uv1;
	            _face.uv2 = uv2;
			} else {
            	_faceActive.push(_face = new Face(v0, v1, v2, material, uv0, uv1, uv2));
   			}
            return _face;
		}
		/** @private */
		arcane function createSegment(v0:Vertex, v1:Vertex, material:ISegmentMaterial = null):Segment
		{
			if (_segmentStore.length) {
            	_segmentActive.push(_segment = _segmentStore.pop());
	            _segment.v0 = v0;
	            _segment.v1 = v1;
	            _segment.material = material;
			} else {
            	_segmentActive.push(_segment = new Segment(v0, v1, material));
   			}
            return _segment;
		}
		
		private var _index:int;
     	
     	protected function updatePrimitive():void
     	{
			buildPrimitive();
    		
        	//execute quarterFaces
        	var i:int = geometry.quarterFacesTotal;
        	while (i--)
        		quarterFaces();
     	}
     	
		/**
		 * Builds the vertex, face and uv objects that make up the 3d primitive.
		 */
    	protected function buildPrimitive():void
    	{
    		_primitiveDirty = false;
    		_objectDirty = true;
    		
    		//remove all faces from the mesh
    		_index = faces.length;
    		while (_index--)
    			removeFace(faces[_index]);
    		
    		//remove all segments from the mesh
    		_index = segments.length;
    		while (_index--)
    			removeSegment(segments[_index]);
    			
    		//clear vertex objects
    		_vStore = _vStore.concat(_vActive);
        	_vActive = [];
    		
    		//clear uv objects
    		_uvStore = _uvStore.concat(_uvActive);
        	_uvActive = [];
        	
        	//clear face objects
    		_faceStore = _faceStore.concat(_faceActive);
        	_faceActive = [];
        	
        	//clear segment objects
    		_segmentStore = _segmentStore.concat(_segmentActive);
        	_segmentActive = [];
    	}
        
		/**
		 * @inheritDoc
		 */
        public override function get vertices():Array
        {
    		if (_primitiveDirty)
    			updatePrimitive();
    		
            return _geometry.vertices;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get faces():Array
        {
    		if (_primitiveDirty)
    			updatePrimitive();
    		
            return _geometry.faces;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get segments():Array
        {
    		if (_primitiveDirty)
    			updatePrimitive();
    		
            return _geometry.segments;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get billboards():Array
        {
    		if (_primitiveDirty)
    			updatePrimitive();
    		
            return _geometry.billboards;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get elements():Array
        {
    		if (_primitiveDirty)
    			updatePrimitive();
    		
            return _geometry.elements;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get geometry():Geometry
        {
    		if (_primitiveDirty)
    			updatePrimitive();
    		
        	return _geometry;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get boundingRadius():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.boundingRadius;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get maxX():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.maxX;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get minX():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.minX;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get maxY():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.maxY;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get minY():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.minY;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get maxZ():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.maxZ;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get minZ():Number
        {
            if (_primitiveDirty)
    			updatePrimitive();
           
           return super.minZ;
        }
        
		/**
		 * @inheritDoc
		 */
		public override function get objectWidth():Number
		{
            if (_primitiveDirty)
    			updatePrimitive();
           
			return super.objectWidth;
		}
        
		/**
		 * @inheritDoc
		 */
		public override function get objectHeight():Number
		{
            if (_primitiveDirty)
    			updatePrimitive();
           
			return super.objectHeight;
		}
        
		/**
		 * @inheritDoc
		 */
		public override function get objectDepth():Number
		{
            if (_primitiveDirty)
    			updatePrimitive();
           
			return  super.objectDepth;
		}
		
		/**
		 * Creates a new <code>AbstractPrimitive</code> object.
		 *
		 * @param	init			[optional]	An initialisation object for specifying default instance properties
		 */
		public function AbstractPrimitive(init:Object = null)
		{
			super(init);
			
			_primitiveDirty = true;
		}
		
		public override function updateObject():void
    	{
    		//build geometry
    		if (_primitiveDirty)
    			updatePrimitive();
        	
        	super.updateObject();
     	}
    }
}