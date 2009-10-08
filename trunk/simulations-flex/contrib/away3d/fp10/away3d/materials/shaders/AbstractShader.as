package away3d.materials.shaders
{
	import away3d.containers.*;
	import away3d.arcane;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.light.*;
	import away3d.core.math.*;
	import away3d.core.render.*;
	import away3d.core.utils.*;
	import away3d.events.*;
	import away3d.materials.*;
	
	import flash.display.*;
	import flash.events.*;
	import flash.geom.*;
	import flash.utils.*;	
	
	use namespace arcane;
	
	/**
	 * Base class for shaders.
    * Not intended for direct use - use one of the shading materials in the materials package.
    */
    public class AbstractShader extends EventDispatcher implements ILayerMaterial
    {
    	/** @private */
        arcane var _id:int;
        /** @private */
        arcane var _materialDirty:Boolean;
        /** @private */
		arcane var _materialupdated:MaterialEvent;
        /** @private */
        arcane var _faceDictionary:Dictionary = new Dictionary(true);
        /** @private */
        arcane var _sprite:Sprite;
        /** @private */
        arcane var _shape:Shape;
        /** @private */
		arcane var eTri0x:Number;
        /** @private */
		arcane var eTri0y:Number;
        /** @private */
		arcane var eTri1x:Number;
        /** @private */
		arcane var eTri1y:Number;
        /** @private */
		arcane var eTri2x:Number;
        /** @private */
		arcane var eTri2y:Number;
        /** @private */
        arcane var _s:Shape = new Shape();
        /** @private */
		arcane var _graphics:Graphics;
        /** @private */
		arcane var _bitmapRect:Rectangle;
        /** @private */
		arcane var _source:Mesh;
        /** @private */
		arcane var _session:AbstractRenderSession;
        /** @private */
		arcane var _view:View3D;
        /** @private */
		arcane var _face:Face;
		/** @private */
		arcane var _faceVO:FaceVO;
        /** @private */
		arcane var _lights:ILightConsumer;
        /** @private */
		arcane var _parentFaceMaterialVO:FaceMaterialVO;
        /** @private */
		arcane var _n0:Number3D;
        /** @private */
		arcane var _n1:Number3D;
        /** @private */
		arcane var _n2:Number3D;
        /** @private */
        arcane var _dict:Dictionary;
        /** @private */
		arcane var ambient:AmbientLight;
        /** @private */
		arcane var directional:DirectionalLight;
        /** @private */
		arcane var _faceMaterialVO:FaceMaterialVO;
        /** @private */
		arcane var _normal0:Number3D = new Number3D();
        /** @private */
		arcane var _normal1:Number3D = new Number3D();
        /** @private */
		arcane var _normal2:Number3D = new Number3D();
        /** @private */
		arcane var _mapping:Matrix = new Matrix();
		/** @private */
        arcane function notifyMaterialUpdate():void
        {
        	_materialDirty = false;
        	
            if (!hasEventListener(MaterialEvent.MATERIAL_UPDATED))
                return;
			
            if (_materialupdated == null)
                _materialupdated = new MaterialEvent(MaterialEvent.MATERIAL_UPDATED, this);
                
            dispatchEvent(_materialupdated);
        }
        /** @private */
		arcane final function contains(v0x:Number, v0y:Number, v1x:Number, v1y:Number, v2x:Number, v2y:Number, x:Number, y:Number):Boolean
        {   
            if (v0x*(y - v1y) + v1x*(v0y - y) + x*(v1y - v0y) < -0.001)
                return false;

            if (v0x*(v2y - y) + x*(v0y - v2y) + v2x*(y - v0y) < -0.001)
                return false;

            if (x*(v2y - v1y) + v1x*(y - v2y) + v2x*(v1y - y) < -0.001)
                return false;

            return true;
        }
        
        /**
        * Instance of the Init object used to hold and parse default property values
        * specified by the initialiser object in the 3d object constructor.
        */
        protected var ini:Init;
        
        /**
        * Renders the shader to the specified face.
        * 
        * @param	face	The face object being rendered.
        */
        protected function renderShader(tri:DrawTriangle):void
        {
        	throw new Error("Not implemented");
        }
        
    	/**
    	 * Determines if the shader bitmap is smoothed (bilinearly filtered) when drawn to screen
    	 */
        public var smooth:Boolean;
        
        /**
        * Determines if faces with the shader applied are drawn with outlines
        */
        public var debug:Boolean;
        
        /**
        * Defines a blendMode value for the shader bitmap.
        */
        public var blendMode:String;
        
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
		 * Creates a new <code>AbstractShader</code> object.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function AbstractShader(init:Object = null)
        {
            ini = Init.parse(init);
            
            smooth = ini.getBoolean("smooth", false);
            debug = ini.getBoolean("debug", false);
            blendMode = ini.getString("blendMode", BlendMode.NORMAL);
            
            //_id = 
        }
        
		/**
		 * @inheritDoc
		 */
		public function updateMaterial(source:Object3D, view:View3D):void
        {
        	throw new Error("Not implemented");
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderLayer(tri:DrawTriangle, layer:Sprite, level:int):int
        {
        	_source = tri.source as Mesh;
        	_session = _source.session;
			_view = tri.view;
			_faceVO = tri.faceVO;
			_face = _faceVO.face;
			_lights = tri.source.lightarray;
			
			return level;
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderBitmapLayer(tri:DrawTriangle, containerRect:Rectangle, parentFaceMaterialVO:FaceMaterialVO):FaceMaterialVO
        {
        	_source = tri.source as Mesh;
        	_session = _source.session;
			_view = tri.view;
			_faceVO = tri.faceVO;
			_face = _faceVO.face;
			_parentFaceMaterialVO = parentFaceMaterialVO;
			
			_faceMaterialVO = getFaceMaterialVO(_faceVO, _source, _view);
			
			//pass on inverse texturemapping
			_faceMaterialVO.invtexturemapping = parentFaceMaterialVO.invtexturemapping;
			
			//pass on resize value
			if (parentFaceMaterialVO.resized) {
				parentFaceMaterialVO.resized = false;
				_faceMaterialVO.resized = true;
			}
			
			//check to see if rendering can be skipped
			if (parentFaceMaterialVO.updated || _faceMaterialVO.invalidated || _faceMaterialVO.updated) {
				parentFaceMaterialVO.updated = false;
				
				//retrieve the bitmapRect
				_bitmapRect = _faceVO.bitmapRect;
				
				//reset booleans
				if (_faceMaterialVO.invalidated)
					_faceMaterialVO.invalidated = false;
				else 
					_faceMaterialVO.updated = true;
				
				//store a clone
				_faceMaterialVO.bitmap = parentFaceMaterialVO.bitmap;
				
				//draw shader
				renderShader(tri);
			}
			
			return _faceMaterialVO;
        }
        
		/**
		 * @inheritDoc
		 */
        public function getFaceMaterialVO(faceVO:FaceVO, source:Object3D = null, view:View3D = null):FaceMaterialVO
        {
        	source;view;//TODO : FDT Warning
        	if ((_faceMaterialVO = _faceDictionary[faceVO]))
        		return _faceMaterialVO;
        	
        	return _faceDictionary[faceVO] = new FaceMaterialVO();
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
