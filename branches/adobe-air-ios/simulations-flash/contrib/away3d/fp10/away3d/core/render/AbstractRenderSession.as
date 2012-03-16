package away3d.core.render
{
	import away3d.arcane;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.clip.*;
	import away3d.core.draw.*;
	import away3d.core.light.*;
	import away3d.events.*;
	import away3d.materials.*;
	
	import flash.display.*;
	import flash.events.*;
	import flash.geom.*;
	import flash.utils.*;
    
	use namespace arcane;
	
	/**
	 * Dispatched when the render contents of the session require updating.
	 * 
	 * @eventType away3d.events.SessionEvent
	 */
	[Event(name="sessionUpdated",type="away3d.events.SessionEvent")]
	
    /**
    * Abstract Drawing session object containing the method used for drawing the view to screen.
    * Not intended for direct use - use <code>SpriteRenderSession</code> or <code>BitmapRenderSession</code>.
    */
	public class AbstractRenderSession extends EventDispatcher
	{
		/** @private */
		arcane var _containers:Dictionary = new Dictionary(true);
		/** @private */
		arcane var _shape:Shape;
		/** @private */
		arcane var _sprite:Sprite;
		/** @private */
		arcane var _level:int = -1;
		/** @private */
		arcane var _material:IMaterial;
		/** @private */
        arcane var _renderSource:Object3D;
		/** @private */
        arcane var _layerDirty:Boolean;
		/** Array for storing old displayobjects to the canvas */
		arcane var _shapeStore:Array;
		/** Array for storing added displayobjects to the canvas */
		arcane var _shapeActive:Array;
		/** Array for storing old displayobjects to the canvas */
		arcane var _spriteStore:Array;
		/** Array for storing added displayobjects to the canvas */
		arcane var _spriteActive:Array;
		/** @private */
		arcane function notifySessionUpdate():void
		{
			if (!hasEventListener(SessionEvent.SESSION_UPDATED))
                return;
			
            if (!_sessionupdated)
                _sessionupdated = new SessionEvent(SessionEvent.SESSION_UPDATED, this);
            
            dispatchEvent(_sessionupdated);
		}
		/** @private */
        arcane function internalAddSceneSession(session:AbstractRenderSession):void
        {
        	sessions = [session];
        	session.addOnSessionUpdate(onSessionUpdate);
        }
		/** @private */
        arcane function internalRemoveSceneSession(session:AbstractRenderSession):void
        {
        	sessions = [];
        	session.removeOnSessionUpdate(onSessionUpdate);
        }
		/** @private */
        arcane function internalAddOwnSession(object:Object3D):void
        {
        	object.addEventListener(Object3DEvent.SESSION_UPDATED, onObjectSessionUpdate);
        }
		/** @private */
        arcane function internalRemoveOwnSession(object:Object3D):void
        {
        	object.removeEventListener(Object3DEvent.SESSION_UPDATED, onObjectSessionUpdate);
        }
        private var _consumer:IPrimitiveConsumer;
        private var _shapeStores:Dictionary = new Dictionary(true);
        private var _shapeActives:Dictionary = new Dictionary(true);
        private var _spriteStores:Dictionary = new Dictionary(true);
        private var _spriteActives:Dictionary = new Dictionary(true);
        private var _spriteLayers:Dictionary = new Dictionary(true);
        private var _spriteLayer:Dictionary;
        private var _shapeLayers:Dictionary = new Dictionary(true);
        private var _shapeLayer:Dictionary;
        private var _lightShapeLayers:Dictionary = new Dictionary(true);
        private var _lightShapeLayer:Dictionary;
        private var _dictionary:Dictionary;
        private var _array:Array;
        private var _defaultColorTransform:ColorTransform = new ColorTransform();
		private var _layerGraphics:Graphics;
		private var fill:GraphicsBitmapFill = new GraphicsBitmapFill();
        private var path:GraphicsTrianglePath = new GraphicsTrianglePath(new Vector.<Number>(3), null, new Vector.<Number>(3));
        private var end:GraphicsEndFill = new GraphicsEndFill();
        private var drawing:Vector.<IGraphicsData> = Vector.<IGraphicsData>([fill, path, end]);
		private var _renderers:Dictionary = new Dictionary(true);
		private var _renderer:IPrimitiveConsumer;
        private var _session:AbstractRenderSession;
        private var _sessionupdated:SessionEvent;
        private var a:Number;
        private var b:Number;
        private var c:Number;
        private var d:Number;
        private var tx:Number;
        private var ty:Number;
        private var _index0:int;
        private var _index1:int;
        private var _index2:int;
        private var v0x:Number;
        private var v0y:Number;
        private var v1x:Number;
        private var v1y:Number;
        private var v2x:Number;
        private var v2y:Number;
        private var a2:Number;
        private var b2:Number;
        private var c2:Number;
        private var d2:Number;
		private var m:Matrix = new Matrix();
		private var area:Number;
        
        private function onObjectSessionUpdate(object:Object3DEvent):void
        {
        	notifySessionUpdate();
        }
		
		private function getShapeStore(view:View3D):Array
		{
			if (!_shapeStores[view])
        		return _shapeStores[view] = new Array();
        	
			return _shapeStores[view];
		}
		
		private function getShapeActive(view:View3D):Array
		{
			if (!_shapeActives[view])
        		return _shapeActives[view] = new Array();
        	
			return _shapeActives[view];
		}
		
		private function getSpriteStore(view:View3D):Array
		{
			if (!_spriteStores[view])
        		return _spriteStores[view] = new Array();
        	
			return _spriteStores[view];
		}
		
		private function getSpriteActive(view:View3D):Array
		{
			if (!_spriteActives[view])
        		return _spriteActives[view] = new Array();
        	
			return _spriteActives[view];
		}
		
		public function getSpriteLayer(view:View3D):Dictionary
		{
			if (!_spriteLayers[view])
        		return _spriteLayers[view] = new Dictionary(true);
        	
			return _spriteLayers[view];
		}
		
		public function getShapeLayer(view:View3D):Dictionary
		{
			if (!_shapeLayers[view])
        		return _shapeLayers[view] = new Dictionary(true);
        	
			return _shapeLayers[view];
		}
		
		public function getLightShapeLayer(view:View3D):Dictionary
		{
			if (!_lightShapeLayers[view])
        		return _lightShapeLayers[view] = new Dictionary(true);
        	
			return _lightShapeLayers[view];
		}
		
        protected function onSessionUpdate(event:SessionEvent):void
        {
        	dispatchEvent(event);
        }
        
		/** @private */
        protected var i:int;
        
        public var layer:DisplayObject;
        
        public var parent:AbstractRenderSession;
        
        public var updated:Boolean;
        
        public var primitives:Array;
        
        public var screenZ:Number;
        /**
        * Placeholder for filters property of containers
        */
        public var filters:Array;
        
        /**
        * Placeholder for alpha property of containers
        */
        public var alpha:Number = 1;
        
        /**
        * Placeholder for blendMode property of containers
        */
        public var blendMode:String;
        
        /**
        * Array of child sessions.
        */
       	public var sessions:Array = new Array();
        
        /**
        * Reference to the current graphics object being used for drawing.
        */
        public var graphics:Graphics;
        
        public var priconsumers:Dictionary = new Dictionary(true);
        
        public var consumer:IPrimitiveConsumer;
		
		public function get renderer():IPrimitiveConsumer
		{
			return _renderer;
		}
		
		public function set renderer(val:IPrimitiveConsumer):void
		{
			if (_renderer == val)
				return;
			
			_renderer = val;
			
			clearRenderers();
			
			for each (var _session:AbstractRenderSession in sessions)
				_session.clearRenderers();
		}
        
		/**
		 * Adds a session as a child of the session object.
		 * 
		 * @param	session		The session object to be added as a child.
		 */
        public function addChildSession(session:AbstractRenderSession):void
        {
        	if (sessions.indexOf(session) != -1)
        		return;
        	
        	sessions.push(session);
        	session.addOnSessionUpdate(onSessionUpdate);
        	session.parent = this;
        }
        
		/**
		 * Removes a child session of the session object.
		 * 
		 * @param	session		The session object to be removed.
		 */
        public function removeChildSession(session:AbstractRenderSession):void
        {
        	session.removeOnSessionUpdate(onSessionUpdate);
        	
        	var index:int = sessions.indexOf(session);
            if (index == -1)
                return;
            
            sessions.splice(index, 1);	
        }
        
        public function clearChildSessions():void
        {
        	for each (_session in sessions)
        		_session.removeOnSessionUpdate(onSessionUpdate);
        		
        	sessions = new Array();
        }
        
       	/**
       	 * Creates a new render layer for rendering composite materials.
       	 * 
       	 * @see away3d.materials.CompositeMaterial#renderTriangle()
       	 */
        protected function createLayer():void
        {
			throw new Error("Not implemented");
        }
        
		/**
		 * Returns a display object representing the container for the specified view.
		 * 
		 * @param	view	The view object being rendered.
		 * @return			The display object container.
		 */
		public function getContainer(view:View3D):DisplayObject
		{
			throw new Error("Not implemented");
		}
			
		public function getConsumer(view:View3D):IPrimitiveConsumer
		{
			if (_renderers[view])
				return _renderers[view];
			
			if (_renderer)
				return _renderers[view] = _renderer.clone();
			
			if (parent)
				return _renderers[view] = parent.getConsumer(view).clone();
			
			return _renderers[view] = (view.session.renderer as IPrimitiveConsumer).clone();
		}
		
        public function getTotalFaces(view:View3D):int
        {
        	var output:int = getConsumer(view).list().length;
			
			for each (_session in sessions)
				output += _session.getTotalFaces(view);
				
			return output;
        }
        
		/**
		 * Clears the render session.
		 */
        public function clear(view:View3D):void
        {
        	updated = view.updated || view.forceUpdate || view.scene.updatedSessions[this];
			
        	for each(_session in sessions)
       			_session.clear(view);
        	
			if (updated) {
	        	
	        	_consumer = getConsumer(view);
	        	
	        	_spriteLayer = getSpriteLayer(view);
	        	
	        	for each (_array in _spriteLayer)
	        		_array.length = 0;
	        	
	        	_shapeLayer = getShapeLayer(view);
	        	
	        	for each (_array in _shapeLayer)
	        		_array.length = 0;
	        	
	        	_lightShapeLayer = getLightShapeLayer(view);
	        	
	        	for each (_dictionary in _lightShapeLayer)
	        		for each (_array in _dictionary)
	        			_array.length = 0;
	        	
	        	_level = -1;
	        	
	        	_shapeStore = getShapeStore(view);
	        	_shapeActive = getShapeActive(view);
	        	
	        	//clear child shapes
	            i = _shapeActive.length;
	            while (i--) {
	            	_shape = _shapeActive.pop();
	            	_shape.graphics.clear();
	            	_shape.filters = [];
	            	_shape.blendMode = BlendMode.NORMAL;
	            	_shape.transform.colorTransform = _defaultColorTransform;
	            	_shapeStore.push(_shape);
	            }
	            
	            _spriteStore = getSpriteStore(view);
	        	_spriteActive = getSpriteActive(view);
	        	
	        	//clear child sprites
	        	i = _spriteActive.length;
	            while (i--) {
	            	_sprite = _spriteActive.pop();
	            	_sprite.graphics.clear();
	            	_sprite.filters = [];
	            	while (_sprite.numChildren)
        				_sprite.removeChildAt(0);
	            	_spriteStore.push(_sprite);
	            }
	            
	            //clear primitives consumer
	            _consumer.clear(view);
			}
        }
        
        public function render(view:View3D):void
        {
	        //index -= priconsumer.length;
        	for each(_session in sessions)
       			_session.render(view);
       		
        	//fill.bitmapData = null;
        	
        	if (updated)
	            (getConsumer(view) as IRenderer).render(view);
	        
	        //if (fill.bitmapData)
	        //	drawBitmapTriangles();
        }
        
        public function clearRenderers():void
        {
        	_renderers = new Dictionary(true);
        }
        
        /**
        * Adds a display object to the render session display list.
        * 
        * @param	child	The display object to add.
        */
        public function addDisplayObject(child:DisplayObject):void
        {
        	throw new Error("Not implemented");
        }
        
        public function getSprite(material:ILayerMaterial, level:int, parent:Sprite = null):Sprite
        {
        	if (!(_array = _spriteLayer[material])) 
        		_array = _spriteLayer[material] = new Array();
        	
        	if (!parent && material != _material) {
        		_level = -1;
        		_material = material;
        	}
        	
        	if (_level >= level && _array.length) {
        		_sprite = _array[0];
        	} else {
	        	_level = level;
        		_array.unshift(_sprite = createSprite(parent));
        	}
            return _sprite;
        }
        
        public function getShape(material:ILayerMaterial, level:int, parent:Sprite):Shape
        {
        	if (!(_array = _shapeLayer[material]))
        		_array = _shapeLayer[material] = new Array();
        	
        	if (_level >= level && _array.length) {
        		_shape = _array[0];
        	} else {
	        	_level = level;
        		_array.unshift(_shape = createShape(parent));
        	}
        	
            return _shape;
        }
        
        
        public function getLightShape(material:ILayerMaterial, level:int, parent:Sprite, light:LightPrimitive):Shape
        {
        	if (!(_dictionary = _lightShapeLayer[material]))
        		_dictionary = _lightShapeLayer[material] = new Dictionary(true);
        	
        	if (!(_array = _dictionary[light]))
        		_array = _dictionary[light] = new Array();
        	
        	if (_level >= level && _array.length) {
        		_shape = _array[0];
        	} else {
        		_level = level;
        		_array.unshift(_shape = createShape(parent));
        	}
        	
            return _shape;
        }
        
        protected function createSprite(parent:Sprite = null):Sprite
        {
        	throw new Error("Not implemented");
        }
        
		/**
		 * @inheritDoc
		 */
        protected function createShape(parent:Sprite):Shape
        {
        	if (_shapeStore.length) {
            	_shapeActive.push(_shape = _shapeStore.pop());
            } else {
            	_shapeActive.push(_shape = new Shape());
            }
            
            parent.addChild(_shape);
            
            _layerDirty = true;
            
            return _shape;
        }
        
        /**
        * Draws a non-scaled bitmap into the graphics object.
        */
        public function renderBitmap(bitmap:BitmapData, v0:ScreenVertex, smooth:Boolean = false):void
        {
        	if (_layerDirty)
        		createLayer();
        	
        	m.identity();
        	m.tx = v0.x-bitmap.width/2; m.ty = v0.y-bitmap.height/2;
            graphics.lineStyle();
            graphics.beginBitmapFill(bitmap, m, false,smooth);
            graphics.drawRect(v0.x-bitmap.width/2, v0.y-bitmap.height/2, bitmap.width, bitmap.height);
            graphics.endFill();
        }
        
        /**
         * Draws a bitmap with a precalculated matrix into the graphics object.
         */
        public function renderScaledBitmap(primitive:DrawScaledBitmap, bitmap:BitmapData, mapping:Matrix, smooth:Boolean = false):void
        {
        	if (_layerDirty)
        		createLayer();
        	
        	graphics.lineStyle();
        	
            if (primitive.rotation != 0) {   
	            graphics.beginBitmapFill(bitmap, mapping, false, smooth);
	            graphics.moveTo(primitive.topleftx, primitive.toplefty);
	            graphics.lineTo(primitive.toprightx, primitive.toprighty);
	            graphics.lineTo(primitive.bottomrightx, primitive.bottomrighty);
	            graphics.lineTo(primitive.bottomleftx, primitive.bottomlefty);
	            graphics.lineTo(primitive.topleftx, primitive.toplefty);
	            graphics.endFill();
            } else {
	            graphics.beginBitmapFill(bitmap, mapping, false, smooth);	            
	            graphics.drawRect(primitive.minX, primitive.minY, primitive.maxX-primitive.minX, primitive.maxY-primitive.minY);
            	graphics.endFill();
            }
        }
        
        /**
         * Draws a segment element into the graphics object.
         */
        public function renderLine(v0x:Number, v0y:Number, v1x:Number, v1y:Number, width:Number, color:uint, alpha:Number):void
        {
        	if (_layerDirty)
        		createLayer();
        	
            graphics.lineStyle(width, color, alpha);
            graphics.moveTo(v0x, v0y);
            graphics.lineTo(v1x, v1y);
        }
        
        /**
         * Draws a triangle element with a bitmap texture into the graphics object.
         */
        public function renderTriangleBitmap(bitmap:BitmapData, map:Matrix, screenVertices:Array, screenIndices:Array, startIndex:Number, endIndex:Number, smooth:Boolean, repeat:Boolean, layerGraphics:Graphics = null):void
        {
        	if (!layerGraphics && _layerDirty)
        		createLayer();
        	
        	_index0 = screenIndices[startIndex]*3;
        	_index1 = screenIndices[startIndex+1]*3;
        	_index2 = screenIndices[startIndex+2]*3;
        	
        	a2 = (v1x = screenVertices[_index1]) - (v0x = screenVertices[_index0]);
        	b2 = (v1y = screenVertices[_index1+1]) - (v0y = screenVertices[_index0+1]);
        	c2 = (v2x = screenVertices[_index2]) - v0x;
        	d2 = (v2y = screenVertices[_index2+1]) - v0y;
        	
			m.a = (a = map.a)*a2 + (b = map.b)*c2;
			m.b = a*b2 + b*d2;
			m.c = (c = map.c)*a2 + (d = map.d)*c2;
			m.d = c*b2 + d*d2;
			m.tx = (tx = map.tx)*a2 + (ty = map.ty)*c2 + v0x;
			m.ty = tx*b2 + ty*d2 + v0y;
			
			area = v0x*(d2 - b2) - v1x*d2 + v2x*b2;
			
			if (area < 0)
				area = -area;
			
			if (layerGraphics) {
				layerGraphics.lineStyle();
	            layerGraphics.moveTo(v0x, v0y);
	            layerGraphics.beginBitmapFill(bitmap, m, repeat, smooth && area > 400);
	            layerGraphics.lineTo(v1x, v1y);
	            layerGraphics.lineTo(v2x, v2y);
	            layerGraphics.endFill();
	  		} else {
	  			graphics.lineStyle();
	            graphics.moveTo(v0x, v0y);
	            graphics.beginBitmapFill(bitmap, m, repeat, smooth && area > 400);
	            graphics.lineTo(v1x, v1y);
	            graphics.lineTo(v2x, v2y);
	            graphics.endFill();
	  		}
        }
        
        /**
         * Draws a triangle element with a bitmap texture into the graphics object, with no uv transforms.
         */
        public function renderTriangleBitmapMask(bitmap:BitmapData, offX:Number, offY:Number, sc:Number, screenVertices:Array, screenIndices:Array, startIndex:Number, endIndex:Number, smooth:Boolean, repeat:Boolean, layerGraphics:Graphics = null):void
        {
        	if (_layerDirty)
        		createLayer();
        	
        	_index0 = screenIndices[startIndex]*3;
        	_index1 = screenIndices[startIndex+1]*3;
        	_index2 = screenIndices[startIndex+2]*3;
        	
        	a2 = (v1x = screenVertices[_index1]) - (v0x = screenVertices[_index0]);
        	b2 = (v1y = screenVertices[_index1+1]) - (v0y = screenVertices[_index0+1]);
        	c2 = (v2x = screenVertices[_index2]) - v0x;
        	d2 = (v2y = screenVertices[_index2+1]) - v0y;
        	
			m.identity();
			m.scale(sc, sc);
			m.translate(offX, offY);
			
			if (layerGraphics) {
				layerGraphics.lineStyle();
	            layerGraphics.moveTo(v0x, v0y);
	            layerGraphics.beginBitmapFill(bitmap, m, repeat, smooth && (v0x*(d2 - b2) - v1x*d2 + v2x*b2 > 400));
	            layerGraphics.lineTo(v1x, v1y);
	            layerGraphics.lineTo(v2x, v2y);
	            layerGraphics.endFill();
	  		} else {
	  			graphics.lineStyle();
	            graphics.moveTo(v0x, v0y);
	            graphics.beginBitmapFill(bitmap, m, repeat, smooth && (v0x*(d2 - b2) - v1x*d2 + v2x*b2 > 400));
	            graphics.lineTo(v1x, v1y);
	            graphics.lineTo(v2x, v2y);
	            graphics.endFill();
	  		}
        }
        
        /**
         * Draws a triangle element with a bitmap texture into the graphics object (Flash 10)
         */
        public function renderTriangleBitmapF10(bitmap:BitmapData, uvtData:Vector.<Number>, screenVertices:Array, screenIndices:Array, startIndex:Number, endIndex:Number, smooth:Boolean, repeat:Boolean, layerGraphics:Graphics = null):void
        {
        	if (!layerGraphics && _layerDirty)
        		createLayer();
        		
        	_index0 = screenIndices[startIndex]*3;
        	_index1 = screenIndices[startIndex+1]*3;
        	_index2 = screenIndices[startIndex+2]*3;
        	
        	fill.bitmapData = bitmap;
			fill.repeat = repeat;
			fill.smooth = smooth;
			
        	path.vertices = Vector.<Number>([screenVertices[_index0], screenVertices[_index0 + 1], screenVertices[_index1], screenVertices[_index1 + 1], screenVertices[_index2], screenVertices[_index2 + 1]]);
        	
        	path.uvtData = uvtData;
			
			if (layerGraphics) {
				layerGraphics.lineStyle();
				layerGraphics.drawGraphicsData(drawing);
	  		} else {
	  			graphics.lineStyle();
	  			graphics.drawGraphicsData(drawing);
	  		}
        }
        
        /**
         * Draws a triangle element with a fill color into the graphics object.
         */
        public function renderTriangleColor(color:int, alpha:Number, screenVertices:Array, commands:Array, screenIndices:Array, startIndex:Number, endIndex:Number, layerGraphics:Graphics = null):void
        {
        	if (!layerGraphics && _layerDirty)
        		createLayer();
        	
        	var applicableGraphics:Graphics = layerGraphics ? layerGraphics : graphics;
        	
        	if(endIndex - startIndex > 3) {
        		
        		applicableGraphics.lineStyle();
        		applicableGraphics.beginFill(color, alpha);
        		
	            while(startIndex < endIndex) {
	            	_index0 = screenIndices[startIndex]*3;
					switch (commands[startIndex++]) {
						case "M":
							applicableGraphics.moveTo(screenVertices[_index0], screenVertices[_index0+1]);
							break;
						case "L":
							applicableGraphics.lineTo(screenVertices[_index0], screenVertices[_index0+1]);
							break;
						case "C":
							_index1 = screenIndices[startIndex++]*3;
							applicableGraphics.curveTo(screenVertices[_index0], screenVertices[_index0+1], screenVertices[_index1], screenVertices[_index1+1]);
							break;
					}
	            }
	            applicableGraphics.endFill();
	        } else {
	        	_index0 = screenIndices[startIndex]*3;
	        	_index1 = screenIndices[startIndex+1]*3;
	        	_index2 = screenIndices[startIndex+2]*3;
	        	applicableGraphics.lineStyle();
	            applicableGraphics.moveTo(screenVertices[_index0], screenVertices[_index0+1]); // Always move before begin will to prevent bugs
	            applicableGraphics.beginFill(color, alpha);
	            applicableGraphics.lineTo(screenVertices[_index1], screenVertices[_index1+1]);
	            applicableGraphics.lineTo(screenVertices[_index2], screenVertices[_index2+1]);
	            applicableGraphics.endFill();
	        }
        }
        
        /**
         * Draws a wire triangle element into the graphics object.
         */
        public function renderTriangleLine(width:Number, color:int, alpha:Number, screenVertices:Array, commands:Array, screenIndices:Array, startIndex:Number, endIndex:Number):void
        {
        	if (_layerDirty)
        		createLayer();
        	
            graphics.lineStyle(width, color, alpha);
            
            if(endIndex - startIndex > 3) {
	            while(startIndex < endIndex) {
	            	_index0 = screenIndices[startIndex]*3;
					switch (commands[startIndex++]) {
						case "M":
							graphics.moveTo(screenVertices[_index0], screenVertices[_index0+1]);
							break;
						case "L":
							graphics.lineTo(screenVertices[_index0], screenVertices[_index0+1]);
							break;
						case "C":
							_index1 = screenIndices[startIndex++]*3;
							graphics.curveTo(screenVertices[_index0], screenVertices[_index0+1], screenVertices[_index1], screenVertices[_index1+1]);
							break;
					}
	            }
	        } else {
	        	_index0 = screenIndices[startIndex]*3;
	        	_index1 = screenIndices[startIndex+1]*3;
	        	_index2 = screenIndices[startIndex+2]*3; 
	        	
	        	graphics.moveTo(v0x = screenVertices[_index0], v0y = screenVertices[_index0+1]);
	            graphics.lineTo(screenVertices[_index1], screenVertices[_index1+1]);
		        graphics.lineTo(screenVertices[_index2], screenVertices[_index2+1]);
		        graphics.lineTo(v0x, v0y);
	        }
        }
        
        /**
         * Draws a wire triangle element with a fill color into the graphics object.
         */
        public function renderTriangleLineFill(width:Number, color:int, alpha:Number, wirecolor:int, wirealpha:Number, screenVertices:Array, commands:Array, screenIndices:Array, startIndex:int, endIndex:int):void
        {
        	if(_layerDirty)
        		createLayer();
        	
            if(wirealpha > 0)
                graphics.lineStyle(width, wirecolor, wirealpha);
            else
                graphics.lineStyle();
                
            if(alpha > 0)
            	graphics.beginFill(color, alpha);
        	
        	if(endIndex - startIndex > 3) {
	            while(startIndex < endIndex) {
	            	_index0 = screenIndices[startIndex]*3;
					switch (commands[startIndex++]) {
						case "M":
							graphics.moveTo(screenVertices[_index0], screenVertices[_index0+1]);
							break;
						case "L":
							graphics.lineTo(screenVertices[_index0], screenVertices[_index0+1]);
							break;
						case "C":
							_index1 = screenIndices[startIndex++]*3;
							graphics.curveTo(screenVertices[_index0], screenVertices[_index0+1], screenVertices[_index1], screenVertices[_index1+1]);
							break;
					}
	            }
	        } else {
	        	_index0 = screenIndices[startIndex]*3;
	        	_index1 = screenIndices[startIndex+1]*3;
	        	_index2 = screenIndices[startIndex+2]*3;
	        	
	        	graphics.moveTo(v0x = screenVertices[_index0], v0y = screenVertices[_index0+1]);
	            graphics.lineTo(screenVertices[_index1], screenVertices[_index1+1]);
	        	graphics.lineTo(screenVertices[_index2], screenVertices[_index2+1]);
	    
	            if (wirealpha > 0)
	                graphics.lineTo(v0x, v0y);
	        }
	        
	        if(alpha > 0)
	        	graphics.endFill();
        }
        
        /**
         * Draws a billboard element with a fill color into the graphics object.
         */
        public function renderBillboardColor(color:int, alpha:Number, primitive:DrawBillboard):void
        {
        	if (_layerDirty)
        		createLayer();
        	
            if (primitive.rotation != 0) {
	            graphics.beginFill(color, alpha);
	            graphics.moveTo(primitive.topleftx, primitive.toplefty);
	            graphics.lineTo(primitive.toprightx, primitive.toprighty);
	            graphics.lineTo(primitive.bottomrightx, primitive.bottomrighty);
	            graphics.lineTo(primitive.bottomleftx, primitive.bottomlefty);
	            graphics.lineTo(primitive.topleftx, primitive.toplefty);
	            graphics.endFill();
            } else {
	            graphics.beginFill(color, alpha);
	            graphics.drawRect(primitive.minX, primitive.minY, primitive.maxX-primitive.minX, primitive.maxY-primitive.minY);
            	graphics.endFill();
            }
        }
        
        /**
         * Draws a billboard element with a fill bitmap into the graphics object.
         */
        public function renderBillboardBitmap(bitmap:BitmapData, primitive:DrawBillboard, smooth:Boolean):void
        {
        	if (_layerDirty)
        		createLayer();
        	
            if (primitive.rotation != 0) {
	            graphics.beginBitmapFill(bitmap, primitive.mapping, false, smooth);
	            graphics.moveTo(primitive.topleftx, primitive.toplefty);
	            graphics.lineTo(primitive.toprightx, primitive.toprighty);
	            graphics.lineTo(primitive.bottomrightx, primitive.bottomrighty);
	            graphics.lineTo(primitive.bottomleftx, primitive.bottomlefty);
	            graphics.lineTo(primitive.topleftx, primitive.toplefty);
	            graphics.endFill();
            } else {
	            graphics.beginBitmapFill(bitmap, primitive.mapping, false, smooth);
	            graphics.drawRect(primitive.minX, primitive.minY, primitive.maxX-primitive.minX, primitive.maxY-primitive.minY);
            	graphics.endFill();
            }
        }
        
        /**
         * Draws a fog element into the graphics object.
         */
        public function renderFogColor(clip:Clipping, color:int, alpha:Number):void
        {
        	if (_layerDirty)
        		createLayer();
        	
        	graphics.lineStyle();
            graphics.beginFill(color, alpha);
            graphics.drawRect(clip.minX, clip.minY, clip.maxX - clip.minX, clip.maxY - clip.minY);
            graphics.endFill();
        }
		
		/**
		 * Duplicates the render session's properties to another render session.
		 * 
		 * @return						The new render session instance with duplicated properties applied
		 */
        public function clone():AbstractRenderSession
        {
        	throw new Error("Not implemented");
        }
		
		/**
		 * Default method for adding a sessionUpdated event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnSessionUpdate(listener:Function):void
        {
            addEventListener(SessionEvent.SESSION_UPDATED, listener, false, 0, false);
        }
		
		/**
		 * Default method for removing a sessionUpdated event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnSessionUpdate(listener:Function):void
        {
            removeEventListener(SessionEvent.SESSION_UPDATED, listener, false);
        }
	}
}