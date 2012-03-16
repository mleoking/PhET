package away3d.core.project
{
	import away3d.blockers.*;
	import away3d.cameras.*;
	import away3d.cameras.lenses.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.block.*;
	import away3d.core.draw.*;
	import away3d.core.math.*;
	import away3d.core.utils.*;
	
	import flash.utils.*;
	
	public class ConvexBlockProjector implements IBlockerProvider, IPrimitiveProvider
	{
		private var _view:View3D;
		private var _drawPrimitiveStore:DrawPrimitiveStore;
		private var _convexBlock:ConvexBlock;
		private var _camera:Camera3D;
		private var _lens:ILens;
		private var _vertices:Array;
		private var _screenVertices:Array;
		private var _index:int;
		private var _i:int;
        private var _points:Array = [];
        private var _pointsN:Array = [];
        private var _pointsS:Array = [];
        private var _screenX:Number;
        private var _screenY:Number;
        private var _baseX:Number;
        private var _baseY:Number;
        private var _baseZ:Number;
        private var _baseIndex:Number;
        private var _s:String;
        private var _p:String;
        
        private function cross(ax:Number, ay:Number, bx:Number, by:Number, cx:Number, cy:Number):Number
        {
            return (bx - ax)*(cy - ay) - (cx - ax)*(by - ay);
        }
        
        public function get view():View3D
        {
        	return _view;
        }
        public function set view(val:View3D):void
        {
        	_view = val;
        	_drawPrimitiveStore = _view.drawPrimitiveStore;
        }
        
		/**
		 * @inheritDoc
		 * 
    	 * @see	away3d.core.traverse.BlockerTraverser
    	 * @see	away3d.core.block.Blocker
		 */
        public function blockers(source:Object3D, viewTransform:MatrixAway3D, consumer:IBlockerConsumer):void
        {
			_screenVertices = _drawPrimitiveStore.getScreenVertices(source.id);
			
        	_convexBlock = source as ConvexBlock;
			
			_camera = _view.camera;
			_lens = _camera.lens;
			
			_vertices = _convexBlock.vertices;
            
        	if (_vertices.length < 3)
                return;
			
			_points.length = 0;
			_pointsN.length = 0;
			_pointsS.length = 0;
			_baseX = Infinity;
			_baseY = Infinity;
			_baseZ = Infinity;
			
			_p = "";
            
            _lens.project(viewTransform, _vertices, _screenVertices);
            
			_index = _screenVertices.length/3;
            while (_index--) {
            	_screenX = _screenVertices[_index*3];
            	_screenY = _screenVertices[_index*3+1];
            	
                if (_baseY > _screenY || _baseY == _screenY && _baseX > _screenX) {
                    _baseX = _screenX;
                    _baseY = _screenY;
                    _baseZ = _screenVertices[_index*3+2];
                    _baseIndex = _index;
                }
				
                _points[_points.length] = _baseX;
                _points[_points.length] = _baseY;
                _points[_points.length] = _baseZ;
            }
			
			_index = _points.length/3;
            while (_index--)
                _pointsN[_index] = (_points[_index*3] - _baseX) / (_points[_index*3+1] - _baseY);
            
            _pointsN[_baseIndex] = -Infinity;
            
			_pointsN = _pointsN.sort(Array.NUMERIC | Array.RETURNINDEXEDARRAY);
			
			_index = 0;
            while (_index < _pointsN.length) {
            	_i = _pointsN[_index]*3;
            	_pointsS[_pointsS.length] = _points[_i];
            	_pointsS[_pointsS.length] = _points[_i+1];
            	_pointsS[_pointsS.length] = _points[_i+2];
            	_index++;
            }
            
            var result:Array = [_pointsS[0], _pointsS[1], _pointsS[2], _pointsS[3], _pointsS[4], _pointsS[5]];
            var o:Number;
			var length:int;
			
            for (_i = 2; _i < _pointsS.length; ++_i)
            {
            	length = result.length;
                o = cross(result[length-6], result[length-5], result[length-3], result[length-2], _pointsS[_i*3], _pointsS[_i*3+1]);
                while (o > 0) {
                    result.pop();
                    result.pop();
                    result.pop();
                    if (result.length == 6)
                        break;
                    length = result.length;
                    o = cross(result[length-6], result[length-5], result[length-3], result[length-2], _pointsS[_i*3], _pointsS[_i*3+1]);
                }
                result.push(_pointsS[_i*3], _pointsS[_i*3+1], _pointsS[_i*3+2]);
            }
            length = result.length;
            o = cross(result[length-6], result[length-5], result[length-3], result[length-2], result[0], result[1]);
            if (o > 0) {
                result.pop();
                result.pop();
                result.pop();
            }
            consumer.blocker(_drawPrimitiveStore.createConvexBlocker(source, result));
 		}
 		
		public function primitives(source:Object3D, viewTransform:MatrixAway3D, consumer:IPrimitiveConsumer):void
		{
			_convexBlock = source as ConvexBlock;
			
        	if (_convexBlock.debug)
                consumer.primitive(_drawPrimitiveStore.blockerDictionary[source]);
		}
	}
}