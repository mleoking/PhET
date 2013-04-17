package away3d.core.render
{
	import away3d.arcane;
	import away3d.containers.*;
	import away3d.core.clip.*;
	import away3d.events.*;
	
	import flash.display.*;
	import flash.geom.*;
	
	use namespace arcane;
	
    /**
    * Drawing session object that renders all drawing primitives into a <code>Sprite</code> container.
    */
	public class SpriteRenderSession extends AbstractRenderSession
	{
        private var _container:Sprite;
        private var _clip:Clipping;
        
        protected override function onSessionUpdate(event:SessionEvent):void
        {
        	super.onSessionUpdate(event);
        	
        	cacheAsBitmap = false;
        }
        
        public var cacheAsBitmap:Boolean;
        
		/**
		 * Creates a new <code>SpriteRenderSession</code> object.
		 */
		public function SpriteRenderSession():void
		{
		}
        
		/**
		 * @inheritDoc
		 */
		public override function getContainer(view:View3D):DisplayObject
		{
			if (!_containers[view])
        		return _containers[view] = new Sprite();
        	
			return _containers[view];
		}
        
		/**
		 * @inheritDoc
		 */
        public override function addDisplayObject(child:DisplayObject):void
        {
            //add to container
            _container.addChild(child);
            child.visible = true;
            
            layer = null;
            
            _level = -1;
            
            _layerDirty = true;
        }
        
		/**
		 * @inheritDoc
		 */
        protected override function createSprite(parent:Sprite = null):Sprite
        {
        	if (_spriteStore.length) {
            	_spriteActive.push(_sprite = _spriteStore.pop());
            } else {
            	_spriteActive.push(_sprite = new Sprite());
            }
            
            if (parent)
            	parent.addChild(_sprite);
            else {
            	_container.addChild(_sprite);
            	layer = _sprite;
            }
            
            _layerDirty = true;
            
            return _sprite;
        }
        
		/**
		 * @inheritDoc
		 */
        protected override function createLayer():void
        {
            //create new canvas for remaining triangles
            if (_shapeStore.length) {
            	_shapeActive.push(_shape = _shapeStore.pop());
            } else {
            	_shapeActive.push(_shape = new Shape());
            }
            
            //update layer reference
            layer = _shape;
            
            //update graphics reference
            graphics = _shape.graphics;
            
            //add new canvas to base canvas
            _container.addChild(_shape);
       		
			_layerDirty = false;
			
			_level = -1;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function clear(view:View3D):void
        {
       		super.clear(view);
       		
    		_container = getContainer(view) as Sprite;
        	if (updated) {
	 			layer = _container;
	 			graphics = _container.graphics;
	 			
	        	//clip the edges of the root container with  scrollRect
	        	if (this == view.session) {
		        	_clip = view.screenClipping;
		        	_container.scrollRect = new Rectangle(_clip.minX-1, _clip.minY-1, _clip.maxX - _clip.minX + 2, _clip.maxY - _clip.minY + 2);
		        	_container.x = _clip.minX - 1;
		        	_container.y = _clip.minY - 1;
		        }
        		_container.cacheAsBitmap = false;
        		
	        	//clear base canvas
	            graphics.clear();
	            
	            //remove all children
				while (_container.numChildren)
					_container.removeChildAt(0);
	            
        	} else {
        		_container.cacheAsBitmap = cacheAsBitmap;
        	}
        	
        	if ((filters && filters.length) || (_container.filters && _container.filters.length))
        		_container.filters = filters;
        	
        	_container.alpha = alpha;
        	_container.blendMode = blendMode || BlendMode.NORMAL;
        }   
        
		/**
		 * @inheritDoc
		 */
        public override function clone():AbstractRenderSession
        {
        	return new SpriteRenderSession();
        }
	}
}