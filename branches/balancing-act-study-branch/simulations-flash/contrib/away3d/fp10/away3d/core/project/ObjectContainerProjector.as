package away3d.core.project
{
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.math.*;
	import away3d.core.render.SpriteRenderSession;
	import away3d.core.utils.*;
	
	import flash.utils.*;
	
	public class ObjectContainerProjector implements IPrimitiveProvider
	{
		private var _view:View3D;
		private var _drawPrimitiveStore:DrawPrimitiveStore;
		private var _cameraViewMatrix:MatrixAway3D;
		private var _viewTransformDictionary:Dictionary;
		private var _container:ObjectContainer3D;
		private var _vx:Number;
		private var _vy:Number;
		private var _vz:Number;
		private var _depthPoint:Number3D = new Number3D();
		
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
			_container = source as ObjectContainer3D;
			
			_cameraViewMatrix = _view.camera.viewMatrix;
			_viewTransformDictionary = _view.cameraVarsStore.viewTransformDictionary;
			
			var _container_children:Array = _container.children;
			var child:Object3D;
        	for each (child in _container_children) {
				if (child.ownCanvas && child.visible) {
					
					if (child.ownSession is SpriteRenderSession)
						(child.ownSession as SpriteRenderSession).cacheAsBitmap = true;
					
					_vx = child.screenXOffset;
					_vy = child.screenYOffset;
					
					if (!isNaN(child.ownSession.screenZ)) {
						_vz = child.ownSession.screenZ;
					} else {
						if (child.scenePivotPoint.modulo) {
							_depthPoint.clone(child.scenePivotPoint);
							_depthPoint.rotate(_depthPoint, _cameraViewMatrix);
							_depthPoint.add(_viewTransformDictionary[child].position, _depthPoint);
							
			             	_vz = _depthPoint.modulo;
							
						} else {
							_vz = _viewTransformDictionary[child].position.modulo;
						}
			            
		             	if (child.pushback)
		             		_vz += child.parentBoundingRadius;
		             		
		             	if (child.pushfront)
		             		_vz -= child.parentBoundingRadius;
		             		
		             	_vz += child.screenZOffset;
	    			}
	    			
	             	consumer.primitive(_drawPrimitiveStore.createDrawDisplayObject(source, _vx, _vy, _vz, _container.session, child.session.getContainer(view)));
	   			}
        	}
		}
	}
}