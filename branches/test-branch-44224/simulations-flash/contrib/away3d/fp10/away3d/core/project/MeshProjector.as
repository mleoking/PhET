package away3d.core.project
{
	import away3d.cameras.*;
	import away3d.cameras.lenses.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.clip.*;
	import away3d.core.draw.*;
	import away3d.core.geom.*;
	import away3d.core.math.*;
	import away3d.core.utils.*;
	import away3d.materials.*;
	
	import flash.utils.Dictionary;
	
	public class MeshProjector implements IPrimitiveProvider
	{
		private var _view:View3D;
		private var _drawPrimitiveStore:DrawPrimitiveStore;
		private var _cameraVarsStore:CameraVarsStore;
		private var _screenVertices:Array;
		private var _screenIndices:Array;
		private var _screenCommands:Array;
		private var _mesh:Mesh;
		private var _clipFlag:Boolean;
		private var _vertex:Vertex;
		private var _defaultStartIndices:Array = new Array();
		private var _startIndices:Array;
		private var _defaultVertices:Array = new Array();
		private var _vertices:Array;
		private var _defaultClippedFaceVOs:Array = new Array();
		private var _faceVOs:Array;
		private var _defaultClippedSegmentVOs:Array = new Array();
		private var _segmentVOs:Array;
		private var _defaultClippedBillboards:Array = new Array();
		private var _billboardVOs:Array;
		private var _camera:Camera3D;
		private var _clipping:Clipping;
		private var _lens:ILens;
		private var _focus:Number;
		private var _zoom:Number;
		private var _outlineIndices:Dictionary = new Dictionary(true);
		private var _faceMaterial:ITriangleMaterial;
		private var _segmentMaterial:ISegmentMaterial;
		private var _billboardMaterial:IBillboardMaterial;
		private var _face:Face;
		private var _faceVO:FaceVO;
		private var _index:int;
		private var _startIndex:int;
		private var _endIndex:int;
		private var _tri:DrawTriangle;
        private var _backface:Boolean;
		private var _backmat:ITriangleMaterial;
		private var _segment:Segment;
		private var _segmentVO:SegmentVO;
		private var _seg:DrawSegment;
		private var _smaterial:ISegmentMaterial;
		private var _billboardVO:BillboardVO;
		private var _bmaterial:IBillboardMaterial;
		private var _n01:Face;
		private var _n12:Face;
		private var _n20:Face;
		
		private var _sv0x:Number;
		private var _sv0y:Number;
		private var _sv1x:Number;
		private var _sv1y:Number;
		private var _sv2x:Number;
		private var _sv2y:Number;
		
		private var _i:int;
		
        private function front(startIndex:int):Number
        {
            _index = _screenIndices[startIndex]*3;
        	_sv0x = _screenVertices[_index];
        	_sv0y = _screenVertices[_index+1];
        	
            _index = _screenIndices[startIndex+1]*3;
        	_sv1x = _screenVertices[_index];
        	_sv1y = _screenVertices[_index+1];
        	
            _index = _screenIndices[startIndex+2]*3;
        	_sv2x = _screenVertices[_index];
        	_sv2y = _screenVertices[_index+1];
        	
            return (_sv0x*(_sv2y - _sv1y) + _sv1x*(_sv0y - _sv2y) + _sv2x*(_sv1y - _sv0y));
        }
        
        public function get view():View3D
        {
        	return _view;
        }
        public function set view(val:View3D):void
        {
        	_view = val;
        	_drawPrimitiveStore = view.drawPrimitiveStore;
        	_cameraVarsStore = view.cameraVarsStore;
        }
        
		public function primitives(source:Object3D, viewTransform:MatrixAway3D, consumer:IPrimitiveConsumer):void
		{
			_cameraVarsStore.createVertexClassificationDictionary(source);
			
			_mesh = source as Mesh;
			
			_camera = _view.camera;
			_clipping = _view.screenClipping;
			_lens = _camera.lens;
        	_focus = _camera.focus;
        	_zoom = _camera.zoom;
        	
			_faceMaterial = _mesh.faceMaterial;
			_segmentMaterial = _mesh.segmentMaterial;
			_billboardMaterial = _mesh.billboardMaterial;
			
			_backmat = _mesh.back || _faceMaterial;
			
            //check if an element needs clipping
            _clipFlag = _cameraVarsStore.nodeClassificationDictionary[source] == Frustum.INTERSECT && !(_clipping is RectangleClipping);
            
			if (_clipFlag) {
            	_vertices = _defaultVertices;
				_vertices.length = 0;
            	_screenCommands = _drawPrimitiveStore.getScreenCommands(source.id);
				_screenCommands.length = 0;
				_screenIndices = _drawPrimitiveStore.getScreenIndices(source.id);
				_screenIndices.length = 0;
            	_startIndices = _defaultStartIndices;
				_startIndices.length = 0;
            	_faceVOs = _defaultClippedFaceVOs;
				_faceVOs.length = 0;
            	_segmentVOs = _defaultClippedSegmentVOs;
				_segmentVOs.length = 0;
            	_billboardVOs = _defaultClippedBillboards;
				_billboardVOs.length = 0;
            	_clipping.checkElements(_mesh, _faceVOs, _segmentVOs, _billboardVOs, _vertices, _screenCommands, _screenIndices, _startIndices);
			} else {
            	_vertices = _mesh.vertices;
            	_screenCommands = _mesh.commands;
            	_screenIndices = _mesh.indices;
            	_startIndices = _mesh.startIndices;
            	_faceVOs = _mesh.faceVOs;
            	_segmentVOs = _mesh.segmentVOs;
            	_billboardVOs = _mesh.billboardVOs;
            }
            
			_screenVertices = _drawPrimitiveStore.getScreenVertices(source.id);
			_screenVertices.length = 0;
            _lens.project(viewTransform, _vertices, _screenVertices);
            
            if (_mesh.outline) {
            	_i = _faceVOs.length;
            	while (_i--)
            		_outlineIndices[_faceVOs[_i]] = _i;
            }
            
            _i = 0;
            
			//loop through all clipped faces
            for each (_faceVO in _faceVOs) {
				
				_startIndex = _startIndices[_i++];
                _endIndex = _startIndices[_i];
                
				if (!_clipFlag) {
					_index = _startIndex;
					
					while (_screenVertices[_screenIndices[_index]*3] != null && _index < _endIndex)
						_index++;
					
					if (_index < _endIndex)
						continue;
				}
                
                
            	_face = _faceVO.face;
            	
            	_tri = _drawPrimitiveStore.createDrawTriangle(source, _faceVO, null, _screenVertices, _screenIndices, _screenCommands, _startIndex, _endIndex, _faceVO.uv0, _faceVO.uv1, _faceVO.uv2, _faceVO.generated);
            	
				//determine if _triangle is facing towards or away from camera
                _backface = _tri.backface = _tri.area < 0;
				
				//if _triangle facing away, check for backface material
                if (_backface) {
                    if (!_mesh.bothsides)
                    	continue;
                    
                    _tri.material = _faceVO.back;
                    
                    if (!_tri.material)
                    	_tri.material = _faceVO.material;
                } else {
                    _tri.material = _faceVO.material;
                }
                
				//determine the material of the _triangle
                if (!_tri.material) {
                    if (_backface)
                        _tri.material = _backmat;
                    else
                        _tri.material = _faceMaterial;
                }
                
				//do not draw material if visible is false
                if (_tri.material && !_tri.material.visible)
                    _tri.material = null;
				
				//if there is no material and no outline, continue
                if (!_mesh.outline && !_tri.material)
                	continue;
                
                //check whether screenClipping removes triangle
                if (!consumer.primitive(_tri))
                	continue;
				
                if (_mesh.pushback)
                    _tri.screenZ = _tri.maxZ;
				
                if (_mesh.pushfront)
                    _tri.screenZ = _tri.minZ;
				
				_tri.screenZ += _mesh.screenZOffset;
				
                if (_mesh.outline && !_backface) {
                    _n01 = _mesh.geometry.neighbour01(_face);
                    if (_n01 == null || front(_startIndices[_outlineIndices[_n01.faceVO]]) <= 0) {
                    	_segmentVO = _cameraVarsStore.createSegmentVO(_mesh.outline);
                    	_startIndex = _screenIndices.length;
                    	_screenIndices[_screenIndices.length] = _screenIndices[_tri.startIndex];
                    	_screenIndices[_screenIndices.length] = _screenIndices[_tri.startIndex+1];
                    	_endIndex = _screenIndices.length;
                    	consumer.primitive(_drawPrimitiveStore.createDrawSegment(source, _segmentVO, _mesh.outline, _screenVertices, _screenIndices, _screenCommands, _startIndex, _endIndex));
                    }
					
                    _n12 = _mesh.geometry.neighbour12(_face);
                    if (_n12 == null || front(_startIndices[_outlineIndices[_n12.faceVO]]) <= 0) {
                    	_segmentVO = _cameraVarsStore.createSegmentVO(_mesh.outline);
                    	_startIndex = _screenIndices.length;
                    	_screenIndices[_screenIndices.length] = _screenIndices[_tri.startIndex+1];
                    	_screenIndices[_screenIndices.length] = _screenIndices[_tri.startIndex+2];
                    	_endIndex = _screenIndices.length;
                    	consumer.primitive(_drawPrimitiveStore.createDrawSegment(source, _segmentVO, _mesh.outline, _screenVertices, _screenIndices, _screenCommands, _startIndex, _endIndex));
                    }
                    
                    _n20 = _mesh.geometry.neighbour20(_face);
                    if (_n20 == null || front(_startIndices[_outlineIndices[_n20.faceVO]]) <= 0) {
                    	_segmentVO = _cameraVarsStore.createSegmentVO(_mesh.outline);
                    	_startIndex = _screenIndices.length;
                    	_screenIndices[_screenIndices.length] = _screenIndices[_tri.startIndex+2];
                    	_screenIndices[_screenIndices.length] = _screenIndices[_tri.startIndex];
                    	_endIndex = _screenIndices.length;
                    	consumer.primitive(_drawPrimitiveStore.createDrawSegment(source, _segmentVO, _mesh.outline, _screenVertices, _screenIndices, _screenCommands, _startIndex, _endIndex));
                    }
                }
            }
            
            for each (_segmentVO in _segmentVOs)
            {
				_startIndex = _startIndices[_i++];
                _endIndex = _startIndices[_i];
                
				if (!_clipFlag) {
					_index = _startIndex;
					
					while (_screenVertices[_screenIndices[_index]*3] != null && _index < _endIndex)
						_index++;
					
					if (_index < _endIndex)
						continue;
				}
				
            	_smaterial = _segmentVO.material || _segmentMaterial;
				
                if (!_smaterial.visible)
                    continue;
                
                _seg = _drawPrimitiveStore.createDrawSegment(source, _segmentVO, _smaterial, _screenVertices, _screenIndices, _screenCommands, _startIndex, _endIndex, _segmentVO.generated)
                
                //check whether screenClipping removes segment
                if (!consumer.primitive(_seg))
                	continue;
				
                if (_mesh.pushback)
                    _seg.screenZ = _seg.maxZ;
				
                if (_mesh.pushfront)
                    _seg.screenZ = _seg.minZ;
                
				_seg.screenZ += _mesh.screenZOffset;
            }
            
            //loop through all clipped billboards
            for each (_billboardVO in _billboardVOs)
            {
            	_index = _startIndices[_i++];
				
				if(!_clipFlag && _screenVertices[_screenIndices[_index]*3] == null)
					continue;
                
                _bmaterial = _billboardVO.material || _billboardMaterial;
                
                if (!_bmaterial.visible)
                    continue;
		        
	            consumer.primitive(_drawPrimitiveStore.createDrawBillboard(source, _billboardVO, _bmaterial, _screenVertices, _screenIndices, _index, _billboardVO.scaling*_zoom / (1 + _screenVertices[_screenIndices[_index]*3+2] / _focus)));
            }
		}
	}
}