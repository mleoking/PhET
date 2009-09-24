package away3d.core.clip
{
	import away3d.arcane;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.geom.*;
	import away3d.core.render.*;
	import away3d.core.utils.*;
	
	import flash.utils.*;
	
	use namespace arcane;
	
    /**
    * Rectangle clipping combined with nearfield clipping
    */
    public class NearfieldClipping extends Clipping
    {
    	private var _faces:Array;
    	private var _face:Face;
    	private var _faceVOs:Array = new Array();
    	private var _faceVO:FaceVO;
    	private var _newFaceVO:FaceVO;
    	private var _v0C:VertexClassification;
    	private var _v1C:VertexClassification;
    	private var _v2C:VertexClassification;
    	private var _v0d:Number;
    	private var _v1d:Number;
    	private var _v2d:Number;
    	private var _v0w:Number;
    	private var _v1w:Number;
    	private var _v2w:Number;
    	private var _d:Number;
    	private var _session:AbstractRenderSession;
    	private var _frustum:Frustum;
    	private var _processed:Dictionary;
    	private var _pass:Boolean;
		private var _plane:Plane3D;
		private var _v0:Vertex;
    	private var _v01:Vertex;
    	private var _v1:Vertex;
    	private var _v12:Vertex;
    	private var _v2:Vertex;
    	private var _v20:Vertex;
    	private var _uv0:UV;
    	private var _uv01:UV;
    	private var _uv1:UV;
    	private var _uv12:UV;
    	private var _uv2:UV;
    	private var _uv20:UV;
		
		public override function set objectCulling(val:Boolean):void
		{
			if (!val)
				throw new Error("objectCulling requires setting to true for NearfieldClipping");
			
			_objectCulling = val;
		}
		
        public function NearfieldClipping(init:Object = null)
        {
            super(init);
            
            objectCulling = ini.getBoolean("objectCulling", true);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function checkPrimitive(pri:DrawPrimitive):Boolean
        {
            if (pri.maxX < minX)
                return false;
            if (pri.minX > maxX)
                return false;
            if (pri.maxY < minY)
                return false;
            if (pri.minY > maxY)
                return false;
            
            return true;
        }
        
        		
		public override function checkElements(mesh:Mesh, clippedFaceVOs:Array, clippedSegmentVOs:Array, clippedBillboards:Array, clippedVertices:Array, clippedCommands:Array, clippedIndices:Array, startIndices:Array):void
		{
			_session = mesh.session;
			_frustum = _cameraVarsStore.frustumDictionary[mesh];
			_processed = new Dictionary();
            _faces = mesh.faces;
            _faceVOs.length = 0;
            
            for each (_face in _faces) {
            	
            	if (!_face.visible)
					continue;
				
            	_faceVOs[_faceVOs.length] = _face.faceVO;
            }
            
			for each (_faceVO in _faceVOs) {
				
				_pass = true;
				
				_v0C = _cameraVarsStore.createVertexClassification(_faceVO.v0);
				_v1C = _cameraVarsStore.createVertexClassification(_faceVO.v1);
				_v2C = _cameraVarsStore.createVertexClassification(_faceVO.v2);
				
				if (_v0C.plane || _v1C.plane || _v2C.plane) {
					if ((_plane = _v0C.plane)) {
						_v0d = _v0C.distance;
						_v1d = _v1C.getDistance(_plane);
						_v2d = _v2C.getDistance(_plane);
					} else if ((_plane = _v1C.plane)) {
						_v0d = _v0C.getDistance(_plane);
						_v1d = _v1C.distance;
						_v2d = _v2C.getDistance(_plane);
					} else if ((_plane = _v2C.plane)) {
						_v0d = _v0C.getDistance(_plane);
						_v1d = _v1C.getDistance(_plane);
						_v2d = _v2C.distance;
					}
				} else {
					_plane = _frustum.planes[Frustum.NEAR];
					_v0d = _v0C.getDistance(_plane);
					_v1d = _v1C.getDistance(_plane);
					_v2d = _v2C.getDistance(_plane);
				}
				
				if (_v0d < 0 && _v1d < 0 && _v2d < 0)
					continue;
				
				if (_v0d < 0 || _v1d < 0 || _v2d < 0) {
					_pass = false;
				}
				
				if (_pass) {
					clippedFaceVOs[clippedFaceVOs.length] = _faceVO;
					
					startIndices[startIndices.length] = clippedIndices.length;
	        		
					if (!_processed[_faceVO.v0]) {
                        clippedVertices[clippedVertices.length] = _faceVO.v0;
                        clippedIndices[clippedIndices.length] = (_processed[_faceVO.v0] = clippedVertices.length) - 1;
                    } else {
                    	clippedIndices[clippedIndices.length] = _processed[_faceVO.v0] - 1;
                    }
                    if (!_processed[_faceVO.v1]) {
                        clippedVertices[clippedVertices.length] = _faceVO.v1;
                        clippedIndices[clippedIndices.length] = (_processed[_faceVO.v1] = clippedVertices.length) - 1;
                    } else {
                    	clippedIndices[clippedIndices.length] = _processed[_faceVO.v1] - 1;
                    }
                    if (!_processed[_faceVO.v2]) {
                        clippedVertices[clippedVertices.length] = _faceVO.v2;
                        clippedIndices[clippedIndices.length] = (_processed[_faceVO.v2] = clippedVertices.length) - 1;
                    } else {
                    	clippedIndices[clippedIndices.length] = _processed[_faceVO.v2] - 1;
                    }
                    
					continue;
				}
				
				if (_v0d >= 0 && _v1d < 0) {
					_v0w = _v0d;
					_v1w = _v1d;
					_v2w = _v2d;
					_v0 = _faceVO.v0;
	    			_v1 = _faceVO.v1;
	    			_v2 = _faceVO.v2;
	    			_uv0 = _faceVO.uv0;
	    			_uv1 = _faceVO.uv1;
	    			_uv2 = _faceVO.uv2;
				} else if (_v1d >= 0 && _v2d < 0) {
					_v0w = _v1d;
					_v1w = _v2d;
					_v2w = _v0d;
					_v0 = _faceVO.v1;
	    			_v1 = _faceVO.v2;
	    			_v2 = _faceVO.v0;
	    			_uv0 = _faceVO.uv1;
	    			_uv1 = _faceVO.uv2;
	    			_uv2 = _faceVO.uv0;
				} else if (_v2d >= 0 && _v0d < 0) {
					_v0w = _v2d;
					_v1w = _v0d;
					_v2w = _v1d;
	    			_v0 = _faceVO.v2;
	    			_v1 = _faceVO.v0;
	    			_v2 = _faceVO.v1;
	    			_uv0 = _faceVO.uv2;
	    			_uv1 = _faceVO.uv0;
	    			_uv2 = _faceVO.uv1;
				}
	    		
	        	_d = (_v0w - _v1w);
	        	
	        	_v01 = _cameraVarsStore.createVertex((_v1.x*_v0w - _v0.x*_v1w)/_d, (_v1.y*_v0w - _v0.y*_v1w)/_d, (_v1.z*_v0w - _v0.z*_v1w)/_d);
	        	
	        	_uv01 = _uv0? _cameraVarsStore.createUV((_uv1.u*_v0w - _uv0.u*_v1w)/_d, (_uv1.v*_v0w - _uv0.v*_v1w)/_d, _session) : null;
	    		
	        	if (_v2w < 0) {
		        	
					_d = (_v0w - _v2w);
					
	        		_v20 = _cameraVarsStore.createVertex((_v2.x*_v0w - _v0.x*_v2w)/_d, (_v2.y*_v0w - _v0.y*_v2w)/_d, (_v2.z*_v0w - _v0.z*_v2w)/_d);
	        		
	        		_uv20 = _uv0? _cameraVarsStore.createUV((_uv2.u*_v0w - _uv0.u*_v2w)/_d, (_uv2.v*_v0w - _uv0.v*_v2w)/_d, _session) : null;
	        		
	        		_newFaceVO = _faceVOs[_faceVOs.length] = _cameraVarsStore.createFaceVO(_faceVO.face, _faceVO.material, _faceVO.back,  _uv0, _uv01, _uv20);
	        		_newFaceVO.vertices[0] = _newFaceVO.v0 = _v0;
	        		_newFaceVO.vertices[1] = _newFaceVO.v1 = _v01;
	        		_newFaceVO.vertices[2] = _newFaceVO.v2 = _v20;
	        	} else {
	        		_d = (_v2w - _v1w);
	        		
	        		_v12 = _cameraVarsStore.createVertex((_v1.x*_v2w - _v2.x*_v1w)/_d, (_v1.y*_v2w - _v2.y*_v1w)/_d, (_v1.z*_v2w - _v2.z*_v1w)/_d);
	        		
	        		_uv12 = _uv0? _cameraVarsStore.createUV((_uv1.u*_v2w - _uv2.u*_v1w)/_d, (_uv1.v*_v2w - _uv2.v*_v1w)/_d, _session) : null;
	        		
	        		_newFaceVO = _faceVOs[_faceVOs.length] = _cameraVarsStore.createFaceVO(_faceVO.face, _faceVO.material, _faceVO.back, _uv0, _uv01, _uv2);
	        		_newFaceVO.vertices[0] = _newFaceVO.v0 = _v0;
	        		_newFaceVO.vertices[1] = _newFaceVO.v1 = _v01;
	        		_newFaceVO.vertices[2] = _newFaceVO.v2 = _v2;
	        		
	        		_newFaceVO = _faceVOs[_faceVOs.length] = _cameraVarsStore.createFaceVO(_faceVO.face, _faceVO.material, _faceVO.back, _uv01, _uv12, _uv2);
	        		_newFaceVO.vertices[0] = _newFaceVO.v0 = _v01;
	        		_newFaceVO.vertices[1] = _newFaceVO.v1 = _v12;
	        		_newFaceVO.vertices[2] = _newFaceVO.v2 = _v2;
	        	}	
			}
			
	        startIndices[startIndices.length] = clippedIndices.length;
		}
		
		/**
		 * @inheritDoc
		 */
        public override function rect(minX:Number, minY:Number, maxX:Number, maxY:Number):Boolean
        {
            if (this.maxX < minX)
                return false;
            if (this.minX > maxX)
                return false;
            if (this.maxY < minY)
                return false;
            if (this.minY > maxY)
                return false;

            return true;
        }
		
		public override function clone(object:Clipping = null):Clipping
        {
        	var clipping:NearfieldClipping = (object as NearfieldClipping) || new NearfieldClipping();
        	
        	super.clone(clipping);
        	
        	return clipping;
        }
    }
}