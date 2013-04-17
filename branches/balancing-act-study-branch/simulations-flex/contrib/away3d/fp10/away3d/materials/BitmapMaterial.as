package away3d.materials{
    import away3d.arcane;
    import away3d.cameras.lenses.*;
    import away3d.containers.*;
    import away3d.core.base.*;
    import away3d.core.clip.*;
    import away3d.core.draw.*;
    import away3d.core.math.*;
    import away3d.core.render.*;
    import away3d.core.utils.*;
    import away3d.events.*;
    
    import flash.display.*;
    import flash.events.*;
    import flash.geom.*;
    import flash.utils.*;
    
	use namespace arcane;
	
    /**
    * Basic bitmap material
    */
    public class BitmapMaterial extends EventDispatcher implements ITriangleMaterial, IUVMaterial, ILayerMaterial, IBillboardMaterial    {
    	/** @private */
        arcane var _id:int;
    	/** @private */
    	arcane var _texturemapping:Matrix;
    	/** @private */
    	arcane var _uvtData:Vector.<Number> = new Vector.<Number>(9, true);
    	/** @private */
    	arcane var _focus:Number;
        /** @private */
    	arcane var _bitmap:BitmapData;
        /** @private */
        arcane var _materialDirty:Boolean;
        /** @private */
    	arcane var _renderBitmap:BitmapData;
        /** @private */
        arcane var _bitmapDirty:Boolean;
        /** @private */
    	arcane var _colorTransform:ColorTransform;
        /** @private */
    	arcane var _colorTransformDirty:Boolean;
        /** @private */
        arcane var _blendMode:String;
        /** @private */
        arcane var _blendModeDirty:Boolean;
        /** @private */
        arcane var _color:uint = 0xFFFFFF;
        /** @private */
		arcane var _red:Number = 1;
        /** @private */
		arcane var _green:Number = 1;
        /** @private */
		arcane var _blue:Number = 1;
        /** @private */
        arcane var _alpha:Number = 1;
        /** @private */
        arcane var _faceDictionary:Dictionary = new Dictionary(true);
        /** @private */
    	arcane var _zeroPoint:Point = new Point(0, 0);
        /** @private */
        arcane var _faceMaterialVO:FaceMaterialVO;
        /** @private */
        arcane var _mapping:Matrix;
        /** @private */
		arcane var _s:Shape = new Shape();
        /** @private */
		arcane var _graphics:Graphics;
        /** @private */
		arcane var _bitmapRect:Rectangle;
        /** @private */
		arcane var _sourceVO:FaceMaterialVO;
        /** @private */
        arcane var _session:AbstractRenderSession;
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
		arcane function renderSource(source:Object3D, containerRect:Rectangle, mapping:Matrix):void
		{
			//check to see if sourceDictionary exists
			if (!(_sourceVO = _faceDictionary[source]))
				_sourceVO = _faceDictionary[source] = new FaceMaterialVO();
			
			_sourceVO.resize(containerRect.width, containerRect.height);
			
			//check to see if rendering can be skipped
			if (_sourceVO.invalidated || _sourceVO.updated) {
				
				//calulate scale matrix
				mapping.scale(containerRect.width/width, containerRect.height/height);
				
				//reset booleans
				_sourceVO.invalidated = false;
				_sourceVO.cleared = false;
				_sourceVO.updated = false;
				
				//draw the bitmap
				if (mapping.a == 1 && mapping.d == 1 && mapping.b == 0 && mapping.c == 0 && mapping.tx == 0 && mapping.ty == 0) {
					//speedier version for non-transformed bitmap
					_sourceVO.bitmap.copyPixels(_bitmap, containerRect, _zeroPoint);
				}else {
					_graphics = _s.graphics;
					_graphics.clear();
					_graphics.beginBitmapFill(_bitmap, mapping, repeat, smooth);
					_graphics.drawRect(0, 0, containerRect.width, containerRect.height);
		            _graphics.endFill();
					_sourceVO.bitmap.draw(_s, null, _colorTransform, _blendMode, _sourceVO.bitmap.rect);
				}
			}
		}
		
		private var index:int;
		private var _view:View3D;		private var _screenVertices:Array;		private var _screenCommands:Array;		private var _screenIndices:Array;
		private var _near:Number;
		private var _smooth:Boolean;
		private var _debug:Boolean;
		private var _repeat:Boolean;
        private var _precision:Number;
    	private var _shape:Shape;
    	private var _materialupdated:MaterialEvent;
        private var map:Matrix = new Matrix();
        private var x:Number;
		private var y:Number;		private var _showNormals:Boolean;		private var _nn:Number3D = new Number3D();		private var _sv0x:Number;		private var _sv0y:Number;		private var _sv1x:Number;		private var _sv1y:Number;        
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
    	protected function updateColorTransform():void
        {
        	_colorTransformDirty = false;
			
			_bitmapDirty = true;
			_materialDirty = true;
        	
            if (_alpha == 1 && _color == 0xFFFFFF) {
                _renderBitmap = _bitmap;
                if (!_colorTransform || (!_colorTransform.redOffset && !_colorTransform.greenOffset && !_colorTransform.blueOffset)) {
                	_colorTransform = null;
                	return;            	}
            } else if (!_colorTransform)
            	_colorTransform = new ColorTransform();
			
			_colorTransform.redMultiplier = _red;
			_colorTransform.greenMultiplier = _green;
			_colorTransform.blueMultiplier = _blue;
			_colorTransform.alphaMultiplier = _alpha;

            if (_alpha == 0) {
                _renderBitmap = null;
                return;
            }
        }
    	
    	/**
    	 * Updates the texture bitmapData with the colortransform determined from the <code>color</code> and <code>alpha</code> properties.
    	 * 
    	 * @see color
    	 * @see alpha
    	 * @see setColorTransform()
    	 */
        protected function updateRenderBitmap():void
        {
        	_bitmapDirty = false;
        	
        	if (_colorTransform) {
	        	if (!_bitmap.transparent && _alpha != 1) {
	                _renderBitmap = new BitmapData(_bitmap.width, _bitmap.height, true);
	                _renderBitmap.draw(_bitmap);
	            } else {
	        		_renderBitmap = _bitmap.clone();
	           }
	            _renderBitmap.colorTransform(_renderBitmap.rect, _colorTransform);
	        } else {
	        	_renderBitmap = _bitmap;
	        }
	        
	        invalidateFaces();
        }
        
        /**
        * Calculates the mapping matrix required to draw the triangle texture to screen.
        * 
        * @param	tri		The data object holding all information about the triangle to be drawn.
        * @return			The required matrix object.
        */
		protected function getMapping(tri:DrawTriangle):Matrix
		{
			if (tri.generated) {
				_texturemapping = tri.transformUV(this).clone();
				_texturemapping.invert();
				
				return _texturemapping;
			}
			
			_faceMaterialVO = getFaceMaterialVO(tri.faceVO);			
			if (!_faceMaterialVO.invalidated)
				return _faceMaterialVO.texturemapping;
						_faceMaterialVO.invalidated = false;			
			_texturemapping = tri.transformUV(this).clone();
			_texturemapping.invert();
			
			return _faceMaterialVO.texturemapping = _texturemapping;
		}
		
		protected function getUVData(tri:DrawTriangle):Vector.<Number>
		{			_faceMaterialVO = getFaceMaterialVO(tri.faceVO, tri.source, tri.view);
						if (_view.camera.lens is ZoomFocusLens)        		_focus = tri.view.camera.focus;        	else        		_focus = 0;						if (tri.generated) {				_uvtData[2] = 1/(_focus + tri.v0z);				_uvtData[5] = 1/(_focus + tri.v1z);				_uvtData[8] = 1/(_focus + tri.v2z);				_uvtData[0] = tri.uv0.u;	    		_uvtData[1] = 1 - tri.uv0.v;	    		_uvtData[3] = tri.uv1.u;	    		_uvtData[4] = 1 - tri.uv1.v;	    		_uvtData[6] = tri.uv2.u;	    		_uvtData[7] = 1 - tri.uv2.v;	    			    		return _uvtData;			}						_faceMaterialVO.uvtData[2] = 1/(_focus + tri.v0z);			_faceMaterialVO.uvtData[5] = 1/(_focus + tri.v1z);			_faceMaterialVO.uvtData[8] = 1/(_focus + tri.v2z);						if (!_faceMaterialVO.invalidated)				return _faceMaterialVO.uvtData;						_faceMaterialVO.invalidated = false;        	        	_faceMaterialVO.uvtData[0] = tri.uv0.u;    		_faceMaterialVO.uvtData[1] = 1 - tri.uv0.v;    		_faceMaterialVO.uvtData[3] = tri.uv1.u;    		_faceMaterialVO.uvtData[4] = 1 - tri.uv1.v;    		_faceMaterialVO.uvtData[6] = tri.uv2.u;    		_faceMaterialVO.uvtData[7] = 1 - tri.uv2.v;        	
			return _faceMaterialVO.uvtData;
		}
		
    	/**
    	 * Determines if texture bitmap is smoothed (bilinearly filtered) when drawn to screen.
    	 */
        public function get smooth():Boolean
        {
        	return _smooth;
        }
        
        public function set smooth(val:Boolean):void
        {
        	if (_smooth == val)
        		return;
        	
        	_smooth = val;
        	
        	_materialDirty = true;
        }
        
        
        /**
        * Toggles debug mode: textured triangles are drawn with white outlines, precision correction triangles are drawn with blue outlines.
        */
        public function get debug():Boolean
        {
        	return _debug;
        }
        
        public function set debug(val:Boolean):void
        {
        	if (_debug == val)
        		return;
        	
        	_debug = val;
        	
        	_materialDirty = true;
        }
        
        /**
        * Determines if texture bitmap will tile in uv-space
        */
        public function get repeat():Boolean
        {
        	return _repeat;
        }
        
        public function set repeat(val:Boolean):void
        {
        	if (_repeat == val)
        		return;
        	
        	_repeat = val;
        	
        	_materialDirty = true;
        }
        
        
        /**
        * Corrects distortion caused by the affine transformation (non-perspective) of textures.
        * The number refers to the pixel correction value - ie. a value of 2 means a distorion correction to within 2 pixels of the correct perspective distortion.
        * 0 performs no precision.
        */
        public function get precision():Number
        {
        	return _precision;
        }
        
        public function set precision(val:Number):void
        {
        	_precision = val*val*1.4;
        	
        	_materialDirty = true;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get width():Number
        {
            return _bitmap.width;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get height():Number
        {
            return _bitmap.height;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get bitmap():BitmapData
        {
        	return _bitmap;
        }
        
        public function set bitmap(val:BitmapData):void
        {
        	_bitmap = val;
        	
        	_bitmapDirty = true;
        }
        
		/**
		 * @inheritDoc
		 */
        public function getPixel32(u:Number, v:Number):uint
        {
        	if (repeat) {
        		x = u%1;
        		y = (1 - v%1);
        	} else {
        		x = u;
        		y = (1 - v);
        	}
        	return _bitmap.getPixel32(x*_bitmap.width, y*_bitmap.height);
        }
        
		/**
		 * Defines a colored tint for the texture bitmap.
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
        * Defines an alpha value for the texture bitmap.
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
        * Defines a colortransform for the texture bitmap.        */        public function get colorTransform():ColorTransform        {            return _colorTransform;        }                public function set colorTransform(value:ColorTransform):void        {            _colorTransform = value;						if (_colorTransform) {				_red = _colorTransform.redMultiplier;				_green = _colorTransform.greenMultiplier;				_blue = _colorTransform.blueMultiplier;				_alpha = _colorTransform.alphaMultiplier;								_color = (_red*255 << 16) + (_green*255 << 8) + _blue*255;			}			            _colorTransformDirty = true;        }
        /**
        * Defines a blendMode value for the texture bitmap.
        * Applies to materials rendered as children of <code>BitmapMaterialContainer</code> or  <code>CompositeMaterial</code>.
        * 
        * @see away3d.materials.BitmapMaterialContainer
        * @see away3d.materials.CompositeMaterial
        */
        public function get blendMode():String
        {
        	return _blendMode;
        }
    	
        public function set blendMode(val:String):void
        {
        	if (_blendMode == val)
        		return;
        	
        	_blendMode = val;
        	_blendModeDirty = true;
        }
				/**        * Displays the normals per face in pink lines.        */        public function get showNormals():Boolean        {        	return _showNormals;        }                public function set showNormals(val:Boolean):void        {        	if (_showNormals == val)        		return;        	        	_showNormals = val;        	        	_materialDirty = true;        }        		/**		 * @inheritDoc		 */        public function get visible():Boolean        {            return _alpha > 0;        }                		/**		 * @inheritDoc		 */        public function get id():int        {            return _id;        }        
		/**
		 * Creates a new <code>BitmapMaterial</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function BitmapMaterial(bitmap:BitmapData, init:Object = null)
        {
        	_bitmap = bitmap;
            
            ini = Init.parse(init);
			
            smooth = ini.getBoolean("smooth", false);
            debug = ini.getBoolean("debug", false);
            repeat = ini.getBoolean("repeat", false);
            precision = ini.getNumber("precision", 0);
            _blendMode = ini.getString("blendMode", BlendMode.NORMAL);
            alpha = ini.getNumber("alpha", _alpha, {min:0, max:1});
            color = ini.getColor("color", _color);
            colorTransform = ini.getObject("colorTransform", ColorTransform) as ColorTransform;
            showNormals = ini.getBoolean("showNormals", false);            _colorTransformDirty = true;
        }
        
		/**
		 * @inheritDoc
		 */
        public function updateMaterial(source:Object3D, view:View3D):void
        {
        	_graphics = null;
        		
        	if (_colorTransformDirty)
        		updateColorTransform();
        		
        	if (_bitmapDirty)
        		updateRenderBitmap();
        	
        	if (_materialDirty || _blendModeDirty)
        		clearFaces(source, view);        	
        	_blendModeDirty = false;
        }
        
        public function getFaceMaterialVO(faceVO:FaceVO, source:Object3D = null, view:View3D = null):FaceMaterialVO
        {        	//check to see if faceMaterialVO exists
        	if ((_faceMaterialVO = _faceDictionary[faceVO]))
        		return _faceMaterialVO;
        	
        	return _faceDictionary[faceVO] = new FaceMaterialVO();
        }
                		/**		 * @inheritDoc		 */        public function clearFaces(source:Object3D = null, view:View3D = null):void        {
        	notifyMaterialUpdate();        	
        	for each (_faceMaterialVO in _faceDictionary)        		if (!_faceMaterialVO.cleared)        			_faceMaterialVO.clear();        }
        
		/**		 * @inheritDoc		 */        public function invalidateFaces(source:Object3D = null, view:View3D = null):void        {
        	_materialDirty = true;        	        	for each (_faceMaterialVO in _faceDictionary)        		_faceMaterialVO.invalidated = true;        }
        
		/**
		 * @inheritDoc
		 */
        public function renderLayer(tri:DrawTriangle, layer:Sprite, level:int):int
        {
        	if (blendMode == BlendMode.NORMAL) {
        		_graphics = layer.graphics;
        	} else {
        		_session = tri.source.session;
        		        		_shape = _session.getShape(this, level++, layer);	    		
	    		_shape.blendMode = _blendMode;
	    		
	    		_graphics = _shape.graphics;
        	}
    		
    		
    		renderTriangle(tri);
    		    		return level;
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderTriangle(tri:DrawTriangle):void
        {
        	//_mapping = getMapping(tri);
			_session = tri.source.session;
			_screenCommands = tri.screenCommands;			_screenVertices = tri.screenVertices;			_screenIndices = tri.screenIndices;
        	_view = tri.view;
        	_near = _view.screenClipping.minZ;        	
        	//if (!_graphics && _session.newLayer)        	//	_graphics = _session.newLayer.graphics;
        	
			_session.renderTriangleBitmapF10(_renderBitmap, getUVData(tri), _screenVertices, _screenIndices, tri.startIndex, tri.endIndex, smooth, repeat, _graphics);
			//_session.renderTriangleBitmap(_renderBitmap, _mapping, tri.v0, tri.v1, tri.v2, smooth, repeat, _graphics);
            if (debug)
                _session.renderTriangleLine(0, 0x0000FF, 1, _screenVertices, _screenCommands, _screenIndices, tri.startIndex, tri.endIndex);
							if(showNormals){								_nn.rotate(tri.faceVO.face.normal, tri.view.cameraVarsStore.viewTransformDictionary[tri.source]);				 				_sv0x = (tri.v0x + tri.v1x + tri.v2x) / 3;				_sv0y = (tri.v0y + tri.v1y + tri.v2y) / 3;				 				_sv1x = (_sv0x - (30*_nn.x));				_sv1y = (_sv0y - (30*_nn.y));				 				_session.renderLine(_sv0x, _sv0y, _sv1x, _sv1y, 0, 0xFF00FF, 1);			}        }        		/**		 * @inheritDoc		 */        public function renderBillboard(bill:DrawBillboard):void        {            bill.source.session.renderBillboardBitmap(_renderBitmap, bill, smooth);        }
        
		/**
		 * @inheritDoc
		 */
		public function renderBitmapLayer(tri:DrawTriangle, containerRect:Rectangle, parentFaceMaterialVO:FaceMaterialVO):FaceMaterialVO
		{
			//draw the bitmap once
			renderSource(tri.source, containerRect, new Matrix());
			
			//get the correct faceMaterialVO			_faceMaterialVO = getFaceMaterialVO(tri.faceVO);
			
			//pass on resize value
			if (parentFaceMaterialVO.resized) {
				parentFaceMaterialVO.resized = false;
				_faceMaterialVO.resized = true;
			}
			
			//pass on invtexturemapping value
			_faceMaterialVO.invtexturemapping = parentFaceMaterialVO.invtexturemapping;
			
			//check to see if face update can be skipped
			if (parentFaceMaterialVO.updated || _faceMaterialVO.invalidated || _faceMaterialVO.updated) {
				parentFaceMaterialVO.updated = false;
				
				//reset booleans
				_faceMaterialVO.invalidated = false;
				_faceMaterialVO.cleared = false;
				_faceMaterialVO.updated = true;
				
				//store a clone
				_faceMaterialVO.bitmap = parentFaceMaterialVO.bitmap.clone();
				
				//draw into faceBitmap
				_faceMaterialVO.bitmap.copyPixels(_sourceVO.bitmap, tri.faceVO.bitmapRect, _zeroPoint, null, null, true);
			}
			
			return _faceMaterialVO;
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