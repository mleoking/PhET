package away3d.core.render
{
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.block.*;
	import away3d.core.clip.*;
	import away3d.core.draw.*;
	import away3d.core.filter.*;
    
    /** 
    * Default renderer for a view.
    * Contains the main render loop for rendering a scene to a view,
    * which resolves the projection, culls any drawing primitives that are occluded or outside the viewport,
    * and then z-sorts and renders them to screen.
    */
    public class BasicRenderer implements IRenderer, IPrimitiveConsumer
    {
    	private var _filters:Array;
    	private var _filter:IPrimitiveFilter
        private var _primitives:Array = new Array();
        private var _primitive:DrawPrimitive
        private var _scene:Scene3D;
        private var _camera:Camera3D;
        private var _screenClipping:Clipping;
        private var _blockers:Array;
        
		/**
		 * Defines the array of filters to be used on the drawing primitives.
		 */
		public function get filters():Array
		{
			return _filters.slice(0, _filters.length - 1);
		}
		
		public function set filters(val:Array):void
		{
			_filters = val;
			_filters.push(new ZSortFilter());
		}
		
		/**
		 * Creates a new <code>BasicRenderer</code> object.
		 *
		 * @param	filters	[optional]	An array of filters to use on projected drawing primitives before rendering them to screen.
		 */
        public function BasicRenderer(...filters)
        {
            _filters = filters;
            _filters.push(new ZSortFilter());
        }
        
		/**
		 * @inheritDoc
		 */
        public function primitive(pri:DrawPrimitive):Boolean
        {
        	if (!_screenClipping.checkPrimitive(pri))
        		return false;
        	
            for each (var _blocker:Blocker in _blockers) {
                if (_blocker.screenZ > pri.minZ)
                    continue;
                if (_blocker.block(pri))
                    return false;
            }
            
            _primitives.push(pri);
            
            return true;
        }
		
		/**
		 * A list of primitives that have been clipped and blocked.
		 * 
		 * @return	An array containing the primitives to be rendered.
		 */
        public function list():Array
        {
            return _primitives;
        }
        
        public function clear(view:View3D):void
        {
        	_primitives.length = 0;
        	_scene = view.scene;
        	_camera = view.camera;
        	_screenClipping = view.screenClipping;
        	_blockers = view.blockerarray.list();
        }
        
        public function render(view:View3D):void
        {
        	
        	//filter primitives array
			for each (_filter in _filters)
        		_primitives = _filter.filter(_primitives, _scene, _camera, _screenClipping);
        	
    		// render all primitives
            for each (_primitive in _primitives)
                _primitive.render();
        }
        
		/**
		 * @inheritDoc
		 */
        public function toString():String
        {
            return "Basic [" + _filters.join("+") + "]";
        }
        
        public function clone():IPrimitiveConsumer
        {
        	var renderer:BasicRenderer = new BasicRenderer();
        	renderer.filters = filters;
        	return renderer;
        }
    }
}
