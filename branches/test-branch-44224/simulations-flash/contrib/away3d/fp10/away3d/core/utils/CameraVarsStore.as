package away3d.core.utils
{
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.geom.*;
	import away3d.core.math.*;
	import away3d.core.render.*;
	import away3d.materials.*;
	
	import flash.utils.*;
	
	public class CameraVarsStore
	{
		private var _sourceDictionary:Dictionary = new Dictionary(true);
        private var _vertexClassificationDictionary:Dictionary;
		private var _vt:MatrixAway3D;
		private var _frustum:Frustum;
		private var _vertex:Vertex;
		private var _uv:UV;
		private var _vc:VertexClassification;
		private var _faceVO:FaceVO;
		private var _segmentVO:SegmentVO;
		private var _object:Object;
		private var _v:Object;
		private var _source:Object3D;
		private var _session:AbstractRenderSession;
		private var _vtActive:Array = [];
        private var _vtStore:Array = [];
		private var _frActive:Array = [];
        private var _frStore:Array = [];
        private var _vActive:Array = [];
		private var _vStore:Array = [];
		private var _vcStore:Array = [];
		private var _uvDictionary:Dictionary = new Dictionary(true);
		private var _uvArray:Array;
        private var _uvStore:Array = [];
        private var _fActive:Array = [];
        private var _fStore:Array = [];
        private var _sActive:Array = [];
        private var _sStore:Array = [];
		public var view:View3D;
    	
        /**
        * Dictionary of all objects transforms calulated from the camera view for the last render frame
        */
        public var viewTransformDictionary:Dictionary = new Dictionary(true);
        
        public var nodeClassificationDictionary:Dictionary = new Dictionary(true);
        
        public var frustumDictionary:Dictionary = new Dictionary(true);
        
		public function createVertexClassificationDictionary(source:Object3D):Dictionary
		{
	        if (!(_vertexClassificationDictionary = _sourceDictionary[source]))
				_vertexClassificationDictionary = _sourceDictionary[source] = new Dictionary(true);
			
			return _vertexClassificationDictionary;
		}
		
        public function createVertexClassification(vertex:Vertex):VertexClassification
		{
			if ((_vc = _vertexClassificationDictionary[vertex]))
        		return _vc;
        	
			if (_vcStore.length) {
	        	_vc = _vertexClassificationDictionary[vertex] = _vcStore.pop();
	  		} else {
	        	_vc = _vertexClassificationDictionary[vertex] = new VertexClassification();
	    	}
	    	
	        _vc.vertex = vertex;
	        _vc.plane = null;
	        return _vc;
  		}
  		
		public function createViewTransform(node:Object3D):MatrixAway3D
        {
        	if (_vtStore.length)
        		_vtActive.push(_vt = viewTransformDictionary[node] = _vtStore.pop());
        	else
        		_vtActive.push(_vt = viewTransformDictionary[node] = new MatrixAway3D());
        	
        	return _vt;
        }
        
		public function createFrustum(node:Object3D):Frustum
        {
        	if (_frStore.length)
        		_frActive.push(_frustum = frustumDictionary[node] = _frStore.pop());
        	else
        		_frActive.push(_frustum = frustumDictionary[node] = new Frustum());
        	
        	return _frustum;
        }
        
		public function createVertex(x:Number, y:Number, z:Number):Vertex
        {
        	if (_vStore.length) {
        		_vActive.push(_vertex = _vStore.pop());
        		_vertex.x = x;
        		_vertex.y = y;
        		_vertex.z = z;
        	} else {
        		_vActive.push(_vertex = new Vertex(x, y, z));
        	}
        	
        	return _vertex;
        }
        
		public function createUV(u:Number, v:Number, session:AbstractRenderSession):UV
        {
        	if (!(_uvArray = _uvDictionary[session]))
				_uvArray = _uvDictionary[session] = [];
			
        	if (_uvStore.length) {
        		_uvArray.push(_uv = _uvStore.pop());
        		_uv.u = u;
        		_uv.v = v;
        	} else
        		_uvArray.push(_uv = new UV(u, v));
        	
        	return _uv;
        }
        
        public function createFaceVO(face:Face, material:ITriangleMaterial, back:ITriangleMaterial, uv0:UV, uv1:UV, uv2:UV):FaceVO
        {
        	if (_fStore.length)
        		_fActive.push(_faceVO = _fStore.pop());
        	else
        		_fActive.push(_faceVO = new FaceVO());
        	
        	_faceVO.face = face;
        	_faceVO.uv0 = uv0;
        	_faceVO.uv1 = uv1;
        	_faceVO.uv2 = uv2;
        	_faceVO.material = material;
        	_faceVO.back = back;
        	_faceVO.generated = true;
        	
        	return _faceVO;
        }
        
        public function createSegmentVO(material:ISegmentMaterial):SegmentVO
        {
        	if (_sStore.length)
        		_sActive.push(_segmentVO = _sStore.pop());
        	else
        		_sActive.push(_segmentVO = new SegmentVO());
        	
        	_segmentVO.generated = true;
        	
        	return _segmentVO;
        }
        
        public function reset():void
        {
        	
        	for (_object in _sourceDictionary) {
				_source = _object as Object3D;
				if (_source.session && _source.session.updated) {
					for (_v in _sourceDictionary[_source]) {
						_vcStore.push(_sourceDictionary[_source][_v]);
						delete _sourceDictionary[_source][_v];
					}
				}
			}
			
        	nodeClassificationDictionary = new Dictionary(true);
        	
        	_vtStore = _vtStore.concat(_vtActive);
        	_vtActive.length = 0;
        	_frStore = _frStore.concat(_frActive);
        	_frActive.length = 0;
        	_vStore = _vStore.concat(_vActive);
        	_vActive.length = 0;
        	
        	for (_object in _uvDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_uvArray = _uvDictionary[_session] as Array
					_uvStore = _uvStore.concat();
					_uvArray.length = 0;
				}
			}
			
			_fStore = _fStore.concat(_fActive);
        	_fActive.length = 0;
        	
			_sStore = _sStore.concat(_sActive);
        	_sActive.length = 0;
        }
	}
}