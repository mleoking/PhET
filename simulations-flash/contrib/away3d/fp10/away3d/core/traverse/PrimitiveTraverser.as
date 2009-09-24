package away3d.core.traverse
{
	import away3d.arcane;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.clip.*;
	import away3d.core.draw.*;
	import away3d.core.geom.Frustum;
	import away3d.core.light.*;
	import away3d.core.math.*;
	import away3d.core.project.*;
	import away3d.core.render.*;
	import away3d.core.utils.*;
	import away3d.materials.*;
	
	import flash.utils.*;
    
	use namespace arcane;
	
    /**
    * Traverser that gathers drawing primitives to render the scene.
    */
    public class PrimitiveTraverser extends Traverser
    {
    	private var _view:View3D;
    	private var _clipping:Clipping;
    	private var _viewTransform:MatrixAway3D;
    	private var _cameraVarsStore:CameraVarsStore;
    	private var _nodeClassification:int;
    	private var _consumer:IPrimitiveConsumer;
    	private var _mouseEnabled:Boolean;
    	private var _mouseEnableds:Array = new Array();
    	private var _projectorDictionary:Dictionary = new Dictionary(true);
    	
		private var _light:ILightProvider;
		
		/**
		 * Defines the view being used.
		 */
		public function get view():View3D
		{
			return _view;
		}
		public function set view(val:View3D):void
		{
			_view = val;
			_mouseEnabled = true;
			_mouseEnableds.length = 0;
			_cameraVarsStore = _view.cameraVarsStore;
			
        	//setup the projector dictionary
        	_projectorDictionary[ProjectorType.CONVEX_BLOCK] = _view._convexBlockProjector;
			_projectorDictionary[ProjectorType.DIR_SPRITE] = _view._dirSpriteProjector;
			_projectorDictionary[ProjectorType.DOF_SPRITE] = _view._dofSpriteProjector;
			_projectorDictionary[ProjectorType.MESH] = _view._meshProjector;
			_projectorDictionary[ProjectorType.MOVIE_CLIP_SPRITE] = _view._movieClipSpriteProjector;
			_projectorDictionary[ProjectorType.OBJECT_CONTAINER] = _view._objectContainerProjector;
			_projectorDictionary[ProjectorType.SPRITE] = _view._spriteProjector;
		}
		    	
		/**
		 * Creates a new <code>PrimitiveTraverser</code> object.
		 */
        public function PrimitiveTraverser()
        {
        }
        
		/**
		 * @inheritDoc
		 */
		public override function match(node:Object3D):Boolean
        {
        	_clipping = _view.clipping;
        	
            if (!node.visible || (_clipping.objectCulling && !_cameraVarsStore.nodeClassificationDictionary[node]))
                return false;
            if (node is ILODObject)
                return (node as ILODObject).matchLOD(_view.camera);
            return true;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function enter(node:Object3D):void
        {
        	node;//TODO : FDT Warning
        	_mouseEnableds.push(_mouseEnabled);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function apply(node:Object3D):void
        {
        	if (node.session.updated) {
	        	_viewTransform = _cameraVarsStore.viewTransformDictionary[node];
	        	_consumer = node.session.getConsumer(_view);
	        	
	        	
	        	if (node.projectorType)
	        		(_projectorDictionary[node.projectorType] as IPrimitiveProvider).primitives(node, _viewTransform, _consumer);
	            
	            if (node.debugbb && node.debugBoundingBox.visible) {
	            	node.debugBoundingBox._session = node.session;
	            	if (_clipping.objectCulling) {
	            		_cameraVarsStore.frustumDictionary[node.debugBoundingBox] = _cameraVarsStore.frustumDictionary[node];
	            		_nodeClassification = _cameraVarsStore.nodeClassificationDictionary[node];
	            		if (_nodeClassification == Frustum.INTERSECT)
	            			(node.debugBoundingBox.material as WireframeMaterial).color = 0xFF0000;
	            		else
	            			(node.debugBoundingBox.material as WireframeMaterial).color = 0x333333;
	            	}
	            	_view._meshProjector.primitives(node.debugBoundingBox, _viewTransform, _consumer);
	            }
	            
	            if (node.debugbs && node.debugBoundingSphere.visible) {
	            	node.debugBoundingSphere._session = node.session;
	            	if (_clipping.objectCulling) {
	            		_cameraVarsStore.frustumDictionary[node.debugBoundingSphere] = _cameraVarsStore.frustumDictionary[node];
	            		_nodeClassification = _cameraVarsStore.nodeClassificationDictionary[node];
	            		if (_nodeClassification == Frustum.INTERSECT)
	            			(node.debugBoundingSphere.material as WireframeMaterial).color = 0xFF0000;
	            		else
	            			(node.debugBoundingSphere.material as WireframeMaterial).color = 0x00FFFF;
	            	}
	            	_view._meshProjector.primitives(node.debugBoundingSphere, _viewTransform, _consumer);
	            }
	            
	            if (node is ILightProvider) {
	            	_light = node as ILightProvider;
	            	if (_light.debug) {
	            		_light.debugPrimitive._session = node.session;
	            		if (_clipping.objectCulling)
	            			_cameraVarsStore.frustumDictionary[_light.debugPrimitive] = _cameraVarsStore.frustumDictionary[_light];
	            		_view._meshProjector.primitives(_light.debugPrimitive, _viewTransform, _consumer);
	            	}
	            }
	        }
	        
            _mouseEnabled = node._mouseEnabled = (_mouseEnabled && node.mouseEnabled);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function leave(node:Object3D):void
        {
        	node;//TODO : FDT Warning
        	_mouseEnabled = _mouseEnableds.pop();
        }

    }
}
