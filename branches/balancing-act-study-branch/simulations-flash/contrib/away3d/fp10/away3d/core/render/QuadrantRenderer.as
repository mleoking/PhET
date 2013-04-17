package away3d.core.render
{
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.clip.*;
	import away3d.core.draw.*;
	import away3d.core.filter.*;
    

    /** Renderer that uses quadrant tree for storing and operating drawing primitives. Quadrant tree speeds up all proximity based calculations. */
    public class QuadrantRenderer implements IPrimitiveConsumer, IRenderer
    {
        private var _qdrntfilters:Array;
        private var _root:PrimitiveQuadrantTreeNode;
		private var _center:Array;
		private var _result:Array = new Array();
		private var _list:Array = new Array();
		private var _except:Object3D;
		private var _minX:Number;
		private var _minY:Number;
		private var _maxX:Number;
		private var _maxY:Number;
		private var _child:DrawPrimitive;
		private var _children:Array;
		private var i:int;
		private var _primitives:Array = new Array();
        private var _scene:Scene3D;
        private var _camera:Camera3D;
        private var _screenClipping:Clipping;
		
		private function getList(node:PrimitiveQuadrantTreeNode, result:Array):void
        {
            if (node.onlysourceFlag && _except == node.onlysource)
                return;
            
            if (_minX < node.xdiv)
            {
                if (node.lefttopFlag && _minY < node.ydiv)
	                getList(node.lefttop, result);
	            
                if (node.leftbottomFlag && _maxY > node.ydiv)
                	getList(node.leftbottom, result);
            }
            
            if (_maxX > node.xdiv)
            {
                if (node.righttopFlag && _minY < node.ydiv)
                	getList(node.righttop, result);
                
                if (node.rightbottomFlag && _maxY > node.ydiv)
                	getList(node.rightbottom, result);
                
            }
            
            _children = node.center;
            if (_children != null) {
                i = _children.length;
                while (i--)
                {
                	_child = _children[i];
                    if ((_except == null || _child.source != _except) && _child.maxX > _minX && _child.minX < _maxX && _child.maxY > _minY && _child.minY < _maxY)
                        result.push(_child);
                }
            }
        }
        
        private function getParent(node:PrimitiveQuadrantTreeNode, result:Array):void
        {
        	node = node.parent;
        	
            if (node == null || (node.onlysourceFlag && _except == node.onlysource))
                return;

            _children = node.center;
            if (_children != null) {
                i = _children.length;
                while (i--)
                {
                	_child = _children[i];
                    if ((_except == null || _child.source != _except) && _child.maxX > _minX && _child.minX < _maxX && _child.maxY > _minY && _child.minY < _maxY)
                        result.push(_child);
                }
            }
            getParent(node, result);
        }
		
		/**
		 * Defines the array of filters to be used on the drawing primitives.
		 */
		public function get filters():Array
		{
			return _qdrntfilters;
		}
		
		public function set filters(val:Array):void
		{
			_qdrntfilters = val;
		}
		
		/**
		 * Creates a new <code>QuadrantRenderer</code> object.
		 *
		 * @param	filters	[optional]	An array of filters to use on projected drawing primitives before rendering them to screen.
		 */
        public function QuadrantRenderer(...filters)
        {
            _qdrntfilters = filters;
        }
		
		/**
		 * @inheritDoc
		 */
        public function primitive(pri:DrawPrimitive):Boolean
        {
        	if (!_screenClipping.checkPrimitive(pri))
        		return false;
			
            _root.push(pri);
            
            return true;
        }
        
        /**
        * removes a drawing primitive from the quadrant tree.
        * 
        * @param	pri	The drawing primitive to remove.
        */
        public function remove(pri:DrawPrimitive):void
        {
        	_center = pri.quadrant.center;
        	_center.splice(_center.indexOf(pri), 1);
        }
		
		/**
		 * Returns an array containing all primiives overlapping the specifed primitive's quadrant.
		 * 
		 * @param	pri					The drawing primitive to check.
		 * @param	ex		[optional]	Excludes primitives that are children of the 3d object.
		 * @return						An array of drawing primitives.
		 */
        public function get(pri:DrawPrimitive, ex:Object3D = null):Array
        {
        	_result.length = 0;
                    
			_minX = pri.minX;
			_minY = pri.minY;
			_maxX = pri.maxX;
			_maxY = pri.maxY;
			_except = ex;
			
            getList(pri.quadrant, _result);
            getParent(pri.quadrant, _result);
            return _result;
        }
        
		/**
		 * A list of primitives that have been clipped.
		 * 
		 * @return	An array containing the primitives to be rendered.
		 */
        public function list():Array
        {
            _list.length = 0;
                    
			_minX = -1000000;
			_minY = -1000000;
			_maxX = 1000000;
			_maxY = 1000000;
			_except = null;
			
            getList(_root, _list);
            
            return _list;
        }
        
        public function clear(view:View3D):void
        {
        	_primitives.length = 0;
			_scene = view.scene;
			_camera = view.camera;
			_screenClipping = view.screenClipping;
			
			if (!_root)
				_root = new PrimitiveQuadrantTreeNode((_screenClipping.minX + _screenClipping.maxX)/2, (_screenClipping.minY + _screenClipping.maxY)/2, _screenClipping.maxX - _screenClipping.minX, _screenClipping.maxY - _screenClipping.minY, 0);
			else
				_root.reset((_screenClipping.minX + _screenClipping.maxX)/2, (_screenClipping.minY + _screenClipping.maxY)/2, _screenClipping.maxX - _screenClipping.minX, _screenClipping.maxY - _screenClipping.minY);	
        }
        
        public function render(view:View3D):void
        {
			
        	//filter primitives array
			for each (var _filter:IPrimitiveQuadrantFilter in _qdrntfilters)
        		_filter.filter(this, _scene, _camera, _screenClipping);
        	
    		// render all primitives
            _root.render(-Infinity);
        }
        
		/**
		 * @inheritDoc
		 */
        public function toString():String
        {
            return "Quadrant ["+ _qdrntfilters.join("+") + "]";
        }
        
        public function clone():IPrimitiveConsumer
        {
        	var renderer:QuadrantRenderer = new QuadrantRenderer();
        	renderer.filters = filters;
        	return renderer;
        }
    }
}
