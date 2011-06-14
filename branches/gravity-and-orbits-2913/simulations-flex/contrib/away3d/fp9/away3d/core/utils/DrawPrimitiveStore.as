package away3d.core.utils
{
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.block.*;
	import away3d.core.draw.*;
	import away3d.core.render.*;
	import away3d.materials.*;
	
	import flash.display.*;
	import flash.utils.*;
	
	public class DrawPrimitiveStore
	{
		private var _screenVertices:Array = new Array();
		private var _screenIndices:Array = new Array();
		private var _screenCommands:Array = new Array();
		private var _indexDictionary:Dictionary;
		private var _index:int;
		private var _length:int;
		private var _object:Object;
		private var _vertex:Object;
		private var _source:Object3D;
		private var _session:AbstractRenderSession;
		private var _sv:ScreenVertex;
		private var _bill:DrawBillboard;
		private var _seg:DrawSegment;
		private var _tri:DrawTriangle;
		private var _array:Array = new Array();
		private var _cblocker:ConvexBlocker;
		private var _sbitmap:DrawScaledBitmap;
		private var _dobject:DrawDisplayObject;
		private var _svArray:Array;
		private var _svStore:Array = [];
		private var _dtDictionary:Dictionary = new Dictionary(true);
		private var _dtArray:Array;
		private var _dtStore:Array = [];
		private var _dsDictionary:Dictionary = new Dictionary(true);
		private var _dsArray:Array;
        private var _dsStore:Array = [];
        private var _dbDictionary:Dictionary = new Dictionary(true);
		private var _dbArray:Array;
        private var _dbStore:Array = [];
        private var _cbDictionary:Dictionary = new Dictionary(true);
		private var _cbArray:Array;
		private var _cbStore:Array = [];
		private var _sbDictionary:Dictionary = new Dictionary(true);
		private var _sbArray:Array;
		private var _sbStore:Array = [];
		private var _doDictionary:Dictionary = new Dictionary(true);
		private var _doArray:Array;
        private var _doStore:Array = [];
        
		public var view:View3D;
		
		public var blockerDictionary:Dictionary = new Dictionary(true);
		
		public function reset():void
		{
			for (_object in _dtDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_dtArray = _dtDictionary[_session] as Array;
					if (_dtArray.length) {
						_dtStore = _dtStore.concat(_dtArray);
						_dtArray.length = 0;
					}
				}
			}
			
			for (_object in _dsDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_dsArray = _dsDictionary[_session] as Array
					if (_dsArray.length) {
						_dsStore = _dsStore.concat(_dsArray);
						_dsArray.length = 0;
					}
				}
			}
			
			for (_object in _dbDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_dbArray = _dbDictionary[_session] as Array;
					if (_dbArray.length) {
						_dbStore = _dbStore.concat(_dbArray);
						_dbArray.length = 0;
					}
				}
			}
			
			for (_object in _cbDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_cbArray = _cbDictionary[_session] as Array;
					if (_cbArray.length) {
						_cbStore = _cbStore.concat(_cbArray);
						_cbArray.length = 0;
					}
				}
			}
			
			for (_object in _sbDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_sbArray = _sbDictionary[_session] as Array;
					if (_sbArray.length) {
						_sbStore = _sbStore.concat(_sbArray);
						_sbArray.length = 0;
					}
				}
			}
			
			for (_object in _doDictionary) {
				_session = _object as AbstractRenderSession;
				if (_session.updated) {
					_doArray = _doDictionary[_session] as Array;
					if (_doArray.length) {
						_doStore = _doStore.concat(_doArray);
						_doArray.length = 0;
					}
				}
			}
		}
		
		public function getScreenVertices(id:int):Array
		{
			return _screenVertices[id] || (_screenVertices[id] = []);
		}
		
		public function getScreenIndices(id:int):Array
		{
			return _screenIndices[id] || (_screenIndices[id] = []);
		}
		
		public function getScreenCommands(id:int):Array
		{
			return _screenCommands[id] || (_screenCommands[id] = []);
		}
		
	    public function createDrawBillboard(source:Object3D, billboardVO:BillboardVO, material:IBillboardMaterial, screenVertices:Array, screenIndices:Array, index:uint, scale:Number, generated:Boolean = false):DrawBillboard
	    {
	    	if (!(_dbArray = _dbDictionary[source.session]))
				_dbArray = _dbDictionary[source.session] = [];
			
	        if (_dbStore.length) {
	        	_dbArray.push(_bill = _dbStore.pop());
	    	} else {
	        	_dbArray.push(_bill = new DrawBillboard());
	            _bill.view = view;
	            _bill.create = createDrawBillboard;
	        }
	        _bill.generated = generated;
	        _bill.source = source;
	        _bill.material = material;
	        _bill.billboardVO = billboardVO;
	        _bill.screenVertices = screenVertices;
	        _bill.screenIndices = screenIndices;
	        _bill.index = index;
	        _bill.width = billboardVO.width;
	        _bill.height = billboardVO.height;
	        _bill.rotation = billboardVO.rotation;
	        _bill.scale = scale;
	        _bill.calc();
	        
	        return _bill;
	    }
	    
	    public function createDrawSegment(source:Object3D, segmentVO:SegmentVO, material:ISegmentMaterial, screenVertices:Array, screenIndices:Array, screenCommands:Array, startIndex:int, endIndex:int, generated:Boolean = false):DrawSegment
	    {
	    	if (!(_dsArray = _dsDictionary[source.session]))
				_dsArray = _dsDictionary[source.session] = [];
			
	        if (_dsStore.length) {
	        	_dsArray[_dsArray.length] = _seg = _dsStore.pop();
	    	} else {
	        	_dsArray[_dsArray.length] = _seg = new DrawSegment();
	            _seg.view = view;
	            _seg.create = createDrawSegment;
	        }
	        _seg.generated = generated;
	        _seg.source = source;
	        _seg.segmentVO = segmentVO;
	        _seg.material = material;
	        _seg.screenVertices = screenVertices;
	        _seg.screenIndices = screenIndices;
	        _seg.screenCommands = screenCommands;
	        _seg.startIndex = startIndex;
	        _seg.endIndex = endIndex;
	        _seg.calc();
	        
	        return _seg;
	    }
	    
		public function createDrawTriangle(source:Object3D, faceVO:FaceVO, material:ITriangleMaterial, screenVertices:Array, screenIndices:Array, screenCommands:Array, startIndex:int, endIndex:int, uv0:UV, uv1:UV, uv2:UV, generated:Boolean = false):DrawTriangle
		{
			if (!(_dtArray = _dtDictionary[source.session]))
				_dtArray = _dtDictionary[source.session] = [];
			
			if (_dtStore.length) {
	        	_dtArray[_dtArray.length] = _tri = _dtStore.pop();
	   		} else {
	        	_dtArray[_dtArray.length] = _tri = new DrawTriangle();
		        _tri.view = view;
		        _tri.create = createDrawTriangle;
	        }
	        
	        _tri.reverseArea = faceVO.reverseArea;
	        _tri.generated = generated;
	        _tri.source = source;
	        _tri.faceVO = faceVO;
	        _tri.material = material;
	        _tri.screenVertices = screenVertices;
	        _tri.screenIndices = screenIndices;
	        _tri.screenCommands = screenCommands;
	        _tri.startIndex = startIndex;
	        _tri.endIndex = endIndex;
	        _tri.uv0 = uv0;
	        _tri.uv1 = uv1;
	        _tri.uv2 = uv2;
	    	_tri.calc();
	        
	        return _tri;
		}
	    
		public function createConvexBlocker(source:Object3D, vertices:Array):ConvexBlocker
		{
			if (!(_cbArray = _cbDictionary[source.session]))
				_cbArray = _cbDictionary[source.session] = [];
			
			if (_cbStore.length) {
	        	_cbArray[_cbArray.length] = _cblocker = blockerDictionary[source] = _cbStore.pop();
	   		} else {
	        	_cbArray[_cbArray.length] = _cblocker = blockerDictionary[source] = new ConvexBlocker();
		        _cblocker.view = view;
		        _cblocker.create = createConvexBlocker;
	        }
	        
	        _cblocker.source = source;
	        _cblocker.vertices = vertices;
	        _cblocker.calc();
	        
	        return _cblocker;
	    }
	    
	    public function createDrawScaledBitmap(source:Object3D, screenVertices:Array, smooth:Boolean, bitmap:BitmapData, scale:Number, rotation:Number, generated:Boolean = false):DrawScaledBitmap
	    {
	    	if (!(_sbArray = _sbDictionary[source.session]))
				_sbArray = _sbDictionary[source.session] = [];
			
	        if (_sbStore.length) {
	        	_sbArray[_sbArray.length] = _sbitmap = _sbStore.pop();
	    	} else {
	        	_sbArray[_sbArray.length] = _sbitmap = new DrawScaledBitmap();
	            _sbitmap.view = view;
	            _sbitmap.create = createDrawSegment;
	        }
	        _sbitmap.generated = generated;
	        _sbitmap.source = source;
	        _sbitmap.vx = screenVertices[0];
	        _sbitmap.vy = screenVertices[1];
	        _sbitmap.vz = screenVertices[2];
	        _sbitmap.smooth = smooth;
	        _sbitmap.bitmap = bitmap;
	        _sbitmap.scale = scale;
	        _sbitmap.rotation = rotation;
	        _sbitmap.calc();
	        
	        return _sbitmap;
	    }
	    
	    public function createDrawDisplayObject(source:Object3D, vx:Number, vy:Number, vz:Number, session:AbstractRenderSession, displayobject:DisplayObject, generated:Boolean = false):DrawDisplayObject
	    {
	    	if (!(_doArray = _doDictionary[source.session]))
				_doArray = _doDictionary[source.session] = [];
			
			if (_doStore.length) {
	        	_doArray[_doArray.length] = _dobject = _doStore.pop();
	    	} else {
	        	_doArray[_doArray.length] = _dobject = new DrawDisplayObject();
	            _dobject.view = view;
	            _dobject.create = createDrawSegment;
	        }
	        _dobject.generated = generated;
	        _dobject.source = source;
	        _dobject.vx = vx;
	        _dobject.vy = vy;
	        _dobject.vz = vz;
	        _dobject.session = session;
	        _dobject.displayobject = displayobject;
	        _dobject.calc();
	        
	        return _dobject;
	    }
	}
}