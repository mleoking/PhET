package away3d.materials
{
	import away3d.arcane;
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
    public class BitmapMaskMaterial extends EventDispatcher implements ITriangleMaterial, IUVMaterial, ILayerMaterial, IBillboardMaterial
    {
    	/** @private */
        arcane var _id:int;
    	/** @private */
    	arcane var _texturemapping:Matrix;
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
		
		private var _view:View3D;
		private var _screenVertices:Array;
		private var _screenCommands:Array;
		private var _screenIndices:Array;
		private var _near:Number;
		private var _smooth:Boolean;
		private var _debug:Boolean;
		private var _repeat:Boolean;
        private var _precision:Number;
    	private var _shape:Shape;
    	private var _materialupdated:MaterialEvent;
        private var focus:Number;
        private var map:Matrix = new Matrix();
        private var x:Number;
		private var y:Number;
        private var faz:Number;
        private var fbz:Number;
        private var fcz:Number;
        private var mabz:Number;
        private var mbcz:Number;
        private var mcaz:Number;
        private var mabx:Number;
        private var maby:Number;
        private var mbcx:Number;
        private var mbcy:Number;
        private var mcax:Number;
        private var mcay:Number;
        private var dabx:Number;
        private var daby:Number;
        private var dbcx:Number;
        private var dbcy:Number;
        private var dcax:Number;
        private var dcay:Number;    
        private var dsab:Number;
        private var dsbc:Number;
        private var dsca:Number;
        private var dmax:Number;
        private var ai:Number;
        private var ax:Number;
        private var ay:Number;
        private var az:Number;
        private var bi:Number;
        private var bx:Number;
        private var by:Number;
        private var bz:Number;
        private var ci:Number;
        private var cx:Number;
        private var cy:Number;
        private var cz:Number;
		private var _showNormals:Boolean;
		private var _nn:Number3D = new Number3D();
		private var _sv0x:Number;
		private var _sv0y:Number;
		private var _sv1x:Number;
		private var _sv1y:Number;
        
        private function renderRec(startIndex:Number, endIndex:Number, index:Number):void
        {
            ai = _screenIndices[startIndex]*3;
            ax = _screenVertices[ai];
            ay = _screenVertices[ai+1];
            az = _screenVertices[ai+2];
            bi = _screenIndices[startIndex+1]*3;
            bx = _screenVertices[bi];
            by = _screenVertices[bi+1];
            bz = _screenVertices[bi+2];
            ci = _screenIndices[startIndex+2]*3;
            cx = _screenVertices[ci];
            cy = _screenVertices[ci+1];
            cz = _screenVertices[ci+2];
            
            if (!(_view.screenClipping is FrustumClipping) && !_view.screenClipping.rect(Math.min(ax, Math.min(bx, cx)), Math.min(ay, Math.min(by, cy)), Math.max(ax, Math.max(bx, cx)), Math.max(ay, Math.max(by, cy))))
                return;

            if ((_view.screenClipping is RectangleClipping) && (az < _near || bz < _near || cz < _near))
                return;
            
            if (index >= 100 || (focus == Infinity) || (Math.max(Math.max(ax, bx), cx) - Math.min(Math.min(ax, bx), cx) < 10) || (Math.max(Math.max(ay, by), cy) - Math.min(Math.min(ay, by), cy) < 10))
            {
                _session.renderTriangleBitmap(_renderBitmap, map, _screenVertices, _screenIndices, startIndex, endIndex, smooth, repeat, _graphics);
                if (debug)
                    _session.renderTriangleLine(1, 0x00FF00, 1, _screenVertices, _screenCommands, _screenIndices, startIndex, endIndex);
                return;
            }
			
            faz = focus + az;
            fbz = focus + bz;
            fcz = focus + cz;
			
            mabz = 2 / (faz + fbz);
            mbcz = 2 / (fbz + fcz);
            mcaz = 2 / (fcz + faz);
			
            dabx = ax + bx - (mabx = (ax*faz + bx*fbz)*mabz);
            daby = ay + by - (maby = (ay*faz + by*fbz)*mabz);
            dbcx = bx + cx - (mbcx = (bx*fbz + cx*fcz)*mbcz);
            dbcy = by + cy - (mbcy = (by*fbz + cy*fcz)*mbcz);
            dcax = cx + ax - (mcax = (cx*fcz + ax*faz)*mcaz);
            dcay = cy + ay - (mcay = (cy*fcz + ay*faz)*mcaz);
            
            dsab = (dabx*dabx + daby*daby);
            dsbc = (dbcx*dbcx + dbcy*dbcy);
            dsca = (dcax*dcax + dcay*dcay);
			
            if ((dsab <= precision) && (dsca <= precision) && (dsbc <= precision))
            {
                _session.renderTriangleBitmap(_renderBitmap, map, _screenVertices, _screenIndices, startIndex, endIndex, smooth, repeat, _graphics);
                if (debug)
                    _session.renderTriangleLine(1, 0x00FF00, 1, _screenVertices, _screenCommands, _screenIndices, startIndex, endIndex);
                return;
            }
			
            var map_a:Number = map.a;
            var map_b:Number = map.b;
            var map_c:Number = map.c;
            var map_d:Number = map.d;
            var map_tx:Number = map.tx;
            var map_ty:Number = map.ty;
            
            var sv1:int;
            var sv2:int;
            var sv3:int;
            
            index++;
            
            sv3 = _screenVertices.length/3;
            _screenVertices[_screenVertices.length] = mbcx/2;
            _screenVertices[_screenVertices.length] = mbcy/2;
            _screenVertices[_screenVertices.length] = (bz+cz)/2;
            
            if ((dsab > precision) && (dsca > precision) && (dsbc > precision))
            {
            	index += 2;
            	
            	sv1 = _screenVertices.length/3;
            	_screenVertices[_screenVertices.length] = mabx/2;
                _screenVertices[_screenVertices.length] = maby/2;
                _screenVertices[_screenVertices.length] = (az+bz)/2;
                
                sv2 = _screenVertices.length/3;
                _screenVertices[_screenVertices.length] = mcax/2;
                _screenVertices[_screenVertices.length] = mcay/2;
                _screenVertices[_screenVertices.length] = (cz+az)/2;
                
	            _screenIndices[startIndex = _screenIndices.length] = ai;
                _screenIndices[_screenIndices.length] = sv1;
                _screenIndices[_screenIndices.length] = sv2;
                
            	endIndex = _screenIndices.length;
                
                map.a = map_a*=2;
                map.b = map_b*=2;
                map.c = map_c*=2;
                map.d = map_d*=2;
                map.tx = map_tx*=2;
                map.ty = map_ty*=2;
                renderRec(startIndex, endIndex, index);
            	
            	_screenIndices[startIndex = _screenIndices.length] = sv1;
                _screenIndices[_screenIndices.length] = bi;
                _screenIndices[_screenIndices.length] = sv3;
                
            	endIndex = _screenIndices.length;
            	
                map.a = map_a;
                map.b = map_b;
                map.c = map_c;
                map.d = map_d;
                map.tx = map_tx-1;
                map.ty = map_ty;
                renderRec(startIndex, endIndex, index);
            	
            	_screenIndices[startIndex = _screenIndices.length] = sv2;
                _screenIndices[_screenIndices.length] = sv3;
                _screenIndices[_screenIndices.length] = ci;
                
            	endIndex = _screenIndices.length;
            	
                map.a = map_a;
                map.b = map_b;
                map.c = map_c;
                map.d = map_d;
                map.tx = map_tx;
                map.ty = map_ty-1;
                renderRec(startIndex, endIndex, index);
            	
            	_screenIndices[startIndex = _screenIndices.length] = sv3;
                _screenIndices[_screenIndices.length] = sv2;
                _screenIndices[_screenIndices.length] = sv1;
                
            	endIndex = _screenIndices.length;
            	
                map.a = -map_a;
                map.b = -map_b;
                map.c = -map_c;
                map.d = -map_d;
                map.tx = 1-map_tx;
                map.ty = 1-map_ty;
                renderRec(startIndex, endIndex, index);
                
                return;
            }
			
            dmax = Math.max(dsab, Math.max(dsca, dsbc));
            if (dsab == dmax)
            {
            	index++;
            	
            	sv1 = _screenVertices.length/3;
            	_screenVertices[_screenVertices.length] = mabx/2;
                _screenVertices[_screenVertices.length] = maby/2;
                _screenVertices[_screenVertices.length] = (az+bz)/2;
                
	            _screenIndices[startIndex = _screenIndices.length] = ai;
                _screenIndices[_screenIndices.length] = sv1;
                _screenIndices[_screenIndices.length] = ci;
                
            	endIndex = _screenIndices.length;
            	
                map.a = map_a*=2;
                map.c = map_c*=2;
                map.tx = map_tx*=2;
                renderRec(startIndex, endIndex, index);
                
	            _screenIndices[startIndex = _screenIndices.length] = sv1;
                _screenIndices[_screenIndices.length] = bi;
                _screenIndices[_screenIndices.length] = ci;
                
            	endIndex = _screenIndices.length;
            	
                map.a = map_a + map_b;
                map.b = map_b;
                map.c = map_c + map_d;
                map.d = map_d;
                map.tx = map_tx + map_ty - 1;
                map.ty = map_ty;
                renderRec(startIndex, endIndex, index);
                
                return;
            }
			
            if (dsca == dmax)
            {
            	index++;
            	
                sv2 = _screenVertices.length/3;
                _screenVertices[_screenVertices.length] = mcax/2;
                _screenVertices[_screenVertices.length] = mcay/2;
                _screenVertices[_screenVertices.length] = (cz+az)/2;
                
	            _screenIndices[startIndex = _screenIndices.length] = ai;
                _screenIndices[_screenIndices.length] = bi;
                _screenIndices[_screenIndices.length] = sv2;
                
            	endIndex = _screenIndices.length;
            	
                map.b = map_b*=2;
                map.d = map_d*=2;
                map.ty = map_ty*=2;
                renderRec(startIndex, endIndex, index);
                
	            _screenIndices[startIndex = _screenIndices.length] = sv2;
                _screenIndices[_screenIndices.length] = bi;
                _screenIndices[_screenIndices.length] = ci;
                
            	endIndex = _screenIndices.length;
            	
                map.a = map_a;
                map.b = map_b + map_a;
                map.c = map_c;
                map.d = map_d + map_c;
                map.tx = map_tx;
                map.ty = map_ty + map_tx - 1;
                renderRec(startIndex, endIndex, index);
                
                return;
            }
            
            _screenIndices[startIndex = _screenIndices.length] = ai;
            _screenIndices[_screenIndices.length] = bi;
            _screenIndices[_screenIndices.length] = sv3;
            
        	endIndex = _screenIndices.length;
        	
            map.a = map_a - map_b;
            map.b = map_b*2;
            map.c = map_c - map_d;
            map.d = map_d*2;
            map.tx = map_tx - map_ty;
            map.ty = map_ty*2;
            renderRec(startIndex, endIndex, index);
            
            _screenIndices[startIndex = _screenIndices.length] = ai;
            _screenIndices[_screenIndices.length] = sv3;
            _screenIndices[_screenIndices.length] = ci;
            
        	endIndex = _screenIndices.length;
        	
            map.a = map_a*2;
            map.b = map_b - map_a;
            map.c = map_c*2;
            map.d = map_d - map_c;
            map.tx = map_tx*2;
            map.ty = map_ty - map_tx;
            renderRec(startIndex, endIndex, index);
        }
        
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
                _colorTransform = null;
                return;
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
	        	_renderBitmap = _bitmap.clone();
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
        * Corrects distortion caused by the affine transformation (non-perpective) of textures.
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
        
        public function set offsetX(value:Number):void
        {
        	_offsetX = value;
        }
        
        public function set offsetY(value:Number):void
        {
        	_offsetY = value;
        }
        
        public function set scaling(value:Number):void
        {
        	_scaling = value;
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
        * Defines a blendMode value for the texture bitmap.
        * Applies to materials rendered as children of <code>BitmapMaskMaterialContainer</code> or  <code>CompositeMaterial</code>.
        * 
        * @see away3d.materials.BitmapMaskMaterialContainer
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
		
		 /**
        * Displays the normals per face in pink lines.
        */
        public function get showNormals():Boolean
        {
        	return _showNormals;
        }
        
        public function set showNormals(val:Boolean):void
        {
        	if (_showNormals == val)
        		return;
        	
        	_showNormals = val;
        	
        	_materialDirty = true;
        }
                
		/**
		 * @inheritDoc
		 */
        public function get visible():Boolean
        {
            return _alpha > 0;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get id():int
        {
            return _id;
        }
        
		/**
		 * Creates a new <code>BitmapMaskMaterial</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		private var _offsetX:Number;
		private var _offsetY:Number;
		private var _scaling:Number;
        public function BitmapMaskMaterial(bitmap:BitmapData, init:Object = null)
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
            showNormals = ini.getBoolean("showNormals", false);
            _offsetX = ini.getNumber("offsetX", 0);
            _offsetY = ini.getNumber("offsetY", 0);
            _colorTransformDirty = true;
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
        {
        	//check to see if faceMaterialVO exists
        	if ((_faceMaterialVO = _faceDictionary[faceVO]))
        		return _faceMaterialVO;
        	
        	return _faceDictionary[faceVO] = new FaceMaterialVO();
        }
        
        
		/**
		 * @inheritDoc
		 */
        public function clearFaces(source:Object3D = null, view:View3D = null):void
        {
        	notifyMaterialUpdate();
        	
        	for each (var _faceMaterialVO:FaceMaterialVO in _faceDictionary)
        		if (!_faceMaterialVO.cleared)
        			_faceMaterialVO.clear();
        }
        
		/**
		 * @inheritDoc
		 */
        public function invalidateFaces(source:Object3D = null, view:View3D = null):void
        {
        	_materialDirty = true;
        	
        	for each (var _faceMaterialVO:FaceMaterialVO in _faceDictionary)
        		_faceMaterialVO.invalidated = true;
        }
        
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
        	_mapping = getMapping(tri);
			_session = tri.source.session;
			_screenCommands = tri.screenCommands;
        	_view = tri.view;
        	
			if (precision) {
            	focus = tri.view.camera.focus;
            	
            	map.a = _mapping.a;
	            map.b = _mapping.b;
	            map.c = _mapping.c;
	            map.d = _mapping.d;
	            map.tx = _mapping.tx;
	            map.ty = _mapping.ty;
	            renderRec(tri.startIndex, tri.endIndex, 0);
			} else {
				_session.renderTriangleBitmapMask(_renderBitmap, _offsetX, _offsetY, _scaling, _screenVertices, _screenIndices, tri.startIndex, tri.endIndex, smooth, repeat, _graphics);
			}
			
            if (debug)
                _session.renderTriangleLine(0, 0x0000FF, 1, _screenVertices, tri.screenCommands, _screenIndices, tri.startIndex, tri.endIndex);
				
			if(showNormals){
				
				_nn.rotate(tri.faceVO.face.normal, tri.view.cameraVarsStore.viewTransformDictionary[tri.source]);
				 
				_sv0x = (tri.v0x + tri.v1x + tri.v2x) / 3;
				_sv0y = (tri.v0y + tri.v1y + tri.v2y) / 3;
				 
				_sv1x = (_sv0x - (30*_nn.x));
				_sv1y = (_sv0y - (30*_nn.y));
				 
				_session.renderLine(_sv0x, _sv0y, _sv1x, _sv1y, 0, 0xFF00FF, 1);
			}
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderBillboard(bill:DrawBillboard):void
        {
            bill.source.session.renderBillboardBitmap(_renderBitmap, bill, smooth);
        }
        
		/**
		 * @inheritDoc
		 */
		public function renderBitmapLayer(tri:DrawTriangle, containerRect:Rectangle, parentFaceMaterialVO:FaceMaterialVO):FaceMaterialVO
		{
			//draw the bitmap once
			renderSource(tri.source, containerRect, new Matrix());
			
			//get the correct faceMaterialVO
			_faceMaterialVO = getFaceMaterialVO(tri.faceVO);
			
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