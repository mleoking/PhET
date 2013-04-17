package away3d.core.project
{
	import away3d.cameras.lenses.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.math.*;
	import away3d.core.utils.*;
	import away3d.sprites.*;
	
	import flash.utils.*;
	
	public class DofSpriteProjector implements IPrimitiveProvider
	{
		private var _view:View3D;
		private var _drawPrimitiveStore:DrawPrimitiveStore;
		private var _screenVertices:Array;
		private var _dofsprite:DofSprite2D;
		private var _lens:ILens;
		private var _dofcache:DofCache;
		private var _screenZ:Number;
		
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
        	
			_dofsprite = source as DofSprite2D;
			
			_lens = _view.camera.lens;
			
            _lens.project(viewTransform, _dofsprite.center, _screenVertices);
            
            if (_screenVertices[0] == null)
            	return;
            
            _screenZ = (_screenVertices[2] += _dofsprite.deltaZ);
            
            _dofcache = DofCache.getDofCache(_dofsprite.bitmap);
            
            consumer.primitive(_drawPrimitiveStore.createDrawScaledBitmap(source, _screenVertices, _dofsprite.smooth, _dofcache.getBitmap(_screenZ), _dofsprite.scaling*_view.camera.zoom / (1 + _screenZ / _view.camera.focus), _dofsprite.rotation));
		}
	}
}