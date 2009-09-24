package org.papervision3d.core.render.data
{
	import flash.display.Graphics;
	
	import org.papervision3d.core.clipping.draw.Clipping;
	import org.papervision3d.core.clipping.draw.RectangleClipping;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.objects.DisplayObject3D;
	   

    /**
    * Quadrant tree for storing drawing primitives
    */
    public final class QuadTree
    {
        private var _root:QuadTreeNode;
        private var _clip:Clipping;
		private var _rect:RectangleClipping;
		private var _center:Array;
		private var _result:Array;
		private var _except:DisplayObject3D;
		private var _minX:Number;
		private var _minY:Number;
		private var _maxX:Number;
		private var _maxY:Number;
		private var _child:RenderableListItem;
		private var _children:Array;
		private var i:int;
		private var _maxlevel:uint = 4;
		
		private function getList(node:QuadTreeNode):void
        {
        	if(!node)
        		return;
        	
            if (node.onlysourceFlag && _except == node.onlysource)
                return;

            if (_minX < node.xdiv)
            {
                if (node.lefttopFlag && _minY < node.ydiv)
	                getList(node.lefttop);
	            
                if (node.leftbottomFlag && _maxY > node.ydiv)
                	getList(node.leftbottom);
            }
            
            if (_maxX > node.xdiv)
            {
                if (node.righttopFlag && _minY < node.ydiv)
                	getList(node.righttop);
                
                if (node.rightbottomFlag && _maxY > node.ydiv)
                	getList(node.rightbottom);
                
            }
            
            _children = node.center;
            if (_children != null) {
                i = _children.length;
                while (i--)
                {
                	_child = _children[i];
                    if ((_except == null || _child.instance != _except) && _child.maxX > _minX && _child.minX < _maxX && _child.maxY > _minY && _child.minY < _maxY)
                        _result.push(_child);
                }
            }           
        }
        
        private function getParent(node:QuadTreeNode = null):void
        {
        	if(!node)
        		return;
        		
        	node = node.parent;
        	
            if (node == null || (node.onlysourceFlag && _except == node.onlysource))
                return;

            _children = node.center;
            if (_children != null) {
                i = _children.length;
                while (i--)
                {
                	_child = _children[i];
                    if ((_except == null || _child.instance != _except) && _child.maxX > _minX && _child.minX < _maxX && _child.maxY > _minY && _child.minY < _maxY)
                        _result.push(_child);
                }
            }
            getParent(node);
        }
        
		/**
		 * Defines the clipping object to be used on the drawing primitives.
		 */
		public function get clip():Clipping
		{
			return _clip;
		}
		
		public function set clip(val:Clipping):void
		{
			_clip = val;
			_rect = _clip.asRectangleClipping();
			if (!_root)
				_root = new QuadTreeNode((_rect.minX + _rect.maxX)/2, (_rect.minY + _rect.maxY)/2, _rect.maxX - _rect.minX, _rect.maxY - _rect.minY, 0, null, _maxlevel);
			else
				_root.reset((_rect.minX + _rect.maxX)/2, (_rect.minY + _rect.maxY)/2, _rect.maxX - _rect.minX, _rect.maxY - _rect.minY, _maxlevel);	
		}
		
		
		public function get maxLevel():uint{
			return _maxlevel;
		}
		
		public function set maxLevel(value:uint):void{
			_maxlevel = value;
			if(_root)
				_root.maxlevel = _maxlevel;
		}
        
		/**
		 * @inheritDoc
		 */
        public function add(renderItem:RenderableListItem):void
        {
            if (_clip.check(renderItem))
            {
                _root.push(renderItem);
            }
        }
        
        /**
        * removes a drawing primitive from the quadrant tree.
        * 
        * @param	pri	The drawing primitive to remove.
        */
        public function remove(renderItem:RenderableListItem):void
        {
        	_center = renderItem.quadrant.center;
        	_center.splice(_center.indexOf(renderItem), 1);
        }
		
		/**
		 * A list of primitives that have been clipped.
		 * 
		 * @return	An array containing the primitives to be rendered.
		 */
        public function list():Array
        {
            _result = [];
                    
			_minX = -1000000;
			_minY = -1000000;
			_maxX = 1000000;
			_maxY = 1000000;
			_except = null;
			
            getList(_root);
            
            return _result;
        }
		
		/**
		 * Returns an array containing all primiives overlapping the specifed primitive's quadrant.
		 * 
		 * @param	renderItem			The drawing primitive to check.
		 * @param	ex		[optional]	Excludes primitives that are children of the 3d object.
		 * @return						An array of drawing primitives.
		 */
        public function getOverlaps(renderItem:RenderableListItem, ex:DisplayObject3D = null):Array
        {
        	_result = [];
                    
			_minX = renderItem.minX;
			_minY = renderItem.minY;
			_maxX = renderItem.maxX;
			_maxY = renderItem.maxY;
			_except = ex;
			
            getList(renderItem.quadrant);
            getParent(renderItem.quadrant);
            return _result;
        }
        
        /**
        * Calls the render function on all primitives in the quadrant tree
        */
        public function render(renderSessionData:RenderSessionData, graphics:Graphics):void
        {
            _root.render(-Infinity, renderSessionData, graphics);
        }
        
        public function getRoot():QuadTreeNode{
        	return _root;
        }
    }
}
