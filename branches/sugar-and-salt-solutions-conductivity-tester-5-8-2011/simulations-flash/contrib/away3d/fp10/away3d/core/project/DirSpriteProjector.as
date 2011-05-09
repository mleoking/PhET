package away3d.core.project
{
	import away3d.cameras.lenses.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.math.*;
	import away3d.core.utils.*;
	import away3d.sprites.*;
	
	import flash.display.*;
	import flash.utils.*;
	
	public class DirSpriteProjector implements IPrimitiveProvider
	{
		private var _view:View3D;
		private var _drawPrimitiveStore:DrawPrimitiveStore;
		private var _dirsprite:DirSprite2D;
		private var _vertices:Array;
		private var _bitmaps:Dictionary;
		private var _lens:ILens;
		private var _screenVertices:Array;
		private var _centerScreenVertices:Array = new Array();
		private var _index:int;
        
        public function get view():View3D
        {
        	return _view;
        }
        public function set view(val:View3D):void
        {
        	_view = val;
        	_drawPrimitiveStore = view.drawPrimitiveStore;
        }
        
		public function primitives(source:Object3D, viewTransform:MatrixAway3D, consumer:IPrimitiveConsumer):void
		{
			_screenVertices = _drawPrimitiveStore.getScreenVertices(source.id);
			
			_dirsprite = source as DirSprite2D;
			
			_vertices = _dirsprite.vertices;
			_bitmaps = _dirsprite.bitmaps;
			
			_lens = _view.camera.lens;
			
            if (_vertices.length == 0)
                return;
                
            var minz:Number = Infinity;
            var bitmap:BitmapData = null;
            
			_lens.project(viewTransform, _vertices, _screenVertices);
            
            _index = _screenVertices.length/3;
            while (_index--) {
                var z:Number = _screenVertices[_index*3+2];
                
                if (z < minz) {
                    minz = z;
                    bitmap = _bitmaps[_vertices[_index]];
                }
            }
			
            if (bitmap == null)
                return;
			
			_centerScreenVertices.length = 0;
			
            _lens.project(viewTransform, _dirsprite.center, _centerScreenVertices);
            
            if (_centerScreenVertices[0] == null)
            	return;
            
            _centerScreenVertices[2] += _dirsprite.deltaZ;
            
            consumer.primitive(_drawPrimitiveStore.createDrawScaledBitmap(source, _centerScreenVertices, _dirsprite.smooth, bitmap, _dirsprite.scaling*_view.camera.zoom / (1 + _screenVertices[2] / _view.camera.focus), _dirsprite.rotation));
		}
	}
}