package away3d.materials
{
	import away3d.arcane;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.render.AbstractRenderSession;
	import away3d.core.utils.*;
	import away3d.events.*;
	
	import flash.display.*;
	import flash.events.*;
	import flash.geom.*;
	import flash.utils.*;
	
	use namespace arcane;
	
	/**
	 * Container for layering multiple material objects.
	 * Renders each material by drawing one triangle per meterial layer.
	 * For static bitmap materials, use <code>BitmapMaterialContainer</code>.
	 * 
	 * @see away3d.materials.BitmapMaterialContainer
	 */
	public class CompositeMaterial extends EventDispatcher implements ITriangleMaterial, ILayerMaterial
	{
		/** @private */
        arcane var _id:int;
        /** @private */
		arcane var _color:uint;
        /** @private */
        arcane var _alpha:Number;
        /** @private */
		arcane var _colorTransform:ColorTransform = new ColorTransform();
        /** @private */
    	arcane var _colorTransformDirty:Boolean;
        /** @private */
        arcane var _source:Object3D;
        /** @private */
        arcane var _session:AbstractRenderSession;
		
		private var _defaultColorTransform:ColorTransform = new ColorTransform();
		private var _red:Number;
		private var _green:Number;
		private var _blue:Number;
        
        private function onMaterialUpdate(event:MaterialEvent):void
        {
        	dispatchEvent(event);
        }
        
		/**
		 * An array of bitmapmaterial objects to be overlayed sequentially.
		 */
		protected var materials:Array;
		
        /**
        * Instance of the Init object used to hold and parse default property values
        * specified by the initialiser object in the 3d object constructor.
        */
        protected var ini:Init;
        
    	/**
    	 * Updates the colortransform object applied to the texture from the <code>color</code> and <code>alpha</code> properties.
    	 * 
    	 * @see color
    	 * @see alpha
    	 */
        protected function setColorTransform():void
        {
        	_colorTransformDirty = false;
        	
            if (_alpha == 1 && _color == 0xFFFFFF) {
                _colorTransform = null;
                return;
            } else if (!_colorTransform)
            	_colorTransform = new ColorTransform();
            
			_colorTransform.redMultiplier = _red;
			_colorTransform.greenMultiplier = _green;
			_colorTransform.blueMultiplier = _blue;
			_colorTransform.alphaMultiplier = _alpha;
        }
        
        /**
        * Defines a blendMode value for the layer container.
        */
		public var blendMode:String;
        
		/**
		 * Defines a colored tint for the layer container.
		 */
		public function get color():uint
		{
			return _color;
		}
        
        public function set color(val:uint):void
		{
			if (_color == val)
				return;
			
			_color = val;
            _red = ((_color & 0xFF0000) >> 16)/255;
            _green = ((_color & 0x00FF00) >> 8)/255;
            _blue = (_color & 0x0000FF)/255;
            
            _colorTransformDirty = true;
		}
		
        /**
        * Defines an alpha value for the layer container.
        */
        public function get alpha():Number
        {
            return _alpha;
        }
        
		public function set alpha(value:Number):void
        {
            if (value > 1)
                value = 1;

            if (value < 0)
                value = 0;

            if (_alpha == value)
                return;

            _alpha = value;

            _colorTransformDirty = true;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get visible():Boolean
        {
            return true;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get id():int
        {
            return _id;
        }
        
		/**
		 * Creates a new <code>CompositeMaterial</code> object.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function CompositeMaterial(init:Object = null)
		{
            ini = Init.parse(init);
            
			materials = ini.getArray("materials");
			blendMode = ini.getString("blendMode", BlendMode.NORMAL);
			alpha = ini.getNumber("alpha", 1, {min:0, max:1});
            color = ini.getColor("color", 0xFFFFFF);
            
            for each (var _material:ILayerMaterial in materials)
            	_material.addOnMaterialUpdate(onMaterialUpdate);
            
            _colorTransformDirty = true;
		}
        
        public function addMaterial(material:ILayerMaterial):void
        {
        	material.addOnMaterialUpdate(onMaterialUpdate);
        	materials.push(material);
        }
        
        public function removeMaterial(material:ILayerMaterial):void
        {
        	var index:int = materials.indexOf(material);
        	
        	if (index == -1)
        		return;
        	
        	material.removeOnMaterialUpdate(onMaterialUpdate);
        	
        	materials.splice(index, 1);
        }
        
		/**
		 * @inheritDoc
		 */
        public function updateMaterial(source:Object3D, view:View3D):void
        {
        	if (_colorTransformDirty)
        		setColorTransform();
        	
        	for each (var _material:ILayerMaterial in materials)
        		_material.updateMaterial(source, view);
        }
        
		/**
		 * @inheritDoc
		 */
		public function renderTriangle(tri:DrawTriangle):void
        {
        	_source = tri.source;
        	_session = _source.session;
    		var level:int = 0;
    		
    		var _sprite:Sprite = _session.layer as Sprite;
    		
        	if (!_sprite || this != _session._material || _colorTransform || blendMode != BlendMode.NORMAL) {
        		_sprite = _session.getSprite(this, level++);
        		_sprite.blendMode = blendMode;
        	}
    		
    		if (_colorTransform)
    			_sprite.transform.colorTransform = _colorTransform;
    		else
    			_sprite.transform.colorTransform = _defaultColorTransform;
	        
    		//call renderLayer on each material
    		for each (var _material:ILayerMaterial in materials)
        		level = _material.renderLayer(tri, _sprite, level);
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderLayer(tri:DrawTriangle, layer:Sprite, level:int):int
        {
        	var _sprite:Sprite;
        	if (!_colorTransform && blendMode == BlendMode.NORMAL) {
        		_sprite = layer;
        	} else {
        		_source = tri.source;
        		_session = _source.session;
        		
        		_sprite = _session.getSprite(this, level++, layer);
	        	
	        	_sprite.blendMode = blendMode;
	        	
	    		if (_colorTransform)
	    			_sprite.transform.colorTransform = _colorTransform;
	    		else
	    			_sprite.transform.colorTransform = _defaultColorTransform;
        	}
    		
	    	//call renderLayer on each material
    		for each (var _material:ILayerMaterial in materials)
        		level = _material.renderLayer(tri, _sprite, level);
        	
        	return level;
        }
        
		/**
		 * @private
		 */
        public function renderBitmapLayer(tri:DrawTriangle, containerRect:Rectangle, parentFaceMaterialVO:FaceMaterialVO):FaceMaterialVO
		{
			throw new Error("Not implemented");
		}
        
		/**
		 * @inheritDoc
		 */
        public function addOnMaterialUpdate(listener:Function):void
        {
        	addEventListener(MaterialEvent.MATERIAL_UPDATED, listener, false, 0, true);
        }
        
		/**
		 * @inheritDoc
		 */
        public function removeOnMaterialUpdate(listener:Function):void
        {
        	removeEventListener(MaterialEvent.MATERIAL_UPDATED, listener, false);
        }
	}
}