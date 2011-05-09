package away3d.core.traverse
{
	import away3d.arcane;
	import away3d.cameras.*;
	import away3d.cameras.lenses.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.clip.*;
	import away3d.core.geom.*;
	import away3d.core.math.*;
	import away3d.core.project.*;
	import away3d.core.utils.*;
	
	use namespace arcane;
	
    /**
    * Traverser that resolves the transform tree in a scene, ready for rendering.
    */
    public class ProjectionTraverser extends Traverser
    {
        private var _view:View3D;
        private var _frustum:Frustum;
        private var _cameraVarsStore:CameraVarsStore;
        private var _camera:Camera3D;
        private var _lens:ILens;
        private var _clipping:Clipping;
        private var _cameraViewMatrix:MatrixAway3D;
        private var _viewTransform:MatrixAway3D;
        private var _nodeClassification:int;
        private var _mesh:Mesh;
		
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
			_cameraVarsStore = val.cameraVarsStore;
			_clipping = val.clipping;
			_camera = val.camera;
			_lens = _camera.lens;
            _cameraViewMatrix = _camera.viewMatrix;
			if (val.statsOpen)
				val.statsPanel.clearObjects();
		}
		    	
		/**
		 * Creates a new <code>ProjectionTraverser</code> object.
		 */
        public function ProjectionTraverser()
        {
        }
        
		/**
		 * @inheritDoc
		 */
        public override function match(node:Object3D):Boolean
        {
        	//check if node is visible
            if (!node.visible)
                return false;
            
            //compute viewTransform matrix
            _viewTransform = _cameraVarsStore.createViewTransform(node);
            _viewTransform.multiply(_cameraViewMatrix, node.sceneTransform);
            
            if (_clipping.objectCulling) {
	        	_frustum = _lens.getFrustum(node, _viewTransform);
	        	
	            if ((node is Scene3D || _cameraVarsStore.nodeClassificationDictionary[node.parent] == Frustum.INTERSECT)) {
	            	if (node.pivotZero)
	            		_nodeClassification = _cameraVarsStore.nodeClassificationDictionary[node] = _frustum.classifyRadius(node.boundingRadius);
	            	else
	            		_nodeClassification = _cameraVarsStore.nodeClassificationDictionary[node] = _frustum.classifySphere(node.pivotPoint, node.boundingRadius);
	            } else {
	            	_nodeClassification = _cameraVarsStore.nodeClassificationDictionary[node] = _cameraVarsStore.nodeClassificationDictionary[node.parent];
	            }
	            
	            if (_nodeClassification == Frustum.OUT) {
	            	node.updateObject();
	            	return false;
	            }
            }
            
            //check which LODObject is visible
            if (node is ILODObject)
                return (node as ILODObject).matchLOD(_camera);
            
            return true;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function enter(node:Object3D):void
        {
        	if (_view.statsOpen && node is Mesh)
        		_view.statsPanel.addObject(node as Mesh);
        }
        
        public override function apply(node:Object3D):void
        {
            if (node.projectorType == ProjectorType.CONVEX_BLOCK)
                _view.blockers[node] = node;
            
        	//add to scene meshes dictionary
            if ((_mesh = node as Mesh)) {
            	if (!_view.scene.meshes[node])
            		_view.scene.meshes[node] = [];
            	
            	_view.scene.meshes[node].push(_view);
            	
            	_view.scene.geometries[_mesh.geometry] = _mesh.geometry;
            }
        }
        
        public override function leave(node:Object3D):void
        {
            //update object
            node.updateObject();
        }
    }
}
